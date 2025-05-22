-- 初始化职责领域和领导关系
-- 根据docs/组织人员.md文件中的信息创建职责领域和领导关系

-- 清空现有的职责领域和领导关系
DELETE FROM domain_keywords;
DELETE FROM domain_leader_mapping WHERE tenant_code = 'default';
DELETE FROM leader_domains;
DELETE FROM responsibility_domain WHERE tenant_code = 'default';

-- 1. 添加职责领域
INSERT INTO responsibility_domain (domain_id, domain_name, description, tenant_code, create_time, created_by)
VALUES
('IT_PLANNING', 'IT规划', 'IT系统规划与架构设计', 'default', NOW(), 'system'),
('DATA_GOVERNANCE', '数据治理', '数据标准、数据质量与数据管理', 'default', NOW(), 'system'),
('NETWORK_SECURITY', '网络安全', '网络安全与信息安全防护', 'default', NOW(), 'system'),
('ENTERPRISE_MARKET', '政企市场', '政企客户市场开发与维护', 'default', NOW(), 'system'),
('BILLING', '计费账务', '计费系统与账务管理', 'default', NOW(), 'system'),
('HR_MANAGEMENT', '人力资源管理', '负责人力资源相关工作', 'default', NOW(), 'system'),
('GENERAL_AFFAIRS', '综合事务', '负责综合事务相关工作', 'default', NOW(), 'system'),
('FINANCE_MANAGEMENT', '财务管理', '负责财务相关工作', 'default', NOW(), 'system');

-- 2. 添加领域关键词
-- IT规划关键词
INSERT INTO domain_keywords (responsibility_domain_id, keyword, keyword_order)
SELECT id, 'IT', 1
FROM responsibility_domain
WHERE domain_id = 'IT_PLANNING' AND tenant_code = 'default';

INSERT INTO domain_keywords (responsibility_domain_id, keyword, keyword_order)
SELECT id, '规划', 2
FROM responsibility_domain
WHERE domain_id = 'IT_PLANNING' AND tenant_code = 'default';

INSERT INTO domain_keywords (responsibility_domain_id, keyword, keyword_order)
SELECT id, '架构', 3
FROM responsibility_domain
WHERE domain_id = 'IT_PLANNING' AND tenant_code = 'default';

-- 数据治理关键词
INSERT INTO domain_keywords (responsibility_domain_id, keyword, keyword_order)
SELECT id, '数据', 1
FROM responsibility_domain
WHERE domain_id = 'DATA_GOVERNANCE' AND tenant_code = 'default';

INSERT INTO domain_keywords (responsibility_domain_id, keyword, keyword_order)
SELECT id, '治理', 2
FROM responsibility_domain
WHERE domain_id = 'DATA_GOVERNANCE' AND tenant_code = 'default';

INSERT INTO domain_keywords (responsibility_domain_id, keyword, keyword_order)
SELECT id, '标准', 3
FROM responsibility_domain
WHERE domain_id = 'DATA_GOVERNANCE' AND tenant_code = 'default';

-- 网络安全关键词
INSERT INTO domain_keywords (responsibility_domain_id, keyword, keyword_order)
SELECT id, '网络', 1
FROM responsibility_domain
WHERE domain_id = 'NETWORK_SECURITY' AND tenant_code = 'default';

INSERT INTO domain_keywords (responsibility_domain_id, keyword, keyword_order)
SELECT id, '安全', 2
FROM responsibility_domain
WHERE domain_id = 'NETWORK_SECURITY' AND tenant_code = 'default';

INSERT INTO domain_keywords (responsibility_domain_id, keyword, keyword_order)
SELECT id, '防护', 3
FROM responsibility_domain
WHERE domain_id = 'NETWORK_SECURITY' AND tenant_code = 'default';

-- 政企市场关键词
INSERT INTO domain_keywords (responsibility_domain_id, keyword, keyword_order)
SELECT id, '政企', 1
FROM responsibility_domain
WHERE domain_id = 'ENTERPRISE_MARKET' AND tenant_code = 'default';

INSERT INTO domain_keywords (responsibility_domain_id, keyword, keyword_order)
SELECT id, '市场', 2
FROM responsibility_domain
WHERE domain_id = 'ENTERPRISE_MARKET' AND tenant_code = 'default';

INSERT INTO domain_keywords (responsibility_domain_id, keyword, keyword_order)
SELECT id, '客户', 3
FROM responsibility_domain
WHERE domain_id = 'ENTERPRISE_MARKET' AND tenant_code = 'default';

-- 计费账务关键词
INSERT INTO domain_keywords (responsibility_domain_id, keyword, keyword_order)
SELECT id, '计费', 1
FROM responsibility_domain
WHERE domain_id = 'BILLING' AND tenant_code = 'default';

INSERT INTO domain_keywords (responsibility_domain_id, keyword, keyword_order)
SELECT id, '账务', 2
FROM responsibility_domain
WHERE domain_id = 'BILLING' AND tenant_code = 'default';

-- 人力资源管理关键词
INSERT INTO domain_keywords (responsibility_domain_id, keyword, keyword_order)
SELECT id, '人力资源', 1
FROM responsibility_domain
WHERE domain_id = 'HR_MANAGEMENT' AND tenant_code = 'default';

INSERT INTO domain_keywords (responsibility_domain_id, keyword, keyword_order)
SELECT id, '招聘', 2
FROM responsibility_domain
WHERE domain_id = 'HR_MANAGEMENT' AND tenant_code = 'default';

