package com.zeta.webhook

import com.zeta.webhook.properties.PaymentWebhookProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(PaymentWebhookProperties::class)
class PaymentWebhookConfiguration

