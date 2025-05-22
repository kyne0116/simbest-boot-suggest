-- 初始化组织人员数据脚本
-- 根据docs/组织人员.md文件中的信息创建组织和人员数据

-- 清空现有的组织和人员关系
DELETE FROM organization_deputy_leaders;
DELETE FROM organization_keywords;
DELETE FROM domain_keywords;
DELETE FROM domain_leader_mapping WHERE tenant_code = 'default';
DELETE FROM leader_domains;
DELETE FROM organization WHERE tenant_code = 'default';
DELETE FROM leader WHERE tenant_code = 'default';
DELETE FROM responsibility_domain WHERE tenant_code = 'default';

-- 1. 添加所有领导信息
-- 公司总部
INSERT INTO leader (account, name, tenant_code, create_time, created_by)
VALUES ('ceo001', '总经理', 'default', NOW(), 'system');

-- 管理层
INSERT INTO leader (account, name, tenant_code, create_time, created_by)
VALUES
('user001', '用户一', 'default', NOW(), 'system'),
('user002', '用户二', 'default', NOW(), 'system'),
('user003', '用户三', 'default', NOW(), 'system');

-- 信息技术部
INSERT INTO leader (account, name, tenant_code, create_time, created_by)
VALUES
('user004', '用户四', 'default', NOW(), 'system'),
('user005', '用户五', 'default', NOW(), 'system'),
('user006', '用户六', 'default', NOW(), 'system');

-- 信息技术部下属团队领导
INSERT INTO leader (account, name, tenant_code, create_time, created_by)
VALUES
('user007', '用户七', 'default', NOW(), 'system'),
('user008', '用户八', 'default', NOW(), 'system'),
('user009', '用户九', 'default', NOW(), 'system'),
('user010', '用户十', 'default', NOW(), 'system'),
('user011', '用户十一', 'default', NOW(), 'system'),
('user012', '用户十二', 'default', NOW(), 'system'),
('user013', '用户十三', 'default', NOW(), 'system');

-- 信息技术部员工
INSERT INTO leader (account, name, tenant_code, create_time, created_by)
VALUES
('user014', '用户十四', 'default', NOW(), 'system'),
('user015', '用户十五', 'default', NOW(), 'system'),
('user016', '用户十六', 'default', NOW(), 'system'),
('user017', '用户十七', 'default', NOW(), 'system'),
('user018', '用户十八', 'default', NOW(), 'system');

-- 政企业务部
INSERT INTO leader (account, name, tenant_code, create_time, created_by)
VALUES
('user019', '用户十九', 'default', NOW(), 'system'),
('user020', '用户二十', 'default', NOW(), 'system'),
('user021', '用户二十一', 'default', NOW(), 'system');

-- 政企业务部下属团队领导
INSERT INTO leader (account, name, tenant_code, create_time, created_by)
VALUES
('user022', '用户二十二', 'default', NOW(), 'system'),
('user023', '用户二十三', 'default', NOW(), 'system'),
('user024', '用户二十四', 'default', NOW(), 'system');

-- 人力部
INSERT INTO leader (account, name, tenant_code, create_time, created_by)
VALUES
('user025', '用户二十五', 'default', NOW(), 'system'),
('user026', '用户二十六', 'default', NOW(), 'system'),
('user027', '用户二十七', 'default', NOW(), 'system'),
('user028', '用户二十八', 'default', NOW(), 'system'),
('user029', '用户二十九', 'default', NOW(), 'system');

-- 综合部
INSERT INTO leader (account, name, tenant_code, create_time, created_by)
VALUES
('user030', '用户三十', 'default', NOW(), 'system'),
('user031', '用户三十一', 'default', NOW(), 'system'),
('user032', '用户三十二', 'default', NOW(), 'system'),
('user033', '用户三十三', 'default', NOW(), 'system'),
('user034', '用户三十四', 'default', NOW(), 'system');

