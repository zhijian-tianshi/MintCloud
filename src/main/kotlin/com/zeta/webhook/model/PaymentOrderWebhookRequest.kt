package com.zeta.webhook.model

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@ApiModel(description = "收款消息 Webhook（回调推送）请求体")
data class PaymentOrderWebhookRequest(
    @ApiModelProperty(value = "事件名", required = true, example = "payment.order")
    @field:NotBlank(message = "event不能为空")
    val event: String?,

    @ApiModelProperty(value = "激活码", required = true, example = "ABC123")
    @field:NotBlank(message = "actCode不能为空")
    val actCode: String?,

    @ApiModelProperty(value = "设备编码（Android ID）", required = true, example = "18071adc0206edbbf05")
    @field:NotBlank(message = "deviceId不能为空")
    val deviceId: String?,

    @ApiModelProperty(value = "本地订单自增ID", required = true, example = "9")
    @field:NotNull(message = "id不能为空")
    val id: Long?,

    @ApiModelProperty(value = "收款方式：微信/支付宝", required = true, example = "支付宝")
    @field:NotBlank(message = "method不能为空")
    val method: String?,

    @ApiModelProperty(value = "包名：com.tencent.mm / com.eg.android.AlipayGphone", required = true, example = "com.eg.android.AlipayGphone")
    @field:NotBlank(message = "packageName不能为空")
    val packageName: String?,

    @ApiModelProperty(value = "金额字符串，例如：0.96", required = true, example = "0.96")
    @field:NotBlank(message = "amount不能为空")
    val amount: String?,

    @ApiModelProperty(value = "结果：当前固定完成", required = true, example = "完成")
    @field:NotBlank(message = "result不能为空")
    val result: String?,

    @ApiModelProperty(value = "事件时间戳（毫秒）", required = true, example = "1719561003000")
    @field:NotNull(message = "timeMillis不能为空")
    val timeMillis: Long?,

    @ApiModelProperty(value = "通知标题原文", required = false)
    val rawTitle: String? = null,

    @ApiModelProperty(value = "通知正文原文", required = false)
    val rawText: String? = null,
)

