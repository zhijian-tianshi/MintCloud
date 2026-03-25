package com.zeta.webhook.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "zeta.payment-webhook")
class PaymentWebhookProperties(
    /** 是否启用回调接收接口（仅控制校验/行为开关，不影响Controller注册） */
    var enabled: Boolean = true,
    /**
     * actCode 白名单
     * - 为空时：不放行任何请求（更安全）
     */
    var actCodes: List<String> = emptyList(),
)

