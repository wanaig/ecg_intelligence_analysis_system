import random
from datetime import datetime, timedelta

def get_time(days_ago):
    return (datetime.now() - timedelta(days=days_ago, minutes=random.randint(10, 60))).strftime('%Y-%m-%d %H:%M:%S')

def get_date(days_ago):
    return (datetime.now() - timedelta(days=days_ago)).strftime('%Y-%m-%d')

sql = """-- ====== 心电图情报分析系统 测试数据初始化脚本 ======
-- 生成15条左右的关联测试数据，涵盖整个生命周期

USE `ecg_intelligence_analysis_system`;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 1. 系统角色表 (sys_role)
TRUNCATE TABLE `sys_role`;
INSERT INTO `sys_role` (`role_id`, `role_name`, `description`, `user_count`, `status`) VALUES
(1001, '系统管理员', '系统所有模块配置管理', 1, 1),
(1002, '心内科医生', '具备诊断报告生成、审核、设备查看权限', 5, 1),
(1003, '心内科护士', '负责数据采集、病房管理', 8, 1),
(1004, '设备管理员', '负责设备运维与质检', 2, 1);

-- 2. 角色权限表 (sys_role_permission)
TRUNCATE TABLE `sys_role_permission`;
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_code`, `permission_name`) VALUES
(2001, 1002, 'report:audit', '审核报告'),
(2002, 1002, 'report:view', '查看报告'),
(2003, 1003, 'ecg:upload', '上传心电数据'),
(2004, 1004, 'device:maintain', '设备维护');

-- 3. 科室病区字典表 (sys_department)
TRUNCATE TABLE `sys_department`;
INSERT INTO `sys_department` (`dept_id`, `parent_id`, `parent_name`, `dept_name`, `dept_code`, `dept_type`, `sort`) VALUES
(3001, NULL, NULL, '心血管内科', 'DEPT_CARDIO_001', 1, 1),
(3002, 3001, '心血管内科', '心血管内科一病区', 'WARD_CARDIO_1', 2, 1),
(3003, 3001, '心血管内科', '心血管内科二病区', 'WARD_CARDIO_2', 2, 2),
(3004, NULL, NULL, '居家健康管理中心', 'DEPT_HOME_001', 1, 2),
(3005, 3004, '居家健康管理中心', '社区监护一站', 'STATION_HOME_1', 3, 1);

-- 4. 系统用户表 (sys_user)
TRUNCATE TABLE `sys_user`;
INSERT INTO `sys_user` (`user_id`, `user_name`, `real_name`, `password`, `role_id`, `role_name`, `dept_id`, `dept_name`, `phone`) VALUES
(4001, 'admin', '超级管理员', 'hashed_pass_123', 1001, '系统管理员', NULL, NULL, '13800138000'),
(4002, 'doctor_li', '李医生', 'hashed_pass_123', 1002, '心内科医生', 3002, '心血管内科一病区', '13811112222'),
(4003, 'doctor_zhang', '张主治', 'hashed_pass_123', 1002, '心内科医生', 3002, '心血管内科一病区', '13811113333'),
(4004, 'nurse_wang', '王护士长', 'hashed_pass_123', 1003, '心内科护士', 3002, '心血管内科一病区', '13911114444'),
(4005, 'nurse_zhao', '赵护士', 'hashed_pass_123', 1003, '心内科护士', 3003, '心血管内科二病区', '13911115555'),
(4006, 'technician_wu', '吴工', 'hashed_pass_123', 1004, '设备管理员', NULL, NULL, '13711116666');

-- 5. 操作日志表 (sys_operation_log)
TRUNCATE TABLE `sys_operation_log`;
INSERT INTO `sys_operation_log` (`log_id`, `user_id`, `real_name`, `module`, `operation_type`, `operation_content`, `request_ip`, `operation_time`) VALUES
(5001, 4002, '李医生', '心电数据管理', '查询', '查看心电数据列表', '192.168.1.100', '2026-04-18 08:30:00'),
(5002, 4003, '张主治', '诊断报告管理', '审核', '审核报告ID：20260411001，审核结果：通过', '192.168.1.101', '2026-04-18 09:15:00');

-- 6. 病区表 (sys_ward) - 与sys_department的病区级别冗余绑定
TRUNCATE TABLE `sys_ward`;
INSERT INTO `sys_ward` (`ward_id`, `ward_name`, `ward_code`, `dept_id`, `dept_name`, `floor`) VALUES
(6001, '心血管内科一病区', 'W_CAR_1', 3001, '心血管内科', '4F'),
(6002, '心血管内科二病区', 'W_CAR_2', 3001, '心血管内科', '5F');

-- 7. 房间表 (sys_room)
TRUNCATE TABLE `sys_room`;
INSERT INTO `sys_room` (`room_id`, `ward_id`, `ward_name`, `room_no`, `room_type`, `bed_total`) VALUES
(7001, 6001, '心血管内科一病区', '401', 1, 3),
(7002, 6001, '心血管内科一病区', '402', 1, 3),
(7003, 6001, '心血管内科一病区', 'ICU01', 4, 1),
(7004, 6002, '心血管内科二病区', '501', 1, 3),
(7005, 6002, '心血管内科二病区', '502', 1, 3);

-- 8. 床位表 (sys_bed) - 15个床位
TRUNCATE TABLE `sys_bed`;
INSERT INTO `sys_bed` (`bed_id`, `room_id`, `room_no`, `ward_id`, `bed_no`, `bed_type`, `bed_status`) VALUES
(8001, 7001, '401', 6001, '1床', 1, 2),
(8002, 7001, '401', 6001, '2床', 1, 2),
(8003, 7001, '401', 6001, '3床', 1, 1),
(8004, 7002, '402', 6001, '4床', 1, 2),
(8005, 7002, '402', 6001, '5床', 1, 2),
(8006, 7002, '402', 6001, '6床', 1, 1),
(8007, 7003, 'ICU01', 6001, '监护1床', 3, 2),
(8008, 7004, '501', 6002, '7床', 1, 2),
(8009, 7004, '501', 6002, '8床', 1, 2),
(8010, 7004, '501', 6002, '9床', 1, 2),
(8011, 7005, '502', 6002, '10床', 1, 2),
(8012, 7005, '502', 6002, '11床', 1, 2),
(8013, 7005, '502', 6002, '12床', 1, 2),
(8014, 7005, '502', 6002, '13床', 1, 1),
(8015, 7005, '502', 6002, '14床', 1, 1);

-- 9. 心电设备管理表 (ecg_device)
TRUNCATE TABLE `ecg_device`;
INSERT INTO `ecg_device` (`device_id`, `device_code`, `device_name`, `device_type`, `device_model`, `bind_dept_id`, `bind_dept_name`) VALUES
(9001, 'DEV_ECG_001', '12导接心电图机', 1, 'ECG-1200', 3002, '心血管内科一病区'),
(9002, 'DEV_ECG_002', '动态心电记录仪', 2, 'Holter-24H', 3002, '心血管内科一病区'),
(9003, 'DEV_MON_001', '床旁监护仪', 4, 'PM-9000', 3002, '心血管内科一病区'),
(9004, 'DEV_HOME_001', '可穿戴单导心电仪', 3, 'WearECG-1', 3005, '社区监护一站'),
(9005, 'DEV_HOME_002', '可穿戴单导心电仪', 3, 'WearECG-1', 3005, '社区监护一站');

-- 10. 患者主表 (patient_info) - 15名患者
TRUNCATE TABLE `patient_info`;
"""
import random
from datetime import datetime, timedelta
# Append patient data dynamically 
names = ['赵铁柱', '钱小萍', '孙大强', '李明', '周建国', '吴兰', '郑胜利', '王磊', '冯勇', '陈红', '褚卫国', '卫小梅', '蒋大伟', '沈桂英', '韩建平']
bed_mapping = [8001, 8002, 8004, 8005, 8007, 8008, 8009, 8010, 8011, 8012, 8013, None, None, None, None] # Last 4 are home patients
ward_mapping = [6001]*5 + [6002]*6 + [None]*4

