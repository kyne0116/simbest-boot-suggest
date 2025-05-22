-- 核心数据初始化脚本
-- 合并了原来的01-tenant.sql到07-history.sql的内容
-- 用于一次性初始化系统所有核心数据

-- =====================================================
-- 1. 租户初始化
-- =====================================================
-- 清空租户表
DELETE FROM tenant;

-- 添加默认租户
INSERT INTO tenant (tenant_code, tenant_name, status, create_time, created_by)
VALUES
('default', '默认租户', 1, NOW(), 'system');

-- =====================================================
-- 2. 基础数据初始化
-- =====================================================
-- 清空配置相关表
DELETE FROM config_item;
DELETE FROM config_category;

-- 添加配置类别
INSERT INTO config_category (category_code, category_name, description, tenant_code, create_time, created_by)
VALUES
('system', '系统配置', '系统基本配置参数', 'default', NOW(), 'system'),
('display', '显示配置', '系统显示相关配置参数', 'default', NOW(), 'system'),
('algorithm', '算法权重配置', '文本相似度和关键词匹配算法的权重配置', 'default', NOW(), 'system'),
('threshold', '阈值配置', '系统阈值相关配置', 'default', NOW(), 'system'),
('ai-analysis', 'AI分析配置', 'AI分析相关的配置参数', 'default', NOW(), 'system'),
('recommendation', '推荐配置', '推荐算法相关的配置参数', 'default', NOW(), 'system');

-- 初始化系统配置
INSERT INTO config_item (category_id, item_key, item_value, value_type, description, tenant_code, create_time, created_by)
SELECT
    c.id,
    'name',
    '领导推荐系统',
    'STRING',
    '系统名称',
    'default',
    NOW(),
    'system'
FROM config_category c
WHERE c.category_code = 'system' AND c.tenant_code = 'default';

INSERT INTO config_item (category_id, item_key, item_value, value_type, description, tenant_code, create_time, created_by)
SELECT
    c.id,
    'version',
    '1.0.0',
    'STRING',
    '系统版本',
    'default',
    NOW(),
    'system'
FROM config_category c
WHERE c.category_code = 'system' AND c.tenant_code = 'default';

INSERT INTO config_item (category_id, item_key, item_value, value_type, description, tenant_code, create_time, created_by)
SELECT
    c.id,
    'domainIdPrefix',
    'domain_',
    'STRING',
    '领域ID前缀',
    'default',
    NOW(),
    'system'
FROM config_category c
WHERE c.category_code = 'system' AND c.tenant_code = 'default';

-- 初始化显示配置
INSERT INTO config_item (category_id, item_key, item_value, value_type, description, tenant_code, create_time, created_by)
SELECT
    c.id,
    'showConfidence',
    'true',
    'BOOLEAN',
    '是否显示置信度',
    'default',
    NOW(),
    'system'
FROM config_category c
WHERE c.category_code = 'display' AND c.tenant_code = 'default';

-- 初始化算法权重配置
INSERT INTO config_item (category_id, item_key, item_value, value_type, description, tenant_code, create_time, created_by)
SELECT
    c.id,
    'textSimilarity',
    '{"characterLevel":{"jaccardWeight":0.4,"cosineWeight":0.3,"levenshteinWeight":0.3},"tokenLevel":{"jaccardWeight":0.4,"cosineWeight":0.4,"levenshteinWeight":0.2},"finalCombination":{"characterLevelWeight":0.4,"tokenLevelWeight":0.6}}',
    'JSON',
    '文本相似度算法权重配置',
    'default',
    NOW(),
    'system'
FROM config_category c
WHERE c.category_code = 'algorithm' AND c.tenant_code = 'default';

-- 初始化阈值配置
INSERT INTO config_item (category_id, item_key, item_value, value_type, description, tenant_code, create_time, created_by)
SELECT
    c.id,
    'baseThreshold',
    '0.01',
    'NUMBER',
    '基础阈值',
    'default',
    NOW(),
    'system'
FROM config_category c
WHERE c.category_code = 'threshold' AND c.tenant_code = 'default';

-- 初始化AI分析配置
INSERT INTO config_item (category_id, item_key, item_value, value_type, description, tenant_code, create_time, created_by)
SELECT
    c.id,
    'confidence',
    '{"highConfidence":0.8,"mediumConfidence":0.6,"lowConfidence":0.4,"minimumConfidence":0.2}',
    'JSON',
    '置信度配置',
    'default',
    NOW(),
    'system'
