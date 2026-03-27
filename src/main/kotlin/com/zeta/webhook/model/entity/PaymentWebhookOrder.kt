package com.zeta.webhook.model.entity

import com.mybatisflex.annotation.Column
import com.mybatisflex.annotation.Table
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.zetaframework.base.entity.Entity
import java.math.BigDecimal

@ApiModel(description = "收款消息Webhook入库表")
@Table(value = "payment_webhook_order")
class PaymentWebhookOrder : Entity<Long>() {

    @ApiModelProperty(value = "事件名：payment.order", required = true)
    @Column("event")
    var event: String? = null

    @ApiModelProperty(value = "激活码", required = true)
    @Column("act_code")
    var actCode: String? = null

    @ApiModelProperty(value = "设备编码（Android ID）", required = true)
    @Column("device_id")
    var deviceId: String? = null

    @ApiModelProperty(value = "客户端本地订单自增ID（Room id）", required = true)
    @Column("client_order_id")
    var clientOrderId: Long? = null

    @ApiModelProperty(value = "来源：notification/sms", required = true)
    @Column("source")
    var source: String? = null

    @ApiModelProperty(value = "渠道：wechat/alipay/sms/unknown", required = true)
    @Column("channel")
    var channel: String? = null

    @ApiModelProperty(value = "微信/支付宝/短信", required = true)
    @Column("method")
    var method: String? = null

    @ApiModelProperty(value = "包名", required = true)
    @Column("package_name")
    var packageName: String? = null

    @ApiModelProperty(value = "方向：income/expense/refund/unknown", required = true)
    @Column("direction")
    var direction: String? = null

    @ApiModelProperty(value = "是否争议", required = true)
    @Column("disputed")
    var disputed: Boolean? = null

    @ApiModelProperty(value = "争议原因", required = true)
    @Column("disputed_reason")
    var disputedReason: String? = null

    @ApiModelProperty(value = "金额", required = true)
    @Column("amount")
    var amount: BigDecimal? = null

    @ApiModelProperty(value = "结果：完成", required = true)
    @Column("result")
    var result: String? = null

    @ApiModelProperty(value = "事件时间戳（毫秒）", required = true)
    @Column("time_millis")
    var timeMillis: Long? = null

    @ApiModelProperty(value = "统一详情文本", required = true)
    @Column("detail")
    var detail: String? = null

    @ApiModelProperty(value = "通知标题原文")
    @Column("raw_title")
    var rawTitle: String? = null

    @ApiModelProperty(value = "通知正文原文")
    @Column("raw_text")
    var rawText: String? = null
}

