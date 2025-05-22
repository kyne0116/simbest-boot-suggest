-- 查询组织表中的数据
SELECT org_id, org_name, parent_org_id, main_leader_account, org_type FROM organization WHERE tenant_code = 'default' ORDER BY org_id;

-- 查询领导表中的数据
SELECT account, name FROM leader WHERE tenant_code = 'default' ORDER BY account;

-- 查询组织副领导表中的数据
SELECT o.org_id, o.org_name, odl.deputy_leader_account, l.name
FROM organization_deputy_leaders odl
JOIN organization o ON odl.organization_id = o.id
JOIN leader l ON odl.deputy_leader_account = l.account
WHERE o.tenant_code = 'default'
ORDER BY o.org_id, odl.deputy_leader_order;

-- 查询组织关键词
SELECT o.org_id, o.org_name, ok.keyword, ok.keyword_weight
FROM organization_keywords ok
JOIN organization o ON ok.organization_id = o.id
WHERE o.tenant_code = 'default'
ORDER BY o.org_id, ok.keyword_order;
