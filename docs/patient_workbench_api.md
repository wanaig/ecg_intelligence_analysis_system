# 心电图预警与患者管理工作台 API 接口规范

## 1. 文档信息

| 项目 | 内容 |
|---|---|
| 文档名称 | 心电图预警与患者管理工作台 API 接口规范 |
| 版本 | v1.0 |
| 更新日期 | 2026-04-18 |
| 适用系统 | 心电图情报分析系统（患者管理工作台、预警联动场景） |
| 协议 | HTTP/HTTPS + JSON |
| 字符集 | UTF-8 |
| 基础路径 | /api |

---

## 2. 全局接口规范

### 2.1 RESTful 设计约定

1. 资源域使用 patient、warning、monitor，URL 采用名词化表达。
2. 查询型接口使用 GET；复杂筛选分页使用 POST。
3. 所有接口统一返回标准响应包，不直接裸返回业务对象。
4. 业务数据采用逻辑删除过滤（is_deleted = 0）。

### 2.2 认证与权限约定

1. 建议统一使用 JWT（Authorization: Bearer token）进行身份认证。
2. 接口权限采用 RBAC 权限码控制，并按科室/病区实施数据范围过滤。
3. 关键查询行为建议记录至系统操作日志，满足医疗审计追溯要求。

### 2.3 全局响应结构

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| code | integer | 是 | 业务状态码 |
| message | string | 是 | 状态描述 |
| data | object | 否 | 业务数据，失败时可为 null |
| timestamp | long | 是 | 服务端毫秒时间戳 |

成功响应示例：

```json
{
  "code": 0,
  "message": "成功",
  "data": {},
  "timestamp": 1776501256203
}
```

失败响应示例：

```json
{
  "code": 400,
  "message": "patientId 参数不合法",
  "data": null,
  "timestamp": 1776501256203
}
```

### 2.4 通用状态码

| code | 状态 | 说明 |
|---|---|---|
| 0 | SUCCESS | 请求成功 |
| 400 | BAD_REQUEST | 请求参数错误 |
| 401 | UNAUTHORIZED | 未认证或登录失效 |
| 403 | FORBIDDEN | 无权限访问 |
| 404 | NOT_FOUND | 资源不存在 |
| 422 | VALIDATION_ERROR | 参数校验失败 |
| 1000 | BUSINESS_ERROR | 业务异常 |
| 5000 | INTERNAL_ERROR | 系统内部异常 |

### 2.5 业务字段枚举

#### 2.5.1 患者基础枚举

| 字段 | 值 | 含义 |
|---|---|---|
| gender | 1 | 男 |
| gender | 2 | 女 |
| gender | 3 | 未知 |
| riskLevel | 1 | 低危 |
| riskLevel | 2 | 中危 |
| riskLevel | 3 | 中高危 |
| riskLevel | 4 | 高危 |
| patientStatus | 1 | 住院中 |
| patientStatus | 2 | 出院 |
| patientStatus | 3 | 居家随访 |

#### 2.5.2 心电与预警枚举

| 字段 | 值 | 含义 |
|---|---|---|
| aiAnalysisStatus | 0 | 待分析 |
| aiAnalysisStatus | 1 | 分析中 |
| aiAnalysisStatus | 2 | 已分析 |
| aiAnalysisStatus | 3 | 失败 |
| reportStatus(采集记录) | 0 | 未生成 |
| reportStatus(采集记录) | 1 | 待审核 |
| reportStatus(采集记录) | 2 | 已审核 |
| reportStatus(采集记录) | 3 | 驳回 |
| reportStatus(诊断报告) | 0 | 草稿 |
| reportStatus(诊断报告) | 1 | 待审核 |
| reportStatus(诊断报告) | 2 | 审核通过 |
| reportStatus(诊断报告) | 3 | 已驳回 |
| reportStatus(诊断报告) | 4 | 已作废 |
| displayStatus | 0 | 待分析 |
| displayStatus | 1 | 已分析 |
| displayStatus | 2 | 已审核 |
| displayStatus | 3 | 已驳回 |
| displayStatus | 4 | 采集失败 |
| warningLevel | 1 | 低危 |
| warningLevel | 2 | 中危 |
| warningLevel | 3 | 高危 |
| handleStatus | 0 | 待确认 |
| handleStatus | 1 | 待处理 |
| handleStatus | 2 | 处理中 |
| handleStatus | 3 | 已处理 |
| handleStatus | 4 | 已忽略 |

---

## 3. 接口清单

| 序号 | 接口名称 | 方法 | 路径 |
|---|---|---|---|
| 1 | 患者工作台顶部统计 | GET | /api/patient/statistics |
| 2 | 患者列表分页查询 | POST | /api/patient/page |
| 3 | 患者详情查询 | GET | /api/patient/detail |
| 4 | 预警全局统计 | GET | /api/warning/statistics |
| 5 | 预警列表分页查询 | POST | /api/warning/page |
| 6 | 预警单条详情 | GET | /api/warning/detail |
| 7 | 提交处理预警 | POST | /api/warning/handle |
| 8 | 预警筛选字典枚举 | GET | /api/warning/dicts |
| 9 | 实时监护全局顶部统计 | GET | /api/monitor/statistics（兼容 /api/monitor/overview/stat） |
| 10 | 实时监护大屏患者数据 | GET | /api/monitor/patients（兼容 /api/monitor/realTime/list） |
| 11 | 重点监护患者列表查询 | GET | /api/monitor/key/list |
| 12 | 实时监护科室分布统计 | GET | /api/monitor/ward-distribution（兼容 /api/monitor/dept/stat） |
| 13 | 加入重点监护 | POST | /api/monitor/opt/addKey |
| 14 | 解除重点监护 | POST | /api/monitor/opt/cancelKey |
| 15 | 设备分页列表查询 | POST | /api/monitor/device/page |
| 16 | 设备筛选字典枚举 | GET | /api/monitor/device/dicts |
| 17 | 新增设备 | POST | /api/monitor/device |
| 18 | 编辑设备 | PUT | /api/monitor/device |
| 19 | 删除设备 | DELETE | /api/monitor/device |
| 20 | 设备使用详情查询 | GET | /api/monitor/device/detail |
| 21 | 设备维护记录分页查询 | GET | /api/monitor/device/maintain-records |
| 22 | 质控记录分页查询 | POST | /api/monitor/quality-control/page |
| 23 | 质控公共字典枚举 | GET | /api/monitor/quality-control/dicts |
| 24 | 新增质控记录 | POST | /api/monitor/quality-control |
| 25 | 编辑质控记录 | PUT | /api/monitor/quality-control |
| 26 | 删除质控记录 | DELETE | /api/monitor/quality-control |
| 27 | 质控记录详情查询 | GET | /api/monitor/quality-control/detail |
| 28 | 核心大盘指标 | GET | /api/analysis/core-metrics |
| 29 | 病区心电测量统计 | GET | /api/analysis/ward-ecg-stats |
| 30 | 预警维度统计 | GET | /api/analysis/warning-dimensions |
| 31 | 报告与设备统计 | GET | /api/analysis/report-device-stats |
| 32 | 数据分析筛选字典 | GET | /api/analysis/dicts |
| 33 | 用户分页列表查询 | POST | /api/system/user/page |
| 34 | 用户筛选字典接口 | GET | /api/system/user/dicts |
| 35 | 新增用户 | POST | /api/system/user |
| 36 | 查询用户单条详情 | GET | /api/system/user/detail |
| 37 | 编辑用户信息 | PUT | /api/system/user |
| 38 | 删除用户 | DELETE | /api/system/user |
| 39 | 重置用户密码 | POST | /api/system/user/reset-password |
| 40 | 角色分页列表查询 | POST | /api/system/role/page |
| 41 | 新增角色 | POST | /api/system/role |
| 42 | 角色详情查询 | GET | /api/system/role/detail |
| 43 | 编辑角色 | PUT | /api/system/role |
| 44 | 删除角色 | DELETE | /api/system/role |
| 45 | 查询角色已有权限 | GET | /api/system/role/permissions |
| 46 | 保存角色权限分配 | POST | /api/system/role/permissions |
| 47 | 科室分页列表查询 | POST | /api/system/department/page |
| 48 | 上级科室树形下拉 | GET | /api/system/department/tree |
| 49 | 新增科室 | POST | /api/system/department |
| 50 | 科室详情查询 | GET | /api/system/department/detail |
| 51 | 编辑科室 | PUT | /api/system/department |
| 52 | 删除科室 | DELETE | /api/system/department |
| 53 | 工作台全局概览统计 | GET | /api/workbench/overview |
| 54 | 待处理预警分页列表 | POST | /api/workbench/pending-alerts/page |
| 55 | 最新心电记录分页查询 | POST | /api/workbench/latest-ecg/page |
| 56 | 预警详情查询 | GET | /api/workbench/alerts/{alertId} |
| 57 | 单条心电数据详情 | GET | /api/workbench/ecg/{ecgId} |
| 58 | 工作台公共筛选字典 | GET | /api/workbench/dicts |
| 59 | 心电数据分页列表查询 | POST | /api/ecg-data/page |
| 60 | 心电数据上传 | POST | /api/ecg-data/upload |
| 61 | 心电数据详情查询 | GET | /api/ecg-data/{ecgId} |
| 62 | 心电数据状态更新 | PUT | /api/ecg-data/status |
| 63 | 心电数据筛选字典 | GET | /api/ecg-data/dicts |
| 64 | 原始心电波形数据获取 | GET | /api/ecg-data/{ecgId}/waveform |
| 65 | AI诊断全局统计概览 | GET | /api/analysis/ai-diagnosis/overview |
| 66 | AI诊断记录分页查询 | POST | /api/analysis/ai-diagnosis/page |
| 67 | AI诊断审核状态字典 | GET | /api/analysis/ai-diagnosis/dicts |
| 68 | AI诊断详情查询 | GET | /api/analysis/ai-diagnosis/{diagnosisId} |
| 69 | AI诊断原始心电波形数据 | GET | /api/analysis/ai-diagnosis/ecg/{ecgId}/waveform |
| 70 | 医生审核提交 | POST | /api/analysis/ai-diagnosis/audit |
| 71 | 诊断报告分页列表查询 | POST | /api/analysis/diagnosis-report/page |
| 72 | 诊断报告筛选字典 | GET | /api/analysis/diagnosis-report/dicts |
| 73 | 单条诊断报告详情查询 | GET | /api/analysis/diagnosis-report/{reportId} |
| 74 | 诊断报告PDF下载 | GET | /api/analysis/diagnosis-report/{reportId}/pdf |
| 75 | 患者公共筛选字典 | GET | /api/patient/dicts |
| 76 | 质控设备下拉列表 | GET | /api/monitor/quality-control/device-options |
| 77 | 测试人员下拉列表 | GET | /api/sys/user/testUserOptions |
| 78 | 数据分析大盘核心统计指标 | GET | /api/analysis/dashboard/core-metrics |
| 79 | 预警级别分布数据 | GET | /api/analysis/dashboard/warning-level-distribution |
| 80 | 预警类型+病区TOP排行数据 | GET | /api/analysis/dashboard/warning-type-ward-top |
| 81 | 近7日预警趋势折线数据 | GET | /api/analysis/dashboard/warning-trend-7d |
| 82 | 待处理预警分页列表（数据分析） | POST | /api/analysis/dashboard/pending-warnings/page |
| 83 | 最新心电记录分页列表（数据分析） | POST | /api/analysis/dashboard/latest-ecg/page |
| 84 | AI引擎运行状态 | GET | /api/analysis/ai-diagnosis/dashboard/engine-status |
| 85 | AI核心指标（含同比） | GET | /api/analysis/ai-diagnosis/dashboard/core-metrics |
| 86 | AI审核趋势（7/15/30） | GET | /api/analysis/ai-diagnosis/dashboard/warning-trend |
| 87 | AI异常类型占比饼图 | GET | /api/analysis/ai-diagnosis/dashboard/abnormal-type-ratio |
| 88 | AI诊断记录分页（重构版） | POST | /api/analysis/ai-diagnosis/dashboard/page |
| 89 | AI诊断轻量详情 | GET | /api/analysis/ai-diagnosis/dashboard/{diagnosisId}/lite |
| 90 | AI审核完整详情 | GET | /api/analysis/ai-diagnosis/dashboard/{diagnosisId}/audit-detail |
| 91 | AI审核提交（闭环） | POST | /api/analysis/ai-diagnosis/dashboard/audit/submit |
| 92 | 科研数据分页列表查询 | GET | /api/analysis/research-data/page |
| 93 | 选中科研数据脱敏导出 | POST | /api/analysis/research-data/export/selected |
| 94 | 筛选结果全量脱敏导出 | POST | /api/analysis/research-data/export/all |
| 95 | 数据分析-预警单条详情 | GET | /api/analysis/dashboard/warnings/{alertId}/detail |
| 96 | 数据分析-全量预警列表前置加载 | POST | /api/analysis/dashboard/warnings/full-page/init |
| 97 | 预警纳入科研/重点监护 | POST | /api/analysis/dashboard/warnings/include |
| 98 | 患者详情回响（弹窗初始化） | GET | /api/patient/{id} |
| 99 | 修改患者信息 | PUT | /api/patient/{patientId} |

---

## 4. 接口详情

## 4.1 患者工作台顶部统计

### 4.1.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 患者工作台顶部统计 |
| 业务作用 | 渲染患者管理工作台顶部 4 个核心统计数字 |
| 请求路径 | /api/patient/statistics |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | patient:dashboard:read |
| 数据范围 | 按用户数据权限返回可见患者统计 |

### 4.1.2 请求入参

无业务入参。

### 4.1.3 出参说明

#### data 字段

| 字段 | 类型 | 说明 |
|---|---|---|
| totalPatient | long | 总患者数（patient_info 逻辑未删除） |
| inHospital | long | 住院患者数（patient_status = 1） |
| homeFollow | long | 居家随访患者数（patient_status = 3） |
| highRisk | long | 高危患者数（risk_level = 4） |

### 4.1.4 完整请求示例

```http
GET /api/patient/statistics HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
```

### 4.1.5 完整响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "totalPatient": 5,
    "inHospital": 4,
    "homeFollow": 1,
    "highRisk": 1
  },
  "timestamp": 1776501256203
}
```

---

## 4.2 患者列表分页查询

### 4.2.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 患者列表分页查询 |
| 业务作用 | 渲染患者表格，支持多条件筛选与分页 |
| 请求路径 | /api/patient/page |
| 请求方式 | POST |
| Content-Type | application/json |
| 权限码 | patient:list:read |
| 数据范围 | 按用户科室/病区数据权限返回列表 |

### 4.2.2 请求入参

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|---|---|---|---|---|
| keyword | string | 否 | 空 | 姓名/住院号模糊检索 |
| ward | string | 否 | 空 | 病区筛选，支持 wardName 或 wardId 字符串 |
| riskLevel | string | 否 | 空 | 风险等级，支持 低危/中危/中高危/高危 或 1/2/3/4 |
| patientStatus | string | 否 | 空 | 患者状态，支持 住院中/出院/居家随访 或 1/2/3 |
| pageNum | long | 否 | 1 | 页码，最小 1 |
| pageSize | long | 否 | 10 | 每页条数，最大 200 |

### 4.2.3 出参说明

#### data（分页信息）

| 字段 | 类型 | 说明 |
|---|---|---|
| pageNum | long | 当前页码 |
| pageSize | long | 每页条数 |
| total | long | 总记录数 |
| pages | long | 总页数 |
| records | array | 当前页数据 |

#### data.records[]（患者列表项）

| 字段 | 类型 | 说明 |
|---|---|---|
| patientId | long | 患者 ID |
| patientName | string | 患者姓名 |
| gender | integer | 性别编码 |
| genderText | string | 性别文本 |
| age | integer | 年龄 |
| inpatientNo | string | 住院号 |
| wardId | long | 病区 ID |
| wardName | string | 病区名称 |
| bedId | long | 床位 ID |
| bedNo | string | 床号 |
| riskLevel | integer | 风险等级编码 |
| riskLevelText | string | 风险等级文本 |
| patientStatus | integer | 患者状态编码 |
| patientStatusText | string | 患者状态文本 |
| primaryDiagnosis | string | 主要诊断 |
| ecgCount | integer | 心电记录次数 |
| latestEcgTime | string | 最近心电时间（ISO-8601） |

### 4.2.4 完整请求示例

```http
POST /api/patient/page HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
Content-Type: application/json

{
  "keyword": "张",
  "ward": "心内一病区",
  "riskLevel": "高危",
  "patientStatus": "住院中",
  "pageNum": 1,
  "pageSize": 10
}
```

### 4.2.5 完整响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "pageNum": 1,
    "pageSize": 10,
    "total": 5,
    "pages": 1,
    "records": [
      {
        "patientId": 1904,
        "patientName": "陈敏",
        "gender": 2,
        "genderText": "女",
        "age": 49,
        "inpatientNo": "ZY202604004",
        "wardId": 1503,
        "wardName": "冠心监护病区",
        "bedId": 1704,
        "bedNo": "ICU-01",
        "riskLevel": 4,
        "riskLevelText": "高危",
        "patientStatus": 1,
        "patientStatusText": "住院中",
        "primaryDiagnosis": "急性冠脉综合征（监护中）",
        "ecgCount": 5,
        "latestEcgTime": "2026-04-18T09:40:00"
      }
    ]
  },
  "timestamp": 1776501256203
}
```

---

## 4.3 患者详情查询

### 4.3.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 患者详情查询 |
| 业务作用 | 点击患者列表“查看详情”后返回完整患者全景数据 |
| 请求路径 | /api/patient/detail |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | patient:detail:read |
| 数据范围 | 按用户权限校验目标患者是否可见 |

### 4.3.2 请求入参

| 参数位置 | 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|---|
| Query | patientId | long | 是 | 患者唯一 ID |

### 4.3.3 出参说明

#### data 顶层结构

| 字段 | 类型 | 说明 |
|---|---|---|
| basicInfo | object | 患者基础信息 |
| ecgRecords | array | 心电记录列表（按采集时间倒序） |
| warningHistory | array | 预警历史（按预警时间倒序） |
| diagnosisInfo | array | 诊断报告与 AI 诊断信息（按报告创建时间倒序） |

#### data.basicInfo 字段

| 字段 | 类型 | 说明 |
|---|---|---|
| patientId | long | 患者 ID |
| patientName | string | 患者姓名 |
| gender | integer | 性别编码 |
| genderText | string | 性别文本 |
| birthDate | string | 出生日期 |
| age | integer | 年龄 |
| idCard | string | 身份证号（脱敏） |
| inpatientNo | string | 住院号 |
| wardId | long | 病区 ID |
| wardName | string | 病区名称 |
| bedId | long | 床位 ID |
| bedNo | string | 床号 |
| deviceId | long | 设备 ID |
| phone | string | 联系电话（脱敏） |
| admissionTime | string | 入院时间 |
| dischargeTime | string/null | 出院时间 |
| primaryDiagnosis | string | 初步诊断 |
| riskLevel | integer | 风险等级编码 |
| riskLevelText | string | 风险等级文本 |
| patientType | integer | 患者类型 |
| patientStatus | integer | 患者状态编码 |
| patientStatusText | string | 患者状态文本 |
| followUpStatus | integer | 随访状态 |
| ecgCount | integer | 心电记录次数 |
| latestEcgTime | string | 最近心电时间 |

#### data.ecgRecords[] 字段

| 字段 | 类型 | 说明 |
|---|---|---|
| recordId | long | 采集记录 ID |
| ecgNo | string | 心电编号 |
| deviceName | string | 设备名称 |
| collectionStartTime | string | 采集开始时间 |
| collectionEndTime | string | 采集结束时间 |
| aiAnalysisStatus | integer | AI 分析状态编码 |
| aiAnalysisStatusText | string | AI 分析状态文本 |
| reportStatus | integer | 报告状态编码 |
| reportStatusText | string | 报告状态文本 |
| displayStatus | integer | 展示状态编码 |
| displayStatusText | string | 展示状态文本 |
| aiConclusionShort | string | AI 简要结论 |

#### data.warningHistory[] 字段

| 字段 | 类型 | 说明 |
|---|---|---|
| warningId | long | 预警 ID |
| warningTime | string | 预警时间 |
| warningType | string | 预警类型 |
| warningLevel | integer | 预警等级编码 |
| warningLevelText | string | 预警等级文本 |
| warningDesc | string | 预警描述 |
| handleStatus | integer | 处理状态编码 |
| handleStatusText | string | 处理状态文本 |
| handleUserName | string | 处理人 |
| handleTime | string | 处理时间 |
| handleOpinion | string | 处理意见 |

#### data.diagnosisInfo[] 字段

| 字段 | 类型 | 说明 |
|---|---|---|
| reportId | long | 报告 ID |
| reportNo | string | 报告编号 |
| recordId | long | 采集记录 ID |
| aiDiagnosisId | long | AI 诊断 ID |
| reportCreateTime | string | 报告创建时间 |
| reportStatus | integer | 报告状态编码 |
| reportStatusText | string | 报告状态文本 |
| aiConclusion | string | AI 完整结论 |
| doctorDiagnosis | string | 医生诊断结论 |
| doctorSuggestion | string | 医生建议 |
| reportCreateDoctorName | string | 报告医生 |
| auditDoctorName | string | 审核医生 |
| auditTime | string | 审核时间 |
| auditOpinion | string | 审核意见 |
| abnormalLevel | integer | AI 异常级别 |
| abnormalLevelText | string | AI 异常级别文本 |
| aiConfidence | number | AI 置信度 |

### 4.3.4 完整请求示例

```http
GET /api/patient/detail?patientId=1901 HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
```

