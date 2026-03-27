# IoT遥测接口文档

## 1. 背景与数据链路

基于 `docs/sql/03_iot_telemetry_10m.sql` 与现有代码，数据链路如下：

1. 设备每30秒发布一条JSON遥测到 MQTT。
2. 服务端 `MqttClientManager` 接收消息后写入内存聚合器 `TelemetryAggregator`。
3. 聚合规则：按 `deviceId + 10分钟整点窗口`（如 `10:00~10:10`）聚合。
4. 定时任务 `TelemetryPersistScheduler` 每分钟扫描一次，将已结束窗口批量落库到 `iot_telemetry_10m`。

因此前端查看需求分为：

- **实时部分**：看“当前进行中的窗口”内存聚合数据（尚未落库或刚在聚合）。
- **历史部分**：看已落库的 `iot_telemetry_10m` 窗口记录。

---

## 2. 实时查看接口

### 2.1 接口信息

- **URL**: `GET /api/iot/telemetry/realtime`
- **说明**: 获取当前进行中的10分钟窗口聚合数据（来自内存聚合器）。

### 2.2 Query参数

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| deviceId | string | 否 | 设备ID，不传则返回全部设备实时窗口 |

### 2.3 响应示例

```json
{
  "code": 200,
  "success": true,
  "message": "请求成功",
  "data": [
    {
      "deviceId": "stm32_001",
      "windowStart": "2026-03-25 10:20:00",
      "windowEnd": "2026-03-25 10:30:00",
      "sampleCount": 8,
      "tempAvg": 26.3525,
      "humidityAvg": 48.2100,
      "pm25Avg": 12.3000,
      "co2Avg": 652.1000,
      "tvocAvg": 88.2000,
      "aqiAvg": 23.5000,
      "motionCount": 2,
      "motionAny": true
    }
  ]
}
```

---

## 3. 历史查询接口（分页）

### 3.1 接口信息

- **URL**: `POST /api/iot/telemetry/history/page`
- **说明**: 查询已落库的10分钟窗口历史数据（来自 `iot_telemetry_10m`）。

### 3.2 请求体

请求体采用通用分页结构 `PageParam<T>`：

```json
{
  "page": 1,
  "limit": 20,
  "model": {
    "deviceId": "stm32_001",
    "startTime": "2026-03-25 00:00:00",
    "endTime": "2026-03-25 23:59:59"
  }
}
```

`model` 字段说明：

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| deviceId | string | 否 | 设备ID |
| startTime | datetime | 否 | 窗口开始时间下限（含） |
| endTime | datetime | 否 | 窗口开始时间上限（含） |

### 3.3 响应示例

```json
{
  "code": 200,
  "success": true,
  "message": "请求成功",
  "data": {
    "list": [
      {
        "id": 192837465001,
        "deviceId": "stm32_001",
        "recordDate": "2026-03-25",
        "windowStart": "2026-03-25 10:10:00",
        "windowEnd": "2026-03-25 10:20:00",
        "sampleCount": 20,
        "tempAvg": 26.4100,
        "tempMin": 26.1200,
        "tempMax": 26.7800,
        "humidityAvg": 48.3200,
        "humidityMin": 47.8000,
        "humidityMax": 48.9000,
        "pm25Avg": 12.0000,
        "pm25Min": 10.0000,
        "pm25Max": 14.0000,
        "co2Avg": 650.5000,
        "co2Min": 640.0000,
        "co2Max": 665.0000,
        "tvocAvg": 90.2000,
        "tvocMin": 85.0000,
        "tvocMax": 97.0000,
        "aqiAvg": 23.1000,
        "aqiMin": 20,
        "aqiMax": 28,
        "motionCount": 3,
        "motionAny": true
      }
    ],
    "count": 288
  }
}
```

---

## 4. 新增接口（统计与趋势）

> 以下为新增设计接口信息，已纳入文档，便于前端提前联调。

### 4.1 趋势图接口（按窗口序列）

- **URL**: `POST /api/iot/telemetry/history/trend`
- **说明**: 返回指定设备在时间范围内的多指标趋势序列，适合折线图直接渲染。

请求体示例：

```json
{
  "deviceId": "stm32_001",
  "startTime": "2026-03-25 00:00:00",
  "endTime": "2026-03-25 23:59:59",
  "metrics": ["tempAvg", "humidityAvg", "pm25Avg", "co2Avg", "aqiAvg", "motionCount"]
}
```

响应示例：

```json
{
  "code": 200,
  "success": true,
  "message": "请求成功",
  "data": {
    "deviceId": "stm32_001",
    "points": [
      {
        "windowStart": "2026-03-25 10:10:00",
        "windowEnd": "2026-03-25 10:20:00",
        "tempAvg": 26.41,
        "humidityAvg": 48.32,
        "pm25Avg": 12.00,
        "co2Avg": 650.50,
        "aqiAvg": 23.10,
        "motionCount": 3
      }
    ]
  }
}
```

### 4.2 汇总统计接口（概览卡片）

- **URL**: `POST /api/iot/telemetry/history/summary`
- **说明**: 返回时间段内概览统计（最值/均值/总样本数/人体检测次数），用于顶部统计卡。

请求体示例：

```json
{
  "deviceId": "stm32_001",
  "startTime": "2026-03-25 00:00:00",
  "endTime": "2026-03-25 23:59:59"
}
```

响应示例：

```json
{
  "code": 200,
  "success": true,
  "message": "请求成功",
  "data": {
    "deviceId": "stm32_001",
    "windowCount": 144,
    "sampleCountTotal": 2860,
    "tempAvg": 25.97,
    "tempMin": 22.10,
    "tempMax": 28.43,
    "humidityAvg": 49.27,
    "pm25Avg": 11.36,
    "co2Avg": 642.81,
    "aqiAvg": 22.40,
    "motionCountTotal": 37,
    "motionAnyWindowCount": 18
  }
}
```

### 4.3 设备列表接口（历史设备下拉）

- **URL**: `GET /api/iot/telemetry/history/devices`
- **说明**: 返回历史库中出现过的设备列表，供前端筛选下拉框使用。

响应示例：

```json
{
  "code": 200,
  "success": true,
  "message": "请求成功",
  "data": ["stm32_001", "stm32_002"]
}
```

---

## 5. 字段含义（前端展示建议）

- `windowStart/windowEnd`: 10分钟窗口边界，可作为X轴。
- `sampleCount`: 窗口内采样数，理论值约20（30秒上报一次）。
- `temp*/humidity*`: 温湿度统计值，可用于折线图与区间带。
- `pm25*/co2*/tvoc*/aqi*`: 空气质量指标，支持多图表展示。
- `motionCount/motionAny`: 人体检测统计，可用于占用状态或告警面板。
- `recordDate`: 日维度过滤/分区查询字段。

---

## 6. 状态码约定

- `200`: 查询成功
- `400`: 请求参数错误
- `401/403`: 若后续加鉴权，可能返回未授权/无权限

---

## 7. 备注

- 实时接口读取内存聚合器，服务重启后仅能看到重启后的实时窗口。
- 历史接口读取数据库，适合看趋势与报表。
- 当前历史接口按 `windowStart desc` 返回。
- 新增的 `trend/summary/devices` 接口目前为文档设计，后端实现时会与本文档保持一致。

