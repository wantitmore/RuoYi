package com.ruoyi.web.controller.biz.ld_quarter_common.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.system.service.ISysPostService;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.web.controller.biz.ld_quarter_common.domain.QuarterFactor;
import com.ruoyi.web.controller.biz.ld_quarter_common.domain.QuarterScore;
import com.ruoyi.web.controller.biz.ld_quarter_common.service.IAssessPostConfigService;
import com.ruoyi.web.controller.biz.ld_quarter_common.service.IQuarterFactorService;
import com.ruoyi.web.controller.biz.ld_quarter_common.service.IQuarterScoreService;
import com.ruoyi.system.domain.SysPost;
import com.ruoyi.system.domain.SysUserPost;

@Controller
@RequestMapping("/quarter")
public class QuarterController extends BaseController {

    @Autowired
    private IQuarterScoreService quarterScoreService;

    @Autowired
    private IQuarterFactorService quarterFactorService;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysPostService postService;

    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private IAssessPostConfigService assessPostConfigService;

    private String prefix = "quarter";

    /**
     * 打开季度考核列表页
     */
    @RequiresPermissions("quarter:view")
    @GetMapping("/list")
    public String list(@RequestParam(required = false, defaultValue = "quarter") String type, ModelMap mmap) {
        // 加载因素列表（按 type 过滤）
        QuarterFactor queryFactor = new QuarterFactor();
        queryFactor.setType(type);
        List<QuarterFactor> factors = quarterFactorService.selectQuarterFactorList(queryFactor);
        mmap.put("factors", factors);

        // 加载部门列表（管理员用）
        List<SysDept> depts = deptService.selectDeptList(new SysDept());
        mmap.put("depts", depts);

        // 设置默认季度
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int q = (month - 1) / 3 + 1;
        mmap.put("defaultQuarter", year + "-Q" + q);

        // 把 type 传到前端，AJAX 会用到
        mmap.put("type", type);
        mmap.put("canEdit", true);

        SysUser currentUser = ShiroUtils.getSysUser();
        List<SysPost> userPosts = postService.selectPostsByUserId(currentUser.getUserId());
        String userPostName = userPosts.stream()
                .filter(SysPost::isFlag)
                .findFirst()
                .map(SysPost::getPostName)
                .orElse("");
        mmap.put("userPostName", userPostName);

        return prefix + "/list";
    }

