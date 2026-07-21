package com.ruoyi.web.controller.biz.kpi.mapper;

import java.util.List;
import com.ruoyi.web.controller.biz.kpi.domain.KpiItem;

/**
 * 考核项目Mapper接口
 * 
 * @author 莲塘
 * @date 2026-05-31
 */
public interface KpiItemMapper 
{
    /**
     * 查询考核项目
     * 
     * @param id 考核项目主键
     * @return 考核项目
     */
    public KpiItem selectKpiItemById(Long id);

    /**
     * 查询考核项目列表
     * 
     * @param kpiItem 考核项目
     * @return 考核项目集合
     */
    public List<KpiItem> selectKpiItemList(KpiItem kpiItem);

    /**
     * 新增考核项目
     * 
     * @param kpiItem 考核项目
     * @return 结果
     */
    public int insertKpiItem(KpiItem kpiItem);

    /**
     * 修改考核项目
     * 
     * @param kpiItem 考核项目
     * @return 结果
     */
    public int updateKpiItem(KpiItem kpiItem);

    /**
     * 删除考核项目
     * 
     * @param id 考核项目主键
     * @return 结果
     */
    public int deleteKpiItemById(Long id);

    /**
     * 批量删除考核项目
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteKpiItemByIds(String[] ids);
}
