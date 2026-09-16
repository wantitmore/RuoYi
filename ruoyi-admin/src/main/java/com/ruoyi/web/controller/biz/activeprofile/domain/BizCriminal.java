/**
 * 
 * 
 * @author Zack
 * @date Sep 15, 2026
 */
package com.ruoyi.web.controller.biz.activeprofile.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

public class BizCriminal extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    @Excel(name = "囚号")
    private String bh;
    @Excel(name = "姓名")
    private String xm;
    private String dataJson;
    private String rawJson;
    private Long ownerId;
    @Excel(name = "归属人")
    private String ownerName;
    private Long deptId;
    private String dataSource;
    private String manualFields;
    /** 非数据库字段 */
    private String deptName;
    /** 查询过滤类型：my / public / all（非数据库字段） */
    private String filterType;

    /** 只查公共档案（owner_id IS NULL）（非数据库字段） */
    private Boolean ownerIdNull;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBh() {
        return bh;
    }

    public void setBh(String bh) {
        this.bh = bh;
    }

    public String getXm() {
        return xm;
    }

    public void setXm(String xm) {
        this.xm = xm;
    }

    public String getDataJson() {
        return dataJson;
    }

    public void setDataJson(String dataJson) {
        this.dataJson = dataJson;
    }

    public String getRawJson() {
        return rawJson;
    }

    public void setRawJson(String rawJson) {
        this.rawJson = rawJson;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getManualFields() {
        return manualFields;
    }

    public void setManualFields(String manualFields) {
        this.manualFields = manualFields;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

    public Boolean getOwnerIdNull() {
        return ownerIdNull;
    }

    public void setOwnerIdNull(Boolean ownerIdNull) {
        this.ownerIdNull = ownerIdNull;
    }
}