-- 财务部
INSERT INTO leader (account, name, tenant_code, create_time, created_by)
VALUES
('user035', '用户三十五', 'default', NOW(), 'system'),
('user036', '用户三十六', 'default', NOW(), 'system'),
('user037', '用户三十七', 'default', NOW(), 'system'),
('user038', '用户三十八', 'default', NOW(), 'system'),
('user039', '用户三十九', 'default', NOW(), 'system');

-- 2. 添加组织结构
-- 添加公司总部
INSERT INTO organization (org_id, org_name, parent_org_id, main_leader_account, tenant_code, org_type, create_time, created_by)
VALUES ('company', '公司总部', NULL, 'ceo001', 'default', 'COMPANY', NOW(), 'system');

-- 添加管理层
INSERT INTO organization (org_id, org_name, parent_org_id, main_leader_account, tenant_code, org_type, create_time, created_by)
VALUES ('management', '管理层', 'company', 'user001', 'default', 'DEPARTMENT', NOW(), 'system');

-- 添加管理层副领导
INSERT INTO organization_deputy_leaders (organization_id, deputy_leader_account, deputy_leader_order)
VALUES
((SELECT id FROM organization WHERE org_id = 'management' AND tenant_code = 'default'), 'user002', 1),
((SELECT id FROM organization WHERE org_id = 'management' AND tenant_code = 'default'), 'user003', 2);

-- 添加信息技术部
INSERT INTO organization (org_id, org_name, parent_org_id, main_leader_account, superior_leader_account, tenant_code, org_type, create_time, created_by)
VALUES ('dept_it', '信息技术部', 'company', 'user004', 'user003', 'default', 'DEPARTMENT', NOW(), 'system');

-- 添加信息技术部副领导
INSERT INTO organization_deputy_leaders (organization_id, deputy_leader_account, deputy_leader_order)
VALUES
((SELECT id FROM organization WHERE org_id = 'dept_it' AND tenant_code = 'default'), 'user005', 1),
((SELECT id FROM organization WHERE org_id = 'dept_it' AND tenant_code = 'default'), 'user006', 2);

-- 添加信息技术部下属团队
INSERT INTO organization (org_id, org_name, parent_org_id, main_leader_account, superior_leader_account, tenant_code, org_type, create_time, created_by)
VALUES
('team_operation', '运营管理室', 'dept_it', 'user007', 'user005', 'default', 'OFFICE', NOW(), 'system'),
('team_it_planning', '系统规划与中台运营室', 'dept_it', 'user008', 'user006', 'default', 'OFFICE', NOW(), 'system'),
('team_mis', '管理信息系统室', 'dept_it', 'user009', 'user005', 'default', 'OFFICE', NOW(), 'system'),
('team_enterprise_support', '政企业务支撑室', 'dept_it', 'user010', 'user005', 'default', 'OFFICE', NOW(), 'system'),
('team_billing', '计费账务室', 'dept_it', 'user011', 'user006', 'default', 'OFFICE', NOW(), 'system'),
('team_platform', '软件平台维护室', 'dept_it', 'user012', 'user006', 'default', 'OFFICE', NOW(), 'system'),
('team_security', '信息安全室', 'dept_it', 'user013', null, 'default', 'OFFICE', NOW(), 'system');

-- 添加政企业务部
INSERT INTO organization (org_id, org_name, parent_org_id, main_leader_account, superior_leader_account, tenant_code, org_type, create_time, created_by)
VALUES ('dept_enterprise', '政企业务部', 'company', 'user019', 'user003', 'default', 'DEPARTMENT', NOW(), 'system');

-- 添加政企业务部副领导
INSERT INTO organization_deputy_leaders (organization_id, deputy_leader_account, deputy_leader_order)
VALUES
((SELECT id FROM organization WHERE org_id = 'dept_enterprise' AND tenant_code = 'default'), 'user020', 1),
((SELECT id FROM organization WHERE org_id = 'dept_enterprise' AND tenant_code = 'default'), 'user021', 2);

