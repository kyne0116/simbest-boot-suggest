#!/bin/bash

# 测试场景1：zxpzong应该推荐zb1
echo "测试场景1：zxpzong应该推荐zb1"
curl -s "http://localhost:8080/api/recommend?userAccount=zxpzong&taskTitle=关于迎接集团公司政企大客户行为规范培训的通知" | jq

echo ""
echo "--------------------------------------"
echo ""

# 测试场景2：yuzong应该推荐zqf
echo "测试场景2：yuzong应该推荐zqf"
curl -s "http://localhost:8080/api/recommend?userAccount=yuzong&taskTitle=关于迎接集团公司政企大客户行为规范培训的通知" | jq
