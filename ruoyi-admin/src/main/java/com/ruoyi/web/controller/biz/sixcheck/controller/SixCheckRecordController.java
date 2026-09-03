package com.ruoyi.web.controller.biz.sixcheck.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.web.controller.biz.kpi.domain.KpiItem;
import com.ruoyi.web.controller.biz.kpi.domain.KpiScore;
import com.ruoyi.web.controller.biz.kpi.service.IKpiItemService;
import com.ruoyi.web.controller.biz.kpi.service.IKpiScoreService;
import com.ruoyi.web.controller.biz.sixcheck.domain.SixCheckDeductDetail;
import com.ruoyi.web.controller.biz.sixcheck.domain.SixCheckItem;
import com.ruoyi.web.controller.biz.sixcheck.domain.SixCheckRecord;
import com.ruoyi.web.controller.biz.sixcheck.domain.SixCheckRecordWrapper;
import com.ruoyi.web.controller.biz.sixcheck.service.ISixCheckDeductDetailService;
import com.ruoyi.web.controller.biz.sixcheck.service.ISixCheckItemService;
import com.ruoyi.web.controller.biz.sixcheck.service.ISixCheckRecordService;
import com.ruoyi.web.controller.biz.sixcheck.service.impl.SixCheckDeductDetailServiceImpl;

import io.micrometer.common.util.StringUtils;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.ShiroUtils;

/**
 * 六必查记录Controller
 * 
 * @author ruoyi
 * @date 2026-07-01
 */
@Controller
@RequestMapping("/sixcheck/record")
public class SixCheckRecordController extends BaseController {
    private String prefix = "sixcheck/record";

    @Autowired
    private ISixCheckRecordService sixCheckRecordService;

    @Autowired
    private ISixCheckItemService sixCheckItemService; // 确保注入项目Service

    @Autowired
    private IKpiScoreService kpiScoreService; // 注入考核分数Service

    @Autowired
    private IKpiItemService kpiItemService; // 注入考核项目Service
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISixCheckDeductDetailService sixCheckDeductDetailService;

    @GetMapping("/input")
    public String input(ModelMap mmap) {
        // 判断当前用户是否拥有 sixcheck:record:edit 权限
        mmap.put("canEdit", ShiroUtils.getSubject().isPermitted("sixcheck:record:edit"));
        mmap.put("currentUserName", ShiroUtils.getSysUser().getUserName());
        mmap.put("currentDeptId", ShiroUtils.getSysUser().getDeptId());
        return "sixcheck/input";
    }

    @RequiresPermissions("sixcheck:record:view")
    @GetMapping()
    public String record() {
        return prefix + "/record";
    }

    /**
     * 查询六必查记录列表
     */
    @RequiresPermissions("sixcheck:record:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(SixCheckRecord sixCheckRecord) {
        startPage();
        if (!ShiroUtils.getSysUser().isAdmin()) {
            sixCheckRecord.setDeptId(ShiroUtils.getSysUser().getDeptId());
        }
        List<SixCheckRecord> list = sixCheckRecordService.selectSixCheckRecordList(sixCheckRecord);
        return getDataTable(list);
    }

    /**
     * 导出六必查记录列表
     */
    @RequiresPermissions("sixcheck:record:export")
    @Log(title = "六必查记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(SixCheckRecord sixCheckRecord) {
        List<SixCheckRecord> list = sixCheckRecordService.selectSixCheckRecordList(sixCheckRecord);
        ExcelUtil<SixCheckRecord> util = new ExcelUtil<SixCheckRecord>(SixCheckRecord.class);
        return util.exportExcel(list, "六必查记录数据");
    }

    /**
     * 新增六必查记录
     */
    @RequiresPermissions("sixcheck:record:add")
    @GetMapping("/add")
    public String add() {
        return prefix + "/add";
    }

