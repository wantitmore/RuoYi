-- 一键复制
-- 1. 查出“系统管理”的菜单ID，并存入变量
SET @parent_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '系统管理' AND menu_type = 'M');

-- 2. 插入“一键复制默认项目”子菜单（使用上一步的变量）
INSERT INTO sys_menu (menu_name, parent_id, order_num, menu_type, visible, url, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('一键复制默认项目', @parent_id, 99, 'C', '0', 'system/defaultcopy', 'system:defaultcopy:view', '#', 'admin', NOW(), '', NULL, '');

-- 3. 给超级管理员授权
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, menu_id FROM sys_menu WHERE menu_name = '一键复制默认项目'
AND NOT EXISTS (
    SELECT 1 FROM sys_role_menu rm WHERE rm.role_id = 1 AND rm.menu_id = sys_menu.menu_id
);


--正负面清单部门隔离

-- 清空旧数据
TRUNCATE TABLE positive_negative;

-- 添加部门字段
ALTER TABLE positive_negative ADD COLUMN dept_id bigint(20) DEFAULT NULL COMMENT '所属部门ID';





--量化考核
-- 1. 清空已有数据
TRUNCATE TABLE kpi_score;
TRUNCATE TABLE kpi_item;

-- 2. 为每个部门插入默认项目（部门由 sys_dept 表决定）
INSERT INTO kpi_item (name, max_score, score_type, dept_id, category, remark, create_by, create_time)
SELECT proj.name, proj.max_score, proj.score_type, d.dept_id, proj.category, proj.remark, 'admin', NOW()
FROM sys_dept d
CROSS JOIN (
    SELECT '未按规定建立、更新专管罪犯动态档案' AS name, 20 AS max_score, 'NUMBER' AS score_type, '一犯一档分析' AS category, '视情节予以扣1-3分' AS remark
    UNION ALL SELECT '掌握四知道、无册点名及专管罪犯相关情况未达要求', 20, 'NUMBER', '一犯一档分析', ''
    UNION ALL SELECT '未按规定开展入监、中期、日常危险性评估', 20, 'NUMBER', '一犯一档分析', '视情节予以扣1-3分'
    UNION ALL SELECT '其他未按要求落实一犯一档分析工作', 20, 'NUMBER', '一犯一档分析', '视情节予以扣1-3分'
    UNION ALL SELECT '未按规定落实狱情排查及闭环处置', 20, 'NUMBER', '一情一报联动', '视情节予以扣1-3分'
    UNION ALL SELECT '未按规定落实罪犯耳目、信息员管理；', 20, 'NUMBER', '一情一报联动', '视情节予以扣1-3分'
    UNION ALL SELECT '未落实清仓、搜身，违禁品、违规品、危险品处置不规范', 20, 'NUMBER', '一情一报联动', '视情节予以扣1-3分'
    UNION ALL SELECT '异常罪犯未落实互监包夹盯防、及时报告处置', 20, 'NUMBER', '一情一报联动', '视情节予以扣1-3分'
    UNION ALL SELECT '其他未按要求落实一情一报联动工作', 20, 'NUMBER', '一情一报联动', '视情节予以扣1-3分'
    UNION ALL SELECT '未落实讲评工作要求', 20, 'NUMBER', '一时一控维稳', '视情节予以扣1-3分'
    UNION ALL SELECT '未按规定开展个别谈话教育', 20, 'NUMBER', '一时一控维稳', '视情节予以扣1-3分'
    UNION ALL SELECT '敏感时段未落实专管罪犯思想动态监测', 20, 'NUMBER', '一时一控维稳', '视情节予以扣1-3分'
    UNION ALL SELECT '未及时开展政策宣传、教育转化，化解不稳定因素', 20, 'NUMBER', '一时一控维稳', '视情节予以扣1-3分'
    UNION ALL SELECT '其他未按要求落实一时一控维稳工作', 20, 'NUMBER', '一时一控维稳', '视情节予以扣1-3分'
    UNION ALL SELECT '未及时接访、化解、处置罪犯矛盾诉求', 20, 'NUMBER', '一诉一化疏导', '视情节予以扣1-3分'
    UNION ALL SELECT '突出矛盾诉求未及时教育转化、上报、跟进', 20, 'NUMBER', '一诉一化疏导', '视情节予以扣1-3分'
    UNION ALL SELECT '其他未按要求落实一诉一化疏导工作', 20, 'NUMBER', '一诉一化疏导', '视情节予以扣1-3分'
     UNION ALL SELECT '重点罪犯教育转化、专项档案登记更新不到位', 20, 'NUMBER', '一危一策攻坚', '视情节予以扣1-3分'
    UNION ALL SELECT '未落实重点罪犯搜身、清查要求', 20, 'NUMBER', '一危一策攻坚', '视情节予以扣1-3分'
    UNION ALL SELECT '未落实重点罪犯专项谈话教育', 20, 'NUMBER', '一危一策攻坚', '视情节予以扣1-3分'
    UNION ALL SELECT '未及时调整顽固、危险、重点罪犯防控措施', 20, 'NUMBER', '一危一策攻坚', '视情节予以扣1-3分'
    UNION ALL SELECT '未落实“一人一策”教育转化', 20, 'NUMBER', '一危一策攻坚', '视情节予以扣1-3分'
    UNION ALL SELECT '其他未按要求落实一危一策攻坚工作', 20, 'NUMBER', '一危一策攻坚', '视情节予以扣1-3分'
    UNION ALL SELECT '未跟进专管罪犯岗位技能培训', 20, 'NUMBER', '一定一查劳动', '视情节予以扣1-3分'
    UNION ALL SELECT '未跟进劳动类级确定变更、劳动定额完成情况', 20, 'NUMBER', '一定一查劳动', '视情节予以扣1-3分'
    UNION ALL SELECT '未掌握罪犯劳动相关情况，未对劳动违规被处理罪犯开展谈话教育', 20, 'NUMBER', '一定一查劳动', '视情节予以扣1-3分'
    UNION ALL SELECT '未跟进核查安全生产措施落实情况', 20, 'NUMBER', '一定一查劳动', '视情节予以扣1-3分'
    UNION ALL SELECT '其他未按要求落实一定一查劳动工作', 20, 'NUMBER', '一定一查劳动', '视情节予以扣1-3分'
    UNION ALL SELECT '未按时完成计分考核审查提请', 20, 'NUMBER', '一案一审评查', '视情节予以扣1-3分'
    UNION ALL SELECT '“减假暂”案件台账审核填写不规范', 20, 'NUMBER', '一案一审评查', '视情节予以扣1-3分'
    UNION ALL SELECT '未落实“减假暂”案件实质化审理', 20, 'NUMBER', '一案一审评查', '视情节予以扣1-3分'
    UNION ALL SELECT '未及时对相关罪犯开展教育转化', 20, 'NUMBER', '一案一审评查', '视情节予以扣1-3分'
    UNION ALL SELECT '其他未按要求落实一案一审评查工作。', 20, 'NUMBER', '一案一审评查', '视情节予以扣1-3分'
    UNION ALL SELECT '未按规定开展出监危险性评估', 20, 'NUMBER', '一释一评预警', '视情节予以扣1-3分'
    UNION ALL SELECT '未开展临释罪犯回归社会专项教育', 20, 'NUMBER', '一释一评预警', '视情节予以扣1-3分'
    UNION ALL SELECT '未落实队前讲评工作要求', 20, 'NUMBER', '一释一评预警', '视情节予以扣1-3分'
    UNION ALL SELECT '罪犯临释动态掌握不清，异常情况未及时报告处置', 20, 'NUMBER', '一释一评预警', '视情节予以扣1-3分'
    UNION ALL SELECT '其他未按要求落实一释一评预警工作', 20, 'NUMBER', '一释一评预警', '视情节予以扣1-3分'
    UNION ALL SELECT '值班执勤信息、异常狱情未规范转递交接', 20, 'NUMBER', '一班一表管控', '视情节予以扣1-3分'
    UNION ALL SELECT '未及时排查处置现场异常情况', 20, 'NUMBER', '一班一表管控', '视情节予以扣1-3分'
    UNION ALL SELECT '其他未按要求落实一班一表管控工作', 20, 'NUMBER', '一班一表管控', '视情节予以扣1-3分'
    UNION ALL SELECT '未按规定履行岗位职责', 20, 'NUMBER', '一岗一责处置', '视情节予以扣1-3分'
    UNION ALL SELECT '执勤从事与工作无关事项', 20, 'NUMBER', '一岗一责处置', '视情节予以扣1-3分'
    UNION ALL SELECT '未落实警察直接管理要求', 20, 'NUMBER', '一岗一责处置', '视情节予以扣1-3分'
    UNION ALL SELECT '存在脱岗、串岗行为', 20, 'NUMBER', '一岗一责处置', '视情节予以扣1-3分'
    UNION ALL SELECT '其他未按要求落实一岗一责处置工作', 20, 'NUMBER', '一岗一责处置', '视情节予以扣1-3分'
    UNION ALL SELECT '其它未落实“两个职责”相关工作情形', 20, 'NUMBER', '其他履职事项', '视情节予以扣1-5分'
) proj;

--考核类别
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark)
SELECT '考核类别', 'kpi_category', '0', 'admin', NOW(), '', NULL, '考核类别列表'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'kpi_category');

