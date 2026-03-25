package com.zeta.iot.mqtt.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "zeta.mqtt")
class MqttProperties(
    var enabled: Boolean = false,
    var brokerUrl: String = "ssl://localhost:8883",
    var username: String? = null,
    var password: String? = null,
    var topic: String = "mintcloud/demo/test",
    var qos: Int = 1,
    /** classpath 相对路径，例如：CA/emqxsl-ca.crt */
    var caCertClasspath: String = "CA/emqxsl-ca.crt",
    /** 连接超时（秒） */
    var connectionTimeoutSeconds: Int = 10,
    /** keepalive（秒） */
    var keepAliveSeconds: Int = 30,
    /** 自动重连 */
    var automaticReconnect: Boolean = true,
    /** clean session */
    var cleanSession: Boolean = true,
)

