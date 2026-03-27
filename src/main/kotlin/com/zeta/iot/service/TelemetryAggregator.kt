package com.zeta.iot.service

import com.zeta.iot.model.TelemetryMessage
import com.zeta.iot.model.TelemetryWindowKey
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.concurrent.ConcurrentHashMap

/**
 * 内存聚合器：按 deviceId + 10分钟整点窗口 进行统计
 */
class TelemetryAggregator(
    private val zoneId: ZoneId = ZoneId.systemDefault(),
) {
    private val buckets = ConcurrentHashMap<TelemetryWindowKey, Bucket>()

    fun add(message: TelemetryMessage) {
        val deviceId = message.deviceId?.trim().orEmpty()
        val ts = message.ts
        if (deviceId.isBlank() || ts == null) return

        val sampleTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(ts), zoneId)
        val windowStart = alignToTenMinuteWindowStart(sampleTime)
        val key = TelemetryWindowKey(deviceId = deviceId, windowStart = windowStart)

        buckets.compute(key) { _, old ->
            val bucket = old ?: Bucket(deviceId, windowStart)
            bucket.update(message)
            bucket
        }
    }

    /**
     * 取出所有已经结束的窗口（窗口结束时间 <= nowWindowStart）
     */
    fun drainClosedWindows(now: LocalDateTime = LocalDateTime.now(zoneId)): List<BucketSnapshot> {
        val nowWindowStart = alignToTenMinuteWindowStart(now)

        val closed = mutableListOf<BucketSnapshot>()
        val iterator = buckets.entries.iterator()
        while (iterator.hasNext()) {
            val (key, bucket) = iterator.next()
            val windowEndExclusive = key.windowStart.plusMinutes(10)
            if (!windowEndExclusive.isAfter(nowWindowStart)) {
                closed.add(bucket.snapshot())
                iterator.remove()
            }
        }
        return closed
    }

    /**
     * 获取仍在进行中的窗口快照（不移除内存数据）
     */
    fun snapshotOpenWindows(now: LocalDateTime = LocalDateTime.now(zoneId)): List<BucketSnapshot> {
        val nowWindowStart = alignToTenMinuteWindowStart(now)
        val open = mutableListOf<BucketSnapshot>()
        buckets.forEach { (key, bucket) ->
            val windowEndExclusive = key.windowStart.plusMinutes(10)
            if (windowEndExclusive.isAfter(nowWindowStart)) {
                open.add(bucket.snapshot())
            }
        }
        return open
    }

    private fun alignToTenMinuteWindowStart(t: LocalDateTime): LocalDateTime {
        val truncated = t.truncatedTo(ChronoUnit.MINUTES)
        val minute = truncated.minute
        val alignedMinute = (minute / 10) * 10
        return truncated.withMinute(alignedMinute).withSecond(0).withNano(0)
    }

    class Bucket(
        val deviceId: String,
        val windowStart: LocalDateTime,
    ) {
        private var sampleCount: Int = 0

        private val temp = Stats()
        private val humidity = Stats()
        private val pm25 = Stats()
        private val co2 = Stats()
        private val tvoc = Stats()
        private val aqi = IntStats()

        private var motionCount: Int = 0
        private var motionAny: Boolean = false

        fun update(m: TelemetryMessage) {
            sampleCount++

            val env = m.env
            temp.add(env?.tempC)
            humidity.add(env?.humidityPct)

            val air = env?.air
            pm25.add(air?.pm25UgM3)
            co2.add(air?.co2Ppm)
            tvoc.add(air?.tvocPpb)
            aqi.add(air?.aqi)

            val detected = m.motion?.detected == true
            if (detected) {
                motionCount++
                motionAny = true
            }
        }

        fun snapshot(): BucketSnapshot {
            val windowEnd = windowStart.plusMinutes(10)
            return BucketSnapshot(
                deviceId = deviceId,
                windowStart = windowStart,
                windowEnd = windowEnd,
                sampleCount = sampleCount,
                temp = temp.snapshot(),
                humidity = humidity.snapshot(),
                pm25 = pm25.snapshot(),
                co2 = co2.snapshot(),
                tvoc = tvoc.snapshot(),
                aqi = aqi.snapshot(),
                motionCount = motionCount,
                motionAny = motionAny,
            )
        }
    }

    data class BucketSnapshot(
        val deviceId: String,
        val windowStart: LocalDateTime,
        val windowEnd: LocalDateTime,
        val sampleCount: Int,
        val temp: StatsSnapshot,
        val humidity: StatsSnapshot,
        val pm25: StatsSnapshot,
        val co2: StatsSnapshot,
        val tvoc: StatsSnapshot,
        val aqi: IntStatsSnapshot,
        val motionCount: Int,
        val motionAny: Boolean,
    )

    data class StatsSnapshot(
        val count: Int,
        val avg: BigDecimal?,
        val min: BigDecimal?,
        val max: BigDecimal?,
    )

    private class Stats {
        private var count = 0
        private var sum = BigDecimal.ZERO
        private var min: BigDecimal? = null
        private var max: BigDecimal? = null

        fun add(v: Double?) {
            if (v == null) return
            val bd = BigDecimal.valueOf(v)
            count++
            sum = sum.add(bd)
            min = min?.min(bd) ?: bd
            max = max?.max(bd) ?: bd
        }

        fun snapshot(): StatsSnapshot {
            val avg = if (count == 0) null else sum.divide(BigDecimal.valueOf(count.toLong()), 4, RoundingMode.HALF_UP)
            return StatsSnapshot(count = count, avg = avg, min = min, max = max)
        }
    }

    data class IntStatsSnapshot(
        val count: Int,
        val avg: BigDecimal?,
        val min: Int?,
        val max: Int?,
    )

    private class IntStats {
        private var count = 0
        private var sum = 0L
        private var min: Int? = null
        private var max: Int? = null

        fun add(v: Int?) {
            if (v == null) return
            count++
            sum += v.toLong()
            min = min?.coerceAtMost(v) ?: v
            max = max?.coerceAtLeast(v) ?: v
        }

        fun snapshot(): IntStatsSnapshot {
            val avg = if (count == 0) null else BigDecimal.valueOf(sum).divide(BigDecimal.valueOf(count.toLong()), 4, RoundingMode.HALF_UP)
            return IntStatsSnapshot(count = count, avg = avg, min = min, max = max)
        }
    }
}

