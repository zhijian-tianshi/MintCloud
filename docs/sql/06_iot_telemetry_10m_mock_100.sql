/*
  iot_telemetry_10m 生成100条测试数据（兼容 MySQL 5.7+）

  用法：
  1) 先确保已执行表结构脚本：03_iot_telemetry_10m.sql
  2) 执行本脚本插入100条模拟数据
  3) 可按需修改起始时间 @base_time
*/

-- 起始窗口时间（整点对齐到10分钟）
SET @base_time := TIMESTAMP('2026-03-25 00:00:00');

INSERT INTO `iot_telemetry_10m` (
    `id`,
    `create_time`,
    `created_by`,
    `update_time`,
    `updated_by`,
    `device_id`,
    `record_date`,
    `window_start`,
    `window_end`,
    `sample_count`,
    `temp_avg`,
    `temp_min`,
    `temp_max`,
    `humidity_avg`,
    `humidity_min`,
    `humidity_max`,
    `pm25_avg`,
    `pm25_min`,
    `pm25_max`,
    `co2_avg`,
    `co2_min`,
    `co2_max`,
    `tvoc_avg`,
    `tvoc_min`,
    `tvoc_max`,
    `aqi_avg`,
    `aqi_min`,
    `aqi_max`,
    `motion_count`,
    `motion_any`
)
SELECT
    900000000000 + n AS id,
    DATE_ADD(@base_time, INTERVAL n MINUTE) AS create_time,
    NULL AS created_by,
    NULL AS update_time,
    NULL AS updated_by,
    CONCAT('stm32_', LPAD(((n - 1) % 5) + 1, 3, '0')) AS device_id,
    DATE(DATE_ADD(@base_time, INTERVAL (n - 1) * 10 MINUTE)) AS record_date,
    DATE_ADD(@base_time, INTERVAL (n - 1) * 10 MINUTE) AS window_start,
    DATE_ADD(@base_time, INTERVAL (n - 1) * 10 + 10 MINUTE) AS window_end,
    18 + (n % 3) AS sample_count,

    -- 温度（摄氏度）
    ROUND(24.50 + (n % 12) * 0.15 + ((n % 5) * 0.01), 4) AS temp_avg,
    ROUND(24.10 + (n % 12) * 0.15, 4) AS temp_min,
    ROUND(24.90 + (n % 12) * 0.15, 4) AS temp_max,

    -- 湿度（%）
    ROUND(45.00 + (n % 20) * 0.30, 4) AS humidity_avg,
    ROUND(44.20 + (n % 20) * 0.30, 4) AS humidity_min,
    ROUND(45.80 + (n % 20) * 0.30, 4) AS humidity_max,

    -- PM2.5（ug/m3）
    ROUND(8.00 + (n % 15) * 0.60, 4) AS pm25_avg,
    ROUND(6.00 + (n % 15) * 0.60, 4) AS pm25_min,
    ROUND(10.00 + (n % 15) * 0.60, 4) AS pm25_max,

    -- CO2（ppm）
    ROUND(550.00 + (n % 30) * 6.50, 4) AS co2_avg,
    ROUND(530.00 + (n % 30) * 6.50, 4) AS co2_min,
    ROUND(570.00 + (n % 30) * 6.50, 4) AS co2_max,

    -- TVOC（ppb）
    ROUND(60.00 + (n % 25) * 2.20, 4) AS tvoc_avg,
    ROUND(52.00 + (n % 25) * 2.20, 4) AS tvoc_min,
    ROUND(68.00 + (n % 25) * 2.20, 4) AS tvoc_max,

    -- AQI
    ROUND(18.00 + (n % 20) * 0.95, 4) AS aqi_avg,
    15 + (n % 20) AS aqi_min,
    22 + (n % 20) AS aqi_max,

    -- 人体检测
    (n % 4) AS motion_count,
    IF((n % 4) > 0, 1, 0) AS motion_any
FROM (
    SELECT (t.tens * 10 + o.ones + 1) AS n
    FROM
      (SELECT 0 AS ones UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL
       SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) o
    CROSS JOIN
      (SELECT 0 AS tens UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL
       SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) t
    ORDER BY n
) seq;

-- 可选：查看效果
-- SELECT device_id, window_start, temp_avg, humidity_avg, pm25_avg, co2_avg, aqi_avg, motion_count
-- FROM iot_telemetry_10m
-- ORDER BY window_start DESC
-- LIMIT 20;