### 4.3.5 完整响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "basicInfo": {
      "patientId": 1901,
      "patientName": "张建国",
      "gender": 1,
      "genderText": "男",
      "birthDate": "1959-03-12",
      "age": 67,
      "idCard": "110101********1201",
      "inpatientNo": "ZY202604001",
      "wardId": 1501,
      "wardName": "心内一病区",
      "bedId": 1701,
      "bedNo": "1床",
      "deviceId": 1801,
      "phone": "138****0001",
      "admissionTime": "2026-04-12T09:20:00",
      "dischargeTime": null,
      "primaryDiagnosis": "冠状动脉粥样硬化性心脏病",
      "riskLevel": 3,
      "riskLevelText": "中高危",
      "patientType": 1,
      "patientStatus": 1,
      "patientStatusText": "住院中",
      "followUpStatus": 0,
      "ecgCount": 3,
      "latestEcgTime": "2026-04-18T08:10:00"
    },
    "ecgRecords": [
      {
        "recordId": 2101,
        "ecgNo": "ECG20260418001",
        "deviceName": "十二导联心电图机",
        "collectionStartTime": "2026-04-18T08:00:00",
        "collectionEndTime": "2026-04-18T08:10:00",
        "aiAnalysisStatus": 2,
        "aiAnalysisStatusText": "已分析",
        "reportStatus": 2,
        "reportStatusText": "已审核",
        "displayStatus": 2,
        "displayStatusText": "已审核",
        "aiConclusionShort": "窦性心律，偶发室早"
      }
    ],
    "warningHistory": [
      {
        "warningId": 2401,
        "warningTime": "2026-04-18T08:11:00",
        "warningType": "室性早搏增多",
        "warningLevel": 2,
        "warningLevelText": "中危",
        "warningDesc": "连续3次捕获室性早搏，建议人工复核",
        "handleStatus": 3,
        "handleStatusText": "已处理",
        "handleUserName": "陈医生",
        "handleTime": "2026-04-18T08:30:00",
        "handleOpinion": "已处理，继续观察"
      }
    ],
    "diagnosisInfo": [
      {
        "reportId": 2301,
        "reportNo": "REP20260418001",
        "recordId": 2101,
        "aiDiagnosisId": 2201,
        "reportCreateTime": "2026-04-18T08:20:00",
        "reportStatus": 2,
        "reportStatusText": "审核通过",
        "aiConclusion": "窦性心律，偶发室性早搏，建议结合临床复核",
        "doctorDiagnosis": "偶发室早，继续药物治疗并复查",
        "doctorSuggestion": "建议48小时后复查心电",
        "reportCreateDoctorName": "陈医生",
        "auditDoctorName": "孙主任",
        "auditTime": "2026-04-18T08:45:00",
        "auditOpinion": "同意诊断意见",
        "abnormalLevel": 2,
        "abnormalLevelText": "中危",
        "aiConfidence": 92.40
      }
    ]
  },
  "timestamp": 1776501256203
}
```

---

## 4.4 患者公共筛选字典接口

### 4.4.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 患者公共筛选字典 |
| 业务作用 | 加载患者管理页面筛选下拉项（病区、风险等级、患者状态） |
| 请求路径 | /api/patient/dicts |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | patient:dict:read |
| 数据范围 | 返回系统启用病区与标准枚举 |

### 4.4.2 请求入参

无业务入参。

### 4.4.3 出参说明

#### data 顶层结构

| 字段 | 类型 | 说明 |
|---|---|---|
| wardOptions | array | 病区筛选字典 |
| riskLevelOptions | array | 风险等级筛选字典 |
| patientStatusOptions | array | 患者状态筛选字典 |

#### wardOptions[] / riskLevelOptions[] / patientStatusOptions[] 字段

| 字段 | 类型 | 说明 |
|---|---|---|
| value | string | 字典值 |
| label | string | 字典展示文本 |

### 4.4.4 完整请求示例

```http
GET /api/patient/dicts HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
```

### 4.4.5 完整响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "wardOptions": [
      {
        "value": "",
        "label": "全部病区"
      },
      {
        "value": "1501",
        "label": "心内一病区"
      }
    ],
    "riskLevelOptions": [
      {
        "value": "",
        "label": "全部风险等级"
      },
      {
        "value": "1",
        "label": "低危"
      },
      {
        "value": "2",
        "label": "中危"
      },
      {
        "value": "3",
        "label": "中高危"
      },
      {
        "value": "4",
        "label": "高危"
      }
    ],
    "patientStatusOptions": [
      {
        "value": "",
        "label": "全部患者状态"
      },
      {
        "value": "1",
        "label": "住院中"
      },
      {
        "value": "2",
        "label": "出院"
      },
      {
        "value": "3",
        "label": "居家随访"
      }
    ]
  },
  "timestamp": 1776587656203
}
```

---

## 5. 权限说明（建议落地方案）

### 5.1 权限点定义

| 权限码 | 接口 | 说明 |
|---|---|---|
| patient:dashboard:read | GET /api/patient/statistics | 查看顶部统计 |
| patient:list:read | POST /api/patient/page | 查看患者分页列表 |
| patient:detail:read | GET /api/patient/detail | 查看患者详情 |
| patient:dict:read | GET /api/patient/dicts | 查看患者筛选字典 |

### 5.2 角色建议映射

| 角色 | 建议权限 |
|---|---|
| 系统管理员 | 全部患者查询权限 |
| 心内科主任 | 统计、列表、详情 |
| 心内科医生 | 统计、列表、详情 |
| 心内科护士 | 统计、列表、详情（可限制部分敏感字段） |
| 设备工程师 | 默认不开放患者详情，可按业务授权 |

### 5.3 数据权限建议

1. 支持按科室、病区、居家站点进行数据范围隔离。
2. 患者详情接口在 patientId 命中后仍需校验数据权限。
3. 超权限访问返回 403，禁止通过错误信息泄露患者存在性。

---

## 6. 业务流转规则（工作台场景）

1. 顶部统计口径来源 patient_info，且仅统计逻辑未删除患者。
2. 住院数口径为 patient_status = 1；居家随访口径为 patient_status = 3；高危口径为 risk_level = 4。
3. 列表筛选 keyword 同时作用于 patient_name 与 inpatient_no。
4. ward 支持病区名称和 wardId 字符串双模式，兼容前端下拉组件差异。
5. riskLevel、patientStatus 支持中文文本与数值编码双输入，便于多端统一接入。
6. 详情页数据聚合顺序：患者基础信息 -> 心电记录 -> 预警历史 -> 诊断信息。
7. 列表与详情时间排序规则：
   - ecgRecords 按 collection_start_time 倒序；
   - warningHistory 按 warning_time 倒序；
   - diagnosisInfo 按 report_create_time 倒序。
8. 预警与诊断联动：当 AI 异常级别提升时，预警记录进入 warningHistory，并由处理状态形成闭环。
9. 空数据语义：详情对象存在但子列表可为空数组；不存在患者返回 404。
10. 所有查询均应遵循医疗审计要求，建议记录查询操作日志。

---

## 7. 错误场景定义

| 场景 | code | message 示例 |
|---|---|---|
| patientId 为空或非法 | 400 | patientId 参数不合法 |
| riskLevel 传入非法值 | 400 | riskLevel 参数不合法 |
| patientStatus 传入非法值 | 400 | patientStatus 参数不合法 |
| 患者不存在或已逻辑删除 | 404 | 患者不存在 |
| 未登录或 token 失效 | 401 | 未认证或登录已失效 |
| 无权限访问目标患者 | 403 | 无权限访问 |
| 服务内部异常 | 5000 | 系统繁忙，请稍后重试 |

---

## 8. 联调与落地建议

1. 前端工作台初始化建议并发调用 statistics 与 page，提升首屏性能。
2. 列表操作列“查看详情”使用 detail 接口并按 patientId 精确查询。
3. page 的 pageSize 建议限制在 20~50，避免一次性拉取过大数据。
4. 生产环境建议接入接口限流、审计日志与字段脱敏策略。
5. 如需对接 OpenAPI，可基于本文档直接生成接口注释与 Swagger 描述。

---

## 9. 预警工作台接口规范

## 9.1 预警全局统计接口

### 9.1.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 预警全局统计 |
| 业务作用 | 页面首次加载，渲染顶部“高危预警、待处理预警”统计数字 |
| 请求路径 | /api/warning/statistics |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | warning:dashboard:read |

### 9.1.2 请求入参

无业务入参。

### 9.1.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| highRiskCount | long | 高危预警数（warning_level = 3） |
| pendingHandleCount | long | 待处理预警数（handle_status in 0/1/2） |

### 9.1.4 请求示例

```http
GET /api/warning/statistics HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
```

### 9.1.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "highRiskCount": 1,
    "pendingHandleCount": 3
  },
  "timestamp": 1776501256203
}
```

---

## 9.2 预警列表分页查询主接口

### 9.2.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 预警列表分页查询 |
| 业务作用 | 加载预警表格，支持关键字、病区、级别、状态、时间范围筛选 |
| 请求路径 | /api/warning/page |
| 请求方式 | POST |
| Content-Type | application/json |
| 权限码 | warning:list:read |

### 9.2.2 请求入参

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|---|---|---|---|---|
| keyword | string | 否 | 空 | 患者姓名/住院号搜索 |
| ward | string | 否 | 空 | 病区筛选（病区名或病区ID字符串） |
| alertLevel | string | 否 | 空 | 预警级别（低危/中危/高危 或 1/2/3） |
| alertStatus | string | 否 | 空 | 预警状态（待确认/待处理/处理中/已处理/已忽略 或 0-4） |
| startTime | string | 否 | 空 | 起始时间，支持 yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss |
| endTime | string | 否 | 空 | 结束时间，支持 yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss |
| pageNum | long | 否 | 1 | 页码 |
| pageSize | long | 否 | 15 | 每页条数，最大 200 |

### 9.2.3 出参说明

#### data（分页）

| 字段 | 类型 | 说明 |
|---|---|---|
| pageNum | long | 当前页码 |
| pageSize | long | 每页条数 |
| total | long | 总记录数 |
| pages | long | 总页数 |
| records | array | 列表数据 |

#### data.records[]（预警列表项）

| 字段 | 类型 | 说明 |
|---|---|---|
| rowNum | long | 序号（跨页连续） |
| alertId | long | 预警ID |
| warningTime | string | 预警时间 |
| patientName | string | 患者姓名 |
| genderText | string | 性别 |
| age | integer | 年龄 |
| patientInfo | string | 患者信息（姓名/性别/年龄） |
| inpatientNo | string | 住院号 |
| wardName | string | 病区 |
| warningType | string | 预警类型 |
| alertLevel | integer | 预警级别编码 |
| alertLevelText | string | 预警级别文本 |
| alertStatus | integer | 预警状态编码 |
| alertStatusText | string | 预警状态文本 |
| handleTime | string/null | 处理时间 |

### 9.2.4 请求示例

```http
POST /api/warning/page HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
Content-Type: application/json

{
  "keyword": "陈敏",
  "alertLevel": "高危",
  "alertStatus": "处理中",
  "startTime": "2026-04-18 00:00:00",
  "endTime": "2026-04-18 23:59:59",
  "pageNum": 1,
  "pageSize": 15
}
```

### 9.2.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "pageNum": 1,
    "pageSize": 15,
    "total": 5,
    "pages": 1,
    "records": [
      {
        "rowNum": 1,
        "alertId": 2404,
        "warningTime": "2026-04-18T09:41:00",
        "patientName": "陈敏",
        "genderText": "女",
        "age": 49,
        "patientInfo": "陈敏 / 女 / 49岁",
        "inpatientNo": "ZY202604004",
        "wardName": "冠心监护病区",
        "warningType": "室性心动过速风险",
        "alertLevel": 3,
        "alertLevelText": "高危",
        "alertStatus": 2,
        "alertStatusText": "处理中",
        "handleTime": "2026-04-18T09:50:00"
      }
    ]
  },
  "timestamp": 1776501256203
}
```

---

## 9.3 预警单条详情接口

### 9.3.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 预警单条详情 |
| 业务作用 | 点击“查看详情”打开预警详情弹窗 |
| 请求路径 | /api/warning/detail |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | warning:detail:read |

### 9.3.2 请求入参

| 参数位置 | 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|---|
| Query | alertId | long | 是 | 预警ID |

### 9.3.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| alertId | long | 预警ID |
| warningTime | string | 预警时间 |
| alertLevel | integer | 预警级别编码 |
| alertLevelText | string | 预警级别文本 |
| alertStatus | integer | 预警状态编码 |
| alertStatusText | string | 预警状态文本 |
| patientName | string | 患者姓名 |
| age | integer | 患者年龄 |
| genderText | string | 性别文本 |
| inpatientNo | string | 住院号 |
| wardName | string | 病区 |
| warningType | string | 预警类型 |
| warningDesc | string | 完整预警内容 |
| aiConclusion | string | AI心电结论 |
| clinicalManifestation | string | 患者临床表现 |
| lisHint | string | LIS检验关联提示 |
| handleUserName | string | 处理人 |
| handleTime | string/null | 处理时间 |
| handleRemark | string | 处理备注 |

### 9.3.4 请求示例

```http
GET /api/warning/detail?alertId=2404 HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
```

### 9.3.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "alertId": 2404,
    "warningTime": "2026-04-18T09:41:00",
    "alertLevel": 3,
    "alertLevelText": "高危",
    "alertStatus": 2,
    "alertStatusText": "处理中",
    "patientName": "陈敏",
    "age": 49,
    "genderText": "女",
    "inpatientNo": "ZY202604004",
    "wardName": "冠心监护病区",
    "warningType": "室性心动过速风险",
    "warningDesc": "AI判定高危，已进入持续监护流程",
    "aiConclusion": "疑似室速风险，建议立即人工复判并持续监护",
    "clinicalManifestation": "急性冠脉综合征（监护中）",
    "lisHint": "建议关联检查：肌钙蛋白、CK-MB、电解质、D-二聚体",
    "handleUserName": "孙主任",
    "handleTime": "2026-04-18T09:50:00",
    "handleRemark": "处理中，等待二次复核结果"
  },
  "timestamp": 1776501256203
}
```

---

## 9.4 提交处理预警接口

### 9.4.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 提交处理预警 |
| 业务作用 | 列表页或详情弹窗点击“处理”后完成预警闭环 |
| 请求路径 | /api/warning/handle |
| 请求方式 | POST |
| Content-Type | application/json |
| 权限码 | warning:handle:submit |

### 9.4.2 请求入参

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| alertId | long | 是 | 预警ID |
| handleRemark | string | 否 | 医生处置备注，最大256字符 |

### 9.4.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| alertId | long | 预警ID |
| alertStatus | integer | 处理后状态（固定为3） |
| alertStatusText | string | 处理后状态文本（已处理） |
| handleTime | string | 处理时间 |
| handleRemark | string | 处理备注 |

### 9.4.4 请求示例

```http
POST /api/warning/handle HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
Content-Type: application/json

{
  "alertId": 2405,
  "handleRemark": "已通知值班医生并安排复查，继续心电监护"
}
```

### 9.4.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "alertId": 2405,
    "alertStatus": 3,
    "alertStatusText": "已处理",
    "handleTime": "2026-04-18T17:20:00",
    "handleRemark": "已通知值班医生并安排复查，继续心电监护"
  },
  "timestamp": 1776501256203
}
```

---

## 9.5 筛选字典枚举接口

### 9.5.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 预警筛选字典枚举 |
| 业务作用 | 页面初始化时渲染“全部级别、全部状态”下拉项，避免前端硬编码 |
| 请求路径 | /api/warning/dicts |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | warning:dict:read |

### 9.5.2 请求入参

无业务入参。

### 9.5.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| alertLevelOptions | array | 预警级别字典 |
| alertStatusOptions | array | 预警状态字典 |

字典项结构：

| 字段 | 类型 | 说明 |
|---|---|---|
| value | string | 枚举值 |
| label | string | 展示文本 |

### 9.5.4 请求示例

```http
GET /api/warning/dicts HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
```

### 9.5.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "alertLevelOptions": [
      { "value": "", "label": "全部级别" },
      { "value": "1", "label": "低危" },
      { "value": "2", "label": "中危" },
      { "value": "3", "label": "高危" }
    ],
    "alertStatusOptions": [
      { "value": "", "label": "全部状态" },
      { "value": "0", "label": "待确认" },
      { "value": "1", "label": "待处理" },
      { "value": "2", "label": "处理中" },
      { "value": "3", "label": "已处理" },
      { "value": "4", "label": "已忽略" }
    ]
  },
  "timestamp": 1776501256203
}
```

---

## 10. 预警业务流转规则

1. 待处理预警定义为 handle_status in (0,1,2)。
2. 高危预警定义为 warning_level = 3。
3. 处理接口仅允许非“已处理/已忽略”状态提交，防止重复闭环。
4. 处理成功后状态统一置为已处理（3），并写入处理时间与处理备注。
5. 详情页 LIS 检验关联提示基于预警级别规则生成，作为临床辅助提醒。

## 11. 预警错误场景

| 场景 | code | message 示例 |
|---|---|---|
| alertId 为空或非法 | 400 | alertId 参数不合法 |
| alertLevel 传入非法值 | 400 | alertLevel 参数不合法 |
| alertStatus 传入非法值 | 400 | alertStatus 参数不合法 |
| 时间格式不合法 | 400 | startTime 参数格式错误 |
| 预警不存在 | 404 | 预警不存在 |
| 重复处理已处理预警 | 1000 | 预警已处理，请勿重复提交 |
| 处理已忽略预警 | 1000 | 预警已忽略，无法处理 |

---

## 12. 实时监护模块接口规范

## 12.1 实时监护全局顶部统计接口

### 12.1.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 实时监护全局顶部统计 |
| 业务作用 | 渲染监护大屏顶部 4 个核心统计数字 |
| 请求路径 | /api/monitor/statistics |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | monitor:overview:stat:read |

兼容路径：`/api/monitor/overview/stat`

### 12.1.2 请求入参

无业务入参。

### 12.1.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| todayCollect | long | 今日采集量 |
| pendingAnalyse | long | 待分析量 |
| pendingAudit | long | 待审核量 |
| alertCount | long | 预警总数 |

### 12.1.4 请求示例

```http
GET /api/monitor/statistics HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
```

### 12.1.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "todayCollect": 45,
    "pendingAnalyse": 8,
    "pendingAudit": 12,
    "alertCount": 11
  },
  "timestamp": 1776501256203
}
```

---

## 12.2 实时监护大屏患者数据接口

### 12.2.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 实时监护大屏患者数据 |
| 业务作用 | 查询普通监护患者列表，默认返回大屏数据；支持模板筛选兼容调用 |
| 请求路径 | /api/monitor/patients |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | monitor:realtime:list:read |

兼容路径：`/api/monitor/realTime/list`（POST，支持 `wardId`、`level` 筛选）

### 12.2.2 请求入参

无业务入参。

说明：当使用兼容路径 `/api/monitor/realTime/list` 时，可传 `wardId`（科室筛选）和 `level`（等级筛选）。

### 12.2.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| list | array | 实时监护患者列表 |

list[] 字段：

| 字段 | 类型 | 说明 |
|---|---|---|
| patientId | long | 患者ID |
| patientName | string | 患者姓名 |
| wardBed | string | 病区床号 |
| heartRate | integer | 心率 |
| status | string | 状态（正常/预警） |
| updateTime | string | 更新时间 |
| actionPermissions | array | 操作按钮权限（示例：monitor:opt:addKey） |

### 12.2.4 请求示例

```http
GET /api/monitor/patients HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
```

### 12.2.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "list": [
      {
        "patientId": 1901,
        "patientName": "张建国",
        "wardBed": "心内一病区 | 1床",
        "heartRate": 86,
        "status": "正常",
        "updateTime": "2026-04-20 10:15:30",
        "actionPermissions": [
          "monitor:opt:addKey"
        ]
      }
    ]
  },
  "timestamp": 1776501256203
}
```

---

## 12.3 重点监护患者列表查询

### 12.3.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 重点监护患者列表查询 |
| 业务作用 | 查询重点监护患者列表，页面展示“解除重点监护”按钮 |
| 请求路径 | /api/monitor/key/list |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | monitor:key:list:read |

### 12.3.2 请求入参

无业务入参。

### 12.3.3 出参说明

返回结构与 12.2 一致，`list[].actionPermissions` 固定包含 `monitor:opt:cancelKey`。

### 12.3.4 请求示例

```http
GET /api/monitor/key/list HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
```

### 12.3.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "list": [
      {
        "patientId": 1904,
        "patientName": "陈敏",
        "wardBed": "冠心监护病区 | ICU-01",
        "heartRate": 128,
        "status": "预警",
        "updateTime": "2026-04-20 10:16:42",
        "actionPermissions": [
          "monitor:opt:cancelKey"
        ]
      }
    ]
  },
  "timestamp": 1776501256203
}
```

---

## 12.4 实时监护科室分布统计接口

### 12.4.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 实时监护科室分布统计 |
| 业务作用 | 返回科室名称与患者总数，供右侧条形图渲染 |
| 请求路径 | /api/monitor/ward-distribution |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | monitor:dept:stat:read |

兼容路径：`/api/monitor/dept/stat`

### 12.4.2 请求入参

无业务入参。

### 12.4.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| list | array | 科室统计列表 |

list[] 字段：

| 字段 | 类型 | 说明 |
|---|---|---|
| deptName | string | 科室名称 |
| patientCount | long | 患者总数 |

### 12.4.4 请求示例

```http
GET /api/monitor/ward-distribution HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
```

### 12.4.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "list": [
      { "deptName": "冠心监护病区", "patientCount": 4 },
      { "deptName": "心内一病区", "patientCount": 3 }
    ]
  },
  "timestamp": 1776501256203
}
```

---

## 12.5 加入重点监护（状态更新）

### 12.5.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 加入重点监护 |
| 业务作用 | 将患者监护状态更新为重点监护 |
| 请求路径 | /api/monitor/opt/addKey |
| 请求方式 | POST |
| Content-Type | application/json |
| 权限码 | monitor:opt:addKey |

### 12.5.2 请求入参

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| patientId | long | 是 | 患者ID |
| monitorType | string | 是 | 监护类型 |

### 12.5.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| patientId | long | 患者ID |
| keyMonitor | boolean | 是否重点监护（固定 true） |
| monitorStatus | string | 监护状态文本（重点监护） |
| monitorType | string | 监护类型 |

### 12.5.4 请求示例

```http
POST /api/monitor/opt/addKey HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
Content-Type: application/json

{
  "patientId": 1904,
  "monitorType": "床旁重点监护"
}
```

### 12.5.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "patientId": 1904,
    "keyMonitor": true,
    "monitorStatus": "重点监护",
    "monitorType": "床旁重点监护"
  },
  "timestamp": 1776501256203
}
```

---

## 12.6 解除重点监护（状态回滚）

### 12.6.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 解除重点监护 |
| 业务作用 | 将患者监护状态回滚为普通监护 |
| 请求路径 | /api/monitor/opt/cancelKey |
| 请求方式 | POST |
| Content-Type | application/json |
| 权限码 | monitor:opt:cancelKey |

### 12.6.2 请求入参

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| patientId | long | 是 | 患者ID |

### 12.6.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| patientId | long | 患者ID |
| keyMonitor | boolean | 是否重点监护（固定 false） |
| monitorStatus | string | 监护状态文本（普通监护） |
| monitorType | string | 监护类型（普通监护） |

### 12.6.4 请求示例

```http
POST /api/monitor/opt/cancelKey HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
Content-Type: application/json

{
  "patientId": 1904
}
```

