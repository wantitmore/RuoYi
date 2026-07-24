package com.ruoyi.web.controller.biz.kpi.service;

import java.util.List;
import java.util.Map;

import com.ruoyi.web.controller.biz.kpi.domain.KpiItem;

/**
 * 考核项目Service接口
 * 
 * @author 莲塘
 * @date 2026-05-31
 */
public interface IKpiItemService {
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
     * 批量删除考核项目
     * 
     * @param ids 需要删除的考核项目主键集合
     * @return 结果
     */
    public int deleteKpiItemByIds(String ids);

    /**
     * 删除考核项目信息
     * 
     * @param id 考核项目主键
     * @return 结果
     */
    public int deleteKpiItemById(Long id);

    /**
     * 查询所有类别及其对应的工作内容要求（每个类别取第一个有效值）
     * 
     * @return Map<类别名称, 工作内容要求>
     */
    Map<String, String> selectCategoryRequirementMap();
}
