package com.ruoyi.web.controller.biz.video.service;

import java.util.List;
import com.ruoyi.web.controller.biz.video.domain.VideoPlaybackRecord;

/**
 * 视频回放记录Service接口
 * 
 * @author ruoyi
 * @date 2026-07-12
 */
public interface IVideoPlaybackRecordService 
{
    /**
     * 查询视频回放记录
     * 
     * @param id 视频回放记录主键
     * @return 视频回放记录
     */
    public VideoPlaybackRecord selectVideoPlaybackRecordById(Long id);

    /**
     * 查询视频回放记录列表
     * 
     * @param videoPlaybackRecord 视频回放记录
     * @return 视频回放记录集合
     */
    public List<VideoPlaybackRecord> selectVideoPlaybackRecordList(VideoPlaybackRecord videoPlaybackRecord);

    /**
     * 新增视频回放记录
     * 
     * @param videoPlaybackRecord 视频回放记录
     * @return 结果
     */
    public int insertVideoPlaybackRecord(VideoPlaybackRecord videoPlaybackRecord);

    /**
     * 修改视频回放记录
     * 
     * @param videoPlaybackRecord 视频回放记录
     * @return 结果
     */
    public int updateVideoPlaybackRecord(VideoPlaybackRecord videoPlaybackRecord);

    /**
     * 批量删除视频回放记录
     * 
     * @param ids 需要删除的视频回放记录主键集合
     * @return 结果
     */
    public int deleteVideoPlaybackRecordByIds(String ids);

    /**
     * 删除视频回放记录信息
     * 
     * @param id 视频回放记录主键
     * @return 结果
     */
    public int deleteVideoPlaybackRecordById(Long id);
}
