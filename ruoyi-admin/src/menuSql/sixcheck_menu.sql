-- 清理旧数据
DELETE rm FROM sys_role_menu rm
JOIN sys_menu m ON rm.menu_id = m.menu_id
WHERE m.menu_name LIKE '%六必查%';

DELETE FROM sys_menu WHERE menu_name LIKE '%六必查%';

-- 插入一级菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, menu_type, visible, url, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('六必查', 0, 2, 'M', '0', '', 'sixcheck:view', 'fa fa-check-square', 'admin', NOW(), '', NULL, '六必查模块');
SET @six_menu_id = LAST_INSERT_ID();

-- 插入子菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, menu_type, visible, url, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('六必查录入', @six_menu_id, 1, 'C', '0', 'sixcheck/record/input', 'sixcheck:record:input', '#', 'admin', NOW(), '', NULL, '六必查录入');

-- 插入编辑按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, menu_type, visible, url, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT '六必查编辑', menu_id, 1, 'F', '0', '', 'sixcheck:record:edit', '#', 'admin', NOW(), '', NULL, ''
FROM sys_menu
WHERE menu_name = '六必查录入' AND menu_type = 'C';



-- 授权给超级管理员（含按钮）
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, menu_id FROM sys_menu 
WHERE menu_name IN ('六必查', '六必查录入', '六必查编辑')
AND NOT EXISTS (
    SELECT 1 FROM sys_role_menu rm
    WHERE rm.role_id = 1 AND rm.menu_id = sys_menu.menu_id
);