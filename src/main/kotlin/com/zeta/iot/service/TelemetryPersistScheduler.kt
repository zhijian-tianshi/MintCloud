package com.zeta.iot.service

import com.zeta.iot.model.entity.IotTelemetry10m
import com.zeta.iot.mqtt.MqttClientManager
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * 每分钟扫描一次，落库已经关闭的 10 分钟整点窗口数据
 */
@Component
class TelemetryPersistScheduler(
    private val mqttClientManager: MqttClientManager,
    private val telemetry10mService: IIotTelemetry10mService,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * second minute hour day month week
     * 每分钟第5秒执行，避免与整点边界秒数抖动
     */
    @Scheduled(cron = "5 * * * * ?")
    fun persistClosedWindows() {
        val aggregator = mqttClientManager.getAggregator()
        val snapshots = aggregator.drainClosedWindows()
        if (snapshots.isEmpty()) return

        val entities = snapshots.map { s ->
            IotTelemetry10m().apply {
                deviceId = s.deviceId
                recordDate = s.windowStart.toLocalDate()
                windowStart = s.windowStart
                windowEnd = s.windowEnd
                sampleCount = s.sampleCount

                tempAvg = s.temp.avg
                tempMin = s.temp.min
                tempMax = s.temp.max

                humidityAvg = s.humidity.avg
                humidityMin = s.humidity.min
                humidityMax = s.humidity.max

                pm25Avg = s.pm25.avg
                pm25Min = s.pm25.min
                pm25Max = s.pm25.max

                co2Avg = s.co2.avg
                co2Min = s.co2.min
                co2Max = s.co2.max

                tvocAvg = s.tvoc.avg
                tvocMin = s.tvoc.min
                tvocMax = s.tvoc.max

                aqiAvg = s.aqi.avg
                aqiMin = s.aqi.min
                aqiMax = s.aqi.max

                motionCount = s.motionCount
                motionAny = s.motionAny
            }
        }

        // 批量入库
        telemetry10mService.saveBatch(entities)
        logger.info("Persisted iot_telemetry_10m rows={}", entities.size)
    }
}

