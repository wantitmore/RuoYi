-- ----------------------------
-- 22、考核项目表
-- ----------------------------
drop table if exists kpi_item;
create table kpi_item (
  id          bigint(20)   not null auto_increment comment 'ID',
  name        varchar(100) not null comment '考核项目名称',
  max_score   decimal(5,1) not null default 100.0 comment '满分',
  score_type  varchar(20)  default 'NUMBER' comment '评分类型（NUMBER/STAR/TEXT等）',
  category    varchar(20)  not null DEFAULT '其他' comment '考核类别（纪律作风/狱政/生产/教育/执勤现场管理/正负面清单/动态档案评价）',
  remark      varchar(500) default null comment '备注',
  create_by   varchar(64)  default '' comment '创建者',
  create_time datetime     comment '创建时间',
  update_by   varchar(64)  default '' comment '更新者',
  update_time datetime     comment '更新时间',
  dept_id     bigint(20)   not null  comment '所属部门ID（关联sys_dept）',           
  primary key (id)
) engine=innodb auto_increment=1 comment = '考核项目表';

-- ----------------------------
-- 23、考核分数表
-- ----------------------------
drop table if exists kpi_score;
create table kpi_score (
  id          bigint(20)  not null auto_increment comment 'ID',
  user_id     bigint(20)  not null comment '被考核人ID（关联sys_user）',
  item_id     bigint(20)  not null comment '考核项目ID（关联kpi_item）',
  score       decimal(5,1) comment '得分',
  text_content varchar(500) comment '评语/文字评价',
  batch_no    varchar(50) default '' comment '考核批次（如2024-Q4，可用于区分不同期次）',
  remark      varchar(500) DEFAULT NULL COMMENT '备注',
  create_by   varchar(64) default '' comment '创建者（考核人）',
  create_time datetime    comment '创建时间',
  update_by   varchar(64) default '' comment '更新者',
  update_time datetime    comment '更新时间',
  primary key (id)
) engine=innodb auto_increment=1 comment = '考核分数表';
