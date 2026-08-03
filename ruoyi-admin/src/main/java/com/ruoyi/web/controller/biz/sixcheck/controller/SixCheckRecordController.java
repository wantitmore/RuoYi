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
        String dutyLeader = "";
        for (SixCheckRecord r : records) {
            recordMap.put(r.getItemId(), r.getRecordValue());
        }
        if (!records.isEmpty()) {
            dutyLeader = records.get(0).getDutyLeader() != null ? records.get(0).getDutyLeader() : "";
        }

        Map<String, Object> result = new HashMap<>();
        result.put("items", items);
        result.put("records", recordMap);
        result.put("dutyLeader", dutyLeader);
        return success().put("data", result);
    }

    @RequiresPermissions("sixcheck:record:edit")
    @PostMapping("/save")
    @ResponseBody
    public AjaxResult save(@RequestBody SixCheckRecordWrapper wrapper) {
        if (wrapper.getDutyLeader() == null || wrapper.getDutyLeader().trim().isEmpty()) {
            return error("值班领导不能为空");
        }
        for (SixCheckRecord r : wrapper.getRecordList()) {
            r.setDeptId(ShiroUtils.getSysUser().getDeptId());
            r.setCheckDate(wrapper.getCheckDate());
            r.setShift(wrapper.getShift());
            r.setDutyLeader(wrapper.getDutyLeader());

            // 检查是否存在相同 item_id + check_date + shift + dept_id 的记录
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
    @RequiresPermissions("sixcheck:record:edit")
    @PostMapping("/cancelDeduct")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult cancelDeduct(@RequestBody Map<String, Object> params) {
        Long itemId = Long.valueOf(params.get("itemId").toString()); // 考核项目ID
        String checkDate = params.get("checkDate").toString();
        String shift = params.get("shift").toString();
        String deductInfo = params.get("deductInfo").toString();
        Long deptId = ShiroUtils.getSysUser().getDeptId();
        Long userId = ShiroUtils.getUserId(); // 当前登录用户即被考核人

        // 1. 从扣分描述中解析分数（不再依赖用户名）
        Pattern p = Pattern.compile("（关联加扣分-.+?-(.+?)分-.+?）");
        Matcher m = p.matcher(deductInfo);
        BigDecimal deductScore = BigDecimal.ZERO;
        if (m.find()) {
            deductScore = new BigDecimal(m.group(1).trim());
        } else {
            return error("扣分描述格式错误，无法解析分数");
        }

        // 2. 更新六必查记录（移除扣分描述）—— 使用前端额外传的 sixCheckItemId
        Long sixCheckItemId = Long.valueOf(params.getOrDefault("sixCheckItemId", itemId).toString());
        SixCheckRecord query = new SixCheckRecord();
        query.setItemId(sixCheckItemId);
        query.setCheckDate(DateUtils.parseDate(checkDate));
        query.setShift(shift);
        query.setDeptId(deptId);
        List<SixCheckRecord> records = sixCheckRecordService.selectSixCheckRecordList(query);
        if (!records.isEmpty()) {
            SixCheckRecord exist = records.get(0);
            String oldValue = exist.getRecordValue();
            if (oldValue != null && oldValue.contains(deductInfo)) {
                String newValue = oldValue.replace("\n" + deductInfo, "").replace(deductInfo, "");
                exist.setRecordValue(newValue.trim().isEmpty() ? "正常" : newValue.trim());
                exist.setUpdateBy(ShiroUtils.getLoginName());
                sixCheckRecordService.updateSixCheckRecord(exist);
            }
        }

        // 3. 更新考核打分记录：userId + 考核项目ID + 月份 精准定位
        String batchNo = DateUtils.parseDateToStr("yyyy-MM", DateUtils.parseDate(checkDate));
        KpiScore existQuery = new KpiScore();
        existQuery.setUserId(userId);
        existQuery.setItemId(itemId);
        existQuery.setBatchNo(batchNo);
        List<KpiScore> scoreList = kpiScoreService.selectKpiScoreList(existQuery);

        if (!scoreList.isEmpty()) {
            KpiScore existScore = scoreList.get(0);
            // 减去分数（扣分为负，撤销时 subtract 负值 = 加回正分）
            BigDecimal oldScore = existScore.getScore() != null ? existScore.getScore() : BigDecimal.ZERO;
            existScore.setScore(oldScore.subtract(deductScore));

            // 移除备注中对应的扣分描述
            String oldRemark = existScore.getRemark() != null ? existScore.getRemark() : "";
            String newRemark = oldRemark.replace("；" + deductInfo, "").replace(deductInfo, "");
            newRemark = newRemark.replace(";；", ";").replace("\n\n", "\n").trim();
            existScore.setRemark(newRemark);
            existScore.setUpdateBy(ShiroUtils.getLoginName());

            // 如果分数归零且备注为空，删除记录；否则更新
            if (existScore.getScore().compareTo(BigDecimal.ZERO) == 0 && newRemark.isEmpty()) {
                kpiScoreService.deleteKpiScoreByIds(existScore.getId().toString());
            } else {
                kpiScoreService.updateKpiScore(existScore);
            }
        }

        return success("撤销成功");
    }
}
