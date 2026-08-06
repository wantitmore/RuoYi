package com.ruoyi.web.controller.biz.ld_quarter_common.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class AssessPostConfig extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;

    @Excel(name = "考核类型")
    private String type;

    @Excel(name = "岗位编码")
    private String postCode;

    @Excel(name = "排序号")
    private Integer sortOrder;

    // getter / setter 略（自动生成）

    public Long getId() {
        return id;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
    
}