### 12.6.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "patientId": 1904,
    "keyMonitor": false,
    "monitorStatus": "普通监护",
    "monitorType": "普通监护"
  },
  "timestamp": 1776501256203
}
```

---

## 13. 实时监护业务规则

1. 顶部统计由采集记录、诊断报告、预警表联合计算，返回字段固定为 todayCollect、pendingAnalyse、pendingAudit、alertCount。
2. 实时监护大屏患者数据接口（/api/monitor/patients）仅查询普通监护数据（ecg_real_time_monitor.is_key_monitor = 0）；兼容路径 /api/monitor/realTime/list 支持 wardId、level 可选筛选。
3. 重点监护患者列表仅查询重点监护数据（ecg_real_time_monitor.is_key_monitor = 1）。
4. 列表状态按 monitor_status 映射：1=正常，2=预警；接口不返回离线态。
5. 加入重点监护与解除重点监护均为状态更新接口，通过 patientId 更新 is_key_monitor（1/0），实现页面卡片即时移入/移出闭环。
6. 加入重点监护时 monitorType 为必填，用于记录业务动作上下文与前端展示。
7. 列表接口返回 actionPermissions，前端按权限渲染“加入重点监护”或“解除重点监护”按钮。

## 14. 实时监护错误场景

| 场景 | code | message 示例 |
|---|---|---|
| wardId 为空或非法 | 400 | wardId 参数不合法 |
| level 传入非法值 | 400 | level 参数不合法 |
| patientId 为空或非法 | 400 | patientId 参数不合法 |
| monitorType 为空或非法 | 400 | monitorType 参数不合法 |
| 患者监护数据不存在 | 404 | 患者监护数据不存在 |
| 未登录或 token 失效 | 401 | 未认证或登录已失效 |
| 无权限访问 | 403 | 无权限访问 |
| 服务内部异常 | 5000 | 系统繁忙，请稍后重试 |

---

## 15. 实时监护设备管理接口规范

## 15.1 设备分页列表查询

### 15.1.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 设备分页列表查询 |
| 业务作用 | 渲染设备管理主表，支持名称、类型、病区、采购日期区间筛选 |
| 请求路径 | /api/monitor/device/page |
| 请求方式 | POST |
| Content-Type | application/json |
| 权限码 | monitor:device:list:read |

### 15.1.2 请求入参

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|---|---|---|---|---|
| deviceName | string | 否 | 空 | 设备名称/设备编码模糊搜索 |
| deviceType | string | 否 | 空 | 设备类型：1/2/3/4 或 对应中文文本 |
| ward | string | 否 | 空 | 病区筛选，支持病区名或病区ID字符串 |
| purchaseDateStart | string | 否 | 空 | 采购起始日期，格式 yyyy-MM-dd |
| purchaseDateEnd | string | 否 | 空 | 采购结束日期，格式 yyyy-MM-dd |
| pageNum | long | 否 | 1 | 页码，最小 1 |
| pageSize | long | 否 | 10 | 每页条数，最大 200 |

### 15.1.3 出参说明

#### data（分页）

| 字段 | 类型 | 说明 |
|---|---|---|
| pageNum | long | 当前页码 |
| pageSize | long | 每页条数 |
| total | long | 总记录数 |
| pages | long | 总页数 |
| records | array | 当前页设备数据 |

#### data.records[]（设备列表项）

| 字段 | 类型 | 说明 |
|---|---|---|
| deviceId | long | 设备ID |
| deviceCode | string | 设备编码 |
| deviceName | string | 设备名称 |
| deviceType | integer | 设备类型编码 |
| deviceTypeText | string | 设备类型文本 |
| deviceModel | string | 设备型号 |
| manufacturer | string | 厂商 |
| supplier | string | 供应商 |
| installDate | string | 安装日期 |
| wardId | long | 绑定病区ID |
| wardName | string | 绑定病区名称 |
| deviceStatus | integer | 设备状态编码 |
| deviceStatusText | string | 设备状态文本 |
| lastMaintainTime | string | 上次维护时间 |
| nextMaintainTime | string | 下次维护时间 |

### 15.1.4 请求示例

```http
POST /api/monitor/device/page HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
Content-Type: application/json

{
  "deviceName": "监护",
  "deviceType": "4",
  "ward": "1503",
  "purchaseDateStart": "2025-01-01",
  "purchaseDateEnd": "2026-12-31",
  "pageNum": 1,
  "pageSize": 10
}
```

### 15.1.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "pageNum": 1,
    "pageSize": 10,
    "total": 2,
    "pages": 1,
    "records": [
      {
        "deviceId": 1804,
        "deviceCode": "DEV-MON-004",
        "deviceName": "ICU多参数监护仪",
        "deviceType": 4,
        "deviceTypeText": "床边监护仪",
        "deviceModel": "MP-9800",
        "manufacturer": "深圳某医疗",
        "supplier": "华北医疗供应链",
        "installDate": "2026-03-15",
        "wardId": 1503,
        "wardName": "冠心监护病区",
        "deviceStatus": 1,
        "deviceStatusText": "正常",
        "lastMaintainTime": "2026-04-01",
        "nextMaintainTime": "2026-07-01"
      }
    ]
  },
  "timestamp": 1776501256203
}
```

---

## 15.2 设备筛选字典枚举

### 15.2.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 设备筛选字典枚举 |
| 业务作用 | 页面初始化渲染设备类型和病区下拉项 |
| 请求路径 | /api/monitor/device/dicts |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | monitor:device:dict:read |

### 15.2.2 请求入参

无业务入参。

### 15.2.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| deviceTypeOptions | array | 设备类型字典 |
| wardOptions | array | 病区字典 |

字典项结构：

| 字段 | 类型 | 说明 |
|---|---|---|
| value | string | 枚举值 |
| label | string | 展示文本 |

### 15.2.4 请求示例

```http
GET /api/monitor/device/dicts HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
```

### 15.2.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "deviceTypeOptions": [
      { "value": "", "label": "全部类型" },
      { "value": "1", "label": "静态心电设备" },
      { "value": "2", "label": "动态心电设备" },
      { "value": "3", "label": "居家监护设备" },
      { "value": "4", "label": "床边监护仪" }
    ],
    "wardOptions": [
      { "value": "", "label": "全部病区" },
      { "value": "1501", "label": "心内一病区" },
      { "value": "1503", "label": "冠心监护病区" }
    ]
  },
  "timestamp": 1776501256203
}
```

---

## 15.3 新增设备

### 15.3.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 新增设备 |
| 业务作用 | 新建设备主档 |
| 请求路径 | /api/monitor/device |
| 请求方式 | POST |
| Content-Type | application/json |
| 权限码 | monitor:device:create |

### 15.3.2 请求入参

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| deviceCode | string | 否 | 设备编码，不传则后端自动生成 |
| deviceName | string | 是 | 设备名称 |
| deviceType | integer | 是 | 设备类型：1-静态心电设备 2-动态心电设备 3-居家监护设备 4-床边监护仪 |
| deviceModel | string | 否 | 设备型号 |
| manufacturer | string | 否 | 生产厂商 |
| supplier | string | 否 | 供应商 |
| installDate | string | 否 | 安装日期，格式 yyyy-MM-dd |
| bindDeptId | long | 否 | 绑定病区ID |
| bindDeptName | string | 否 | 绑定病区名称（当 bindDeptId 为空时可作为兜底展示） |
| lastMaintainTime | string | 否 | 上次维护日期，格式 yyyy-MM-dd |
| nextMaintainTime | string | 否 | 下次维护日期，格式 yyyy-MM-dd |
| deviceStatus | integer | 否 | 设备状态，默认 1 |

### 15.3.3 出参说明

返回新增后的设备快照，字段与设备分页列表项一致。

### 15.3.4 请求示例

```http
POST /api/monitor/device HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
Content-Type: application/json

{
  "deviceName": "床旁心电监护仪-A01",
  "deviceType": 4,
  "deviceModel": "MP-9800",
  "manufacturer": "深圳某医疗",
  "supplier": "华北医疗供应链",
  "installDate": "2026-04-20",
  "bindDeptId": 1503,
  "lastMaintainTime": "2026-04-20",
  "nextMaintainTime": "2026-07-20",
  "deviceStatus": 1
}
```

### 15.3.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "deviceId": 1899,
    "deviceCode": "DEV-AUTO-1849238723412342784",
    "deviceName": "床旁心电监护仪-A01",
    "deviceType": 4,
    "deviceTypeText": "床边监护仪",
    "deviceModel": "MP-9800",
    "manufacturer": "深圳某医疗",
    "supplier": "华北医疗供应链",
    "installDate": "2026-04-20",
    "wardId": 1503,
    "wardName": "冠心监护病区",
    "deviceStatus": 1,
    "deviceStatusText": "正常",
    "lastMaintainTime": "2026-04-20",
    "nextMaintainTime": "2026-07-20"
  },
  "timestamp": 1776501256203
}
```

---

## 15.4 编辑设备

### 15.4.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 编辑设备 |
| 业务作用 | 更新设备主档信息 |
| 请求路径 | /api/monitor/device |
| 请求方式 | PUT |
| Content-Type | application/json |
| 权限码 | monitor:device:update |

### 15.4.2 请求入参

在新增设备入参基础上增加 `deviceId`（必填），其余字段与新增接口保持一致。

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| deviceId | long | 是 | 设备ID |

### 15.4.3 出参说明

返回更新后的设备快照，字段与新增设备返回一致。

### 15.4.4 请求示例

```http
PUT /api/monitor/device HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
Content-Type: application/json

{
  "deviceId": 1899,
  "deviceCode": "DEV-MON-899",
  "deviceName": "床旁心电监护仪-A01",
  "deviceType": 4,
  "deviceModel": "MP-9800 Pro",
  "manufacturer": "深圳某医疗",
  "supplier": "华北医疗供应链",
  "installDate": "2026-04-20",
  "bindDeptId": 1503,
  "lastMaintainTime": "2026-04-20",
  "nextMaintainTime": "2026-08-20",
  "deviceStatus": 1
}
```

### 15.4.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "deviceId": 1899,
    "deviceCode": "DEV-MON-899",
    "deviceName": "床旁心电监护仪-A01",
    "deviceType": 4,
    "deviceTypeText": "床边监护仪",
    "deviceModel": "MP-9800 Pro",
    "manufacturer": "深圳某医疗",
    "supplier": "华北医疗供应链",
    "installDate": "2026-04-20",
    "wardId": 1503,
    "wardName": "冠心监护病区",
    "deviceStatus": 1,
    "deviceStatusText": "正常",
    "lastMaintainTime": "2026-04-20",
    "nextMaintainTime": "2026-08-20"
  },
  "timestamp": 1776501256203
}
```

---

## 15.5 删除设备

### 15.5.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 删除设备 |
| 业务作用 | 逻辑删除设备（监护绑定中设备禁止删除） |
| 请求路径 | /api/monitor/device |
| 请求方式 | DELETE |
| Content-Type | application/json |
| 权限码 | monitor:device:delete |

### 15.5.2 请求入参

| 参数位置 | 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|---|
| Query | deviceId | long | 是 | 设备ID |

### 15.5.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| deviceId | long | 被删除设备ID |
| deleted | boolean | 是否删除成功 |

### 15.5.4 请求示例

```http
DELETE /api/monitor/device?deviceId=1899 HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
```

### 15.5.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "deviceId": 1899,
    "deleted": true
  },
  "timestamp": 1776501256203
}
```

---

## 15.6 设备使用详情查询

### 15.6.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 设备使用详情查询 |
| 业务作用 | 设备详情页展示基础信息、使用统计、科室标签、当前患者 |
| 请求路径 | /api/monitor/device/detail |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | monitor:device:detail:read |

### 15.6.2 请求入参

| 参数位置 | 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|---|
| Query | deviceId | long | 是 | 设备ID |

### 15.6.3 出参说明

#### data 顶层字段

| 字段 | 类型 | 说明 |
|---|---|---|
| basicInfo | object | 设备基础信息 |
| usageStat | object | 使用统计（今日/本周/本月、在线率、错误率） |
| deptTags | array | 设备关联科室标签 |
| currentPatients | array | 当前使用该设备的患者列表 |

#### data.basicInfo 字段

| 字段 | 类型 | 说明 |
|---|---|---|
| deviceId | long | 设备ID |
| deviceCode | string | 设备编码 |
| deviceName | string | 设备名称 |
| deviceType | integer | 设备类型编码 |
| deviceTypeText | string | 设备类型文本 |
| deviceModel | string | 设备型号 |
| manufacturer | string | 厂商 |
| deviceStatus | integer | 设备状态编码 |
| deviceStatusText | string | 设备状态文本 |
| wardId | long | 绑定病区ID |
| wardName | string | 绑定病区名称 |
| installDate | string | 安装日期 |

#### data.usageStat 字段

| 字段 | 类型 | 说明 |
|---|---|---|
| todayMeasureCount | long | 今日测量次数 |
| weekMeasureCount | long | 本周测量次数 |
| monthMeasureCount | long | 本月测量次数 |
| onlineRate | number | 在线率（0~100） |
| errorRate | number | 错误率（0~100） |

#### data.currentPatients[] 字段

| 字段 | 类型 | 说明 |
|---|---|---|
| patientId | long | 患者ID |
| patientName | string | 患者姓名 |
| inpatientNo | string | 住院号 |
| wardBed | string | 病区与床位 |
| heartRate | integer | 当前心率 |
| monitorStatus | string | 监护状态文本 |
| updateTime | string | 最近更新时间 |

### 15.6.4 请求示例

```http
GET /api/monitor/device/detail?deviceId=1804 HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
```

### 15.6.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "basicInfo": {
      "deviceId": 1804,
      "deviceCode": "DEV-MON-004",
      "deviceName": "ICU多参数监护仪",
      "deviceType": 4,
      "deviceTypeText": "床边监护仪",
      "deviceModel": "MP-9800",
      "manufacturer": "深圳某医疗",
      "deviceStatus": 1,
      "deviceStatusText": "正常",
      "wardId": 1503,
      "wardName": "冠心监护病区",
      "installDate": "2026-03-15"
    },
    "usageStat": {
      "todayMeasureCount": 12,
      "weekMeasureCount": 55,
      "monthMeasureCount": 213,
      "onlineRate": 96.50,
      "errorRate": 1.88
    },
    "deptTags": [
      "冠心监护病区",
      "心内二病区"
    ],
    "currentPatients": [
      {
        "patientId": 1904,
        "patientName": "陈敏",
        "inpatientNo": "ZY202604004",
        "wardBed": "冠心监护病区 | ICU-01",
        "heartRate": 128,
        "monitorStatus": "预警",
        "updateTime": "2026-04-18T11:58:00"
      }
    ]
  },
  "timestamp": 1776501256203
}
```

---

## 15.7 设备维护记录分页查询

### 15.7.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 设备维护记录分页查询 |
| 业务作用 | 设备维保记录分页展示 |
| 请求路径 | /api/monitor/device/maintain-records |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | monitor:device:maintain:read |

### 15.7.2 请求入参

| 参数位置 | 字段 | 类型 | 必填 | 默认值 | 说明 |
|---|---|---|---|---|---|
| Query | deviceId | long | 是 | - | 设备ID |
| Query | pageNum | long | 否 | 1 | 页码 |
| Query | pageSize | long | 否 | 10 | 每页条数，最大 200 |

### 15.7.3 出参说明

分页结构同通用分页。

data.records[] 字段：

| 字段 | 类型 | 说明 |
|---|---|---|
| maintainId | long | 维护记录ID |
| maintainType | string | 维护类型 |
| maintainUserName | string | 维护人 |
| maintainTime | string | 维护时间 |
| maintainContent | string | 维护内容 |
| maintainResult | string | 维护结果 |
| nextMaintainTime | string | 下次维护时间 |

### 15.7.4 请求示例

```http
GET /api/monitor/device/maintain-records?deviceId=1804&pageNum=1&pageSize=10 HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
```

### 15.7.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "pageNum": 1,
    "pageSize": 10,
    "total": 2,
    "pages": 1,
    "records": [
      {
        "maintainId": 2901,
        "maintainType": "常规保养",
        "maintainUserName": "王工",
        "maintainTime": "2026-04-01",
        "maintainContent": "电极接口检查与清洁",
        "maintainResult": "设备运行正常",
        "nextMaintainTime": "2026-07-01"
      }
    ]
  },
  "timestamp": 1776501256203
}
```

---

## 16. 实时监护设备管理业务规则

1. 设备删除采用逻辑删除（is_deleted = 1），不做物理删除。
2. 删除前必须校验实时监护绑定关系：存在 ecg_real_time_monitor.device_id = 目标设备ID 且未删除时，返回业务异常并禁止删除。
3. 设备编码要求全局唯一；新增时可不传，后端自动生成编码。
4. 病区绑定优先使用 bindDeptId 反查病区名称，保证设备主档与病区主数据一致。
5. 日期类字段（installDate、lastMaintainTime、nextMaintainTime、purchaseDateStart、purchaseDateEnd）统一使用 yyyy-MM-dd。
6. 维护时间约束：lastMaintainTime 不得晚于 nextMaintainTime。
7. 设备使用统计口径：
   - todayMeasureCount：当天采集次数；
   - weekMeasureCount：本周采集次数；
   - monthMeasureCount：本月采集次数；
   - onlineRate：实时监护表中在线状态占比；
   - errorRate：采集记录中 AI 分析失败占比。

## 17. 实时监护设备管理错误场景

| 场景 | code | message 示例 |
|---|---|---|
| deviceId 为空或非法 | 400 | deviceId 参数不合法 |
| deviceType 传入非法值 | 400 | deviceType 参数不合法 |
| deviceStatus 传入非法值 | 400 | deviceStatus 参数不合法 |
| 日期格式错误 | 400 | installDate 参数格式错误 |
| 采购日期区间非法 | 400 | purchaseDateStart 不能晚于 purchaseDateEnd |
| 设备编码重复 | 400 | deviceCode 已存在 |
| 病区ID非法 | 400 | bindDeptId 参数不合法 |
| 设备不存在 | 404 | 设备不存在 |
| 设备正在监护绑定中不可删 | 1000 | 设备已绑定监护中患者，禁止删除 |
| 未登录或 token 失效 | 401 | 未认证或登录已失效 |
| 无权限访问 | 403 | 无权限访问 |
| 服务内部异常 | 5000 | 系统繁忙，请稍后重试 |

---

## 18. 质控管理接口规范

## 18.1 质控记录分页查询

### 18.1.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 质控记录分页查询 |
| 业务作用 | 渲染质控记录列表，支持设备、测试类型、测试结果、设备状态、时间范围筛选 |
| 请求路径 | /api/monitor/quality-control/page |
| 请求方式 | POST |
| Content-Type | application/json |
| 权限码 | monitor:quality:list:read |

### 18.1.2 请求入参

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|---|---|---|---|---|
| deviceId | long | 否 | 空 | 设备ID |
| testType | string | 否 | 空 | 测试类型（日检/周检/月检/远程检测 或 1/2/3/4） |
| testResult | string | 否 | 空 | 测试结果（通过/未通过 或 1/2） |
| deviceStatus | string | 否 | 空 | 设备状态（正常/异常 或 1/2） |
| startTime | string | 否 | 空 | 起始时间，支持 yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss |
| endTime | string | 否 | 空 | 结束时间，支持 yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss |
| pageNum | long | 否 | 1 | 页码 |
| pageSize | long | 否 | 10 | 每页条数，最大 200 |

### 18.1.3 出参说明

分页结构同通用分页。

data.records[] 字段：

| 字段 | 类型 | 说明 |
|---|---|---|
| qcId | long | 质控记录ID |
| qcNo | string | 质控单号（示例：QC2804） |
| deviceId | long | 设备ID |
| deviceName | string | 设备名称 |
| deptId | long | 病区ID |
| deptName | string | 病区名称 |
| testTime | string | 测试时间 |
| testType | string | 测试类型（日检/周检/月检/远程检测） |
| testUserId | long | 测试人员ID |
| testUserName | string | 测试人员姓名 |
| deviceStatus | string | 设备状态（正常/异常） |
| testResult | string | 测试结果（通过/未通过） |
| remark | string | 备注 |
| createTime | string | 创建时间 |
| updateTime | string | 更新时间 |
| actionPermissions | array | 操作按钮权限（detail/update/delete） |

### 18.1.4 请求示例

```http
POST /api/monitor/quality-control/page HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
Content-Type: application/json

{
  "deviceId": 1804,
  "testType": "周检",
  "testResult": "未通过",
  "deviceStatus": "异常",
  "startTime": "2026-04-18",
  "endTime": "2026-04-18",
  "pageNum": 1,
  "pageSize": 10
}
```

### 18.1.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "pageNum": 1,
    "pageSize": 10,
    "total": 1,
    "pages": 1,
    "records": [
      {
        "qcId": 2804,
        "qcNo": "QC2804",
        "deviceId": 1804,
        "deviceName": "ICU多参数监护仪",
        "deptId": 1203,
        "deptName": "心血管内科二病区",
        "testTime": "2026-04-18T07:45:00",
        "testType": "周检",
        "testUserId": 1305,
        "testUserName": "吴工程师",
        "deviceStatus": "异常",
        "testResult": "未通过",
        "remark": "已记录维修工单，待复检",
        "createTime": "2026-04-18T07:45:00",
        "updateTime": "2026-04-18T07:45:00",
        "actionPermissions": [
          "monitor:quality:detail",
          "monitor:quality:update",
          "monitor:quality:delete"
        ]
      }
    ]
  },
  "timestamp": 1776501256203
}
```

---

## 18.2 质控公共字典枚举

### 18.2.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 质控公共字典枚举 |
| 业务作用 | 页面初始化渲染测试类型、设备状态、测试结果字典 |
| 请求路径 | /api/monitor/quality-control/dicts |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | monitor:quality:dict:read |

### 18.2.2 请求入参

无业务入参。

### 18.2.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| testTypeOptions | array | 测试类型字典 |
| deviceStatusOptions | array | 设备状态字典 |
| deviceOptions | array | 设备下拉字典 |
| testResultOptions | array | 测试结果字典 |
| testStatusOptions | array | 兼容字段，等同 testResultOptions |

字典项结构：

| 字段 | 类型 | 说明 |
|---|---|---|
| value | string | 枚举值 |
| label | string | 展示文本 |

### 18.2.4 请求示例

```http
GET /api/monitor/quality-control/dicts HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
```

### 18.2.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "testTypeOptions": [
      { "value": "", "label": "全部类型" },
      { "value": "1", "label": "日检" },
      { "value": "2", "label": "周检" },
      { "value": "3", "label": "月检" },
      { "value": "4", "label": "远程检测" }
    ],
    "deviceStatusOptions": [
      { "value": "", "label": "全部状态" },
      { "value": "1", "label": "正常" },
      { "value": "2", "label": "异常" }
    ],
    "deviceOptions": [
      { "value": "", "label": "全部设备" },
      { "value": "1804", "label": "ICU多参数监护仪" },
      { "value": "1899", "label": "床旁心电监护仪-A01" }
    ],
    "testResultOptions": [
      { "value": "", "label": "全部结果" },
      { "value": "1", "label": "通过" },
      { "value": "2", "label": "未通过" }
    ],
    "testStatusOptions": [
      { "value": "", "label": "全部结果" },
      { "value": "1", "label": "通过" },
      { "value": "2", "label": "未通过" }
    ]
  },
  "timestamp": 1776501256203
}
```

---

## 18.3 新增质控记录

### 18.3.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 新增质控记录 |
| 业务作用 | 新增单条设备质控记录，自动固化设备与病区快照 |
| 请求路径 | /api/monitor/quality-control |
| 请求方式 | POST |
| Content-Type | application/json |
| 权限码 | monitor:quality:create |

### 18.3.2 请求入参

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| deviceId | long | 是 | 设备ID |
| testType | string | 是 | 测试类型（日检/周检/月检/远程检测 或 1/2/3/4） |
| testUserId | long | 否 | 测试人员ID |
| testUserName | string | 否 | 测试人员姓名（当 testUserId 为空时必填） |
| testResult | string | 是 | 测试结果（通过/未通过 或 1/2） |
| deviceStatus | string | 是 | 设备状态（正常/异常 或 1/2） |
| testTime | string | 否 | 测试时间，支持 yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss；为空默认当前时间 |
| remark | string | 否 | 备注，最大 256 字符 |

### 18.3.3 出参说明

返回质控记录详情对象，字段同 18.5。

### 18.3.4 请求示例

```http
POST /api/monitor/quality-control HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
Content-Type: application/json

{
  "deviceId": 1804,
  "testType": "周检",
  "testUserId": 1305,
  "testResult": "未通过",
  "deviceStatus": "异常",
  "testTime": "2026-04-19 08:10:00",
  "remark": "电极接口松动，已提维修"
}
```

