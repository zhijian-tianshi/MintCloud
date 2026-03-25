package com.zeta.iot.mqtt

import com.zeta.iot.mqtt.properties.MqttProperties
import com.zeta.iot.model.TelemetryMessage
import com.zeta.iot.service.TelemetryAggregator
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.zetaframework.core.utils.JSONUtil
import java.io.BufferedInputStream
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateFactory
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory

@Component
class MqttClientManager(
    private val props: MqttProperties,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val aggregator = TelemetryAggregator()

    @Volatile
    private var client: MqttClient? = null

    fun getAggregator(): TelemetryAggregator = aggregator

    @PostConstruct
    fun start() {
        if (!props.enabled) {
            logger.info("MQTT disabled (zeta.mqtt.enabled=false)")
            return
        }

        val clientId = MqttClient.generateClientId()
        val c = MqttClient(props.brokerUrl, clientId, MemoryPersistence())
        c.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                logger.info("MQTT connectComplete reconnect={} uri={} clientId={}", reconnect, serverURI, clientId)
                try {
                    c.subscribe(props.topic, props.qos)
                    logger.info("MQTT subscribed topic={} qos={}", props.topic, props.qos)
                } catch (e: Exception) {
                    logger.error("MQTT subscribe failed", e)
                }
            }

            override fun connectionLost(cause: Throwable?) {
                logger.warn("MQTT connectionLost: {}", cause?.message, cause)
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                val payload = message?.payload?.toString(Charsets.UTF_8) ?: return
                val telemetry = JSONUtil.parseObject(payload, TelemetryMessage::class.java)
                if (telemetry == null) {
                    logger.warn("MQTT payload parse failed. topic={} payload={}", topic, payload)
                    return
                }
                aggregator.add(telemetry)
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                // no-op
            }
        })

        val options = MqttConnectOptions().apply {
            isAutomaticReconnect = props.automaticReconnect
            isCleanSession = props.cleanSession
            connectionTimeout = props.connectionTimeoutSeconds
            keepAliveInterval = props.keepAliveSeconds
            props.username?.takeIf { it.isNotBlank() }?.let { userName = it }
            props.password?.let { password = it.toCharArray() }
            socketFactory = singleTlsSocketFactoryFromClasspath(props.caCertClasspath)
        }

        logger.info("MQTT connecting. brokerUrl={} topic={} qos={}", props.brokerUrl, props.topic, props.qos)
        c.connect(options)
        this.client = c
    }

    @PreDestroy
    fun stop() {
        val c = client ?: return
        try {
            if (c.isConnected) c.disconnect()
        } catch (e: Exception) {
            logger.warn("MQTT disconnect failed", e)
        } finally {
            try {
                c.close()
            } catch (_: Exception) {
            }
        }
    }

    private fun singleTlsSocketFactoryFromClasspath(caCertClasspathPath: String): SSLSocketFactory {
        val inputStream = Thread.currentThread().contextClassLoader.getResourceAsStream(caCertClasspathPath)
            ?: error("找不到CA证书资源: $caCertClasspathPath（请确认在 src/main/resources 下）")

        BufferedInputStream(inputStream).use { bis ->
            val cf = CertificateFactory.getInstance("X.509")
            val caCert = cf.generateCertificate(bis)

            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply { load(null, null) }
            keyStore.setCertificateEntry("ca-certificate", caCert)

            val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            tmf.init(keyStore)

            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, tmf.trustManagers, SecureRandom())
            return sslContext.socketFactory
        }
    }
}

