/**
 * 
 * 
 * @author Zack
 * @date Sep 01, 2026
 */
package com.ruoyi.web.controller.biz.weekcheck.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysPost;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.system.service.ISysPostService;
import com.ruoyi.system.service.ISysRoleService;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.web.controller.biz.ld_quarter_common.service.IAssessPostConfigService;
import com.ruoyi.web.controller.biz.weekcheck.domain.WeekCheck;
import com.ruoyi.web.controller.biz.weekcheck.service.IWeekCheckService;

/**
 * 周检查 Controller
 */
@Controller
@RequestMapping("/week")
public class WeekController extends BaseController {

    @Autowired
    private IWeekCheckService weekCheckService;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysPostService postService;

    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private IAssessPostConfigService assessPostConfigService;

    @Autowired
    private ISysRoleService roleService;

    private String prefix = "week";

    /**
     * 打开周检查列表页
     */
    @RequiresPermissions("week:view")
    @GetMapping("/list")
    public String list(ModelMap mmap) {
        SysUser currentUser = ShiroUtils.getSysUser();

        // 加载部门列表（用于筛选）
        List<SysDept> depts = deptService.selectDeptList(new SysDept());
        mmap.put("depts", depts);

        // 加载岗位列表（用于筛选）
        List<SysPost> posts = postService.selectPostList(new SysPost());
        mmap.put("posts", posts);

        // 权限：编辑权限
        mmap.put("canEdit", ShiroUtils.getSubject().isPermitted("week:edit"));

        // 当前用户部门ID
        mmap.put("currentDeptId", currentUser.getDeptId());

        // 判断是否为分区领导
        List<SysPost> userPosts = postService.selectPostsByUserId(currentUser.getUserId());
        boolean isZoneLeader = userPosts.stream()
                .filter(SysPost::isFlag)
                .anyMatch(p -> p.getPostCode() != null && p.getPostCode().contains("fq_leader"));
        mmap.put("isZoneLeader", isZoneLeader);

        boolean isDeptLeader = ShiroUtils.getSubject().isPermitted("week:check");
        mmap.put("isDeptLeader", isDeptLeader);

        // 查询"干事"岗位ID（监区领导默认选中）
        SysPost queryPost = new SysPost();
        queryPost.setPostCode("sec");
        List<SysPost> secPosts = postService.selectPostList(queryPost);
        Long secPostId = secPosts.isEmpty() ? null : secPosts.get(0).getPostId();
        mmap.put("secPostId", secPostId);

        return prefix + "/list";
    }

