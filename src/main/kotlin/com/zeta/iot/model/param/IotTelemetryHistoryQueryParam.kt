package com.zeta.iot.model.param

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime

@ApiModel(description = "物联网遥测历史查询参数")
data class IotTelemetryHistoryQueryParam(
    @ApiModelProperty(value = "设备ID", required = false, example = "stm32_001")
    val deviceId: String? = null,

    @ApiModelProperty(value = "窗口开始时间（起）", required = false, example = "2026-03-25 00:00:00")
    val startTime: LocalDateTime? = null,

    @ApiModelProperty(value = "窗口开始时间（止）", required = false, example = "2026-03-25 23:59:59")
    val endTime: LocalDateTime? = null,
)