### 18.3.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "qcId": 2980000000001,
    "deviceId": 1804,
    "deviceName": "ICU多参数监护仪",
    "deptId": 1503,
    "deptName": "冠心监护病区",
    "testTime": "2026-04-19T08:10:00",
    "testType": "周检",
    "testUserId": 1305,
    "testUserName": "吴工程师",
    "deviceStatus": "异常",
    "testResult": "未通过",
    "remark": "电极接口松动，已提维修",
    "createTime": "2026-04-19T08:10:00",
    "updateTime": "2026-04-19T08:10:00",
    "indicatorDetails": [
      { "indicatorCode": "device_status", "indicatorName": "设备状态", "indicatorValue": "异常", "result": "异常" },
      { "indicatorCode": "test_result", "indicatorName": "测试结果", "indicatorValue": "未通过", "result": "未通过" }
    ]
  },
  "timestamp": 1776501256203
}
```

---

## 18.4 编辑质控记录

### 18.4.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 编辑质控记录 |
| 业务作用 | 修改质控记录字段，保留历史记录主键不变 |
| 请求路径 | /api/monitor/quality-control |
| 请求方式 | PUT |
| Content-Type | application/json |
| 权限码 | monitor:quality:update |

### 18.4.2 请求入参

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| qcId | long | 是 | 质控记录ID |
| deviceId | long | 否 | 设备ID |
| testType | string | 否 | 测试类型（日检/周检/月检/远程检测 或 1/2/3/4） |
| testUserId | long | 否 | 测试人员ID |
| testUserName | string | 否 | 测试人员姓名 |
| testResult | string | 否 | 测试结果（通过/未通过 或 1/2） |
| deviceStatus | string | 否 | 设备状态（正常/异常 或 1/2） |
| testTime | string | 否 | 测试时间，支持 yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss |
| remark | string | 否 | 备注，最大 256 字符 |

### 18.4.3 出参说明

返回质控记录详情对象，字段同 18.5。

### 18.4.4 请求示例

```http
PUT /api/monitor/quality-control HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
Content-Type: application/json

{
  "qcId": 2804,
  "testResult": "通过",
  "deviceStatus": "正常",
  "remark": "复检通过，恢复上线"
}
```

### 18.4.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "qcId": 2804,
    "deviceId": 1804,
    "deviceName": "ICU多参数监护仪",
    "deptId": 1203,
    "deptName": "心血管内科二病区",
    "testTime": "2026-04-18T07:45:00",
    "testType": "周检",
    "testUserId": 1305,
    "testUserName": "吴工程师",
    "deviceStatus": "正常",
    "testResult": "通过",
    "remark": "复检通过，恢复上线",
    "createTime": "2026-04-18T07:45:00",
    "updateTime": "2026-04-19T09:15:00",
    "indicatorDetails": [
      { "indicatorCode": "device_status", "indicatorName": "设备状态", "indicatorValue": "正常", "result": "达标" },
      { "indicatorCode": "test_result", "indicatorName": "测试结果", "indicatorValue": "通过", "result": "通过" }
    ]
  },
  "timestamp": 1776501256203
}
```

---

## 18.5 质控记录详情查询

### 18.5.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 质控记录详情查询 |
| 业务作用 | 查询单条质控完整档案，含测试指标明细 |
| 请求路径 | /api/monitor/quality-control/detail |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | monitor:quality:detail:read |

### 18.5.2 请求入参

| 参数位置 | 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|---|
| Query | qcId | long | 是 | 质控记录ID |

### 18.5.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| qcId | long | 质控记录ID |
| qcNo | string | 质控单号（示例：QC2804） |
| deviceId | long | 设备ID |
| deviceName | string | 设备名称 |
| deptId | long | 病区ID |
| deptName | string | 病区名称 |
| testTime | string | 测试时间 |
| testType | string | 测试类型 |
| testUserId | long | 测试人员ID |
| testUserName | string | 测试人员姓名 |
| deviceStatus | string | 设备状态 |
| testResult | string | 测试结果 |
| remark | string | 备注 |
| createTime | string | 创建时间 |
| updateTime | string | 更新时间 |
| deviceInfo | object | 设备信息快照 |
| testerInfo | object | 测试人员快照 |
| qualityParams | object | 全部质控参数 |
| timeSnapshot | object | 时间快照 |
| indicatorDetails | array | 测试指标明细 |

indicatorDetails[] 字段：

| 字段 | 类型 | 说明 |
|---|---|---|
| indicatorCode | string | 指标编码 |
| indicatorName | string | 指标名称 |
| indicatorValue | string | 指标值 |
| result | string | 指标结果 |

deviceInfo 字段：

| 字段 | 类型 | 说明 |
|---|---|---|
| deviceId | long | 设备ID |
| deviceName | string | 设备名称 |
| deptId | long | 科室ID |
| deptName | string | 科室名称 |
| deviceStatus | string | 设备状态 |

testerInfo 字段：

| 字段 | 类型 | 说明 |
|---|---|---|
| testUserId | long | 测试人员ID |
| testUserName | string | 测试人员姓名 |

qualityParams 字段：

| 字段 | 类型 | 说明 |
|---|---|---|
| testType | string | 测试类型 |
| deviceStatus | string | 设备状态 |
| testResult | string | 测试结果 |

timeSnapshot 字段：

| 字段 | 类型 | 说明 |
|---|---|---|
| testTime | string | 测试时间 |
| createTime | string | 创建时间 |
| updateTime | string | 更新时间 |

### 18.5.4 请求示例

```http
GET /api/monitor/quality-control/detail?qcId=2804 HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
```

### 18.5.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "qcId": 2804,
    "qcNo": "QC2804",
    "deviceId": 1804,
    "deviceName": "ICU多参数监护仪",
    "deptId": 1203,
    "deptName": "心血管内科二病区",
    "testTime": "2026-04-18T07:45:00",
    "testType": "周检",
    "testUserId": 1305,
    "testUserName": "吴工程师",
    "deviceStatus": "异常",
    "testResult": "未通过",
    "remark": "已记录维修工单，待复检",
    "createTime": "2026-04-18T07:45:00",
    "updateTime": "2026-04-18T07:45:00",
    "deviceInfo": {
      "deviceId": 1804,
      "deviceName": "ICU多参数监护仪",
      "deptId": 1203,
      "deptName": "心血管内科二病区",
      "deviceStatus": "异常"
    },
    "testerInfo": {
      "testUserId": 1305,
      "testUserName": "吴工程师"
    },
    "qualityParams": {
      "testType": "周检",
      "deviceStatus": "异常",
      "testResult": "未通过"
    },
    "timeSnapshot": {
      "testTime": "2026-04-18T07:45:00",
      "createTime": "2026-04-18T07:45:00",
      "updateTime": "2026-04-18T07:45:00"
    },
    "indicatorDetails": [
      { "indicatorCode": "qc_no", "indicatorName": "质控单号", "indicatorValue": "QC2804", "result": "已生成" },
      { "indicatorCode": "device_name", "indicatorName": "设备名称", "indicatorValue": "ICU多参数监护仪", "result": "已绑定" },
      { "indicatorCode": "dept_name", "indicatorName": "所属科室", "indicatorValue": "心血管内科二病区", "result": "已归属" },
      { "indicatorCode": "test_user", "indicatorName": "测试人员", "indicatorValue": "吴工程师", "result": "已记录" },
      { "indicatorCode": "device_status", "indicatorName": "设备状态", "indicatorValue": "异常", "result": "异常" },
      { "indicatorCode": "test_result", "indicatorName": "测试结果", "indicatorValue": "未通过", "result": "未通过" },
      { "indicatorCode": "test_type", "indicatorName": "测试类型", "indicatorValue": "周检", "result": "已执行" },
      { "indicatorCode": "test_time", "indicatorName": "测试时间", "indicatorValue": "2026-04-18 07:45:00", "result": "已记录" }
    ]
  },
  "timestamp": 1776501256203
}
```

---

## 18.6 删除质控记录

### 18.6.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 删除质控记录 |
| 业务作用 | 删除单条质控记录（逻辑删除） |
| 请求路径 | /api/monitor/quality-control |
| 请求方式 | DELETE |
| Content-Type | application/json |
| 权限码 | monitor:quality:delete |

### 18.6.2 请求入参

| 参数位置 | 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|---|
| Query | qcId | long | 是 | 质控记录ID |

### 18.6.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| qcId | long | 删除的质控记录ID |
| deleted | boolean | 是否删除成功（固定 true） |

### 18.6.4 请求示例

```http
DELETE /api/monitor/quality-control?qcId=2804 HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
```

### 18.6.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "qcId": 2804,
    "deleted": true
  },
  "timestamp": 1776501256203
}
```

---

## 18.7 设备下拉列表

### 18.7.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 设备下拉列表 |
| 业务作用 | 返回所有正常状态设备下拉选项（设备ID + 设备名称） |
| 请求路径 | /api/monitor/quality-control/device-options |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | monitor:quality:device-options:read |

### 18.7.2 请求入参

无业务入参。

### 18.7.3 出参说明

data 为数组，元素字段：

| 字段 | 类型 | 说明 |
|---|---|---|
| value | string | 设备ID |
| label | string | 设备名称 |

### 18.7.4 请求示例

```http
GET /api/monitor/quality-control/device-options HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
```

### 18.7.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": [
    {
      "value": "1801",
      "label": "心电图机 ECG-2000"
    },
    {
      "value": "1804",
      "label": "ICU多参数监护仪"
    }
  ],
  "timestamp": 1776501256203
}
```

---

## 18.8 测试人员下拉

### 18.8.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 测试人员下拉 |
| 业务作用 | 页面“测试工号”下拉选择，返回启用状态系统用户 |
| 请求路径 | /api/sys/user/testUserOptions |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | system:user:options:read |

### 18.8.2 请求入参

无业务入参。

### 18.8.3 出参说明

data 为数组，元素字段：

| 字段 | 类型 | 说明 |
|---|---|---|
| userId | long | 用户ID |
| userName | string | 工号/账号 |
| realName | string | 姓名 |

### 18.8.4 请求示例

```http
GET /api/sys/user/testUserOptions HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
```

### 18.8.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": [
    {
      "userId": 1001,
      "userName": "ADMIN001",
      "realName": "系统管理员"
    },
    {
      "userId": 1002,
      "userName": "TEST002",
      "realName": "李医生"
    }
  ],
  "timestamp": 1776501256203
}
```

---

## 19. 质控管理业务规则

1. 质控记录采用逻辑删除模型（is_deleted），提供新增、编辑、查询、删除接口；删除后记录不再参与查询。
2. 新增/编辑时必须校验 deviceId 存在且未删除；deviceName、deptId、deptName 统一由设备主档回填，防止前端脏写。
3. testType 支持中文枚举与数字编码双输入，统一落库为中文值（日检/周检/月检/远程检测）。
4. deviceStatus 与 testResult 支持中文枚举与数字编码双输入，统一落库为中文值（正常/异常、通过/未通过）。
5. testUserId 传入时，测试人员姓名优先以系统用户表反查结果为准；testUserId 不传时，testUserName 必填。
6. testTime 支持日期与日期时间两种输入，传日期时自动补齐到当天起始或结束时间用于查询边界。
7. 设备下拉接口仅返回 ecg_device.device_status=1 且 is_deleted=0 的设备数据。
8. 测试人员下拉接口仅返回 sys_user.status=1 且 is_deleted=0 的用户数据。
9. remark 最大长度 256，超过长度返回 400。
10. 详情接口返回 indicatorDetails + qualityParams + timeSnapshot，保证前端可直接渲染“完整档案 + 指标明细 + 时间快照”。

## 20. 质控管理错误场景

| 场景 | code | message 示例 |
|---|---|---|
| qcId 为空或非法 | 400 | qcId 参数不合法 |
| deviceId 为空或非法 | 400 | deviceId 参数不合法 |
| testType 传入非法值 | 400 | testType 参数不合法 |
| deviceStatus 传入非法值 | 400 | deviceStatus 参数不合法 |
| testResult 传入非法值 | 400 | testResult 参数不合法 |
| testUserId 传入非法值 | 400 | testUserId 参数不合法 |
| testUserName 缺失 | 400 | testUserName 参数不合法 |
| testTime/startTime/endTime 格式错误 | 400 | testTime 参数格式错误 |
| 时间区间非法 | 400 | startTime 不能晚于 endTime |
| remark 超过长度限制 | 400 | remark 长度不能超过 256 |
| 质控记录不存在 | 404 | 质控记录不存在 |
| 未登录或 token 失效 | 401 | 未认证或登录已失效 |
| 无权限访问 | 403 | 无权限访问 |
| 服务内部异常 | 5000 | 系统繁忙，请稍后重试 |

---

## 21. 数据分析模块接口规范

## 21.1 核心大盘指标接口

### 21.1.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 核心大盘指标 |
| 业务作用 | 统计大盘核心指标，支持时间周期与病区过滤 |
| 请求路径 | /api/analysis/core-metrics |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | analysis:dashboard:read |

### 21.1.2 请求入参

| 参数位置 | 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|---|
| Query | timeType | string | 否 | 时间周期：日/周/月 或 DAY/WEEK/MONTH |
| Query | wardId | long | 否 | 病区ID |
| Query | startDate | string | 否 | 起始日期，格式 yyyy-MM-dd |
| Query | endDate | string | 否 | 结束日期，格式 yyyy-MM-dd |

说明：

1. startDate/endDate 传入时优先使用日期区间过滤。
2. 未传日期区间时，按 timeType 计算时间范围；timeType 与日期都不传时统计全量数据。

### 21.1.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| totalMeasureCount | long | 心电总测量次数 |
| normalMeasureCount | long | 正常心电数量 |
| abnormalMeasureCount | long | 异常心电数量 |
| warningTotalCount | long | 预警总数 |
| warningHandledCount | long | 已处理预警数量 |
| avgWarningHandleMinutes | number | 平均处理耗时（分钟） |
| reportTotalCount | long | 报告总数 |
| reportAuditedCount | long | 已审核报告数量 |
| avgReportAuditMinutes | number | 平均审核耗时（分钟） |
| aiAccuracyRate | number | AI诊断准确率（以AI置信度均值口径统计） |
| aiSampleTotal | long | AI诊断总样本量 |

### 21.1.4 请求示例

```http
GET /api/analysis/core-metrics?timeType=MONTH&wardId=1503&startDate=2026-04-01&endDate=2026-04-30 HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
```

### 21.1.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "totalMeasureCount": 5,
    "normalMeasureCount": 1,
    "abnormalMeasureCount": 4,
    "warningTotalCount": 5,
    "warningHandledCount": 1,
    "avgWarningHandleMinutes": 19.00,
    "reportTotalCount": 5,
    "reportAuditedCount": 4,
    "avgReportAuditMinutes": 22.75,
    "aiAccuracyRate": 91.72,
    "aiSampleTotal": 5
  },
  "timestamp": 1776501256203
}
```

---

## 21.2 病区心电测量统计接口

### 21.2.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 病区心电测量统计 |
| 业务作用 | 返回各病区正常/异常心电数量，用于渲染对比条形图 |
| 请求路径 | /api/analysis/ward-ecg-stats |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | analysis:ward:read |

### 21.2.2 请求入参

无业务入参。

### 21.2.3 出参说明

data 为数组，字段说明如下：

| 字段 | 类型 | 说明 |
|---|---|---|
| wardId | long | 病区ID |
| wardName | string | 病区名称 |
| normalCount | long | 正常心电数量 |
| abnormalCount | long | 异常心电数量 |

### 21.2.4 请求示例

```http
GET /api/analysis/ward-ecg-stats HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
```

### 21.2.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": [
    { "wardId": 1501, "wardName": "心内一病区", "normalCount": 1, "abnormalCount": 1 },
    { "wardId": 1502, "wardName": "心内二病区", "normalCount": 0, "abnormalCount": 1 },
    { "wardId": 1503, "wardName": "冠心监护病区", "normalCount": 0, "abnormalCount": 1 }
  ],
  "timestamp": 1776501256203
}
```

---

## 21.3 预警维度统计接口

### 21.3.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 预警维度统计 |
| 业务作用 | 返回预警级别与预警类型两组统计 |
| 请求路径 | /api/analysis/warning-dimensions |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | analysis:warning:read |

### 21.3.2 请求入参

无业务入参。

### 21.3.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| levelStats | array | 预警级别分组统计（高危/中危/低危） |
| typeStats | array | 预警类型分组统计（房颤、早搏、心动过速等） |

levelStats[] 字段：

| 字段 | 类型 | 说明 |
|---|---|---|
| level | string | 预警级别 |
| count | long | 数量 |

typeStats[] 字段：

| 字段 | 类型 | 说明 |
|---|---|---|
| warningType | string | 预警类型 |
| count | long | 数量 |

### 21.3.4 请求示例

```http
GET /api/analysis/warning-dimensions HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
```

### 21.3.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "levelStats": [
      { "level": "高危", "count": 1 },
      { "level": "中危", "count": 2 },
      { "level": "低危", "count": 2 }
    ],
    "typeStats": [
      { "warningType": "房颤发作倾向", "count": 1 },
      { "warningType": "室性心动过速风险", "count": 1 },
      { "warningType": "室性早搏增多", "count": 1 }
    ]
  },
  "timestamp": 1776501256203
}
```

---

## 21.4 报告与设备统计接口

### 21.4.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 报告与设备统计 |
| 业务作用 | 返回报告状态统计与设备使用维度统计 |
| 请求路径 | /api/analysis/report-device-stats |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | analysis:report-device:read |

### 21.4.2 请求入参

无业务入参。

### 21.4.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| reportStatusStats | array | 报告状态统计（草稿、待审核、已完成） |
| deviceUsageStats | array | 设备使用维度统计（在线、在用、空闲、离线） |

reportStatusStats[] 字段：

| 字段 | 类型 | 说明 |
|---|---|---|
| status | string | 报告状态名称 |
| count | long | 数量 |

deviceUsageStats[] 字段：

| 字段 | 类型 | 说明 |
|---|---|---|
| dimension | string | 设备维度名称 |
| count | long | 数量 |

### 21.4.4 请求示例

```http
GET /api/analysis/report-device-stats HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
```

### 21.4.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "reportStatusStats": [
      { "status": "草稿", "count": 0 },
      { "status": "待审核", "count": 1 },
      { "status": "已完成", "count": 4 }
    ],
    "deviceUsageStats": [
      { "dimension": "在线", "count": 5 },
      { "dimension": "在用", "count": 5 },
      { "dimension": "空闲", "count": 0 },
      { "dimension": "离线", "count": 0 }
    ]
  },
  "timestamp": 1776501256203
}
```

---

## 21.5 数据分析筛选下拉字典接口

### 21.5.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 数据分析筛选字典 |
| 业务作用 | 页面初始化渲染病区下拉与时间周期枚举 |
| 请求路径 | /api/analysis/dicts |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | analysis:dict:read |

### 21.5.2 请求入参

无业务入参。

### 21.5.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| wardOptions | array | 病区下拉字典 |
| timeTypeOptions | array | 时间周期字典（日/周/月） |

字典项结构：

| 字段 | 类型 | 说明 |
|---|---|---|
| value | string | 枚举值 |
| label | string | 展示文本 |

### 21.5.4 请求示例

```http
GET /api/analysis/dicts HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
```

### 21.5.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "wardOptions": [
      { "value": "", "label": "全部病区" },
      { "value": "1501", "label": "心内一病区" },
      { "value": "1502", "label": "心内二病区" }
    ],
    "timeTypeOptions": [
      { "value": "", "label": "全部周期" },
      { "value": "DAY", "label": "日" },
      { "value": "WEEK", "label": "周" },
      { "value": "MONTH", "label": "月" }
    ]
  },
  "timestamp": 1776501256203
}
```

---

## 22. 数据分析模块业务规则

1. 核心大盘指标的时间过滤优先级为：startDate/endDate > timeType > 全量。
2. timeType 支持中文与英文双输入（日/周/月 或 DAY/WEEK/MONTH）。
3. 平均处理耗时与平均审核耗时统一按分钟返回，保留两位小数。
4. 报告“已完成”口径为 report_status in (2,3,4)。
5. 设备“空闲”口径为：设备总数 - 在用设备数，最小值为 0。
6. AI准确率按 AI 诊断置信度均值统计，total 样本量来自 AI 诊断记录数量。

## 23. 数据分析模块错误场景

| 场景 | code | message 示例 |
|---|---|---|
| timeType 传入非法值 | 400 | timeType 参数不合法 |
| wardId 非法 | 400 | wardId 参数不合法 |
| 日期格式错误 | 400 | startDate 参数格式错误 |
| 日期区间非法 | 400 | startDate 不能晚于 endDate |
| 未登录或 token 失效 | 401 | 未认证或登录已失效 |
| 无权限访问 | 403 | 无权限访问 |
| 服务内部异常 | 5000 | 系统繁忙，请稍后重试 |

---

## 24. 用户管理模块接口规范

## 24.1 用户分页列表查询接口

### 24.1.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 用户分页列表查询 |
| 业务作用 | 分页查询系统用户，支持关键字、角色、科室、状态筛选 |
| 请求路径 | /api/system/user/page |
| 请求方式 | POST |
| Content-Type | application/json |
| 权限码 | system:user:list |

### 24.1.2 请求入参

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|---|---|---|---|---|
| keyword | string | 否 | 空 | 账号/姓名/手机号模糊检索 |
| roleId | long | 否 | 空 | 角色 ID |
| deptId | long | 否 | 空 | 科室 ID |
| status | string | 否 | 空 | 账号状态，支持 启用/禁用 或 1/0 |
| pageNum | long | 否 | 1 | 页码，最小 1 |
| pageSize | long | 否 | 10 | 每页条数，最大 200 |

### 24.1.3 出参说明

#### data（分页信息）

| 字段 | 类型 | 说明 |
|---|---|---|
| pageNum | long | 当前页码 |
| pageSize | long | 每页条数 |
| total | long | 总记录数 |
| pages | long | 总页数 |
| records | array | 当前页数据 |

#### data.records[]（用户列表项）

| 字段 | 类型 | 说明 |
|---|---|---|
| userId | long | 用户 ID |
| userName | string | 登录账号 |
| realName | string | 姓名 |
| roleId | long | 角色 ID |
| roleName | string | 角色名称 |
| deptId | long | 科室 ID |
| deptName | string | 科室名称 |
| phone | string | 手机号 |
| email | string | 邮箱 |
| status | integer | 状态编码（1 启用 / 0 禁用） |
| statusText | string | 状态文本 |
| lastLoginTime | string | 最后登录时间（ISO-8601） |
| createTime | string | 创建时间（ISO-8601） |

### 24.1.4 请求示例

```http
POST /api/system/user/page HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
Content-Type: application/json

{
  "keyword": "chen",
  "roleId": 1003,
  "deptId": 1202,
  "status": "1",
  "pageNum": 1,
  "pageSize": 10
}
```

### 24.1.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "pageNum": 1,
    "pageSize": 10,
    "total": 1,
    "pages": 1,
    "records": [
      {
        "userId": 1303,
        "userName": "chen_xin",
        "realName": "陈医生",
        "roleId": 1003,
        "roleName": "心内科医生",
        "deptId": 1202,
        "deptName": "心血管内科一病区",
        "phone": "13800000003",
        "email": "chen.xin@hospital.local",
        "status": 1,
        "statusText": "启用",
        "lastLoginTime": null,
        "createTime": "2026-04-18T00:00:00"
      }
    ]
  },
  "timestamp": 1776501256203
}
```

---

## 24.2 公共筛选字典接口

### 24.2.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 用户筛选字典 |
| 业务作用 | 用户管理页面初始化下拉字典（角色/科室/状态） |
| 请求路径 | /api/system/user/dicts |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | system:user:dict |

