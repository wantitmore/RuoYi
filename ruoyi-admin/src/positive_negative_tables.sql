-- 正负面清单表
CREATE TABLE positive_negative (
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