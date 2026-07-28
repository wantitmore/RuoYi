package com.ruoyi.web.controller.biz.sixcheck.domain;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class SixCheckRecordWrapper {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date checkDate;
    private String shift;
    private String dutyLeader;
    private List<SixCheckRecord> recordList;

    // getter/setter
    public Date getCheckDate() { return checkDate; }
    public void setCheckDate(Date checkDate) { this.checkDate = checkDate; }
    public String getShift() { return shift; }
    public void setShift(String shift) { this.shift = shift; }
    public String getDutyLeader() { return dutyLeader; }
    public void setDutyLeader(String dutyLeader) { this.dutyLeader = dutyLeader; }
    public List<SixCheckRecord> getRecordList() { return recordList; }
    public void setRecordList(List<SixCheckRecord> recordList) { this.recordList = recordList; }
}