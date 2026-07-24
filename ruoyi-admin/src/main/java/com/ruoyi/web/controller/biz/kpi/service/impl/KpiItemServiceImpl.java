package com.ruoyi.web.controller.biz.kpi.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.web.controller.biz.kpi.mapper.KpiItemMapper;
import com.ruoyi.web.controller.biz.kpi.domain.KpiItem;
import com.ruoyi.web.controller.biz.kpi.service.IKpiItemService;
import com.ruoyi.common.core.text.Convert;

/**
 * 考核项目Service业务层处理
 * 
 * @author 莲塘
 * @date 2026-05-31
 */
@Service
public class KpiItemServiceImpl implements IKpiItemService {
    @Autowired
    private KpiItemMapper kpiItemMapper;

    /**
     * 查询考核项目
     * 
     * @param id 考核项目主键
     * @return 考核项目
     */
    @Override
    public KpiItem selectKpiItemById(Long id) {
        return kpiItemMapper.selectKpiItemById(id);
    }

    /**
     * 查询考核项目列表
     * 
     * @param kpiItem 考核项目
     * @return 考核项目
     */
    @Override
    public List<KpiItem> selectKpiItemList(KpiItem kpiItem) {
        return kpiItemMapper.selectKpiItemList(kpiItem);
    }

    /**
     * 新增考核项目
     * 
     * @param kpiItem 考核项目
     * @return 结果
     */
    @Override
    public int insertKpiItem(KpiItem kpiItem) {
        kpiItem.setCreateTime(DateUtils.getNowDate());
        return kpiItemMapper.insertKpiItem(kpiItem);
    }

    /**
     * 修改考核项目
     * 
     * @param kpiItem 考核项目
     * @return 结果
     */
    @Override
    public int updateKpiItem(KpiItem kpiItem) {
        kpiItem.setUpdateTime(DateUtils.getNowDate());
        return kpiItemMapper.updateKpiItem(kpiItem);
    }

    /**
     * 批量删除考核项目
     * 
     * @param ids 需要删除的考核项目主键
     * @return 结果
     */
    @Override
    public int deleteKpiItemByIds(String ids) {
        return kpiItemMapper.deleteKpiItemByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除考核项目信息
     * 
     * @param id 考核项目主键
     * @return 结果
     */
    @Override
    public int deleteKpiItemById(Long id) {
        return kpiItemMapper.deleteKpiItemById(id);
    }

    @Override
    public Map<String, String> selectCategoryRequirementMap() {
        List<KpiItem> allItems = kpiItemMapper.selectKpiItemList(new KpiItem());
        Map<String, String> map = new LinkedHashMap<>();
        for (KpiItem item : allItems) {
            if (item.getCategory() != null && !map.containsKey(item.getCategory())) {
                map.put(item.getCategory(),
                        item.getWorkRequirement() != null ? item.getWorkRequirement() : "");
            }
        }
        return map;
    }
}
