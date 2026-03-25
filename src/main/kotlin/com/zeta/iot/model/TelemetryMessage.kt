package com.zeta.iot.model

/**
 * 设备上报消息（建议格式 v=1.0）
 *
 * 字段说明（逐项）
 * v：消息版本号。用于后续扩展字段/兼容旧格式（例如将来升级到 "1.1" 也能区分解析策略）。
 * deviceId：设备唯一标识（建议与 MQTT 主题里的设备号一致）。服务端用它做分设备聚合、查询与统计。
 * ts：设备采样时间戳（Epoch 毫秒）。服务端会用它把数据归到整点对齐的 10 分钟窗口（例如 10:01:xx 会归到 10:00~10:10）。
 * seq：设备端递增序号（消息序列号）。用于排查丢包/乱序/重启（重启后可能从 0 或 1 重新计数）。
 * env（环境传感器数据）
 * env.tempC：温度，单位 °C（摄氏度）。
 * env.humidityPct：相对湿度，单位 %（0~100）。
 * env.air：空气质量相关（有哪个传感器就上报哪个；没有可省略或为 null）。
 * env.air.pm25UgM3：PM2.5 浓度，单位 µg/m³。
 * env.air.co2Ppm：二氧化碳浓度，单位 ppm。
 * env.air.tvocPpb：TVOC（总挥发性有机物）浓度，单位 ppb。
 * env.air.aqi：AQI 空气质量指数（整数，通常越低越好；具体区间按你使用的标准/算法）。
 * motion（人体检测）
 * motion.detected：是否检测到人体，布尔值：
 * true：当前采样点检测到人体
 * false：未检测到
 * motion.confidence：置信度（0~1 的小数，越接近 1 越可信）。如果你的传感器/算法没有这个值，可以不传。
 *{
 *   "v": "1.0",
 *   "deviceId": "stm32_001",
 *   "ts": 1774400000123,
 *   "seq": 12345,
 *   "env": {
 *     "tempC": 26.31,
 *     "humidityPct": 48.2,
 *     "air": {
 *       "pm25UgM3": 12.0,
 *       "co2Ppm": 650,
 *       "tvocPpb": 90,
 *       "aqi": 23
 *     }
 *   },
 *   "motion": {
 *     "detected": true,
 *     "confidence": 0.86
 *   }
 * }
 * 说明：
 * - 设备每 30 秒发布一次
 * - 服务端按 ts 归档到整点对齐的 10 分钟窗口
 */
data class TelemetryMessage(
    val v: String? = null,
    val deviceId: String? = null,
    /** epoch millis */
    val ts: Long? = null,
    val seq: Long? = null,
    val env: Env? = null,
    val motion: Motion? = null,
    val batteryMv: Int? = null,
    val rssiDbm: Int? = null,
    val fw: String? = null,
) {
    data class Env(
        val tempC: Double? = null,
        val humidityPct: Double? = null,
        val air: Air? = null,
    )

    data class Air(
        val pm25UgM3: Double? = null,
        val co2Ppm: Double? = null,
        val tvocPpb: Double? = null,
        val aqi: Int? = null,
    )

    data class Motion(
        val detected: Boolean? = null,
        val confidence: Double? = null,
    )
}

