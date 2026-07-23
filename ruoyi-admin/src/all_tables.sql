-- =====================================================
-- 全模块业务表初始化脚本（含部门隔离）
-- 适用于：绩效考核、六必查、正负面清单、执勤表扬台账、问题通报、视频回放
-- =====================================================

-- ========== 考核模块 ==========
CREATE TABLE IF NOT EXISTS kpi_item (
    id          bigint(20)   NOT NULL AUTO_INCREMENT COMMENT 'ID',
    name        varchar(100) NOT NULL COMMENT '考核项目名称',
    max_score   decimal(5,1) NOT NULL DEFAULT 100.0 COMMENT '满分',
    score_type  varchar(20)  DEFAULT 'NUMBER' COMMENT '评分类型',
    dept_id     bigint(20)   NOT NULL COMMENT '所属部门ID',
    category    varchar(20)  NOT NULL DEFAULT '其他' COMMENT '考核类别（狱政/政工/生产/教育/其他）',
    remark      varchar(500) DEFAULT NULL COMMENT '备注',
    create_by   varchar(64)  DEFAULT '' COMMENT '创建者',
    create_time datetime     COMMENT '创建时间',
    update_by   varchar(64)  DEFAULT '' COMMENT '更新者',
    update_time datetime     COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='考核项目表';

CREATE TABLE IF NOT EXISTS kpi_score (
    id          bigint(20)   NOT NULL AUTO_INCREMENT COMMENT 'ID',
    user_id     bigint(20)   NOT NULL COMMENT '被考核人ID',
    item_id     bigint(20)   NOT NULL COMMENT '考核项目ID',
    score       decimal(5,1) DEFAULT NULL COMMENT '得分',
    batch_no    varchar(50)  DEFAULT '' COMMENT '考核批次',
    remark      varchar(500) DEFAULT NULL COMMENT '备注',
    create_by   varchar(64)  DEFAULT '' COMMENT '创建者',
    create_time datetime     COMMENT '创建时间',
    update_by   varchar(64)  DEFAULT '' COMMENT '更新者',
    update_time datetime     COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='考核分数表';

-- ========== 六必查模块 ==========
-- 六必查项目表（可增删改）
CREATE TABLE six_check_item (
    id          bigint(20)   NOT NULL AUTO_INCREMENT COMMENT 'ID',
    name        varchar(100) NOT NULL COMMENT '检查项目名称',
    sort_order  int(4)       DEFAULT 0 COMMENT '排序号',
    create_by   varchar(64)  DEFAULT '' COMMENT '创建者',
    create_time datetime     COMMENT '创建时间',
    update_by   varchar(64)  DEFAULT '' COMMENT '更新者',
    update_time datetime     COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='六必查项目表';

-- 初始化十条默认项目
INSERT INTO six_check_item (name, sort_order) VALUES
('履职状态、到岗到位和物品定制', 1),
('劳动工具管理和外协人员管理', 2),
('搜身', 3),
('监管安全管理', 4),
('清点人数', 5),
('出收工队列和如厕管理', 6),
('互监组管理', 7),
('门禁门锁', 8),
('安全生产', 9),
('其他', 10);

-- 六必查记录表（按月份存储）
CREATE TABLE six_check_record (
    id          bigint(20)   NOT NULL AUTO_INCREMENT COMMENT 'ID',
    item_id     bigint(20)   NOT NULL COMMENT '检查项目ID',
    record_value text         COMMENT '记录内容',
    batch_no    varchar(7)   NOT NULL COMMENT '月份（YYYY-MM）',
    create_by   varchar(64)  DEFAULT '' COMMENT '创建者',
    create_time datetime     COMMENT '创建时间',
    update_by   varchar(64)  DEFAULT '' COMMENT '更新者',
    update_time datetime     COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_item_month (item_id, batch_no)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='六必查记录表';

-- ========== 部门隔离字段 ==========
ALTER TABLE six_check_item ADD COLUMN IF NOT EXISTS dept_id bigint(20) DEFAULT NULL COMMENT '所属部门ID';
ALTER TABLE six_check_record ADD COLUMN IF NOT EXISTS dept_id bigint(20) DEFAULT NULL COMMENT '所属部门ID';

-- 删除旧唯一约束（如果存在）
ALTER TABLE six_check_record DROP INDEX IF EXISTS uk_item_month;

-- 创建新唯一约束（包含部门）
ALTER TABLE six_check_record ADD UNIQUE KEY uk_item_month_dept (item_id, batch_no, dept_id);

-- ========== 正负面清单模块 ==========
CREATE TABLE IF NOT EXISTS positive_negative (
    id          bigint(20)   NOT NULL AUTO_INCREMENT COMMENT 'ID',
    user_id     bigint(20)   NOT NULL COMMENT '人员ID',
    category    varchar(10)  NOT NULL COMMENT '类别（正面/负面）',
    situation   text         COMMENT '情形描述',
    suggestion  text         COMMENT '结果运用建议',
    count       int(4)       DEFAULT 1 COMMENT '次数',
    batch_no    varchar(7)   NOT NULL COMMENT '月份（YYYY-MM）',
    create_by   varchar(64)  DEFAULT '' COMMENT '创建者',
    create_time datetime     COMMENT '创建时间',
    update_by   varchar(64)  DEFAULT '' COMMENT '更新者',
    update_time datetime     COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='正负面清单表';

-- ========== 执勤表扬台账模块 ==========
CREATE TABLE IF NOT EXISTS pra_log (
    id          bigint(20)   NOT NULL AUTO_INCREMENT COMMENT 'ID',
    duty_date   date         NOT NULL COMMENT '执勤日期',
    detail      text         COMMENT '执勤详情',
    police      varchar(50)  NOT NULL COMMENT '执勤/专管警察',
    batch_no    varchar(7)   NOT NULL COMMENT '月份（YYYY-MM）',
    dept_id     bigint(20)   DEFAULT NULL COMMENT '所属部门ID',
    create_by   varchar(64)  DEFAULT '' COMMENT '创建者',
    create_time datetime     COMMENT '创建时间',
    update_by   varchar(64)  DEFAULT '' COMMENT '更新者',
    update_time datetime     COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='执勤表扬台账表';

-- ========== 问题通报模块 ==========
CREATE TABLE IF NOT EXISTS issue_report (
    id            bigint(20)   NOT NULL AUTO_INCREMENT COMMENT 'ID',
    business_line varchar(20)  NOT NULL COMMENT '业务线条（政工/狱政/教育/生产）',
    issue_date    date         NOT NULL COMMENT '时间',
    problem       text         COMMENT '问题',
    solution      text         COMMENT '对策',
    source        varchar(20)  NOT NULL COMMENT '来源（警务督察/三人小组/值班组/指挥中心）',
    remark        text         COMMENT '备注',
    batch_no      varchar(7)   NOT NULL COMMENT '月份（YYYY-MM）',
    dept_id       bigint(20)   DEFAULT NULL COMMENT '所属部门ID',
    create_by     varchar(64)  DEFAULT '' COMMENT '创建者',
    create_time   datetime     COMMENT '创建时间',
    update_by     varchar(64)  DEFAULT '' COMMENT '更新者',
    update_time   datetime     COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='问题通报表';

-- ========== 视频回放模块 ==========
CREATE TABLE IF NOT EXISTS video_check_item (
    id                bigint(20)   NOT NULL AUTO_INCREMENT COMMENT 'ID',
    dept_id           bigint(20)   NOT NULL COMMENT '所属部门ID',
    check_position    varchar(200) NOT NULL COMMENT '倒查位置',
    specific_content  varchar(500) NOT NULL COMMENT '具体内容',
    sort_order        int(4)       DEFAULT 0 COMMENT '排序号',
    create_by         varchar(64)  DEFAULT '' COMMENT '创建者',
    create_time       datetime     COMMENT '创建时间',
    update_by         varchar(64)  DEFAULT '' COMMENT '更新者',
    update_time       datetime     COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='视频回放检查项目表';

CREATE TABLE IF NOT EXISTS video_playback_record (
    id              bigint(20)   NOT NULL AUTO_INCREMENT COMMENT 'ID',
    item_id         bigint(20)   NOT NULL COMMENT '检查项目ID',
    playback_status text         COMMENT '回放情况',
    batch_no        varchar(7)   NOT NULL COMMENT '月份（YYYY-MM）',
    dept_id         bigint(20)   NOT NULL COMMENT '所属部门ID',
    create_by       varchar(64)  DEFAULT '' COMMENT '创建者',
    create_time     datetime     COMMENT '创建时间',
    update_by       varchar(64)  DEFAULT '' COMMENT '更新者',
    update_time     datetime     COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_item_month_dept (item_id, batch_no, dept_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='视频回放记录表';

-- -- ========== 六必查初始数据（已存在的不会重复插入）==========
-- INSERT INTO six_check_item (dept_id, name, sort_order, create_by, create_time)
-- SELECT d.dept_id, proj.name, proj.sort_order, 'admin', NOW()
-- FROM sys_dept d
-- CROSS JOIN (
--     SELECT '队伍管理规范' AS name, 1 AS sort_order
--     UNION ALL SELECT '劳动工具管理和外协人员管理', 2
--     UNION ALL SELECT '安全隐患排查', 3
--     UNION ALL SELECT '文明执法情况', 4
--     UNION ALL SELECT '教育改造质量', 5
--     UNION ALL SELECT '生活卫生管理', 6
--     UNION ALL SELECT '劳动生产安全', 7
--     UNION ALL SELECT '应急处置能力', 8
--     UNION ALL SELECT '信息化应用', 9
--     UNION ALL SELECT '廉政建设情况', 10
-- ) proj
-- WHERE d.del_flag = '0'
--   AND NOT EXISTS (
--     SELECT 1 FROM six_check_item si
--     WHERE si.dept_id = d.dept_id AND si.name = proj.name
-- );