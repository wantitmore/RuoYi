/**
 * 
 * 
 * @author Zack
 * @date Aug 24, 2026
 */
package com.ruoyi.web.controller.biz.notice.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysNoticeReceiverMapper {
    int batchInsert(@Param("noticeId") Long noticeId, 
                    @Param("userIds") List<Long> userIds);
     List<Long> selectNoticeIdsByUserId(@Param("userId") Long userId);
}

