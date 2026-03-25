package com.zeta.mqtt

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.io.BufferedInputStream
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory

/**
 * 最小 MQTT TLS Demo（EMQX Cloud）
 *
 * - Broker: g1a159a6.ala.cn-hangzhou.emqxsl.cn:8883
 * - 用户名/密码: admin / admin
 * - CA证书: classpath: CA/emqxsl-ca.crt
 * - 测试主题: mintcloud/demo/test
 */
object MqttDemo {
    private const val HOST = "g1a159a6.ala.cn-hangzhou.emqxsl.cn"
    private const val PORT = 8883
    private const val USERNAME = "admin"
    private const val PASSWORD = "admin"

    private const val TOPIC = "mintcloud/demo/test"
    private const val QOS = 1

    @JvmStatic
    fun main(args: Array<String>) {
        val brokerUrl = "ssl://$HOST:$PORT"
        val clientId = MqttClient.generateClientId()
        val client = MqttClient(brokerUrl, clientId, MemoryPersistence())

        client.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                println("connectComplete reconnect=$reconnect serverURI=$serverURI clientId=$clientId")
            }

            override fun connectionLost(cause: Throwable?) {
                println("connectionLost: ${cause?.message}")
                cause?.printStackTrace()
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                val payload = message?.payload?.toString(Charsets.UTF_8)
                println("messageArrived topic=$topic qos=${message?.qos} payload=$payload")
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                val topics = token?.topics?.joinToString(",")
                println("deliveryComplete topics=$topics messageId=${token?.messageId}")
            }
        })

        val options = MqttConnectOptions().apply {
            isAutomaticReconnect = true
            isCleanSession = true
            userName = USERNAME
            password = PASSWORD.toCharArray()
            socketFactory = singleTlsSocketFactoryFromClasspath("CA/emqxsl-ca.crt")
            connectionTimeout = 10
            keepAliveInterval = 30
        }

        println("Connecting to $brokerUrl topic=$TOPIC qos=$QOS")
        client.connect(options)
        client.subscribe(TOPIC, QOS)
        println("Subscribed to $TOPIC")

        Runtime.getRuntime().addShutdownHook(Thread {
            try {
                if (client.isConnected) client.disconnect()
            } finally {
                client.close()
            }
        })

        var seq = 1L
        while (true) {
            val payload = "hello mqtt seq=$seq ts=${System.currentTimeMillis()} clientId=$clientId"
            val message = MqttMessage(payload.toByteArray(Charsets.UTF_8)).apply { qos = QOS }
            client.publish(TOPIC, message)
            Thread.sleep(1000)
            seq++
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

