-- 清理旧数据
DELETE rm FROM sys_role_menu rm
JOIN sys_menu m ON rm.menu_id = m.menu_id
WHERE m.menu_name LIKE '%问题通报%';

DELETE FROM sys_menu WHERE menu_name LIKE '%问题通报%';

-- 一级菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, menu_type, visible, url, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('问题通报', 0, 5, 'M', '0', '', 'issue:view', 'fa fa-exclamation-circle', 'admin', NOW(), '', NULL, '问题通报模块');
SET @parent_id = LAST_INSERT_ID();

-- 二级菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, menu_type, visible, url, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('问题通报管理', @parent_id, 1, 'C', '0', 'issue/list', 'issue:list:view', '#', 'admin', NOW(), '', NULL, '问题通报列表');
SET @list_menu_id = LAST_INSERT_ID();

-- 按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, menu_type, visible, url, perms, icon, create_by, create_time, update_by, update_time, remark) VALUES
('问题通报查询', @list_menu_id, 1, 'F', '0', '', 'issue:list', '#', 'admin', NOW(), '', NULL, ''),
('问题通报新增', @list_menu_id, 2, 'F', '0', '', 'issue:add', '#', 'admin', NOW(), '', NULL, ''),
('问题通报修改', @list_menu_id, 3, 'F', '0', '', 'issue:edit', '#', 'admin', NOW(), '', NULL, ''),
('问题通报删除', @list_menu_id, 4, 'F', '0', '', 'issue:remove', '#', 'admin', NOW(), '', NULL, '');

-- 授权给超级管理员
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, menu_id FROM sys_menu 
WHERE menu_name LIKE '%问题通报%'
AND NOT EXISTS (
    SELECT 1 FROM sys_role_menu rm
    WHERE rm.role_id = 1 AND rm.menu_id = sys_menu.menu_id
);