DELETE FROM sys_dict_data WHERE dict_type = 'kpi_category';

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES
(1, '一犯一档分析', '一犯一档分析', 'kpi_category', NULL, NULL, 'N', '0', 'admin', NOW(), '', NULL, NULL),
(2, '一情一报联动', '一情一报联动', 'kpi_category', NULL, NULL, 'N', '0', 'admin', NOW(), '', NULL, NULL),
(3, '一时一控维稳', '一时一控维稳', 'kpi_category', NULL, NULL, 'N', '0', 'admin', NOW(), '', NULL, NULL),
(4, '一诉一化疏导', '一诉一化疏导', 'kpi_category', NULL, NULL, 'N', '0', 'admin', NOW(), '', NULL, NULL),
(5, '一危一策攻坚', '一危一策攻坚', 'kpi_category', NULL, NULL, 'N', '0', 'admin', NOW(), '', NULL, NULL);
(6, '一定一查劳动', '一定一查劳动', 'kpi_category', NULL, NULL, 'N', '0', 'admin', NOW(), '', NULL, NULL),
(7, '一案一审评查', '一案一审评查', 'kpi_category', NULL, NULL, 'N', '0', 'admin', NOW(), '', NULL, NULL),
(8, '一释一评预警', '一释一评预警', 'kpi_category', NULL, NULL, 'N', '0', 'admin', NOW(), '', NULL, NULL),
(9, '一班一表管控', '一班一表管控', 'kpi_category', NULL, NULL, 'N', '0', 'admin', NOW(), '', NULL, NULL),
(10, '一岗一责处置', '一岗一责处置', 'kpi_category', NULL, NULL, 'N', '0', 'admin', NOW(), '', NULL, NULL);
(11, '其他履职事项', '其他履职事项', 'kpi_category', NULL, NULL, 'N', '0', 'admin', NOW(), '', NULL, NULL);

