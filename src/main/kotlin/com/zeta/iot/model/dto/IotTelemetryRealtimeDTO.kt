package com.zeta.iot.model.dto

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import java.math.BigDecimal
import java.time.LocalDateTime

@ApiModel(description = "物联网遥测实时窗口数据")
data class IotTelemetryRealtimeDTO(
    @ApiModelProperty("设备ID")
    val deviceId: String,

    @ApiModelProperty("窗口开始时间")
    val windowStart: LocalDateTime,

    @ApiModelProperty("窗口结束时间")
    val windowEnd: LocalDateTime,

    @ApiModelProperty("当前窗口样本数")
    val sampleCount: Int,

    @ApiModelProperty("温度均值")
    val tempAvg: BigDecimal?,
    @ApiModelProperty("湿度均值")
    val humidityAvg: BigDecimal?,
    @ApiModelProperty("PM2.5均值")
    val pm25Avg: BigDecimal?,
    @ApiModelProperty("CO2均值")
    val co2Avg: BigDecimal?,
    @ApiModelProperty("TVOC均值")
    val tvocAvg: BigDecimal?,
    @ApiModelProperty("AQI均值")
    val aqiAvg: BigDecimal?,

    @ApiModelProperty("人体检测次数（当前窗口）")
    val motionCount: Int,
    @ApiModelProperty("窗口内是否出现过人体")
    val motionAny: Boolean,
)

