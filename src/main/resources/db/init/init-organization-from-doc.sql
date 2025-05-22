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
('shoujianguo', '首建国', 'default', NOW(), 'system'),
('yuqian', '余谦', 'default', NOW(), 'system'),
('wangyong1', '汪勇', 'default', NOW(), 'system');

-- 信息技术部
INSERT INTO leader (account, name, tenant_code, create_time, created_by)
VALUES
('zhangxinpeng', '张新鹏', 'default', NOW(), 'system'),
('zhaobin1', '赵斌', 'default', NOW(), 'system'),
('zhangyaohua1', '张耀华', 'default', NOW(), 'system');

-- 信息技术部下属团队领导
INSERT INTO leader (account, name, tenant_code, create_time, created_by)
VALUES
('yanbingan', '鄢兵安', 'default', NOW(), 'system'),
('wangjingui', '王金贵', 'default', NOW(), 'system'),
('chenhaiwei', '陈海伟', 'default', NOW(), 'system'),
('huangfushuming', '皇甫淑铭', 'default', NOW(), 'system'),
('wangshouchu', '王守初', 'default', NOW(), 'system'),
('gaojian', '高坚', 'default', NOW(), 'system'),
('xuhuiyun', '许慧云', 'default', NOW(), 'system');

-- 信息技术部员工
INSERT INTO leader (account, name, tenant_code, create_time, created_by)
VALUES
('chaiyuanchen', '柴塬琛', 'default', NOW(), 'system'),
('zhengxiaolei', '郑晓磊', 'default', NOW(), 'system'),
('zhaoxiang', '赵翔', 'default', NOW(), 'system'),
('wangao', '王奥', 'default', NOW(), 'system'),
('hanhailing', '韩海玲', 'default', NOW(), 'system');

-- 政企业务部
INSERT INTO leader (account, name, tenant_code, create_time, created_by)
VALUES
('baiyu', '白玉', 'default', NOW(), 'system'),
('zhangyifei', '张翼飞', 'default', NOW(), 'system'),
('zhaowanli', '赵万里', 'default', NOW(), 'system');

-- 政企业务部下属团队领导
INSERT INTO leader (account, name, tenant_code, create_time, created_by)
VALUES
('duanmufanzhi', '端木繁志', 'default', NOW(), 'system'),
('lixuanang', '李轩昂', 'default', NOW(), 'system'),
('xiongchangshun', '熊常顺', 'default', NOW(), 'system');

-- 人力部
INSERT INTO leader (account, name, tenant_code, create_time, created_by)
VALUES
('wangmin', '王敏', 'default', NOW(), 'system'),
('liuqiang', '刘强', 'default', NOW(), 'system'),
('sunyan', '孙燕', 'default', NOW(), 'system'),
('wulei', '吴磊', 'default', NOW(), 'system'),
('gaofeng', '高峰', 'default', NOW(), 'system');

-- 综合部
INSERT INTO leader (account, name, tenant_code, create_time, created_by)
VALUES
('zhangwei', '张伟', 'default', NOW(), 'system'),
('chenli', '陈丽', 'default', NOW(), 'system'),
('huangxiaoming', '黄晓明', 'default', NOW(), 'system'),
('zhoujie', '周杰', 'default', NOW(), 'system'),
('tangyue', '唐悦', 'default', NOW(), 'system');

-- 财务部
INSERT INTO leader (account, name, tenant_code, create_time, created_by)
VALUES
('linjing', '林静', 'default', NOW(), 'system'),
('zhaojun', '赵军', 'default', NOW(), 'system'),
('xuwei', '徐伟', 'default', NOW(), 'system'),
('yangmei', '杨梅', 'default', NOW(), 'system'),
('fenglin', '冯林', 'default', NOW(), 'system');

-- 2. 添加组织结构
-- 添加公司总部
INSERT INTO organization (org_id, org_name, parent_org_id, main_leader_account, tenant_code, org_type, create_time, created_by)
VALUES ('company', '公司总部', NULL, 'ceo001', 'default', 'COMPANY', NOW(), 'system');

