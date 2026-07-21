package com.ruoyi.web.controller.biz.sixcheck.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 六必查记录对象 six_check_record
 * 
 * @author ruoyi
 * @date 2026-07-01
 */
public class SixCheckRecord extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 检查项目ID */
    @Excel(name = "检查项目ID")
    private Long itemId;

    /** 记录内容 */
    @Excel(name = "记录内容")
    private String recordValue;

    /** 月份（YYYY-MM） */
    @Excel(name = "月份", readConverterExp = "Y=YYY-MM")
    private String batchNo;

    private Long deptId;

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setRecordValue(String recordValue) {
        this.recordValue = recordValue;
    }

    public String getRecordValue() {
        return recordValue;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getBatchNo() {
        return batchNo;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("itemId", getItemId())
                .append("recordValue", getRecordValue())
                .append("batchNo", getBatchNo())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