for i in range(15):
    pid = 10001 + i
    name = names[i]
    gender = random.choice([1, 2])
    age = random.randint(45, 85)
    bd = f"{(datetime.now().year - age)}-0{random.randint(1,9)}-1{random.randint(0,9)}"
    ptype = 1 if i < 11 else 3
    ward_id = ward_mapping[i] if ward_mapping[i] else "NULL"
    bed_id = bed_mapping[i] if bed_mapping[i] else "NULL"
    device_id = 9001 if i < 11 else random.choice([9004, 9005])
    
    sql += f"INSERT INTO `patient_info` (`patient_id`, `patient_name`, `gender`, `birth_date`, `age`, `id_card`, `inpatient_no`, `ward_id`, `bed_id`, `device_id`, `phone`, `primary_diagnosis`, `risk_level`, `patient_type`, `patient_status`, `ecg_count`) VALUES "
    sql += f"({pid}, '{name}', {gender}, '{bd}', {age}, '110105******3412', 'IP{str(pid)[1:]}', {ward_id}, {bed_id}, {device_id}, '138****00{i:02d}', '高血压;冠心病', {random.choice([1,2,3])}, {ptype}, {1 if ptype==1 else 3}, {random.randint(1,5)});\n"

sql += """

-- 11. 患者随访记录表 (patient_follow_up_record)
TRUNCATE TABLE `patient_follow_up_record`;
INSERT INTO `patient_follow_up_record` (`follow_up_id`, `patient_id`, `patient_name`, `follow_up_user_id`, `follow_up_user_name`, `follow_up_time`, `follow_up_content`, `follow_up_result`, `next_follow_up_time`) VALUES
(11001, 10012, '卫小梅', 4002, '李医生', '2026-04-10 10:00:00', '电话回访，患者诉偶有心悸', '建议调整用药剂量', '2026-05-10'),
(11002, 10013, '蒋大伟', 4003, '张主治', '2026-04-15 14:30:00', '视频连线，心电数据平稳', '继续保持当前方案', '2026-05-15');

-- 12. 设备维护记录表
TRUNCATE TABLE `ecg_device_maintain_record`;
INSERT INTO `ecg_device_maintain_record` (`maintain_id`, `device_id`, `device_name`, `maintain_type`, `maintain_user_id`, `maintain_user_name`, `maintain_time`, `maintain_content`, `maintain_result`) VALUES
(12001, 9001, '12导接心电图机', '常规保养', 4006, '吴工', '2026-03-01', '清理电极片，校对导联线', '保养完成');

-- 13. 设备质控
TRUNCATE TABLE `ecg_device_quality_control`;
INSERT INTO `ecg_device_quality_control` (`qc_id`, `device_id`, `device_name`, `test_time`, `test_type`, `test_user_id`, `test_user_name`, `device_status`, `test_result`) VALUES
(13001, 9001, '12导接心电图机', '2026-04-18 08:00:00', '日检', 4004, '王护士长', '正常', '通过');

-- 14. 心电采集记录表 (ecg_collection_record) - 为每个患者生成1条
TRUNCATE TABLE `ecg_collection_record`;
"""

