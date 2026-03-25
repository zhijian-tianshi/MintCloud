package com.zeta.webhook.service.impl

import com.mybatisflex.spring.service.impl.ServiceImpl
import com.zeta.webhook.dao.PaymentWebhookOrderMapper
import com.zeta.webhook.model.entity.PaymentWebhookOrder
import com.zeta.webhook.service.IPaymentWebhookOrderService
import org.springframework.stereotype.Service

@Service
class PaymentWebhookOrderServiceImpl :
    IPaymentWebhookOrderService,
    ServiceImpl<PaymentWebhookOrderMapper, PaymentWebhookOrder>()

