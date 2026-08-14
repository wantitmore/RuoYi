/**
 * 
 * 
 * @author Zack
 * @date Aug 13, 2026
 */
package com.ruoyi.web.controller.biz.sixcheck.domain;

import java.math.BigDecimal;

import com.ruoyi.common.core.domain.BaseEntity;

public class SixCheckDeductDetail extends BaseEntity {
    private Long id;
    private Long kpiScoreId; // 关联的KPI记录ID
    private String deductInfo; // 扣分描述
    private Integer status;
    private Long sixCheckRecordId;
    private BigDecimal deductScore;

    public Long getSixCheckRecordId() {
        return sixCheckRecordId;
    }

    public void setSixCheckRecordId(Long sixCheckRecordId) {
        this.sixCheckRecordId = sixCheckRecordId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getKpiScoreId() {
        return kpiScoreId;
    }

    public void setKpiScoreId(Long kpiScoreId) {
        this.kpiScoreId = kpiScoreId;
    }

    public String getDeductInfo() {
        return deductInfo;
    }

    public void setDeductInfo(String deductInfo) {
        this.deductInfo = deductInfo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public BigDecimal getDeductScore() {
        return deductScore;
    }

    public void setDeductScore(BigDecimal deductScore) {
        this.deductScore = deductScore;
    }
    

}
