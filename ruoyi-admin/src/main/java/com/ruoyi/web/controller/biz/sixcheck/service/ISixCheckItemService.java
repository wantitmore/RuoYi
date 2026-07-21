package com.ruoyi.web.controller.biz.sixcheck.service;

import java.util.List;
import com.ruoyi.web.controller.biz.sixcheck.domain.SixCheckItem;

/**
 * 六必查项目Service接口
 * 
 * @author zack
 * @date 2026-07-01
 */
public interface ISixCheckItemService 
{
    /**
     * 查询六必查项目
     * 
     * @param id 六必查项目主键
     * @return 六必查项目
     */
    public SixCheckItem selectSixCheckItemById(Long id);

    /**
     * 查询六必查项目列表
     * 
     * @param sixCheckItem 六必查项目
     * @return 六必查项目集合
     */
    public List<SixCheckItem> selectSixCheckItemList(SixCheckItem sixCheckItem);

    /**
     * 新增六必查项目
     * 
     * @param sixCheckItem 六必查项目
     * @return 结果
     */
    public int insertSixCheckItem(SixCheckItem sixCheckItem);

    /**
     * 修改六必查项目
     * 
     * @param sixCheckItem 六必查项目
     * @return 结果
     */
    public int updateSixCheckItem(SixCheckItem sixCheckItem);

    /**
     * 批量删除六必查项目
     * 
     * @param ids 需要删除的六必查项目主键集合
     * @return 结果
     */
    public int deleteSixCheckItemByIds(String ids);

    /**
     * 删除六必查项目信息
     * 
     * @param id 六必查项目主键
     * @return 结果
     */
    public int deleteSixCheckItemById(Long id);
}
