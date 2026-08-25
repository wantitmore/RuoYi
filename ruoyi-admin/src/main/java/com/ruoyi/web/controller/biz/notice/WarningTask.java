/**
 * 
 * 
 * @author Zack
 * @date Aug 21, 2026
 */
package com.ruoyi.web.controller.biz.notice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.web.controller.biz.kpi.service.IKpiScoreService;

@Component
public class WarningTask {
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private WarningNoticeService warningNoticeService;

    @Autowired
    private IKpiScoreService kpiScoreService;

    @Scheduled(cron = "0 0 1 25 * ?")
    public void checkNoRecordWarning() {
        String batchNo = new SimpleDateFormat("yyyy-MM").format(new Date());

        List<Long> deptIds = deptService.selectDeptList(new SysDept())
                .stream()
                .map(SysDept::getDeptId)
                .collect(Collectors.toList());

        for (Long deptId : deptIds) {
            List<String> noRecordUsers = getUsersWithNoRecord(deptId, batchNo);
            if (!noRecordUsers.isEmpty()) {
                warningNoticeService.sendNoRecordWarning(deptId, noRecordUsers, batchNo);
            }
        }
    }

    private List<String> getUsersWithNoRecord(Long deptId, String batchNo) {
        SysUser query = new SysUser();
        query.setDeptId(deptId);
        List<SysUser> users = userService.selectUserList(query);
        if (users.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> hasRecordUserIds = kpiScoreService.selectUserIdsByBatchNoAndDept(deptId, batchNo);

        return users.stream()
                .filter(u -> !hasRecordUserIds.contains(u.getUserId()))
                .map(SysUser::getUserName)
                .collect(Collectors.toList());
    }

}
