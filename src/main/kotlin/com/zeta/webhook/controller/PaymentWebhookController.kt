package com.zeta.webhook.controller

import com.zeta.webhook.model.PaymentOrderWebhookRequest
import com.zeta.webhook.model.entity.PaymentWebhookOrder
import com.zeta.webhook.properties.PaymentWebhookProperties
import com.zeta.webhook.service.IPaymentWebhookOrderService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.util.Locale

@Api(tags = ["Webhook-收款消息"])
@RestController
@RequestMapping("/api/webhook/payment")
@Validated
class PaymentWebhookController(
    private val props: PaymentWebhookProperties,
    private val paymentWebhookOrderService: IPaymentWebhookOrderService,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    data class OkResponse(val ok: Boolean = true)

    @ApiOperation("收款消息 Webhook（回调推送）")
    @PostMapping(
        "/order",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun paymentOrder(
        @RequestHeader(name = "X-Act-Code", required = false) headerActCode: String?,
        @RequestHeader(name = "X-Device-Id", required = false) headerDeviceId: String?,
        @RequestBody @Validated body: PaymentOrderWebhookRequest,
    ): ResponseEntity<Any> {
        if (!props.enabled) {
            return ResponseEntity.status(503).body(mapOf("ok" to false, "message" to "webhook disabled"))
        }

        // Header 只是建议：优先使用 Body；Body 为空则回退到 Header（兼容）
        val actCode = body.actCode?.trim().takeUnless { it.isNullOrBlank() } ?: headerActCode?.trim()
        val deviceId = body.deviceId?.trim().takeUnless { it.isNullOrBlank() } ?: headerDeviceId?.trim()

        if (actCode.isNullOrBlank()) {
            return ResponseEntity.status(401).body(mapOf("ok" to false, "message" to "actCode required"))
        }

        val allowList = props.actCodes.map { it.trim() }.filter { it.isNotBlank() }.toSet()
        if (allowList.isEmpty() || !allowList.contains(actCode)) {
            return ResponseEntity.status(403).body(mapOf("ok" to false, "message" to "actCode forbidden"))
        }

        val event = body.event?.trim().orEmpty()
        if (event.lowercase(Locale.ROOT) != "payment.order") {
            return ResponseEntity.status(400).body(mapOf("ok" to false, "message" to "invalid event"))
        }

        // 目前按你的约定：2xx 视为成功；非 2xx 视为失败（客户端仅记录日志，不阻塞本地入库）
        logger.info(
            "payment webhook received actCode={} deviceId={} id={} method={} amount={} timeMillis={} pkg={}",
            actCode,
            deviceId,
            body.id,
            body.method,
            body.amount,
            body.timeMillis,
            body.packageName,
        )

        // 落库（如果客户端重复推送，同一 (actCode, deviceId, client_order_id) 会因唯一键重复）
        try {
            val entity = PaymentWebhookOrder().apply {
                this.event = body.event
                this.actCode = actCode
                this.deviceId = deviceId
                clientOrderId = body.id
                method = body.method
                packageName = body.packageName
                amount = body.amount?.let { BigDecimal(it) }
                result = body.result
                timeMillis = body.timeMillis
                rawTitle = body.rawTitle
                rawText = body.rawText
            }
            paymentWebhookOrderService.save(entity)
        } catch (e: Exception) {
            // 不阻塞回调：客户端约定非2xx才算失败；这里尽量保证2xx返回
            logger.warn("payment webhook persist failed (ignored): {}", e.message)
        }

        return ResponseEntity.ok(OkResponse())
    }
}