-- 添加政企业务部下属团队
INSERT INTO organization (org_id, org_name, parent_org_id, main_leader_account, superior_leader_account, tenant_code, org_type, create_time, created_by)
VALUES
('team_project', '项目管理室', 'dept_enterprise', 'user022', 'user021', 'default', 'OFFICE', NOW(), 'system'),
('team_channel', '渠道管理室', 'dept_enterprise', 'user023', 'user020', 'default', 'OFFICE', NOW(), 'system'),
('team_marketing', '营销管理室', 'dept_enterprise', 'user024', 'user020', 'default', 'OFFICE', NOW(), 'system');

-- 添加人力部
INSERT INTO organization (org_id, org_name, parent_org_id, main_leader_account, superior_leader_account, tenant_code, org_type, create_time, created_by)
VALUES ('dept_hr', '人力部', 'company', 'user025', 'user001', 'default', 'DEPARTMENT', NOW(), 'system');

-- 添加人力部副领导
INSERT INTO organization_deputy_leaders (organization_id, deputy_leader_account, deputy_leader_order)
VALUES
((SELECT id FROM organization WHERE org_id = 'dept_hr' AND tenant_code = 'default'), 'user026', 1);

-- 添加人力部下属团队
INSERT INTO organization (org_id, org_name, parent_org_id, main_leader_account, superior_leader_account, tenant_code, org_type, create_time, created_by)
VALUES
('hr_recruitment', '招聘科', 'dept_hr', 'user027', 'user026', 'default', 'OFFICE', NOW(), 'system'),
('hr_training', '培训科', 'dept_hr', 'user028', 'user026', 'default', 'OFFICE', NOW(), 'system'),
('hr_compensation', '薪酬福利科', 'dept_hr', 'user029', 'user025', 'default', 'OFFICE', NOW(), 'system');

-- 添加综合部
INSERT INTO organization (org_id, org_name, parent_org_id, main_leader_account, superior_leader_account, tenant_code, org_type, create_time, created_by)
VALUES ('dept_general', '综合部', 'company', 'user030', 'user002', 'default', 'DEPARTMENT', NOW(), 'system');

-- 添加综合部副领导
INSERT INTO organization_deputy_leaders (organization_id, deputy_leader_account, deputy_leader_order)
VALUES
((SELECT id FROM organization WHERE org_id = 'dept_general' AND tenant_code = 'default'), 'user031', 1);

-- 添加综合部下属团队
INSERT INTO organization (org_id, org_name, parent_org_id, main_leader_account, superior_leader_account, tenant_code, org_type, create_time, created_by)
VALUES
('general_admin', '行政科', 'dept_general', 'user032', 'user031', 'default', 'OFFICE', NOW(), 'system'),
('general_logistics', '后勤科', 'dept_general', 'user033', 'user031', 'default', 'OFFICE', NOW(), 'system'),
('general_reception', '接待科', 'dept_general', 'user034', 'user031', 'default', 'OFFICE', NOW(), 'system');

-- 添加财务部
INSERT INTO organization (org_id, org_name, parent_org_id, main_leader_account, superior_leader_account, tenant_code, org_type, create_time, created_by)
VALUES ('dept_finance', '财务部', 'company', 'user035', 'user002', 'default', 'DEPARTMENT', NOW(), 'system');

-- 添加财务部副领导
INSERT INTO organization_deputy_leaders (organization_id, deputy_leader_account, deputy_leader_order)
VALUES
((SELECT id FROM organization WHERE org_id = 'dept_finance' AND tenant_code = 'default'), 'user036', 1);

-- 添加财务部下属团队
INSERT INTO organization (org_id, org_name, parent_org_id, main_leader_account, superior_leader_account, tenant_code, org_type, create_time, created_by)
VALUES
('finance_accounting', '会计科', 'dept_finance', 'user037', 'user035', 'default', 'OFFICE', NOW(), 'system'),
('finance_budget', '预算科', 'dept_finance', 'user038', 'user036', 'default', 'OFFICE', NOW(), 'system'),
('finance_tax', '税务科', 'dept_finance', 'user039', 'user036', 'default', 'OFFICE', NOW(), 'system');

