#!/bin/bash

# 测试场景1：zhangxinpeng应该推荐zhaobin1
echo "测试场景1：zhangxinpeng应该推荐zhaobin1"
curl -s "http://localhost:8080/api/recommend?userAccount=zhangxinpeng&taskTitle=关于迎接集团公司政企大客户行为规范培训的通知" | jq

echo ""
echo "--------------------------------------"
echo ""

# 测试场景2：baiyu应该推荐zhangyifei
echo "测试场景2：baiyu应该推荐zhangyifei"
curl -s "http://localhost:8080/api/recommend?userAccount=baiyu&taskTitle=关于迎接集团公司政企大客户行为规范培训的通知" | jq
