package com.ruoyi.web.controller.biz.video.controller;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.web.controller.biz.common.service.CommonDeductService;
import com.ruoyi.web.controller.biz.kpi.domain.KpiScore;
import com.ruoyi.web.controller.biz.kpi.service.IKpiScoreService;
import com.ruoyi.web.controller.biz.video.domain.VideoCheckItem;
import com.ruoyi.web.controller.biz.video.domain.VideoPlaybackRecord;
import com.ruoyi.web.controller.biz.video.service.IVideoCheckItemService;
import com.ruoyi.web.controller.biz.video.service.IVideoPlaybackRecordService;

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

        Map<String, Object> result = new HashMap<>();
        result.put("items", items);
        result.put("records", recordMap);
        result.put("kpiScoreMap", kpiScoreMap);
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

    @PostMapping("/cancelDeduct")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult cancelDeduct(@RequestBody Map<String, Object> params) {
        Long kpiScoreId = Long.valueOf(params.get("kpiScoreId").toString());
        String deductInfo = params.get("deductInfo").toString();
        System.out.println("=== 撤销收到的 kpiScoreId = " + kpiScoreId);

        // 1. 查询 KPI 记录
        KpiScore kpiScore = kpiScoreService.selectKpiScoreById(kpiScoreId);
        if (kpiScore == null) {
            return error("未找到考核记录");
        }

        // 2. 通过 source_record_id 定位视频回放记录
        Long videoRecordId = kpiScore.getSourceRecordId();
        System.out.println("kpiScore " + kpiScore.toString());
        if (videoRecordId != null) {
            VideoPlaybackRecord videoRecord = videoPlaybackRecordService.selectVideoPlaybackRecordById(videoRecordId);
            if (videoRecord != null) {
                String newStatus = commonDeductService.removeDeductDesc(videoRecord.getPlaybackStatus(), deductInfo);
                videoRecord.setPlaybackStatus(newStatus);
                videoRecord.setUpdateBy(ShiroUtils.getLoginName());
                videoPlaybackRecordService.updateVideoPlaybackRecord(videoRecord);
            }
        } else {
            // 降级方案：按 itemId + batchNo 匹配（兼容旧数据）
            // 但建议尽量保证 source_record_id 不为空
            return error("未关联视频记录，无法撤销");
        }

        // 3. 恢复 KPI 分数
        BigDecimal scoreChange = commonDeductService.parseScoreChange(deductInfo);
        if (scoreChange != null) {
            kpiScore.setScore(kpiScore.getScore().subtract(scoreChange)); // 反向操作
            kpiScore.setUpdateBy(ShiroUtils.getLoginName());
            kpiScoreService.updateKpiScore(kpiScore);
        }

        // 4. 从 KPI 备注中移除扣分描述
        String newRemark = commonDeductService.removeDeductDesc(kpiScore.getRemark(), deductInfo);
        kpiScore.setRemark(newRemark);
        kpiScoreService.updateKpiScore(kpiScore);

        return success("撤销成功");
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