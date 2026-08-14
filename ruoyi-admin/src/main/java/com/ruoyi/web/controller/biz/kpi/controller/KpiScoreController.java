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
import java.util.List;
import java.util.Map;
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
    public AjaxResult saveBatch(@RequestBody List<KpiScore> scoreList) {
        System.out.println("=== 完整 scoreList: " + scoreList.toString());
        for (KpiScore score : scoreList) {
            System.out.println("=== 收到数据: userId=" + score.getUserId() +
                    ", itemId=" + score.getItemId() +
                    ", score=[" + score.getScore() + "]" +
                    ", batchNo=[" + score.getBatchNo() + "]" +
                    ", remark=[" + score.getRemark() + "]");
            // 查询是否已有记录
            KpiScore query = new KpiScore();
            query.setUserId(score.getUserId());
            query.setItemId(score.getItemId());
            query.setBatchNo(score.getBatchNo());
            List<KpiScore> existList = kpiScoreService.selectKpiScoreList(query);

            if (existList.size() > 0) {
                // 已有记录
                KpiScore exist = existList.get(0);
                if (score.getScore() == null) {
                    // 用户清空了分数 → 删除已有记录
                    kpiScoreService.deleteKpiScoreByIds(exist.getId().toString());
                } else {
                    // 有分数（含0） → 更新
                    exist.setScore(score.getScore());
                    exist.setRemark(score.getRemark()); // 备注也同步更新
                    exist.setUpdateBy(ShiroUtils.getLoginName());
                    kpiScoreService.updateKpiScore(exist);
                }
            } else {
                // 无记录
                if (score.getScore() != null) {
                    // 有分数（含0） → 插入
                    score.setCreateBy(ShiroUtils.getLoginName());
                    score.setRemark(score.getRemark()); // 保证备注入库
                    kpiScoreService.insertKpiScore(score);
                }
                // 无记录且分数为null → 什么都不做
            }
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
            System.out.println("接收到的 category = " + category);
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
            if (/* StringUtils.isNotBlank(s.getRemark()) &&  */s.getScore() != null) {
                Map<String, Object> detail = new HashMap<>();
                detail.put("score", s.getScore() != null ? s.getScore() : 0);
                detail.put("remark", s.getRemark() != null ? s.getRemark() : "");
                scoreMap.put(s.getUserId() + "_" + s.getItemId(), detail);
                System.out.println("s.getScore() " + s.getScore() + ", s.getRemark() " + s.getRemark());
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

    /**
     * 六必查快速扣分接口
     */
    @RequiresPermissions("kpi:score:edit")
    @PostMapping("/quickDeduct")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult quickDeduct(@RequestBody QuickDeductDTO dto) {
        // 1. 参数校验
        if (dto.getUserId() == null || dto.getItemId() == null || dto.getScore() == null) {
            return error("缺少必填参数");
        }

        // 2. 生成扣分描述
        String userName = userService.selectUserById(dto.getUserId()).getUserName();
        String deductInfo = String.format("（关联加扣分-%s-%.1f分-%s）",
                userName, dto.getScore().doubleValue(), dto.getRemark());

        // ============================================================
        // 第3步：先处理六必查记录，获取 sixCheckRecordId
        // ============================================================
        Long sixCheckRecordId = null;
        String finalValue = null; // 用于返回给前端

        if (dto.getSixCheckItemId() != null && StringUtils.isNotBlank(dto.getCheckDate())
                && StringUtils.isNotBlank(dto.getShift())) {
            SixCheckRecord query = new SixCheckRecord();
            query.setItemId(dto.getSixCheckItemId());
            try {
                query.setCheckDate(new SimpleDateFormat("yyyy-MM-dd").parse(dto.getCheckDate()));
            } catch (ParseException e) {
                e.printStackTrace();
                return error("日期格式错误");
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
                // 使用数据库中已有的值，不依赖前端传参
                newRecord.setRecordValue(deductInfo);
                newRecord.setCreateBy(ShiroUtils.getLoginName());
                sixCheckRecordService.insertSixCheckRecord(newRecord);
                sixCheckRecordId = newRecord.getId(); // 获取ID
                finalValue = deductInfo;
            } else {
                // 更新已有六必查记录
                SixCheckRecord exist = records.get(0);
                String oldValue = exist.getRecordValue();
                // 从数据库读取最新值，不依赖前端传参
                if (StringUtils.isNotBlank(oldValue) && !"正常".equals(oldValue.trim())) {
                    exist.setRecordValue(oldValue + "\n" + deductInfo);
                } else {
                    exist.setRecordValue(deductInfo);
                }
                exist.setUpdateBy(ShiroUtils.getLoginName());
                sixCheckRecordService.updateSixCheckRecord(exist);
                sixCheckRecordId = exist.getId(); // 获取ID
                finalValue = exist.getRecordValue(); // 更新后的值
            }
        }

        // ============================================================
        // 第4步：再处理 KPI 记录（此时 sixCheckRecordId 已有值）
        // ============================================================
        String batchNo = dto.getCheckDate().substring(0, 7); // "2026-07"

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
            // 避免重复追加
            if (!oldRemark.contains(deductInfo)) {
                exist.setRemark(oldRemark.isEmpty() ? deductInfo : oldRemark + "\n" + deductInfo);
            }
            exist.setSourceRecordId(sixCheckRecordId); // 此时 sixCheckRecordId 有值
            exist.setUpdateBy(ShiroUtils.getLoginName());
            kpiScoreService.updateKpiScore(exist);
            finalScoreId = exist.getId();
        } else {
            KpiScore score = new KpiScore();
            score.setUserId(dto.getUserId());
            score.setItemId(dto.getItemId());
            score.setScore(dto.getScore());
            score.setBatchNo(batchNo);
            score.setRemark(deductInfo);
            score.setSourceRecordId(sixCheckRecordId); // 此时 sixCheckRecordId 有值
            score.setCreateBy(ShiroUtils.getLoginName());
            kpiScoreService.insertKpiScore(score);
            finalScoreId = score.getId();
        }

        // ============================================================
        // 第5步：返回结果
        // ============================================================
        Map<String, Object> result = new HashMap<>();
        result.put("kpiScoreId", finalScoreId);
        result.put("finalValue", finalValue); // 供前端更新 textarea
        // 在保存或更新 KPI 记录后，得到 finalScoreId

        // 插入扣分明细
        if (finalScoreId != null && sixCheckRecordId != null) {
            SixCheckDeductDetail detail = new SixCheckDeductDetail();
            detail.setSixCheckRecordId(sixCheckRecordId);
            detail.setKpiScoreId(finalScoreId);
            detail.setDeductInfo(deductInfo);
            detail.setStatus(1);
            detail.setCreateBy(ShiroUtils.getLoginName());
            detail.setDeductScore(dto.getScore());
            sixCheckDeductDetailService.insert(detail);
            System.out.println("=== 插入扣分明细成功，detailId=" + detail.getId());
        }
        return success("加扣分成功！").put("data", result);
    }

    @RequiresPermissions("kpi:score:summary")
    @GetMapping("/summary/export")
    @ResponseBody
    public void exportSummary(HttpServletResponse response,
            @RequestParam String startMonth,
            @RequestParam String endMonth,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) Long postId) { // 新增 postId
        List<String> months = getMonthsBetween(startMonth, endMonth);
        List<ScoreSummary> list = kpiScoreService.selectAvgSummary(months, deptId, postId);
        System.out.println("months " + months + ", postId is " + postId);
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
            System.out.println("list.get(i).getTotalScore() " + list.get(i).getTotalScore());
            list.get(i).setTotalScore(Math.round(list.get(i).getTotalScore() * 100.0) / 100.0);
        }
        return success().put("data", list);
    }

    // 辅助方法：生成区间月份列表
    private List<String> getMonthsBetween(String start, String end) {
        List<String> months = new ArrayList<>();
        System.out.println("start " + start + ", end " + end);
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
    @PostMapping("/videoQuickDeduct")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult videoQuickDeduct(@RequestBody QuickDeductDTO dto) {

        // 1. 参数校验
        if (dto.getUserId() == null || dto.getItemId() == null || dto.getScore() == null) {
            return error("缺少必填参数");
        }
        if (dto.getVideoItemId() == null) {
            return error("视频项目ID或日期不能为空");
        }

        // 2. 生成批次号（使用视频日期，而不是当前时间）
        // String batchNo = new SimpleDateFormat("yyyy-MM").format(dto.getVideoDate())
        String batchNo = dto.getCheckDate().substring(0, 7);
        System.out.println("=== videoQuickDeduct: batchNo=" + batchNo);

        // 3. 查询已有 KPI 记录
        KpiScore existQuery = new KpiScore();
        existQuery.setUserId(dto.getUserId());
        existQuery.setItemId(dto.getItemId());
        existQuery.setBatchNo(batchNo);
        List<KpiScore> existList = kpiScoreService.selectKpiScoreList(existQuery);

        // 4. 生成扣分描述
        SysUser user = userService.selectUserById(dto.getUserId());
        String userName = user != null ? user.getUserName() : dto.getUserId().toString();
        String deductInfo = String.format("（关联加扣分-%s-%.1f分-%s）",
                userName, dto.getScore().doubleValue(), dto.getRemark());

        Long finalScoreId;

        // 5. 处理视频回放记录（先获取或创建，拿到 ID）
        VideoPlaybackRecord videoRecord = null;
        VideoPlaybackRecord queryVideo = new VideoPlaybackRecord();
        queryVideo.setItemId(dto.getVideoItemId());
        queryVideo.setBatchNo(batchNo); // 使用视频日期
        queryVideo.setDeptId(ShiroUtils.getSysUser().getDeptId());
        List<VideoPlaybackRecord> videoRecords = videoPlaybackRecordService.selectVideoPlaybackRecordList(queryVideo);

        if (!videoRecords.isEmpty()) {
            videoRecord = videoRecords.get(0);
            // 更新 playbackStatus
            String oldStatus = videoRecord.getPlaybackStatus();
            if (StringUtils.isNotBlank(oldStatus) && !"正常".equals(oldStatus)) {
                videoRecord.setPlaybackStatus(oldStatus + "\n" + deductInfo);
            } else {
                videoRecord.setPlaybackStatus(deductInfo);
            }
            videoRecord.setUpdateBy(ShiroUtils.getLoginName());
            videoPlaybackRecordService.updateVideoPlaybackRecord(videoRecord);
        } else {
            // 插入新视频记录
            videoRecord = new VideoPlaybackRecord();
            videoRecord.setItemId(dto.getVideoItemId());
            videoRecord.setBatchNo(batchNo);
            videoRecord.setDeptId(ShiroUtils.getSysUser().getDeptId());
            videoRecord.setPlaybackStatus(deductInfo);
            videoRecord.setCreateBy(ShiroUtils.getLoginName());
            videoPlaybackRecordService.insertVideoPlaybackRecord(videoRecord);
        }
        Long videoRecordId = videoRecord.getId(); // 获取视频记录ID

        // 6. 处理 KPI 记录（建立 source_record_id 关联）
        if (!existList.isEmpty()) {
            KpiScore exist = existList.get(0);
            exist.setScore(exist.getScore().add(dto.getScore()));
            String oldRemark = exist.getRemark() != null ? exist.getRemark() : "";
            exist.setRemark(oldRemark.isEmpty() ? deductInfo : oldRemark + "\n" + deductInfo);
            System.out.println("=== [调试] 设置前 exist.getSourceRecordId() = " + exist.getSourceRecordId());
            System.out.println("=== [调试] 设置前 videoRecordId = " + videoRecordId);
            exist.setSourceRecordId(videoRecordId); // 关联视频记录ID
            System.out.println("=== 设置后 exist.getSourceRecordId() = " + exist.getSourceRecordId());
            exist.setUpdateBy(ShiroUtils.getLoginName());
            System.out.println("=== 准备更新 KPI，exist 对象: " + exist);
            kpiScoreService.updateKpiScore(exist);
            finalScoreId = exist.getId();
        } else {
            KpiScore score = new KpiScore();
            score.setUserId(dto.getUserId());
            score.setItemId(dto.getItemId());
            score.setScore(dto.getScore());
            score.setBatchNo(batchNo);
            score.setRemark(deductInfo);
            score.setSourceRecordId(videoRecordId); // 关联视频记录ID
            score.setCreateBy(ShiroUtils.getLoginName());
            kpiScoreService.insertKpiScore(score);
            finalScoreId = score.getId();
        }

        System.out.println("=== videoQuickDeduct: 成功，videoRecordId=" + videoRecordId + ", kpiScoreId=" + finalScoreId);

        return success("加扣分成功！").put("kpiScoreId", finalScoreId);
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

}
