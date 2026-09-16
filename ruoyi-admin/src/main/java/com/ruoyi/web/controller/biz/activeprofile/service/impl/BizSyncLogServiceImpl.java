/**
 * 
 * 
 * @author Zack
 * @date Sep 15, 2026
 */
package com.ruoyi.web.controller.biz.activeprofile.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.web.controller.biz.activeprofile.domain.BizSyncLog;
import com.ruoyi.web.controller.biz.activeprofile.mapper.BizSyncLogMapper;
import com.ruoyi.web.controller.biz.activeprofile.service.IBizSyncLogService;

@Service
public class BizSyncLogServiceImpl implements IBizSyncLogService {

    @Autowired
    private BizSyncLogMapper syncLogMapper;

    @Override
    public BizSyncLog selectByInterfaceName(String interfaceName) {
        return syncLogMapper.selectByInterfaceName(interfaceName);
    }

    @Override
    public List<BizSyncLog> selectAll() {
        return syncLogMapper.selectAll();
    }

    @Override
    public int updateSyncLog(BizSyncLog log) {
        return syncLogMapper.updateSyncLog(log);
    }
}
