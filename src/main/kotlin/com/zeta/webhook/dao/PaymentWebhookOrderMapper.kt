package com.zeta.webhook.dao

import com.mybatisflex.core.BaseMapper
import com.zeta.webhook.model.entity.PaymentWebhookOrder
import org.springframework.stereotype.Repository

@Repository
interface PaymentWebhookOrderMapper : BaseMapper<PaymentWebhookOrder>