    /**
     * 新增保存六必查记录
     */
    @RequiresPermissions("sixcheck:record:add")
    @Log(title = "六必查记录", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(SixCheckRecord sixCheckRecord) {
        return toAjax(sixCheckRecordService.insertSixCheckRecord(sixCheckRecord));
    }

    /**
     * 修改六必查记录
     */
    @RequiresPermissions("sixcheck:record:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap) {
        SixCheckRecord sixCheckRecord = sixCheckRecordService.selectSixCheckRecordById(id);
        mmap.put("sixCheckRecord", sixCheckRecord);
        return prefix + "/edit";
    }

    /**
     * 修改保存六必查记录
     */
    @RequiresPermissions("sixcheck:record:edit")
    @Log(title = "六必查记录", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(SixCheckRecord sixCheckRecord) {
        return toAjax(sixCheckRecordService.updateSixCheckRecord(sixCheckRecord));
    }

    /**
     * 删除六必查记录
     */
    @RequiresPermissions("sixcheck:record:remove")
    @Log(title = "六必查记录", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(sixCheckRecordService.deleteSixCheckRecordByIds(ids));
    }

    @GetMapping("/load")
    @ResponseBody
    public AjaxResult load(@RequestParam("checkDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date checkDate,
            @RequestParam String shift) {
        // 查询本部门的检查项目
        SixCheckItem queryItem = new SixCheckItem();
        queryItem.setDeptId(ShiroUtils.getSysUser().getDeptId());
        List<SixCheckItem> items = sixCheckItemService.selectSixCheckItemList(queryItem);
        items.sort(Comparator.comparing(SixCheckItem::getSortOrder));

        // 查询该日期、该班次已有记录
        SixCheckRecord queryRecord = new SixCheckRecord();
        queryRecord.setCheckDate(checkDate);
        queryRecord.setShift(shift);
        queryRecord.setDeptId(ShiroUtils.getSysUser().getDeptId());
        List<SixCheckRecord> records = sixCheckRecordService.selectSixCheckRecordList(queryRecord);

        Map<Long, String> recordMap = new HashMap<>();
        Map<Long, Long> recordIdMap = new HashMap<>();
        String dutyLeader = "";
        for (SixCheckRecord r : records) {
            recordMap.put(r.getItemId(), r.getRecordValue());
            recordIdMap.put(r.getItemId(), r.getId());
        }
        if (!records.isEmpty()) {
            dutyLeader = records.get(0).getDutyLeader() != null ? records.get(0).getDutyLeader() : "";
        }

        // ========== 构建 kpiScoreMap（原有逻辑） ==========
        String batchNo = new SimpleDateFormat("yyyy-MM").format(checkDate);
        Map<Long, Long> kpiScoreMap = new HashMap<>();
        for (SixCheckRecord r : records) {
            String recordValue = r.getRecordValue();
            if (recordValue != null && recordValue.contains("关联加扣分")) {
                KpiScore scoreQuery = new KpiScore();
                scoreQuery.setSourceRecordId(r.getId());
                scoreQuery.setBatchNo(batchNo);
                List<KpiScore> scoreList = kpiScoreService.selectKpiScoreList(scoreQuery);
                if (!scoreList.isEmpty()) {
                    kpiScoreMap.put(r.getItemId(), scoreList.get(0).getId());
                }
            }
        }

        // ========== 新增：构建扣分明细列表（用于前端显示ID） ==========
        List<Map<String, Object>> detailInfoList = new ArrayList<>();
        for (SixCheckRecord r : records) {
            System.out.println("=== 六必查记录 ID: " + r.getId());
            SixCheckDeductDetail queryDetail = new SixCheckDeductDetail();
            queryDetail.setSixCheckRecordId(r.getId());
            queryDetail.setStatus(1);
            List<SixCheckDeductDetail> details = sixCheckDeductDetailService.selectList(queryDetail);
            System.out.println("=== 查询到明细数量: " + details.size());
            for (SixCheckDeductDetail d : details) {
                Map<String, Object> info = new HashMap<>();
                info.put("detailId", d.getId());
                info.put("deductInfo", d.getDeductInfo());
                info.put("itemId", r.getItemId());
                info.put("status", d.getStatus());
                detailInfoList.add(info);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("items", items);
        result.put("records", recordMap);
        result.put("dutyLeader", dutyLeader);
        result.put("kpiScoreMap", kpiScoreMap);
        result.put("recordIdMap", recordIdMap);
        result.put("detailInfoList", detailInfoList);
        // 在 load 方法中增加查询
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(checkDate);
        String lastUpdateBy = sixCheckRecordService.getLastUpdateBy(dateStr, shift);
        System.out.println("lastUpdateBy is " + lastUpdateBy);
        System.out.println("dateStr is " + dateStr + ", shift is " + shift);
        // 放入返回结果
        result.put("lastUpdateBy", lastUpdateBy != null ? lastUpdateBy : "--");
        return success().put("data", result);
    }

    /**
     * 根据扣分明细ID撤销扣分
     */
    @PostMapping("/cancelByDetail")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult cancelByDetail(@RequestBody Map<String, Object> params) {
        Long detailId = Long.valueOf(params.get("detailId").toString());

        // 1. 查询扣分明细
        SixCheckDeductDetail detail = sixCheckDeductDetailService.selectById(detailId);
        if (detail == null || detail.getStatus() == 0) {
            return error("扣分记录不存在或已撤销");
        }

        // 2. 通过 kpi_score_id 查询 KPI 记录
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

        // 3. 通过 source_record_id 获取六必查记录ID
        Long sixCheckRecordId = kpiScore.getSourceRecordId();
        if (sixCheckRecordId == null) {
            return error("该扣分记录未关联六必查记录，无法撤销");
        }

        // 4. 恢复 KPI 分数 + 移除备注
        // BigDecimal deductScore = extractScore(detail.getDeductInfo());
        BigDecimal deductScore = detail.getDeductScore();
        if (deductScore != null && deductScore.compareTo(BigDecimal.ZERO) != 0) {

            System.out.println("kpiScore.getScore() 原来：" + kpiScore.getScore());
            kpiScore.setScore(kpiScore.getScore().subtract(deductScore));
            System.out.println("kpiScore.getScore() 之后：" + kpiScore.getScore());
        }
        if (StringUtils.isNotBlank(kpiScore.getRemark())) {
            String newRemark = removeDeductInfoByLine(kpiScore.getRemark(), detail.getDeductInfo());
            kpiScore.setRemark(newRemark);
        }
        kpiScore.setUpdateBy(ShiroUtils.getLoginName());
        kpiScoreService.updateKpiScore(kpiScore);
        BigDecimal currentScore = kpiScore.getScore();
        String currentRemark = kpiScore.getRemark();
        if ((currentScore == null || currentScore.compareTo(BigDecimal.ZERO) == 0)
                && StringUtils.isBlank(currentRemark)) {
            kpiScoreService.deleteKpiScoreById(kpiScore.getId());
            System.out.println("=== 删除无效 KPI 记录 id=" + kpiScore.getId());
        }

        // 5. 更新六必查记录
        SixCheckRecord record = sixCheckRecordService.selectSixCheckRecordById(sixCheckRecordId);
        if (record != null) {
            System.out.println("record is " + record.getRecordValue());
            System.out.println("detail.getDeductInfo is " + detail.getDeductInfo());
            String newValue = removeDeductInfoByLine(record.getRecordValue(), detail.getDeductInfo());
            System.out.println("newValue is " + newValue);
            record.setRecordValue(newValue == null ? "正常" : newValue);
            record.setUpdateBy(ShiroUtils.getLoginName());
            sixCheckRecordService.updateSixCheckRecord(record);
        }

        // 6. 标记明细为已撤销
        detail.setStatus(0);
        detail.setUpdateBy(ShiroUtils.getLoginName());
        sixCheckDeductDetailService.update(detail);

        return success("撤销成功");
    }

    @PostMapping("/save")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult save(@RequestBody SixCheckRecordWrapper wrapper) {
        if (wrapper.getDutyLeader() == null || wrapper.getDutyLeader().trim().isEmpty()) {
            return error("值班领导不能为空");
        }

        for (SixCheckRecord r : wrapper.getRecordList()) {
            r.setDeptId(ShiroUtils.getSysUser().getDeptId());
            r.setCheckDate(wrapper.getCheckDate());
            r.setShift(wrapper.getShift());
            r.setDutyLeader(wrapper.getDutyLeader());

            // 检查是否存在相同记录
            SixCheckRecord exist = new SixCheckRecord();
            exist.setItemId(r.getItemId());
            exist.setCheckDate(r.getCheckDate());
            exist.setShift(r.getShift());
            exist.setDeptId(r.getDeptId());
            List<SixCheckRecord> list = sixCheckRecordService.selectSixCheckRecordList(exist);

            if (list.size() > 0) {
                SixCheckRecord update = list.get(0);
                update.setRecordValue(r.getRecordValue());
                update.setDutyLeader(r.getDutyLeader());
                update.setUpdateBy(ShiroUtils.getLoginName());
                sixCheckRecordService.updateSixCheckRecord(update);
            } else {
                r.setCreateBy(ShiroUtils.getLoginName());
                sixCheckRecordService.insertSixCheckRecord(r);
            }
        }
        return success("保存成功");
    }
    // ========== 辅助方法 ==========

    /**
     * 从扣分描述中提取用户名
     */
    private String extractUserName(String deductInfo) {
        Pattern p = Pattern.compile("（关联加扣分-(.+?)-");
        Matcher m = p.matcher(deductInfo);
        if (m.find()) {
            return m.group(1).trim();
        }
        return null;
    }

    /**
     * 从扣分描述中提取分数
     */
    private BigDecimal extractScore(String deductInfo) {
        System.out.println("=== extractScore 输入: " + deductInfo);
        Pattern p = Pattern.compile("(-?\\d+\\.?\\d*)分");
        Matcher m = p.matcher(deductInfo);
        if (m.find()) {
            BigDecimal result = new BigDecimal(m.group(1));
            System.out.println("=== extractScore 输出: " + result);
            return result;
        }
        System.out.println("=== extractScore 输出: 0（解析失败）");
        return BigDecimal.ZERO;
    }

    @RequiresPermissions("sixcheck:summary")
    @GetMapping("/summary")
    public String summary() {
        return "sixcheck/summary";
    }

    /* 日梳理 */
    @GetMapping("/dailySummary")
    @ResponseBody
    public AjaxResult dailySummary(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date checkDate,
            @RequestParam(required = false) Long deptId) {
        if (!ShiroUtils.getSysUser().isAdmin()) {
            deptId = ShiroUtils.getSysUser().getDeptId();
        }

        // 1. 查询该日期所有班次的记录（不过滤正常/异常）
        SixCheckRecord query = new SixCheckRecord();
        query.setCheckDate(checkDate);
        query.setDeptId(deptId);
        List<SixCheckRecord> records = sixCheckRecordService.selectSixCheckRecordList(query);

        // 2. 获取检查项目（本部门优先）
        SixCheckItem queryItem = new SixCheckItem();
        if (deptId != null)
            queryItem.setDeptId(deptId);
        List<SixCheckItem> items = sixCheckItemService.selectSixCheckItemList(queryItem);
        if (items.isEmpty()) {
            items = sixCheckItemService.selectSixCheckItemList(new SixCheckItem());
        }
        items.sort(Comparator.comparing(SixCheckItem::getSortOrder));

        // 3. 按项目分组
        Map<Long, List<SixCheckRecord>> grouped = new LinkedHashMap<>();
        for (SixCheckItem item : items) {
            grouped.put(item.getId(), new ArrayList<>());
        }

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        for (SixCheckRecord r : records) {
            if (grouped.containsKey(r.getItemId())) {
                grouped.get(r.getItemId()).add(r);
            }
        }

        // 4. 组装返回数据（与月汇总完全一致）
        List<Map<String, Object>> result = new ArrayList<>();
        for (SixCheckItem item : items) {
            List<SixCheckRecord> recs = grouped.get(item.getId());
            if (recs.isEmpty()) {
                // 当天没有该项目的记录，视为正常（但一般都会有记录，因为录入时每个项目都会有）
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("itemName", item.getName());
                List<Map<String, String>> detailList = new ArrayList<>();
                // 可以放一条正常记录，或直接空列表
                // 但为了显示“正常”，我们可以放一条内容为“正常”的占位记录
                Map<String, String> normal = new HashMap<>();
                normal.put("shift", "全部班次");
                normal.put("leader", "");
                normal.put("content", "正常");
                normal.put("time", "");
                detailList.add(normal);
                itemMap.put("details", detailList);
                result.add(itemMap);
                continue;
            }

            // 判断是否全部为正常
            boolean allNormal = recs.stream().allMatch(r -> {
                String val = r.getRecordValue();
                return val == null || val.trim().isEmpty() || val.trim().equals("正常");
            });

            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("itemName", item.getName());
            List<Map<String, String>> detailList = new ArrayList<>();

            if (allNormal) {
                // 全部正常，只显示一条“正常”
                Map<String, String> normal = new HashMap<>();
                normal.put("shift", "全部班次");
                normal.put("leader", "");
                normal.put("content", "正常");
                normal.put("time", "");
                detailList.add(normal);
            } else {
                // 有异常，列出所有异常记录（正常记录也可以列出，但只显示异常即可）
                for (SixCheckRecord r : recs) {
                    String val = r.getRecordValue();
                    if (val != null && !val.trim().isEmpty() && !val.trim().equals("正常")) {
                        Map<String, String> detail = new HashMap<>();
                        detail.put("shift", r.getShift() != null ? r.getShift() : "");
                        detail.put("leader", r.getDutyLeader() != null ? r.getDutyLeader() : "");
                        detail.put("content", val);
                        detail.put("time", sdf.format(r.getCreateTime()));
                        detailList.add(detail);
                    }
                }
            }
            itemMap.put("details", detailList);
            result.add(itemMap);
        }

        return success().put("data", result);
    }

    /* 月汇总 */
    @RequiresPermissions("sixcheck:summary")
    @GetMapping("/summary/data")
    @ResponseBody
    public AjaxResult summaryData(@RequestParam String month,
            @RequestParam(required = false) Long deptId) {
        if (!ShiroUtils.getSysUser().isAdmin()) {
            deptId = ShiroUtils.getSysUser().getDeptId();
        }

        List<SixCheckRecord> records = sixCheckRecordService.selectListByMonth(month, deptId);

        // 获取检查项目（本部门优先）
        SixCheckItem queryItem = new SixCheckItem();
        if (deptId != null)
            queryItem.setDeptId(deptId);
        List<SixCheckItem> items = sixCheckItemService.selectSixCheckItemList(queryItem);
        if (items.isEmpty()) {
            items = sixCheckItemService.selectSixCheckItemList(new SixCheckItem());
        }
        items.sort(Comparator.comparing(SixCheckItem::getSortOrder));

        // 按项目分组，并过滤异常记录
        Map<Long, List<SixCheckRecord>> grouped = new LinkedHashMap<>();
        for (SixCheckItem item : items) {
            grouped.put(item.getId(), new ArrayList<>());
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        for (SixCheckRecord r : records) {
            String val = r.getRecordValue();
            if (val != null && !val.trim().isEmpty() && !val.trim().equals("正常")) {
                if (grouped.containsKey(r.getItemId())) {
                    grouped.get(r.getItemId()).add(r);
                }
            }
        }

        // 组装返回数据
        List<Map<String, Object>> result = new ArrayList<>();
        for (SixCheckItem item : items) {
            List<SixCheckRecord> recs = grouped.get(item.getId());
            // 无异常的项目跳过
            if (recs.isEmpty())
                continue;

            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("itemName", item.getName());
            List<Map<String, String>> detailList = new ArrayList<>();
            for (SixCheckRecord rec : recs) {
                Map<String, String> detail = new HashMap<>();
                detail.put("shift", rec.getShift() != null ? rec.getShift() : "");
                detail.put("leader", rec.getDutyLeader() != null ? rec.getDutyLeader() : "");
                detail.put("content", rec.getRecordValue());
                detail.put("date", sdf.format(rec.getCheckDate()));
                detailList.add(detail);
            }
            itemMap.put("details", detailList);
            result.add(itemMap);
        }

        return success().put("data", result);
    }

    // ============================================================
    // 辅助方法
    // ============================================================

    // ========== 辅助方法 ==========

    /**
     * 从 recordValue 中提取扣分描述
     * 格式：（关联加扣分-用户名-分数-备注）
     * 或：（关联加扣分-用户名-备注）
     */
    private String extractDeductInfo(String recordValue) {
        if (recordValue == null)
            return null;
        // 匹配 （关联加扣分-...） 格式
        Pattern p = Pattern.compile("（关联加扣分-[^）]*）");
        Matcher m = p.matcher(recordValue);
        if (m.find()) {
            return m.group();
        }
        return null;
    }

    private String removeDeductInfoByLine(String content, String deductInfo) {
        if (StringUtils.isBlank(content))
            return null;
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
        return StringUtils.isBlank(result) ? null : result;
    }
}
