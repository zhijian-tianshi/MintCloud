/*
  payment_webhook_order V1 -> V2 增量迁移脚本
  适用于你已经执行过 04_payment_webhook_order.sql（V1）的场景
*/

ALTER TABLE `payment_webhook_order`
  ADD COLUMN `source` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'notification' COMMENT '来源：notification/sms' AFTER `client_order_id`,
  ADD COLUMN `channel` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'unknown' COMMENT '渠道：wechat/alipay/sms/unknown' AFTER `source`,
  ADD COLUMN `direction` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'unknown' COMMENT '方向：income/expense/refund/unknown' AFTER `package_name`,
  ADD COLUMN `disputed` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否争议' AFTER `direction`,
  ADD COLUMN `disputed_reason` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'none' COMMENT '争议原因' AFTER `disputed`,
  ADD COLUMN `detail` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '统一详情文本（用于存档/AI）' AFTER `time_millis`;

