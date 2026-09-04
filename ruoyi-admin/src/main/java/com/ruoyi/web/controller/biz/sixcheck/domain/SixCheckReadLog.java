/**
 * 
 * 
 * @author Zack
 * @date Sep 04, 2026
 */
package com.ruoyi.web.controller.biz.sixcheck.domain;


import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 六必查阅读记录对象 six_check_read_log
 * 
 * @author ruoyi
 * @date 2026-09-04
 */
public class SixCheckReadLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 部门ID（监区） */
    @Excel(name = "部门ID")
    private Long deptId;

    /** 值班日期 */
    @Excel(name = "值班日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date checkDate;

    /** 阅读人ID */
    @Excel(name = "阅读人ID")
    private Long userId;

    /** 阅读时间 */
    @Excel(name = "阅读时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date readTime;

    /** 创建时间 */
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    // ========== 非数据库字段（关联查询用） ==========
    /** 阅读人姓名 */
    private String readerName;

    // ========== getter/setter ==========

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Date getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(Date checkDate) {
        this.checkDate = checkDate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getReadTime() {
        return readTime;
    }

    public void setReadTime(Date readTime) {
        this.readTime = readTime;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getReaderName() {
        return readerName;
    }

    public void setReaderName(String readerName) {
        this.readerName = readerName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("deptId", getDeptId())
                .append("checkDate", getCheckDate())
                .append("userId", getUserId())
                .append("readTime", getReadTime())
                .append("createTime", getCreateTime())
                .append("readerName", getReaderName())
                .toString();
    }
}