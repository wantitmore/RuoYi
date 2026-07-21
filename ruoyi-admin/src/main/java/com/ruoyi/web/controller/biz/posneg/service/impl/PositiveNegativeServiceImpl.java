package com.ruoyi.web.controller.biz.posneg.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.web.controller.biz.posneg.mapper.PositiveNegativeMapper;
import com.ruoyi.web.controller.biz.posneg.domain.PositiveNegative;
import com.ruoyi.web.controller.biz.posneg.service.IPositiveNegativeService;
import com.ruoyi.common.core.text.Convert;

/**
 * 正负面清单Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-07-03
 */
@Service
public class PositiveNegativeServiceImpl implements IPositiveNegativeService 
{
    @Autowired
    private PositiveNegativeMapper positiveNegativeMapper;

    /**
     * 查询正负面清单
     * 
     * @param id 正负面清单主键
     * @return 正负面清单
     */
    @Override
    public PositiveNegative selectPositiveNegativeById(Long id)
    {
        return positiveNegativeMapper.selectPositiveNegativeById(id);
    }

    /**
     * 查询正负面清单列表
     * 
     * @param positiveNegative 正负面清单
     * @return 正负面清单
     */
    @Override
    public List<PositiveNegative> selectPositiveNegativeList(PositiveNegative positiveNegative)
    {
        return positiveNegativeMapper.selectPositiveNegativeList(positiveNegative);
    }

    /**
     * 新增正负面清单
     * 
     * @param positiveNegative 正负面清单
     * @return 结果
     */
    @Override
    public int insertPositiveNegative(PositiveNegative positiveNegative)
    {
        positiveNegative.setCreateTime(DateUtils.getNowDate());
        return positiveNegativeMapper.insertPositiveNegative(positiveNegative);
    }

    /**
     * 修改正负面清单
     * 
     * @param positiveNegative 正负面清单
     * @return 结果
     */
    @Override
    public int updatePositiveNegative(PositiveNegative positiveNegative)
    {
        positiveNegative.setUpdateTime(DateUtils.getNowDate());
        return positiveNegativeMapper.updatePositiveNegative(positiveNegative);
    }

    /**
     * 批量删除正负面清单
     * 
     * @param ids 需要删除的正负面清单主键
     * @return 结果
     */
    @Override
    public int deletePositiveNegativeByIds(String ids)
    {
        return positiveNegativeMapper.deletePositiveNegativeByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除正负面清单信息
     * 
     * @param id 正负面清单主键
     * @return 结果
     */
    @Override
    public int deletePositiveNegativeById(Long id)
    {
        return positiveNegativeMapper.deletePositiveNegativeById(id);
    }
}