--添加工作内容字段
ALTER TABLE kpi_item ADD COLUMN work_requirement varchar(200) DEFAULT NULL COMMENT '工作内容要求';
UPDATE kpi_item SET work_requirement = '专管警察对专管罪犯逐一建立动态档案，掌握专管罪犯“四知道”，做到“无册点名”；掌握认罪服判以及入监后的改造情况，及时做好心理动态分析和危险性评估，采取相应教育改造措施，及时更新档案数据，真实记录专管罪犯从入监到出监的改造全过程。' WHERE category = '一犯一档分析';
UPDATE kpi_item SET work_requirement = '严格执行《狱情排查处置办法》，专管警察落实狱情排查并按要求录入狱情。按需物建耳目信息员，及时发现化解异常情况。落实清仓搜身，排查私藏“两违一危”物品。对异常罪犯落实互监包夹盯防、即时报告、及时处置，记录存档，实现问题闭环管理。' WHERE category = '一情一报联动';
UPDATE kpi_item SET work_requirement = '定期对专管罪犯开展集中讲评、个别谈话教育。春节、中秋、国庆等敏感时段开展思想动态监测，及时化解不稳定因素，保障重要节点监管安全。' WHERE category = '一时一控维稳';
UPDATE kpi_item SET work_requirement = '专管警察排查、收集罪犯矛盾诉求并及时处置。对突出矛盾诉求开展个案分析，运用个别谈话、心理干预化解矛盾。对涉访涉诉、存在仇视言行、诉求突出罪犯及时教育转化、上报跟进。' WHERE category = '一诉一化疏导';
UPDATE kpi_item SET work_requirement = '按照《广东省监狱管理局重点罪犯管理教育办法》建立专项档案，落实谈话教育、清仓搜身；每月分析危险倾向变化，动态调整风险防控和教育转化措施，定期记录报告改造进展。' WHERE category = '一危一策攻坚';
UPDATE kpi_item SET work_requirement = '跟进专管罪犯岗位技能培训、劳动岗位安排、劳动类级确定和变更、劳动定额完成、劳动计分考核、安全生产措施落实；掌握劳动改造效果，及时处置异常情况，按期评估劳动改造质效。' WHERE category = '一定一查劳动';
UPDATE kpi_item SET work_requirement = '每月审查提请专管罪犯计分考核；按照“减假暂”案件办理要求落实实质化审理。对考核不满、“减假暂”未达预期罪犯及时开展教育转化、上报跟进。' WHERE category = '一案一审评查';
UPDATE kpi_item SET work_requirement = '掌握专管罪犯刑满释放前思想动态，及时开展出监前专项教育，做好出监评估，出具再犯危险性评估意见；发现异常立即预警上报处置。' WHERE category = '一释一评预警';
UPDATE kpi_item SET work_requirement = '监区、分监区值班领导对现场安全负第一责任，开展现场检查、警力调配，处置交接异常情况；管教员承担现场安全直接责任，排查、处置、上报、交接罪犯风险、矛盾诉求与异常情况。' WHERE category = '一班一表管控';
UPDATE kpi_item SET work_requirement = '全面落实监区警察“两个职责”各项工作要求' WHERE category = '一岗一责处置';
UPDATE kpi_item SET work_requirement = '各岗位出现本标准未规定的不规范情形，依照上级法规制度处理。本标准由广东省河源监狱考核办负责解释，自下发之日起施行。' WHERE category = '其他履职事项';


