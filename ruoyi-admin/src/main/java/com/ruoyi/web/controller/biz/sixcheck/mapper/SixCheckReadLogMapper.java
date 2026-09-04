/**
 * 
 * 
 * @author Zack
 * @date Sep 04, 2026
 */
package com.ruoyi.web.controller.biz.sixcheck.mapper;

import java.util.List;

import com.ruoyi.web.controller.biz.sixcheck.domain.SixCheckReadLog;

public interface SixCheckReadLogMapper {

    public List<SixCheckReadLog> selectReadLogsByDate(Long deptId, String checkDate);

    public void upsertReadLog(Long deptId, String checkDate, Long userId);

}
