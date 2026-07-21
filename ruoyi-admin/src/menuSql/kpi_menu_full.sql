-- 清理旧菜单
DELETE FROM sys_menu WHERE menu_name IN (
    '量化考核', '考核项目', '考核项目查询', '考核项目新增', '考核项目修改', '考核项目删除',
    '考核分数', '考核分数查询', '考核分数新增', '考核分数修改', '考核分数删除',
    '考核打分', '考核结果'
);

-- 1. 量化考核目录
INSERT INTO sys_menu (menu_name, parent_id, order_num, menu_type, visible, url, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('量化考核', 0, 1, 'M', '0', '', 'kpi:view', 'fa fa-tasks', 'admin', NOW(), '', NULL, '量化考核模块');
SET @kpi_menu_id = LAST_INSERT_ID();





-- 2. 考核项目
INSERT INTO sys_menu (menu_name, parent_id, order_num, menu_type, visible, url, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('考核项目', @kpi_menu_id, 1, 'C', '0', 'kpi/item', 'kpi:item:view', '#', 'admin', NOW(), '', NULL, '考核项目管理');
SET @item_menu_id = LAST_INSERT_ID();

INSERT INTO sys_menu (menu_name, parent_id, order_num, menu_type, visible, url, perms, icon, create_by, create_time, update_by, update_time, remark) VALUES
('考核项目查询', @item_menu_id, 1, 'F', '0', '', 'kpi:item:list', '#', 'admin', NOW(), '', NULL, ''),
('考核项目新增', @item_menu_id, 2, 'F', '0', '', 'kpi:item:add', '#', 'admin', NOW(), '', NULL, ''),
('考核项目修改', @item_menu_id, 3, 'F', '0', '', 'kpi:item:edit', '#', 'admin', NOW(), '', NULL, ''),
('考核项目删除', @item_menu_id, 4, 'F', '0', '', 'kpi:item:remove', '#', 'admin', NOW(), '', NULL, '');

-- 3. 考核分数
INSERT INTO sys_menu (menu_name, parent_id, order_num, menu_type, visible, url, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('考核分数', @kpi_menu_id, 2, 'C', '0', 'kpi/score', 'kpi:score:view', '#', 'admin', NOW(), '', NULL, '考核分数查看');
SET @score_menu_id = LAST_INSERT_ID();

INSERT INTO sys_menu (menu_name, parent_id, order_num, menu_type, visible, url, perms, icon, create_by, create_time, update_by, update_time, remark) VALUES
('考核分数查询', @score_menu_id, 1, 'F', '0', '', 'kpi:score:list', '#', 'admin', NOW(), '', NULL, ''),
('考核分数新增', @score_menu_id, 2, 'F', '0', '', 'kpi:score:add', '#', 'admin', NOW(), '', NULL, ''),
('考核分数修改', @score_menu_id, 3, 'F', '0', '', 'kpi:score:edit', '#', 'admin', NOW(), '', NULL, ''),
('考核分数删除', @score_menu_id, 4, 'F', '0', '', 'kpi:score:remove', '#', 'admin', NOW(), '', NULL, '');

-- 4. 考核打分
INSERT INTO sys_menu (menu_name, parent_id, order_num, menu_type, visible, url, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('考核打分', @kpi_menu_id, 3, 'C', '0', 'kpi/score/input', 'kpi:score:input', '#', 'admin', NOW(), '', NULL, '考核打分操作');

-- 5. 考核结果
INSERT INTO sys_menu (menu_name, parent_id, order_num, menu_type, visible, url, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('考核结果', @kpi_menu_id, 4, 'C', '0', 'kpi/score/summary', 'kpi:score:summary', '#', 'admin', NOW(), '', NULL, '考核结果汇总');