### 24.2.2 请求入参

无业务入参。

### 24.2.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| roleOptions | array | 角色下拉 |
| departmentOptions | array | 科室下拉 |
| statusOptions | array | 状态下拉 |

字典项结构：

| 字段 | 类型 | 说明 |
|---|---|---|
| value | string | 字典值 |
| label | string | 字典展示文本 |

### 24.2.4 请求示例

```http
GET /api/system/user/dicts HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
```

### 24.2.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "roleOptions": [
      { "value": "", "label": "全部角色" },
      { "value": "1001", "label": "系统管理员" },
      { "value": "1003", "label": "心内科医生" }
    ],
    "departmentOptions": [
      { "value": "", "label": "全部科室" },
      { "value": "1201", "label": "心血管内科" },
      { "value": "1202", "label": "心血管内科一病区" }
    ],
    "statusOptions": [
      { "value": "", "label": "全部状态" },
      { "value": "1", "label": "启用" },
      { "value": "0", "label": "禁用" }
    ]
  },
  "timestamp": 1776501256203
}
```

---

## 24.3 新增用户接口

### 24.3.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 新增用户 |
| 业务作用 | 新建系统账号并保存角色、科室冗余快照 |
| 请求路径 | /api/system/user |
| 请求方式 | POST |
| Content-Type | application/json |
| 权限码 | system:user:create |

### 24.3.2 请求入参

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| userName | string | 是 | 登录账号，3-32 位字母/数字/下划线 |
| realName | string | 是 | 用户真实姓名 |
| password | string | 否 | 密码，6-32 位；不传默认重置为 123456 |
| roleId | long | 是 | 角色 ID |
| deptId | long | 否 | 科室 ID |
| phone | string | 否 | 手机号，11 位大陆手机号 |
| email | string | 否 | 邮箱 |
| status | integer | 否 | 状态：1-启用，0-禁用；默认 1 |

### 24.3.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| userId | long | 用户 ID |
| userName | string | 登录账号 |
| realName | string | 用户姓名 |
| roleId | long | 角色 ID |
| roleName | string | 角色名称 |
| deptId | long | 科室 ID |
| deptName | string | 科室名称 |
| phone | string | 手机号 |
| email | string | 邮箱 |
| status | integer | 状态编码 |
| statusText | string | 状态文本 |

### 24.3.4 请求示例

```http
POST /api/system/user HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
Content-Type: application/json

{
  "userName": "li_wei",
  "realName": "李医生",
  "password": "Pwd@123456",
  "roleId": 1003,
  "deptId": 1202,
  "phone": "13800138000",
  "email": "li.wei@hospital.local",
  "status": 1
}
```

### 24.3.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "userId": 1933887801123456789,
    "userName": "li_wei",
    "realName": "李医生",
    "roleId": 1003,
    "roleName": "心内科医生",
    "deptId": 1202,
    "deptName": "心血管内科一病区",
    "phone": "13800138000",
    "email": "li.wei@hospital.local",
    "status": 1,
    "statusText": "启用"
  },
  "timestamp": 1776501256203
}
```

---

## 24.4 查询用户单条详情接口

### 24.4.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 用户详情查询 |
| 业务作用 | 查询用户完整档案信息 |
| 请求路径 | /api/system/user/detail |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | system:user:detail |

### 24.4.2 请求入参

| 参数位置 | 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|---|
| Query | userId | long | 是 | 用户 ID |

### 24.4.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| userId | long | 用户 ID |
| userName | string | 登录账号 |
| realName | string | 姓名 |
| roleId | long | 角色 ID |
| roleName | string | 角色名称 |
| deptId | long | 科室 ID |
| deptName | string | 科室名称 |
| phone | string | 手机号 |
| email | string | 邮箱 |
| status | integer | 状态编码 |
| statusText | string | 状态文本 |
| lastLoginTime | string | 最后登录时间（ISO-8601） |
| createTime | string | 创建时间（ISO-8601） |
| updateTime | string | 更新时间（ISO-8601） |
| createUserId | long | 创建人 ID |

### 24.4.4 请求示例

```http
GET /api/system/user/detail?userId=1303 HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
```

### 24.4.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "userId": 1303,
    "userName": "chen_xin",
    "realName": "陈医生",
    "roleId": 1003,
    "roleName": "心内科医生",
    "deptId": 1202,
    "deptName": "心血管内科一病区",
    "phone": "13800000003",
    "email": "chen.xin@hospital.local",
    "status": 1,
    "statusText": "启用",
    "lastLoginTime": null,
    "createTime": "2026-04-18T00:00:00",
    "updateTime": "2026-04-18T00:00:00",
    "createUserId": 1301
  },
  "timestamp": 1776501256203
}
```

---

## 24.5 编辑用户信息接口

### 24.5.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 编辑用户信息 |
| 业务作用 | 更新用户基本资料、角色、科室和状态 |
| 请求路径 | /api/system/user |
| 请求方式 | PUT |
| Content-Type | application/json |
| 权限码 | system:user:update |

### 24.5.2 请求入参

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| userId | long | 是 | 用户 ID |
| userName | string | 否 | 登录账号（修改时需保证唯一） |
| realName | string | 否 | 用户姓名 |
| roleId | long | 否 | 角色 ID |
| deptId | long | 否 | 科室 ID |
| phone | string | 否 | 手机号 |
| email | string | 否 | 邮箱 |
| status | integer | 否 | 状态：1-启用，0-禁用 |

### 24.5.3 出参说明

返回结构同“新增用户接口”。

### 24.5.4 请求示例

```http
PUT /api/system/user HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
Content-Type: application/json

{
  "userId": 1303,
  "phone": "13800009999",
  "email": "chen.xin.new@hospital.local",
  "status": 1
}
```

### 24.5.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "userId": 1303,
    "userName": "chen_xin",
    "realName": "陈医生",
    "roleId": 1003,
    "roleName": "心内科医生",
    "deptId": 1202,
    "deptName": "心血管内科一病区",
    "phone": "13800009999",
    "email": "chen.xin.new@hospital.local",
    "status": 1,
    "statusText": "启用"
  },
  "timestamp": 1776501256203
}
```

---

## 24.6 删除用户接口

### 24.6.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 删除用户 |
| 业务作用 | 逻辑删除用户，包含权限校验、超管保护与审计日志写入 |
| 请求路径 | /api/system/user |
| 请求方式 | DELETE |
| Content-Type | application/json |
| 权限码 | system:user:delete |

### 24.6.2 请求入参

| 参数位置 | 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|---|
| Query | userId | long | 是 | 目标用户 ID |

### 24.6.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| userId | long | 已删除用户 ID |
| deleted | boolean | 是否删除成功 |

### 24.6.4 请求示例

```http
DELETE /api/system/user?userId=1305 HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
```

### 24.6.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "userId": 1305,
    "deleted": true
  },
  "timestamp": 1776501256203
}
```

---

## 24.7 重置用户密码接口

### 24.7.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 重置用户密码 |
| 业务作用 | 将用户密码重置为指定值（不传则使用默认密码） |
| 请求路径 | /api/system/user/reset-password |
| 请求方式 | POST |
| Content-Type | application/json |
| 权限码 | system:user:reset-password |

### 24.7.2 请求入参

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| userId | long | 是 | 用户 ID |
| newPassword | string | 否 | 新密码，6-32 位；不传默认 123456 |

### 24.7.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| userId | long | 用户 ID |
| resetSuccess | boolean | 是否重置成功 |
| resetTime | string | 重置时间（ISO-8601） |

### 24.7.4 请求示例

```http
POST /api/system/user/reset-password HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
Content-Type: application/json

{
  "userId": 1303,
  "newPassword": "NewPwd@123"
}
```

### 24.7.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "userId": 1303,
    "resetSuccess": true,
    "resetTime": "2026-04-18T19:34:56"
  },
  "timestamp": 1776501256203
}
```

---

## 25. 用户管理模块业务规则

1. 用户列表查询默认分页：pageNum=1，pageSize=10，且 pageSize 最大 200。
2. userName 需满足 3-32 位字母/数字/下划线，且在未逻辑删除数据中唯一。
3. roleId、deptId 必须关联有效且启用的数据；roleName、deptName 由后端回填冗余快照。
4. status 支持中文枚举与编码双输入（启用/禁用 或 1/0），统一落库为整型状态。
5. 新增用户与重置密码均使用 BCrypt 加密；password/newPassword 未传时默认使用 123456。
6. 删除用户必须携带有效 JWT，且当前操作人角色需具备 system:all、system:user:delete、user:delete 之一。
7. 超级管理员账号（admin/系统管理员角色）禁止删除；当前登录用户禁止删除自身账号。
8. 删除成功后必须写入 sys_operation_log，记录操作人、模块、操作类型、操作内容、来源 IP、操作时间。
9. 用户删除采用逻辑删除（is_deleted=1），不做物理删除。

## 26. 用户管理模块错误场景

| 场景 | code | message 示例 |
|---|---|---|
| userId / roleId / deptId 非法 | 400 | userId 参数不合法 |
| userName 参数不合法 | 400 | userName 参数不合法 |
| realName 参数不合法 | 400 | realName 参数不合法 |
| phone / email 参数不合法 | 400 | phone 参数不合法 / email 参数不合法 |
| status 参数不合法 | 400 | status 参数不合法 |
| password 长度不合法 | 400 | password 长度需在 6-32 之间 |
| roleId 或 deptId 对应主数据不存在 | 400 | roleId 对应角色不存在 |
| userName 重复 | 1000 | userName 已存在 |
| 用户不存在 | 404 | 用户不存在 |
| 未登录或 token 失效 | 401 | 未认证或登录已失效 |
| 删除权限不足 | 403 | 无权限删除用户 |
| 删除超管账号被拦截 | 403 | 超管账号禁止删除 |
| 删除当前登录用户被拦截 | 1000 | 不允许删除当前登录用户 |
| 服务内部异常 | 5000 | 系统繁忙，请稍后重试 |

---

## 27. 角色管理模块接口规范

## 27.1 角色分页列表接口

### 27.1.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 角色分页列表查询 |
| 业务作用 | 分页查询角色，支持按角色名称关键词检索 |
| 请求路径 | /api/system/role/page |
| 请求方式 | POST |
| Content-Type | application/json |
| 权限码 | system:role:list |

### 27.1.2 请求入参

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|---|---|---|---|---|
| keyword | string | 否 | 空 | 角色名称关键词 |
| pageNum | long | 否 | 1 | 页码，最小 1 |
| pageSize | long | 否 | 10 | 每页条数，最大 200 |

### 27.1.3 出参说明

#### data（分页信息）

| 字段 | 类型 | 说明 |
|---|---|---|
| pageNum | long | 当前页码 |
| pageSize | long | 每页条数 |
| total | long | 总记录数 |
| pages | long | 总页数 |
| records | array | 当前页数据 |

#### data.records[]（角色列表项）

| 字段 | 类型 | 说明 |
|---|---|---|
| roleId | long | 角色 ID |
| roleName | string | 角色名称 |
| description | string | 角色描述 |
| userCount | integer | 关联用户数 |
| createTime | string | 创建时间（ISO-8601） |
| status | integer | 状态编码（1 启用 / 0 禁用） |
| statusText | string | 状态文本 |

### 27.1.4 请求示例

```http
POST /api/system/role/page HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
Content-Type: application/json

{
  "keyword": "医生",
  "pageNum": 1,
  "pageSize": 10
}
```

### 27.1.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "pageNum": 1,
    "pageSize": 10,
    "total": 2,
    "pages": 1,
    "records": [
      {
        "roleId": 1003,
        "roleName": "心内科医生",
        "description": "负责心电解读、报告编写",
        "userCount": 1,
        "createTime": "2026-04-18T00:00:00",
        "status": 1,
        "statusText": "启用"
      }
    ]
  },
  "timestamp": 1776501256203
}
```

---

## 27.2 新增角色接口

### 27.2.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 新增角色 |
| 业务作用 | 创建系统角色并初始化状态 |
| 请求路径 | /api/system/role |
| 请求方式 | POST |
| Content-Type | application/json |
| 权限码 | system:role:create |

### 27.2.2 请求入参

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| roleName | string | 是 | 角色名称，长度 1-32 |
| description | string | 否 | 角色描述，长度最大 256 |
| status | integer | 否 | 状态：1-启用，0-禁用；默认 1 |

### 27.2.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| roleId | long | 角色 ID |
| roleName | string | 角色名称 |
| description | string | 角色描述 |
| userCount | integer | 关联用户数 |
| status | integer | 状态编码 |
| statusText | string | 状态文本 |
| createTime | string | 创建时间（ISO-8601） |
| updateTime | string | 更新时间（ISO-8601） |

### 27.2.4 请求示例

```http
POST /api/system/role HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
Content-Type: application/json

{
  "roleName": "值班医生",
  "description": "负责夜班预警复核",
  "status": 1
}
```

### 27.2.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "roleId": 1933887802234567890,
    "roleName": "值班医生",
    "description": "负责夜班预警复核",
    "userCount": 0,
    "status": 1,
    "statusText": "启用",
    "createTime": "2026-04-18T20:10:00",
    "updateTime": "2026-04-18T20:10:00"
  },
  "timestamp": 1776501256203
}
```

---

## 27.3 角色详情查询接口

### 27.3.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 角色详情查询 |
| 业务作用 | 编辑弹窗打开时预填充表单 |
| 请求路径 | /api/system/role/detail |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | system:role:detail |

### 27.3.2 请求入参

| 参数位置 | 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|---|
| Query | roleId | long | 是 | 角色 ID |

### 27.3.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| roleId | long | 角色 ID |
| roleName | string | 角色名称 |
| description | string | 角色描述 |
| userCount | integer | 关联用户数 |
| status | integer | 状态编码 |
| statusText | string | 状态文本 |
| createTime | string | 创建时间（ISO-8601） |
| updateTime | string | 更新时间（ISO-8601） |

### 27.3.4 请求示例

```http
GET /api/system/role/detail?roleId=1003 HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
```

### 27.3.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "roleId": 1003,
    "roleName": "心内科医生",
    "description": "负责心电解读、报告编写",
    "userCount": 1,
    "status": 1,
    "statusText": "启用",
    "createTime": "2026-04-18T00:00:00",
    "updateTime": "2026-04-18T00:00:00"
  },
  "timestamp": 1776501256203
}
```

---

## 27.4 编辑角色接口

### 27.4.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 编辑角色 |
| 业务作用 | 更新角色名称、描述、状态 |
| 请求路径 | /api/system/role |
| 请求方式 | PUT |
| Content-Type | application/json |
| 权限码 | system:role:update |

### 27.4.2 请求入参

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| roleId | long | 是 | 角色 ID |
| roleName | string | 否 | 更新后的角色名称 |
| description | string | 否 | 更新后的角色描述 |
| status | integer | 否 | 更新后的状态：1-启用，0-禁用 |

### 27.4.3 出参说明

返回结构同“新增角色接口”。

### 27.4.4 请求示例

```http
PUT /api/system/role HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
Content-Type: application/json

{
  "roleId": 1003,
  "description": "负责心电解读、报告编写与会诊支持",
  "status": 1
}
```

### 27.4.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "roleId": 1003,
    "roleName": "心内科医生",
    "description": "负责心电解读、报告编写与会诊支持",
    "userCount": 1,
    "status": 1,
    "statusText": "启用",
    "createTime": "2026-04-18T00:00:00",
    "updateTime": "2026-04-18T20:15:00"
  },
  "timestamp": 1776501256203
}
```

---

## 27.5 删除角色接口

### 27.5.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 删除角色 |
| 业务作用 | 逻辑删除角色，并处理角色权限关联 |
| 请求路径 | /api/system/role |
| 请求方式 | DELETE |
| Content-Type | application/json |
| 权限码 | system:role:delete |

### 27.5.2 请求入参

| 参数位置 | 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|---|
| Query | roleId | long | 是 | 角色 ID |
| Query | force | boolean | 否 | 是否确认删除已关联用户的角色；默认 false |

### 27.5.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| roleId | long | 角色 ID |
| deleted | boolean | 是否删除成功 |
| associatedUserCount | long | 删除前关联用户数 |
| forcedDelete | boolean | 是否执行了强制确认删除 |

### 27.5.4 请求示例

```http
DELETE /api/system/role?roleId=1005&force=true HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
```

### 27.5.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "roleId": 1005,
    "deleted": true,
    "associatedUserCount": 1,
    "forcedDelete": true
  },
  "timestamp": 1776501256203
}
```

---

## 27.6 查询角色已有权限接口

### 27.6.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 查询角色已有权限 |
| 业务作用 | 权限设置弹窗打开时，返回权限选项及当前勾选状态 |
| 请求路径 | /api/system/role/permissions |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | system:role:permission:read |

### 27.6.2 请求入参

| 参数位置 | 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|---|
| Query | roleId | long | 是 | 角色 ID |

### 27.6.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| roleId | long | 角色 ID |
| roleName | string | 角色名称 |
| permissionOptions | array | 权限选项列表 |
| checkedPermissionIds | array | 已勾选权限 ID 列表 |

permissionOptions[] 字段：

| 字段 | 类型 | 说明 |
|---|---|---|
| permissionId | long | 权限 ID |
| permissionCode | string | 权限编码 |
| permissionName | string | 权限名称 |
| checked | boolean | 当前角色是否已勾选 |

### 27.6.4 请求示例

```http
GET /api/system/role/permissions?roleId=1003 HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
```

### 27.6.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "roleId": 1003,
    "roleName": "心内科医生",
    "permissionOptions": [
      {
        "permissionId": 1101,
        "permissionCode": "system:all",
        "permissionName": "系统全权限",
        "checked": false
      },
      {
        "permissionId": 1103,
        "permissionCode": "diagnosis:write",
        "permissionName": "编写诊断结论",
        "checked": true
      }
    ],
    "checkedPermissionIds": [1103]
  },
  "timestamp": 1776501256203
}
```

---

## 27.7 保存角色权限分配接口

### 27.7.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 保存角色权限分配 |
| 业务作用 | 保存权限设置弹窗勾选结果 |
| 请求路径 | /api/system/role/permissions |
| 请求方式 | POST |
| Content-Type | application/json |
| 权限码 | system:role:permission:save |

### 27.7.2 请求入参

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| roleId | long | 是 | 角色 ID |
| permissionIds | array | 否 | 勾选的权限 ID 集合；传空数组表示清空该角色权限 |

### 27.7.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| roleId | long | 角色 ID |
| permissionCount | integer | 保存后的权限数量 |
| saveSuccess | boolean | 是否保存成功 |
| saveTime | string | 保存时间（ISO-8601） |

### 27.7.4 请求示例

```http
POST /api/system/role/permissions HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
Content-Type: application/json

{
  "roleId": 1003,
  "permissionIds": [1103, 1102]
}
```

### 27.7.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "roleId": 1003,
    "permissionCount": 2,
    "saveSuccess": true,
    "saveTime": "2026-04-18T20:22:00"
  },
  "timestamp": 1776501256203
}
```

---

## 28. 角色管理模块业务规则

1. 角色分页默认参数：pageNum=1，pageSize=10，且 pageSize 最大 200。
2. roleName 为必填字段，长度最大 32，且在未逻辑删除数据中唯一。
3. description 最大长度 256；status 仅支持 1（启用）或 0（禁用）。
4. 系统内置超管角色（roleId=1001 或角色名“系统管理员”）禁止删除。
5. 删除已关联用户的角色时，首次请求需返回二次确认提醒；携带 force=true 才执行删除。
6. 角色删除采用逻辑删除（sys_role.is_deleted=1），同时逻辑删除该角色已有权限关联。
7. 角色权限查询返回“全量权限选项 + 当前勾选项”，用于弹窗直接渲染勾选状态。
8. 保存角色权限采用“覆盖式保存”：先清空历史权限，再按提交的 permissionIds 重建关联。
9. permissionIds 允许空数组，表示清空该角色权限。

## 29. 角色管理模块错误场景

| 场景 | code | message 示例 |
|---|---|---|
| roleId 非法 | 400 | roleId 参数不合法 |
| roleName 参数不合法 | 400 | roleName 参数不合法 |
| description 参数不合法 | 400 | description 参数不合法 |
| status 参数不合法 | 400 | status 参数不合法 |
| permissionIds 参数不合法 | 400 | permissionIds 参数不合法 |
| permissionIds 含无效权限ID | 400 | permissionIds 包含无效权限ID |
| roleName 重复 | 1000 | roleName 已存在 |
| 角色不存在 | 404 | 角色不存在 |
| 删除系统内置超管角色 | 403 | 系统内置超管角色禁止删除 |
| 删除已关联用户角色但未确认 | 1000 | 角色已关联用户，请二次确认后删除（传 force=true） |
| 未登录或 token 失效 | 401 | 未认证或登录已失效 |
| 服务内部异常 | 5000 | 系统繁忙，请稍后重试 |

---

## 30. 科室管理模块接口规范

## 30.1 科室分页列表查询接口

### 30.1.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 科室分页列表查询 |
| 业务作用 | 分页查询科室/病区，支持关键词与状态筛选 |
| 请求路径 | /api/system/department/page |
| 请求方式 | POST |
| Content-Type | application/json |
| 权限码 | system:department:list |

### 30.1.2 请求入参

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|---|---|---|---|---|
| keyword | string | 否 | 空 | 科室名称关键词 |
| status | string | 否 | 空 | 状态，支持 启用/禁用 或 1/0 |
| pageNum | long | 否 | 1 | 页码，最小 1 |
| pageSize | long | 否 | 10 | 每页条数，最大 200 |

### 30.1.3 出参说明

#### data（分页信息）

| 字段 | 类型 | 说明 |
|---|---|---|
| pageNum | long | 当前页码 |
| pageSize | long | 每页条数 |
| total | long | 总记录数 |
| pages | long | 总页数 |
| records | array | 当前页数据 |

#### data.records[]（科室列表项）

| 字段 | 类型 | 说明 |
|---|---|---|
| deptId | long | 科室 ID |
| deptName | string | 科室名称 |
| parentDeptName | string | 上级科室名称 |
| deptType | integer | 层级类型编码（1-科室 2-病区 3-居家站点） |
| deptTypeText | string | 层级类型文本 |
| director | string | 负责人 |
| phone | string | 联系电话 |
| status | integer | 状态编码 |
| statusText | string | 状态文本 |
| createTime | string | 创建时间（ISO-8601） |

### 30.1.4 请求示例

```http
POST /api/system/department/page HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
Content-Type: application/json

{
  "keyword": "心内",
  "status": "1",
  "pageNum": 1,
  "pageSize": 10
}
```

### 30.1.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "pageNum": 1,
    "pageSize": 10,
    "total": 1,
    "pages": 1,
    "records": [
      {
        "deptId": 1202,
        "deptName": "心血管内科一病区",
        "parentDeptName": "心血管内科",
        "deptType": 2,
        "deptTypeText": "病区",
        "director": "孙主任",
        "phone": "13800000002",
        "status": 1,
        "statusText": "启用",
        "createTime": "2026-04-18T00:00:00"
      }
    ]
  },
  "timestamp": 1776501256203
}
```

---

## 30.2 新增科室接口

### 30.2.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 新增科室 |
| 业务作用 | 新建科室主档，写入层级关系、负责人和状态 |
| 请求路径 | /api/system/department |
| 请求方式 | POST |
| Content-Type | application/json |
| 权限码 | system:department:create |

