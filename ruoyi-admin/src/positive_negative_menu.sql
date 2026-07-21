-- =====================================================
-- 正负面清单模块菜单初始化脚本（权限标识已统一为长格式）
-- =====================================================

-- 清理旧数据
DELETE rm FROM sys_role_menu rm
JOIN sys_menu m ON rm.menu_id = m.menu_id
WHERE m.menu_name LIKE '%正负面清单%';

DELETE FROM sys_menu WHERE menu_name LIKE '%正负面清单%';

-- 插入一级菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, menu_type, visible, url, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('正负面清单', 0, 3, 'M', '0', '', 'posneg:view', 'fa fa-list-alt', 'admin', NOW(), '', NULL, '正负面清单模块');

SET @parent_id = LAST_INSERT_ID();

-- 插入子菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, menu_type, visible, url, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('正负面清单管理', @parent_id, 1, 'C', '0', 'posneg/pos_nega', 'posneg:pos_nega:view', '#', 'admin', NOW(), '', NULL, '正负面清单列表');

SET @list_menu_id = LAST_INSERT_ID();

-- 按钮权限（权限标识统一为 posneg:pos_nega:xxx）
INSERT INTO sys_menu (menu_name, parent_id, order_num, menu_type, visible, url, perms, icon, create_by, create_time, update_by, update_time, remark) VALUES
('正负面清单查询', @list_menu_id, 1, 'F', '0', '', 'posneg:pos_nega:list', '#', 'admin', NOW(), '', NULL, ''),
('正负面清单新增', @list_menu_id, 2, 'F', '0', '', 'posneg:pos_nega:add', '#', 'admin', NOW(), '', NULL, ''),
('正负面清单修改', @list_menu_id, 3, 'F', '0', '', 'posneg:pos_nega:edit', '#', 'admin', NOW(), '', NULL, ''),
('正负面清单删除', @list_menu_id, 4, 'F', '0', '', 'posneg:pos_nega:remove', '#', 'admin', NOW(), '', NULL, '');

-- 授权给超级管理员（角色ID=1）
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, menu_id FROM sys_menu 
WHERE menu_name LIKE '%正负面清单%'
AND NOT EXISTS (
    SELECT 1 FROM sys_role_menu rm
    WHERE rm.role_id = 1 AND rm.menu_id = sys_menu.menu_id
);