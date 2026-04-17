-- 心电图情报分析系统 完整 SQL 建表脚本
-- 脚本说明
-- 字符集：utf8mb4（支持医疗特殊字符与全量 Unicode，符合医疗数据存储规范）
-- 存储引擎：InnoDB（支持事务、行级锁，保障医疗数据一致性）
-- 核心原则：全程无物理外键约束，所有关联通过业务 ID 实现；核心展示字段冗余设计，单表即可完成列表渲染；全表逻辑删除，满足医疗数据合规追溯要求
-- 注释规范：表注释说明表的核心作用与业务定位；字段注释说明字段含义、业务规则、枚举值定义；每个表后附详细业务逻辑说明



-- 脚本使用说明
-- 执行环境：建议在 MySQL 5.7 及以上版本执行，InnoDB 引擎支持事务与行级锁，适合医疗系统的高并发场景。
-- 主键生成：所有表的主键均为BIGINT类型，建议使用 ** 雪花算法（Snowflake）** 生成唯一 ID，避免自增 ID 的性能瓶颈与数据泄露风险。
-- 初始数据：脚本执行完成后，可先初始化sys_department（科室病区）、sys_role（角色）、sys_user（管理员账号）等基础数据。
-- 业务层配套：需在业务代码中实现冗余字段的同步更新、状态联动逻辑、数据一致性校验（详见每个表的「业务逻辑说明」）。

-- ----------------------------
-- 创建数据库
-- ----------------------------
CREATE DATABASE IF NOT EXISTS `ecg_intelligence_analysis_system`
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_unicode_ci
COMMENT '心电图情报分析系统：支撑心电数据采集、AI诊断、报告审核、预警监护、设备管理、患者管理、统计分析全业务流程的核心数据库';