### 30.2.2 请求入参

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| deptName | string | 是 | 科室名称 |
| parentId | long | 否 | 上级科室 ID（不传表示顶级） |
| deptType | integer | 是 | 层级类型：1-科室 2-病区 3-居家站点 |
| director | string | 否 | 负责人 |
| phone | string | 否 | 联系电话 |
| location | string | 否 | 位置 |
| status | integer | 否 | 状态：1-启用，0-禁用；默认 1 |

### 30.2.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| deptId | long | 科室 ID |
| deptName | string | 科室名称 |
| parentDeptId | long | 上级科室 ID |
| parentDeptName | string | 上级科室名称 |
| deptType | integer | 层级类型编码 |
| deptTypeText | string | 层级类型文本 |
| director | string | 负责人 |
| phone | string | 联系电话 |
| location | string | 位置 |
| status | integer | 状态编码 |
| statusText | string | 状态文本 |
| createTime | string | 创建时间（ISO-8601） |
| updateTime | string | 更新时间（ISO-8601） |

### 30.2.4 请求示例

```json
{
  "deptName": "心电门诊",
  "parentId": 1201,
  "deptType": 2,
  "director": "张主任",
  "phone": "13800138000",
  "location": "3F-东区",
  "status": 1
}
```

### 30.2.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "deptId": 1206,
    "deptName": "心电门诊",
    "parentDeptId": 1201,
    "parentDeptName": "心血管内科",
    "deptType": 2,
    "deptTypeText": "病区",
    "director": "张主任",
    "phone": "13800138000",
    "location": "3F-东区",
    "status": 1,
    "statusText": "启用",
    "createTime": "2026-04-18T21:10:00",
    "updateTime": "2026-04-18T21:10:00"
  },
  "timestamp": 1776501256203
}
```

---

## 30.3 编辑科室接口

### 30.3.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 编辑科室 |
| 业务作用 | 更新科室主档与层级关系 |
| 请求路径 | /api/system/department |
| 请求方式 | PUT |
| Content-Type | application/json |
| 权限码 | system:department:update |

### 30.3.2 请求入参

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| deptId | long | 是 | 科室 ID |
| deptName | string | 是 | 科室名称 |
| parentId | long | 否 | 上级科室 ID |
| deptType | integer | 是 | 层级类型：1-科室 2-病区 3-居家站点 |
| director | string | 否 | 负责人 |
| phone | string | 否 | 联系电话 |
| location | string | 否 | 位置 |
| status | integer | 否 | 状态：1-启用，0-禁用 |

### 30.3.3 出参说明

返回字段同 30.2.3。

### 30.3.4 请求示例

```json
{
  "deptId": 1202,
  "deptName": "心血管内科一病区",
  "parentId": 1201,
  "deptType": 2,
  "director": "孙主任",
  "phone": "13800000002",
  "location": "4F-东区",
  "status": 1
}
```

### 30.3.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "deptId": 1202,
    "deptName": "心血管内科一病区",
    "parentDeptId": 1201,
    "parentDeptName": "心血管内科",
    "deptType": 2,
    "deptTypeText": "病区",
    "director": "孙主任",
    "phone": "13800000002",
    "location": "4F-东区",
    "status": 1,
    "statusText": "启用",
    "createTime": "2026-04-18T00:00:00",
    "updateTime": "2026-04-18T21:20:00"
  },
  "timestamp": 1776501256203
}
```

---

## 30.4 科室详情查询接口

### 30.4.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 科室详情查询 |
| 业务作用 | 编辑/查看弹窗回显全部字段 |
| 请求路径 | /api/system/department/detail |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | system:department:detail |

### 30.4.2 请求入参

| 参数位置 | 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|---|
| Query | deptId | long | 是 | 科室 ID |

### 30.4.3 出参说明

返回字段同 30.2.3。

### 30.4.4 请求示例

```http
GET /api/system/department/detail?deptId=1202 HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
```

### 30.4.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "deptId": 1202,
    "deptName": "心血管内科一病区",
    "parentDeptId": 1201,
    "parentDeptName": "心血管内科",
    "deptType": 2,
    "deptTypeText": "病区",
    "director": "孙主任",
    "phone": "13800000002",
    "location": "4F-东区",
    "status": 1,
    "statusText": "启用",
    "createTime": "2026-04-18T00:00:00",
    "updateTime": "2026-04-18T21:20:00"
  },
  "timestamp": 1776501256203
}
```

---

## 30.5 删除科室接口

### 30.5.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 删除科室 |
| 业务作用 | 删除前执行关联校验，满足条件后逻辑删除 |
| 请求路径 | /api/system/department |
| 请求方式 | DELETE |
| Content-Type | application/json |
| 权限码 | system:department:delete |

### 30.5.2 请求入参

| 参数位置 | 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|---|
| Query | deptId | long | 是 | 科室 ID |

### 30.5.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| deptId | long | 科室 ID |
| deleted | boolean | 是否删除成功 |
| childCount | long | 下级科室数量 |
| boundBedCount | long | 绑定床位数量 |
| boundDeviceCount | long | 绑定设备数量 |

### 30.5.4 请求示例

```http
DELETE /api/system/department?deptId=1205 HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
```

### 30.5.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "deptId": 1205,
    "deleted": true,
    "childCount": 0,
    "boundBedCount": 0,
    "boundDeviceCount": 0
  },
  "timestamp": 1776501256203
}
```

---

## 30.6 科室状态单独切换接口

### 30.6.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 科室状态单独切换 |
| 业务作用 | 独立切换科室启停状态，不更新其他字段 |
| 请求路径 | /api/system/department/status |
| 请求方式 | PUT |
| Content-Type | application/json |
| 权限码 | system:department:update |

### 30.6.2 请求入参

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| deptId | long | 是 | 科室 ID |
| status | integer | 是 | 目标状态：1-启用，0-禁用 |

### 30.6.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| deptId | long | 科室 ID |
| status | integer | 状态编码 |
| statusText | string | 状态文本 |

### 30.6.4 请求示例

```json
{
  "deptId": 1202,
  "status": 0
}
```

### 30.6.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "deptId": 1202,
    "status": 0,
    "statusText": "禁用"
  },
  "timestamp": 1776501256203
}
```

---

## 30.7 上级科室树形下拉接口

### 30.7.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 上级科室树形下拉 |
| 业务作用 | 获取上级科室树；编辑时可按 deptId 过滤自身及其下级，防止闭环 |
| 请求路径 | /api/system/department/tree |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | system:department:dict |

### 30.7.2 请求入参

| 参数位置 | 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|---|
| Query | deptId | long | 否 | 当前编辑的科室 ID（传入后过滤自身及其下级） |

### 30.7.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| deptId | long | 科室 ID |
| parentDeptId | long | 上级科室 ID |
| deptName | string | 科室名称 |
| children | array | 子节点 |

### 30.7.4 请求示例

```http
GET /api/system/department/tree?deptId=1202 HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
```

### 30.7.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": [
    {
      "deptId": 1201,
      "parentDeptId": null,
      "deptName": "心血管内科",
      "children": []
    },
    {
      "deptId": 1301,
      "parentDeptId": null,
      "deptName": "神经内科",
      "children": [
        {
          "deptId": 1302,
          "parentDeptId": 1301,
          "deptName": "神经内科一病区",
          "children": []
        }
      ]
    }
  ],
  "timestamp": 1776501256203
}
```

---

## 31. 科室管理模块业务规则

1. 科室分页默认参数：pageNum=1，pageSize=10，且 pageSize 最大 200。
2. status 支持中文枚举与编码双输入（启用/禁用 或 1/0）。
3. deptName 长度最大 64，且在同一上级科室下保持唯一。
4. deptType 仅支持 1（科室）、2（病区）、3（居家站点）。
5. 删除科室前必须校验下级科室；存在下级科室直接拦截。
6. 删除科室前必须校验科室是否绑定床位或设备；有绑定直接拦截。
7. 科室删除采用逻辑删除（sys_department.is_deleted=1），不做物理删除。
8. 科室状态切换使用独立接口 `/api/system/department/status`。
9. 上级科室树接口在传入 deptId 时，自动过滤自身及其下级，避免形成父子闭环。

## 32. 科室管理模块错误场景

| 场景 | code | message 示例 |
|---|---|---|
| deptId / parentId 非法 | 400 | deptId 参数不合法 |
| deptName 参数不合法 | 400 | deptName 参数不合法 |
| deptType 参数不合法 | 400 | deptType 参数不合法 |
| status 参数不合法 | 400 | status 参数不合法 |
| phone 参数不合法 | 400 | phone 参数不合法 |
| location 参数不合法 | 400 | location 参数不合法 |
| parentId 对应科室不存在 | 400 | parentId 对应科室不存在 |
| parentId 形成闭环 | 400 | parentId 参数不合法 |
| deptName 重复 | 1000 | deptName 已存在 |
| 科室不存在 | 404 | 科室不存在 |
| 存在下级子科室禁止删除 | 1000 | 存在下级子科室，禁止直接删除 |
| 已绑定床位/设备禁止删除 | 1000 | 科室已绑定床位或设备，禁止删除 |
| 未登录或 token 失效 | 401 | 未认证或登录已失效 |
| 服务内部异常 | 5000 | 系统繁忙，请稍后重试 |

---

## 33. 工作台模块接口规范

## 33.1 工作台全局概览统计接口

### 33.1.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 工作台全局概览统计 |
| 请求路径 | /api/workbench/overview |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | workbench:overview:read |

### 33.1.2 请求入参

| 参数位置 | 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|---|
| Query | timeType | string | 否 | 时间维度：today/week/month/year |
| Query | startTime | string | 否 | 自定义开始时间，支持 yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss |
| Query | endTime | string | 否 | 自定义结束时间，支持 yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss |

### 33.1.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| ecgTotalCount | long | 心电图测量总次数 |
| todayAddCount | long | 今日新增测量次数 |
| pendingAnalyseCount | long | 待 AI 分析记录数 |
| pendingAuditCount | long | 待审核报告数 |
| alertTotalCount | long | 异常预警总数 |
| alertHandledCount | long | 已处理预警数量 |

### 33.1.4 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "ecgTotalCount": 1256,
    "todayAddCount": 45,
    "pendingAnalyseCount": 8,
    "pendingAuditCount": 12,
    "alertTotalCount": 11,
    "alertHandledCount": 3
  },
  "timestamp": 1776501256203
}
```

---

## 33.2 待处理预警分页列表接口

### 33.2.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 待处理预警分页列表 |
| 请求路径 | /api/workbench/pending-alerts/page |
| 请求方式 | POST |
| Content-Type | application/json |
| 权限码 | workbench:alert:page |

### 33.2.2 请求入参

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|---|---|---|---|---|
| timeType | string | 否 | 空 | 时间维度：today/week/month/year |
| startTime | string | 否 | 空 | 开始时间 |
| endTime | string | 否 | 空 | 结束时间 |
| pageNum | long | 否 | 1 | 页码 |
| pageSize | long | 否 | 10 | 每页条数，最大 200 |

### 33.2.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| total | long | 总记录数 |
| list | array | 当前页数据 |

list[] 字段：

| 字段 | 类型 | 说明 |
|---|---|---|
| alertId | long | 预警 ID |
| alertTime | string | 预警时间 |
| patientInfo | string | 患者信息（姓名/性别/年龄） |
| ward | string | 所属病区 |
| alertType | string | 预警类型 |
| alertLevel | string | 预警级别（高危/中危/低危） |
| alertStatus | string | 预警状态 |
| sourceType | string | 数据来源（动态心电图/静态心电图） |
| clinicalSymptom | string | 患者临床表现 |
| lisCheckData | string | LIS 检验异常数据摘要 |

### 33.2.4 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "total": 2,
    "list": [
      {
        "alertId": 2601,
        "alertTime": "2026-04-18T08:40:00",
        "patientInfo": "陈敏 / 女 / 49岁",
        "ward": "冠心监护病区",
        "alertType": "室性心动过速风险",
        "alertLevel": "高危",
        "alertStatus": "待处理",
        "sourceType": "动态心电图",
        "clinicalSymptom": "不明原因晕厥，伴胸闷",
        "lisCheckData": "肌钙蛋白偏高；CK-MB偏高；建议急查电解质"
      }
    ]
  },
  "timestamp": 1776501256203
}
```

---

## 33.3 最新心电记录分页查询接口

### 33.3.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 最新心电记录分页查询 |
| 请求路径 | /api/workbench/latest-ecg/page |
| 请求方式 | POST |
| Content-Type | application/json |
| 权限码 | workbench:ecg:page |

### 33.3.2 请求入参

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|---|---|---|---|---|
| timeType | string | 否 | 空 | 时间维度：today/week/month/year |
| startTime | string | 否 | 空 | 开始时间 |
| endTime | string | 否 | 空 | 结束时间 |
| pageNum | long | 否 | 1 | 页码 |
| pageSize | long | 否 | 10 | 每页条数，最大 200 |

### 33.3.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| total | long | 总记录数 |
| list | array | 当前页数据 |

list[] 字段：

| 字段 | 类型 | 说明 |
|---|---|---|
| ecgId | long | 心电数据 ID |
| collectTime | string | 采集时间 |
| patientInfo | string | 患者信息 |
| ward | string | 病区 |
| deviceNo | string | 采集设备 |
| status | string | 数据状态 |
| aiConclusion | string | AI 诊断结论 |

---

## 33.4 预警详情查询接口

### 33.4.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 预警详情查询 |
| 请求路径 | /api/workbench/alerts/{alertId} |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | workbench:alert:detail |

### 33.4.2 请求入参

| 参数位置 | 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|---|
| Path | alertId | long | 是 | 预警唯一 ID |

### 33.4.3 出参说明

返回完整预警全量信息，包含患者档案、预警详情、动态心电来源、临床表现、LIS 异常摘要、历史心电数据。

关键字段：

| 字段 | 类型 | 说明 |
|---|---|---|
| alertId | long | 预警 ID |
| alertTime | string | 预警时间 |
| alertType | string | 预警类型 |
| alertLevel | string | 预警等级 |
| alertStatus | string | 预警状态 |
| patientId | long | 患者 ID |
| patientName | string | 患者姓名 |
| gender | string | 性别 |
| age | integer | 年龄 |
| inpatientNo | string | 住院号 |
| ward | string | 病区 |
| bedNo | string | 床号 |
| primaryDiagnosis | string | 患者主要诊断 |
| sourceType | string | 来源类型 |
| sourceRecordId | long | 来源心电记录 ID |
| sourceEcgNo | string | 来源心电编号 |
| sourceDeviceName | string | 来源设备名称 |
| sourceCollectTime | string | 来源采集时间 |
| clinicalSymptom | string | 临床表现 |
| lisCheckData | string | LIS 异常摘要 |
| historyEcgList | array | 历史心电数据 |

---

## 33.5 单条心电数据详情接口

### 33.5.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 单条心电数据详情 |
| 请求路径 | /api/workbench/ecg/{ecgId} |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | workbench:ecg:detail |

### 33.5.2 请求入参

| 参数位置 | 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|---|
| Path | ecgId | long | 是 | 心电数据唯一 ID |

### 33.5.3 出参说明

返回患者基础信息、采集信息、原始数据文件地址、AI 完整诊断结论与波形预览点。

---

## 33.6 公共筛选字典接口

### 33.6.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 工作台公共筛选字典 |
| 请求路径 | /api/workbench/dicts |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | workbench:dict:read |

### 33.6.2 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| timeTypeOptions | array | 时间周期枚举 |
| alertLevelOptions | array | 预警级别字典 |
| alertStatusOptions | array | 预警状态字典 |
| deviceTypeOptions | array | 设备类型字典 |

---

## 34. 心电数据列表模块接口规范

## 34.1 心电数据分页列表查询（主接口）

### 34.1.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 心电数据分页列表查询 |
| 请求路径 | /api/ecg-data/page |
| 请求方式 | POST |
| Content-Type | application/json |
| 权限码 | ecg:data:page |

### 34.1.2 请求入参

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|---|---|---|---|---|
| keyword | string | 否 | 空 | 患者姓名/住院号/设备编号/心电编号 |
| wardId | long | 否 | 空 | 病区 ID |
| status | string | 否 | 空 | 状态：待分析/已分析/已审核 |
| startTime | string | 否 | 空 | 开始时间 |
| endTime | string | 否 | 空 | 结束时间 |
| pageNum | long | 否 | 1 | 页码 |
| pageSize | long | 否 | 10 | 每页条数，最大 200 |

### 34.1.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| total | long | 总记录数 |
| list | array | 当前页数据 |

list[] 字段：

| 字段 | 类型 | 说明 |
|---|---|---|
| ecgId | long | 心电数据 ID |
| ecgNo | string | 心电编号 |
| collectTime | string | 采集时间 |
| patientInfo | string | 患者信息 |
| patientName | string | 患者姓名 |
| inpatientNo | string | 住院号 |
| ward | string | 病区 |
| deviceNo | string | 设备编号/名称 |
| status | string | 状态 |
| aiConclusion | string | AI 结论 |

---

## 34.2 数据上传接口

### 34.2.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 数据上传 |
| 请求路径 | /api/ecg-data/upload |
| 请求方式 | POST |
| Content-Type | multipart/form-data |
| 权限码 | ecg:data:upload |

### 34.2.2 请求入参

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| file | file | 是 | 原始心电文件 |
| patientName | string | 是 | 患者姓名 |
| inpatientNo | string | 是 | 住院号 |
| deviceNo | string | 是 | 设备编号 |

### 34.2.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| ecgId | long | 新增心电记录 ID |
| ecgNo | string | 心电编号 |
| status | string | 当前状态 |
| aiQueueTriggered | boolean | 是否已触发 AI 分析队列 |
| uploadTime | string | 上传时间 |

---

## 34.3 心电数据详情查询

### 34.3.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 心电数据详情查询 |
| 请求路径 | /api/ecg-data/{ecgId} |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | ecg:data:detail |

### 34.3.2 请求入参

| 参数位置 | 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|---|
| Path | ecgId | long | 是 | 心电数据 ID |

### 34.3.3 出参说明

返回患者基础信息、采集信息、原始心电数据入口、AI 完整诊断结论与波形预览。

---

## 34.4 数据状态更新接口

### 34.4.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 数据状态更新 |
| 请求路径 | /api/ecg-data/status |
| 请求方式 | PUT |
| Content-Type | application/json |
| 权限码 | ecg:data:status:update |

### 34.4.2 请求入参

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| ecgId | long | 是 | 心电数据 ID |
| targetStatus | string | 是 | 目标状态：待分析/已分析/已审核 |

### 34.4.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| ecgId | long | 心电数据 ID |
| targetStatus | string | 更新后的状态 |
| updated | boolean | 是否更新成功 |

---

## 34.5 公共字典接口（筛选项）

### 34.5.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 心电数据筛选字典 |
| 请求路径 | /api/ecg-data/dicts |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | ecg:data:dict:read |

### 34.5.2 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| wardOptions | array | 病区列表 |
| deviceOptions | array | 设备列表 |
| statusOptions | array | 状态枚举 |

---

## 34.6 原始心电波形数据获取接口

### 34.6.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 原始心电波形数据获取 |
| 请求路径 | /api/ecg-data/{ecgId}/waveform |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | ecg:data:waveform:read |

### 34.6.2 请求入参

| 参数位置 | 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|---|
| Path | ecgId | long | 是 | 心电数据 ID |

### 34.6.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| ecgId | long | 心电数据 ID |
| ecgNo | string | 心电编号 |
| samplingRate | integer | 采样率 |
| leadCount | integer | 导联数 |
| rawDataFileUrl | string | 原始数据文件地址 |
| collectStartTime | string | 采集开始时间 |
| collectEndTime | string | 采集结束时间 |
| points | array | 波形点数据 |

points[] 字段：

| 字段 | 类型 | 说明 |
|---|---|---|
| pointIndex | integer | 点位索引 |
| pointValue | number | 点位值 |

---

## 35. 工作台与心电数据模块业务规则

1. 时间过滤统一优先级：startTime/endTime > timeType > 全量。
2. timeType 支持 today/week/month/year 与中英文等价输入。
3. 待处理预警列表仅返回 handle_status in (0,1,2) 的预警记录。
4. 心电数据分页状态筛选与 display_status 对应：待分析=0，已分析=1，已审核=2。
5. 数据上传后默认进入待分析状态，并返回 aiQueueTriggered=true 表示已触发 AI 队列。
6. 数据状态更新会联动 ai_analysis_status、report_status、display_status，确保列表状态一致。
7. 原始波形接口返回采样元数据与点位集合，供 AI 诊断中心、详情页直接绘图。

## 36. 工作台与心电数据模块错误场景

| 场景 | code | message 示例 |
|---|---|---|
| alertId / ecgId 非法 | 400 | alertId parameter is invalid |
| timeType 非法 | 400 | timeType parameter is invalid |
| 时间格式错误 | 400 | time format is invalid |
| 时间区间非法 | 400 | startTime cannot be later than endTime |
| status / targetStatus 非法 | 400 | status parameter is invalid |
| 上传文件为空 | 400 | file is required |
| patientName / inpatientNo 参数非法 | 400 | patientName parameter is invalid |
| deviceNo 参数非法 | 400 | deviceNo parameter is invalid |
| 患者不存在（patientName + inpatientNo 未匹配） | 404 | patient not found |
| deviceNo 不存在 | 404 | device not found |
| 预警记录不存在 | 404 | alert record not found |
| 心电数据不存在 | 404 | ecg data not found |
| 未登录或 token 失效 | 401 | 未认证或登录已失效 |
| 服务内部异常 | 5000 | 系统繁忙，请稍后重试 |

---

## 37. AI诊断中心模块接口规范

## 37.1 AI诊断全局统计概览接口

### 37.1.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | AI诊断全局统计概览 |
| 请求路径 | /api/analysis/ai-diagnosis/overview |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | analysis:ai-diagnosis:overview |

### 37.1.2 请求入参

无业务入参（按当前登录用户权限返回可见数据）。

### 37.1.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| totalCount | long | 诊断总数 |
| pendingAuditCount | long | 待审核数量 |
| auditedCount | long | 已审核数量 |
| avgConfidence | number | AI诊断平均置信度 |

