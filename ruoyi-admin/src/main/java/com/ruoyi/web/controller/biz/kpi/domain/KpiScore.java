package com.ruoyi.web.controller.biz.kpi.domain;

import java.beans.Transient;
import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 考核分数对象 kpi_score
 * 
 * @author ruoyi
 * @date 2026-06-07
 */
public class KpiScore extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 被考核人ID（关联sys_user） */
    @Excel(name = "被考核人ID", readConverterExp = "关=联sys_user")
    private Long userId;

    /** 考核项目ID（关联kpi_item） */
    @Excel(name = "考核项目ID", readConverterExp = "关=联kpi_item")
    private Long itemId;

    /** 得分 */
    @Excel(name = "得分")
    private BigDecimal score;

    /** 评语/文字评价 */
    @Excel(name = "评语/文字评价")
    private String textContent;

    /** 考核批次（如2024-Q4，可用于区分不同期次） */
    @Excel(name = "考核批次", readConverterExp = "如=2024-Q4，可用于区分不同期次")
    private String batchNo;


    private Long sourceRecordId;

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public Long getSourceRecordId() {
        return sourceRecordId;
    }

    public void setSourceRecordId(Long sourceRecordId) {
        this.sourceRecordId = sourceRecordId;
    }



    /**
     *
     */
    private String userName;

    private String itemName;

    private String category;

    private String remark;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
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

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public String getTextContent() {
        return textContent;
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
                .append("itemId", getItemId())
                .append("score", getScore())
                .append("textContent", getTextContent())
                .append("batchNo", getBatchNo())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("sourceRecordId", getSourceRecordId())
                .toString();
    }

}