USE `ecg_intelligence_analysis_system`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 系统权限域 - 系统角色表
-- ----------------------------
CREATE TABLE `sys_role` (
                            `role_id` BIGINT NOT NULL COMMENT '角色唯一ID（雪花ID）',
                            `role_name` VARCHAR(32) NOT NULL COMMENT '角色名称（如心内科医生、护士、设备管理员）',
                            `description` VARCHAR(256) DEFAULT NULL COMMENT '角色描述/权限说明',
                            `user_count` INT NOT NULL DEFAULT 0 COMMENT '关联用户数（冗余字段，业务层定时统计更新）',
                            `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
                            `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
                            PRIMARY KEY (`role_id`),
                            UNIQUE INDEX `idx_role_name` (`role_name`),
                            INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表：存储系统角色基础信息与权限范围，支撑RBAC权限模型；user_count字段冗余，避免实时统计用户数的联查开销；无物理外键约束。';

-- 业务逻辑说明：
-- 1. 权限关联：通过sys_role_permission表关联权限标识，无物理外键，权限校验由业务层完成。
-- 2. 冗余字段：user_count由业务层通过「事件触发+定时任务」双机制更新，角色管理页面直接展示，无需实时COUNT查询。
-- 3. 数据安全：采用逻辑删除，保留所有历史角色数据，满足审计追溯要求。


-- ----------------------------
-- 2. 系统权限域 - 角色权限关联表
-- ----------------------------
CREATE TABLE `sys_role_permission` (
                                       `id` BIGINT NOT NULL COMMENT '主键ID（雪花ID）',
                                       `role_id` BIGINT NOT NULL COMMENT '角色ID（业务关联，无外键约束）',
                                       `permission_code` VARCHAR(64) NOT NULL COMMENT '权限标识（如report:audit、ecg:upload）',
                                       `permission_name` VARCHAR(64) DEFAULT NULL COMMENT '权限名称（如审核报告、上传心电数据）',
                                       `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
                                       PRIMARY KEY (`id`),
                                       INDEX `idx_role_id` (`role_id`),
                                       INDEX `idx_permission_code` (`permission_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表：存储角色与权限的多对多关联关系，支撑细粒度权限控制；无物理外键约束，关联一致性由业务层保障。';

-- 业务逻辑说明：
-- 1. 关联逻辑：通过role_id与sys_role表业务关联，permission_code为权限唯一标识，权限列表由业务代码维护。
-- 2. 性能优化：role_id建立索引，快速查询某角色的所有权限；无外键约束，提升权限配置的操作性能。


-- ----------------------------
-- 3. 系统权限域 - 科室病区字典表
-- ----------------------------
CREATE TABLE `sys_department` (
                                  `dept_id` BIGINT NOT NULL COMMENT '科室/病区唯一ID（雪花ID）',
                                  `parent_id` BIGINT DEFAULT NULL COMMENT '上级科室ID（科室-病区层级关联，无外键约束）',
                                  `parent_name` VARCHAR(64) DEFAULT NULL COMMENT '上级科室名称（冗余快照，避免层级联查）',
                                  `dept_name` VARCHAR(64) NOT NULL COMMENT '科室/病区全称（如心血管内科一病区、居家监护站）',
                                  `dept_code` VARCHAR(32) NOT NULL COMMENT '院内唯一科室编码',
                                  `dept_type` TINYINT NOT NULL COMMENT '类型：1-科室 2-病区 3-居家站点',
                                  `sort` INT NOT NULL DEFAULT 0 COMMENT '排序序号',
                                  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-正常',
                                  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
                                  PRIMARY KEY (`dept_id`),
                                  UNIQUE INDEX `idx_dept_code` (`dept_code`),
                                  INDEX `idx_parent_id` (`parent_id`),
                                  INDEX `idx_dept_type` (`dept_type`),
                                  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科室病区字典表：存储医院科室、病区、居家站点的层级结构数据；parent_name冗余设计，避免层级查询时的自连接；无物理外键约束。';

-- 业务逻辑说明：
-- 1. 层级关联：通过parent_id实现科室-病区的二级层级结构，无物理外键，层级一致性由业务层校验。
-- 2. 冗余设计：parent_name为冗余快照，科室/病区创建/更新时同步写入，列表展示直接读取，无需自连接查询。
-- 3. 场景适配：dept_type支持「居家站点」，适配患者管理页面的「居家」病区场景。


-- ----------------------------
-- 4. 系统权限域 - 系统用户表
-- ----------------------------
CREATE TABLE `sys_user` (
                            `user_id` BIGINT NOT NULL COMMENT '用户唯一ID（雪花ID）',
                            `user_name` VARCHAR(64) NOT NULL COMMENT '登录账号',
                            `real_name` VARCHAR(32) NOT NULL COMMENT '人员真实姓名',
                            `password` VARCHAR(128) NOT NULL COMMENT '加密存储密码（BCrypt加密）',
                            `role_id` BIGINT DEFAULT NULL COMMENT '所属角色ID（业务关联，无外键约束）',
                            `role_name` VARCHAR(32) DEFAULT NULL COMMENT '所属角色名称（冗余快照，避免联查角色表）',
                            `dept_id` BIGINT DEFAULT NULL COMMENT '所属科室ID（业务关联，无外键约束）',
                            `dept_name` VARCHAR(64) DEFAULT NULL COMMENT '所属科室名称（冗余快照，避免联查科室表）',
                            `phone` VARCHAR(16) DEFAULT NULL COMMENT '联系电话',
                            `email` VARCHAR(64) DEFAULT NULL COMMENT '邮箱',
                            `status` TINYINT NOT NULL DEFAULT 1 COMMENT '账号状态：0-禁用 1-启用',
                            `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
                            `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            `create_user_id` BIGINT DEFAULT NULL COMMENT '创建人ID',
                            `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
                            PRIMARY KEY (`user_id`),
                            UNIQUE INDEX `idx_user_name` (`user_name`),
                            INDEX `idx_real_name` (`real_name`),
                            INDEX `idx_role_id` (`role_id`),
                            INDEX `idx_dept_id` (`dept_id`),
                            INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表：存储系统所有操作人员（医生、护士、管理员等）的账号信息与基础档案；冗余角色名称、科室名称，单表即可完成用户列表渲染，无需跨表联查；无物理外键约束。';

-- 业务逻辑说明：
-- 1. 关联逻辑：通过role_id、dept_id与角色表、科室表进行业务关联，无数据库外键，关联一致性由业务层校验。
-- 2. 冗余设计：role_name、dept_name为冗余快照字段，用户创建/更新时同步写入，后续角色/科室名称变更不影响历史数据，同时避免用户列表查询时的跨表联查。
-- 3. 安全合规：采用逻辑删除，保留所有历史用户数据；密码BCrypt加密存储，不保存明文；last_login_time记录登录行为，满足审计要求。


-- ----------------------------
-- 5. 系统权限域 - 系统操作日志表
-- ----------------------------
CREATE TABLE `sys_operation_log` (
                                     `log_id` BIGINT NOT NULL COMMENT '日志唯一ID（雪花ID）',
                                     `user_id` BIGINT DEFAULT NULL COMMENT '操作人ID（业务关联，无外键约束）',
                                     `real_name` VARCHAR(32) DEFAULT NULL COMMENT '操作人姓名（冗余快照）',
                                     `module` VARCHAR(64) NOT NULL COMMENT '操作模块（如心电数据管理、AI诊断中心、报告审核）',
                                     `operation_type` VARCHAR(32) NOT NULL COMMENT '操作类型（新增/修改/删除/审核/查看/导出）',
                                     `operation_content` TEXT DEFAULT NULL COMMENT '操作详情（如审核报告ID：20260411001，审核结果：通过）',
                                     `request_ip` VARCHAR(64) DEFAULT NULL COMMENT '操作IP地址',
                                     `operation_time` DATETIME NOT NULL COMMENT '操作时间',
                                     `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
                                     PRIMARY KEY (`log_id`),
                                     INDEX `idx_user_id` (`user_id`),
                                     INDEX `idx_module` (`module`),
                                     INDEX `idx_operation_time` (`operation_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统操作日志表：记录全系统所有操作行为，满足医疗系统合规审计与追溯要求；无物理外键约束，即使操作人账号删除，日志仍完整保留。';

-- 业务逻辑说明：
-- 1. 审计合规：全量记录操作人、模块、类型、详情、IP、时间，满足《医疗数据安全管理规范》的审计追溯要求。
-- 2. 数据独立：real_name冗余快照，即使sys_user表中的用户信息变更或删除，日志中的操作人姓名仍完整保留，保证审计数据的独立性。
-- 3. 性能优化：module、operation_time建立索引，支持按模块、时间范围快速查询审计日志。


-- ----------------------------
-- 6. 患者管理域 - 患者信息主表
-- ----------------------------
CREATE TABLE `patient_info` (
                                `patient_id` BIGINT NOT NULL COMMENT '患者唯一ID（雪花ID）',
                                `patient_name` VARCHAR(32) NOT NULL COMMENT '患者姓名',
                                `gender` TINYINT NOT NULL COMMENT '性别：1-男 2-女 3-未知',
                                `birth_date` DATE DEFAULT NULL COMMENT '出生日期',
                                `age` INT DEFAULT NULL COMMENT '年龄（冗余字段，业务层根据birth_date自动计算更新）',
                                `id_card` VARCHAR(18) DEFAULT NULL COMMENT '身份证号（脱敏存储，仅保留前6后4位）',
                                `inpatient_no` VARCHAR(32) DEFAULT NULL COMMENT '住院号',
                                `bed_no` VARCHAR(16) DEFAULT NULL COMMENT '床号',
                                `current_dept_id` BIGINT DEFAULT NULL COMMENT '当前所属病区ID（业务关联，无外键约束）',
                                `current_dept_name` VARCHAR(64) DEFAULT NULL COMMENT '当前所属病区名称（冗余快照）',
                                `phone` VARCHAR(16) DEFAULT NULL COMMENT '联系电话（脱敏存储）',
                                `admission_time` DATETIME DEFAULT NULL COMMENT '入院时间',
                                `discharge_time` DATETIME DEFAULT NULL COMMENT '出院时间',
                                `primary_diagnosis` VARCHAR(256) DEFAULT NULL COMMENT '基础诊断（对应患者管理列表「诊断」字段）',
                                `risk_level` TINYINT DEFAULT NULL COMMENT '风险等级：1-低危 2-中危 3-中高危 4-高危',
                                `patient_type` TINYINT NOT NULL DEFAULT 1 COMMENT '患者类型：1-住院患者 2-门诊患者 3-居家患者',
                                `patient_status` TINYINT NOT NULL DEFAULT 1 COMMENT '患者状态：1-住院中 2-出院 3-居家随访',
                                `follow_up_status` TINYINT NOT NULL DEFAULT 0 COMMENT '随访状态：0-未随访 1-已随访',
                                `ecg_count` INT NOT NULL DEFAULT 0 COMMENT '心电采集次数（冗余字段，业务层统计更新）',
                                `latest_ecg_time` DATETIME DEFAULT NULL COMMENT '最近心电采集时间（冗余快照）',
                                `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
                                PRIMARY KEY (`patient_id`),
                                UNIQUE INDEX `idx_id_card` (`id_card`),
                                INDEX `idx_inpatient_no` (`inpatient_no`),
                                INDEX `idx_patient_name` (`patient_name`),
                                INDEX `idx_current_dept_id` (`current_dept_id`),
                                INDEX `idx_risk_level` (`risk_level`),
                                INDEX `idx_patient_status` (`patient_status`),
                                INDEX `idx_composite_filter` (`current_dept_name`, `risk_level`, `patient_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者信息主表：存储患者全生命周期档案信息，是系统核心基础表；冗余心电采集次数、最近时间等字段，单表即可完成患者管理列表的全量渲染与筛选，无需跨表联查；无物理外键约束。';

-- 业务逻辑说明：
-- 1. 关联逻辑：通过current_dept_id与科室表业务关联，无物理外键，关联一致性由业务层校验。
-- 2. 冗余设计：
--    - age：根据birth_date自动计算更新，避免列表查询时实时计算年龄。
--    - current_dept_name、ecg_count、latest_ecg_time：冗余快照，患者管理列表直接展示，无需联查心电采集记录表。
-- 3. 合规要求：id_card、phone脱敏存储，保护患者隐私；全表逻辑删除，保留所有历史患者数据。
-- 4. 性能优化：建立idx_composite_filter复合索引，优化病区、风险等级、状态的组合筛选查询。


-- ----------------------------
-- 7. 患者管理域 - 患者随访记录表
-- ----------------------------
CREATE TABLE `patient_follow_up_record` (
                                            `follow_up_id` BIGINT NOT NULL COMMENT '随访记录唯一ID（雪花ID）',
                                            `patient_id` BIGINT NOT NULL COMMENT '患者ID（业务关联，无外键约束）',
                                            `patient_name` VARCHAR(32) NOT NULL COMMENT '患者姓名（冗余快照）',
                                            `follow_up_user_id` BIGINT DEFAULT NULL COMMENT '随访人ID（业务关联，无外键约束）',
                                            `follow_up_user_name` VARCHAR(32) DEFAULT NULL COMMENT '随访人姓名（冗余快照）',
                                            `follow_up_time` DATETIME NOT NULL COMMENT '随访时间',
                                            `follow_up_content` TEXT DEFAULT NULL COMMENT '随访内容',
                                            `follow_up_result` VARCHAR(256) DEFAULT NULL COMMENT '随访结果',
                                            `next_follow_up_time` DATE DEFAULT NULL COMMENT '下次随访时间',
                                            `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                            `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
                                            PRIMARY KEY (`follow_up_id`),
                                            INDEX `idx_patient_id` (`patient_id`),
                                            INDEX `idx_follow_up_time` (`follow_up_time`),
                                            INDEX `idx_next_follow_up_time` (`next_follow_up_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者随访记录表：存储居家/出院患者的随访信息，支撑患者全生命周期管理；冗余患者姓名、随访人姓名，单表即可完成随访记录查询；无物理外键约束。';

-- 业务逻辑说明：
-- 1. 关联逻辑：通过patient_id、follow_up_user_id与患者表、用户表业务关联，无物理外键。
-- 2. 冗余设计：patient_name、follow_up_user_name冗余快照，随访记录列表直接展示，无需联查。


-- ----------------------------
-- 8. 设备管理域 - 心电设备管理表
-- ----------------------------
CREATE TABLE `ecg_device` (
                              `device_id` BIGINT NOT NULL COMMENT '设备唯一ID（雪花ID）',
                              `device_code` VARCHAR(64) NOT NULL COMMENT '设备唯一序列号/院内编码',
                              `device_name` VARCHAR(64) NOT NULL COMMENT '设备名称（如心电图机ECG-2000、远程心电监测仪RM-200）',
                              `device_type` TINYINT NOT NULL COMMENT '设备类型：1-静态心电设备 2-动态心电设备 3-居家监护设备 4-床边监护仪',
                              `device_model` VARCHAR(64) DEFAULT NULL COMMENT '设备型号',
                              `manufacturer` VARCHAR(64) DEFAULT NULL COMMENT '生产厂商',
                              `supplier` VARCHAR(64) DEFAULT NULL COMMENT '供应商',
                              `install_date` DATE DEFAULT NULL COMMENT '安装日期',
                              `bind_dept_id` BIGINT DEFAULT NULL COMMENT '绑定病区ID（业务关联，无外键约束）',
                              `bind_dept_name` VARCHAR(64) DEFAULT NULL COMMENT '绑定病区名称（冗余快照）',
                              `last_maintain_time` DATE DEFAULT NULL COMMENT '上次维护时间',
                              `next_maintain_time` DATE DEFAULT NULL COMMENT '下次维护时间',
                              `device_status` TINYINT NOT NULL DEFAULT 1 COMMENT '设备状态：1-正常 2-维修 3-停用 4-离线',
                              `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
                              PRIMARY KEY (`device_id`),
                              UNIQUE INDEX `idx_device_code` (`device_code`),
                              INDEX `idx_device_name` (`device_name`),
                              INDEX `idx_device_type` (`device_type`),
                              INDEX `idx_bind_dept_id` (`bind_dept_id`),
                              INDEX `idx_device_status` (`device_status`),
                              INDEX `idx_next_maintain_time` (`next_maintain_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='心电设备管理表：存储心电设备全生命周期档案信息，是设备管理模块的核心表；冗余绑定病区名称，单表即可完成设备管理列表渲染；无物理外键约束。';

-- 业务逻辑说明：
-- 1. 关联逻辑：通过bind_dept_id与科室表业务关联，无物理外键。
-- 2. 冗余设计：bind_dept_name冗余快照，设备列表直接展示，无需联查科室表。
-- 3. 场景适配：device_type支持「居家监护设备、床边监护仪」，覆盖所有页面出现的设备品类。
-- 4. 维护提醒：next_maintain_time建立索引，支持即将到期维护设备的快速查询与提醒。


-- ----------------------------
-- 9. 设备管理域 - 设备维护记录表
-- ----------------------------
CREATE TABLE `ecg_device_maintain_record` (
                                              `maintain_id` BIGINT NOT NULL COMMENT '维护记录唯一ID（雪花ID）',
                                              `device_id` BIGINT NOT NULL COMMENT '设备ID（业务关联，无外键约束）',
                                              `device_name` VARCHAR(64) NOT NULL COMMENT '设备名称（冗余快照）',
                                              `maintain_type` VARCHAR(32) NOT NULL COMMENT '维护类型（常规保养/故障维修/计量校准）',
                                              `maintain_user_id` BIGINT DEFAULT NULL COMMENT '维护人ID（业务关联，无外键约束）',
                                              `maintain_user_name` VARCHAR(32) DEFAULT NULL COMMENT '维护人姓名（冗余快照）',
                                              `maintain_time` DATE NOT NULL COMMENT '维护时间',
                                              `maintain_content` TEXT DEFAULT NULL COMMENT '维护内容',
                                              `maintain_result` VARCHAR(256) DEFAULT NULL COMMENT '维护结果',
                                              `next_maintain_time` DATE DEFAULT NULL COMMENT '下次维护时间',
                                              `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                              `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
                                              PRIMARY KEY (`maintain_id`),
                                              INDEX `idx_device_id` (`device_id`),
                                              INDEX `idx_maintain_time` (`maintain_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备维护记录表：存储设备全生命周期的维护、维修、校准记录；冗余设备名称、维护人姓名，单表即可完成维护记录查询；无物理外键约束。';

-- 业务逻辑说明：
-- 1. 关联逻辑：通过device_id与设备表业务关联，无物理外键。
-- 2. 数据同步：维护记录创建成功后，业务层自动更新ecg_device表的last_maintain_time、next_maintain_time字段。


-- ----------------------------
-- 10. 设备管理域 - 设备质控记录表
-- ----------------------------
CREATE TABLE `ecg_device_quality_control` (
                                              `qc_id` BIGINT NOT NULL COMMENT '质控记录唯一ID（雪花ID）',
                                              `device_id` BIGINT NOT NULL COMMENT '设备ID（业务关联，无外键约束）',
                                              `device_name` VARCHAR(64) NOT NULL COMMENT '设备名称（冗余快照）',
                                              `dept_id` BIGINT DEFAULT NULL COMMENT '所属病区ID（业务关联，无外键约束）',
                                              `dept_name` VARCHAR(64) DEFAULT NULL COMMENT '所属病区名称（冗余快照）',
                                              `test_time` DATETIME NOT NULL COMMENT '测试时间',
                                              `test_type` VARCHAR(32) NOT NULL COMMENT '测试类型：日检/周检/月检/远程检测',
                                              `test_user_id` BIGINT DEFAULT NULL COMMENT '测试人员ID（业务关联，无外键约束）',
                                              `test_user_name` VARCHAR(32) DEFAULT NULL COMMENT '测试人员姓名（冗余快照）',
                                              `device_status` VARCHAR(16) NOT NULL COMMENT '设备状态：正常/异常',
                                              `test_result` VARCHAR(16) NOT NULL COMMENT '测试结果：通过/未通过',
                                              `remark` VARCHAR(256) DEFAULT NULL COMMENT '备注',
                                              `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                              `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                              `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
                                              PRIMARY KEY (`qc_id`),
                                              INDEX `idx_device_id` (`device_id`),
                                              INDEX `idx_device_name` (`device_name`),
                                              INDEX `idx_test_time` (`test_time`),
                                              INDEX `idx_test_result` (`test_result`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备质控记录表：100%适配「质控管理」列表页，存储设备日常质控检测的全量信息；冗余所有展示字段，单表即可完成列表渲染与筛选；无物理外键约束。';

-- 业务逻辑说明：
-- 1. 关联逻辑：通过device_id、dept_id、test_user_id与设备表、科室表、用户表业务关联，无物理外键。
-- 2. 冗余设计：device_name、dept_name、test_user_name冗余快照，质控管理列表直接展示，无需跨表联查。
-- 3. 性能优化：test_time、test_result建立索引，优化时间范围、测试结果的筛选查询。


-- ----------------------------
-- 11. 心电核心业务域 - 心电采集记录表
-- ----------------------------
CREATE TABLE `ecg_collection_record` (
                                         `record_id` BIGINT NOT NULL COMMENT '采集记录唯一ID（雪花ID）',
                                         `ecg_no` VARCHAR(32) NOT NULL COMMENT '心电图编号（院内唯一，如ECG001）',
                                         `patient_id` BIGINT NOT NULL COMMENT '患者ID（业务关联，无外键约束）',
                                         `patient_name` VARCHAR(32) NOT NULL COMMENT '患者姓名（冗余快照）',
                                         `gender` VARCHAR(4) DEFAULT NULL COMMENT '患者性别（冗余快照，如男/女）',
                                         `age` INT DEFAULT NULL COMMENT '患者年龄（冗余快照）',
                                         `inpatient_no` VARCHAR(32) DEFAULT NULL COMMENT '住院号（冗余快照）',
                                         `dept_id` BIGINT DEFAULT NULL COMMENT '采集病区ID（业务关联，无外键约束）',
                                         `dept_name` VARCHAR(64) DEFAULT NULL COMMENT '采集病区名称（冗余快照）',
                                         `bed_no` VARCHAR(16) DEFAULT NULL COMMENT '床号（冗余快照）',
                                         `device_id` BIGINT NOT NULL COMMENT '采集设备ID（业务关联，无外键约束）',
                                         `device_name` VARCHAR(64) DEFAULT NULL COMMENT '设备名称（冗余快照）',
                                         `lead_count` INT DEFAULT NULL COMMENT '导联数（如12导联、3导联）',
                                         `collection_start_time` DATETIME NOT NULL COMMENT '采集时间',
                                         `collection_end_time` DATETIME DEFAULT NULL COMMENT '采集结束时间',
                                         `sampling_rate` INT DEFAULT NULL COMMENT '采样率（Hz）',
                                         `ecg_data_file_url` VARCHAR(256) DEFAULT NULL COMMENT '心电原始数据文件存储地址',
                                         `collection_duration` INT DEFAULT NULL COMMENT '采集时长（秒）',
                                         `collection_type` TINYINT NOT NULL DEFAULT 1 COMMENT '采集类型：1-院内常规采集 2-居家远程采集 3-床旁监护 4-手动上传',
                                         `upload_user_id` BIGINT DEFAULT NULL COMMENT '上传人ID（业务关联，无外键约束）',
                                         `upload_user_name` VARCHAR(32) DEFAULT NULL COMMENT '上传人姓名（冗余快照）',
                                         `upload_time` DATETIME DEFAULT NULL COMMENT '上传时间',
                                         `upload_source_file_url` VARCHAR(256) DEFAULT NULL COMMENT '上传源文件存储地址',
                                         `record_status` TINYINT NOT NULL DEFAULT 1 COMMENT '采集状态：1-采集中 2-完成 3-失败',
                                         `ai_analysis_status` TINYINT NOT NULL DEFAULT 0 COMMENT 'AI分析状态：0-待分析 1-分析中 2-已分析 3-失败',
                                         `report_status` TINYINT NOT NULL DEFAULT 0 COMMENT '报告状态：0-未生成 1-待审核 2-已审核 3-驳回',
                                         `display_status` TINYINT NOT NULL DEFAULT 0 COMMENT '列表展示状态（业务层统一维护）：0-待分析 1-已分析 2-已审核 3-已驳回 4-采集失败',
                                         `ai_conclusion_short` VARCHAR(256) DEFAULT NULL COMMENT 'AI诊断简短结论（冗余快照，列表直接展示）',
                                         `create_user_id` BIGINT DEFAULT NULL COMMENT '采集操作人ID',
                                         `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                         `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                         `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
                                         PRIMARY KEY (`record_id`),
                                         UNIQUE INDEX `idx_ecg_no` (`ecg_no`),
                                         INDEX `idx_patient_id` (`patient_id`),
                                         INDEX `idx_patient_name` (`patient_name`),
                                         INDEX `idx_inpatient_no` (`inpatient_no`),
                                         INDEX `idx_device_name` (`device_name`),
                                         INDEX `idx_collection_start_time` (`collection_start_time`),
                                         INDEX `idx_display_status` (`display_status`),
                                         INDEX `idx_composite_search` (`patient_name`, `inpatient_no`, `device_name`),
                                         INDEX `idx_composite_filter` (`dept_name`, `display_status`, `collection_start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='心电采集记录表：系统核心业务主表，100%适配「心电数据管理」列表页；冗余所有展示字段（患者信息、病区、设备、AI结论等），单表即可完成列表渲染、搜索、筛选，无需跨表联查；无物理外键约束。';

-- 业务逻辑说明：
-- 1. 关联逻辑：通过patient_id、dept_id、device_id、upload_user_id与患者表、科室表、设备表、用户表业务关联，无物理外键。
-- 2. 冗余设计：
--    - 患者/病区/设备信息：采集记录生成时一次性快照写入，后续基础信息变更不影响历史记录，符合医疗数据“记录当时状态”的合规要求。
--    - ai_conclusion_short：AI分析完成后同步写入，心电数据列表直接展示，无需联查AI诊断表。
-- 3. 状态联动：
--    - 采集完成 → 自动触发AI分析任务，ai_analysis_status更新为「待分析」，display_status更新为0。
--    - AI分析完成 → ai_analysis_status更新为「已分析」，display_status更新为1，同步写入ai_conclusion_short。
--    - 报告审核完成 → report_status更新为「已审核」，display_status更新为2。
-- 4. 性能优化：
--    - idx_composite_search：优化患者姓名/住院号/设备的全局模糊搜索。
--    - idx_composite_filter：优化病区/状态/时间范围的组合筛选。


-- ----------------------------
-- 12. 心电核心业务域 - AI诊断分析结果表
-- ----------------------------
CREATE TABLE `ecg_ai_diagnosis` (
                                    `ai_diagnosis_id` BIGINT NOT NULL COMMENT 'AI诊断记录唯一ID（雪花ID）',
                                    `diagnosis_no` VARCHAR(32) NOT NULL COMMENT '诊断编号（院内唯一，如DIAG001）',
                                    `record_id` BIGINT NOT NULL COMMENT '关联采集记录ID（业务关联，无外键约束）',
                                    `ecg_no` VARCHAR(32) NOT NULL COMMENT '心电图编号（冗余快照）',
                                    `patient_id` BIGINT NOT NULL COMMENT '患者ID（业务关联，无外键约束）',
                                    `patient_name` VARCHAR(32) NOT NULL COMMENT '患者姓名（冗余快照）',
                                    `gender` VARCHAR(4) DEFAULT NULL COMMENT '患者性别（冗余快照）',
                                    `age` INT DEFAULT NULL COMMENT '患者年龄（冗余快照）',
                                    `inpatient_no` VARCHAR(32) DEFAULT NULL COMMENT '住院号（冗余快照）',
                                    `dept_id` BIGINT DEFAULT NULL COMMENT '科室ID（业务关联，无外键约束）',
                                    `dept_name` VARCHAR(64) DEFAULT NULL COMMENT '科室名称（冗余快照）',
                                    `ai_model_version` VARCHAR(32) NOT NULL COMMENT 'AI模型版本号',
                                    `analysis_start_time` DATETIME DEFAULT NULL COMMENT '分析开始时间',
                                    `analysis_end_time` DATETIME DEFAULT NULL COMMENT '分析结束时间',
                                    `ai_conclusion` TEXT DEFAULT NULL COMMENT 'AI诊断完整结论',
                                    `heart_rate` INT DEFAULT NULL COMMENT '心率（次/分）',
                                    `pr_interval` INT DEFAULT NULL COMMENT 'PR间期（ms）',
                                    `qrs_duration` INT DEFAULT NULL COMMENT 'QRS时限（ms）',
                                    `qt_interval` INT DEFAULT NULL COMMENT 'QT间期（ms）',
                                    `qtc_interval` INT DEFAULT NULL COMMENT '校正QT间期（ms）',
                                    `abnormal_type` VARCHAR(256) DEFAULT NULL COMMENT '异常类型（如心动过缓、ST段异常）',
                                    `abnormal_count` INT NOT NULL DEFAULT 0 COMMENT '异常数量（对应AI诊断中心列表「异常数量」字段）',
                                    `confidence` DECIMAL(5,2) DEFAULT NULL COMMENT '置信度（%，如85.00）',
                                    `is_abnormal` TINYINT NOT NULL DEFAULT 0 COMMENT '是否异常：0-正常 1-异常',
                                    `abnormal_level` TINYINT NOT NULL DEFAULT 0 COMMENT '异常级别：0-正常 1-低危 2-中危 3-高危',
                                    `analysis_status` TINYINT NOT NULL DEFAULT 0 COMMENT '分析状态：0-待分析 1-分析中 2-完成 3-失败',
                                    `fail_reason` VARCHAR(256) DEFAULT NULL COMMENT '分析失败原因',
                                    `diagnosis_time` DATETIME DEFAULT NULL COMMENT '诊断完成时间',
                                    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
                                    PRIMARY KEY (`ai_diagnosis_id`),
                                    UNIQUE INDEX `idx_diagnosis_no` (`diagnosis_no`),
                                    INDEX `idx_record_id` (`record_id`),
                                    INDEX `idx_ecg_no` (`ecg_no`),
                                    INDEX `idx_patient_name` (`patient_name`),
                                    INDEX `idx_analysis_status` (`analysis_status`),
                                    INDEX `idx_diagnosis_time` (`diagnosis_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI诊断分析结果表：100%适配「AI诊断中心」列表页，存储AI诊断的全量结果与测量参数；冗余所有展示字段，单表即可完成列表渲染、搜索、筛选；无物理外键约束。';

-- 业务逻辑说明：
-- 1. 关联逻辑：通过record_id、patient_id、dept_id与采集记录表、患者表、科室表业务关联，无物理外键。
-- 2. 冗余设计：患者/科室/ecg_no等字段冗余快照，AI诊断中心列表直接展示，无需联查采集记录表。
-- 3. 预警联动：AI分析完成后，若is_abnormal=1且abnormal_level≥2，业务层自动生成ecg_abnormal_warning预警记录。
-- 4. 统计支撑：confidence字段支撑数据分析页面的「AI诊断准确率」统计。


-- ----------------------------
-- 13. 心电核心业务域 - 诊断报告管理表
-- ----------------------------
CREATE TABLE `ecg_diagnosis_report` (
                                        `report_id` BIGINT NOT NULL COMMENT '报告唯一ID（雪花ID）',
                                        `report_no` VARCHAR(32) NOT NULL COMMENT '报告编号（院内唯一，如20260411001）',
                                        `record_id` BIGINT NOT NULL COMMENT '关联采集记录ID（业务关联，无外键约束）',
                                        `ai_diagnosis_id` BIGINT DEFAULT NULL COMMENT '关联AI诊断记录ID（业务关联，无外键约束）',
                                        `patient_id` BIGINT NOT NULL COMMENT '患者ID（业务关联，无外键约束）',
                                        `patient_name` VARCHAR(32) NOT NULL COMMENT '患者姓名（冗余快照）',
                                        `gender` VARCHAR(4) DEFAULT NULL COMMENT '患者性别（冗余快照）',
                                        `age` INT DEFAULT NULL COMMENT '患者年龄（冗余快照）',
                                        `inpatient_no` VARCHAR(32) DEFAULT NULL COMMENT '住院号（冗余快照）',
                                        `collection_time` DATETIME DEFAULT NULL COMMENT '采集时间（冗余快照）',
                                        `ai_conclusion` TEXT DEFAULT NULL COMMENT 'AI诊断结论（冗余快照）',
                                        `doctor_diagnosis` TEXT DEFAULT NULL COMMENT '医生诊断结论（对应诊断报告管理列表「医生结论」字段）',
                                        `doctor_suggestion` TEXT DEFAULT NULL COMMENT '医生建议',
                                        `report_create_doctor_id` BIGINT DEFAULT NULL COMMENT '报告生成医生ID（业务关联，无外键约束）',
                                        `report_create_doctor_name` VARCHAR(32) DEFAULT NULL COMMENT '报告生成医生姓名（冗余快照）',
                                        `report_create_time` DATETIME DEFAULT NULL COMMENT '报告生成时间',
                                        `audit_doctor_id` BIGINT DEFAULT NULL COMMENT '审核医生ID（业务关联，无外键约束）',
                                        `audit_doctor_name` VARCHAR(32) DEFAULT NULL COMMENT '审核医生姓名（对应列表「审核医生」字段，冗余快照）',
                                        `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间（对应列表「审核时间」字段）',
                                        `audit_opinion` VARCHAR(256) DEFAULT NULL COMMENT '审核意见',
                                        `report_status` TINYINT NOT NULL DEFAULT 0 COMMENT '报告状态：0-草稿 1-待审核 2-审核通过 3-已驳回 4-已作废',
                                        `report_file_url` VARCHAR(256) DEFAULT NULL COMMENT '报告PDF文件存储地址',
                                        `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                        `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
                                        PRIMARY KEY (`report_id`),
                                        UNIQUE INDEX `idx_report_no` (`report_no`),
                                        INDEX `idx_record_id` (`record_id`),
                                        INDEX `idx_patient_id` (`patient_id`),
                                        INDEX `idx_patient_name` (`patient_name`),
                                        INDEX `idx_report_status` (`report_status`),
                                        INDEX `idx_audit_time` (`audit_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='诊断报告管理表：100%适配「诊断报告管理」列表页，存储诊断报告的全生命周期信息；冗余所有展示字段，单表即可完成列表渲染；无物理外键约束。';

-- 业务逻辑说明：
-- 1. 关联逻辑：通过record_id、ai_diagnosis_id、patient_id、report_create_doctor_id、audit_doctor_id与采集记录表、AI诊断表、患者表、用户表业务关联，无物理外键。
-- 2. 冗余设计：患者信息、采集时间、AI结论、医生姓名等字段冗余快照，报告管理列表直接展示，无需跨表联查。
-- 3. 状态联动：
--    - 报告生成 → report_status更新为「待审核」，同步更新ecg_collection_record的report_status为1。
--    - 审核通过 → report_status更新为「审核通过」，同步更新ecg_collection_record的report_status为2、display_status为2。
--    - 审核驳回 → report_status更新为「已驳回」，同步更新ecg_collection_record的report_status为3、display_status为3。


-- ----------------------------
-- 14. 预警监护域 - 异常预警信息表
-- ----------------------------
CREATE TABLE `ecg_abnormal_warning` (
                                        `warning_id` BIGINT NOT NULL COMMENT '预警唯一ID（雪花ID）',
                                        `record_id` BIGINT NOT NULL COMMENT '关联采集记录ID（业务关联，无外键约束）',
                                        `ai_diagnosis_id` BIGINT DEFAULT NULL COMMENT '关联AI诊断记录ID（业务关联，无外键约束）',
                                        `patient_id` BIGINT NOT NULL COMMENT '患者ID（业务关联，无外键约束）',
                                        `patient_name` VARCHAR(32) NOT NULL COMMENT '患者姓名（冗余快照）',
                                        `gender` VARCHAR(4) DEFAULT NULL COMMENT '患者性别（冗余快照）',
                                        `age` INT DEFAULT NULL COMMENT '患者年龄（冗余快照）',
                                        `inpatient_no` VARCHAR(32) DEFAULT NULL COMMENT '住院号（冗余快照）',
                                        `dept_id` BIGINT DEFAULT NULL COMMENT '病区ID（业务关联，无外键约束）',
                                        `dept_name` VARCHAR(64) NOT NULL COMMENT '病区名称（冗余快照）',
                                        `bed_no` VARCHAR(16) DEFAULT NULL COMMENT '床号（冗余快照）',
                                        `warning_time` DATETIME NOT NULL COMMENT '预警触发时间',
                                        `warning_type` VARCHAR(64) NOT NULL COMMENT '预警类型（如心房颤动、ST段异常、室性早搏增多）',
                                        `warning_level` TINYINT NOT NULL COMMENT '预警级别：1-低危 2-中危 3-高危',
                                        `warning_desc` TEXT DEFAULT NULL COMMENT '预警详情描述',
                                        `handle_status` TINYINT NOT NULL DEFAULT 0 COMMENT '处理状态：0-待确认 1-待处理 2-处理中 3-已处理 4-已忽略',
                                        `handle_user_id` BIGINT DEFAULT NULL COMMENT '处理人ID（业务关联，无外键约束）',
                                        `handle_user_name` VARCHAR(32) DEFAULT NULL COMMENT '处理人姓名（冗余快照）',
                                        `handle_time` DATETIME DEFAULT NULL COMMENT '处理时间',
                                        `handle_opinion` VARCHAR(256) DEFAULT NULL COMMENT '处理意见',
                                        `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                        `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
                                        PRIMARY KEY (`warning_id`),
                                        INDEX `idx_record_id` (`record_id`),
                                        INDEX `idx_patient_id` (`patient_id`),
                                        INDEX `idx_patient_name` (`patient_name`),
                                        INDEX `idx_inpatient_no` (`inpatient_no`),
                                        INDEX `idx_warning_time` (`warning_time`),
                                        INDEX `idx_warning_level` (`warning_level`),
                                        INDEX `idx_handle_status` (`handle_status`),
                                        INDEX `idx_composite_filter` (`warning_level`, `handle_status`, `warning_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='异常预警信息表：100%适配「预警监控」列表页，存储心电异常预警的全量信息与处理闭环；冗余所有展示字段，单表即可完成列表渲染、搜索、筛选；无物理外键约束。';

-- 业务逻辑说明：
-- 1. 关联逻辑：通过record_id、ai_diagnosis_id、patient_id、dept_id、handle_user_id与采集记录表、AI诊断表、患者表、科室表、用户表业务关联，无物理外键。
-- 2. 冗余设计：患者/病区/床号等字段冗余快照，预警监控列表直接展示，无需跨表联查。
-- 3. 生成规则：AI分析完成后，若is_abnormal=1且abnormal_level≥2，自动生成预警记录；实时监护数据异常时，也可手动/自动生成预警。
-- 4. 处理闭环：handle_status覆盖「待确认→待处理→处理中→已处理」全流程，处理完成后同步更新handle_time、handle_user_name。
-- 5. 性能优化：idx_composite_filter复合索引，优化预警级别、处理状态、时间范围的组合筛选。


-- ----------------------------
-- 15. 预警监护域 - 实时监护数据表
-- ----------------------------
CREATE TABLE `ecg_real_time_monitor` (
                                         `monitor_id` BIGINT NOT NULL COMMENT '监护记录唯一ID（雪花ID）',
                                         `patient_id` BIGINT NOT NULL COMMENT '患者ID（业务关联，无外键约束，单患者单条实时监护记录）',
                                         `patient_name` VARCHAR(32) NOT NULL COMMENT '患者姓名（冗余快照）',
                                         `dept_id` BIGINT DEFAULT NULL COMMENT '病区ID（业务关联，无外键约束）',
                                         `dept_name` VARCHAR(64) DEFAULT NULL COMMENT '病区名称（冗余快照）',
                                         `bed_no` VARCHAR(16) DEFAULT NULL COMMENT '床号（冗余快照）',
                                         `device_id` BIGINT DEFAULT NULL COMMENT '监护设备ID（业务关联，无外键约束）',
                                         `device_name` VARCHAR(64) DEFAULT NULL COMMENT '设备名称（冗余快照）',
                                         `current_heart_rate` INT DEFAULT NULL COMMENT '当前心率（bpm）',
                                         `monitor_status` TINYINT NOT NULL DEFAULT 1 COMMENT '监护状态：1-正常 2-预警 3-离线',
                                         `is_abnormal` TINYINT NOT NULL DEFAULT 0 COMMENT '是否异常：0-正常 1-异常',
                                         `warning_level` TINYINT NOT NULL DEFAULT 0 COMMENT '预警级别：0-正常 1-低危 2-中危 3-高危',
                                         `latest_update_time` DATETIME NOT NULL COMMENT '最新数据更新时间',
                                         `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                         `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                         `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
                                         PRIMARY KEY (`monitor_id`),
                                         UNIQUE INDEX `idx_patient_id` (`patient_id`),
                                         INDEX `idx_dept_id` (`dept_id`),
                                         INDEX `idx_monitor_status` (`monitor_status`),
                                         INDEX `idx_latest_update_time` (`latest_update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实时监护数据表：适配「实时监护」大屏页面，存储床旁/居家患者的实时监护高频更新数据；patient_id唯一索引，保证单患者仅一条实时监护记录；无物理外键约束。';

-- 业务逻辑说明：
-- 1. 关联逻辑：通过patient_id、dept_id、device_id与患者表、科室表、设备表业务关联，无物理外键。
-- 2. 数据更新：实时监护数据高频推送时，业务层根据patient_id执行UPSERT操作（存在则更新，不存在则插入），保证单患者仅一条最新记录。
-- 3. 大屏支撑：current_heart_rate、monitor_status、warning_level等字段直接支撑实时监护大屏的展示与预警。


-- ----------------------------
-- 16. 统计分析域 - 统计结果预计算表
-- ----------------------------
CREATE TABLE `ecg_statistics_result` (
                                         `stat_id` BIGINT NOT NULL COMMENT '统计记录唯一ID（雪花ID）',
                                         `stat_date` DATE NOT NULL COMMENT '统计日期',
                                         `stat_type` TINYINT NOT NULL COMMENT '统计类型：1-心电测量统计 2-预警统计 3-报告统计 4-AI准确率统计 5-科室分布统计 6-设备使用统计',
                                         `stat_dimension` TINYINT NOT NULL COMMENT '统计维度：1-日 2-周 3-月 4-年',
                                         `dept_id` BIGINT DEFAULT NULL COMMENT '科室ID（按科室统计时填充，业务关联，无外键约束）',
                                         `dept_name` VARCHAR(64) DEFAULT NULL COMMENT '科室名称（冗余快照）',
                                         `stat_value1` BIGINT NOT NULL DEFAULT 0 COMMENT '统计值1（如总测量次数、正常数、待处理数）',
                                         `stat_value2` BIGINT NOT NULL DEFAULT 0 COMMENT '统计值2（如异常数、高危数、已审核数）',
                                         `stat_value3` BIGINT NOT NULL DEFAULT 0 COMMENT '统计值3（如已处理数、低危数、平均处理时长）',
                                         `stat_value4` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '统计值4（如AI准确率、平均审核时长）',
                                         `stat_extra` TEXT DEFAULT NULL COMMENT '扩展统计信息（JSON格式，存储预警类型分布等复杂数据）',
                                         `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                         PRIMARY KEY (`stat_id`),
                                         INDEX `idx_stat_date` (`stat_date`),
                                         INDEX `idx_stat_type` (`stat_type`),
                                         INDEX `idx_stat_dimension` (`stat_dimension`),
                                         INDEX `idx_dept_id` (`dept_id`),
                                         INDEX `idx_composite_query` (`stat_date`, `stat_type`, `stat_dimension`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='统计结果预计算表：适配「数据分析」页面、工作台统计卡片，预计算多维度统计指标，避免实时多表联查的性能开销；无物理外键约束。';

-- 业务逻辑说明：
-- 1. 预计算规则：
--    - 日统计：每日凌晨定时任务执行，统计前一日的全量数据。
--    - 实时统计：核心指标（如今日采集数、待处理预警数）通过事件触发实时更新。
-- 2. 字段映射：
--    - 心电测量统计：stat_value1=总次数，stat_value2=正常数，stat_value3=异常数。
--    - 预警统计：stat_value1=总预警数，stat_value2=高危数，stat_value3=已处理数，stat_value4=平均处理时长。
--    - AI准确率统计：stat_value1=分析样本数，stat_value4=准确率。
-- 3. 性能优化：idx_composite_query复合索引，优化日期、类型、维度的组合查询，保证大屏与报表的低延迟响应。


SET FOREIGN_KEY_CHECKS = 1;