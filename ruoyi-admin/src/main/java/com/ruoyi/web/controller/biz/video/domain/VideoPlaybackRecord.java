package com.ruoyi.web.controller.biz.video.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 视频回放记录对象 video_playback_record
 * 
 * @author ruoyi
 * @date 2026-07-12
 */
public class VideoPlaybackRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 检查项目ID */
    @Excel(name = "检查项目ID")
    private Long itemId;

    /** 回放情况 */
    @Excel(name = "回放情况")
    private String playbackStatus;

    /** 月份（YYYY-MM） */
    @Excel(name = "月份", readConverterExp = "Y=YYY-MM")
    private String batchNo;

    /** 所属部门ID */
    @Excel(name = "所属部门ID")
    private Long deptId;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setItemId(Long itemId) 
    {
        this.itemId = itemId;
    }

    public Long getItemId() 
    {
        return itemId;
    }

    public void setPlaybackStatus(String playbackStatus) 
    {
        this.playbackStatus = playbackStatus;
    }

    public String getPlaybackStatus() 
    {
        return playbackStatus;
    }

    public void setBatchNo(String batchNo) 
    {
        this.batchNo = batchNo;
    }

    public String getBatchNo() 
    {
        return batchNo;
    }

    public void setDeptId(Long deptId) 
    {
        this.deptId = deptId;
    }

    public Long getDeptId() 
    {
        return deptId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("itemId", getItemId())
            .append("playbackStatus", getPlaybackStatus())
            .append("batchNo", getBatchNo())
            .append("deptId", getDeptId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