### 37.1.4 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "totalCount": 4,
    "pendingAuditCount": 2,
    "auditedCount": 2,
    "avgConfidence": 90.0
  },
  "timestamp": 1776501256203
}
```

---

## 37.2 AI诊断记录分页查询接口

### 37.2.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | AI诊断记录分页查询 |
| 请求路径 | /api/analysis/ai-diagnosis/page |
| 请求方式 | POST |
| Content-Type | application/json |
| 权限码 | analysis:ai-diagnosis:page |

### 37.2.2 请求入参

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|---|---|---|---|---|
| ecgNo | string | 否 | 空 | 心电图编号 |
| patientName | string | 否 | 空 | 患者姓名 |
| status | string | 否 | 空 | 审核状态：待审核/已审核 |
| pageNum | long | 否 | 1 | 页码 |
| pageSize | long | 否 | 10 | 每页条数，最大 200 |

### 37.2.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| total | long | 总记录数 |
| pages | long | 总页数 |
| list | array | 当前页数据 |

list[] 字段：

| 字段 | 类型 | 说明 |
|---|---|---|
| diagnosisId | string | 诊断唯一编号 |
| ecgId | long | 心电记录 ID |
| ecgNo | string | 心电图编号 |
| patientInfo | string | 患者信息（姓名/年龄/性别） |
| hospitalNo | string | 住院号 |
| deptName | string | 科室/病区名称 |
| aiVersion | string | AI模型版本 |
| aiConclusion | string | AI结论 |
| abnormalCount | integer | 异常点数量 |
| confidence | number | 置信度 |
| diagnosisTime | string | 诊断时间 |
| status | string | 审核状态 |

---

## 37.3 审核状态公共字典接口

### 37.3.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | AI诊断审核状态字典 |
| 请求路径 | /api/analysis/ai-diagnosis/dicts |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | analysis:ai-diagnosis:dict |

### 37.3.2 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| statusList | array | 审核状态下拉字典 |

statusList[] 字段：

| 字段 | 类型 | 说明 |
|---|---|---|
| label | string | 展示名称 |
| value | string | 字典值 |

---

## 37.4 单条诊断详情查询接口

### 37.4.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | AI诊断详情查询 |
| 请求路径 | /api/analysis/ai-diagnosis/{diagnosisId} |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | analysis:ai-diagnosis:detail |

### 37.4.2 请求入参

| 参数位置 | 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|---|
| Path | diagnosisId | string | 是 | 诊断唯一编号（支持 diagnosis_no 或 ai_diagnosis_id） |

### 37.4.3 出参说明

返回单条 AI 诊断完整数据：

1. 诊断基础信息（诊断编号、模型版本、时间、置信度）。
2. 关联患者信息（姓名、性别、年龄、住院号、科室）。
3. 关联心电基础信息（导联数、采样率、采集时间、原始数据入口）。
4. 全部异常点位说明（abnormalPointList）。
5. 审核与报告信息（报告编号、审核状态、审核医生、审核时间、医生终审结论）。

---

## 37.5 原始心电波形数据接口

### 37.5.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | AI诊断原始心电波形数据 |
| 请求路径 | /api/analysis/ai-diagnosis/ecg/{ecgId}/waveform |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | analysis:ai-diagnosis:waveform |

### 37.5.2 请求入参

| 参数位置 | 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|---|
| Path | ecgId | long | 是 | 心电记录 ID |

### 37.5.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| ecgId | long | 心电记录 ID |
| ecgNo | string | 心电编号 |
| samplingRate | integer | 采样率 |
| leadCount | integer | 导联数 |
| rawDataFileUrl | string | 原始数据文件路径 |
| collectStartTime | string | 采集开始时间 |
| collectEndTime | string | 采集结束时间 |
| waveform | array | 波形点数组 |
| segments | array | 波段图谱（P/QRS/T） |
| annotations | array | 异常标注点位 |

waveform[] 字段：

| 字段 | 类型 | 说明 |
|---|---|---|
| pointIndex | integer | 点位索引 |
| pointValue | number | 点位值 |

segments[] 字段：

| 字段 | 类型 | 说明 |
|---|---|---|
| segmentType | string | 波段类型（P/QRS/T） |
| startIndex | integer | 起始点位 |
| endIndex | integer | 结束点位 |

annotations[] 字段：

| 字段 | 类型 | 说明 |
|---|---|---|
| pointIndex | integer | 标注点位 |
| label | string | 异常标签 |
| level | string | 风险等级 |

---

## 37.6 医生审核提交接口

### 37.6.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 医生审核提交 |
| 请求路径 | /api/analysis/ai-diagnosis/audit |
| 请求方式 | POST |
| Content-Type | application/json |
| 权限码 | report:audit / diagnosis:write |

### 37.6.2 请求入参

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| diagnosisId | string | 是 | 诊断编号 |
| doctorConclusion | string | 是 | 医生终审结论 |

### 37.6.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| diagnosisId | string | 诊断编号 |
| status | string | 审核后状态（已审核） |
| auditedBy | string | 审核医生 |
| auditedTime | string | 审核时间 |
| reportDraftId | long | 关联报告草稿/报告 ID |
| reportNo | string | 报告编号 |

### 37.6.4 业务规则

1. 校验当前登录医生身份与审核权限（基于 Token 与角色权限）。
2. 仅允许“待审核”记录提交审核，已审核记录禁止重复提交。
3. 审核成功后状态流转：待审核 -> 已审核。
4. 写入审核医生、审核时间、医生终审结论与审核意见。
5. 若不存在报告记录则自动生成草稿并落库；存在记录则更新审核结果。
6. 同步更新心电采集记录状态：report_status=2，display_status=2。

---

## 38. AI诊断中心模块错误场景

| 场景 | code | message 示例 |
|---|---|---|
| diagnosisId 参数非法 | 400 | diagnosisId 参数不合法 |
| ecgId 参数非法 | 400 | ecgId 参数不合法 |
| status 参数非法 | 400 | status 参数不合法 |
| doctorConclusion 为空 | 400 | doctorConclusion 参数不合法 |
| doctorConclusion 过长 | 400 | doctorConclusion 长度不能超过 2000 |
| AI诊断记录不存在 | 404 | AI 诊断记录不存在 |
| 心电记录不存在 | 404 | 心电记录不存在 |
| Token 缺失或失效 | 401 | 未认证或登录已失效 |
| 当前用户无审核权限 | 403 | 无 AI 诊断审核权限 |
| 已审核记录重复提交 | 400 | 该诊断记录已审核，不能重复提交 |

---

## 43. 科研数据管理模块接口规范

## 43.1 科研数据分页列表查询

### 43.1.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 科研数据分页列表查询 |
| 请求路径 | /api/analysis/research-data/page |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | analysis:research-data:read |

### 43.1.2 请求入参

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|---|---|---|---|---|
| patientKeyword | string | 否 | 空 | 患者姓名关键词（模糊匹配） |
| emrKeyword | string | 否 | 空 | EMR主要诊断关键词（模糊匹配） |
| ecgKeyword | string | 否 | 空 | 心电特征关键词（模糊匹配） |
| deptId | long | 否 | 空 | 病区ID |
| startTime | string | 否 | 空 | 开始时间，支持 yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss |
| endTime | string | 否 | 空 | 结束时间，支持 yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss |
| pageNum | long | 否 | 1 | 页码，最小 1 |
| pageSize | long | 否 | 10 | 每页条数，最大 200 |

### 43.1.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| total | long | 总记录数 |
| pages | long | 总页数 |
| list | array | 当前页列表 |

list[] 字段：

| 字段 | 类型 | 说明 |
|---|---|---|
| rowNum | long | 序号（跨页连续） |
| researchId | long | 科研记录ID |
| patientName | string | 患者姓名 |
| gender | integer | 性别编码（1男 2女） |
| genderText | string | 性别文本 |
| age | integer | 年龄 |
| inpatientNo | string | 住院号 |
| deptId | long | 病区ID |
| deptName | string | 病区名称 |
| mainEmrDiagnosis | string | 病历主要诊断 |
| ecgFeatureSummary | string | 心电特征总结 |
| collectionTime | string | 数据采集时间 |
| isDataApproved | integer | 伦理授权标记（0未授权 1已授权） |
| isDataApprovedText | string | 伦理授权文本 |
| isExported | integer | 导出标记（0未导出 1已导出） |
| isExportedText | string | 导出状态文本 |

### 43.1.4 请求示例

```http
GET /api/analysis/research-data/page?patientKeyword=张&emrKeyword=冠心病&ecgKeyword=室早&deptId=1501&startTime=2026-04-18&endTime=2026-04-21&pageNum=1&pageSize=10 HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
```

### 43.1.5 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "total": 2,
    "pages": 1,
    "list": [
      {
        "rowNum": 1,
        "researchId": 3001,
        "patientName": "张建国",
        "gender": 1,
        "genderText": "男",
        "age": 67,
        "inpatientNo": "ZY202604001",
        "deptId": 1501,
        "deptName": "心内一病区",
        "mainEmrDiagnosis": "冠状动脉粥样硬化性心脏病，高血压3级",
        "ecgFeatureSummary": "窦性心律，偶发室性早搏，心率88次/分",
        "collectionTime": "2026-04-18T08:10:00",
        "isDataApproved": 1,
        "isDataApprovedText": "已授权",
        "isExported": 1,
        "isExportedText": "已导出"
      }
    ]
  },
  "timestamp": 1776587656203
}
```

---

## 43.2 选中科研数据脱敏导出

### 43.2.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 选中科研数据脱敏导出 |
| 请求路径 | /api/analysis/research-data/export/selected |
| 请求方式 | POST |
| Content-Type | application/json |
| 权限码 | analysis:research-data:export |

### 43.2.2 请求入参

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| researchIdList | array<long> | 是 | 选中的科研记录ID列表 |

请求示例：

```json
{
  "researchIdList": [3001, 3003, 3005]
}
```

### 43.2.3 响应说明

1. 返回文件流下载，不走统一 JSON 包装。
2. 响应头 `Content-Type` 为 `text/csv;charset=UTF-8`。
3. 响应头 `Content-Disposition` 为附件下载，文件名示例：`research-data-selected-20260421103020.csv`。

### 43.2.4 脱敏与审计规则

1. 服务端自动脱敏：姓名脱敏、住院号脱敏。
2. 仅导出 `is_data_approved=1` 的记录。
3. 导出成功后批量更新 `ecg_research_data.is_exported=1`。
4. 写入 `sys_operation_log` 审计日志（模块、操作类型、导出范围、操作人、IP、时间）。

---

## 43.3 筛选结果全量脱敏导出

### 43.3.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 筛选结果全量脱敏导出 |
| 请求路径 | /api/analysis/research-data/export/all |
| 请求方式 | POST |
| Content-Type | application/json |
| 权限码 | analysis:research-data:export |

### 43.3.2 请求入参

与 43.1 分页筛选参数一致：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| patientKeyword | string | 否 | 患者姓名关键词 |
| emrKeyword | string | 否 | EMR关键词 |
| ecgKeyword | string | 否 | 心电特征关键词 |
| deptId | long | 否 | 病区ID |
| startTime | string | 否 | 开始时间 |
| endTime | string | 否 | 结束时间 |
| pageNum | long | 否 | 与分页筛选保持一致，导出时可忽略 |
| pageSize | long | 否 | 与分页筛选保持一致，导出时可忽略 |

请求示例：

```json
{
  "patientKeyword": "张",
  "emrKeyword": "冠心病",
  "ecgKeyword": "室早",
  "deptId": 1501,
  "startTime": "2026-04-18",
  "endTime": "2026-04-21",
  "pageNum": 1,
  "pageSize": 10
}
```

### 43.3.3 响应说明

1. 返回筛选命中数据的全量脱敏 CSV 文件流。
2. 脱敏、授权校验、`is_exported` 更新、操作审计规则与 43.2 一致。

---

## 43.4 科研导出错误场景

| 场景 | code | message 示例 |
|---|---|---|
| researchIdList 为空或包含非法ID | 400 | researchIdList 参数不合法 |
| startTime/endTime 格式不合法 | 400 | startTime 参数格式不合法 |
| 开始时间晚于结束时间 | 400 | startTime 不能晚于 endTime |
| 未认证或 token 无效 | 401 | 未认证或登录已失效 |
| 无可导出的已授权科研数据 | 404 | 未查询到可导出的科研数据 |
| 服务内部异常 | 5000 | 系统繁忙，请稍后重试 |

---

## 44. 数据分析-预警查看与跳转接口规范

## 44.1 预警单条详情接口（数据分析）

### 44.1.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 预警单条详情接口（数据分析） |
| 业务作用 | 列表“查看详情”按钮跳转，加载完整预警、患者、心电原始数据、AI诊断全量信息 |
| 请求路径 | /api/analysis/dashboard/warnings/{alertId}/detail |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | analysis:dashboard:warning:detail |

### 44.1.2 请求入参

| 参数位置 | 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|---|
| Path | alertId | long | 是 | 预警ID |

### 44.1.3 出参说明

说明：返回一个详情对象，按“预警信息、患者信息、心电原始数据、AI诊断信息”聚合。

| 字段 | 类型 | 说明 |
|---|---|---|
| alertId | long | 预警ID |
| warningTime | string | 预警时间 |
| alertLevel | integer | 预警等级编码 |
| alertLevelText | string | 预警等级文本 |
| alertStatus | integer | 处理状态编码 |
| alertStatusText | string | 处理状态文本 |
| warningType | string | 预警类型 |
| warningDesc | string | 预警描述 |
| lisHint | string | LIS辅助提示 |
| handleUserName | string | 处理人 |
| handleTime | string | 处理时间 |
| handleRemark | string | 处理意见 |
| patientId | long | 患者ID |
| patientName | string | 患者姓名 |
| age | integer | 年龄 |
| genderText | string | 性别 |
| inpatientNo | string | 住院号 |
| wardName | string | 病区 |
| bedNo | string | 床号 |
| phone | string | 联系电话 |
| primaryDiagnosis | string | 主要诊断 |
| recordId | long | 心电采集记录ID |
| ecgNo | string | 心电图编号 |
| deviceId | long | 设备ID |
| deviceName | string | 设备名称 |
| leadCount | integer | 导联数 |
| samplingRate | integer | 采样率 |
| collectionDuration | integer | 采集时长（秒） |
| collectionStartTime | string | 采集开始时间 |
| collectionEndTime | string | 采集结束时间 |
| ecgDataFileUrl | string | 心电原始数据文件地址 |
| uploadSourceFileUrl | string | 源文件地址 |
| aiDiagnosisId | long | AI诊断ID |
| diagnosisNo | string | AI诊断编号 |
| aiModelVersion | string | 模型版本 |
| aiConclusion | string | AI完整结论 |
| heartRate | integer | 心率 |
| prInterval | integer | PR间期 |
| qrsDuration | integer | QRS时限 |
| qtInterval | integer | QT间期 |
| qtcInterval | integer | QTc间期 |
| abnormalType | string | 异常类型 |
| abnormalCount | integer | 异常数量 |
| abnormalLevel | integer | 异常级别编码 |
| abnormalLevelText | string | 异常级别文本 |
| confidence | number | 置信度 |
| analysisStatus | integer | AI分析状态编码 |
| analysisStatusText | string | AI分析状态文本 |
| diagnosisTime | string | 诊断完成时间 |

---

## 44.2 跳转全量预警列表前置接口

### 44.2.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 跳转全量预警列表前置接口 |
| 业务作用 | “查看全部”跳转入口，加载完整预警监控页面初始化数据（顶部统计 + 筛选字典 + 首屏分页） |
| 请求路径 | /api/analysis/dashboard/warnings/full-page/init |
| 请求方式 | POST |
| Content-Type | application/json |
| 权限码 | analysis:dashboard:warning:full-page |

### 44.2.2 请求入参

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|---|---|---|---|---|
| keyword | string | 否 | 空 | 患者姓名/住院号/预警类型关键词 |
| ward | string | 否 | 空 | 病区名或病区ID |
| alertLevel | string | 否 | 空 | 预警级别（1/2/3 或 低危/中危/高危） |
| alertStatus | string | 否 | 空 | 处理状态（0-4 或 待确认/待处理/处理中/已处理/已忽略） |
| startTime | string | 否 | 空 | 开始时间 |
| endTime | string | 否 | 空 | 结束时间 |
| pageNum | long | 否 | 1 | 页码 |
| pageSize | long | 否 | 10 | 每页条数，最大 200 |

### 44.2.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| highRiskCount | long | 高危预警数（warning_level=3） |
| pendingHandleCount | long | 待处理预警数（handle_status in 0/1/2） |
| wardOptions | array | 病区筛选字典 |
| alertLevelOptions | array | 预警级别字典 |
| alertStatusOptions | array | 预警状态字典 |
| pageData | object | 首屏分页数据 |

`pageData` 结构：

| 字段 | 类型 | 说明 |
|---|---|---|
| total | long | 总记录数 |
| pages | long | 总页数 |
| list | array | 当前页记录 |

`pageData.list[]` 字段：

| 字段 | 类型 | 说明 |
|---|---|---|
| warningId | long | 预警ID |
| warningTime | string | 预警时间 |
| patientName | string | 患者姓名 |
| genderText | string | 性别 |
| age | integer | 年龄 |
| patientInfo | string | 患者信息合成字段 |
| inpatientNo | string | 住院号 |
| wardName | string | 病区 |
| warningType | string | 预警类型 |
| alertLevel | integer | 预警等级编码 |
| alertLevelText | string | 预警等级文本 |
| alertStatus | integer | 处理状态编码 |
| alertStatusText | string | 处理状态文本 |
| handleTime | string | 处理时间 |

---

## 44.3 预警纳入科研/重点监护接口

### 44.3.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 预警纳入科研/重点监护 |
| 业务作用 | 列表“纳入”按钮使用：按预警ID将患者纳入重点管理，并同步纳入科研数据池 |
| 请求路径 | /api/analysis/dashboard/warnings/include |
| 请求方式 | POST |
| Content-Type | application/json |
| 权限码 | analysis:dashboard:warning:include |

### 44.3.2 请求入参

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| warningId | long | 是 | 预警ID（对应 ecg_abnormal_warning.warning_id） |

请求示例：

```json
{
  "warningId": 2400005
}
```

### 44.3.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| warningId | long | 预警ID |
| patientId | long | 患者ID |
| keyMonitorIncluded | boolean | 是否已纳入重点监护 |
| researchIncluded | boolean | 是否已纳入科研数据 |
| monitorId | long | 重点监护记录ID（ecg_real_time_monitor.monitor_id） |
| researchId | long | 科研归档记录ID（ecg_research_data.research_id） |
| includedBy | string | 操作人 |
| includedTime | string | 纳入时间 |

### 44.3.4 业务规则

1. 根据 warningId 查询 ecg_abnormal_warning，要求 is_deleted=0 且记录存在。
2. 从预警记录提取 patient_id、record_id、ai_diagnosis_id、dept_id、dept_name 等字段作为纳入依据。
3. 重点监护纳入：将 ecg_real_time_monitor.is_key_monitor 置为 1；若监护记录不存在则按患者快照创建监护记录。
4. 科研纳入：向 ecg_research_data 归档数据；若存在同 patient_id + record_id 的有效记录则幂等返回，不重复插入。
5. 该接口为幂等接口：重复点击“纳入”按钮，不应产生重复科研记录与重复监护关系。
6. 操作需记录审计日志（操作对象 warningId、patientId、操作者、时间、IP）。

### 44.3.5 响应示例

```json
{
  "code": 0,
  "message": "纳入成功",
  "data": {
    "warningId": 2400005,
    "patientId": 2005,
    "keyMonitorIncluded": true,
    "researchIncluded": true,
    "monitorId": 2605,
    "researchId": 3006,
    "includedBy": "admin",
    "includedTime": "2026-04-21T10:30:20"
  },
  "timestamp": 1776588220203
}
```

---

## 44.4 错误场景

| 场景 | code | message 示例 |
|---|---|---|
| alertId 非法 | 400 | alertId 参数不合法 |
| alertLevel 非法 | 400 | alertLevel 参数不合法 |
| alertStatus 非法 | 400 | alertStatus 参数不合法 |
| 时间格式错误 | 400 | time 参数格式错误 |
| 预警不存在 | 404 | 预警不存在 |

---

## 39. 诊断报告管理模块接口规范

## 39.1 诊断报告分页列表查询接口

### 39.1.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 诊断报告分页列表查询 |
| 请求路径 | /api/analysis/diagnosis-report/page |
| 请求方式 | POST |
| Content-Type | application/json |
| 权限码 | analysis:diagnosis-report:page |

### 39.1.2 请求入参

```json
{
  "reportNo": "",
  "patientName": "",
  "status": "",
  "pageNum": 1,
  "pageSize": 10
}
```

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|---|---|---|---|---|
| reportNo | string | 否 | 空 | 报告编号（模糊匹配） |
| patientName | string | 否 | 空 | 患者姓名（模糊匹配） |
| status | string | 否 | 空 | 报告状态：待生成/已审核 |
| pageNum | long | 否 | 1 | 页码 |
| pageSize | long | 否 | 10 | 每页条数，最大 200 |

### 39.1.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| total | long | 总记录数 |
| pages | long | 总页数 |
| list | array | 当前页记录 |

list[] 字段：

| 字段 | 类型 | 说明 |
|---|---|---|
| reportId | long | 报告唯一 ID |
| reportNo | string | 报告编号 |
| patientInfo | string | 患者信息（姓名/年龄/性别） |
| hospitalNo | string | 住院号 |
| collectionTime | string | 采集时间 |
| aiConclusion | string | AI 结论 |
| doctorConclusion | string | 医生结论 |
| auditDoctorName | string | 审核医生 |
| auditTime | string | 审核时间 |
| status | string | 报告状态（待生成/已审核） |

### 39.1.4 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "total": 5,
    "pages": 1,
    "list": [
      {
        "reportId": 2301,
        "reportNo": "REP20260418001",
        "patientInfo": "张建国/67岁/男",
        "hospitalNo": "ZY202604001",
        "collectionTime": "2026-04-18T08:00:00",
        "aiConclusion": "窦性心律，偶发室性早搏，建议结合临床复核",
        "doctorConclusion": "偶发室早，继续药物治疗并复查",
        "auditDoctorName": "孙主任",
        "auditTime": "2026-04-18T08:45:00",
        "status": "已审核"
      }
    ]
  },
  "timestamp": 1776502056203
}
```

---

## 39.2 公共筛选字典接口

### 39.2.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 诊断报告筛选字典 |
| 请求路径 | /api/analysis/diagnosis-report/dicts |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | analysis:diagnosis-report:dict |

### 39.2.2 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| statusList | array | 报告状态枚举 |

statusList[] 字段：

| 字段 | 类型 | 说明 |
|---|---|---|
| label | string | 展示名称 |
| value | string | 字典值 |

返回示例：

1. 全部状态
2. 待生成
3. 已审核

---

## 39.3 单条报告详情查询接口

### 39.3.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 单条诊断报告详情查询 |
| 请求路径 | /api/analysis/diagnosis-report/{reportId} |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | analysis:diagnosis-report:detail |

### 39.3.2 请求入参

| 参数位置 | 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|---|
| Path | reportId | long | 是 | 报告唯一 ID |

### 39.3.3 出参说明

返回全量报告字段，覆盖：

1. 患者基础信息（patientName、gender、age、hospitalNo）。
2. 采集时间（collectionTime）。
3. AI 原始诊断结论（aiConclusion）。
4. 医生终审结论（doctorConclusion）与医生建议（doctorSuggestion）。
5. 报告生成信息（reportCreateDoctorName、reportCreateTime）。
6. 审核信息（auditDoctorName、auditTime、auditOpinion）。

---

## 39.4 报告 PDF 下载接口

### 39.4.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 诊断报告PDF下载 |
| 请求路径 | /api/analysis/diagnosis-report/{reportId}/pdf |
| 请求方式 | GET |
| Content-Type | application/pdf |
| 权限码 | analysis:diagnosis-report:download |

### 39.4.2 请求入参

| 参数位置 | 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|---|
| Path | reportId | long | 是 | 报告唯一 ID |

### 39.4.3 响应说明

1. 响应头为 application/pdf。
2. 服务端基于报告详情动态合成 PDF 内容。
3. 响应头 Content-Disposition 为 attachment，前端可直接触发浏览器下载。

---

## 39.5 报告打印接口说明

打印场景不新增独立后端接口，直接复用 [39.3 单条报告详情查询接口] 返回数据进行前端打印排版。

推荐前端处理流程：

1. 调用详情接口加载完整报告数据。
2. 按打印模板渲染页面（A4 版式）。
3. 调用浏览器 print API 输出打印。

---

## 40. 诊断报告管理模块错误场景

| 场景 | code | message 示例 |
|---|---|---|
| reportId 参数非法 | 400 | reportId 参数不合法 |
| status 参数非法 | 400 | status 参数不合法 |
| 诊断报告不存在 | 404 | 诊断报告不存在 |
| 报告 PDF 生成失败 | 5000 | 报告 PDF 生成失败 |
| 服务内部异常 | 5000 | 系统繁忙，请稍后重试 |

---

## 41. 数据分析驾驶舱（重构版）接口规范

## 41.1 顶部大盘核心统计指标

### 41.1.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 数据分析大盘核心统计指标 |
| 请求路径 | /api/analysis/dashboard/core-metrics |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | analysis:dashboard:core:read |

### 41.1.2 请求入参

| 参数位置 | 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|---|
| Query | startTime | string | 否 | 开始时间，支持 yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss |
| Query | endTime | string | 否 | 结束时间，支持 yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss |

### 41.1.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| ecgTotal | long | 心电采集总量 |
| pendingAnalyse | long | 待分析数量（ai_analysis_status in 0/1） |
| pendingAudit | long | 待审核数量（report_status = 1） |
| abnormalWarning | long | 异常高危预警数（warning_level = 3） |
| warningTotal | long | 预警总数 |
| reportTotal | long | 报告总数 |
| aiAccuracy | number | AI准确率（基于 ecg_ai_diagnosis.confidence 平均值） |

### 41.1.4 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "ecgTotal": 2453,
    "pendingAnalyse": 194,
    "pendingAudit": 28,
    "abnormalWarning": 11,
    "warningTotal": 564,
    "reportTotal": 118,
    "aiAccuracy": 92.8
  },
  "timestamp": 1776501256203
}
```

