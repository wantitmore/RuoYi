package com.ruoyi.web.controller.biz.meeting.service;

import java.util.List;
import com.ruoyi.web.controller.biz.meeting.domain.MeetingLog;

/**
 * 开会台账Service接口
 * 
 * @author ruoyi
 * @date 2026-07-24
 */
public interface IMeetingLogService 
{
    /**
     * 查询开会台账
     * 
     * @param id 开会台账主键
     * @return 开会台账
     */
    public MeetingLog selectMeetingLogById(Long id);

    /**
     * 查询开会台账列表
     * 
     * @param meetingLog 开会台账
     * @return 开会台账集合
     */
    public List<MeetingLog> selectMeetingLogList(MeetingLog meetingLog);

    /**
     * 新增开会台账
     * 
     * @param meetingLog 开会台账
     * @return 结果
     */
    public int insertMeetingLog(MeetingLog meetingLog);

    /**
     * 修改开会台账
     * 
     * @param meetingLog 开会台账
     * @return 结果
     */
    public int updateMeetingLog(MeetingLog meetingLog);

    /**
     * 批量删除开会台账
     * 
     * @param ids 需要删除的开会台账主键集合
     * @return 结果
     */
    public int deleteMeetingLogByIds(String ids);

    /**
     * 删除开会台账信息
     * 
     * @param id 开会台账主键
     * @return 结果
     */
    public int deleteMeetingLogById(Long id);
}