-- 添加管理层
INSERT INTO organization (org_id, org_name, parent_org_id, main_leader_account, tenant_code, org_type, create_time, created_by)
VALUES ('management', '管理层', 'company', 'shoujianguo', 'default', 'DEPARTMENT', NOW(), 'system');

-- 添加管理层副领导
INSERT INTO organization_deputy_leaders (organization_id, deputy_leader_account, deputy_leader_order)
VALUES
((SELECT id FROM organization WHERE org_id = 'management' AND tenant_code = 'default'), 'yuqian', 1),
((SELECT id FROM organization WHERE org_id = 'management' AND tenant_code = 'default'), 'wangyong1', 2);

-- 添加信息技术部
INSERT INTO organization (org_id, org_name, parent_org_id, main_leader_account, superior_leader_account, tenant_code, org_type, create_time, created_by)
VALUES ('dept_it', '信息技术部', 'company', 'zhangxinpeng', 'wangyong1', 'default', 'DEPARTMENT', NOW(), 'system');

-- 添加信息技术部副领导
INSERT INTO organization_deputy_leaders (organization_id, deputy_leader_account, deputy_leader_order)
VALUES
((SELECT id FROM organization WHERE org_id = 'dept_it' AND tenant_code = 'default'), 'zhaobin1', 1),
((SELECT id FROM organization WHERE org_id = 'dept_it' AND tenant_code = 'default'), 'zhangyaohua1', 2);

-- 添加信息技术部下属团队
INSERT INTO organization (org_id, org_name, parent_org_id, main_leader_account, superior_leader_account, tenant_code, org_type, create_time, created_by)
VALUES
('team_operation', '运营管理室', 'dept_it', 'yanbingan', 'zhaobin1', 'default', 'OFFICE', NOW(), 'system'),
('team_it_planning', '系统规划与中台运营室', 'dept_it', 'wangjingui', 'zhangyaohua1', 'default', 'OFFICE', NOW(), 'system'),
('team_mis', '管理信息系统室', 'dept_it', 'chenhaiwei', 'zhaobin1', 'default', 'OFFICE', NOW(), 'system'),
('team_enterprise_support', '政企业务支撑室', 'dept_it', 'huangfushuming', 'zhaobin1', 'default', 'OFFICE', NOW(), 'system'),
('team_billing', '计费账务室', 'dept_it', 'wangshouchu', 'zhangyaohua1', 'default', 'OFFICE', NOW(), 'system'),
('team_platform', '软件平台维护室', 'dept_it', 'gaojian', 'zhangyaohua1', 'default', 'OFFICE', NOW(), 'system'),
('team_security', '信息安全室', 'dept_it', 'xuhuiyun', null, 'default', 'OFFICE', NOW(), 'system');

-- 添加政企业务部
INSERT INTO organization (org_id, org_name, parent_org_id, main_leader_account, superior_leader_account, tenant_code, org_type, create_time, created_by)
VALUES ('dept_enterprise', '政企业务部', 'company', 'baiyu', 'wangyong1', 'default', 'DEPARTMENT', NOW(), 'system');

-- 添加政企业务部副领导
INSERT INTO organization_deputy_leaders (organization_id, deputy_leader_account, deputy_leader_order)
VALUES
((SELECT id FROM organization WHERE org_id = 'dept_enterprise' AND tenant_code = 'default'), 'zhangyifei', 1),
((SELECT id FROM organization WHERE org_id = 'dept_enterprise' AND tenant_code = 'default'), 'zhaowanli', 2);

-- 添加政企业务部下属团队
INSERT INTO organization (org_id, org_name, parent_org_id, main_leader_account, superior_leader_account, tenant_code, org_type, create_time, created_by)
VALUES
('team_project', '项目管理室', 'dept_enterprise', 'duanmufanzhi', 'zhaowanli', 'default', 'OFFICE', NOW(), 'system'),
('team_channel', '渠道管理室', 'dept_enterprise', 'lixuanang', 'zhangyifei', 'default', 'OFFICE', NOW(), 'system'),
('team_marketing', '营销管理室', 'dept_enterprise', 'xiongchangshun', 'zhangyifei', 'default', 'OFFICE', NOW(), 'system');