for i in range(15):
    pid = 10001 + i
    name = names[i]
    rid = 14001 + i
    d_id = 9001 if i < 11 else 9004
    d_name = '12导接心电图机' if i < 11 else '可穿戴单导心电仪'
    sql += f"INSERT INTO `ecg_collection_record` (`record_id`, `ecg_no`, `patient_id`, `patient_name`, `device_id`, `device_name`, `collection_start_time`, `record_status`, `ai_analysis_status`, `report_status`, `display_status`, `upload_user_name`, `ai_conclusion_short`) VALUES "
    sql += f"({rid}, 'ECG{rid}', {pid}, '{name}', {d_id}, '{d_name}', '{get_time(random.randint(0,2))}', 2, 2, 2, 2, '自动采集', '窦性心律,部分ST段改变');\n"

sql += """

-- 15. AI诊断表 (ecg_ai_diagnosis)
TRUNCATE TABLE `ecg_ai_diagnosis`;
"""
for i in range(15):
    pid = 10001 + i
    name = names[i]
    rid = 14001 + i
    sid = 15001 + i
    is_abn = random.choice([0, 1])
    abn_level = random.randint(1, 3) if is_abn else 0
    hr = random.randint(60, 110)
    sql += f"INSERT INTO `ecg_ai_diagnosis` (`ai_diagnosis_id`, `diagnosis_no`, `record_id`, `ecg_no`, `patient_id`, `patient_name`, `ai_model_version`, `heart_rate`, `is_abnormal`, `abnormal_level`, `analysis_status`) VALUES "
    sql += f"({sid}, 'DIAG{sid}', {rid}, 'ECG{rid}', {pid}, '{name}', 'v2.1.0', {hr}, {is_abn}, {abn_level}, 2);\n"

