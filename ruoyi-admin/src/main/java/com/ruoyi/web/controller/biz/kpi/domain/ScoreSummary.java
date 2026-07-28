package com.ruoyi.web.controller.biz.kpi.domain;

import com.ruoyi.common.annotation.Excel;

public class ScoreSummary {

    @Excel(name = "排名")
    private Integer rank;

    @Excel(name = "被考核人")
    private String userName;

    @Excel(name = "部门")
    private String deptName;

    @Excel(name = "总分")
    private Double totalScore;

    @Excel(name = "考核项目数")
    private Integer itemCount;

    @Excel(name = "岗位")
    private String postName;

    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public Double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Double totalScore) {
        this.totalScore = totalScore;
    }

    public Integer getItemCount() {
        return itemCount;
    }

    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
    }
}