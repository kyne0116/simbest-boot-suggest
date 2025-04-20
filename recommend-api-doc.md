# `/recommend` 接口文档

## 接口概述

`/recommend`接口是领导推荐系统的核心API，用于根据当前办理人信息、任务标题和工作流方向，智能推荐最合适的领导账号。该接口采用多级匹配策略，包括基于组织关系的匹配、基于职责领域的匹配和基于文本相似度的匹配，确保在各种情况下都能提供合理的推荐结果。

## 基本信息

- **URL**: `/suggest/recommend`
- **方法**: POST
- **请求内容类型**: application/json
- **响应内容类型**: application/json

## 请求参数

请求体为JSON格式，包含以下字段：

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| userAccount | String | 是 | 当前办理人账号 |
| taskTitle | String | 是 | 任务标题，用于内容匹配 |
| workflowDirection | String | 是 | 工作流方向，可选值：DOWNWARD(向下指派)、UPWARD(向上请示)、PARALLEL(同级协办) |
| orgId | String | 否 | 当前办理人组织ID，用于组织关系匹配。如不提供，系统将尝试根据userAccount查找组织 |
| useOrg | Boolean | 否 | 是否使用组织关系匹配，默认为true。设置为false时将跳过组织关系匹配 |
| candidateAccounts | String[] | 否 | 候选账号列表，如果提供，则推荐结果必须在此列表中 |

## 响应结果

响应体为JSON格式，包含以下字段：

| 字段名 | 类型 | 描述 |
|--------|------|------|
| errcode | Integer | 错误码，0表示成功，非0表示失败 |
| status | Integer | HTTP状态码，200表示成功，500表示失败 |
| message | String | 提示信息 |
| data | Object | 推荐结果对象 |

### 推荐结果对象(data)字段

| 字段名 | 类型 | 描述 |
|--------|------|------|
| leaderAccount | String | 推荐的领导账号 |
| leaderName | String | 推荐的领导姓名 |
| reason | String | 推荐理由，包含使用的规则类型(组织关系、职责领域或文本相似度) |
| score | Double | 匹配分数(0-1之间) |
| confidenceLevel | Double | AI置信度(0-1之间) |
| recommendationType | String | 推荐类型，如"组织结构智能映射"、"语义理解与知识图谱"、"深度学习文本分析" |
| aiMetrics | Object | AI分析指标，包含语义理解度、任务匹配度等指标 |

## 工作流方向说明

系统支持三种工作流方向，不同方向下的组织关系匹配规则不同：

1. **向下指派(DOWNWARD)**：
   - 当前用户是组织的主管领导时，推荐该组织的分管领导
   - 根据任务标题匹配最合适的分管领导

2. **向上请示(UPWARD)**：
   - 直接推荐当前组织的上级领导账号
   - 如果当前用户是分管领导，则推荐主管领导

3. **同级协办(PARALLEL)**：
   - 当前用户是分管领导时，推荐其他分管领导
   - 当前用户是普通成员时，推荐同级组织的领导

## 匹配规则优先级

系统按照以下优先级进行推荐：

1. **基于组织关系的匹配**：如果当前办理人所在组织有对应的分管领导，直接推荐该领导
2. **基于职责领域的匹配**：如果组织关系匹配失败，系统将分析任务标题与各个职责领域的匹配度
3. **基于文本相似度的匹配**：当前两种方法都无法找到合适的领导时，系统使用复合文本相似度算法

## 请求示例

```json
{
  "userAccount": "zhangsan",
  "orgId": "org001",
  "taskTitle": "关于网络安全专项检查工作的通知",
  "workflowDirection": "UPWARD",
  "useOrg": true,
  "candidateAccounts": ["lisi", "wangwu", "zhaoliu"]
}
```

## 响应示例

```json
{
  "errcode": 0,
  "status": 200,
  "message": "操作成功",
  "data": {
    "leaderAccount": "lisi",
    "leaderName": "李四",
    "reason": "【智能组织关系分析】系统通过组织结构智能分析，识别到此类任务需要上级审批。已为您智能定位信息安全部的上级领导作为最佳处理人",
    "score": 1.0,
    "confidenceLevel": 0.95,
    "recommendationType": "组织结构智能映射",
    "aiMetrics": {
      "语义理解度": 0.92,
      "任务匹配度": 0.95,
      "专业领域关联度": 0.89,
      "历史处理效率": 0.87
    }
  }
}
```

## 错误响应示例

```json
{
  "errcode": -1,
  "status": 500,
  "message": "用户账号不能为空",
  "data": null
}
```

## 使用场景

1. **向下指派任务**：主管领导将任务分配给下属分管领导
   ```json
   {
     "userAccount": "zhangsan",
     "orgId": "org001",
     "taskTitle": "关于网络安全专项检查工作的通知",
     "workflowDirection": "DOWNWARD"
   }
   ```

2. **向上请示审批**：普通员工或分管领导向上级请示
   ```json
   {
     "userAccount": "lisi",
     "orgId": "org002",
     "taskTitle": "关于增加系统维护经费的请示",
     "workflowDirection": "UPWARD"
   }
   ```

3. **同级协办任务**：同级部门之间的协作
   ```json
   {
     "userAccount": "wangwu",
     "orgId": "org003",
     "taskTitle": "关于数据共享平台建设的协作方案",
     "workflowDirection": "PARALLEL"
   }
   ```

4. **跳过组织关系匹配**：直接基于任务内容进行匹配
   ```json
   {
     "userAccount": "zhaoliu",
     "taskTitle": "关于AI智能体建设项目的协作配合",
     "workflowDirection": "UPWARD",
     "useOrg": false
   }
   ```

5. **限定候选人范围**：在特定人员中选择最合适的领导
   ```json
   {
     "userAccount": "zhangsan",
     "taskTitle": "关于数据治理和系统升级的方案",
     "workflowDirection": "UPWARD",
     "candidateAccounts": ["lisi", "wangwu"]
   }
   ```

## 注意事项

1. 必须提供`userAccount`、`taskTitle`和`workflowDirection`参数
2. 如果不提供`orgId`但提供了`userAccount`，系统会尝试根据用户账号查找组织
3. 设置`useOrg=false`可以跳过组织关系匹配，直接使用基于内容的匹配
4. 提供`candidateAccounts`可以限定推荐结果必须在指定的候选人范围内
5. 推荐结果中的`reason`字段会明确标识使用了哪种规则类型生成推荐
6. 系统会根据任务标题特征动态调整匹配阈值，提高匹配准确性