-- 3. 添加组织关键词
-- 信息技术部关键词
INSERT INTO organization_keywords (organization_id, keyword, keyword_order, keyword_weight)
SELECT id, '信息', 1, 1.0 FROM organization WHERE org_id = 'dept_it' AND tenant_code = 'default';

INSERT INTO organization_keywords (organization_id, keyword, keyword_order, keyword_weight)
SELECT id, '技术', 2, 0.9 FROM organization WHERE org_id = 'dept_it' AND tenant_code = 'default';

-- 政企业务部关键词
INSERT INTO organization_keywords (organization_id, keyword, keyword_order, keyword_weight)
SELECT id, '政企', 1, 1.0 FROM organization WHERE org_id = 'dept_enterprise' AND tenant_code = 'default';

INSERT INTO organization_keywords (organization_id, keyword, keyword_order, keyword_weight)
SELECT id, '业务', 2, 0.9 FROM organization WHERE org_id = 'dept_enterprise' AND tenant_code = 'default';

-- 政企业务支撑室关键词
INSERT INTO organization_keywords (organization_id, keyword, keyword_order, keyword_weight)
SELECT id, '政企', 1, 1.0 FROM organization WHERE org_id = 'team_enterprise_support' AND tenant_code = 'default';

INSERT INTO organization_keywords (organization_id, keyword, keyword_order, keyword_weight)
SELECT id, '支撑', 2, 0.9 FROM organization WHERE org_id = 'team_enterprise_support' AND tenant_code = 'default';

-- 渠道管理室关键词
INSERT INTO organization_keywords (organization_id, keyword, keyword_order, keyword_weight)
SELECT id, '渠道', 1, 1.0 FROM organization WHERE org_id = 'team_channel' AND tenant_code = 'default';

INSERT INTO organization_keywords (organization_id, keyword, keyword_order, keyword_weight)
SELECT id, '客户', 2, 0.9 FROM organization WHERE org_id = 'team_channel' AND tenant_code = 'default';

INSERT INTO organization_keywords (organization_id, keyword, keyword_order, keyword_weight)
SELECT id, '服务', 3, 0.8 FROM organization WHERE org_id = 'team_channel' AND tenant_code = 'default';

-- 人力部关键词
INSERT INTO organization_keywords (organization_id, keyword, keyword_order, keyword_weight)
SELECT id, '人力', 1, 1.0 FROM organization WHERE org_id = 'dept_hr' AND tenant_code = 'default';

INSERT INTO organization_keywords (organization_id, keyword, keyword_order, keyword_weight)
SELECT id, '招聘', 2, 0.9 FROM organization WHERE org_id = 'dept_hr' AND tenant_code = 'default';

INSERT INTO organization_keywords (organization_id, keyword, keyword_order, keyword_weight)
SELECT id, '培训', 3, 0.8 FROM organization WHERE org_id = 'dept_hr' AND tenant_code = 'default';

-- 综合部关键词
INSERT INTO organization_keywords (organization_id, keyword, keyword_order, keyword_weight)
SELECT id, '综合', 1, 1.0 FROM organization WHERE org_id = 'dept_general' AND tenant_code = 'default';

INSERT INTO organization_keywords (organization_id, keyword, keyword_order, keyword_weight)
SELECT id, '行政', 2, 0.9 FROM organization WHERE org_id = 'dept_general' AND tenant_code = 'default';

-- 财务部关键词
INSERT INTO organization_keywords (organization_id, keyword, keyword_order, keyword_weight)
SELECT id, '财务', 1, 1.0 FROM organization WHERE org_id = 'dept_finance' AND tenant_code = 'default';

INSERT INTO organization_keywords (organization_id, keyword, keyword_order, keyword_weight)
SELECT id, '预算', 2, 0.9 FROM organization WHERE org_id = 'dept_finance' AND tenant_code = 'default';

INSERT INTO organization_keywords (organization_id, keyword, keyword_order, keyword_weight)
SELECT id, '会计', 3, 0.8 FROM organization WHERE org_id = 'dept_finance' AND tenant_code = 'default';
