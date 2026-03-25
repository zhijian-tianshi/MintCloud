package com.zeta.iot.mqtt

import com.zeta.iot.mqtt.properties.MqttProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(MqttProperties::class)
class MqttConfiguration