sql += """

-- 16. 诊断报告表 (ecg_diagnosis_report)
TRUNCATE TABLE `ecg_diagnosis_report`;
"""
for i in range(15):
    pid = 10001 + i
    name = names[i]
    rid = 14001 + i
    sid = 15001 + i
    repid = 16001 + i
    sql += f"INSERT INTO `ecg_diagnosis_report` (`report_id`, `report_no`, `record_id`, `ai_diagnosis_id`, `patient_id`, `patient_name`, `doctor_diagnosis`, `report_create_doctor_name`, `audit_doctor_name`, `report_status`) VALUES "
    sql += f"({repid}, 'REP{repid}', {rid}, {sid}, {pid}, '{name}', '窦性心动过缓', '李医生', '张主治', 2);\n"

sql += """

-- 17. 异常预警表 (ecg_abnormal_warning) - 取3条数据生成预警
TRUNCATE TABLE `ecg_abnormal_warning`;
INSERT INTO `ecg_abnormal_warning` (`warning_id`, `record_id`, `ai_diagnosis_id`, `patient_id`, `patient_name`, `dept_id`, `dept_name`, `warning_time`, `warning_type`, `warning_level`, `handle_status`) VALUES
(17001, 14001, 15001, 10001, '赵铁柱', 3002, '心血管内科一病区', '2026-04-18 09:00:00', 'ST段抬高', 3, 3),
(17002, 14005, 15005, 10005, '周建国', 3002, '心血管内科一病区', '2026-04-18 10:15:00', '室性早搏', 2, 1),
(17003, 14012, 15012, 10012, '卫小梅', 3005, '社区监护一站', '2026-04-18 11:30:00', '心房颤动', 3, 2);

-- 18. 实时监护表 (ecg_real_time_monitor)
TRUNCATE TABLE `ecg_real_time_monitor`;
INSERT INTO `ecg_real_time_monitor` (`monitor_id`, `patient_id`, `patient_name`, `dept_id`, `dept_name`, `current_heart_rate`, `monitor_status`, `is_abnormal`, `warning_level`, `latest_update_time`) VALUES
(18001, 10007, '郑胜利', 3002, '心血管内科一病区', 85, 1, 0, 0, '2026-04-18 12:00:00'),
(18002, 10015, '韩建平', 3005, '社区监护一站', 120, 2, 1, 2, '2026-04-18 12:05:00');

-- 19. 统计分析表 (ecg_statistics_result)
TRUNCATE TABLE `ecg_statistics_result`;
INSERT INTO `ecg_statistics_result` (`stat_id`, `stat_date`, `stat_type`, `stat_dimension`, `stat_value1`, `stat_value2`, `stat_value3`, `stat_value4`) VALUES
(19001, '2026-04-17', 1, 1, 150, 100, 50, 0.00),
(19002, '2026-04-17', 2, 1, 20, 5, 15, 12.5),
(19003, '2026-04-17', 4, 1, 150, 0, 0, 98.5);

SET FOREIGN_KEY_CHECKS = 1;
"""
with open('sql/test_data.sql', 'w', encoding='utf-8') as f:
    f.write(sql)
print("Data generated safely at sql/test_data.sql")
