package com.ruoyi.web.controller.biz.kpi.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.web.controller.biz.kpi.domain.KpiItem;
import com.ruoyi.web.controller.biz.kpi.domain.KpiScore;
import com.ruoyi.web.controller.biz.kpi.domain.QuickDeductDTO;
import com.ruoyi.web.controller.biz.kpi.service.IKpiItemService;
import com.ruoyi.web.controller.biz.kpi.service.IKpiScoreService;
import com.ruoyi.web.controller.biz.sixcheck.domain.SixCheckItem;
import com.ruoyi.web.controller.biz.sixcheck.domain.SixCheckRecord;
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
            Map<String, Object> detail = new HashMap<>();
            detail.put("score", s.getScore() != null ? s.getScore() : 0);
            detail.put("remark", s.getRemark() != null ? s.getRemark() : "");
            scoreMap.put(s.getUserId() + "_" + s.getItemId(), detail);
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

    // 返回 JSON 数据的方法（访问地址：/kpi/score/summary/data）
    @GetMapping("/summary/data")
    @ResponseBody
    public AjaxResult summaryData(@RequestParam String batchNo,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) Long postId) {
        SysUser user = ShiroUtils.getSysUser();
        if (!user.isAdmin()) {
            deptId = user.getDeptId();
        }
        List<ScoreSummary> list = kpiScoreService.selectSummary(batchNo, deptId, postId);
        return success().put("data", list);
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
    public AjaxResult quickDeduct(@RequestBody QuickDeductDTO dto) {
        // 1. 参数校验
        if (dto.getUserId() == null || dto.getItemId() == null || dto.getScore() == null) {
            return error("缺少必填参数");
        }

        // 2. 查询已有记录
        String batchNo = new SimpleDateFormat("yyyy-MM").format(new Date());
        KpiScore existQuery = new KpiScore();
        existQuery.setUserId(dto.getUserId());
        existQuery.setItemId(dto.getItemId());
        existQuery.setBatchNo(batchNo);
        List<KpiScore> existList = kpiScoreService.selectKpiScoreList(existQuery);

        // 3. 生成扣分描述（用于六必查和备注）
        String userName = dto.getUserId().toString(); // 默认，下面会从数据库查
        SysUser user = userService.selectUserById(dto.getUserId());
        if (user != null)
            userName = user.getUserName();
        String deductInfo = String.format("（关联加扣分-%s-%.1f分-%s）",
                userName, dto.getScore().doubleValue(), dto.getRemark());

        Long finalScoreId; // 最终返回给前端的 kpi_score ID

        if (existList.size() > 0) {
            // 已有记录：累加分数，备注换行追加
            KpiScore exist = existList.get(0);
            exist.setScore(exist.getScore().add(dto.getScore()));
            String oldRemark = exist.getRemark() != null ? exist.getRemark() : "";
            exist.setRemark(oldRemark.isEmpty() ? deductInfo : oldRemark + "\n" + deductInfo);
            exist.setUpdateBy(ShiroUtils.getLoginName());
            kpiScoreService.updateKpiScore(exist);
            finalScoreId = exist.getId(); // 返回已有记录的ID
        } else {
            // 新记录
            KpiScore score = new KpiScore();
            score.setUserId(dto.getUserId());
            score.setItemId(dto.getItemId());
            score.setScore(dto.getScore());
            score.setBatchNo(batchNo);
            score.setRemark(deductInfo);
            score.setCreateBy(ShiroUtils.getLoginName());
            kpiScoreService.insertKpiScore(score);
            finalScoreId = score.getId();
        }

        // 4. 更新六必查记录（保持原有逻辑）
        if (dto.getSixCheckItemId() != null && StringUtils.isNotBlank(dto.getCheckDate())
                && StringUtils.isNotBlank(dto.getShift())) {
            SixCheckRecord query = new SixCheckRecord();
            query.setItemId(dto.getSixCheckItemId());
            try {
                query.setCheckDate(new SimpleDateFormat("yyyy-MM-dd").parse(dto.getCheckDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            query.setShift(dto.getShift());
            query.setDeptId(ShiroUtils.getSysUser().getDeptId());
            List<SixCheckRecord> records = sixCheckRecordService.selectSixCheckRecordList(query);

            if (records.isEmpty()) {
                SixCheckRecord newRecord = new SixCheckRecord();
                newRecord.setItemId(dto.getSixCheckItemId());
                newRecord.setCheckDate(query.getCheckDate());
                newRecord.setShift(dto.getShift());
                newRecord.setDeptId(ShiroUtils.getSysUser().getDeptId());
                newRecord.setDutyLeader(ShiroUtils.getSysUser().getUserName());
                String baseValue = StringUtils.defaultString(dto.getCurrentRecordValue(), "");
                if (StringUtils.isNotBlank(baseValue) && !"正常".equals(baseValue)) {
                    newRecord.setRecordValue(baseValue + "\n" + deductInfo);
                } else {
                    newRecord.setRecordValue(deductInfo);
                }
                newRecord.setCreateBy(ShiroUtils.getLoginName());
                sixCheckRecordService.insertSixCheckRecord(newRecord);
            } else {
                SixCheckRecord exist = records.get(0);
                String oldValue = exist.getRecordValue();
                String baseValue = StringUtils.isNotBlank(dto.getCurrentRecordValue())
                        ? dto.getCurrentRecordValue()
                        : oldValue;
                if (StringUtils.isNotBlank(baseValue) && !"正常".equals(baseValue)) {
                    exist.setRecordValue(baseValue + "\n" + deductInfo);
                } else {
                    exist.setRecordValue(deductInfo);
                }
                exist.setUpdateBy(ShiroUtils.getLoginName());
                sixCheckRecordService.updateSixCheckRecord(exist);
            }
        }

        return success("加扣分成功！").put("kpiScoreId", finalScoreId);
    }

    @RequiresPermissions("kpi:score:summary")
    @GetMapping("/summary/export")
    @ResponseBody
    public void exportSummary(HttpServletResponse response,
            @RequestParam String batchNo,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) Long postId) { // 新增 postId
        List<ScoreSummary> list = kpiScoreService.selectSummary(batchNo, deptId, postId);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setRank(i + 1);
            list.get(i).setTotalScore(list.get(i).getTotalScore() + 100);
        }
        ExcelUtil<ScoreSummary> util = new ExcelUtil<>(ScoreSummary.class);
        util.exportExcel(response, list, "考核结果_" + batchNo);
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
    @PostMapping("/videoQuickDeduct")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult videoQuickDeduct(@RequestBody QuickDeductDTO dto) {

        // 2. 查询已有记录
        String batchNo = new SimpleDateFormat("yyyy-MM").format(new Date());
        KpiScore existQuery = new KpiScore();
        existQuery.setUserId(dto.getUserId());
        existQuery.setItemId(dto.getItemId());
        existQuery.setBatchNo(batchNo);
        List<KpiScore> existList = kpiScoreService.selectKpiScoreList(existQuery);

        // 3. 生成扣分描述（用于视频回放和备注）
        String userName = dto.getUserId().toString(); // 默认，下面会从数据库查
        SysUser user = userService.selectUserById(dto.getUserId());
        if (user != null)
            userName = user.getUserName();
        String deductInfo = String.format("（关联加扣分-%s-%.1f分-%s）",
                userName, dto.getScore().doubleValue(), dto.getRemark());

        Long finalScoreId; // 最终返回给前端的 kpi_score ID
        System.out.println("=== videoQuickDeduct: 查询已有记录，existList.size()=" + existList.size() + ", dto.getUserId()="
                        + dto.getUserId() + ", dto.getItemId()=" + dto.getItemId() + ", batchNo=" + batchNo);
        if (existList.size() > 0) {
            // 已有记录：累加分数，备注换行追加
            KpiScore exist = existList.get(0);
            exist.setScore(exist.getScore().add(dto.getScore()));
            System.out.println("=== videoQuickDeduct: 更新已有记录，旧备注=" + exist.getRemark() 
            + ", 新扣分信息=" + deductInfo + ", 新分数=" + exist.getScore());
            String oldRemark = exist.getRemark() != null ? exist.getRemark() : "";
            exist.setRemark(oldRemark.isEmpty() ? deductInfo : oldRemark + "\n" + deductInfo);
            exist.setUpdateBy(ShiroUtils.getLoginName());
            kpiScoreService.updateKpiScore(exist);
            finalScoreId = exist.getId(); // 返回已有记录的ID
        } else {
            // 新记录
            KpiScore score = new KpiScore();
            score.setUserId(dto.getUserId());
            score.setItemId(dto.getItemId());
            score.setScore(dto.getScore());
            score.setBatchNo(batchNo);
            score.setRemark(deductInfo);
            score.setCreateBy(ShiroUtils.getLoginName());
            kpiScoreService.insertKpiScore(score);
            finalScoreId = score.getId();
        }

        System.out.println(
                "=== videoQuickDeduct: 保存考核dto.getVideoItemId() =" + dto.getVideoItemId() + ", dto.getVideoDate()="
                        + dto.getVideoDate() + ", dto.getRemark()=" + dto.getRemark());

        // 2. 更新视频回放记录内容（追加关联扣分信息）
        if (dto.getVideoItemId() != null) {
            VideoPlaybackRecord query = new VideoPlaybackRecord();
            query.setItemId(dto.getVideoItemId());
            query.setBatchNo(new SimpleDateFormat("yyyy-MM").format(new Date()));
            query.setDeptId(ShiroUtils.getSysUser().getDeptId());
            List<VideoPlaybackRecord> records = videoPlaybackRecordService.selectVideoPlaybackRecordList(query);
            System.out.println("=== videoQuickDeduct: 查询视频回放记录，records.size()=" + records.size());
            if (user != null)
                userName = user.getUserName();

            if (!records.isEmpty()) {
                VideoPlaybackRecord exist = records.get(0);
                String oldValue = exist.getPlaybackStatus();
                System.out.println("=== videoQuickDeduct: 旧的回放状态 oldValue=" + oldValue);
                if (StringUtils.isNotBlank(oldValue) && !"正常".equals(oldValue)) {
                    exist.setPlaybackStatus(oldValue + "\n" + deductInfo);
                } else {
                    exist.setPlaybackStatus(deductInfo);
                }
                exist.setUpdateBy(ShiroUtils.getLoginName());
                videoPlaybackRecordService.updateVideoPlaybackRecord(exist);
            } else {
                VideoPlaybackRecord newRecord = new VideoPlaybackRecord();
                newRecord.setItemId(dto.getVideoItemId());
                newRecord.setPlaybackStatus(deductInfo);
                newRecord.setBatchNo(new SimpleDateFormat("yyyy-MM").format(new Date()));
                newRecord.setDeptId(ShiroUtils.getSysUser().getDeptId());
                newRecord.setCreateBy(ShiroUtils.getLoginName());
                videoPlaybackRecordService.insertVideoPlaybackRecord(newRecord);
            }
        }

        return success("加扣分成功！").put("kpiScoreId", finalScoreId);
    }

}
