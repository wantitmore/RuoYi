package com.ruoyi.web.controller.biz.posneg.domain;

import java.beans.Transient;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 正负面清单对象 positive_negative
 * 
 * @author ruoyi
 * @date 2026-07-03
 */
public class PositiveNegative extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 人员ID */
    @Excel(name = "人员ID")
    private Long userId;

    /** 类别（正面/负面） */
    @Excel(name = "类别", readConverterExp = "正=面/负面")
    private String category;

    /** 情形描述 */
    @Excel(name = "情形描述")
    private String situation;

    /** 结果运用建议 */
    @Excel(name = "结果运用建议")
    private String suggestion;

    /** 次数 */
    @Excel(name = "次数")
    private Long count;

    /** 月份（YYYY-MM） */
    @Excel(name = "月份", readConverterExp = "Y=YYY-MM")
    private String batchNo;

    @Excel(name = "人员姓名")
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public String getSituation() {
        return situation;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getCount() {
        return count;
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
                .append("userId", getUserId())
                .append("category", getCategory())
                .append("situation", getSituation())
                .append("suggestion", getSuggestion())
                .append("count", getCount())
                .append("batchNo", getBatchNo())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("userName", getUserName())
                .toString();
    }
}
