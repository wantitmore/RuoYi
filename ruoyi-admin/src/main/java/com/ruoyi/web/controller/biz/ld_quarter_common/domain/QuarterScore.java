package com.ruoyi.web.controller.biz.ld_quarter_common.domain;

import java.util.Date;

public class QuarterScore {
    private Long id;
    private Long userId;
    private Long factorId;
    private String grade;       // 好/较好/差
    private Integer score;      // 95/85/60
    private String batchNo;     // 2025-Q1
    private Long deptId;
    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;

    // 列表展示用
    private String userName;
    private String factorName;
    private String deptName;
    private String postName;    // 岗位名称
    private Double avgScore;    // 平均分
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public Long getFactorId() {
        return factorId;
    }
    public void setFactorId(Long factorId) {
        this.factorId = factorId;
    }
    public String getGrade() {
        return grade;
    }
    public void setGrade(String grade) {
        this.grade = grade;
    }
    public Integer getScore() {
        return score;
    }
    public void setScore(Integer score) {
        this.score = score;
    }
    public String getBatchNo() {
        return batchNo;
    }
    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }
    public Long getDeptId() {
        return deptId;
    }
    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }
    public String getCreateBy() {
        return createBy;
    }
    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }
    public Date getCreateTime() {
        return createTime;
    }
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    public String getUpdateBy() {
        return updateBy;
    }
    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }
    public Date getUpdateTime() {
        return updateTime;
    }
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getFactorName() {
        return factorName;
    }
    public void setFactorName(String factorName) {
        this.factorName = factorName;
    }
    public String getDeptName() {
        return deptName;
    }
    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }
    public String getPostName() {
        return postName;
    }
    public void setPostName(String postName) {
        this.postName = postName;
    }
    public Double getAvgScore() {
        return avgScore;
    }
    public void setAvgScore(Double avgScore) {
        this.avgScore = avgScore;
    }

    

    // getter/setter...
    
}
