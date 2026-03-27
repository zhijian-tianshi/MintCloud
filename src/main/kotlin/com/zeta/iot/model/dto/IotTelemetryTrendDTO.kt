package com.zeta.iot.model.dto

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import java.math.BigDecimal
import java.time.LocalDateTime

@ApiModel(description = "物联网遥测趋势数据")
data class IotTelemetryTrendDTO(
    @ApiModelProperty("设备ID")
    val deviceId: String?,

    @ApiModelProperty("实际返回指标")
    val metrics: List<String>,

    @ApiModelProperty("趋势点")
    val points: List<IotTelemetryTrendPointDTO>,
)

@ApiModel(description = "物联网遥测趋势点")
data class IotTelemetryTrendPointDTO(
    @ApiModelProperty("窗口开始时间")
    val windowStart: LocalDateTime?,
    @ApiModelProperty("窗口结束时间")
    val windowEnd: LocalDateTime?,

    @ApiModelProperty("温度均值")
    val tempAvg: BigDecimal? = null,
    @ApiModelProperty("湿度均值")
    val humidityAvg: BigDecimal? = null,
    @ApiModelProperty("PM2.5均值")
    val pm25Avg: BigDecimal? = null,
    @ApiModelProperty("CO2均值")
    val co2Avg: BigDecimal? = null,
    @ApiModelProperty("TVOC均值")
    val tvocAvg: BigDecimal? = null,
    @ApiModelProperty("AQI均值")
    val aqiAvg: BigDecimal? = null,
    @ApiModelProperty("人体检测次数")
    val motionCount: Int? = null,
)

