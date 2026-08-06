package com.ruoyi.web.controller.biz.kpi.domain;

import java.math.BigDecimal;

public class QuickDeductDTO {
    private Long userId;
    private Long itemId;
    private BigDecimal score;
    private String remark;
    private Long sixCheckItemId; // 检查项ID（对应 six_check_item 的 item_id）
    private String checkDate; // 值班日期（yyyy-MM-dd）
    private String shift; // 班次
    private String currentRecordValue;
    private Long videoItemId;      // video_check_item 的 ID
    private String videoDate;      // 值班日期（yyyy-MM-dd）

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
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

    public Long getSixCheckItemId() {
        return sixCheckItemId;
    }

    public void setSixCheckItemId(Long sixCheckItemId) {
        this.sixCheckItemId = sixCheckItemId;
    }

    public String getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(String checkDate) {
        this.checkDate = checkDate;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public String getCurrentRecordValue() {
        return currentRecordValue;
    }

    public void setCurrentRecordValue(String currentRecordValue) {
        this.currentRecordValue = currentRecordValue;
    }

    public Long getVideoItemId() {
        return videoItemId;
    }

    public void setVideoItemId(Long videoItemId) {
        this.videoItemId = videoItemId;
    }


    public String getVideoDate() {
        return videoDate;
    }

    public void setVideoDate(String videoDate) {
        this.videoDate = videoDate;
    }

    @Override
    public String toString() {
        return "QuickDeductDTO [userId=" + userId + ", itemId=" + itemId + ", score=" + score + ", remark=" + remark
                + ", getUserId()=" + getUserId() + ", getItemId()=" + getItemId() + ", getScore()=" + getScore()
                + ", getRemark()=" + getRemark() + ", getVideoItemId()=" + getVideoItemId() + ", getVideoDate()=" + getVideoDate()
                + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
                + ", toString()=" + super.toString() + "]";
    }

}
