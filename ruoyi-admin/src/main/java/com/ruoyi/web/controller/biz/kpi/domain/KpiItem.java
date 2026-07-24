package com.ruoyi.web.controller.biz.kpi.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 考核项目对象 kpi_item
 * 
 * @author 莲塘
 * @date 2026-05-31
 */
public class KpiItem extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 考核项目名称 */
    @Excel(name = "考核项目名称")
    private String name;

    /** 满分 */
    @Excel(name = "满分")
    private BigDecimal maxScore;

    /** 评分类型（NUMBER/STAR/TEXT等） */
    @Excel(name = "评分类型", readConverterExp = "N=UMBER/STAR/TEXT等")
    private String scoreType;

    /** 所属部门ID（关联sys_dept） */
    private Long deptId;

    @Excel(name = "工作内容要求")
    private String workRequirement;

    public String getWorkRequirement() {
        return workRequirement;
    }

    public void setWorkRequirement(String workRequirement) {
        this.workRequirement = workRequirement;
    }

    private String category; // 考核类别（纪律作风/狱政/生产/教育/执勤现场管理/正负面清单/动态档案评价）

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }

    public void setMaxScore(BigDecimal maxScore) 
    {
        this.maxScore = maxScore;
    }

    public BigDecimal getMaxScore() 
    {
        return maxScore;
    }

    public void setScoreType(String scoreType) 
    {
        this.scoreType = scoreType;
    }

    public String getScoreType() 
    {
        return scoreType;
    }

    

    public Long getDeptId() {
        return deptId;
    }



    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

        @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("maxScore", getMaxScore())
            .append("scoreType", getScoreType())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("deptId", getDeptId())
            .append("category", getCategory())
            .append("workRequirement", getWorkRequirement())
            .toString();
    }
}
