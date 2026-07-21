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