-- 1.开会台账主表
CREATE TABLE meeting_log (
    id               bigint(20)   NOT NULL AUTO_INCREMENT COMMENT 'ID',
    meeting_date     date         NOT NULL COMMENT '开会日期',
    meeting_content  text         COMMENT '开会情况',
    dept_id          bigint(20)   DEFAULT NULL COMMENT '所属部门ID',
    create_by        varchar(64)  DEFAULT '' COMMENT '创建者',
    create_time      datetime     COMMENT '创建时间',
    update_by        varchar(64)  DEFAULT '' COMMENT '更新者',
    update_time      datetime     COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='开会台账主表';

-- 2.参会人员关联表
CREATE TABLE meeting_participant (
    id           bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    meeting_id   bigint(20) NOT NULL COMMENT '会议ID',
    user_id      bigint(20) NOT NULL COMMENT '用户ID',
    PRIMARY KEY (id),
    KEY idx_meeting_id (meeting_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='会议参会人员关联表';

--3.菜单sql
-- 清理旧数据
DELETE rm FROM sys_role_menu rm
JOIN sys_menu m ON rm.menu_id = m.menu_id
WHERE m.menu_name LIKE '%开会台账%';

DELETE FROM sys_menu WHERE menu_name LIKE '%开会台账%';

-- 获取父菜单ID
SET @parent_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '执勤表扬台账' AND menu_type = 'M');

-- 插入子菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, menu_type, visible, url, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('开会台账管理', @parent_id, 2, 'C', '0', 'meeting/log', 'meeting:view', '#', 'admin', NOW(), '', NULL, '开会台账列表');
SET @menu_id = LAST_INSERT_ID();

-- 按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, menu_type, visible, url, perms, icon, create_by, create_time, update_by, update_time, remark) VALUES
('开会台账查询', @menu_id, 1, 'F', '0', '', 'meeting:list', '#', 'admin', NOW(), '', NULL, ''),
('开会台账新增', @menu_id, 2, 'F', '0', '', 'meeting:add', '#', 'admin', NOW(), '', NULL, ''),
('开会台账修改', @menu_id, 3, 'F', '0', '', 'meeting:edit', '#', 'admin', NOW(), '', NULL, ''),
('开会台账删除', @menu_id, 4, 'F', '0', '', 'meeting:remove', '#', 'admin', NOW(), '', NULL, '');

-- 授权给超级管理员
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, menu_id FROM sys_menu 
WHERE menu_name LIKE '%开会台账%'
AND NOT EXISTS (
    SELECT 1 FROM sys_role_menu rm
    WHERE rm.role_id = 1 AND rm.menu_id = sys_menu.menu_id
);

--增加其他履职事项 增加项目
INSERT INTO kpi_item (name, max_score, score_type, dept_id, category, remark, create_by, create_time)
SELECT proj.name, proj.max_score, proj.score_type, d.dept_id, proj.category, '', 'admin', NOW()
FROM sys_dept d
CROSS JOIN (
    SELECT '正面清单' AS name, 20 AS max_score, 'NUMBER' AS score_type, '其他履职事项' AS category
    UNION ALL SELECT '负面清单', 20, 'NUMBER', '其他履职事项'
    UNION ALL SELECT '问题通报', 20, 'NUMBER', '其他履职事项'
) proj
WHERE d.del_flag = '0'
  AND NOT EXISTS (SELECT 1 FROM kpi_item existing WHERE existing.dept_id = d.dept_id AND existing.name = proj.name);

--修改菜单名称
-- 修改一级菜单名称
UPDATE sys_menu SET menu_name = '值班领导六必查' WHERE menu_name = '六必查' AND menu_type = 'M';

-- 修改子菜单名称（例如“六必查录入”、“六必查管理”等，如果有的话）
UPDATE sys_menu SET menu_name = REPLACE(menu_name, '六必查', '值班领导六必查') WHERE menu_name LIKE '%六必查%' AND menu_type = 'C';

--修改六必查功能
-- 增加新字段
-- =====================================================
-- 值班领导六必查模块数据库更新脚本
-- 包含：表结构调整、唯一约束更新、检查项目重置
-- =====================================================

-- =====================================================
-- 值班领导六必查模块数据库更新脚本（内网直接执行版）
-- 执行前请备份 six_check_item 和 six_check_record 表
-- 如果某条语句报错（如字段已存在、索引已存在），可忽略继续执行下一条
-- =====================================================

-- 1. 增加新字段（如果已存在会报错，忽略即可）
ALTER TABLE six_check_record ADD COLUMN check_date  date         DEFAULT NULL COMMENT '值班日期';
ALTER TABLE six_check_record ADD COLUMN shift       varchar(10)  DEFAULT NULL COMMENT '值班班次（A班/B班/C班）';
ALTER TABLE six_check_record ADD COLUMN duty_leader varchar(50)  DEFAULT NULL COMMENT '值班领导';

-- 2. 删除旧的唯一约束（如果不存在会报错，忽略即可）
ALTER TABLE six_check_record DROP INDEX uk_item_month_dept;

-- 3. 创建新的唯一约束（如果已存在会报错，忽略即可）
ALTER TABLE six_check_record ADD UNIQUE KEY uk_item_date_shift_dept (item_id, check_date, shift, dept_id);

-- 4. 清空旧检查项目并插入7项新内容
TRUNCATE TABLE six_check_item;

INSERT INTO six_check_item (dept_id, name, sort_order, create_by, create_time)
SELECT d.dept_id, proj.name, proj.sort_order, 'admin', NOW()
FROM sys_dept d
CROSS JOIN (
    SELECT '值班民警到岗到位、履行岗位职责、规范佩戴和使用警戒具情况' AS name, 1 AS sort_order
    UNION ALL SELECT '值班民警“三大现场”直接管理、规范执法和现场管理秩序情况', 2
    UNION ALL SELECT '值班民警落实五个重点、安全隐患排查及整改情况', 3
    UNION ALL SELECT '值班民警生产组织、队列指挥、搜身讲评情况', 4
    UNION ALL SELECT '值班民警狱情排查及处置、执勤日志登记和交接情况', 5
    UNION ALL SELECT '监狱和监区交办的重点问题整改和重点工作推进情况', 6
    UNION ALL SELECT '监狱值班组巡查情况', 7
) proj
WHERE d.del_flag = '0';

ALTER TABLE six_check_record MODIFY COLUMN batch_no varchar(7) DEFAULT NULL COMMENT '考核批次（已废弃）';

--六必查月汇总

SET @parent_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '值班领导六必查' AND menu_type = 'M');

