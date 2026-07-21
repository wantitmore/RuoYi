package com.ruoyi.web.controller.biz.sixcheck.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.web.controller.biz.sixcheck.mapper.SixCheckItemMapper;
import com.ruoyi.web.controller.biz.sixcheck.domain.SixCheckItem;
import com.ruoyi.web.controller.biz.sixcheck.service.ISixCheckItemService;
import com.ruoyi.common.core.text.Convert;

/**
 * 六必查项目Service业务层处理
 * 
 * @author zack
 * @date 2026-07-01
 */
@Service
public class SixCheckItemServiceImpl implements ISixCheckItemService 
{
    @Autowired
    private SixCheckItemMapper sixCheckItemMapper;

    /**
     * 查询六必查项目
     * 
     * @param id 六必查项目主键
     * @return 六必查项目
     */
    @Override
    public SixCheckItem selectSixCheckItemById(Long id)
    {
        return sixCheckItemMapper.selectSixCheckItemById(id);
    }

    /**
     * 查询六必查项目列表
     * 
     * @param sixCheckItem 六必查项目
     * @return 六必查项目
     */
    @Override
    public List<SixCheckItem> selectSixCheckItemList(SixCheckItem sixCheckItem)
    {
        return sixCheckItemMapper.selectSixCheckItemList(sixCheckItem);
    }

    /**
     * 新增六必查项目
     * 
     * @param sixCheckItem 六必查项目
     * @return 结果
     */
    @Override
    public int insertSixCheckItem(SixCheckItem sixCheckItem)
    {
        sixCheckItem.setCreateTime(DateUtils.getNowDate());
        return sixCheckItemMapper.insertSixCheckItem(sixCheckItem);
    }

    /**
     * 修改六必查项目
     * 
     * @param sixCheckItem 六必查项目
     * @return 结果
     */
    @Override
    public int updateSixCheckItem(SixCheckItem sixCheckItem)
    {
        sixCheckItem.setUpdateTime(DateUtils.getNowDate());
        return sixCheckItemMapper.updateSixCheckItem(sixCheckItem);
    }

    /**
     * 批量删除六必查项目
     * 
     * @param ids 需要删除的六必查项目主键
     * @return 结果
     */
    @Override
    public int deleteSixCheckItemByIds(String ids)
    {
        return sixCheckItemMapper.deleteSixCheckItemByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除六必查项目信息
     * 
     * @param id 六必查项目主键
     * @return 结果
     */
    @Override
    public int deleteSixCheckItemById(Long id)
    {
        return sixCheckItemMapper.deleteSixCheckItemById(id);
    }
}
