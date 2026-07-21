-- 插入缺失的按钮（如果已存在会自动跳过）
INSERT INTO sys_menu (menu_name, parent_id, order_num, menu_type, visible, url, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT '执勤表扬台账查询', menu_id, 1, 'F', '0', '', 'pralog:list', '#', 'admin', NOW(), '', NULL, ''
FROM sys_menu
WHERE menu_name = '执勤表扬台账管理' AND menu_type = 'C'
AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_name = '执勤表扬台账查询');

INSERT INTO sys_menu (menu_name, parent_id, order_num, menu_type, visible, url, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT '执勤表扬台账新增', menu_id, 2, 'F', '0', '', 'pralog:add', '#', 'admin', NOW(), '', NULL, ''
FROM sys_menu
WHERE menu_name = '执勤表扬台账管理' AND menu_type = 'C'
AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_name = '执勤表扬台账新增');

INSERT INTO sys_menu (menu_name, parent_id, order_num, menu_type, visible, url, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT '执勤表扬台账修改', menu_id, 3, 'F', '0', '', 'pralog:edit', '#', 'admin', NOW(), '', NULL, ''
FROM sys_menu
WHERE menu_name = '执勤表扬台账管理' AND menu_type = 'C'
AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_name = '执勤表扬台账修改');

INSERT INTO sys_menu (menu_name, parent_id, order_num, menu_type, visible, url, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT '执勤表扬台账删除', menu_id, 4, 'F', '0', '', 'pralog:remove', '#', 'admin', NOW(), '', NULL, ''
FROM sys_menu
WHERE menu_name = '执勤表扬台账管理' AND menu_type = 'C'
AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_name = '执勤表扬台账删除');

INSERT INTO sys_menu (menu_name, parent_id, order_num, menu_type, visible, url, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT '执勤表扬台账导出', menu_id, 5, 'F', '0', '', 'pralog:export', '#', 'admin', NOW(), '', NULL, ''
FROM sys_menu
WHERE menu_name = '执勤表扬台账管理' AND menu_type = 'C'
AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_name = '执勤表扬台账导出');

-- 授权给超级管理员
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, menu_id FROM sys_menu WHERE menu_name LIKE '%执勤表扬台账%' AND menu_type = 'F'
AND NOT EXISTS (
    SELECT 1 FROM sys_role_menu rm WHERE rm.role_id = 1 AND rm.menu_id = sys_menu.menu_id
);