INSERT INTO domain_keywords (responsibility_domain_id, keyword, keyword_order)
SELECT id, '培训', 3
FROM responsibility_domain
WHERE domain_id = 'HR_MANAGEMENT' AND tenant_code = 'default';

-- 综合事务关键词
INSERT INTO domain_keywords (responsibility_domain_id, keyword, keyword_order)
SELECT id, '综合', 1
FROM responsibility_domain
WHERE domain_id = 'GENERAL_AFFAIRS' AND tenant_code = 'default';

INSERT INTO domain_keywords (responsibility_domain_id, keyword, keyword_order)
SELECT id, '行政', 2
FROM responsibility_domain
WHERE domain_id = 'GENERAL_AFFAIRS' AND tenant_code = 'default';

INSERT INTO domain_keywords (responsibility_domain_id, keyword, keyword_order)
SELECT id, '后勤', 3
FROM responsibility_domain
WHERE domain_id = 'GENERAL_AFFAIRS' AND tenant_code = 'default';

-- 财务管理关键词
INSERT INTO domain_keywords (responsibility_domain_id, keyword, keyword_order)
SELECT id, '财务', 1
FROM responsibility_domain
WHERE domain_id = 'FINANCE_MANAGEMENT' AND tenant_code = 'default';

INSERT INTO domain_keywords (responsibility_domain_id, keyword, keyword_order)
SELECT id, '预算', 2
FROM responsibility_domain
WHERE domain_id = 'FINANCE_MANAGEMENT' AND tenant_code = 'default';

INSERT INTO domain_keywords (responsibility_domain_id, keyword, keyword_order)
SELECT id, '会计', 3
FROM responsibility_domain
WHERE domain_id = 'FINANCE_MANAGEMENT' AND tenant_code = 'default';

-- 3. 添加领导领域关系
-- 信息技术部领导
INSERT INTO leader_domains (leader_id, domain_id, domain_order)
SELECT id, 'IT_PLANNING', 1
FROM leader
WHERE account = 'zxpzong' AND tenant_code = 'default';

INSERT INTO leader_domains (leader_id, domain_id, domain_order)
SELECT id, 'DATA_GOVERNANCE', 1
FROM leader
WHERE account = 'zb1' AND tenant_code = 'default';

INSERT INTO leader_domains (leader_id, domain_id, domain_order)
SELECT id, 'BILLING', 1
FROM leader
WHERE account = 'zhangyaohua1' AND tenant_code = 'default';

-- 政企业务部领导
INSERT INTO leader_domains (leader_id, domain_id, domain_order)
SELECT id, 'ENTERPRISE_MARKET', 1
FROM leader
WHERE account = 'yuzong' AND tenant_code = 'default';

INSERT INTO leader_domains (leader_id, domain_id, domain_order)
SELECT id, 'ENTERPRISE_MARKET', 1
FROM leader
WHERE account = 'zqf' AND tenant_code = 'default';

-- 人力部领导
INSERT INTO leader_domains (leader_id, domain_id, domain_order)
SELECT id, 'HR_MANAGEMENT', 1
FROM leader
WHERE account = 'wangmin' AND tenant_code = 'default';

INSERT INTO leader_domains (leader_id, domain_id, domain_order)
SELECT id, 'HR_MANAGEMENT', 1
FROM leader
WHERE account = 'liuqiang' AND tenant_code = 'default';

-- 综合部领导
INSERT INTO leader_domains (leader_id, domain_id, domain_order)
SELECT id, 'GENERAL_AFFAIRS', 1
FROM leader
WHERE account = 'zhangwei' AND tenant_code = 'default';

INSERT INTO leader_domains (leader_id, domain_id, domain_order)
SELECT id, 'GENERAL_AFFAIRS', 1
FROM leader
WHERE account = 'chenli' AND tenant_code = 'default';

-- 财务部领导
INSERT INTO leader_domains (leader_id, domain_id, domain_order)
SELECT id, 'FINANCE_MANAGEMENT', 1
FROM leader
WHERE account = 'linjing' AND tenant_code = 'default';

INSERT INTO leader_domains (leader_id, domain_id, domain_order)
SELECT id, 'FINANCE_MANAGEMENT', 1
FROM leader
WHERE account = 'zhaojun' AND tenant_code = 'default';

-- 4. 添加领域领导映射
INSERT INTO domain_leader_mapping (domain_name, leader_account, tenant_code, create_time, created_by)
VALUES
('IT规划', 'zxpzong', 'default', NOW(), 'system'),
('数据治理', 'zb1', 'default', NOW(), 'system'),
('网络安全', 'xuhuiyun', 'default', NOW(), 'system'),
('政企市场', 'yuzong', 'default', NOW(), 'system'),
('政企市场', 'zqf', 'default', NOW(), 'system'),
('计费账务', 'zhangyaohua1', 'default', NOW(), 'system'),
('人力资源管理', 'wangmin', 'default', NOW(), 'system'),
('人力资源管理', 'liuqiang', 'default', NOW(), 'system'),
('综合事务', 'zhangwei', 'default', NOW(), 'system'),
('综合事务', 'chenli', 'default', NOW(), 'system'),
('财务管理', 'linjing', 'default', NOW(), 'system'),
('财务管理', 'zhaojun', 'default', NOW(), 'system');
