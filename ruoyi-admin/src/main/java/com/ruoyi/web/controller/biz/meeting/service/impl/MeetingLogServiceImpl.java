package com.ruoyi.web.controller.biz.meeting.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.web.controller.biz.meeting.mapper.MeetingLogMapper;
import com.ruoyi.web.controller.biz.meeting.domain.MeetingLog;
import com.ruoyi.web.controller.biz.meeting.service.IMeetingLogService;
import com.ruoyi.common.core.text.Convert;

/**
 * 开会台账Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-07-24
 */
@Service
public class MeetingLogServiceImpl implements IMeetingLogService 
{
    @Autowired
    private MeetingLogMapper meetingLogMapper;

    /**
     * 查询开会台账
     * 
     * @param id 开会台账主键
     * @return 开会台账
     */
    @Override
    public MeetingLog selectMeetingLogById(Long id)
    {
        return meetingLogMapper.selectMeetingLogById(id);
    }

    /**
     * 查询开会台账列表
     * 
     * @param meetingLog 开会台账
     * @return 开会台账
     */
    @Override
    public List<MeetingLog> selectMeetingLogList(MeetingLog meetingLog)
    {
        return meetingLogMapper.selectMeetingLogList(meetingLog);
    }

    /**
     * 新增开会台账
     * 
     * @param meetingLog 开会台账
     * @return 结果
     */
    @Override
    public int insertMeetingLog(MeetingLog meetingLog)
    {
        meetingLog.setCreateTime(DateUtils.getNowDate());
        return meetingLogMapper.insertMeetingLog(meetingLog);
    }

    /**
     * 修改开会台账
     * 
     * @param meetingLog 开会台账
     * @return 结果
     */
    @Override
    public int updateMeetingLog(MeetingLog meetingLog)
    {
        meetingLog.setUpdateTime(DateUtils.getNowDate());
        return meetingLogMapper.updateMeetingLog(meetingLog);
    }

    /**
     * 批量删除开会台账
     * 
     * @param ids 需要删除的开会台账主键
     * @return 结果
     */
    @Override
    public int deleteMeetingLogByIds(String ids)
    {
        return meetingLogMapper.deleteMeetingLogByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除开会台账信息
     * 
     * @param id 开会台账主键
     * @return 结果
     */
    @Override
    public int deleteMeetingLogById(Long id)
    {
        return meetingLogMapper.deleteMeetingLogById(id);
    }
}
