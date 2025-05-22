-- 租户初始化脚本
-- 用于初始化系统租户数据

-- 清空租户表
DELETE FROM tenant;

-- 添加默认租户
-- 使用MERGE语法代替ON DUPLICATE KEY UPDATE（H2兼容）
MERGE INTO tenant (tenant_code, tenant_name, status, create_time, created_by)
KEY(tenant_code)
VALUES
('default', '默认租户', 1, NOW(), 'system');
