package com.zeta.iot.model

import java.time.LocalDateTime

data class TelemetryWindowKey(
    val deviceId: String,
    val windowStart: LocalDateTime,
)

