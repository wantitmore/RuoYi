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

import org.apache.shiro.authz.annotation.RequiresPermissions;
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
import com.ruoyi.web.controller.biz.sixcheck.domain.SixCheckItem;
import com.ruoyi.web.controller.biz.sixcheck.domain.SixCheckRecord;
import com.ruoyi.web.controller.biz.sixcheck.domain.SixCheckRecordWrapper;
import com.ruoyi.web.controller.biz.sixcheck.service.ISixCheckItemService;
import com.ruoyi.web.controller.biz.sixcheck.service.ISixCheckRecordService;

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
        System.out.println("=== 查询条件: checkDate=" + queryRecord.getCheckDate()
                + ", shift=" + queryRecord.getShift()
                + ", deptId=" + queryRecord.getDeptId());

        Map<Long, String> recordMap = new HashMap<>();
        String dutyLeader = "";
        for (SixCheckRecord r : records) {
            recordMap.put(r.getItemId(), r.getRecordValue());
            System.out.println("getRemark is " + r.getRemark() + ", getRecordValue " + r.getRecordValue());
        }
        if (!records.isEmpty()) {
            dutyLeader = records.get(0).getDutyLeader() != null ? records.get(0).getDutyLeader() : "";
        }

        // ========== 新增：构建 kpiScoreMap ==========
        String batchNo = new SimpleDateFormat("yyyy-MM").format(checkDate);

        Map<Long, Long> kpiScoreMap = new HashMap<>();
        for (SixCheckRecord r : records) {
            String recordValue = r.getRecordValue();
            // 只处理包含扣分信息的记录
            if (recordValue != null && recordValue.contains("关联加扣分")) {
                // 查询关联的 kpi_score.id
                
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
        result.put("dutyLeader", dutyLeader);
        result.put("kpiScoreMap", kpiScoreMap);
        return success().put("data", result);
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

    /**
     * 撤销关联扣分：清除六必查记录中的扣分描述，并删除考核打分中的扣分记录
     */
    /**
     * 撤销扣分
     * 通过六必查记录ID，反向恢复KPI分数
     */
    /**
     * 撤销扣分（仅限六必查关联扣分）
     * 支持两种入参方式：
     * 1. 传 kpiScoreId：直接通过KPI记录ID撤销
     * 2. 传 recordId：通过六必查记录ID撤销
     */
    @PostMapping("/cancelDeduct")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult cancelDeduct(@RequestBody Map<String, Object> params) {
        try {
            // ========== 1. 获取参数 ==========
            Long kpiScoreId = null;
            if (params.get("kpiScoreId") != null) {
                kpiScoreId = Long.valueOf(params.get("kpiScoreId").toString());
                String deductInfo = params.get("deductInfo").toString();
        System.out.println("=== 撤销参数: kpiScoreId=" + kpiScoreId + ", deductInfo=" + deductInfo);
            }
            if (kpiScoreId == null) {
                return error("参数错误：缺少kpiScoreId");
            }

            String deductInfo = params.get("deductInfo") != null ? params.get("deductInfo").toString() : null;
            if (deductInfo == null || deductInfo.isEmpty()) {
                return error("参数错误：缺少扣分描述");
            }

            SysUser currentUser = ShiroUtils.getSysUser();
            String loginName = currentUser.getLoginName();

            System.out.println("=== 开始撤销扣分，kpiScoreId=" + kpiScoreId + ", 操作人=" + loginName);

            // ========== 2. 查询 KPI 记录 ==========
            KpiScore kpiScore = kpiScoreService.selectKpiScoreById(kpiScoreId);
            if (kpiScore == null) {
                return error("未找到考核记录，kpiScoreId=" + kpiScoreId);
            }
             System.out.println("=== 查询到 KPI 记录: id=" + kpiScore.getId()
              + ", score=" + kpiScore.getScore() + ", remark=" + kpiScore.getRemark());

            // ========== 3. 校验：只有六必查关联扣分才能撤销 (去掉校验)==========

            // ========== 4. 幂等性校验 ==========
            String remark = kpiScore.getRemark();
            System.out.println("remark is " + remark + ", deductInfo is " + deductInfo);
            if (remark == null || !remark.contains(deductInfo)) {
                System.out.println("=== 幂等性校验失败: remark 为空或不包含 deductInfo");
            return error("该扣分记录已撤销或不存在");
            }

            // ========== 5. 恢复分数 ==========
            BigDecimal deductScore = extractScore(deductInfo);
            System.out.println("=== 解析 deductScore: " + deductScore);
            if (deductScore.compareTo(BigDecimal.ZERO) == 0) {
                return error("无法解析扣分分数");
            }
            BigDecimal originalScore = kpiScore.getScore();
            BigDecimal newScore = originalScore.add(deductScore);
            System.out.println("=== 分数变化: 原始=" + originalScore + ", deductScore=" + deductScore + ", 计算后新分数=" + newScore);

            // ========== 6. 移除扣分描述 ==========
            System.out.println("remark : " + remark + ", deductInfo " + deductInfo);
            String newRemark = remark.replace(deductInfo, "").trim();
            System.out.println("=== 移除前 remark: " + remark);
        System.out.println("=== 移除后 newRemark: " + newRemark);
            kpiScore.setRemark(newRemark.isEmpty() ? null : newRemark);
            kpiScore.setScore(newRemark.isEmpty() ? null : newScore); 
            System.out.println("new remark is " + newRemark);
            kpiScore.setUpdateBy(loginName);
            kpiScoreService.updateKpiScore(kpiScore);
             System.out.println("=== KPI 更新后: score=" + kpiScore.getScore() + ", remark=" + kpiScore.getRemark());

            // ========== 7. 更新六必查记录（如果存在） ==========
            Long recordId = kpiScore.getSourceRecordId();
            System.out.println("=== source_record_id=" + recordId);
            System.out.println("recordId: " + recordId);
            if (recordId != null) {
                SixCheckRecord record = sixCheckRecordService.selectSixCheckRecordById(recordId);
                if (record != null) {
                    String recordValue = record.getRecordValue();
                    System.out.println("=== 六必查原始 recordValue: " + recordValue);
                    if (recordValue != null && recordValue.contains(deductInfo)) {
                        String newRecordValue = recordValue.replace(deductInfo, "").trim();
                        System.out.println("=== 六必查新 recordValue: " + newRecordValue);
                        record.setRecordValue(newRecordValue.isEmpty() ? "正常" : newRecordValue);
                        record.setUpdateBy(loginName);
                        sixCheckRecordService.updateSixCheckRecord(record);
                    } else{
                        System.out.println("=== 六必查 recordValue 不包含 deductInfo，跳过更新");
                    }
                }
            }

            System.out.println("=== 撤销成功：kpiScoreId=" + kpiScoreId
                    + ", 恢复分数=" + deductScore.abs()
                    + ", 新分数=" + newScore);

            return success("撤销成功");

        } catch (NumberFormatException e) {
            System.err.println("=== 撤销异常: " + e.getMessage());
            return error("参数格式错误");
        } catch (Exception e) {
            System.err.println("撤销扣分失败 " + e.getMessage());
            return error("撤销失败：" + e.getMessage());
        }
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
}