FROM config_category c
WHERE c.category_code = 'ai-analysis' AND c.tenant_code = 'default';

-- 初始化推荐配置
INSERT INTO config_item (category_id, item_key, item_value, value_type, description, tenant_code, create_time, created_by)
SELECT
    c.id,
    'organizationMatch',
    '{"mainLeaderToDeputyScore":0.9,"deputyLeaderToMainScore":0.8,"peerLeaderScore":0.7,"defaultScore":0.5}',
    'JSON',
    '组织匹配配置',
    'default',
    NOW(),
    'system'
FROM config_category c
WHERE c.category_code = 'recommendation' AND c.tenant_code = 'default';

-- =====================================================
-- 3. 组织结构初始化
-- =====================================================
-- 清空现有的组织和人员关系
DELETE FROM organization_deputy_leaders;
DELETE FROM organization WHERE tenant_code = 'default';
DELETE FROM leader WHERE tenant_code = 'default';

-- 添加所有领导信息
-- 公司总部
INSERT INTO leader (account, name, tenant_code, create_time, created_by) VALUES ('ceo001', '总经理', 'default', NOW(), 'system');
-- 管理层
INSERT INTO leader (account, name, tenant_code, create_time, created_by) VALUES ('user001', '用户一', 'default', NOW(), 'system');
INSERT INTO leader (account, name, tenant_code, create_time, created_by) VALUES ('user002', '用户二', 'default', NOW(), 'system');
INSERT INTO leader (account, name, tenant_code, create_time, created_by) VALUES ('user003', '用户三', 'default', NOW(), 'system');
-- 信息技术部
INSERT INTO leader (account, name, tenant_code, create_time, created_by) VALUES ('user004', '用户四', 'default', NOW(), 'system');
INSERT INTO leader (account, name, tenant_code, create_time, created_by) VALUES ('user005', '用户五', 'default', NOW(), 'system');
INSERT INTO leader (account, name, tenant_code, create_time, created_by) VALUES ('user006', '用户六', 'default', NOW(), 'system');
-- 政企业务部
INSERT INTO leader (account, name, tenant_code, create_time, created_by) VALUES ('user019', '用户十九', 'default', NOW(), 'system');
INSERT INTO leader (account, name, tenant_code, create_time, created_by) VALUES ('user020', '用户二十', 'default', NOW(), 'system');
INSERT INTO leader (account, name, tenant_code, create_time, created_by) VALUES ('user021', '用户二十一', 'default', NOW(), 'system');

-- 添加组织结构
-- 添加公司总部
INSERT INTO organization (org_id, org_name, parent_org_id, main_leader_account, tenant_code, org_type, create_time, created_by)
VALUES ('company', '公司总部', NULL, 'ceo001', 'default', 'COMPANY', NOW(), 'system');

-- 添加信息技术部
INSERT INTO organization (org_id, org_name, parent_org_id, main_leader_account, tenant_code, org_type, create_time, created_by)
VALUES ('dept_it', '信息技术部', 'company', 'user004', 'default', 'DEPARTMENT', NOW(), 'system');

-- 添加信息技术部副领导
INSERT INTO organization_deputy_leaders (organization_id, deputy_leader_account, deputy_leader_order)
VALUES
((SELECT id FROM organization WHERE org_id = 'dept_it' AND tenant_code = 'default'), 'user005', 1),
((SELECT id FROM organization WHERE org_id = 'dept_it' AND tenant_code = 'default'), 'user006', 2);

-- 添加政企业务部
INSERT INTO organization (org_id, org_name, parent_org_id, main_leader_account, tenant_code, org_type, create_time, created_by)
VALUES ('dept_enterprise', '政企业务部', 'company', 'user019', 'default', 'DEPARTMENT', NOW(), 'system');

-- 添加政企业务部副领导
INSERT INTO organization_deputy_leaders (organization_id, deputy_leader_account, deputy_leader_order)
VALUES
((SELECT id FROM organization WHERE org_id = 'dept_enterprise' AND tenant_code = 'default'), 'user020', 1),
((SELECT id FROM organization WHERE org_id = 'dept_enterprise' AND tenant_code = 'default'), 'user021', 2);

-- =====================================================
-- 4. 组织关键词初始化
-- =====================================================
-- 清空组织关键词表
DELETE FROM organization_keywords;

