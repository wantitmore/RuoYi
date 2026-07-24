package com.ruoyi.web.controller.biz.meeting.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface MeetingParticipantMapper {
    // 根据会议ID查询所有参与人员ID
    List<Long> selectUserIdsByMeetingId(Long meetingId);
    // 根据会议ID删除所有参与人员记录
    int deleteByMeetingId(Long meetingId);
    // 批量插入参与人员
    int insertMeetingParticipants(@Param("meetingId") Long meetingId, @Param("userIds") Long[] userIds);
}