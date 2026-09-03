package com.ruoyi.web.controller.biz.video.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.web.controller.biz.common.service.CommonDeductService;
import com.ruoyi.web.controller.biz.kpi.domain.KpiScore;
import com.ruoyi.web.controller.biz.kpi.service.IKpiScoreService;
import com.ruoyi.web.controller.biz.sixcheck.domain.SixCheckDeductDetail;
import com.ruoyi.web.controller.biz.sixcheck.service.ISixCheckDeductDetailService;
import com.ruoyi.web.controller.biz.video.domain.VideoCheckItem;
import com.ruoyi.web.controller.biz.video.domain.VideoPlaybackRecord;
import com.ruoyi.web.controller.biz.video.service.IVideoCheckItemService;
import com.ruoyi.web.controller.biz.video.service.IVideoPlaybackRecordService;
import com.ruoyi.system.service.ISysUserService;

@Controller
@RequestMapping("/video/record")
public class VideoPlaybackRecordController extends BaseController {

    @Autowired
    private IVideoPlaybackRecordService videoPlaybackRecordService;

    @Autowired
    private IVideoCheckItemService videoCheckItemService;

    @Autowired
    private CommonDeductService commonDeductService;

    @Autowired
    private IKpiScoreService kpiScoreService;

    @Autowired
    private ISixCheckDeductDetailService sixCheckDeductDetailService;

    @Autowired
    private ISysUserService userService;

    private String prefix = "video/record";

    // ==================== 列表页（菜单 url: video/record/list）====================
    @RequiresPermissions("video:list:view")
    @GetMapping("/list")
    public String listPage(ModelMap mmap) {
        mmap.put("canAdd", ShiroUtils.getSubject().isPermitted("video:add"));
        mmap.put("canEdit", ShiroUtils.getSubject().isPermitted("video:edit"));
        mmap.put("canRemove", ShiroUtils.getSubject().isPermitted("video:remove"));
        return "video/record/record";
    }

    @RequiresPermissions("video:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(VideoPlaybackRecord record) {
        startPage();
        List<VideoPlaybackRecord> list = videoPlaybackRecordService.selectVideoPlaybackRecordList(record);
        return getDataTable(list);
    }

    // ==================== 录入页（菜单 url: video/record/input）====================
    @GetMapping("/input")
    public String input(ModelMap mmap) {
        mmap.put("canEdit", ShiroUtils.getSubject().isPermitted("video:record:edit"));
        mmap.put("currentDeptId", ShiroUtils.getSysUser().getDeptId());
        return "video/input";
    }

