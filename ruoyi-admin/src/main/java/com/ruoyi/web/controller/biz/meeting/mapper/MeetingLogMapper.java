package com.ruoyi.web.controller.biz.meeting.mapper;

import java.util.List;
import com.ruoyi.web.controller.biz.meeting.domain.MeetingLog;

/**
 * 开会台账Mapper接口
 * 
 * @author ruoyi
 * @date 2026-07-24
 */
public interface MeetingLogMapper 
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
     * 删除开会台账
     * 
     * @param id 开会台账主键
     * @return 结果
     */
    public int deleteMeetingLogById(Long id);

    /**
     * 批量删除开会台账
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteMeetingLogByIds(String[] ids);
}