---

## 41.2 预警级别分布数据

### 41.2.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 预警级别分布数据 |
| 请求路径 | /api/analysis/dashboard/warning-level-distribution |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | analysis:dashboard:warning-level:read |

### 41.2.2 请求入参

| 参数位置 | 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|---|
| Query | startTime | string | 否 | 开始时间，支持 yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss |
| Query | endTime | string | 否 | 结束时间，支持 yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss |

### 41.2.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| lowRiskCount | long | 低危数量（warning_level = 1） |
| middleRiskCount | long | 中危数量（warning_level = 2） |
| highRiskCount | long | 高危数量（warning_level = 3） |

---

## 41.3 预警类型 + 病区 TOP 排行数据

### 41.3.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 预警类型+病区TOP排行 |
| 请求路径 | /api/analysis/dashboard/warning-type-ward-top |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | analysis:dashboard:warning-top:read |

### 41.3.2 请求入参

| 参数位置 | 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|---|
| Query | startTime | string | 否 | 开始时间，支持 yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss |
| Query | endTime | string | 否 | 结束时间，支持 yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss |

### 41.3.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| warningTypeStats | array | 预警类型占比列表（饼图） |
| wardTopStats | array | 病区预警数量TOP列表（横向柱状图） |

warningTypeStats[] 字段：

| 字段 | 类型 | 说明 |
|---|---|---|
| warningType | string | 预警类型名称 |
| count | long | 数量 |
| ratio | number | 占比（百分比） |

wardTopStats[] 字段：

| 字段 | 类型 | 说明 |
|---|---|---|
| wardName | string | 病区名称 |
| warningCount | long | 预警数量 |

---

## 41.4 近 7 日预警趋势折线数据

### 41.4.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 近7日预警趋势 |
| 请求路径 | /api/analysis/dashboard/warning-trend-7d |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | analysis:dashboard:warning-trend:read |

### 41.4.2 请求入参

无业务入参，默认返回最近 7 个自然日（含当天）趋势。

### 41.4.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| dateList | array | 日期数组（yyyy-MM-dd） |
| warningCountList | array | 每日预警数量数组，与 dateList 一一对应 |

### 41.4.4 响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "dateList": ["2026-04-15", "2026-04-16", "2026-04-17", "2026-04-18", "2026-04-19", "2026-04-20", "2026-04-21"],
    "warningCountList": [7, 9, 11, 8, 10, 6, 5]
  },
  "timestamp": 1776501256203
}
```

---

## 41.5 待处理预警分页列表（数据分析）

### 41.5.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 待处理预警分页列表（数据分析） |
| 请求路径 | /api/analysis/dashboard/pending-warnings/page |
| 请求方式 | POST |
| Content-Type | application/json |
| 权限码 | analysis:dashboard:pending-warning:page |

### 41.5.2 请求入参

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|---|---|---|---|---|
| startTime | string | 否 | 空 | 开始时间，支持 yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss |
| endTime | string | 否 | 空 | 结束时间，支持 yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss |
| pageNum | long | 否 | 1 | 页码 |
| pageSize | long | 否 | 10 | 每页条数，最大 200 |

### 41.5.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| total | long | 总记录数 |
| pages | long | 总页数 |
| list | array | 当前页数据 |

list[] 字段：

| 字段 | 类型 | 说明 |
|---|---|---|
| warningId | long | 预警ID |
| warningTime | string | 预警时间 |
| patientInfo | string | 患者信息（姓名/性别/年龄） |
| ward | string | 病区 |
| clinicalIndicator | string | 临床指标 |
| warningLevel | string | 预警级别 |
| status | string | 状态 |

---

## 41.6 最新心电记录分页列表（数据分析）

### 41.6.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 最新心电记录分页列表（数据分析） |
| 请求路径 | /api/analysis/dashboard/latest-ecg/page |
| 请求方式 | POST |
| Content-Type | application/json |
| 权限码 | analysis:dashboard:latest-ecg:page |

### 41.6.2 请求入参

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|---|---|---|---|---|
| startTime | string | 否 | 空 | 开始时间，支持 yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss |
| endTime | string | 否 | 空 | 结束时间，支持 yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss |
| pageNum | long | 否 | 1 | 页码 |
| pageSize | long | 否 | 10 | 每页条数，最大 200 |

### 41.6.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| total | long | 总记录数 |
| pages | long | 总页数 |
| list | array | 最新采集心电全量记录 |

list[] 关键字段（全量快照）：

| 字段 | 类型 | 说明 |
|---|---|---|
| recordId | long | 采集记录ID |
| ecgNo | string | 心电图编号 |
| patientId | long | 患者ID |
| patientName | string | 患者姓名 |
| gender | string | 性别 |
| age | integer | 年龄 |
| inpatientNo | string | 住院号 |
| deptId | long | 病区ID |
| deptName | string | 病区名称 |
| bedNo | string | 床号 |
| deviceId | long | 设备ID |
| deviceName | string | 设备名称 |
| leadCount | integer | 导联数 |
| samplingRate | integer | 采样率 |
| collectionDuration | integer | 采集时长（秒） |
| collectionType | integer | 采集类型编码 |
| collectionTypeText | string | 采集类型文本 |
| collectStartTime | string | 采集开始时间 |
| collectEndTime | string | 采集结束时间 |
| aiAnalysisStatus | integer | AI分析状态 |
| reportStatus | integer | 报告状态 |
| displayStatus | integer | 列表展示状态 |
| statusText | string | 列表展示状态文本 |
| aiConclusionShort | string | AI简短结论 |
| aiConclusion | string | AI完整结论 |

---

## 42. AI诊断中心（重构版）接口规范

说明：本章为重构版驾驶舱接口，和 37 章历史接口并行提供，便于前后端平滑切换。

## 42.1 AI引擎运行状态

### 42.1.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | AI引擎运行状态 |
| 请求路径 | /api/analysis/ai-diagnosis/dashboard/engine-status |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | analysis:ai-diagnosis:engine-status:read |

### 42.1.2 请求入参

无业务入参。

### 42.1.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| engineStatus | string | 引擎状态编码：RUNNING/PENDING/IDLE |
| engineStatusText | string | 引擎状态文本 |
| engineVersion | string | 当前模型版本 |
| runningInstanceCount | long | 运行实例数 |
| queueBacklogCount | long | 队列积压任务数 |
| todayAnalysisCount | long | 今日完成分析数 |
| avgAnalysisSeconds | number | 今日平均分析耗时（秒） |
| lastHeartbeatTime | string | 最近心跳时间 |
| statusMessage | string | 状态说明 |

---

## 42.2 AI核心指标（含同比）

### 42.2.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | AI核心指标（含同比） |
| 请求路径 | /api/analysis/ai-diagnosis/dashboard/core-metrics |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | analysis:ai-diagnosis:core-metrics:read |

### 42.2.2 请求入参

| 参数位置 | 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|---|
| Query | startTime | string | 否 | 开始时间，支持 yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss |
| Query | endTime | string | 否 | 结束时间，支持 yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss |

### 42.2.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| diagnosisTotal | long | AI诊断总量 |
| diagnosisYoYRate | number | 诊断总量同比（%） |
| pendingAuditCount | long | 待审核数量 |
| pendingAuditYoYRate | number | 待审核同比（%） |
| auditedPassCount | long | 审核通过数量 |
| auditedPassYoYRate | number | 审核通过同比（%） |
| abnormalCount | long | 异常判定数量 |
| abnormalYoYRate | number | 异常数量同比（%） |
| normalCount | long | 正常判定数量 |
| normalYoYRate | number | 正常数量同比（%） |

---

## 42.3 AI审核趋势（7/15/30）

### 42.3.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | AI审核趋势（7/15/30） |
| 请求路径 | /api/analysis/ai-diagnosis/dashboard/warning-trend |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | analysis:ai-diagnosis:trend:read |

### 42.3.2 请求入参

| 参数位置 | 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|---|
| Query | timeRange | string | 否 | 时间窗，仅支持 7 / 15 / 30，默认 7 |

### 42.3.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| dateList | array | 日期数组（yyyy-MM-dd） |
| pendingAuditList | array | 每日待审核数量 |
| passList | array | 每日审核通过数量 |
| rejectList | array | 每日审核驳回数量 |

---

## 42.4 AI异常类型占比饼图

### 42.4.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | AI异常类型占比饼图 |
| 请求路径 | /api/analysis/ai-diagnosis/dashboard/abnormal-type-ratio |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | analysis:ai-diagnosis:abnormal-ratio:read |

### 42.4.2 请求入参

| 参数位置 | 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|---|
| Query | startTime | string | 否 | 开始时间 |
| Query | endTime | string | 否 | 结束时间 |

### 42.4.3 出参说明

返回数组，每项字段：

| 字段 | 类型 | 说明 |
|---|---|---|
| abnormalType | string | 异常类型 |
| count | long | 数量 |
| ratio | number | 占比（%） |

---

## 42.5 AI诊断记录分页（重构版）

### 42.5.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | AI诊断记录分页（重构版） |
| 请求路径 | /api/analysis/ai-diagnosis/dashboard/page |
| 请求方式 | POST |
| Content-Type | application/json |
| 权限码 | analysis:ai-diagnosis:dashboard:page |

### 42.5.2 请求入参

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|---|---|---|---|---|
| keyword | string | 否 | 空 | 关键字（姓名/住院号/心电编号/诊断编号） |
| deptId | long | 否 | 空 | 病区ID |
| startTime | string | 否 | 空 | 开始时间 |
| endTime | string | 否 | 空 | 结束时间 |
| pageNum | long | 否 | 1 | 页码 |
| pageSize | long | 否 | 10 | 每页条数，最大 200 |

### 42.5.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| total | long | 总记录数 |
| pages | long | 总页数 |
| list | array | 当前页数据 |

list[] 字段：

| 字段 | 类型 | 说明 |
|---|---|---|
| diagnosisId | string | 诊断编号 |
| aiDiagnosisId | long | AI诊断ID |
| ecgId | long | 采集记录ID |
| ecgNo | string | 心电图编号 |
| patientName | string | 患者姓名 |
| gender | string | 性别 |
| age | integer | 年龄 |
| inpatientNo | string | 住院号 |
| deptId | long | 病区ID |
| deptName | string | 病区名称 |
| aiConclusion | string | AI结论 |
| abnormalType | string | 异常类型 |
| abnormalCount | integer | 异常数量 |
| confidence | number | 置信度 |
| reportStatus | integer | 报告状态 |
| reportStatusText | string | 报告状态文本 |
| warningLevel | integer | 最高预警级别 |
| warningLevelText | string | 最高预警级别文本 |
| diagnosisTime | string | 诊断时间 |

---

## 42.6 AI诊断轻量详情

### 42.6.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | AI诊断轻量详情 |
| 请求路径 | /api/analysis/ai-diagnosis/dashboard/{diagnosisId}/lite |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | analysis:ai-diagnosis:lite:read |

### 42.6.2 请求入参

| 参数位置 | 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|---|
| Path | diagnosisId | string | 是 | 诊断编号（支持 diagnosis_no / ai_diagnosis_id） |

### 42.6.3 出参说明

用于列表侧边栏或弹窗快速查看，包含：患者快照、AI结论、异常信息、报告状态与预警级别。

---

## 42.7 AI审核完整详情

### 42.7.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | AI审核完整详情 |
| 请求路径 | /api/analysis/ai-diagnosis/dashboard/{diagnosisId}/audit-detail |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | analysis:ai-diagnosis:audit-detail:read |

### 42.7.2 请求入参

| 参数位置 | 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|---|
| Path | diagnosisId | string | 是 | 诊断编号（支持 diagnosis_no / ai_diagnosis_id） |

### 42.7.3 出参说明

在轻量详情基础上增加：

1. 心电测量参数（heartRate/prInterval/qrsDuration/qtInterval/qtcInterval）。
2. 报告审核信息（reportNo/reportStatus/doctorConclusion/doctorSuggestion/auditDoctorName/auditTime/auditOpinion）。
3. 关联预警列表 warningList（级别、处理状态、意见）。

---

## 42.8 AI审核提交（闭环）

### 42.8.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | AI审核提交（闭环） |
| 请求路径 | /api/analysis/ai-diagnosis/dashboard/audit/submit |
| 请求方式 | POST |
| Content-Type | application/json |
| 权限码 | report:audit / diagnosis:write / analysis:ai-diagnosis:audit |

### 42.8.2 请求入参

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| diagnosisId | string | 是 | 诊断编号 |
| adoptAiFlag | integer | 否 | 是否采纳AI结论：1-是，0-否，默认 1 |
| markNormalFlag | integer | 否 | 是否标记正常：1-是，0-否，默认 0 |
| finalAuditResult | string | 是 | 终审结果：PASS/REJECT（兼容 2/3、通过/驳回） |
| doctorConclusion | string | 是 | 医生终审结论，最大 2000 |
| doctorSuggestion | string | 否 | 医生建议，最大 2000 |
| auditOpinion | string | 否 | 审核意见，最大 256 |

### 42.8.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| diagnosisId | string | 诊断编号 |
| finalAuditResult | string | 终审结果文本 |
| reportStatus | integer | 报告状态 |
| status | string | 状态文本 |
| auditedBy | string | 审核医生 |
| auditedTime | string | 审核时间 |
| reportId | long | 报告ID |
| reportNo | string | 报告编号 |
| warningSyncCount | integer | 同步处理的预警条数 |
| markNormalFlag | integer | 回显标记正常参数 |
| adoptAiFlag | integer | 回显采纳AI参数 |

### 42.8.4 状态闭环规则

1. 写入/更新诊断报告（ecg_diagnosis_report），同步 report_status。
2. 同步采集记录状态（ecg_collection_record.report_status、display_status）。
3. 同步 AI 结果快照（ecg_ai_diagnosis.is_abnormal、abnormal_level、abnormal_type、abnormal_count）。
4. 同步预警处理状态（ecg_abnormal_warning.handle_status、处理人、处理时间、处理意见）。
5. 已审核通过（report_status=2）和已作废（report_status=4）记录禁止重复提交。

---

## 42.9 AI重构版错误场景

| 场景 | code | message 示例 |
|---|---|---|
| diagnosisId 参数非法 | 400 | diagnosisId 参数不合法 |
| timeRange 参数非法 | 400 | timeRange 参数不合法 |
| timeRange 非 7/15/30 | 400 | timeRange 仅支持 7、15、30 |
| finalAuditResult 参数非法 | 400 | finalAuditResult 参数不合法 |
| adoptAiFlag/markNormalFlag 参数非法 | 400 | adoptAiFlag 参数不合法 |
| doctorConclusion 为空 | 400 | doctorConclusion 参数不合法 |
| doctorConclusion 过长 | 400 | doctorConclusion 长度不能超过 2000 |
| doctorSuggestion 过长 | 400 | doctorSuggestion 长度不能超过 2000 |
| auditOpinion 过长 | 400 | auditOpinion 长度不能超过 256 |
| AI 诊断记录不存在 | 404 | AI 诊断记录不存在 |
| 当前用户无审核权限 | 403 | 无 AI 诊断审核权限 |
| 已审核记录重复提交 | 400 | 该诊断记录已审核，不能重复提交 |

---

## 43. 患者管理写接口规范

## 43.1 患者详情回响接口

### 43.1.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 患者详情回响 |
| 业务作用 | 打开患者详情弹窗或修改弹窗时初始化数据 |
| 请求路径 | /api/patient/{id} |
| 请求方式 | GET |
| Content-Type | application/json |
| 权限码 | patient:detail:read |

### 43.1.2 请求入参

| 参数位置 | 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|---|
| Path | id | long | 是 | 患者唯一 ID |

### 43.1.3 出参说明

| 字段 | 类型 | 说明 |
|---|---|---|
| id | long | 患者ID |
| name | string | 患者姓名 |
| gender | string | 性别枚举（MALE/FEMALE） |
| genderText | string | 性别文本（男/女） |
| age | integer | 年龄 |
| inpatientNo | string | 住院号/居家编号 |
| ward | string | 病区名称 |
| bedNo | string | 床位号 |
| status | string | 患者状态枚举（IN_HOSPITAL/DISCHARGED/HOME_FOLLOW） |
| statusText | string | 患者状态文本（在院/出院/居家随访） |
| riskLevel | string | 风险等级枚举（LOW/MEDIUM/MEDIUM_HIGH/HIGH） |
| riskLevelText | string | 风险等级文本（低危/中危/中高危/高危） |
| deviceNo | string | 设备编号 |
| admissionTime | string | 入院时间（ISO-8601） |
| dischargeTime | string/null | 出院时间（ISO-8601） |
| diagnosis | string | 主要诊断 |
| ecgCount | integer | 心电采集次数 |
| lastEcgTime | string | 最新采集时间（ISO-8601） |

### 43.1.4 完整请求示例

```http
GET /api/patient/1904 HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
```

### 43.1.5 完整响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "id": 1904,
    "name": "陈敏",
    "gender": "FEMALE",
    "genderText": "女",
    "age": 49,
    "inpatientNo": "ZY202604004",
    "ward": "冠心监护病区",
    "bedNo": "ICU-01",
    "status": "IN_HOSPITAL",
    "statusText": "在院",
    "riskLevel": "HIGH",
    "riskLevelText": "高危",
    "deviceNo": "1804",
    "admissionTime": "2026-04-15T14:35:00",
    "dischargeTime": null,
    "diagnosis": "急性冠脉综合征（监护中）",
    "ecgCount": 5,
    "lastEcgTime": "2026-04-18T09:40:00"
  },
  "timestamp": 1776501256203
}
```

---

## 43.2 修改患者信息接口

### 43.2.1 接口信息

| 项 | 内容 |
|---|---|
| 接口名称 | 修改患者信息 |
| 业务作用 | 修改患者基本信息并同步冗余表数据 |
| 请求路径 | /api/patient/{patientId} |
| 请求方式 | PUT |
| Content-Type | application/json |
| 权限码 | patient:update |

### 43.2.2 请求入参

| 参数位置 | 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|---|
| Path | patientId | long | 是 | 患者唯一 ID |
| Body | name | string | 否 | 患者姓名 |
| Body | gender | string | 否 | 性别枚举（MALE/FEMALE） |
| Body | age | integer | 否 | 年龄 |
| Body | ward | string | 否 | 病区名称 |
| Body | bedNo | string | 否 | 床位号 |
| Body | deviceNo | string | 否 | 设备编号 |
| Body | inpatientNo | string | 否 | 住院号/居家编号 |
| Body | riskLevel | string | 否 | 风险等级枚举（LOW/MEDIUM/MEDIUM_HIGH/HIGH） |
| Body | status | string | 否 | 患者状态枚举（IN_HOSPITAL/DISCHARGED/HOME_FOLLOW） |
| Body | admissionTime | string | 否 | 入院时间（ISO-8601） |
| Body | dischargeTime | string | 否 | 出院时间（ISO-8601，可为空） |
| Body | diagnosis | string | 否 | 主要诊断 |

### 43.2.3 出参说明

无业务数据返回，成功时 data 为 null。

### 43.2.4 冗余表同步规则

修改患者信息时，系统自动同步以下冗余表的患者快照字段：

| 冗余表 | 同步字段 |
|---|---|
| ecg_collection_record | patient_name, gender, age, inpatient_no |
| ecg_ai_diagnosis | patient_name, gender, age, inpatient_no |
| ecg_diagnosis_report | patient_name, gender, age, inpatient_no |
| ecg_abnormal_warning | patient_name, gender, age, inpatient_no |
| ecg_real_time_monitor | patient_name |
| patient_follow_up_record | patient_name |
| ecg_research_data | patient_name, gender, age, inpatient_no |

### 43.2.5 完整请求示例

```http
PUT /api/patient/1904 HTTP/1.1
Host: 127.0.0.1:8080
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "陈敏",
  "gender": "FEMALE",
  "age": 50,
  "ward": "冠心监护病区",
  "bedNo": "ICU-01",
  "deviceNo": "1804",
  "inpatientNo": "ZY202604004",
  "riskLevel": "HIGH",
  "status": "IN_HOSPITAL",
  "admissionTime": "2026-04-15T14:35:00",
  "dischargeTime": null,
  "diagnosis": "急性冠脉综合征（恢复期）"
}
```

### 43.2.6 完整响应示例

```json
{
  "code": 0,
  "message": "成功",
  "data": null,
  "timestamp": 1776501256203
}
```

---

## 43.3 患者管理写接口业务规则

1. 患者详情回响接口用于详情弹窗和修改弹窗初始化数据，返回枚举字符串和中文文本双字段。
2. 修改患者信息接口支持部分更新，前端未传的字段保留原值。
3. 修改接口使用事务管理，主表更新失败时自动回滚。
4. 冗余表同步在事务内执行，确保数据一致性。
5. 性别枚举：MALE（男）、FEMALE（女）。
6. 风险等级枚举：LOW（低危）、MEDIUM（中危）、MEDIUM_HIGH（中高危）、HIGH（高危）。
7. 患者状态枚举：IN_HOSPITAL（在院）、DISCHARGED（出院）、HOME_FOLLOW（居家随访）。

## 43.4 患者管理写接口错误场景

| 场景 | code | message 示例 |
|---|---|---|
| patientId 为空或非法 | 400 | patientId 参数不合法 |
| gender 传入非法值 | 400 | gender 参数不合法，应为 MALE 或 FEMALE |
| riskLevel 传入非法值 | 400 | riskLevel 参数不合法，应为 LOW/MEDIUM/MEDIUM_HIGH/HIGH |
| status 传入非法值 | 400 | patientStatus 参数不合法，应为 IN_HOSPITAL/DISCHARGED/HOME_FOLLOW |
| 患者不存在 | 404 | 患者不存在 |
| 更新失败 | 400 | 更新患者信息失败 |
