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

  `method` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '微信/支付宝',
  `package_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '包名：com.tencent.mm / com.eg.android.AlipayGphone',
  `amount` decimal(18,2) NOT NULL COMMENT '金额',
  `result` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '结果：完成',
  `time_millis` bigint(20) NOT NULL COMMENT '事件时间戳（毫秒）',

  `raw_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '通知标题原文',
  `raw_text` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '通知正文原文',

  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_act_device_client_order` (`act_code`,`device_id`,`client_order_id`) USING BTREE,
  KEY `idx_time_millis` (`time_millis`) USING BTREE,
  KEY `idx_act_code` (`act_code`) USING BTREE,
  KEY `idx_device_id` (`device_id`) USING BTREE
) ENGINE=InnoDB CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='收款消息Webhook入库表' ROW_FORMAT=DYNAMIC;

