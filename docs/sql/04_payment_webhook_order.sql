/*
  收款消息 Webhook（回调推送）- 入库表

  目标：
  - 接收 Android 端推送的收款/支付通知解析结果
  - 持久化用于对账、统计、追溯
  - 使用唯一键避免重复回调导致重复入库
*/

DROP TABLE IF EXISTS `payment_webhook_order`;

CREATE TABLE `payment_webhook_order` (
  `id` bigint(20) NOT NULL COMMENT '主键（服务端生成）',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `created_by` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `updated_by` bigint(20) NULL DEFAULT NULL COMMENT '修改人',

  `event` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '事件名：payment.order',
  `act_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '激活码（鉴权/归属）',
  `device_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '设备编码（Android ID）',
  `client_order_id` bigint(20) NOT NULL COMMENT '客户端本地订单自增ID（Room id）',

  `source` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '来源：notification/sms',
  `channel` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '渠道：wechat/alipay/sms/unknown',
  `method` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '展示标签：微信/支付宝/短信',
  `package_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '来源包名/标识：com.tencent.mm/com.eg.android.AlipayGphone/sms',
  `direction` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '方向：income/expense/refund/unknown',
  `disputed` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否争议',
  `disputed_reason` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '争议原因：none/no_strong_hint/direction_unknown/income_expense_conflict',

  `amount` decimal(18,2) NOT NULL COMMENT '金额',
  `result` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '结果：完成',
  `time_millis` bigint(20) NOT NULL COMMENT '事件时间戳（毫秒）',
  `detail` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '统一详情文本（用于存档/AI）',

  `raw_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '通知标题原文',
  `raw_text` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '通知正文原文',

  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_act_device_client_order` (`act_code`,`device_id`,`client_order_id`) USING BTREE,
  KEY `idx_time_millis` (`time_millis`) USING BTREE,
  KEY `idx_act_code` (`act_code`) USING BTREE,
  KEY `idx_device_id` (`device_id`) USING BTREE
) ENGINE=InnoDB CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='收款消息Webhook入库表' ROW_FORMAT=DYNAMIC;

