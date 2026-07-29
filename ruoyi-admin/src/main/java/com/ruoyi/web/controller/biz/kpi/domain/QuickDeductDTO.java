package com.ruoyi.web.controller.biz.kpi.domain;

import java.math.BigDecimal;

public class QuickDeductDTO {
    private Long userId;
    private Long itemId;
    private BigDecimal score;
    private String remark;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    @Override
    public String toString() {
        return "QuickDeductDTO [userId=" + userId + ", itemId=" + itemId + ", score=" + score + ", remark=" + remark
                + ", getUserId()=" + getUserId() + ", getItemId()=" + getItemId() + ", getScore()=" + getScore()
                + ", getRemark()=" + getRemark() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
                + ", toString()=" + super.toString() + "]";
    }
    
}
