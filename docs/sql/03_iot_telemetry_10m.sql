/*
 Navicat / MySQL
 物联网 - 10分钟整点窗口聚合表

 说明：
 - 设备（STM32）每30秒上报一次 JSON
 - 服务端按 ts 归档到整点对齐的10分钟窗口，并每10分钟（通过定时扫描）落库一次
*/

DROP TABLE IF EXISTS `iot_telemetry_10m`;

CREATE TABLE `iot_telemetry_10m` (
  `id` bigint NOT NULL COMMENT '主键',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `update_time` datetime DEFAULT NULL COMMENT '最后修改时间',
  `updated_by` bigint DEFAULT NULL COMMENT '最后修改人ID',

  `device_id` varchar(64) NOT NULL COMMENT '设备ID',
  `record_date` date NOT NULL COMMENT '记录日期（窗口开始日期）',
  `window_start` datetime NOT NULL COMMENT '窗口开始时间（整点对齐，每10分钟）',
  `window_end` datetime NOT NULL COMMENT '窗口结束时间（window_start+10分钟）',
  `sample_count` int NOT NULL DEFAULT 0 COMMENT '窗口内样本数',

  `temp_avg` decimal(12,4) DEFAULT NULL COMMENT '温度均值(°C)',
  `temp_min` decimal(12,4) DEFAULT NULL COMMENT '温度最小值(°C)',
  `temp_max` decimal(12,4) DEFAULT NULL COMMENT '温度最大值(°C)',

  `humidity_avg` decimal(12,4) DEFAULT NULL COMMENT '湿度均值(%)',
  `humidity_min` decimal(12,4) DEFAULT NULL COMMENT '湿度最小值(%)',
  `humidity_max` decimal(12,4) DEFAULT NULL COMMENT '湿度最大值(%)',

  `pm25_avg` decimal(12,4) DEFAULT NULL COMMENT 'PM2.5均值(ug/m3)',
  `pm25_min` decimal(12,4) DEFAULT NULL COMMENT 'PM2.5最小值(ug/m3)',
  `pm25_max` decimal(12,4) DEFAULT NULL COMMENT 'PM2.5最大值(ug/m3)',

  `co2_avg` decimal(12,4) DEFAULT NULL COMMENT 'CO2均值(ppm)',
  `co2_min` decimal(12,4) DEFAULT NULL COMMENT 'CO2最小值(ppm)',
  `co2_max` decimal(12,4) DEFAULT NULL COMMENT 'CO2最大值(ppm)',

  `tvoc_avg` decimal(12,4) DEFAULT NULL COMMENT 'TVOC均值(ppb)',
  `tvoc_min` decimal(12,4) DEFAULT NULL COMMENT 'TVOC最小值(ppb)',
  `tvoc_max` decimal(12,4) DEFAULT NULL COMMENT 'TVOC最大值(ppb)',

  `aqi_avg` decimal(12,4) DEFAULT NULL COMMENT 'AQI均值',
  `aqi_min` int DEFAULT NULL COMMENT 'AQI最小值',
  `aqi_max` int DEFAULT NULL COMMENT 'AQI最大值',

  `motion_count` int NOT NULL DEFAULT 0 COMMENT '人体检测次数（detected=true累计）',
  `motion_any` tinyint(1) NOT NULL DEFAULT 0 COMMENT '窗口内是否出现过人体',

  PRIMARY KEY (`id`),
  KEY `idx_device_window_start` (`device_id`, `window_start`),
  KEY `idx_record_date` (`record_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='物联网遥测10分钟聚合';