INSERT INTO sys_menu (menu_name, parent_id, order_num, menu_type, visible, url, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('六必查月汇总', @parent_id, 2, 'C', '0', 'sixcheck/record/summary', 'sixcheck:summary', '#', 'admin', NOW(), '', NULL, '');

-- 1. 先查出父菜单ID并保存到变量
SET @parent_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '六必查月汇总');

-- 2. 使用变量插入按钮菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, menu_type, visible, url, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('六必查汇总查询', @parent_id, 1, 'F', '0', '', 'sixcheck:summary', '#', 'admin', NOW(), '', NULL, '');

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, menu_id FROM sys_menu WHERE menu_name LIKE '%六必查月汇总%'
AND NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = sys_menu.menu_id);



--复制107三个项目
INSERT INTO kpi_item (name, max_score, score_type, dept_id, category, work_requirement, remark, create_by, create_time)
SELECT name, max_score, score_type, 108, category, work_requirement, remark, 'admin', NOW()
FROM kpi_item
WHERE dept_id = 107
  AND NOT EXISTS (
      SELECT 1 FROM kpi_item existing
      WHERE existing.dept_id = 108 AND existing.name = kpi_item.name
  );

  INSERT INTO six_check_item (dept_id, name, sort_order, create_by, create_time)
SELECT 108, name, sort_order, 'admin', NOW()
FROM six_check_item
WHERE dept_id = 107
  AND NOT EXISTS (
      SELECT 1 FROM six_check_item si
      WHERE si.dept_id = 108 AND si.name = six_check_item.name
  );

  INSERT INTO video_check_item (dept_id, check_position, specific_content, sort_order, create_by, create_time)
SELECT 108, check_position, specific_content, sort_order, 'admin', NOW()
FROM video_check_item
WHERE dept_id = 107
  AND NOT EXISTS (
      SELECT 1 FROM video_check_item vci
      WHERE vci.dept_id = 108 AND vci.check_position = video_check_item.check_position
  );

--六必查更改 1. 把“监狱值班组巡查情况”改为“上级问题通报”
UPDATE six_check_item
SET name = '上级问题通报'
WHERE name = '监狱值班组巡查情况';

