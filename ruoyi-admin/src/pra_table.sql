CREATE TABLE pra_log (
    id          bigint(20)   NOT NULL AUTO_INCREMENT COMMENT 'ID',
    duty_date   date         NOT NULL COMMENT '执勤日期',
    detail      text         COMMENT '执勤详情',
    police      varchar(50)  NOT NULL COMMENT '执勤/专管警察',
    batch_no    varchar(7)   NOT NULL COMMENT '月份（YYYY-MM）',
    create_by   varchar(64)  DEFAULT '' COMMENT '创建者',
    create_time datetime     COMMENT '创建时间',
    update_by   varchar(64)  DEFAULT '' COMMENT '更新者',
    update_time datetime     COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='执勤表扬台账表';