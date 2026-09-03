/**
 * 
 * 
 * @author Zack
 * @date Aug 31, 2026
 */
package com.ruoyi.web.controller.biz.weekcheck.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

public class WeekCheck extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 被考核用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 检查周（格式：2025-W12） */
    @Excel(name = "检查周")
    private String week;

    /** 部门ID */
    @Excel(name = "部门ID")
    private Long deptId;

    /** 四知道（好/较好/一般/差） */
    @Excel(name = "四知道")
    private String fourKnow;

    /** 无册（好/较好/一般/差） */
    @Excel(name = "无册")
    private String noBook;

    /** 重点人物掌握（好/较好/一般/差） */
    @Excel(name = "重点罪犯掌握")
    private String keyPerson;

    /** 两个职责熟悉情况（好/较好/一般/差） */
    @Excel(name = "两个职责熟悉情况")
    private String dutyFamiliar;

    /** 应知应会掌握情况（好/较好/一般/差） */
    @Excel(name = "应知应会掌握情况")
    private String knowledgeMastery;

    /** 其他（好/较好/一般/差） */
    @Excel(name = "其他")
    private String other;

    // ----- getter / setter -----
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

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getFourKnow() {
        return fourKnow;
    }

    public void setFourKnow(String fourKnow) {
        this.fourKnow = fourKnow;
    }

    public String getNoBook() {
        return noBook;
    }

    public void setNoBook(String noBook) {
        this.noBook = noBook;
    }

    public String getKeyPerson() {
        return keyPerson;
    }

    public void setKeyPerson(String keyPerson) {
        this.keyPerson = keyPerson;
    }

    public String getDutyFamiliar() {
        return dutyFamiliar;
    }

    public void setDutyFamiliar(String dutyFamiliar) {
        this.dutyFamiliar = dutyFamiliar;
    }


    public String getKnowledgeMastery() {
        return knowledgeMastery;
    }

    public void setKnowledgeMastery(String knowledgeMastery) {
        this.knowledgeMastery = knowledgeMastery;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    @Override
    public String toString() {
        return "WeekCheck{" +
                "id=" + id +
                ", userId=" + userId +
                ", week='" + week + '\'' +
                ", deptId=" + deptId +
                ", fourKnow='" + fourKnow + '\'' +
                ", noBook='" + noBook + '\'' +
                ", keyPerson='" + keyPerson + '\'' +
                ", dutyFamiliar='" + dutyFamiliar + '\'' +
                ", other='" + other + '\'' +
                '}';
    }
}
