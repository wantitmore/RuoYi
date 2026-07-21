-- 1. 清空已有数据
TRUNCATE TABLE kpi_score;
TRUNCATE TABLE kpi_item;

-- 2. 为每个部门插入默认项目（部门由 sys_dept 表决定）
INSERT INTO kpi_item (name, max_score, score_type, dept_id, category, remark, create_by, create_time)
SELECT proj.name, proj.max_score, proj.score_type, d.dept_id, proj.category, proj.remark, 'admin', NOW()
FROM sys_dept d
CROSS JOIN (
    SELECT '违反警容风纪或礼节礼貌规定' AS name, 20 AS max_score, 'NUMBER' AS score_type, '纪律作风' AS category, '视情节予以扣1-3分' AS remark
    UNION ALL SELECT '违反学习、培训或会议纪律', 20, 'NUMBER', '纪律作风', ''
    UNION ALL SELECT '不熟悉岗位职责、应知应会内容，掌握四知道、无册点名情况未达要求', 20, 'NUMBER', '纪律作风', '视情节予以扣1-3分'
    UNION ALL SELECT '违反考勤、备勤规定', 20, 'NUMBER', '纪律作风', '视情节予以扣1-3分'
    UNION ALL SELECT '违反警务用品管理规定', 20, 'NUMBER', '纪律作风', '视情节予以扣1-3分'
    UNION ALL SELECT '其他违反纪律作风管理规定', 20, 'NUMBER', '纪律作风', '视情节予以扣1-3分'
    UNION ALL SELECT '未按规定落实点名或清点人数', 20, 'NUMBER', '狱政', '视情节予以扣1-3分'
    UNION ALL SELECT '未按规定落实狱情排查、监管安全隐患排查及闭环处置', 20, 'NUMBER', '狱政', '视情节予以扣1-3分'
    UNION ALL SELECT '未按规定落实清仓、搜身或对违规违禁品、危化品未按要求处置', 20, 'NUMBER', '狱政', '视情节予以扣1-3分'
    UNION ALL SELECT '未按规定落实罪犯会见、顾送物品、寄收信件、亲情电话管理', 20, 'NUMBER', '狱政', '视情节予以扣1-3分'
    UNION ALL SELECT '未按规定落实罪犯互监组管理', 20, 'NUMBER', '狱政', '视情节予以扣1-3分'
    UNION ALL SELECT '未按规定处置罪犯违纪行为', 20, 'NUMBER', '狱政', '视情节予以扣1-3分'
    UNION ALL SELECT '未按规定落实罪犯内务卫生管理', 20, 'NUMBER', '狱政', '视情节予以扣1-3分'
    UNION ALL SELECT '未按规定落实罪犯耳目、信息员管理', 20, 'NUMBER', '狱政', '视情节予以扣1-3分'
    UNION ALL SELECT '未按规定开展罪犯计分考核', 20, 'NUMBER', '狱政', '视情节予以扣1-3分'
    UNION ALL SELECT '未按规定落实警察直接管理', 20, 'NUMBER', '狱政', '视情节予以扣1-3分'
    UNION ALL SELECT '未按时完成计分考核提请', 20, 'NUMBER', '狱政', '视情节予以扣1-3分'
    UNION ALL SELECT '狱情阅读量未达标（60条/月）', 20, 'NUMBER', '狱政', '视情节予以扣1-3分'
    UNION ALL SELECT '防自杀专项活动落实情况', 20, 'NUMBER', '狱政', '视情节予以扣1-3分'
    UNION ALL SELECT '其他违反狱政工作管理规定', 20, 'NUMBER', '狱政', '视情节予以扣1-3分'
    UNION ALL SELECT '未按规定建立、更新专管罪犯动态档案', 20, 'NUMBER', '教育', '视情节予以扣1-3分'
    UNION ALL SELECT '未按规定开展个别教育谈话', 20, 'NUMBER', '教育', '视情节予以扣1-3分'
    UNION ALL SELECT '未按规定开展危险性评估', 20, 'NUMBER', '教育', '视情节予以扣1-3分'
    UNION ALL SELECT '未及时调整顽危犯、重点犯防控措施', 20, 'NUMBER', '教育', '视情节予以扣1-3分'
    UNION ALL SELECT '未落实队前讲评工作要求', 20, 'NUMBER', '教育', '视情节予以扣1-3分'
    UNION ALL SELECT '未落实"减假暂"案件实质化审理', 20, 'NUMBER', '教育', '视情节予以扣1-3分'
    UNION ALL SELECT '未落实重点罪犯教育转化及专项档案登记更新', 20, 'NUMBER', '教育', '视情节予以扣1-3分'
    UNION ALL SELECT '未及时接访、化解、处置罪犯矛盾诉求', 20, 'NUMBER', '教育', '视情节予以扣1-3分'
    UNION ALL SELECT '重要节点出现专管罪犯违规违纪', 20, 'NUMBER', '教育', '视情节予以扣1-3分'
    UNION ALL SELECT '调出监监区动态档案移交情况', 20, 'NUMBER', '教育', '视情节予以扣1-3分'
    UNION ALL SELECT '其他违反教育工作管理规定', 20, 'NUMBER', '教育', '视情节予以扣1-3分'
    UNION ALL SELECT '动态档案实时更新进行考核评价', 20, 'NUMBER', '动态档案评价', '视情节予以扣1-3分'
    UNION ALL SELECT '未按要求落实外协人员陪同管理', 20, 'NUMBER', '生产', '视情节予以扣1-3分'
    UNION ALL SELECT '未按规定落实劳动工具、危化品管理要求', 20, 'NUMBER', '生产', '视情节予以扣1-3分'
    UNION ALL SELECT '未按规定落实工厂正门及安全门上开锁要求', 20, 'NUMBER', '生产', '视情节予以扣1-3分'
    UNION ALL SELECT '未按规定落实收工断电安全检查', 20, 'NUMBER', '生产', '视情节予以扣1-3分'
    UNION ALL SELECT '专管罪犯未完成月劳动定额任务', 20, 'NUMBER', '生产', '视情节予以扣1-3分'
    UNION ALL SELECT '未落实安全生产教育培训', 20, 'NUMBER', '生产', '视情节予以扣1-3分'
    UNION ALL SELECT '其他违反生产工作管理规定', 20, 'NUMBER', '生产', '视情节予以扣1-3分'
    UNION ALL SELECT '未按规定履行岗位职责', 20, 'NUMBER', '执勤现场管理', '视情节予以扣1-3分'
    UNION ALL SELECT '执勤期间做与工作无关的事情', 20, 'NUMBER', '执勤现场管理', '视情节予以扣1-3分'
    UNION ALL SELECT '执勤期间被通报且属于现场管理问题', 20, 'NUMBER', '执勤现场管理', '视情节予以扣1-3分'
    UNION ALL SELECT '未及时转递交接系统及异常狱情', 20, 'NUMBER', '执勤现场管理', '视情节予以扣1-3分'
    UNION ALL SELECT '其他违反执勤现场管理工作规定', 20, 'NUMBER', '执勤现场管理', '视情节予以扣1-3分'
    UNION ALL SELECT '值班领导巡查情况', 20, 'NUMBER', '正负面清单', '视情节予以加扣分'
    UNION ALL SELECT '各类检查主体检查通报', 20, 'NUMBER', '正负面清单', '视情节予以加扣分'
    UNION ALL SELECT '监区对三大现场视频回放民警履职情况', 20, 'NUMBER', '正负面清单', '视情节予以加扣分'
) proj;