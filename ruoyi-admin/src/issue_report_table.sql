CREATE TABLE issue_report (
    id            bigint(20)   NOT NULL AUTO_INCREMENT COMMENT 'ID',
    business_line varchar(20)  NOT NULL COMMENT '业务线条（政工/狱政/教育/生产）',
    issue_date    date         NOT NULL COMMENT '时间',
    problem       text         COMMENT '问题',
    solution      text         COMMENT '对策',
    source        varchar(20)  NOT NULL COMMENT '来源（警务督察/三人小组/值班组/指挥中心）',
    remark        text         COMMENT '备注',
    batch_no      varchar(7)   NOT NULL COMMENT '月份（YYYY-MM）',
    create_by     varchar(64)  DEFAULT '' COMMENT '创建者',
    create_time   datetime     COMMENT '创建时间',
    update_by     varchar(64)  DEFAULT '' COMMENT '更新者',
    update_time   datetime     COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='问题通报表';