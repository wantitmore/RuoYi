package com.ruoyi.web.controller.biz.kpi.domain;

import java.math.BigDecimal;

public class KpiScoreDetailVo {
    private String batchNo;
    private String userName;
    private String itemName;
    private BigDecimal score;
    private String remark;
    // getter/setter
    public String getBatchNo() {
        return batchNo;
    }
    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
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
    public BigDecimal getScore() {
        return score;
    }
    public void setScore(BigDecimal score) {
        this.score = score;
    }
    public String getRemark() {
        return remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
    @Override
    public String toString() {
        return "KpiScoreDetailVo [batchNo=" + batchNo + ", userName=" + userName + ", itemName=" + itemName + ", score="
                + score + ", remark=" + remark + "]";
    }

    
}