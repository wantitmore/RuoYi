package com.ruoyi.web.controller.biz.kpi.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
        for (KpiScore score : scoreList) {
            // 检查是否存在相同 user_id + item_id + batch_no 的记录
            KpiScore exist = new KpiScore();
            exist.setUserId(score.getUserId());
            exist.setItemId(score.getItemId());
            exist.setBatchNo(score.getBatchNo());
            List<KpiScore> existingList = kpiScoreService.selectKpiScoreList(exist);
            if (existingList.size() > 0) {
                // 更新
                KpiScore updateScore = existingList.get(0);
                updateScore.setScore(score.getScore());
                updateScore.setUpdateBy(ShiroUtils.getLoginName());
                updateScore.setRemark(score.getRemark());
                kpiScoreService.updateKpiScore(updateScore);
            } else {
                // 插入
                score.setCreateBy(ShiroUtils.getLoginName());
                score.setRemark(score.getRemark());
                kpiScoreService.insertKpiScore(score);
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
    public AjaxResult personDetail(@RequestParam String batchNo, @RequestParam Long userId) {
        // 查询该用户、该批次的所有打分记录
        KpiScore query = new KpiScore();
        query.setBatchNo(batchNo);
        query.setUserId(userId);
        List<KpiScore> list = kpiScoreService.selectKpiScoreList(query);

        // 组装详情数据：需要考核项目名称
        List<Map<String, Object>> details = new ArrayList<>();
        for (KpiScore score : list) {
            Map<String, Object> item = new HashMap<>();
            KpiItem kpiItem = kpiItemService.selectKpiItemById(score.getItemId());
            item.put("itemName", kpiItem != null ? kpiItem.getName() : "未知项目");
            item.put("score", score.getScore());
            item.put("remark", score.getRemark() != null ? score.getRemark() : "");
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
        KpiScore score = new KpiScore();
        score.setUserId(dto.getUserId());
        score.setItemId(dto.getItemId());
        score.setScore(dto.getScore());
        score.setBatchNo(new SimpleDateFormat("yyyy-MM").format(new Date())); // 自动取当前月份
        score.setRemark(dto.getRemark());
        score.setCreateBy(ShiroUtils.getLoginName());
        return toAjax(kpiScoreService.insertKpiScore(score));
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
        }
        ExcelUtil<ScoreSummary> util = new ExcelUtil<>(ScoreSummary.class);
        util.exportExcel(response, list, "考核结果_" + batchNo);
    }
}
