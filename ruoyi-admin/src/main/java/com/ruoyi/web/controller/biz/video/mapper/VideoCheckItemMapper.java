package com.ruoyi.web.controller.biz.video.mapper;

import java.util.List;
import com.ruoyi.web.controller.biz.video.domain.VideoCheckItem;

/**
 * 视频回放检查项目Mapper接口
 * 
 * @author ruoyi
 * @date 2026-07-12
 */
public interface VideoCheckItemMapper 
{
    /**
     * 查询视频回放检查项目
     * 
     * @param id 视频回放检查项目主键
     * @return 视频回放检查项目
     */
    public VideoCheckItem selectVideoCheckItemById(Long id);

    /**
     * 查询视频回放检查项目列表
     * 
     * @param videoCheckItem 视频回放检查项目
     * @return 视频回放检查项目集合
     */
    public List<VideoCheckItem> selectVideoCheckItemList(VideoCheckItem videoCheckItem);

    /**
     * 新增视频回放检查项目
     * 
     * @param videoCheckItem 视频回放检查项目
     * @return 结果
     */
    public int insertVideoCheckItem(VideoCheckItem videoCheckItem);

    /**
     * 修改视频回放检查项目
     * 
     * @param videoCheckItem 视频回放检查项目
     * @return 结果
     */
    public int updateVideoCheckItem(VideoCheckItem videoCheckItem);

    /**
     * 删除视频回放检查项目
     * 
     * @param id 视频回放检查项目主键
     * @return 结果
     */
    public int deleteVideoCheckItemById(Long id);

    /**
     * 批量删除视频回放检查项目
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteVideoCheckItemByIds(String[] ids);
}