    @GetMapping("/load")
    @ResponseBody
    public AjaxResult load(@RequestParam String batchNo) {
        System.out.println("load batchNo : " + batchNo);
        VideoCheckItem queryItem = new VideoCheckItem();
        queryItem.setDeptId(ShiroUtils.getSysUser().getDeptId());
        List<VideoCheckItem> items = videoCheckItemService.selectVideoCheckItemList(queryItem);
        items.sort(Comparator.comparing(VideoCheckItem::getSortOrder));

        VideoPlaybackRecord queryRecord = new VideoPlaybackRecord();
        queryRecord.setBatchNo(batchNo);
        queryRecord.setDeptId(ShiroUtils.getSysUser().getDeptId());
        List<VideoPlaybackRecord> records = videoPlaybackRecordService.selectVideoPlaybackRecordList(queryRecord);

        Map<Long, String> recordMap = new HashMap<>();
        for (VideoPlaybackRecord r : records) {
            recordMap.put(r.getItemId(), r.getPlaybackStatus());
        }

        Map<Long, Long> kpiScoreMap = new HashMap<>();
        for (VideoPlaybackRecord r : records) {
            String status = r.getPlaybackStatus();
            if (status != null && status.contains("关联加扣分")) {
                KpiScore scoreQuery = new KpiScore();
                scoreQuery.setSourceRecordId(r.getId());
                scoreQuery.setBatchNo(batchNo);
                List<KpiScore> scoreList = kpiScoreService.selectKpiScoreList(scoreQuery);
                if (!scoreList.isEmpty()) {
                    kpiScoreMap.put(r.getItemId(), scoreList.get(0).getId());
                }
            }
        }

        List<Map<String, Object>> detailInfoList = new ArrayList<>();
        for (VideoPlaybackRecord r : records) {
            SixCheckDeductDetail queryDetail = new SixCheckDeductDetail();
            queryDetail.setSourceType("video"); // 只查视频来源
            queryDetail.setSixCheckRecordId(r.getId());
            queryDetail.setStatus(1);
            List<SixCheckDeductDetail> details = sixCheckDeductDetailService.selectList(queryDetail);
            for (SixCheckDeductDetail d : details) {
                Map<String, Object> info = new HashMap<>();
                info.put("detailId", d.getId());
                info.put("deductInfo", d.getDeductInfo());
                info.put("itemId", r.getItemId());
                info.put("status", d.getStatus());
                info.put("createBy", d.getCreateBy());
                detailInfoList.add(info);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("items", items);
        result.put("records", recordMap);
        result.put("kpiScoreMap", kpiScoreMap);
        result.put("detailInfoList", detailInfoList);
        return success().put("data", result);

    }

    @RequiresPermissions("video:record:edit")
    @PostMapping("/save")
    @ResponseBody
    public AjaxResult save(@RequestBody List<VideoPlaybackRecord> recordList) {
        for (VideoPlaybackRecord r : recordList) {
            r.setDeptId(ShiroUtils.getSysUser().getDeptId());
            VideoPlaybackRecord exist = new VideoPlaybackRecord();
            exist.setItemId(r.getItemId());
            exist.setBatchNo(r.getBatchNo());
            exist.setDeptId(r.getDeptId());
            List<VideoPlaybackRecord> list = videoPlaybackRecordService.selectVideoPlaybackRecordList(exist);
            if (list.size() > 0) {
                VideoPlaybackRecord update = list.get(0);
                update.setPlaybackStatus(r.getPlaybackStatus());
                update.setUpdateBy(ShiroUtils.getLoginName());
                videoPlaybackRecordService.updateVideoPlaybackRecord(update);
            } else {
                r.setCreateBy(ShiroUtils.getLoginName());
                videoPlaybackRecordService.insertVideoPlaybackRecord(r);
            }
        }
        return success("保存成功");
    }

    @PostMapping("/cancelByDetail")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult cancelByDetail(@RequestBody Map<String, Object> params) {
        Long detailId = Long.valueOf(params.get("detailId").toString());
        System.out.println("detailId : " + detailId);
        // 1. 查询明细（source_type 不需要特意过滤，但可加）
        SixCheckDeductDetail detail = sixCheckDeductDetailService.selectById(detailId);
        if (detail == null || detail.getStatus() == 0) {
            return error("扣分记录不存在或已撤销");
        }

        // 2. 恢复 KPI 分数
        KpiScore kpiScore = kpiScoreService.selectKpiScoreById(detail.getKpiScoreId());
        if (kpiScore == null) {
            return error("关联的考核记录不存在");
        }

        SysUser targetUser = userService.selectUserById(kpiScore.getUserId());
        if (targetUser == null) {
            return error("被考核人不存在");
        }
        SysUser currentUser = ShiroUtils.getSysUser();
        if (!currentUser.isAdmin() && !currentUser.getDeptId().equals(targetUser.getDeptId())) {
            return error("无权撤销其他部门的扣分记录");
        }

        Subject subject = SecurityUtils.getSubject();

        boolean isSelf = currentUser.equals(detail.getCreateBy());
        boolean isAdmin = subject.hasRole("admin");
        boolean isDeptAdmin = subject.hasRole("dept_manager");

        if (!isSelf && !isAdmin && !isDeptAdmin) {
            return AjaxResult.error("权限不足：只有本人、部门管理员可以撤销");
        }

        BigDecimal deductScore = detail.getDeductScore();
        if (deductScore != null) {
            kpiScore.setScore(kpiScore.getScore().subtract(deductScore));
        }

        // 3. 移除 KPI 备注中的扣分描述
        if (StringUtils.isNotBlank(kpiScore.getRemark())) {
            String newRemark = removeDeductInfoByLine(kpiScore.getRemark(), detail.getDeductInfo());
            kpiScore.setRemark(newRemark);
        }
        kpiScore.setUpdateBy(ShiroUtils.getLoginName());
        kpiScoreService.updateKpiScore(kpiScore);

        // 4. 更新来源记录（视频回放或六必查）
        Long sourceRecordId = detail.getSixCheckRecordId();
        System.out.println("=== 撤销前=== sourceRecordId: [" + sourceRecordId + "]");
        if (sourceRecordId != null && "video".equals(detail.getSourceType())) {
            System.out.println("=== 撤销前 sourceRecordId: [" + sourceRecordId + "]");
            VideoPlaybackRecord record = videoPlaybackRecordService.selectVideoPlaybackRecordById(sourceRecordId);
            if (record != null) {
                String oldStatus = record.getPlaybackStatus();
                System.out.println("=== 撤销前 playback_status: [" + oldStatus + "]");
                System.out.println("=== 待移除 deductInfo: [" + detail.getDeductInfo() + "]");
                String newValue = removeDeductInfoByLine(record.getPlaybackStatus(), detail.getDeductInfo());
                System.out.println("=== 新 playback_status: [" + newValue + "]");
                record.setPlaybackStatus(newValue == null ? "" : newValue);
                record.setUpdateBy(ShiroUtils.getLoginName());
                videoPlaybackRecordService.updateVideoPlaybackRecord(record);
            }
        }

        // 5. 标记明细为已撤销
        detail.setStatus(0);
        System.out.println("撤销成功");
        detail.setUpdateBy(ShiroUtils.getLoginName());
        sixCheckDeductDetailService.update(detail);

        return success("撤销成功");
    }

    private String removeDeductInfoByLine(String content, String deductInfo) {
        if (StringUtils.isBlank(content))
            return null;
        System.out.println("content is " + content);
        System.out.println("deductInfo is " + deductInfo);
        String[] lines = content.split("\n");
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            if (line.trim().equals(deductInfo.trim())) {
                continue;
            }
            if (sb.length() > 0)
                sb.append("\n");
            sb.append(line);
        }
        String result = sb.toString().trim();
        System.out.println("result is " + result);
        return StringUtils.isBlank(result) ? null : result;
    }
    // ==================== 以下为生成器自动生成的 CRUD 方法（请保持原有代码不变）====================

    /**
     * 导出视频回放记录列表
     */
    @RequiresPermissions("video:export")
    @Log(title = "视频回放记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(VideoPlaybackRecord videoPlaybackRecord) {
        List<VideoPlaybackRecord> list = videoPlaybackRecordService.selectVideoPlaybackRecordList(videoPlaybackRecord);
        ExcelUtil<VideoPlaybackRecord> util = new ExcelUtil<VideoPlaybackRecord>(VideoPlaybackRecord.class);
        return util.exportExcel(list, "视频回放记录数据");
    }

    /**
     * 新增视频回放记录
     */
    @RequiresPermissions("video:add")
    @GetMapping("/add")
    public String add() {
        return prefix + "/add";
    }

    /**
     * 新增保存视频回放记录
     */
    @RequiresPermissions("video:add")
    @Log(title = "视频回放记录", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(VideoPlaybackRecord videoPlaybackRecord) {
        return toAjax(videoPlaybackRecordService.insertVideoPlaybackRecord(videoPlaybackRecord));
    }

    /**
     * 修改视频回放记录
     */
    @RequiresPermissions("video:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap) {
        VideoPlaybackRecord videoPlaybackRecord = videoPlaybackRecordService.selectVideoPlaybackRecordById(id);
        mmap.put("videoPlaybackRecord", videoPlaybackRecord);
        return prefix + "/edit";
    }

    /**
     * 修改保存视频回放记录
     */
    @RequiresPermissions("video:edit")
    @Log(title = "视频回放记录", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(VideoPlaybackRecord videoPlaybackRecord) {
        return toAjax(videoPlaybackRecordService.updateVideoPlaybackRecord(videoPlaybackRecord));
    }

    /**
     * 删除视频回放记录
     */
    @RequiresPermissions("video:remove")
    @Log(title = "视频回放记录", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(videoPlaybackRecordService.deleteVideoPlaybackRecordByIds(ids));
    }
}