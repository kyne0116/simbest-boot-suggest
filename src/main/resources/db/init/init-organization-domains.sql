-- 初始化组织与职责领域的映射关系
-- 根据docs/组织人员.md文件中的信息创建组织与职责领域的映射关系

-- 清空现有的组织与职责领域的映射关系
DELETE FROM organization_domains WHERE tenant_code = 'default';

-- 添加组织与职责领域的映射
INSERT INTO organization_domains (organization_id, domain_id, domain_weight, tenant_code, create_time, created_by)
VALUES
-- 信息技术部与IT规划领域关联
('dept_it', 'IT_PLANNING', 1.0, 'default', NOW(), 'system'),
-- 信息技术部与数据治理领域关联
('dept_it', 'DATA_GOVERNANCE', 0.9, 'default', NOW(), 'system'),
-- 信息技术部与网络安全领域关联
('dept_it', 'NETWORK_SECURITY', 0.8, 'default', NOW(), 'system'),
-- 政企业务部与政企市场领域关联
('dept_enterprise', 'ENTERPRISE_MARKET', 1.0, 'default', NOW(), 'system'),
-- 计费账务室与计费账务领域关联
('team_billing', 'BILLING', 1.0, 'default', NOW(), 'system'),
-- 人力部与人力资源管理领域关联
('dept_hr', 'HR_MANAGEMENT', 1.0, 'default', NOW(), 'system'),
-- 综合部与综合事务领域关联
('dept_general', 'GENERAL_AFFAIRS', 1.0, 'default', NOW(), 'system'),
-- 财务部与财务管理领域关联
('dept_finance', 'FINANCE_MANAGEMENT', 1.0, 'default', NOW(), 'system'),

-- 系统规划与中台运营室与IT规划领域关联
('team_it_planning', 'IT_PLANNING', 1.0, 'default', NOW(), 'system'),
-- 管理信息系统室与数据治理领域关联
('team_mis', 'DATA_GOVERNANCE', 0.9, 'default', NOW(), 'system'),
-- 信息安全室与网络安全领域关联
('team_security', 'NETWORK_SECURITY', 1.0, 'default', NOW(), 'system'),
-- 政企业务支撑室与政企市场领域关联
('team_enterprise_support', 'ENTERPRISE_MARKET', 0.9, 'default', NOW(), 'system'),
-- 项目管理室与政企市场领域关联
('team_project', 'ENTERPRISE_MARKET', 0.8, 'default', NOW(), 'system'),
-- 渠道管理室与政企市场领域关联
('team_channel', 'ENTERPRISE_MARKET', 0.7, 'default', NOW(), 'system'),
-- 营销管理室与政企市场领域关联
('team_marketing', 'ENTERPRISE_MARKET', 0.7, 'default', NOW(), 'system'),

-- 招聘科与人力资源管理领域关联
('hr_recruitment', 'HR_MANAGEMENT', 0.9, 'default', NOW(), 'system'),
-- 培训科与人力资源管理领域关联
('hr_training', 'HR_MANAGEMENT', 0.9, 'default', NOW(), 'system'),
-- 薪酬福利科与人力资源管理领域关联
('hr_compensation', 'HR_MANAGEMENT', 0.9, 'default', NOW(), 'system'),

-- 行政科与综合事务领域关联
('general_admin', 'GENERAL_AFFAIRS', 0.9, 'default', NOW(), 'system'),
-- 后勤科与综合事务领域关联
('general_logistics', 'GENERAL_AFFAIRS', 0.9, 'default', NOW(), 'system'),
-- 接待科与综合事务领域关联
('general_reception', 'GENERAL_AFFAIRS', 0.9, 'default', NOW(), 'system'),

-- 会计科与财务管理领域关联
('finance_accounting', 'FINANCE_MANAGEMENT', 0.9, 'default', NOW(), 'system'),
-- 预算科与财务管理领域关联
('finance_budget', 'FINANCE_MANAGEMENT', 0.9, 'default', NOW(), 'system'),
-- 税务科与财务管理领域关联
('finance_tax', 'FINANCE_MANAGEMENT', 0.9, 'default', NOW(), 'system');
