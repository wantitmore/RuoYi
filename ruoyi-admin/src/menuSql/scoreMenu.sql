-- 24、考核打分表
-- ----------------------------
drop table if exists kpi_input_score;
create table kpi_input_score (
  id          bigint(20)  not null auto_increment comment 'ID',
  user_id     bigint(20)  not null comment '被考核人ID（关联sys_user）',
  item_id     bigint(20)  not null comment '考核项目ID（关联kpi_item）',
  score       decimal(5,1) comment '得分',
  text_content varchar(500) comment '评语/文字评价',
  batch_no    varchar(50) default '' comment '考核批次（如2024-Q4，可用于区分不同期次）',
  create_by   varchar(64) default '' comment '创建者（考核人）',
  create_time datetime    comment '创建时间',
  update_by   varchar(64) default '' comment '更新者',
  update_time datetime    comment '更新时间',
  primary key (id)
) engine=innodb auto_increment=1 comment = '考核分数表';