-- 为信息技术部添加关键字
INSERT INTO organization_keywords (organization_id, keyword, keyword_order, keyword_weight)
SELECT id, '信息', 1, 1.0
FROM organization
WHERE org_id = 'dept_it' AND tenant_code = 'default';

INSERT INTO organization_keywords (organization_id, keyword, keyword_order, keyword_weight)
SELECT id, '技术', 2, 0.9
FROM organization
WHERE org_id = 'dept_it' AND tenant_code = 'default';

-- 为政企业务部添加关键字
INSERT INTO organization_keywords (organization_id, keyword, keyword_order, keyword_weight)
SELECT id, '政企', 1, 1.0
FROM organization
WHERE org_id = 'dept_enterprise' AND tenant_code = 'default';

INSERT INTO organization_keywords (organization_id, keyword, keyword_order, keyword_weight)
SELECT id, '业务', 2, 0.9
FROM organization
WHERE org_id = 'dept_enterprise' AND tenant_code = 'default';

-- =====================================================
-- 5. 责任领域初始化
-- =====================================================
-- 清空责任领域相关表
DELETE FROM domain_keywords;
DELETE FROM domain_leader_mapping WHERE tenant_code = 'default';
DELETE FROM leader_domains;
DELETE FROM responsibility_domain WHERE tenant_code = 'default';

-- 添加责任领域
INSERT INTO responsibility_domain (domain_id, domain_name, description, tenant_code, create_time, created_by)
VALUES
('IT_PLANNING', 'IT规划', 'IT系统规划与架构设计', 'default', NOW(), 'system'),
('DATA_GOVERNANCE', '数据治理', '数据标准、数据质量与数据管理', 'default', NOW(), 'system'),
('NETWORK_SECURITY', '网络安全', '网络安全与信息安全防护', 'default', NOW(), 'system'),
('ENTERPRISE_MARKET', '政企市场', '政企客户市场开发与维护', 'default', NOW(), 'system'),
('BILLING', '计费账务', '计费系统与账务管理', 'default', NOW(), 'system');

-- 添加领导领域关系
INSERT INTO leader_domains (leader_id, domain_id, domain_order)
VALUES
((SELECT id FROM leader WHERE account = 'user004' AND tenant_code = 'default'), 'IT_PLANNING', 1),
((SELECT id FROM leader WHERE account = 'user005' AND tenant_code = 'default'), 'DATA_GOVERNANCE', 1),
((SELECT id FROM leader WHERE account = 'user019' AND tenant_code = 'default'), 'ENTERPRISE_MARKET', 1),
((SELECT id FROM leader WHERE account = 'user020' AND tenant_code = 'default'), 'ENTERPRISE_MARKET', 2);

-- 添加领域领导映射
INSERT INTO domain_leader_mapping (domain_name, leader_account, tenant_code, create_time, created_by)
VALUES
('IT规划', 'user004', 'default', NOW(), 'system'),
('数据治理', 'user005', 'default', NOW(), 'system'),
('政企市场', 'user019', 'default', NOW(), 'system'),
('政企市场', 'user020', 'default', NOW(), 'system');

-- =====================================================
-- 6. 同义词和常用词初始化
-- =====================================================
-- 清空同义词和常用词表
DELETE FROM synonym_group WHERE tenant_code = 'default';
DELETE FROM common_word WHERE tenant_code = 'default';

-- 添加常用词
INSERT INTO common_word (word, category, tenant_code, create_time, created_by)
VALUES
('政企', '业务领域', 'default', NOW(), 'system'),
('客户', '业务领域', 'default', NOW(), 'system'),
('大客户', '业务领域', 'default', NOW(), 'system'),
('市场', '业务领域', 'default', NOW(), 'system'),
('数据', '业务领域', 'default', NOW(), 'system'),
('治理', '业务领域', 'default', NOW(), 'system'),
('系统', '业务领域', 'default', NOW(), 'system'),
('规划', '业务领域', 'default', NOW(), 'system'),
('网络', '业务领域', 'default', NOW(), 'system'),
('安全', '业务领域', 'default', NOW(), 'system');

