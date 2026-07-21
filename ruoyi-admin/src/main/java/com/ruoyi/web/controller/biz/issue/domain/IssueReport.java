package com.ruoyi.web.controller.biz.issue.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 问题通报对象 issue_report
 * 
 * @author ruoyi
 * @date 2026-07-08
 */
public class IssueReport extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 业务线条（政工/狱政/教育/生产） */
    @Excel(name = "业务线条", readConverterExp = "政=工/狱政/教育/生产")
    private String businessLine;

    /** 时间 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Excel(name = "时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date issueDate;

    /** 问题 */
    @Excel(name = "问题")
    private String problem;

    /** 对策 */
    @Excel(name = "对策")
    private String solution;

    /** 来源（警务督察/三人小组/值班组/指挥中心） */
    @Excel(name = "来源", readConverterExp = "警=务督察/三人小组/值班组/指挥中心")
    private String source;

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

    public void setBusinessLine(String businessLine) {
        this.businessLine = businessLine;
    }

    public String getBusinessLine() {
        return businessLine;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getProblem() {
        return problem;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public String getSolution() {
        return solution;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
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
                .append("businessLine", getBusinessLine())
                .append("issueDate", getIssueDate())
                .append("problem", getProblem())
                .append("solution", getSolution())
                .append("source", getSource())
                .append("remark", getRemark())
                .append("batchNo", getBatchNo())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
