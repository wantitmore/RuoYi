package com.ruoyi.web.controller.biz.video.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.web.controller.biz.video.mapper.VideoPlaybackRecordMapper;
import com.ruoyi.web.controller.biz.video.domain.VideoPlaybackRecord;
import com.ruoyi.web.controller.biz.video.service.IVideoPlaybackRecordService;
import com.ruoyi.common.core.text.Convert;

/**
 * 视频回放记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-07-12
 */
@Service
public class VideoPlaybackRecordServiceImpl implements IVideoPlaybackRecordService 
{
    @Autowired
    private VideoPlaybackRecordMapper videoPlaybackRecordMapper;

    /**
     * 查询视频回放记录
     * 
     * @param id 视频回放记录主键
     * @return 视频回放记录
     */
    @Override
    public VideoPlaybackRecord selectVideoPlaybackRecordById(Long id)
    {
        return videoPlaybackRecordMapper.selectVideoPlaybackRecordById(id);
    }

    /**
     * 查询视频回放记录列表
     * 
     * @param videoPlaybackRecord 视频回放记录
     * @return 视频回放记录
     */
    @Override
    public List<VideoPlaybackRecord> selectVideoPlaybackRecordList(VideoPlaybackRecord videoPlaybackRecord)
    {
        return videoPlaybackRecordMapper.selectVideoPlaybackRecordList(videoPlaybackRecord);
    }

    /**
     * 新增视频回放记录
     * 
     * @param videoPlaybackRecord 视频回放记录
     * @return 结果
     */
    @Override
    public int insertVideoPlaybackRecord(VideoPlaybackRecord videoPlaybackRecord)
    {
        videoPlaybackRecord.setCreateTime(DateUtils.getNowDate());
        return videoPlaybackRecordMapper.insertVideoPlaybackRecord(videoPlaybackRecord);
    }

    /**
     * 修改视频回放记录
     * 
     * @param videoPlaybackRecord 视频回放记录
     * @return 结果
     */
    @Override
    public int updateVideoPlaybackRecord(VideoPlaybackRecord videoPlaybackRecord)
    {
        videoPlaybackRecord.setUpdateTime(DateUtils.getNowDate());
        return videoPlaybackRecordMapper.updateVideoPlaybackRecord(videoPlaybackRecord);
    }

    /**
     * 批量删除视频回放记录
     * 
     * @param ids 需要删除的视频回放记录主键
     * @return 结果
     */
    @Override
    public int deleteVideoPlaybackRecordByIds(String ids)
    {
        return videoPlaybackRecordMapper.deleteVideoPlaybackRecordByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除视频回放记录信息
     * 
     * @param id 视频回放记录主键
     * @return 结果
     */
    @Override
    public int deleteVideoPlaybackRecordById(Long id)
    {
        return videoPlaybackRecordMapper.deleteVideoPlaybackRecordById(id);
    }
}
