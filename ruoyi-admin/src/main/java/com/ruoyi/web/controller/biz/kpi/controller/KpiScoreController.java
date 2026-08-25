package com.ruoyi.web.controller.biz.kpi.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

// import com.mysql.cj.result.Row;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.web.controller.biz.kpi.domain.KpiItem;
import com.ruoyi.web.controller.biz.kpi.domain.KpiScore;
import com.ruoyi.web.controller.biz.kpi.domain.KpiScoreDetailVo;
import com.ruoyi.web.controller.biz.kpi.domain.QuickDeductDTO;
import com.ruoyi.web.controller.biz.kpi.service.IKpiItemService;
import com.ruoyi.web.controller.biz.kpi.service.IKpiScoreService;
import com.ruoyi.web.controller.biz.notice.WarningNoticeService;
import com.ruoyi.web.controller.biz.sixcheck.domain.SixCheckDeductDetail;
import com.ruoyi.web.controller.biz.sixcheck.domain.SixCheckItem;
import com.ruoyi.web.controller.biz.sixcheck.domain.SixCheckRecord;
import com.ruoyi.web.controller.biz.sixcheck.service.ISixCheckDeductDetailService;
import com.ruoyi.web.controller.biz.sixcheck.service.ISixCheckItemService;
import com.ruoyi.web.controller.biz.sixcheck.service.ISixCheckRecordService;
import com.ruoyi.web.controller.biz.video.domain.VideoPlaybackRecord;
import com.ruoyi.web.controller.biz.video.service.IVideoPlaybackRecordService;

import jakarta.servlet.http.HttpServletResponse;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.SysPost;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.system.service.ISysPostService;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.web.bind.annotation.RequestBody;
import com.ruoyi.web.controller.biz.kpi.domain.ScoreSummary;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.web.controller.biz.sixcheck.domain.SixCheckRecord;
import com.ruoyi.web.controller.biz.sixcheck.service.ISixCheckRecordService;

/**
 * 考核分数Controller
 * 
 * @author ruoyi
 * @date 2026-06-07
 */
@Controller
@RequestMapping("/kpi/score")
public class KpiScoreController extends BaseController {
    private String prefix = "kpi/score";

    @Autowired
    private IKpiScoreService kpiScoreService;

    @Autowired
    private ISysDeptService deptService; // 若依系统部门服务

    @Autowired
    private IKpiItemService kpiItemService; // 考核项目服务

    @Autowired
    private ISysUserService userService; // 若依系统用户服务

    @Autowired
    private ISysPostService postService;

    @Autowired
    private ISixCheckRecordService sixCheckRecordService;

    @Autowired
    private ISixCheckItemService sixCheckItemService;

    @Autowired
    private IVideoPlaybackRecordService videoPlaybackRecordService;

    @Autowired
    private ISixCheckDeductDetailService sixCheckDeductDetailService;

    @Autowired
    private WarningNoticeService warningNoticeService;

    @RequiresPermissions("kpi:score:view")
    @GetMapping()
    public String score() {
        return prefix + "/score";
    }

    /**
     * 查询考核分数列表
     */
    @RequiresPermissions("kpi:score:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(KpiScore kpiScore) {
        startPage();
        List<KpiScore> list = kpiScoreService.selectKpiScoreList(kpiScore);
        // 补充用户名和项目名
        for (KpiScore s : list) {
            SysUser user = userService.selectUserById(s.getUserId());
            if (user != null) {
                s.setUserName(user.getUserName());
            }
            KpiItem item = kpiItemService.selectKpiItemById(s.getItemId());
            if (item != null) {
                s.setItemName(item.getName());
            }
        }
        return getDataTable(list);
    }