    /**
     * 获取周检查数据（矩阵表格）
     */
    @RequiresPermissions("week:list")
    @GetMapping("/data")
    @ResponseBody
    public AjaxResult data(@RequestParam String week,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) Long postId) {
        SysUser currentUser = ShiroUtils.getSysUser();
        boolean isAdmin = currentUser.isAdmin();
        boolean isDeptLeader = ShiroUtils.getSubject().isPermitted("week:check");
        String loginName = currentUser.getLoginName();

        // 1. 非管理员 + 非监区领导：强制使用自己的部门
        if (!isAdmin && !isDeptLeader) {
            deptId = currentUser.getDeptId();
        }

        // 2. 获取目标岗位（复用季度考核的 common 类型配置）
        List<String> targetPostCodes = assessPostConfigService.getPostCodesByType("common");
        if (targetPostCodes.isEmpty()) {
            return success().put("data", Collections.emptyMap());
        }

        // 3. 查询该周该部门的所有检查记录（按 create_by 过滤）
        WeekCheck query = new WeekCheck();
        query.setWeek(week);
        if (deptId != null) {
            query.setDeptId(deptId);
        }
        List<SysPost> currentUserPosts = postService.selectPostsByUserId(currentUser.getUserId());
        // 分区领导只能看到自己填写的记录
        boolean isZoneLeader = currentUserPosts.stream()
                .filter(SysPost::isFlag)
                .anyMatch(p -> p.getPostCode() != null && p.getPostCode().contains("fq_leader"));
        if (!isAdmin && !isDeptLeader && isZoneLeader) {
            query.setCreateBy(loginName);
        }
        List<WeekCheck> checks = weekCheckService.selectCheckList(query);
        Map<Long, WeekCheck> checkMap = checks.stream()
                .collect(Collectors.toMap(WeekCheck::getUserId, c -> c, (c1, c2) -> c1));

        // 4. 查询该部门下所有用户
        SysUser userQuery = new SysUser();
        if (deptId != null) {
            userQuery.setDeptId(deptId);
        }
        List<SysUser> allUsers = userService.selectUserList(userQuery);

        // 5. 判断当前用户是否为分区领导

        String currentZone = extractZone(currentUserPosts);

        // 6. 过滤用户
        List<SysUser> targetUsers = new ArrayList<>();
        for (SysUser user : allUsers) {
            List<SysPost> userPosts = postService.selectPostsByUserId(user.getUserId());
            List<SysPost> actualPosts = userPosts.stream()
                    .filter(SysPost::isFlag)
                    .collect(Collectors.toList());

            // 1. 检查是否属于目标岗位（被考核人）
            boolean isTargetPost = actualPosts.stream()
                    .map(SysPost::getPostCode)
                    .anyMatch(targetPostCodes::contains);
            if (!isTargetPost) {
                continue;
            }

            // 2. 按岗位下拉框过滤（监区领导/管理员）
            if (postId != null && postId > 0) {
                boolean hasPost = actualPosts.stream()
                        .anyMatch(p -> p.getPostId().equals(postId));
                if (!hasPost) {
                    continue;
                }
            }

            // 3. 分区领导只能看到本分区人员
            if (isZoneLeader && !isAdmin && !isDeptLeader) {
                String postName = actualPosts.stream()
                        .findFirst()
                        .map(SysPost::getPostName)
                        .orElse("");
                if (StringUtils.isNotEmpty(currentZone) && !postName.contains(currentZone)) {
                    continue;
                }
            }

            targetUsers.add(user);
        }

        // 7. 组装返回数据
        List<Map<String, Object>> rows = new ArrayList<>();
        for (SysUser user : targetUsers) {
            Map<String, Object> row = new HashMap<>();
            row.put("userId", user.getUserId());
            row.put("userName", user.getUserName());

            // 获取岗位名称
            List<SysPost> userPosts = postService.selectPostsByUserId(user.getUserId());
            String postName = userPosts.stream()
                    .filter(SysPost::isFlag)
                    .findFirst()
                    .map(SysPost::getPostName)
                    .orElse("");
            row.put("postName", postName);

            // 从检查记录中取值
            WeekCheck check = checkMap.get(user.getUserId());
            if (check != null) {
                row.put("fourKnow", check.getFourKnow());
                row.put("noBook", check.getNoBook());
                row.put("keyPerson", check.getKeyPerson());
                row.put("dutyFamiliar", check.getDutyFamiliar());
                row.put("knowledgeMastery", check.getKnowledgeMastery());
                row.put("other", check.getOther());
                row.put("checkDate", check.getCreateTime() != null ? check.getCreateTime().toString() : "");
            } else {
                row.put("fourKnow", "");
                row.put("noBook", "");
                row.put("keyPerson", "");
                row.put("dutyFamiliar", "");
                row.put("knowledgeMastery", "");
                row.put("other", "");
                row.put("checkDate", "");
            }
            rows.add(row);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("rows", rows);
        return success().put("data", result);
    }

    /**
     * 保存周检查评分
     */
    @RequiresPermissions("week:edit")
    @PostMapping("/save")
    @ResponseBody
    public AjaxResult save(@RequestBody Map<String, Object> params) {
        String week = (String) params.get("week");
        Long deptId = params.get("deptId") != null ? Long.valueOf(params.get("deptId").toString()) : null;
        // Long postId = params.get("postId") != null ?
        // Long.valueOf(params.get("postId").toString()) : null;

        SysUser currentUser = ShiroUtils.getSysUser();
        String loginName = currentUser.getLoginName();
        boolean isAdmin = currentUser.isAdmin();
        boolean isDeptLeader = ShiroUtils.getSubject().isPermitted("week:check");

        // 非管理员 + 非监区领导：强制使用自己的部门
        if (!isAdmin && !isDeptLeader) {
            deptId = currentUser.getDeptId();
        }

        // 判断当前用户是否为分区领导
        List<SysPost> currentUserPosts = postService.selectPostsByUserId(currentUser.getUserId());
        boolean isZoneLeader = currentUserPosts.stream()
                .filter(SysPost::isFlag)
                .anyMatch(p -> p.getPostCode() != null && p.getPostCode().contains("fq_leader"));
        String currentZone = extractZone(currentUserPosts);

        List<Map<String, Object>> records = (List<Map<String, Object>>) params.get("records");
        if (records == null || records.isEmpty()) {
            return error("没有数据可保存");
        }

        // 如果是分区领导，校验其保存的用户是否都属于本分区
        if (isZoneLeader && !isAdmin && !isDeptLeader) {
            for (Map<String, Object> record : records) {
                Long userId = Long.valueOf(record.get("userId").toString());
                List<SysPost> userPosts = postService.selectPostsByUserId(userId);
                String postName = userPosts.stream()
                        .filter(SysPost::isFlag)
                        .findFirst()
                        .map(SysPost::getPostName)
                        .orElse("");
                if (StringUtils.isNotEmpty(currentZone) && !postName.contains(currentZone)) {
                    return error("您无权保存非本分区人员的数据：" + postName);
                }
            }
        }

        // 保存：根据 userId + week + createBy 判断存在则更新，否则插入
        try {
            for (Map<String, Object> record : records) {
                Long userId = Long.valueOf(record.get("userId").toString());
                String fourKnow = getString(record, "fourKnow");
                String noBook = getString(record, "noBook");
                String keyPerson = getString(record, "keyPerson");
                String dutyFamiliar = getString(record, "dutyFamiliar");
                String knowledgeMastery = getString(record, "knowledgeMastery");
                String other = getString(record, "other");

                // 查询是否已存在（userId + week + createBy）
                WeekCheck exist = weekCheckService.selectByUserWeekAndCreator(userId, week, loginName);
                if (exist != null) {
                    // 更新
                    exist.setFourKnow(fourKnow);
                    exist.setNoBook(noBook);
                    exist.setKeyPerson(keyPerson);
                    exist.setDutyFamiliar(dutyFamiliar);
                    exist.setKnowledgeMastery(knowledgeMastery);
                    exist.setOther(other);
                    exist.setUpdateBy(loginName);
                    weekCheckService.updateWeekCheck(exist);
                } else {
                    // 插入
                    WeekCheck insert = new WeekCheck();
                    insert.setUserId(userId);
                    insert.setWeek(week);
                    insert.setDeptId(deptId);
                    insert.setFourKnow(fourKnow);
                    insert.setNoBook(noBook);
                    insert.setKeyPerson(keyPerson);
                    insert.setDutyFamiliar(dutyFamiliar);
                    insert.setKnowledgeMastery(knowledgeMastery);
                    insert.setOther(other);
                    insert.setCreateBy(loginName);
                    weekCheckService.insertWeekCheck(insert);
                }
            }
            return success("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return error("保存失败：" + e.getMessage());
        }
    }

    /**
     * 从Map中安全获取字符串值
     */
    private String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : "";
    }

    /**
     * 从岗位列表中提取分区名称（如"一分区"、"二分区"）
     */
    private String extractZone(List<SysPost> posts) {
        String postName = posts.stream()
                .filter(SysPost::isFlag)
                .findFirst()
                .map(SysPost::getPostName)
                .orElse("");
        if (postName.contains("一分区"))
            return "一分区";
        if (postName.contains("二分区"))
            return "二分区";
        if (postName.contains("三分区"))
            return "三分区";
        return "";
    }

    // ============================================================
    // 监区领导查看填表情况（改为使用 create_by 分组统计）
    // ============================================================

    /**
     * 打开填表检查页面（监区领导专用）
     */
    @RequiresPermissions("week:check")
    @GetMapping("/check")
    public String check(ModelMap mmap) {
        // 加载部门列表
        List<SysDept> depts = deptService.selectDeptList(new SysDept());
        mmap.put("depts", depts);
        return "week/check";
    }

    /**
     * 获取填表情况汇总（监区领导专用）
     * 按 create_by 分组统计每个分区领导的填表情况
     */
    /**
     * 获取填表情况汇总（监区领导专用）
     * 按 create_by 分组统计每个分区领导的填表情况
     */
    @RequiresPermissions("week:check")
    @GetMapping("/summary")
    @ResponseBody
    public AjaxResult summary(@RequestParam String week,
            @RequestParam(required = false) Long deptId) {
        // 1. 查询该周所有检查记录
        WeekCheck query = new WeekCheck();
        query.setWeek(week);
        if (deptId != null) {
            query.setDeptId(deptId);
        }
        List<WeekCheck> checks = weekCheckService.selectCheckList(query);

        // 2. 按 create_by 分组
        Map<String, List<WeekCheck>> groupMap = checks.stream()
                .collect(Collectors.groupingBy(WeekCheck::getCreateBy));

        // 3. 查询所有部门
        List<SysDept> depts = deptService.selectDeptList(new SysDept());
        if (deptId != null) {
            depts = depts.stream().filter(d -> d.getDeptId().equals(deptId)).collect(Collectors.toList());
        }

        // 4. 获取目标岗位（用于计算应填人数）
        List<String> targetPostCodes = assessPostConfigService.getPostCodesByType("common");

        // 5. 组装数据
        List<Map<String, Object>> rows = new ArrayList<>();
        for (SysDept dept : depts) {
            // 查询该部门所有用户
            SysUser userQuery = new SysUser();
            userQuery.setDeptId(dept.getDeptId());
            List<SysUser> users = userService.selectUserList(userQuery);

            // 找出该部门所有分区领导（岗位编码包含 fq_leader）
            List<String> zoneLeaderLoginNames = users.stream()
                    .filter(u -> {
                        List<SysPost> posts = postService.selectPostsByUserId(u.getUserId());
                        return posts.stream()
                                .filter(SysPost::isFlag)
                                .anyMatch(p -> p.getPostCode() != null && p.getPostCode().contains("fq_leader"));
                    })
                    .map(SysUser::getLoginName)
                    .collect(Collectors.toList());

            // 计算该部门应填人数（被考核人数量）
            int totalCount = 0;
            for (SysUser user : users) {
                List<SysPost> posts = postService.selectPostsByUserId(user.getUserId());
                boolean isTarget = posts.stream()
                        .filter(SysPost::isFlag)
                        .map(SysPost::getPostCode)
                        .anyMatch(targetPostCodes::contains);
                if (isTarget) {
                    totalCount++;
                }
            }

            // ============================================================
            // 第一部分：分区领导（全部展示，含未填）
            // ============================================================
            for (SysUser leader : users) {
                // 只处理分区领导
                List<SysPost> posts = postService.selectPostsByUserId(leader.getUserId());
                boolean isZoneLeader = posts.stream()
                        .filter(SysPost::isFlag)
                        .anyMatch(p -> p.getPostCode() != null && p.getPostCode().contains("fq_leader"));
                if (!isZoneLeader)
                    continue;

                Map<String, Object> row = new HashMap<>();
                row.put("deptId", dept.getDeptId());
                row.put("deptName", dept.getDeptName());
                row.put("leaderId", leader.getUserId());
                row.put("leaderName", leader.getUserName());
                row.put("loginName", leader.getLoginName());
                row.put("week", week);
                row.put("roleType", "zoneLeader"); // 标识：分区领导

                // 该领导填写的记录
                List<WeekCheck> leaderChecks = groupMap.getOrDefault(leader.getLoginName(), new ArrayList<>());
                int filledCount = leaderChecks.size();
                row.put("filledCount", filledCount);
                row.put("totalCount", totalCount);

                // 状态判断
                String status;
                if (filledCount == 0) {
                    status = "未填写";
                } else if (filledCount >= totalCount) {
                    status = "已填完";
                } else {
                    status = "部分填写";
                }
                row.put("status", status);

                // 填表时间
                row.put("fillTime", filledCount > 0 ? leaderChecks.get(0).getCreateTime() : null);

                rows.add(row);
            }

            // ============================================================
            // 第二部分：监区领导（只展示已填的）
            // ============================================================
            // 遍历 groupMap，找出该部门下已填表的非分区领导用户
            System.out.println("=== [调试] groupMap keys: " + groupMap.keySet());
            System.out.println("=== [调试] zoneLeaderLoginNames: " + zoneLeaderLoginNames);

            for (Map.Entry<String, List<WeekCheck>> entry : groupMap.entrySet()) {
                String loginName = entry.getKey();
                List<WeekCheck> leaderChecks = entry.getValue();

                System.out.println("=== [调试] 处理用户: " + loginName + ", 记录数: " + leaderChecks.size());

                // 跳过已是分区领导的用户
                if (zoneLeaderLoginNames.contains(loginName)) {
                    System.out.println("=== [调试] " + loginName + " 在 zoneLeaderLoginNames 中，跳过");
                    continue;
                }

                SysUser user = userService.selectUserByLoginName(loginName);
                if (user == null) {
                    System.out.println("=== [调试] " + loginName + " 用户不存在");
                    continue;
                }

                if (!user.getDeptId().equals(dept.getDeptId())) {
                    System.out.println("=== [调试] " + loginName + " 部门不匹配: user.deptId=" + user.getDeptId() + ", 当前dept="
                            + dept.getDeptId());
                    continue;
                }

                // 判断是否为监区领导（通过岗位编码）
                List<SysPost> posts = postService.selectPostsByUserId(user.getUserId());
                System.out.println("=== [调试] " + loginName + " 的岗位: "
                        + posts.stream().map(SysPost::getPostCode).collect(Collectors.toList()));

                boolean isDeptLeader = posts.stream()
                        .anyMatch(p -> p.getPostCode() != null && p.getPostCode().contains("jq_leader"));
                System.out.println("=== [调试] " + loginName + " 是否为监区领导(jq_leader): " + isDeptLeader);

                if (!isDeptLeader)
                    continue;

                // 添加到结果
                System.out.println("=== [调试] " + loginName + " 通过所有检查，添加到结果");

                Map<String, Object> row = new HashMap<>();
                row.put("deptId", dept.getDeptId());
                row.put("deptName", dept.getDeptName());
                row.put("leaderId", user.getUserId());
                row.put("leaderName", user.getUserName());
                row.put("loginName", user.getLoginName());
                row.put("week", week);
                row.put("roleType", "deptLeader");
                row.put("status", "已填写");
                row.put("filledCount", leaderChecks.size());
                row.put("totalCount", totalCount);
                row.put("fillTime", leaderChecks.get(0).getCreateTime());

                rows.add(row);
            }
        }

        // 按部门排序
        rows.sort((a, b) -> {
            String nameA = (String) a.get("deptName");
            String nameB = (String) b.get("deptName");
            return nameA.compareTo(nameB);
        });

        return success().put("data", rows);
    }

    /**
     * 检查用户是否拥有指定权限
     */
    private boolean checkUserHasPermi(Long userId, String perms) {
        // 方法1：通过角色判断（如果监区领导有固定角色）
        // 查询该用户的角色列表，判断是否包含监区领导角色
        List<SysRole> roles = roleService.selectRolesByUserId(userId);
        return roles.stream().anyMatch(r -> "dept_leader".equals(r.getRoleKey()));
    }

    /**
     * 查看某领导填写的周检查详情（只读）
     * 原 masterId 改为 loginName + week 查询
     */
    /**
     * 查看某领导填写的周检查详情（只读）
     * 通过 loginName + week + deptId 查询该领导填写的所有记录
     */
    @RequiresPermissions("week:check")
    @GetMapping("/detail")
    @ResponseBody
    public AjaxResult detail(@RequestParam String loginName,
            @RequestParam String week,
            @RequestParam Long deptId) {
        // 查询该领导该周该部门填写的所有记录
        WeekCheck query = new WeekCheck();
        query.setWeek(week);
        query.setCreateBy(loginName);
        query.setDeptId(deptId);
        List<WeekCheck> checks = weekCheckService.selectCheckList(query);

        if (checks.isEmpty()) {
            return error("该领导尚未填写本周检查表");
        }

        // 查询被考核人信息
        List<Map<String, Object>> rows = new ArrayList<>();
        for (WeekCheck c : checks) {
            SysUser user = userService.selectUserById(c.getUserId());
            Map<String, Object> row = new HashMap<>();
            row.put("userId", c.getUserId());
            row.put("userName", user != null ? user.getUserName() : "未知");
            row.put("fourKnow", c.getFourKnow());
            row.put("noBook", c.getNoBook());
            row.put("keyPerson", c.getKeyPerson());
            row.put("dutyFamiliar", c.getDutyFamiliar());
            row.put("knowledgeMastery", c.getKnowledgeMastery());
            row.put("other", c.getOther());
            rows.add(row);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("checks", rows);
        result.put("createBy", loginName);
        result.put("week", week);
        result.put("deptId", deptId);
        return success().put("data", result);
    }

    /**
     * 查看详情页面
     */
    @RequiresPermissions("week:check")
    @GetMapping("/detailPage")
    public String detailPage(@RequestParam String loginName,
            @RequestParam String week,
            @RequestParam Long deptId,
            ModelMap mmap) {
        mmap.put("loginName", loginName);
        mmap.put("week", week);
        mmap.put("deptId", deptId);
        return "week/detail";
    }
}