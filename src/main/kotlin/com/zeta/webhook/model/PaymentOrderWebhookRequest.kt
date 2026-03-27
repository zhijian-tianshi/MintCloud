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

    @ApiModelProperty(value = "来源：notification/sms", required = true, example = "sms")
    @field:NotBlank(message = "source不能为空")
    val source: String?,

    @ApiModelProperty(value = "本地订单自增ID", required = true, example = "9")
    @field:NotNull(message = "id不能为空")
    val id: Long?,

    @ApiModelProperty(value = "渠道：wechat/alipay/sms/unknown", required = true, example = "sms")
    @field:NotBlank(message = "channel不能为空")
    val channel: String?,

    @ApiModelProperty(value = "展示方法：微信/支付宝/短信", required = true, example = "短信")
    @field:NotBlank(message = "method不能为空")
    val method: String?,

    @ApiModelProperty(value = "来源包名/标识：com.tencent.mm / com.eg.android.AlipayGphone / sms", required = true, example = "sms")
    @field:NotBlank(message = "packageName不能为空")
    val packageName: String?,

    @ApiModelProperty(value = "方向：income/expense/refund/unknown", required = true, example = "expense")
    @field:NotBlank(message = "direction不能为空")
    val direction: String?,

    @ApiModelProperty(value = "是否有争议", required = true, example = "false")
    @field:NotNull(message = "disputed不能为空")
    val disputed: Boolean?,

    @ApiModelProperty(value = "争议原因：none/no_strong_hint/direction_unknown/income_expense_conflict", required = true, example = "none")
    @field:NotBlank(message = "disputedReason不能为空")
    val disputedReason: String?,

    @ApiModelProperty(value = "金额字符串，例如：0.96", required = true, example = "0.96")
    @field:NotBlank(message = "amount不能为空")
    val amount: String?,

    @ApiModelProperty(value = "结果：当前固定完成", required = true, example = "完成")
    @field:NotBlank(message = "result不能为空")
    val result: String?,

    @ApiModelProperty(value = "事件时间戳（毫秒）", required = true, example = "1719561003000")
    @field:NotNull(message = "timeMillis不能为空")
    val timeMillis: Long?,

    @ApiModelProperty(value = "统一详情文本（通知title/text或短信全文）", required = true)
    @field:NotBlank(message = "detail不能为空")
    val detail: String?,

    @ApiModelProperty(value = "通知标题原文", required = false)
    val rawTitle: String? = null,

    @ApiModelProperty(value = "通知正文原文", required = false)
    val rawText: String? = null,
)