    /**
     * 导出考核分数列表
     */
    @RequiresPermissions("kpi:score:export")
    @Log(title = "考核分数", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(KpiScore kpiScore) {
        List<KpiScore> list = kpiScoreService.selectKpiScoreList(kpiScore);
        ExcelUtil<KpiScore> util = new ExcelUtil<KpiScore>(KpiScore.class);
        return util.exportExcel(list, "考核分数数据");
    }

    /**
     * 新增考核分数
     */
    @RequiresPermissions("kpi:score:add")
    @GetMapping("/add")
    public String add() {
        return prefix + "/add";
    }

    /**
     * 新增保存考核分数
     */
    @RequiresPermissions("kpi:score:add")
    @Log(title = "考核分数", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(KpiScore kpiScore) {
        return toAjax(kpiScoreService.insertKpiScore(kpiScore));
    }

    /**
     * 修改考核分数
     */
    @RequiresPermissions("kpi:score:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap) {
        KpiScore kpiScore = kpiScoreService.selectKpiScoreById(id);
        mmap.put("kpiScore", kpiScore);
        return prefix + "/edit";
    }

    /**
     * 修改保存考核分数
     */
    @RequiresPermissions("kpi:score:edit")
    @Log(title = "考核分数", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(KpiScore kpiScore) {
        return toAjax(kpiScoreService.updateKpiScore(kpiScore));
    }

    /**
     * 删除考核分数
     */
    @RequiresPermissions("kpi:score:remove")
    @Log(title = "考核分数", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(kpiScoreService.deleteKpiScoreByIds(ids));
    }

    @RequiresPermissions("kpi:score:input")
    @GetMapping("/input")
    public String input(ModelMap mmap) {
        // 获取当前用户
        SysUser user = ShiroUtils.getSysUser();
        // 如果不是超级管理员，自动填充其所在部门
        if (!user.isAdmin()) {
            mmap.put("currentDeptId", user.getDeptId());
        } else {
            mmap.put("currentDeptId", null); // 超级管理员不自动选中
        }
        System.out.println("===== 当前用户：" + user.getUserName() + "，部门ID：" + user.getDeptId());
        // 部门列表仍然需要
        List<SysDept> depts = deptService.selectDeptList(new SysDept());
        mmap.put("depts", depts);
        mmap.put("categoryReqMap", kpiItemService.selectCategoryRequirementMap());
        return "kpi/score/input";
    }

    // AJAX：根据部门ID加载该部门的考核项目
    @GetMapping("/getItemsByDept")
    @ResponseBody
    public AjaxResult getItemsByDept(Long deptId) {
        KpiItem item = new KpiItem();
        item.setDeptId(deptId);
        List<KpiItem> list = kpiItemService.selectKpiItemList(item);
        return success().put("data", list);
    }

    // AJAX：根据部门ID加载人员
    @GetMapping("/getUsersByDept")
    @ResponseBody
    public AjaxResult getUsersByDept(Long deptId) {
        SysUser user = new SysUser();
        user.setDeptId(deptId);
        List<SysUser> list = userService.selectUserList(user);
        return success().put("data", list);
    }

    // AJAX：批量保存分数
    @RequiresPermissions("kpi:score:edit")
    @PostMapping("/saveBatch")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult saveBatch(@RequestBody List<KpiScore> scoreList) {
        if (scoreList == null || scoreList.isEmpty()) {
            return error("数据为空");
        }
        String batchNo = scoreList.get(0).getBatchNo();
        if (StringUtils.isBlank(batchNo)) {
            return error("批次号不能为空");
        }

        Long deptId = ShiroUtils.getSysUser().getDeptId();
        if (deptId == null) {
            return error("无法获取部门信息");
        }

        List<Long> hasRecordUserIds = kpiScoreService.selectUserIdsByBatchNoAndDept(deptId, batchNo);
        Map<String, KpiScore> existMap = new HashMap<>();
        if (!hasRecordUserIds.isEmpty()) {
            KpiScore queryCondition = new KpiScore();
            queryCondition.setBatchNo(batchNo);
            queryCondition.setUserIds(hasRecordUserIds); // 临时字段
            List<KpiScore> allExist = kpiScoreService.selectKpiScoreList(queryCondition);
            existMap = allExist.stream()
                    .collect(Collectors.toMap(
                            e -> e.getUserId() + "_" + e.getItemId(),
                            Function.identity()));
        }

        Set<Long> affectedUserIds = new HashSet<>();
        for (KpiScore score : scoreList) {
            String key = score.getUserId() + "_" + score.getItemId();
            KpiScore exist = existMap.get(key);

            if (exist != null) {
                // 已有记录
                if (score.getScore() == null) {
                    // 用户清空了分数 → 删除已有记录
                    kpiScoreService.deleteKpiScoreByIds(exist.getId().toString());
                } else {
                    // 有分数（含0） → 更新
                    exist.setScore(score.getScore());
                    exist.setRemark(score.getRemark());
                    exist.setUpdateBy(ShiroUtils.getLoginName());
                    kpiScoreService.updateKpiScore(exist);
                }
                affectedUserIds.add(score.getUserId());
            } else {
                // 无记录
                if (score.getScore() != null) {
                    // 有分数（含0） → 插入
                    score.setCreateBy(ShiroUtils.getLoginName());
                    score.setRemark(score.getRemark());
                    kpiScoreService.insertKpiScore(score);
                    affectedUserIds.add(score.getUserId());
                }
                // 无记录且分数为null → 什么都不做
            }
        }

        // 预警检查（保持不变）
        for (Long userId : affectedUserIds) {
            checkAndSendWarning(userId, batchNo);
        }

        return success("保存成功");
    }

    @GetMapping("/loadScoreTable")
    @ResponseBody
    public AjaxResult loadScoreTable(Long deptId, String batchNo,
            @RequestParam(required = false) String category) {
        // 1. 查询该部门的考核项目
        KpiItem queryItem = new KpiItem();
        queryItem.setDeptId(deptId);
        List<KpiItem> items = kpiItemService.selectKpiItemList(queryItem);
        // 临时硬编码过滤验证
        if (category != null && !category.isEmpty()) {
            items = items.stream()
                    .filter(i -> category.equals(i.getCategory()))
                    .collect(Collectors.toList());
        }
        if (category != null && !category.isEmpty()) {
            queryItem.setCategory(category);
        }
        items.sort(Comparator.comparing(KpiItem::getCategory));

        // 2. 查询该部门的人员
        SysUser queryUser = new SysUser();
        queryUser.setDeptId(deptId);
        List<SysUser> users = userService.selectUserList(queryUser);

        // 3. 查询该批次下已保存的分数
        KpiScore queryScore = new KpiScore();
        queryScore.setBatchNo(batchNo);
        List<KpiScore> existingScores = kpiScoreService.selectKpiScoreList(queryScore);
        // 转成 Map<"userId_itemId", score> 方便前端查找
        Map<String, Map<String, Object>> scoreMap = new HashMap<>();
        for (KpiScore s : existingScores) {
            if (/* StringUtils.isNotBlank(s.getRemark()) && */s.getScore() != null) {
                Map<String, Object> detail = new HashMap<>();
                detail.put("score", s.getScore() != null ? s.getScore() : 0);
                detail.put("remark", s.getRemark() != null ? s.getRemark() : "");
                scoreMap.put(s.getUserId() + "_" + s.getItemId(), detail);
            }
        }

        // 4. 组装返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("items", items);
        result.put("users", users);
        result.put("scoreMap", scoreMap);
        result.put("canEdit", ShiroUtils.getSubject().isPermitted("kpi:score:edit"));
        return success().put("data", result);
    }

    // 返回页面的方法（访问地址：/kpi/score/summary）
    @RequiresPermissions("kpi:score:summary")
    @GetMapping("/summary")
    public String summaryPage(ModelMap mmap) {
        SysUser user = ShiroUtils.getSysUser();

        // 传入当前用户是否为超级管理员
        mmap.put("isAdmin", user.isAdmin());

        // 如果不是管理员，传入当前部门信息
        if (!user.isAdmin()) {
            mmap.put("currentDeptId", user.getDeptId());
            mmap.put("currentDeptName", user.getDept().getDeptName());
        }
        List<SysDept> depts = deptService.selectDeptList(new SysDept());
        mmap.put("depts", depts);
        List<SysPost> posts = postService.selectPostList(new SysPost());
        mmap.put("posts", posts);
        return "kpi/score/summary";
    }

    @GetMapping("/personDetail")
    @ResponseBody
    public AjaxResult personDetail(@RequestParam String startMonth,
            @RequestParam String endMonth,
            @RequestParam Long userId) {
        // 生成区间月份列表
        List<String> months = getMonthsBetween(startMonth, endMonth);
        if (months.isEmpty()) {
            return error("月份区间无效");
        }

        // 查询该用户在区间内所有考核记录
        List<KpiScore> list = kpiScoreService.selectByMonths(userId, months);

        List<Map<String, Object>> details = new ArrayList<>();
        for (KpiScore score : list) {
            Map<String, Object> item = new HashMap<>();
            KpiItem kpiItem = kpiItemService.selectKpiItemById(score.getItemId());
            item.put("itemName", kpiItem != null ? kpiItem.getName() : "未知项目");
            item.put("score", score.getScore());
            item.put("remark", score.getRemark() != null ? score.getRemark() : "");
            item.put("batchNo", score.getBatchNo());
            details.add(item);
        }
        return success().put("data", details);
    }

    private void checkAndSendWarning(Long userId, String batchNo) {
        BigDecimal decutScore = kpiScoreService.calcTotalScoreMinus100(userId, batchNo);
        // 阈值：总分 - 100 <= -103 即扣分≥3分（如果存储的是变化量，则阈值应为 -103）
        if (decutScore != null && decutScore.compareTo(new BigDecimal("-3")) <= 0) {
            warningNoticeService.sendDeductWarning(userId, batchNo, decutScore);
        }
    }

    @RequiresPermissions("kpi:score:summary")
    @GetMapping("/summary/export")
    @ResponseBody
    public void exportSummary(HttpServletResponse response,
            @RequestParam String startMonth,
            @RequestParam String endMonth,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) Long postId) { // 新增 postId
        SysUser currentUser = ShiroUtils.getSysUser();
        // 如果不是管理员，强制使用当前用户的部门
        if (!currentUser.isAdmin()) {
            deptId = currentUser.getDeptId();
        }
        List<String> months = getMonthsBetween(startMonth, endMonth);
        List<ScoreSummary> list = kpiScoreService.selectAvgSummary(months, deptId, postId);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setRank(i + 1);
            list.get(i).setTotalScore(list.get(i).getTotalScore());
        }
        ExcelUtil<ScoreSummary> util = new ExcelUtil<>(ScoreSummary.class);
        util.exportExcel(response, list, "考核结果");
    }

    @GetMapping("/summary/avg")
    @ResponseBody
    public AjaxResult avgSummary(@RequestParam String startMonth,
            @RequestParam String endMonth,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) Long postId) {
        if (!ShiroUtils.getSysUser().isAdmin()) {
            deptId = ShiroUtils.getSysUser().getDeptId();
        }

        // 生成区间月份列表
        List<String> months = getMonthsBetween(startMonth, endMonth);
        if (months.isEmpty()) {
            return error("开始月份不能大于结束月份");
        }

        List<ScoreSummary> list = kpiScoreService.selectAvgSummary(months, deptId, postId);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setRank(i + 1);
            // totalScore 存储的是平均分，保留两位小数
            list.get(i).setTotalScore(Math.round(list.get(i).getTotalScore() * 100.0) / 100.0);
        }
        return success().put("data", list);
    }

    // 辅助方法：生成区间月份列表
    private List<String> getMonthsBetween(String start, String end) {
        List<String> months = new ArrayList<>();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(start));
            Date endDate = sdf.parse(end);

            while (!cal.getTime().after(endDate)) {
                months.add(sdf.format(cal.getTime()));
                cal.add(Calendar.MONTH, 1);
            }
        } catch (ParseException e) {
            // 格式错误
        }
        return months;
    }

    @RequiresPermissions("kpi:score:edit")
    @PostMapping("/quickDeduct")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult quickDeduct(@RequestBody QuickDeductDTO dto) {
        // ---------- 1. 基础参数校验 ----------
        if (dto.getUserId() == null || dto.getItemId() == null || dto.getScore() == null) {
            return error("缺少必填参数：userId, itemId, score");
        }
        if (StringUtils.isBlank(dto.getCheckDate()) || dto.getCheckDate().length() < 7) {
            return error("检查日期不能为空且格式必须为 yyyy-MM");
        }
        if (dto.getSixCheckItemId() == null) {
            return error("六必查项目ID不能为空");
        }

        // ---------- 2. 查询用户，判空 ----------
        SysUser user = userService.selectUserById(dto.getUserId());
        if (user == null) {
            return error("用户ID " + dto.getUserId() + " 不存在");
        }
        String userName = user.getUserName();

        // ---------- 3. 生成批次号（月份） ----------
        String batchNo;
        try {
            batchNo = dto.getCheckDate().substring(0, 7);
        } catch (Exception e) {
            return error("日期格式错误，请传入 yyyy-MM-dd 格式");
        }

        // ---------- 4. 生成扣分描述 ----------
        String deductInfo = String.format("（关联加扣分-%s-%.1f分-%s）",
                userName, dto.getScore().doubleValue(),
                StringUtils.isNotBlank(dto.getRemark()) ? dto.getRemark() : "");

        // ---------- 5. 处理六必查记录（获取或创建，拿到记录ID） ----------
        Long sixCheckRecordId = null;
        String finalRecordValue = null; // 用于返回给前端（可选）

        SixCheckRecord query = new SixCheckRecord();
        query.setItemId(dto.getSixCheckItemId());
        try {
            query.setCheckDate(new SimpleDateFormat("yyyy-MM-dd").parse(dto.getCheckDate()));
        } catch (ParseException e) {
            return error("日期格式错误，请传入 yyyy-MM-dd");
        }
        query.setShift(dto.getShift());
        query.setDeptId(ShiroUtils.getSysUser().getDeptId());

        List<SixCheckRecord> records = sixCheckRecordService.selectSixCheckRecordList(query);

        if (records.isEmpty()) {
            // 插入新六必查记录
            SixCheckRecord newRecord = new SixCheckRecord();
            newRecord.setItemId(dto.getSixCheckItemId());
            newRecord.setCheckDate(query.getCheckDate());
            newRecord.setShift(dto.getShift());
            newRecord.setDeptId(ShiroUtils.getSysUser().getDeptId());
            newRecord.setDutyLeader(ShiroUtils.getSysUser().getUserName());
            newRecord.setRecordValue(deductInfo);
            newRecord.setCreateBy(ShiroUtils.getLoginName());
            sixCheckRecordService.insertSixCheckRecord(newRecord);
            sixCheckRecordId = newRecord.getId();
            finalRecordValue = deductInfo;
        } else {
            // 更新已有记录
            SixCheckRecord exist = records.get(0);
            String oldValue = exist.getRecordValue();
            if (StringUtils.isNotBlank(oldValue) && !"正常".equals(oldValue.trim())) {
                exist.setRecordValue(oldValue + "\n" + deductInfo);
            } else {
                exist.setRecordValue(deductInfo);
            }
            exist.setUpdateBy(ShiroUtils.getLoginName());
            sixCheckRecordService.updateSixCheckRecord(exist);
            sixCheckRecordId = exist.getId();
            finalRecordValue = exist.getRecordValue();
        }

        // ---------- 6. 调用公共扣分处理 ----------
        AjaxResult result = processDeduct("sixcheck", sixCheckRecordId, dto, deductInfo, batchNo);

        // ---------- 7. 附加六必查记录的最终值（便于前端刷新显示） ----------
        if (result.isSuccess() && finalRecordValue != null) {
            Map<String, Object> data = (Map<String, Object>) result.get("data");
            if (data == null) {
                data = new HashMap<>();
                result.put("data", data);
            }
            data.put("finalValue", finalRecordValue);
        }

        return result;
    }

    @RequiresPermissions("kpi:score:edit")
    @PostMapping("/videoQuickDeduct")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult videoQuickDeduct(@RequestBody QuickDeductDTO dto) {
        // ---------- 1. 基础参数校验 ----------
        if (dto.getUserId() == null || dto.getItemId() == null || dto.getScore() == null) {
            return error("缺少必填参数：userId, itemId, score");
        }
        if (StringUtils.isBlank(dto.getCheckDate()) || dto.getCheckDate().length() < 7) {
            return error("检查日期不能为空且格式必须为 yyyy-MM");
        }
        if (dto.getVideoItemId() == null) {
            return error("视频项目ID不能为空");
        }

        // ---------- 2. 查询用户，判空 ----------
        SysUser user = userService.selectUserById(dto.getUserId());
        if (user == null) {
            return error("用户ID " + dto.getUserId() + " 不存在");
        }
        String userName = user.getUserName();

        // ---------- 3. 生成批次号（月份） ----------
        String batchNo;
        try {
            batchNo = dto.getCheckDate().substring(0, 7);
        } catch (Exception e) {
            return error("日期格式错误，请传入 yyyy-MM-dd 格式");
        }

        // ---------- 4. 生成扣分描述 ----------
        String deductInfo = String.format("（关联加扣分-%s-%.1f分-%s）",
                userName, dto.getScore().doubleValue(),
                StringUtils.isNotBlank(dto.getRemark()) ? dto.getRemark() : "");

        // ---------- 5. 处理视频回放记录（获取或创建，拿到记录ID） ----------
        VideoPlaybackRecord queryVideo = new VideoPlaybackRecord();
        queryVideo.setItemId(dto.getVideoItemId());
        queryVideo.setBatchNo(batchNo);
        queryVideo.setDeptId(ShiroUtils.getSysUser().getDeptId());
        List<VideoPlaybackRecord> videoRecords = videoPlaybackRecordService.selectVideoPlaybackRecordList(queryVideo);

        Long videoRecordId;
        if (!videoRecords.isEmpty()) {
            VideoPlaybackRecord exist = videoRecords.get(0);
            String oldStatus = exist.getPlaybackStatus();
            if (StringUtils.isNotBlank(oldStatus) && !"正常".equals(oldStatus)) {
                exist.setPlaybackStatus(oldStatus + "\n" + deductInfo);
            } else {
                exist.setPlaybackStatus(deductInfo);
            }
            exist.setUpdateBy(ShiroUtils.getLoginName());
            videoPlaybackRecordService.updateVideoPlaybackRecord(exist);
            videoRecordId = exist.getId();
        } else {
            VideoPlaybackRecord newRecord = new VideoPlaybackRecord();
            newRecord.setItemId(dto.getVideoItemId());
            newRecord.setBatchNo(batchNo);
            newRecord.setDeptId(ShiroUtils.getSysUser().getDeptId());
            newRecord.setPlaybackStatus(deductInfo);
            newRecord.setCreateBy(ShiroUtils.getLoginName());
            videoPlaybackRecordService.insertVideoPlaybackRecord(newRecord);
            videoRecordId = newRecord.getId();
        }

        // ---------- 6. 调用公共扣分处理 ----------
        return processDeduct("video", videoRecordId, dto, deductInfo, batchNo);
    }

    /**
     * 导出考核汇总（含明细）
     * 包含两个Sheet：汇总排名 + 全部明细
     */
    @GetMapping("/summary/exportAll")
    public void exportAll(HttpServletResponse response,
            @RequestParam String startMonth,
            @RequestParam String endMonth,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) Long postId) throws IOException {
        SysUser currentUser = ShiroUtils.getSysUser();
        // 如果不是管理员，强制使用当前用户的部门
        if (!currentUser.isAdmin()) {
            deptId = currentUser.getDeptId();
        }
        // 1. 查询汇总数据
        List<String> months = getMonthsBetween(startMonth, endMonth);
        List<ScoreSummary> summaryList = kpiScoreService.selectAvgSummary(months, deptId, postId);
        for (int i = 0; i < summaryList.size(); i++) {
            summaryList.get(i).setRank(i + 1);
        }

        // 2. 查询明细数据
        List<KpiScoreDetailVo> detailList = kpiScoreService.selectAllDetail(months, deptId, postId);

        // 3. 创建工作簿
        Workbook workbook = new XSSFWorkbook();

        // ---------- Sheet 1: 汇总排名 ----------
        Sheet summarySheet = workbook.createSheet("汇总排名");
        String[] summaryHeaders = { "排名", "姓名", "部门", "岗位", "总分" };
        createHeaderRow(summarySheet, summaryHeaders);
        int rowNum = 1;
        for (ScoreSummary s : summaryList) {
            Row row = summarySheet.createRow(rowNum++);
            row.createCell(0).setCellValue(s.getRank());
            row.createCell(1).setCellValue(s.getUserName());
            row.createCell(2).setCellValue(s.getDeptName());
            row.createCell(3).setCellValue(s.getPostName());
            row.createCell(4).setCellValue(s.getTotalScore() != null ? s.getTotalScore().doubleValue() : 0);
        }
        for (int i = 0; i < summaryHeaders.length; i++) {
            summarySheet.autoSizeColumn(i);
        }

        // ---------- Sheet 2: 全部明细 ----------
        Sheet detailSheet = workbook.createSheet("全部明细");
        String[] detailHeaders = { "月份", "姓名", "考核项目", "得分", "备注" };
        createHeaderRow(detailSheet, detailHeaders);
        rowNum = 1;
        for (KpiScoreDetailVo d : detailList) {
            Row row = detailSheet.createRow(rowNum++);
            row.createCell(0).setCellValue(d.getBatchNo());
            row.createCell(1).setCellValue(d.getUserName());
            row.createCell(2).setCellValue(d.getItemName());
            row.createCell(3).setCellValue(d.getScore() != null ? d.getScore().doubleValue() : 0);
            row.createCell(4).setCellValue(d.getRemark() != null ? d.getRemark() : "");
        }
        for (int i = 0; i < detailHeaders.length; i++) {
            detailSheet.autoSizeColumn(i);
        }

        // 4. 输出到 response
        String fileName = URLEncoder.encode("考核汇总_" + startMonth + "_至_" + endMonth + ".xlsx", "UTF-8");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    // 辅助方法：创建表头行（带样式）
    private void createHeaderRow(Sheet sheet, String[] headers) {
        Row headerRow = sheet.createRow(0);
        CellStyle style = sheet.getWorkbook().createCellStyle();
        Font font = sheet.getWorkbook().createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
    }

    /**
     * 通用扣分处理器
     * 
     * @param sourceType "sixcheck" 或 "video"
     * @param sourceId   六必查记录ID 或 视频记录ID
     * @param dto        扣分参数
     * @param deductInfo 扣分描述
     * @param batchNo    批次号
     * @return 结果
     */
    private AjaxResult processDeduct(String sourceType, Long sourceId, QuickDeductDTO dto, String deductInfo,
            String batchNo) {
        // 1. 处理 KPI 记录的 Upsert（原代码中完全一致的部分）
        KpiScore existQuery = new KpiScore();
        existQuery.setUserId(dto.getUserId());
        existQuery.setItemId(dto.getItemId());
        existQuery.setBatchNo(batchNo);
        List<KpiScore> existList = kpiScoreService.selectKpiScoreList(existQuery);

        Long finalScoreId;
        if (!existList.isEmpty()) {
            KpiScore exist = existList.get(0);
            exist.setScore(exist.getScore().add(dto.getScore()));
            String oldRemark = exist.getRemark() != null ? exist.getRemark() : "";
            exist.setRemark(oldRemark.isEmpty() ? deductInfo : oldRemark + "\n" + deductInfo);
            exist.setSourceRecordId(sourceId); // 关联源记录
            exist.setUpdateBy(ShiroUtils.getLoginName());
            kpiScoreService.updateKpiScore(exist);
            finalScoreId = exist.getId();
        } else {
            KpiScore scoreEntity = new KpiScore();
            scoreEntity.setUserId(dto.getUserId());
            scoreEntity.setItemId(dto.getItemId());
            scoreEntity.setScore(dto.getScore());
            scoreEntity.setBatchNo(batchNo);
            scoreEntity.setRemark(deductInfo);
            scoreEntity.setSourceRecordId(sourceId);
            scoreEntity.setCreateBy(ShiroUtils.getLoginName());
            kpiScoreService.insertKpiScore(scoreEntity);
            finalScoreId = scoreEntity.getId();
        }

        // 2. 插入扣分明细（插入类型根据 sourceType 区分）
        SixCheckDeductDetail detail = new SixCheckDeductDetail();
        detail.setSourceType(sourceType);
        detail.setSixCheckRecordId(sourceId);
        detail.setKpiScoreId(finalScoreId);
        detail.setDeductInfo(deductInfo);
        detail.setDeductScore(dto.getScore());
        detail.setStatus(1);
        detail.setCreateBy(ShiroUtils.getLoginName());
        sixCheckDeductDetailService.insert(detail);

        // 3. 预警检查
        checkAndSendWarning(dto.getUserId(), batchNo);
        return success("加扣分成功！").put("kpiScoreId", finalScoreId);
    }

}