    /**
     * 获取季度考核数据（矩阵表格）
     */
    @RequiresPermissions("quarter:list")
    @GetMapping("/data")
    @ResponseBody
    public AjaxResult data(@RequestParam String batchNo,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false, defaultValue = "quarter") String type) {
        if (!ShiroUtils.getSysUser().isAdmin()) {
            deptId = ShiroUtils.getSysUser().getDeptId();
        }

        SysUser currentUser = ShiroUtils.getSysUser();

        // 1. 查询该部门该季度的评分记录
        QuarterScore query = new QuarterScore();
        query.setBatchNo(batchNo);
        if (deptId != null)
            query.setDeptId(deptId);
        if (!currentUser.isAdmin()) {
            query.setCreateBy(currentUser.getLoginName()); // 非管理员只查自己的
        }
        List<SysPost> userPosts = postService.selectPostsByUserId(currentUser.getUserId());
        boolean isZoneLeader = userPosts.stream()
                .filter(SysPost::isFlag)
                .anyMatch(p -> p.getPostName() != null && p.getPostName().contains("分区领导"));
        List<QuarterScore> scores = quarterScoreService.selectQuarterScoreList(query);
        // 2. 查询该部门所有用户
        SysUser userQuery = new SysUser();
        if (deptId != null)
            userQuery.setDeptId(deptId);
        List<SysUser> allUsers = userService.selectUserList(userQuery);

        // 3. 根据 type 决定需要过滤的岗位编码集合（固定岗位）
        List<String> targetPostCodes = assessPostConfigService.getPostCodesByType(type);
        if (targetPostCodes.isEmpty()) {
            // 如果该类型没有配置任何岗位，可以返回空数据或提示
            return success().put("data", Collections.emptyMap());
        }

        // 4. 过滤出属于固定岗位的用户
        List<SysUser> filteredUsers = new ArrayList<>();
        for (SysUser user : allUsers) {
            List<SysPost> allUserPosts = postService.selectPostsByUserId(user.getUserId());
            List<SysPost> actualPosts = allUserPosts.stream()
                    .filter(SysPost::isFlag)
                    .collect(Collectors.toList());

            // 判断是否有岗位在目标列表中
            boolean hasTargetPost = actualPosts.stream()
                    .map(SysPost::getPostCode)
                    .anyMatch(targetPostCodes::contains);
            if (hasTargetPost) {
                filteredUsers.add(user);
            }
        }

        // 5. 查询因素（按 type 过滤）
        QuarterFactor factorQuery = new QuarterFactor();
        factorQuery.setType(type);
        List<QuarterFactor> factors = quarterFactorService.selectQuarterFactorList(factorQuery);

        // 6. 构建矩阵（同原来）
        Map<Long, Map<Long, QuarterScore>> matrix = new HashMap<>();
        for (QuarterScore s : scores) {
            matrix.computeIfAbsent(s.getUserId(), k -> new HashMap<>()).put(s.getFactorId(), s);
        }

        // 7. 组装返回数据（使用 filteredUsers）
        List<Map<String, Object>> rows = new ArrayList<>();
        for (SysUser user : filteredUsers) {
            Map<String, Object> row = new HashMap<>();
            row.put("userId", user.getUserId());
            row.put("userName", user.getUserName());
            row.put("deptName", user.getDept() != null ? user.getDept().getDeptName() : "");
            // 注意：岗位名称直接从 userPosts 中取第一个（或根据排序取）
            List<SysPost> allUserPosts = postService.selectPostsByUserId(user.getUserId());
            List<SysPost> actualPosts = allUserPosts.stream()
                    .filter(SysPost::isFlag)
                    .collect(Collectors.toList());

            String postName = actualPosts.stream()
                    .filter(p -> targetPostCodes.contains(p.getPostCode()))
                    .findFirst()
                    .map(SysPost::getPostName)
                    .orElse("");
            row.put("postName", postName);
            double total = 0;
            int count = 0;
            for (QuarterFactor factor : factors) {
                String grade = "";
                int score = 0;
                Map<Long, QuarterScore> userScores = matrix.get(user.getUserId());
                if (userScores != null && userScores.containsKey(factor.getId())) {
                    QuarterScore qs = userScores.get(factor.getId());
                    grade = qs.getGrade();
                    score = qs.getScore();
                }
                row.put("factor_" + factor.getId() + "_grade", grade);
                row.put("factor_" + factor.getId() + "_score", score);
                if (score > 0) {
                    total += score;
                    count++;
                }
            }
            row.put("avgScore", count > 0 ? Math.round(total / count * 100.0) / 100.0 : 0);

            rows.add(row);

        }

        Map<String, Object> result = new HashMap<>();
        result.put("factors", factors);
        result.put("rows", rows);
        return success().put("data", result);
    }

    /**
     * 保存季度考核评分
     */
    @RequiresPermissions("quarter:add")
    @PostMapping("/save")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult save(@RequestBody Map<String, Object> params) {
        String batchNo = (String) params.get("batchNo");
        Object factorIdObj = params.get("factorId");
        String type = (String) params.get("type");
        if (batchNo == null || factorIdObj == null) {
            return error("参数不完整");
        }
        Long factorId = Long.valueOf(factorIdObj.toString());

        List<Map<String, Object>> grades = (List<Map<String, Object>>) params.get("grades");
        if (grades == null || grades.isEmpty()) {
            return error("没有评分数据");
        }

        // 2. 校验：该因素下只能有一个人被评为“好”
        long goodCount = grades.stream()
                .filter(g -> "好".equals(g.get("grade")))
                .count();

        var totalPeople = grades.size();
        if (goodCount > 1 && "quarter".equals(type)) {
            // 直接返回错误，不执行保存
            return error("每个考察因素只能有一个人被评为“好”，当前有 " + goodCount + " 人");
        } else if (goodCount > Math.max(1, Math.ceil(totalPeople * 0.35)) && "common".equals(type)) {
            return error("每个考察因素只能35%被评为“好”");
        }

        // 3. 保存逻辑
        Long deptId = ShiroUtils.getSysUser().getDeptId();
        String loginName = ShiroUtils.getLoginName();

        for (Map<String, Object> g : grades) {
            Long userId = Long.valueOf(g.get("userId").toString());
            String grade = (String) g.get("grade");
            if (grade == null || grade.isEmpty()) {
                continue; // 跳过空值
            }
            int score = convertGradeToScore(grade);

            // 查询是否已有记录
            QuarterScore exist = new QuarterScore();
            exist.setUserId(userId);
            exist.setFactorId(factorId);
            exist.setBatchNo(batchNo);
            exist.setCreateBy(loginName);
            List<QuarterScore> existList = quarterScoreService.selectQuarterScoreList(exist);

            if (!existList.isEmpty()) {
                // 更新自己的记录（允许修改自己已评的）
                QuarterScore update = existList.get(0);
                update.setGrade(grade);
                update.setScore(score);
                update.setUpdateBy(loginName);
                quarterScoreService.updateQuarterScore(update);
            } else {
                //
                QuarterScore insert = new QuarterScore();
                insert.setUserId(userId);
                insert.setFactorId(factorId);
                insert.setGrade(grade);
                insert.setScore(score);
                insert.setBatchNo(batchNo);
                insert.setDeptId(deptId);
                insert.setCreateBy(loginName);
                insert.setUpdateBy(loginName);
                quarterScoreService.insertQuarterScore(insert);
            }
        }

        return success("保存成功");
    }

    private int convertGradeToScore(String grade) {
        switch (grade) {
            case "好":
                return 95;
            case "较好":
                return 85;
            case "一般":
                return 70;
            case "差":
                return 55;
            default:
                return 0;
        }
    }

    /**
     * 统计结果
     */
    @ResponseBody
    @GetMapping("/stats")
    public AjaxResult stats(@RequestParam String batchNo,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false, defaultValue = "quarter") String type) {
        // 权限处理
        if (!ShiroUtils.getSysUser().isAdmin()) {
            deptId = ShiroUtils.getSysUser().getDeptId();
        }
        System.out.println("=== stats: batchNo=" + batchNo + ", deptId=" + deptId + ", type=" + type);
        // 1. 获取该批次该部门所有评分记录
        QuarterScore query = new QuarterScore();
        query.setBatchNo(batchNo);
        if (deptId != null)
            query.setDeptId(deptId);
        List<QuarterScore> scores = quarterScoreService.selectQuarterScoreList(query);

        // 2. 获取该部门所有用户
        SysUser userQuery = new SysUser();
        if (deptId != null)
            userQuery.setDeptId(deptId);
        List<SysUser> allUsers = userService.selectUserList(userQuery);

        // 3. 获取目标岗位编码
        List<String> targetPostCodes = assessPostConfigService.getPostCodesByType(type);
        System.out.println("=== stats: 目标岗位编码=" + targetPostCodes.size() + ", type=" + type);
        if (targetPostCodes.isEmpty()) {
            return success().put("data", Collections.emptyList());
        }

        // 4. 过滤出被考核人（拥有目标岗位的用户）
        List<SysUser> targetUsers = new ArrayList<>();
        for (SysUser user : allUsers) {
            List<SysPost> userPosts = postService.selectPostsByUserId(user.getUserId());
            boolean isTarget = userPosts.stream()
                    .filter(SysPost::isFlag)
                    .map(SysPost::getPostCode)
                    .anyMatch(targetPostCodes::contains);
            if (isTarget) {
                targetUsers.add(user);
            }
        }
        System.out.println("=== stats: 目标岗位编码=" + targetPostCodes + ", 被考核人数量=" + targetUsers.size());

        // 5. 组装统计结果
        List<Map<String, Object>> result = new ArrayList<>();
        for (SysUser user : targetUsers) {
            Map<String, Object> row = new HashMap<>();
            row.put("userId", user.getUserId());
            row.put("userName", user.getUserName());

            // 岗位名称
            String postName = postService.selectPostsByUserId(user.getUserId()).stream()
                    .filter(SysPost::isFlag)
                    .filter(p -> targetPostCodes.contains(p.getPostCode()))
                    .findFirst()
                    .map(SysPost::getPostName)
                    .orElse("");
            row.put("postName", postName);

            // 该用户的评分记录
            List<QuarterScore> userScores = scores.stream()
                    .filter(s -> s.getUserId().equals(user.getUserId()))
                    .collect(Collectors.toList());

            if (userScores.isEmpty()) {
                row.put("avgScore", 0);
                row.put("evaluators", ""); // 无人评分
            } else {
                // 平均分
                double avg = userScores.stream().mapToInt(QuarterScore::getScore).average().orElse(0);
                row.put("avgScore", Math.round(avg * 100.0) / 100.0);

                // 评分人列表（去重）
                List<String> evaluatorNames = userScores.stream()
                        .map(QuarterScore::getCreateBy)
                        .filter(StringUtils::isNotEmpty)
                        .distinct()
                        .map(loginName -> {
                            SysUser evaluator = userService.selectUserByLoginName(loginName);
                            return evaluator != null ? evaluator.getUserName() : loginName;
                        })
                        .collect(Collectors.toList());
                row.put("evaluators", String.join("、", evaluatorNames));
            }

            result.add(row);
        }

        // 排序：已评分的按平均分降序，未评分的排最后
        result.sort((a, b) -> {
            double scoreA = (double) a.get("avgScore");
            double scoreB = (double) b.get("avgScore");
            if (scoreA == 0 && scoreB == 0)
                return 0;
            if (scoreA == 0)
                return 1;
            if (scoreB == 0)
                return -1;
            return Double.compare(scoreB, scoreA);
        });
        System.out.println("=== stats: 排序后结果=" + result.toString());
        return success().put("data", result);
    }

    @GetMapping("/statsPage")
    public String statsPage() {
        return "quarter/stats";
    }
}