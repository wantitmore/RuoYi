package com.ruoyi.web.controller.biz.meeting.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.web.controller.biz.meeting.domain.MeetingLog;
import com.ruoyi.web.controller.biz.meeting.mapper.MeetingParticipantMapper;
import com.ruoyi.web.controller.biz.meeting.service.IMeetingLogService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 开会台账Controller
 * 
 * @author ruoyi
 * @date 2026-07-24
 */
@Controller
@RequestMapping("/meeting/log")
public class MeetingLogController extends BaseController {
    private String prefix = "meeting/log";

    @Autowired
    private IMeetingLogService meetingLogService;

    @Autowired
    private MeetingParticipantMapper meetingParticipantMapper;

    @Autowired
    private ISysUserService userService;

    @RequiresPermissions("meeting:view")
    @GetMapping()
    public String log(ModelMap mmap) {
        mmap.put("canAdd", ShiroUtils.getSubject().isPermitted("meeting:add"));
        mmap.put("canEdit", ShiroUtils.getSubject().isPermitted("meeting:edit"));
        mmap.put("canRemove", ShiroUtils.getSubject().isPermitted("meeting:remove"));
        mmap.put("canExport", ShiroUtils.getSubject().isPermitted("meeting:export"));
        return prefix + "/log";
    }

    /**
     * 查询开会台账列表
     */
    @RequiresPermissions("meeting:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(MeetingLog meetingLog) {
        startPage();
        if (!ShiroUtils.getSysUser().isAdmin()) {
            meetingLog.setDeptId(ShiroUtils.getSysUser().getDeptId());
        }
        List<MeetingLog> list = meetingLogService.selectMeetingLogList(meetingLog);
        // 填充参会人员姓名
        for (MeetingLog log : list) {
            List<Long> userIds = meetingParticipantMapper.selectUserIdsByMeetingId(log.getId());
            if (userIds != null && !userIds.isEmpty()) {
                List<String> names = new ArrayList<>();
                for (Long uid : userIds) {
                    SysUser user = userService.selectUserById(uid);
                    if (user != null)
                        names.add(user.getUserName());
                }
                log.setParticipantNames(String.join(", ", names));
            }
        }
        return getDataTable(list);
    }

    /**
     * 导出开会台账列表
     */
    @RequiresPermissions("meeting:export")
    @Log(title = "开会台账", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(MeetingLog meetingLog) {
        List<MeetingLog> list = meetingLogService.selectMeetingLogList(meetingLog);
        ExcelUtil<MeetingLog> util = new ExcelUtil<MeetingLog>(MeetingLog.class);
        return util.exportExcel(list, "开会台账数据");
    }

    /**
     * 新增开会台账
     */
    @RequiresPermissions("meeting:add")
    @GetMapping("/add")
    public String add(ModelMap mmap) {
        // 获取本部门用户（非管理员看本部门，管理员看全部）
        SysUser query = new SysUser();
        if (!ShiroUtils.getSysUser().isAdmin()) {
            query.setDeptId(ShiroUtils.getSysUser().getDeptId());
        }
        List<SysUser> users = userService.selectUserList(query);
        mmap.put("users", users);
        return prefix + "/add";
    }

    /**
     * 新增保存开会台账
     */
    @RequiresPermissions("meeting:add")
    @Log(title = "开会台账", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(MeetingLog meetingLog) {
        System.out.println("接收到的 meetingContent = [" + meetingLog.getMeetingContent() + "]");
        meetingLog.setDeptId(ShiroUtils.getSysUser().getDeptId());
        int rows = meetingLogService.insertMeetingLog(meetingLog);
        // 插入后 meetingLog.getId() 已有值（useGeneratedKeys=true）
        if (meetingLog.getUserIds() != null && meetingLog.getUserIds().length > 0) {
            meetingParticipantMapper.insertMeetingParticipants(meetingLog.getId(), meetingLog.getUserIds());
        }
        return toAjax(rows);
    }

    /**
     * 修改开会台账
     */
    @RequiresPermissions("meeting:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap) {
        MeetingLog meetingLog = meetingLogService.selectMeetingLogById(id);
        mmap.put("meetingLog", meetingLog);
        // 获取本部门用户
        SysUser query = new SysUser();
        if (!ShiroUtils.getSysUser().isAdmin()) {
            query.setDeptId(ShiroUtils.getSysUser().getDeptId());
        }
        List<SysUser> users = userService.selectUserList(query);
        mmap.put("users", users);
        // 获取已选中的参会人员ID，用于回显
        List<Long> selectedIds = meetingParticipantMapper.selectUserIdsByMeetingId(id);
        Set<Long> selectedIdSet = new HashSet<>(selectedIds);
        mmap.put("selectedIds", selectedIdSet);
        return prefix + "/edit";
    }

    /**
     * 修改保存开会台账
     */
    @RequiresPermissions("meeting:edit")
    @Log(title = "开会台账", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(MeetingLog meetingLog) {
        meetingLog.setDeptId(ShiroUtils.getSysUser().getDeptId());
        int rows = meetingLogService.updateMeetingLog(meetingLog);
        // 先删除原有参会人员，再插入新选择的
        meetingParticipantMapper.deleteByMeetingId(meetingLog.getId());
        if (meetingLog.getUserIds() != null && meetingLog.getUserIds().length > 0) {
            meetingParticipantMapper.insertMeetingParticipants(meetingLog.getId(), meetingLog.getUserIds());
        }
        return toAjax(rows);
    }

    /**
     * 删除开会台账
     */
    @RequiresPermissions("meeting:remove")
    @Log(title = "开会台账", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        String[] idArray = ids.split(",");
        for (String idStr : idArray) {
            Long id = Long.valueOf(idStr);
            meetingParticipantMapper.deleteByMeetingId(id);
        }
        return toAjax(meetingLogService.deleteMeetingLogByIds(ids));
    }
}
