package com.zeta.iot.controller

import cn.hutool.core.util.StrUtil
import com.mybatisflex.core.query.QueryWrapper
import com.zeta.iot.model.dto.IotTelemetryRealtimeDTO
import com.zeta.iot.model.dto.IotTelemetrySummaryDTO
import com.zeta.iot.model.dto.IotTelemetryTrendDTO
import com.zeta.iot.model.dto.IotTelemetryTrendPointDTO
import com.zeta.iot.model.entity.IotTelemetry10m
import com.zeta.iot.model.param.IotTelemetryHistoryQueryParam
import com.zeta.iot.model.param.IotTelemetryTrendQueryParam
import com.zeta.iot.mqtt.MqttClientManager
import com.zeta.iot.service.IIotTelemetry10mService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.zetaframework.base.param.PageParam
import org.zetaframework.base.result.ApiResult
import org.zetaframework.base.result.PageResult
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime

@Api(tags = ["物联网遥测"])
@RestController
@RequestMapping("/api/iot/telemetry")
class IotTelemetryController(
    private val mqttClientManager: MqttClientManager,
    private val telemetry10mService: IIotTelemetry10mService,
) {

    @ApiOperation("实时查看（当前10分钟窗口）")
    @GetMapping("/realtime")
    fun realtime(
        @RequestParam(required = false) deviceId: String?,
    ): ApiResult<List<IotTelemetryRealtimeDTO>> {
        val snapshots = mqttClientManager.getAggregator().snapshotOpenWindows(LocalDateTime.now())
        val list = snapshots
            .asSequence()
            .filter { deviceId.isNullOrBlank() || it.deviceId == deviceId }
            .sortedByDescending { it.windowStart }
            .map {
                IotTelemetryRealtimeDTO(
                    deviceId = it.deviceId,
                    windowStart = it.windowStart,
                    windowEnd = it.windowEnd,
                    sampleCount = it.sampleCount,
                    tempAvg = it.temp.avg,
                    humidityAvg = it.humidity.avg,
                    pm25Avg = it.pm25.avg,
                    co2Avg = it.co2.avg,
                    tvocAvg = it.tvoc.avg,
                    aqiAvg = it.aqi.avg,
                    motionCount = it.motionCount,
                    motionAny = it.motionAny,
                )
            }
            .toList()
        return ApiResult.success(data = list)
    }

    @ApiOperation("历史查询（10分钟聚合，分页）")
    @PostMapping("/history/page")
    fun historyPage(
        @RequestBody param: PageParam<IotTelemetryHistoryQueryParam>,
    ): ApiResult<PageResult<IotTelemetry10m>> {
        val model = param.model ?: IotTelemetryHistoryQueryParam()
        val rows = queryRows(model)

        val page = if (param.page <= 0) 1 else param.page
        val limit = if (param.limit <= 0) 10 else param.limit
        val from = ((page - 1) * limit).toInt().coerceAtMost(rows.size)
        val to = (from + limit.toInt()).coerceAtMost(rows.size)
        val pageList = if (from >= to) emptyList() else rows.subList(from, to)

        return ApiResult.success(data = PageResult(pageList, rows.size.toLong()))
    }

    @ApiOperation("历史趋势（按窗口序列）")
    @PostMapping("/history/trend")
    fun historyTrend(
        @RequestBody param: IotTelemetryTrendQueryParam,
    ): ApiResult<IotTelemetryTrendDTO> {
        val historyModel = IotTelemetryHistoryQueryParam(param.deviceId, param.startTime, param.endTime)
        val rows = queryRows(historyModel)
        val metricSet = (param.metrics ?: defaultMetrics()).toSet()

        val points = rows.map { row ->
            IotTelemetryTrendPointDTO(
                windowStart = row.windowStart,
                windowEnd = row.windowEnd,
                tempAvg = if (metricSet.contains("tempAvg")) row.tempAvg else null,
                humidityAvg = if (metricSet.contains("humidityAvg")) row.humidityAvg else null,
                pm25Avg = if (metricSet.contains("pm25Avg")) row.pm25Avg else null,
                co2Avg = if (metricSet.contains("co2Avg")) row.co2Avg else null,
                tvocAvg = if (metricSet.contains("tvocAvg")) row.tvocAvg else null,
                aqiAvg = if (metricSet.contains("aqiAvg")) row.aqiAvg else null,
                motionCount = if (metricSet.contains("motionCount")) row.motionCount else null,
            )
        }

        return ApiResult.success(data = IotTelemetryTrendDTO(param.deviceId, metricSet.toList(), points))
    }

    @ApiOperation("历史汇总统计（概览）")
    @PostMapping("/history/summary")
    fun historySummary(
        @RequestBody param: IotTelemetryTrendQueryParam,
    ): ApiResult<IotTelemetrySummaryDTO> {
        val historyModel = IotTelemetryHistoryQueryParam(param.deviceId, param.startTime, param.endTime)
        val rows = queryRows(historyModel)

        val sampleCountTotal = rows.sumOf { (it.sampleCount ?: 0).toLong() }
        val motionCountTotal = rows.sumOf { (it.motionCount ?: 0).toLong() }
        val motionAnyWindowCount = rows.count { it.motionAny == true }

        val summary = IotTelemetrySummaryDTO(
            deviceId = param.deviceId,
            windowCount = rows.size,
            sampleCountTotal = sampleCountTotal,
            tempAvg = avg(rows.mapNotNull { it.tempAvg }),
            tempMin = rows.mapNotNull { it.tempMin }.minOrNull(),
            tempMax = rows.mapNotNull { it.tempMax }.maxOrNull(),
            humidityAvg = avg(rows.mapNotNull { it.humidityAvg }),
            humidityMin = rows.mapNotNull { it.humidityMin }.minOrNull(),
            humidityMax = rows.mapNotNull { it.humidityMax }.maxOrNull(),
            pm25Avg = avg(rows.mapNotNull { it.pm25Avg }),
            co2Avg = avg(rows.mapNotNull { it.co2Avg }),
            aqiAvg = avg(rows.mapNotNull { it.aqiAvg }),
            motionCountTotal = motionCountTotal,
            motionAnyWindowCount = motionAnyWindowCount,
        )
        return ApiResult.success(data = summary)
    }

    @ApiOperation("历史设备列表")
    @GetMapping("/history/devices")
    fun historyDevices(): ApiResult<List<String>> {
        val wrapper = QueryWrapper().orderBy(IotTelemetry10m::deviceId, true)
        val devices = telemetry10mService.list(wrapper).orEmpty()
            .mapNotNull { it.deviceId }
            .filter { it.isNotBlank() }
            .distinct()
        return ApiResult.success(data = devices)
    }

    private fun queryRows(model: IotTelemetryHistoryQueryParam): List<IotTelemetry10m> {
        val wrapper = QueryWrapper()
        if (StrUtil.isNotBlank(model.deviceId)) {
            wrapper.eq(IotTelemetry10m::deviceId, model.deviceId)
        }
        wrapper.orderBy(IotTelemetry10m::windowStart, false)

        return telemetry10mService.list(wrapper).orEmpty()
            .asSequence()
            .filter { model.startTime == null || (it.windowStart != null && !it.windowStart!!.isBefore(model.startTime)) }
            .filter { model.endTime == null || (it.windowStart != null && !it.windowStart!!.isAfter(model.endTime)) }
            .toList()
    }

    private fun defaultMetrics(): List<String> = listOf(
        "tempAvg", "humidityAvg", "pm25Avg", "co2Avg", "tvocAvg", "aqiAvg", "motionCount"
    )

    private fun avg(values: List<BigDecimal>): BigDecimal? {
        if (values.isEmpty()) return null
        val sum = values.reduce { acc, v -> acc.add(v) }
        return sum.divide(BigDecimal.valueOf(values.size.toLong()), 4, RoundingMode.HALF_UP)
    }
}