-- 2. 为每个部门增加“其它”这一项（sort_order 设为 8，避免重复插入）
INSERT INTO six_check_item (dept_id, name, sort_order, create_by, create_time)
SELECT d.dept_id, '其它', 8, 'admin', NOW()
FROM sys_dept d
WHERE d.del_flag = '0'
  AND NOT EXISTS (
      SELECT 1 FROM six_check_item si
      WHERE si.dept_id = d.dept_id AND si.name = '其它'
  );

--分区负责人评价表
  CREATE TABLE quarter_factor (
    id          bigint(20)   NOT NULL AUTO_INCREMENT COMMENT 'ID',
    name        varchar(100) NOT NULL COMMENT '评价要素',
    content     varchar(500) DEFAULT NULL COMMENT '评价内容',
    sort_order  int(4)       DEFAULT 0 COMMENT '排序号',
    type        varchar(50)  DEFAULT 'quarter' COMMENT '考核类型',
    create_by   varchar(64)  DEFAULT '' COMMENT '创建者',
    create_time datetime     COMMENT '创建时间',
    update_by   varchar(64)  DEFAULT '' COMMENT '更新者',
    update_time datetime     COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='分监区负责人评价表';

CREATE TABLE quarter_factor (
    id          bigint(20)   NOT NULL AUTO_INCREMENT COMMENT 'ID',
    name        varchar(100) NOT NULL COMMENT '评价要素',
    content     varchar(500) DEFAULT NULL COMMENT '评价内容',
    sort_order  int(4)       DEFAULT 0 COMMENT '排序号',
    type        varchar(50)  DEFAULT 'quarter' COMMENT '考核类型',
    create_by   varchar(64)  DEFAULT '' COMMENT '创建者',
    create_time datetime     COMMENT '创建时间',
    update_by   varchar(64)  DEFAULT '' COMMENT '更新者',
    update_time datetime     COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='评价要素表';

INSERT INTO quarter_factor (name, content, sort_order, type, create_by, create_time) VALUES
('政治思想方面', '具体为：坚定理想信念、对党忠诚，遵守政治纪律和政治规矩；自觉增强“四个意识”、坚定“四个自信”、做到“两个维护”；自觉参加监狱党委各项政治学习与活动。', 1, 'quarter', 'admin', NOW()),
('工作落实方面', '具体为围绕监狱中心工作，服务大局，科室、监区领导成员间团结协作；认真贯彻落实上级政策、精神，工作作风严谨，积极解决工作中重点、难点问题。带领的队伍有活力、凝聚力和战斗力。', 2, 'quarter', 'admin', NOW()),
('组织纪律方面', '具体为：严格树立组织观念，遵守组织纪律，服从组织安排；遵守廉政规定，严守警囚关系底线；坚守忠诚老实、公道正派、实事求是、清正廉洁等价值观，遵守社会公德、职业道德、家庭美德。', 3, 'quarter', 'admin', NOW());

--分区季度考核记录表
CREATE TABLE quarter_score (
    id          bigint(20)   NOT NULL AUTO_INCREMENT COMMENT 'ID',
    user_id     bigint(20)   NOT NULL COMMENT '被考核人ID',
    factor_id   bigint(20)   NOT NULL COMMENT '考察因素ID',
    grade       varchar(10)  NOT NULL COMMENT '评价等级（好/较好/一般/差）',
    score       int(4)       NOT NULL COMMENT '对应分数（95/85/70/55）',
    batch_no    varchar(7)   NOT NULL COMMENT '季度（YYYY-QX）',
    dept_id     bigint(20)   NOT NULL COMMENT '所属部门ID',
    create_by   varchar(64)  DEFAULT '' COMMENT '创建者',
    create_time datetime     COMMENT '创建时间',
    update_by   varchar(64)  DEFAULT '' COMMENT '更新者',
    update_time datetime     COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_factor_quarter (user_id, factor_id, batch_no, create_by)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='分监区负责人季度考核记录表';

-- 在“考核结果”旁边新增“季度考核”菜单
SET @kpi_menu_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '量化考核' AND menu_type = 'M');

INSERT INTO sys_menu (menu_name, parent_id, order_num, menu_type, visible, url, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('季度考核', @kpi_menu_id, 6, 'C', '0', 'quarter/list', 'quarter:view', '#', 'admin', NOW(), '', NULL, '季度考核模块');

SET @quarter_menu_id = LAST_INSERT_ID();

-- 按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, menu_type, visible, url, perms, icon, create_by, create_time, update_by, update_time, remark) VALUES
('季度考核查询', @quarter_menu_id, 1, 'F', '0', '', 'quarter:list', '#', 'admin', NOW(), '', NULL, ''),
('季度考核新增', @quarter_menu_id, 2, 'F', '0', '', 'quarter:add', '#', 'admin', NOW(), '', NULL, ''),
('季度考核修改', @quarter_menu_id, 3, 'F', '0', '', 'quarter:edit', '#', 'admin', NOW(), '', NULL, ''),
('季度考核删除', @quarter_menu_id, 4, 'F', '0', '', 'quarter:remove', '#', 'admin', NOW(), '', NULL, '');

-- 授权给超级管理员
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, menu_id FROM sys_menu WHERE menu_name LIKE '%季度考核%'
AND NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = sys_menu.menu_id);
--岗位编码
CREATE TABLE assess_post_config (
    id          bigint(20)   NOT NULL AUTO_INCREMENT COMMENT 'ID',
    type        varchar(50)  NOT NULL COMMENT '考核类型：quarter-季度，common-普通警员',
    post_code   varchar(64)  NOT NULL COMMENT '岗位编码（对应 sys_post.post_code）',
    sort_order  int(4)       DEFAULT 0 COMMENT '排序号（用于控制岗位展示顺序）',
    create_by   varchar(64)  DEFAULT '' COMMENT '创建者',
    create_time datetime     COMMENT '创建时间',
    update_by   varchar(64)  DEFAULT '' COMMENT '更新者',
    update_time datetime     COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_type_post (type, post_code)  -- 防止同一类型重复插入相同岗位
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='考核类型与岗位映射配置表';

-- 分区领导季度考核需要哪些岗位
INSERT INTO assess_post_config (type, post_code, sort_order) VALUES 
('quarter', 'fq_leader', 1),   -- 分区领导
('quarter', '1fq_leader', 2),   -- 分区领导
('quarter', '2fq_leader', 3),   -- 分区领导        -- 分区领导

-- 普通警员考核需要哪些岗位
INSERT INTO assess_post_config (type, post_code, sort_order) VALUES 
('common', '1qu_user', 1);
('common', '2qu_user', 2);
('common', 'sec', 3);

--警员考核要素
INSERT INTO quarter_factor (name, content, sort_order, type, create_by, create_time) VALUES 
('政治思想方面', '具体为：坚定理想信念、对党忠诚，尊崇党章、遵守政治纪律和政治规矩；积极参加党支部政治学习和参与党支部各项活动。', 1, 'common', 'admin', NOW()),
('工作落实方面', '具体为：工作作风严谨，严格执行制度、落实指令、履行岗位职责；敢于担当，遇事不推诿、不退避。', 2, 'common', 'admin', NOW()),
('组织纪律方面', '具体为：严格树立组织观念，服从组织安排；遵守廉政规定，严守警囚关系底线，遵守社会公德、职业道德、家庭美德。', 3, 'common', 'admin', NOW());

UPDATE sys_dict_data 
SET dict_label = '负面清单', 
    dict_value = '负面清单' 
WHERE dict_type = 'kpi_category' 
  AND dict_label = '其他履职事项';

INSERT INTO kpi_item (name, max_score, score_type, dept_id, category, remark, create_by, create_time)
SELECT proj.name, proj.max_score, proj.score_type, d.dept_id, proj.category, '', 'admin', NOW()
FROM sys_dept d
CROSS JOIN (
    SELECT '正面清单' AS name, 20 AS max_score, 'NUMBER' AS score_type, '其他履职事项' AS category
    UNION ALL SELECT '负面清单', 20, 'NUMBER', '其他履职事项'
    UNION ALL SELECT '问题通报', 20, 'NUMBER', '其他履职事项'
) proj
WHERE d.del_flag = '0'
  AND NOT EXISTS (SELECT 1 FROM kpi_item existing WHERE existing.dept_id = d.dept_id AND existing.name = proj.name);


INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES(12, '正面清单', '正面清单', 'kpi_category', NULL, NULL, 'N', '0', 'admin', NOW(), '', NULL, NULL);

-- ============================================================
-- 第一步：将原来的 3 条记录改名（针对所有有效部门）
-- ============================================================
UPDATE kpi_item 
SET name = '不积极履行岗位职责，未及时完成任务，尚未造成不良后果的'    
WHERE name = '正面清单' 
  AND category = '负面清单' 
  AND dept_id IN (SELECT dept_id FROM sys_dept WHERE del_flag = '0');

UPDATE kpi_item 
SET name = '不积极工作，不主动作为，推诿扯皮，尚未造成不良后果的'    
WHERE name = '负面清单' 
  AND category = '负面清单' 
  AND dept_id IN (SELECT dept_id FROM sys_dept WHERE del_flag = '0');

UPDATE kpi_item 
SET name = '完成工作任务质量不高、效果不佳的'    
WHERE name = '问题通报' 
  AND category = '负面清单' 
  AND dept_id IN (SELECT dept_id FROM sys_dept WHERE del_flag = '0');

UPDATE kpi_item 
SET name = '分管工作在监狱以上检查、评比中受到批评，承担责任较轻的'    
WHERE name = '其它未落实“两个职责”相关工作情形' 
  AND category = '负面清单' 
  AND dept_id IN (SELECT dept_id FROM sys_dept WHERE del_flag = '0');

-- ============================================================
-- 第二步：为每个有效部门新增 4 条记录（名称、分值、类型、分类请替换）
-- ============================================================
INSERT INTO kpi_item (name, max_score, score_type, dept_id, category, remark, create_by, create_time)
SELECT proj.name, proj.max_score, proj.score_type, d.dept_id, proj.category, '', 'admin', NOW()
FROM sys_dept d
CROSS JOIN (
    -- 请将下面 4 条 UNION ALL 的占位值替换为实际数据
    SELECT '警容不整、纪律作风松散，尚未造成不良影响的' AS name, 10 AS max_score, 'NUMBER' AS score_type, '负面清单' AS category
    UNION ALL 
    SELECT '违法规章制度，情节较轻的', 10, 'NUMBER', '负面清单'
    UNION ALL 
    SELECT '其他应给予批评的行为', 10, 'NUMBER', '负面清单'
) proj
WHERE d.del_flag = '0'
  AND NOT EXISTS (
      SELECT 1 FROM kpi_item existing 
      WHERE existing.dept_id = d.dept_id 
        AND existing.name = proj.name
  );

INSERT INTO kpi_item (name, max_score, score_type, dept_id, category, remark, create_by, create_time)
SELECT proj.name, proj.max_score, proj.score_type, d.dept_id, proj.category, '', 'admin', NOW()
FROM sys_dept d
CROSS JOIN ( 
    SELECT '积极履行岗位职责，出色完成工作任务' AS name, 10 AS max_score, 'NUMBER' AS score_type, '正面清单' AS category
    UNION ALL 
    SELECT '听从指挥，服从安排，落实制度好，执行力强', 10, 'NUMBER', '正面清单'
    UNION ALL 
    SELECT '在专项工作中表现较好，做出一定成绩', 10, 'NUMBER', '正面清单'
    UNION ALL 
    SELECT '积极参与处置突发事件，表现较突出', 10, 'NUMBER', '正面清单'
    UNION ALL 
    SELECT '在省厅、局检查中受到表扬', 10, 'NUMBER', '正面清单'
    UNION ALL 
    SELECT '每教育转化一名重点罪犯或高度、极高度罪犯，或包干A类重点犯连续较长时间未出现问题', 10, 'NUMBER', '正面清单'
    UNION ALL 
    SELECT '认真钻研业务，开展理论研究，推动工作创新，获得省局以上奖励', 10, 'NUMBER', '正面清单'
    UNION ALL 
    SELECT '认真遵守社会公德、家庭美德，有好人好事行为，受到群众好评或表扬', 10, 'NUMBER', '正面清单'
    UNION ALL 
    SELECT '其他应给予肯定的行为', 10, 'NUMBER', '正面清单'
) proj
WHERE d.del_flag = '0'
  AND NOT EXISTS (
      SELECT 1 FROM kpi_item existing 
      WHERE existing.dept_id = d.dept_id 
        AND existing.name = proj.name
  );
--- 为 kpi_score 表增加 source_record_id 字段，用于关联六必查记录
ALTER TABLE kpi_score ADD COLUMN source_record_id bigint(20) DEFAULT NULL COMMENT '来源记录ID（关联六必查记录）';
ALTER TABLE kpi_score ADD INDEX idx_source_record (source_record_id);

--新增扣分明细表
CREATE TABLE six_check_deduct_detail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    kpi_score_id BIGINT NOT NULL COMMENT '关联的KPI记录ID',
    deduct_info VARCHAR(500) COMMENT '扣分描述',
    status TINYINT DEFAULT 1 COMMENT '1-有效，0-已撤销',
    create_by VARCHAR(64),
    create_time DATETIME,
    update_by VARCHAR(64),
    update_time DATETIME,
    INDEX idx_kpi_score (kpi_score_id)
) COMMENT '扣分明细表';

ALTER TABLE six_check_deduct_detail ADD COLUMN six_check_record_id BIGINT COMMENT '六必查记录ID';
ALTER TABLE six_check_deduct_detail ADD INDEX idx_six_check_record (six_check_record_id);
ALTER TABLE six_check_deduct_detail 
ADD COLUMN deduct_score DECIMAL(10,2) COMMENT '扣分分数（冗余存储，便于撤销）';

ALTER TABLE six_check_deduct_detail 
ADD COLUMN source_type VARCHAR(20) DEFAULT 'sixcheck' COMMENT '来源：sixcheck-六必查, video-视频回放';