-- 添加人力部
INSERT INTO organization (org_id, org_name, parent_org_id, main_leader_account, superior_leader_account, tenant_code, org_type, create_time, created_by)
VALUES ('dept_hr', '人力部', 'company', 'wangmin', 'shoujianguo', 'default', 'DEPARTMENT', NOW(), 'system');

-- 添加人力部副领导
INSERT INTO organization_deputy_leaders (organization_id, deputy_leader_account, deputy_leader_order)
VALUES
((SELECT id FROM organization WHERE org_id = 'dept_hr' AND tenant_code = 'default'), 'liuqiang', 1);

-- 添加人力部下属团队
INSERT INTO organization (org_id, org_name, parent_org_id, main_leader_account, superior_leader_account, tenant_code, org_type, create_time, created_by)
VALUES
('hr_recruitment', '招聘科', 'dept_hr', 'sunyan', 'liuqiang', 'default', 'OFFICE', NOW(), 'system'),
('hr_training', '培训科', 'dept_hr', 'wulei', 'liuqiang', 'default', 'OFFICE', NOW(), 'system'),
('hr_compensation', '薪酬福利科', 'dept_hr', 'gaofeng', 'wangmin', 'default', 'OFFICE', NOW(), 'system');

-- 添加综合部
INSERT INTO organization (org_id, org_name, parent_org_id, main_leader_account, superior_leader_account, tenant_code, org_type, create_time, created_by)
VALUES ('dept_general', '综合部', 'company', 'zhangwei', 'yuqian', 'default', 'DEPARTMENT', NOW(), 'system');

-- 添加综合部副领导
INSERT INTO organization_deputy_leaders (organization_id, deputy_leader_account, deputy_leader_order)
VALUES
((SELECT id FROM organization WHERE org_id = 'dept_general' AND tenant_code = 'default'), 'chenli', 1);

-- 添加综合部下属团队
INSERT INTO organization (org_id, org_name, parent_org_id, main_leader_account, superior_leader_account, tenant_code, org_type, create_time, created_by)
VALUES
('general_admin', '行政科', 'dept_general', 'huangxiaoming', 'chenli', 'default', 'OFFICE', NOW(), 'system'),
('general_logistics', '后勤科', 'dept_general', 'zhoujie', 'chenli', 'default', 'OFFICE', NOW(), 'system'),
('general_reception', '接待科', 'dept_general', 'tangyue', 'chenli', 'default', 'OFFICE', NOW(), 'system');

-- 添加财务部
INSERT INTO organization (org_id, org_name, parent_org_id, main_leader_account, superior_leader_account, tenant_code, org_type, create_time, created_by)
VALUES ('dept_finance', '财务部', 'company', 'linjing', 'yuqian', 'default', 'DEPARTMENT', NOW(), 'system');

-- 添加财务部副领导
INSERT INTO organization_deputy_leaders (organization_id, deputy_leader_account, deputy_leader_order)
VALUES
((SELECT id FROM organization WHERE org_id = 'dept_finance' AND tenant_code = 'default'), 'zhaojun', 1);

-- 添加财务部下属团队
INSERT INTO organization (org_id, org_name, parent_org_id, main_leader_account, superior_leader_account, tenant_code, org_type, create_time, created_by)
VALUES
('finance_accounting', '会计科', 'dept_finance', 'xuwei', 'linjing', 'default', 'OFFICE', NOW(), 'system'),
('finance_budget', '预算科', 'dept_finance', 'yangmei', 'zhaojun', 'default', 'OFFICE', NOW(), 'system'),
('finance_tax', '税务科', 'dept_finance', 'fenglin', 'zhaojun', 'default', 'OFFICE', NOW(), 'system');

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
