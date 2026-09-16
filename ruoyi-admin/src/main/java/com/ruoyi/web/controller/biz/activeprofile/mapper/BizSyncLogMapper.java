/**
 * 
 * 
 * @author Zack
 * @date Sep 15, 2026
 */
package com.ruoyi.web.controller.biz.activeprofile.mapper;

import java.util.List;

import com.ruoyi.web.controller.biz.activeprofile.domain.BizSyncLog;

public interface BizSyncLogMapper {
    BizSyncLog selectByInterfaceName(String interfaceName);

    List<BizSyncLog> selectAll();

    int updateSyncLog(BizSyncLog log);
}