-- 添加同义词组
INSERT INTO synonym_group (category, synonym_words, tenant_code, create_time, created_by)
VALUES
('group_enterprise', '政企,政府企业,政企客户', 'default', NOW(), 'system'),
('group_customer', '客户,用户,客户群', 'default', NOW(), 'system'),
('group_market', '市场,营销,销售', 'default', NOW(), 'system'),
('group_data', '数据,信息,资料', 'default', NOW(), 'system'),
('group_governance', '治理,管理,规范', 'default', NOW(), 'system');

-- =====================================================
-- 7. 历史批复记录初始化
-- =====================================================
-- 清空历史批复记录表
DELETE FROM approval_history WHERE tenant_code = 'default';

-- 添加历史批复记录
INSERT INTO approval_history (tenant_code, task_id, task_title, initiator_account, initiator_org_id, approver_account, approver_name, workflow_direction, approval_time, approval_result, is_recommended, create_time, created_by)
VALUES
-- 政企市场相关历史记录
('default', 'task001', '关于迎接集团公司政企大客户行为规范培训的通知', 'user019', 'team002', 'user020', '用户二十', 'DOWNWARD', DATEADD('DAY', -30, NOW()), '同意', false, NOW(), 'system'),
('default', 'task002', '关于政企客户行为规范培训的通知', 'user004', 'dept001', 'user005', '用户五', 'DOWNWARD', DATEADD('DAY', -25, NOW()), '同意', false, NOW(), 'system'),
('default', 'task003', '政企大客户服务方案', 'user019', 'team002', 'user020', '用户二十', 'DOWNWARD', DATEADD('DAY', -20, NOW()), '同意', false, NOW(), 'system');

-- 添加任务模式数据
DELETE FROM task_pattern_approver_weights;
DELETE FROM task_pattern WHERE tenant_code = 'default';

-- 添加任务模式
INSERT INTO task_pattern (tenant_code, pattern_name, workflow_direction, last_update_time, match_count, create_time, created_by)
VALUES
('default', '政企市场模式', 'DOWNWARD', NOW(), 5, NOW(), 'system'),
('default', '数据治理模式', 'UPWARD', NOW(), 3, NOW(), 'system');

-- 添加任务模式关键词
DELETE FROM task_pattern_keywords;

-- 政企市场模式关键词
INSERT INTO task_pattern_keywords (task_pattern_id, keyword, keyword_order)
SELECT id, '政企', 0 FROM task_pattern WHERE tenant_code = 'default' AND pattern_name = '政企市场模式';

INSERT INTO task_pattern_keywords (task_pattern_id, keyword, keyword_order)
SELECT id, '市场', 1 FROM task_pattern WHERE tenant_code = 'default' AND pattern_name = '政企市场模式';

INSERT INTO task_pattern_keywords (task_pattern_id, keyword, keyword_order)
SELECT id, '客户', 2 FROM task_pattern WHERE tenant_code = 'default' AND pattern_name = '政企市场模式';

-- 数据治理模式关键词
INSERT INTO task_pattern_keywords (task_pattern_id, keyword, keyword_order)
SELECT id, '数据', 0 FROM task_pattern WHERE tenant_code = 'default' AND pattern_name = '数据治理模式';

INSERT INTO task_pattern_keywords (task_pattern_id, keyword, keyword_order)
SELECT id, '治理', 1 FROM task_pattern WHERE tenant_code = 'default' AND pattern_name = '数据治理模式';

INSERT INTO task_pattern_keywords (task_pattern_id, keyword, keyword_order)
SELECT id, '标准', 2 FROM task_pattern WHERE tenant_code = 'default' AND pattern_name = '数据治理模式';

-- 添加审批人权重
INSERT INTO task_pattern_approver_weights (task_pattern_id, approver_account, weight)
SELECT id, 'user020', 0.6 FROM task_pattern WHERE tenant_code = 'default' AND pattern_name = '政企市场模式';

INSERT INTO task_pattern_approver_weights (task_pattern_id, approver_account, weight)
SELECT id, 'user005', 0.4 FROM task_pattern WHERE tenant_code = 'default' AND pattern_name = '政企市场模式';

INSERT INTO task_pattern_approver_weights (task_pattern_id, approver_account, weight)
SELECT id, 'user005', 0.4 FROM task_pattern WHERE tenant_code = 'default' AND pattern_name = '数据治理模式';

INSERT INTO task_pattern_approver_weights (task_pattern_id, approver_account, weight)
SELECT id, 'user020', 0.6 FROM task_pattern WHERE tenant_code = 'default' AND pattern_name = '数据治理模式';
