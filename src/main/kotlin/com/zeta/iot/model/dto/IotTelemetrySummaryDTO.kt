package com.zeta.iot.model.dto

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import java.math.BigDecimal

@ApiModel(description = "物联网遥测汇总统计")
data class IotTelemetrySummaryDTO(
    @ApiModelProperty("设备ID")
    val deviceId: String?,
    @ApiModelProperty("窗口数量")
    val windowCount: Int,
    @ApiModelProperty("样本总数")
    val sampleCountTotal: Long,

    @ApiModelProperty("温度均值")
    val tempAvg: BigDecimal?,
    @ApiModelProperty("温度最小值")
    val tempMin: BigDecimal?,
    @ApiModelProperty("温度最大值")
    val tempMax: BigDecimal?,

    @ApiModelProperty("湿度均值")
    val humidityAvg: BigDecimal?,
    @ApiModelProperty("湿度最小值")
    val humidityMin: BigDecimal?,
    @ApiModelProperty("湿度最大值")
    val humidityMax: BigDecimal?,

    @ApiModelProperty("PM2.5均值")
    val pm25Avg: BigDecimal?,
    @ApiModelProperty("CO2均值")
    val co2Avg: BigDecimal?,
    @ApiModelProperty("AQI均值")
    val aqiAvg: BigDecimal?,

    @ApiModelProperty("人体检测总次数")
    val motionCountTotal: Long,
    @ApiModelProperty("出现人体的窗口数")
    val motionAnyWindowCount: Int,
)

