为所有已有部门插入默认检查项目（5条）
INSERT INTO video_check_item (dept_id, check_position, specific_content, sort_order, create_by, create_time)
SELECT d.dept_id, proj.check_position, proj.specific_content, proj.sort_order, 'admin', NOW()
FROM sys_dept d
CROSS JOIN (
    SELECT '监舍一楼储物间、多功能厅厕所洗碗间、工厂会议室' AS check_position, '倒查罪犯进出一楼储物间、多功能厅厕所洗碗间情况：是否落实警察管理、是否存在落单；工厂会议室是否有警察私自带罪犯进入' AS specific_content, 1 AS sort_order
    UNION ALL SELECT '分控室', '倒查警察夜间值班、中午值班、早上值班履职情况', 2
    UNION ALL SELECT '小院搜身现场、罪犯夜值班履职情况、罪犯服药现场', '倒查警察搜身情况、夜值班罪犯履职情况、服药管理情况', 3
    UNION ALL SELECT '图书室、心理矫治室、教育日各现场、早早班警察组织罪犯下楼时段', '倒查警察组织可视会见情况、非可视会见时段是否有警察进入心理矫治室做与工作无关事宜，教育现场组织情况，早餐组织罪犯下楼警察是否落实双岗、清场', 4
    UNION ALL SELECT '断电半小时巡查，工厂仓库、配电房、过道储物间、烤房、下午开工警察组织罪犯下楼时段、组织罪犯上厕所秩序', '断电半小时巡查落实情况；检查罪犯零星进出小房小室情况，下午开工组织罪犯下楼警察是否落实双岗、清场', 5
    UNION ALL SELECT '一分区警察一二号岗执勤岗、罪犯厕所、警察实点名时段、发放劳动工具时段', '倒查一分区警察一二号岗执勤岗警察履职情况、罪犯如厕组织是否有落单、警察是否落实实点名、是否落实警察直接发放', 6
    UNION ALL SELECT '二分区警察一二号岗执勤岗、罪犯厕所、警察实点名时段、发放劳动工具时段', '倒查二分区警察一二号岗执勤岗警察履职情况、犯如厕组织是否有落单、警察是否落实实点、是否落实警察直接发放', 7
    UNION ALL SELECT '二、三楼罪犯进出小房小室时段、拨打亲情电话时段、警察履职时段', '倒查楼层警察履职情况，组织进小房小室是否落清点人数、清场，拨打亲情电话是否落实监听', 8
    UNION ALL SELECT '三、四楼罪犯进出小房小室时段、拨打亲情电话时段、警察履职时段', '倒查楼层警察履职情况，组织进小房小室是否落清点人数、清场，拨打亲情电话是否落实监听', 9
) proj
WHERE d.del_flag = '0';

-- 3. 菜单 + 按钮权限 + 授权超级管理员
DELETE rm FROM sys_role_menu rm
JOIN sys_menu m ON rm.menu_id = m.menu_id
WHERE m.menu_name LIKE '%视频回放%';

DELETE FROM sys_menu WHERE menu_name LIKE '%视频回放%';

-- 一级菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, menu_type, visible, url, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('视频回放', 0, 6, 'M', '0', '', 'video:view', 'fa fa-video-camera', 'admin', NOW(), '', NULL, '视频回放模块');
SET @parent_id = LAST_INSERT_ID();

-- 二级菜单：视频回放管理（列表页）
INSERT INTO sys_menu (menu_name, parent_id, order_num, menu_type, visible, url, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('视频回放管理', @parent_id, 1, 'C', '0', 'video/record/list', 'video:list:view', '#', 'admin', NOW(), '', NULL, '视频回放列表');
SET @list_menu_id = LAST_INSERT_ID();

-- 二级菜单：视频回放录入（录入页面）
INSERT INTO sys_menu (menu_name, parent_id, order_num, menu_type, visible, url, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('视频回放录入', @parent_id, 2, 'C', '0', 'video/input', 'video:input:view', '#', 'admin', NOW(), '', NULL, '视频回放录入');

-- 按钮权限（列表页）
INSERT INTO sys_menu (menu_name, parent_id, order_num, menu_type, visible, url, perms, icon, create_by, create_time, update_by, update_time, remark) VALUES
('视频回放查询', @list_menu_id, 1, 'F', '0', '', 'video:list', '#', 'admin', NOW(), '', NULL, ''),
('视频回放新增', @list_menu_id, 2, 'F', '0', '', 'video:add', '#', 'admin', NOW(), '', NULL, ''),
('视频回放修改', @list_menu_id, 3, 'F', '0', '', 'video:edit', '#', 'admin', NOW(), '', NULL, ''),
('视频回放删除', @list_menu_id, 4, 'F', '0', '', 'video:remove', '#', 'admin', NOW(), '', NULL, '');

-- 按钮权限（录入页：控制是否可以编辑保存）
INSERT INTO sys_menu (menu_name, parent_id, order_num, menu_type, visible, url, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT '视频回放编辑', menu_id, 1, 'F', '0', '', 'video:record:edit', '#', 'admin', NOW(), '', NULL, ''
FROM sys_menu
WHERE menu_name = '视频回放录入' AND menu_type = 'C';

-- 授权给超级管理员
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, menu_id FROM sys_menu 
WHERE menu_name LIKE '%视频回放%'
AND NOT EXISTS (
    SELECT 1 FROM sys_role_menu rm
    WHERE rm.role_id = 1 AND rm.menu_id = sys_menu.menu_id
);