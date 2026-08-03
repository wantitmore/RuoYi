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
    Long itemId = Long.valueOf(params.get("itemId").toString());
    String checkDate = params.get("checkDate").toString();
    String shift = params.get("shift").toString();
    String deductInfo = params.get("deductInfo").toString();
    Long deptId = ShiroUtils.getSysUser().getDeptId();

    // ========== 1. 更新六必查记录（移除扣分描述） ==========
    SixCheckRecord query = new SixCheckRecord();
    query.setItemId(itemId);
    query.setCheckDate(DateUtils.parseDate(checkDate));
    query.setShift(shift);
    query.setDeptId(deptId);
    List<SixCheckRecord> records = sixCheckRecordService.selectSixCheckRecordList(query);

    if (!records.isEmpty()) {
        SixCheckRecord exist = records.get(0);
        String oldValue = exist.getRecordValue();
        if (oldValue != null && oldValue.contains(deductInfo)) {
            String newValue = oldValue.replace("\n" + deductInfo, "").replace(deductInfo, "");
            if (newValue.trim().isEmpty()) {
                newValue = "正常";
            }
            exist.setRecordValue(newValue);
            exist.setUpdateBy(ShiroUtils.getLoginName());
            sixCheckRecordService.updateSixCheckRecord(exist);
        }
    }

    // ========== 2. 解析扣分描述，获取用户名、分数、备注 ==========
    String userName = null;
    BigDecimal deductScore = BigDecimal.ZERO;
    String remark = null;

    // 匹配格式：（关联加扣分-用户名-分数-备注）
    java.util.regex.Pattern p1 = java.util.regex.Pattern.compile("（关联加扣分-(.+?)-(\\d+\\.?\\d*)分-(.+)）");
    java.util.regex.Matcher m1 = p1.matcher(deductInfo);
    if (m1.find()) {
        userName = m1.group(1).trim();
        deductScore = new BigDecimal(m1.group(2));
        remark = m1.group(3).trim();
    } else {
        // 匹配格式：（关联加扣分-用户名-备注） 无分数
        java.util.regex.Pattern p2 = java.util.regex.Pattern.compile("（关联加扣分-(.+?)-(.+)）");
        java.util.regex.Matcher m2 = p2.matcher(deductInfo);
        if (m2.find()) {
            userName = m2.group(1).trim();
            remark = m2.group(2).trim();
        }
    }

    if (userName == null) {
        return error("扣分描述格式错误，无法解析");
    }

    // ========== 3. 更新考核打分记录 ==========
    // 根据用户名查找用户
    SysUser queryUser = new SysUser();
    queryUser.setUserName(userName);
    List<SysUser> userList = userService.selectUserList(queryUser);
    if (userList.isEmpty()) {
        return error("用户不存在：" + userName);
    }
    Long userId = userList.get(0).getUserId();

    // 生成月份字符串（YYYY-MM）
    String batchNo = DateUtils.parseDateToStr("yyyy-MM", DateUtils.parseDate(checkDate));

    // 查询该用户、该月份、该部门的考核记录
    KpiScore scoreQuery = new KpiScore();
    scoreQuery.setUserId(userId);
    scoreQuery.setBatchNo(batchNo);
    List<KpiScore> scoreList = kpiScoreService.selectKpiScoreList(scoreQuery);

    // 找到备注中包含该扣分描述的那条记录
    for (KpiScore score : scoreList) {
        if (score.getRemark() != null && score.getRemark().contains(deductInfo)) {
            // 扣掉分数（deductScore 是扣分，为负数，加上它就是扣减）
            if (score.getScore() != null) {
                score.setScore(score.getScore().add(deductScore));
            }
            // 从备注中移除扣分描述
            String newRemark = score.getRemark().replace(deductInfo, "").trim();
            if (newRemark.isEmpty()) {
                newRemark = null;
            }
            score.setRemark(newRemark);
            score.setUpdateBy(ShiroUtils.getLoginName());
            kpiScoreService.updateKpiScore(score);
            break;
        }
    }

    return success("撤销成功");
}
}
