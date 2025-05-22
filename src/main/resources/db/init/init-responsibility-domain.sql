-- 初始化职责领域数据
-- 创建于 2023-06-01
-- 更新于 2023-06-15

-- 1. 添加职责领域
INSERT INTO responsibility_domain (domain_id, domain_name, domain_description, tenant_code, create_time, created_by)
VALUES
('IT_PLANNING', 'IT规划', 'IT系统规划与架构设计', 'default', NOW(), 'system'),
('DATA_GOVERNANCE', '数据治理', '数据标准、数据质量与数据管理', 'default', NOW(), 'system'),
('NETWORK_SECURITY', '网络安全', '网络安全与信息安全防护', 'default', NOW(), 'system'),
('ENTERPRISE_MARKET', '政企市场', '政企客户市场开发与维护', 'default', NOW(), 'system'),
('BILLING', '计费账务', '计费系统与账务管理', 'default', NOW(), 'system'),
('HR_MANAGEMENT', '人力资源管理', '负责人力资源相关工作', 'default', NOW(), 'system'),
('GENERAL_AFFAIRS', '综合事务', '负责综合事务相关工作', 'default', NOW(), 'system'),
('FINANCE_MANAGEMENT', '财务管理', '负责财务相关工作', 'default', NOW(), 'system');

-- 2. 添加职责领域关键词
-- IT规划关键词
INSERT INTO responsibility_domain_keywords (domain_id, keyword, keyword_order, keyword_weight)
VALUES
('IT_PLANNING', 'IT', 1, 1.0),
('IT_PLANNING', '规划', 2, 0.9),
('IT_PLANNING', '系统', 3, 0.8),
('IT_PLANNING', '架构', 4, 0.8);

-- 数据治理关键词
INSERT INTO responsibility_domain_keywords (domain_id, keyword, keyword_order, keyword_weight)
VALUES
('DATA_GOVERNANCE', '数据', 1, 1.0),
('DATA_GOVERNANCE', '治理', 2, 0.9),
('DATA_GOVERNANCE', '标准', 3, 0.8),
('DATA_GOVERNANCE', '质量', 4, 0.8);

-- 网络安全关键词
INSERT INTO responsibility_domain_keywords (domain_id, keyword, keyword_order, keyword_weight)
VALUES
('NETWORK_SECURITY', '网络', 1, 1.0),
('NETWORK_SECURITY', '安全', 2, 1.0),
('NETWORK_SECURITY', '防护', 3, 0.8),
('NETWORK_SECURITY', '信息安全', 4, 0.9);

-- 政企市场关键词
INSERT INTO responsibility_domain_keywords (domain_id, keyword, keyword_order, keyword_weight)
VALUES
('ENTERPRISE_MARKET', '政企', 1, 1.0),
('ENTERPRISE_MARKET', '市场', 2, 0.9),
('ENTERPRISE_MARKET', '客户', 3, 0.8);

-- 计费账务关键词
INSERT INTO responsibility_domain_keywords (domain_id, keyword, keyword_order, keyword_weight)
VALUES
('BILLING', '计费', 1, 1.0),
('BILLING', '账务', 2, 1.0);

-- 人力资源管理关键词
INSERT INTO responsibility_domain_keywords (domain_id, keyword, keyword_order, keyword_weight)
VALUES
('HR_MANAGEMENT', '人力资源', 1, 1.0),
('HR_MANAGEMENT', '招聘', 2, 0.9),
('HR_MANAGEMENT', '培训', 3, 0.9),
('HR_MANAGEMENT', '薪酬', 4, 0.8);

-- 综合事务关键词
INSERT INTO responsibility_domain_keywords (domain_id, keyword, keyword_order, keyword_weight)
VALUES
('GENERAL_AFFAIRS', '综合', 1, 1.0),
('GENERAL_AFFAIRS', '行政', 2, 0.9),
('GENERAL_AFFAIRS', '后勤', 3, 0.8);

-- 财务管理关键词
INSERT INTO responsibility_domain_keywords (domain_id, keyword, keyword_order, keyword_weight)
VALUES
('FINANCE_MANAGEMENT', '财务', 1, 1.0),
('FINANCE_MANAGEMENT', '预算', 2, 0.9),
('FINANCE_MANAGEMENT', '会计', 3, 0.8);

