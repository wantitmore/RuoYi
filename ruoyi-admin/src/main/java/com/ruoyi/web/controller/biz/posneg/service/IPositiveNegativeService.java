package com.ruoyi.web.controller.biz.posneg.service;

import java.util.List;
import com.ruoyi.web.controller.biz.posneg.domain.PositiveNegative;

/**
 * 正负面清单Service接口
 * 
 * @author ruoyi
 * @date 2026-07-03
 */
public interface IPositiveNegativeService 
{
    /**
     * 查询正负面清单
     * 
     * @param id 正负面清单主键
     * @return 正负面清单
     */
    public PositiveNegative selectPositiveNegativeById(Long id);

    /**
     * 查询正负面清单列表
     * 
     * @param positiveNegative 正负面清单
     * @return 正负面清单集合
     */
    public List<PositiveNegative> selectPositiveNegativeList(PositiveNegative positiveNegative);

    /**
     * 新增正负面清单
     * 
     * @param positiveNegative 正负面清单
     * @return 结果
     */
    public int insertPositiveNegative(PositiveNegative positiveNegative);

    /**
     * 修改正负面清单
     * 
     * @param positiveNegative 正负面清单
     * @return 结果
     */
    public int updatePositiveNegative(PositiveNegative positiveNegative);

    /**
     * 批量删除正负面清单
     * 
     * @param ids 需要删除的正负面清单主键集合
     * @return 结果
     */
    public int deletePositiveNegativeByIds(String ids);

    /**
     * 删除正负面清单信息
     * 
     * @param id 正负面清单主键
     * @return 结果
     */
    public int deletePositiveNegativeById(Long id);
}
