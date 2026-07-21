package com.ruoyi.web.controller.biz.video.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.web.controller.biz.video.mapper.VideoCheckItemMapper;
import com.ruoyi.web.controller.biz.video.domain.VideoCheckItem;
import com.ruoyi.web.controller.biz.video.service.IVideoCheckItemService;
import com.ruoyi.common.core.text.Convert;

/**
 * 视频回放检查项目Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-07-12
 */
@Service
public class VideoCheckItemServiceImpl implements IVideoCheckItemService 
{
    @Autowired
    private VideoCheckItemMapper videoCheckItemMapper;

    /**
     * 查询视频回放检查项目
     * 
     * @param id 视频回放检查项目主键
     * @return 视频回放检查项目
     */
    @Override
    public VideoCheckItem selectVideoCheckItemById(Long id)
    {
        return videoCheckItemMapper.selectVideoCheckItemById(id);
    }

    /**
     * 查询视频回放检查项目列表
     * 
     * @param videoCheckItem 视频回放检查项目
     * @return 视频回放检查项目
     */
    @Override
    public List<VideoCheckItem> selectVideoCheckItemList(VideoCheckItem videoCheckItem)
    {
        return videoCheckItemMapper.selectVideoCheckItemList(videoCheckItem);
    }

    /**
     * 新增视频回放检查项目
     * 
     * @param videoCheckItem 视频回放检查项目
     * @return 结果
     */
    @Override
    public int insertVideoCheckItem(VideoCheckItem videoCheckItem)
    {
        videoCheckItem.setCreateTime(DateUtils.getNowDate());
        return videoCheckItemMapper.insertVideoCheckItem(videoCheckItem);
    }

    /**
     * 修改视频回放检查项目
     * 
     * @param videoCheckItem 视频回放检查项目
     * @return 结果
     */
    @Override
    public int updateVideoCheckItem(VideoCheckItem videoCheckItem)
    {
        videoCheckItem.setUpdateTime(DateUtils.getNowDate());
        return videoCheckItemMapper.updateVideoCheckItem(videoCheckItem);
    }

    /**
     * 批量删除视频回放检查项目
     * 
     * @param ids 需要删除的视频回放检查项目主键
     * @return 结果
     */
    @Override
    public int deleteVideoCheckItemByIds(String ids)
    {
        return videoCheckItemMapper.deleteVideoCheckItemByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除视频回放检查项目信息
     * 
     * @param id 视频回放检查项目主键
     * @return 结果
     */
    @Override
    public int deleteVideoCheckItemById(Long id)
    {
        return videoCheckItemMapper.deleteVideoCheckItemById(id);
    }
}