-- 3. 添加职责领域与人员关联
-- IT规划领域关联人员
INSERT INTO personnel_responsibility_domain (personnel_id, domain_id, tenant_code, create_time, created_by)
SELECT p.id, 'IT_PLANNING', 'default', NOW(), 'system'
FROM personnel p
WHERE p.account IN ('user004') AND p.tenant_code = 'default';

-- 数据治理领域关联人员
INSERT INTO personnel_responsibility_domain (personnel_id, domain_id, tenant_code, create_time, created_by)
SELECT p.id, 'DATA_GOVERNANCE', 'default', NOW(), 'system'
FROM personnel p
WHERE p.account IN ('user005') AND p.tenant_code = 'default';

-- 网络安全领域关联人员
INSERT INTO personnel_responsibility_domain (personnel_id, domain_id, tenant_code, create_time, created_by)
SELECT p.id, 'NETWORK_SECURITY', 'default', NOW(), 'system'
FROM personnel p
WHERE p.account IN ('user013') AND p.tenant_code = 'default';

-- 政企市场领域关联人员
INSERT INTO personnel_responsibility_domain (personnel_id, domain_id, tenant_code, create_time, created_by)
SELECT p.id, 'ENTERPRISE_MARKET', 'default', NOW(), 'system'
FROM personnel p
WHERE p.account IN ('user019', 'user020') AND p.tenant_code = 'default';

-- 计费账务领域关联人员
INSERT INTO personnel_responsibility_domain (personnel_id, domain_id, tenant_code, create_time, created_by)
SELECT p.id, 'BILLING', 'default', NOW(), 'system'
FROM personnel p
WHERE p.account IN ('user006') AND p.tenant_code = 'default';

-- 人力资源管理领域关联人员
INSERT INTO personnel_responsibility_domain (personnel_id, domain_id, tenant_code, create_time, created_by)
SELECT p.id, 'HR_MANAGEMENT', 'default', NOW(), 'system'
FROM personnel p
WHERE p.account IN ('user025', 'user026') AND p.tenant_code = 'default';

-- 综合事务领域关联人员
INSERT INTO personnel_responsibility_domain (personnel_id, domain_id, tenant_code, create_time, created_by)
SELECT p.id, 'GENERAL_AFFAIRS', 'default', NOW(), 'system'
FROM personnel p
WHERE p.account IN ('user030', 'user031') AND p.tenant_code = 'default';

-- 财务管理领域关联人员
INSERT INTO personnel_responsibility_domain (personnel_id, domain_id, tenant_code, create_time, created_by)
SELECT p.id, 'FINANCE_MANAGEMENT', 'default', NOW(), 'system'
FROM personnel p
WHERE p.account IN ('user035', 'user036') AND p.tenant_code = 'default';

-- 4. 添加职责领域与组织关联
-- 政企市场领域关联组织
INSERT INTO organization_responsibility_domain (organization_id, domain_id, domain_weight, tenant_code, create_time, created_by)
SELECT o.id, 'ENTERPRISE_MARKET', 1.0, 'default', NOW(), 'system'
FROM organization o
WHERE o.org_id = 'dept_enterprise' AND o.tenant_code = 'default';

INSERT INTO organization_responsibility_domain (organization_id, domain_id, domain_weight, tenant_code, create_time, created_by)
SELECT o.id, 'ENTERPRISE_MARKET', 0.9, 'default', NOW(), 'system'
FROM organization o
WHERE o.org_id = 'team_enterprise_support' AND o.tenant_code = 'default';

INSERT INTO organization_responsibility_domain (organization_id, domain_id, domain_weight, tenant_code, create_time, created_by)
SELECT o.id, 'ENTERPRISE_MARKET', 0.8, 'default', NOW(), 'system'
FROM organization o
WHERE o.org_id = 'team_project' AND o.tenant_code = 'default';

INSERT INTO organization_responsibility_domain (organization_id, domain_id, domain_weight, tenant_code, create_time, created_by)
SELECT o.id, 'ENTERPRISE_MARKET', 0.7, 'default', NOW(), 'system'
FROM organization o
WHERE o.org_id = 'team_channel' AND o.tenant_code = 'default';

INSERT INTO organization_responsibility_domain (organization_id, domain_id, domain_weight, tenant_code, create_time, created_by)
SELECT o.id, 'ENTERPRISE_MARKET', 0.7, 'default', NOW(), 'system'
FROM organization o
WHERE o.org_id = 'team_marketing' AND o.tenant_code = 'default';
