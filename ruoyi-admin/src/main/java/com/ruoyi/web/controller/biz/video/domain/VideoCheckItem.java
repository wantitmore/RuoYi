package com.ruoyi.web.controller.biz.video.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 视频回放检查项目对象 video_check_item
 * 
 * @author ruoyi
 * @date 2026-07-12
 */
public class VideoCheckItem extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 所属部门ID */
    @Excel(name = "所属部门ID")
    private Long deptId;

    /** 倒查位置 */
    @Excel(name = "倒查位置")
    private String checkPosition;

    /** 具体内容 */
    @Excel(name = "具体内容")
    private String specificContent;

    /** 排序号 */
    @Excel(name = "排序号")
    private Long sortOrder;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setDeptId(Long deptId) 
    {
        this.deptId = deptId;
    }

    public Long getDeptId() 
    {
        return deptId;
    }

    public void setCheckPosition(String checkPosition) 
    {
        this.checkPosition = checkPosition;
    }

    public String getCheckPosition() 
    {
        return checkPosition;
    }

    public void setSpecificContent(String specificContent) 
    {
        this.specificContent = specificContent;
    }

    public String getSpecificContent() 
    {
        return specificContent;
    }

    public void setSortOrder(Long sortOrder) 
    {
        this.sortOrder = sortOrder;
    }

    public Long getSortOrder() 
    {
        return sortOrder;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("deptId", getDeptId())
            .append("checkPosition", getCheckPosition())
            .append("specificContent", getSpecificContent())
            .append("sortOrder", getSortOrder())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
