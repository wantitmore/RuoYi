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
    UNION ALL SELECT '存在上述条款未列明，未落实“两个职责”相关工作情形', 20, 'NUMBER', '其他履职事项', '视情节予以扣1-5分'
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