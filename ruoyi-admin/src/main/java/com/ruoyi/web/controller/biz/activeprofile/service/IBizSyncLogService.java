/**
 * 
 * 
 * @author Zack
 * @date Sep 15, 2026
 */
package com.ruoyi.web.controller.biz.activeprofile.service;

import java.util.List;

import com.ruoyi.web.controller.biz.activeprofile.domain.BizSyncLog;

public interface IBizSyncLogService {
    BizSyncLog selectByInterfaceName(String interfaceName);

    List<BizSyncLog> selectAll();

    int updateSyncLog(BizSyncLog log);
}
