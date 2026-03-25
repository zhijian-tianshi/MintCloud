package com.zeta.iot.model.entity

import com.mybatisflex.annotation.Column
import com.mybatisflex.annotation.Table
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.zetaframework.base.entity.Entity
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@ApiModel(description = "物联网遥测10分钟聚合")
@Table(value = "iot_telemetry_10m")
class IotTelemetry10m : Entity<Long>() {

    @ApiModelProperty(value = "设备ID", required = true)
    @Column("device_id")
    var deviceId: String? = null

    @ApiModelProperty(value = "记录日期（窗口开始日期）", required = true)
    @Column("record_date")
    var recordDate: LocalDate? = null

    @ApiModelProperty(value = "窗口开始时间", required = true)
    @Column("window_start")
    var windowStart: LocalDateTime? = null

    @ApiModelProperty(value = "窗口结束时间", required = true)
    @Column("window_end")
    var windowEnd: LocalDateTime? = null

    @ApiModelProperty(value = "样本数", required = true)
    @Column("sample_count")
    var sampleCount: Int? = null

    @ApiModelProperty("温度均值")
    @Column("temp_avg")
    var tempAvg: BigDecimal? = null

    @ApiModelProperty("温度最小值")
    @Column("temp_min")
    var tempMin: BigDecimal? = null

    @ApiModelProperty("温度最大值")
    @Column("temp_max")
    var tempMax: BigDecimal? = null

    @ApiModelProperty("湿度均值")
    @Column("humidity_avg")
    var humidityAvg: BigDecimal? = null

    @ApiModelProperty("湿度最小值")
    @Column("humidity_min")
    var humidityMin: BigDecimal? = null

    @ApiModelProperty("湿度最大值")
    @Column("humidity_max")
    var humidityMax: BigDecimal? = null

    @ApiModelProperty("PM2.5均值")
    @Column("pm25_avg")
    var pm25Avg: BigDecimal? = null

    @ApiModelProperty("PM2.5最小值")
    @Column("pm25_min")
    var pm25Min: BigDecimal? = null

    @ApiModelProperty("PM2.5最大值")
    @Column("pm25_max")
    var pm25Max: BigDecimal? = null

    @ApiModelProperty("CO2均值")
    @Column("co2_avg")
    var co2Avg: BigDecimal? = null

    @ApiModelProperty("CO2最小值")
    @Column("co2_min")
    var co2Min: BigDecimal? = null

    @ApiModelProperty("CO2最大值")
    @Column("co2_max")
    var co2Max: BigDecimal? = null

    @ApiModelProperty("TVOC均值")
    @Column("tvoc_avg")
    var tvocAvg: BigDecimal? = null

    @ApiModelProperty("TVOC最小值")
    @Column("tvoc_min")
    var tvocMin: BigDecimal? = null

    @ApiModelProperty("TVOC最大值")
    @Column("tvoc_max")
    var tvocMax: BigDecimal? = null

    @ApiModelProperty("AQI均值")
    @Column("aqi_avg")
    var aqiAvg: BigDecimal? = null

    @ApiModelProperty("AQI最小值")
    @Column("aqi_min")
    var aqiMin: Int? = null

    @ApiModelProperty("AQI最大值")
    @Column("aqi_max")
    var aqiMax: Int? = null

    @ApiModelProperty("人体检测次数（detected=true累计）")
    @Column("motion_count")
    var motionCount: Int? = null

    @ApiModelProperty("窗口内是否出现过人体")
    @Column("motion_any")
    var motionAny: Boolean? = null
}

