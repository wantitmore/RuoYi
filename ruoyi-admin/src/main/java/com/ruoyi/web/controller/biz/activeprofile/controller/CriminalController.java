/**
 * 
 * 
 * @author Zack
 * @date Sep 15, 2026
 */
package com.ruoyi.web.controller.biz.activeprofile.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.web.controller.biz.activeprofile.domain.BizCriminal;
import com.ruoyi.web.controller.biz.activeprofile.domain.CriminalImportDto;
import com.ruoyi.web.controller.biz.activeprofile.service.IBizCriminalService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/biz/activeProfile")
public class CriminalController extends BaseController {

    private String prefix = "activeprofile";

    @Autowired
    private IBizCriminalService criminalService;

    @Autowired
    private ISysUserService userService;
    @Autowired
    private ISysDeptService deptService;

    /** 列表页 */
    @RequiresPermissions("biz:activeprofile:view")
    @GetMapping()
    public String page(ModelMap mmap) {
        // 用权限标识判断，不出现任何角色名
        boolean canTransferDept = ShiroUtils.getSubject()
                .isPermitted("biz:activeprofile:transferDept");
        mmap.put("canTransferDept", canTransferDept);

        // 部门切换：只有上级单位（本部门下存在子部门）才显示下拉框
        SysUser currentUser = ShiroUtils.getSysUser();
        boolean canSwitchDept = currentUser.getDeptId() != null
                && deptService.selectNormalChildrenDeptById(currentUser.getDeptId()) > 0;
        mmap.put("canSwitchDept", canSwitchDept);
        return prefix + "/list";
    }

    /** 列表数据 */
    @RequiresPermissions("biz:activeprofile:view")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(BizCriminal query,
            @RequestParam(required = false, defaultValue = "my") String filterType) {
        startPage();
        SysUser user = ShiroUtils.getSysUser();

        if ("my".equals(filterType)) {
            // 只查我专管的
            query.setOwnerId(user.getUserId());
        } else if ("public".equals(filterType)) {
            // 只查本部门公共的
            query.setOwnerIdNull(true);
        }
        // "all" 不加限制，交给 @DataScope 控制部门范围

        List<BizCriminal> list = criminalService.selectCriminalList(query);
        return getDataTable(list);
    }

    /** 详情页 */
    @RequiresPermissions("biz:activeprofile:view")
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, ModelMap mmap) {
        BizCriminal criminal = criminalService.selectCriminalById(id);
        mmap.put("criminal", criminal);
        return prefix + "/detail";
    }

    /** 详情数据（AJAX 拉取） */
    @RequiresPermissions("biz:activeprofile:view")
    @GetMapping("/get/{id}")
    @ResponseBody
    public AjaxResult getInfo(@PathVariable Long id) {
        return success(criminalService.selectCriminalById(id));
    }

    /** 新增页 */
    @RequiresPermissions("biz:activeprofile:add")
    @GetMapping("/add")
    public String add(ModelMap mmap) {
        List<SysDept> depts = deptService.selectDeptList(new SysDept());
        mmap.put("depts", depts);

        // 用户列表（本部门）
        SysUser query = new SysUser();
        query.setStatus("0");
        query.setDeptId(ShiroUtils.getSysUser().getDeptId());
        List<SysUser> users = userService.selectUserList(query);
        mmap.put("users", users);

        return prefix + "/add";
    }

    /** 新增保存 */
    @RequiresPermissions("biz:activeprofile:add")
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(@RequestBody BizCriminal criminal) {
        return toAjax(criminalService.insertCriminal(criminal));
    }

    /** 编辑保存（含 JSON） */
    @RequiresPermissions("biz:activeprofile:edit")
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(@RequestBody BizCriminal criminal) {
        return toAjax(criminalService.updateCriminal(criminal));
    }

    /** 删除 */
    @RequiresPermissions("biz:activeprofile:remove")
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(@RequestParam Long id) {
        return toAjax(criminalService.deleteCriminalById(id));
    }

    /** 认领 */
    @RequiresPermissions("biz:activeprofile:edit")
    @PostMapping("/claim")
    @ResponseBody
    public AjaxResult claim(@RequestParam Long id) {
        return toAjax(criminalService.claim(id));
    }

    /** 退回公共 */
    @RequiresPermissions("biz:activeprofile:edit")
    @PostMapping("/returnPublic")
    @ResponseBody
    public AjaxResult returnPublic(@RequestParam Long id) {
        return toAjax(criminalService.returnToPublic(id));
    }

    /** 转给人 */
    @RequiresPermissions("biz:activeprofile:edit")
    @PostMapping("/transferToUser")
    @ResponseBody
    public AjaxResult transferToUser(@RequestParam Long id, @RequestParam Long targetUserId) {
        return toAjax(criminalService.transferToUser(id, targetUserId));
    }

    /** 批量转给人 */
    @RequiresPermissions("biz:activeprofile:edit")
    @PostMapping("/batchTransferToUser")
    @ResponseBody
    public AjaxResult batchTransferToUser(@RequestParam Long[] ids, @RequestParam Long targetUserId) {
        return toAjax(criminalService.batchTransferToUser(ids, targetUserId));
    }

    /** 转给部门 */
    @RequiresPermissions("biz:activeprofile:transferDept")
    @PostMapping("/transferToDept")
    @ResponseBody
    public AjaxResult transferToDept(@RequestParam Long id, @RequestParam Long targetDeptId) {
        return toAjax(criminalService.transferToDept(id, targetDeptId));
    }

    /** 批量转给部门 */
    @RequiresPermissions("biz:activeprofile:transferDept")
    @PostMapping("/batchTransferToDept")
    @ResponseBody
    public AjaxResult batchTransferToDept(@RequestParam Long[] ids, @RequestParam Long targetDeptId) {
        return toAjax(criminalService.batchTransferToDept(ids, targetDeptId));
    }

    /**
     * 获取当前用户所在部门的用户列表（用于转移弹窗）
     */
    @RequiresPermissions("biz:activeprofile:view")
    @GetMapping("/userList")
    @ResponseBody
    public AjaxResult userList() {
        SysUser currentUser = ShiroUtils.getSysUser();
        SysUser query = new SysUser();
        query.setDeptId(currentUser.getDeptId());
        query.setStatus("0");
        List<SysUser> users = userService.selectUserList(query);
        // 只返回必要字段，减小返回体
        List<Map<String, Object>> result = new ArrayList<>();
        for (SysUser u : users) {
            Map<String, Object> item = new HashMap<>();
            item.put("userId", u.getUserId());
            item.put("userName", u.getUserName());
            item.put("loginName", u.getLoginName());
            result.add(item);
        }
        return success(result);
    }

    /**
     * 获取部门列表（用于“转给部门”下拉）
     */
    @RequiresPermissions("biz:activeprofile:view")
    @GetMapping("/deptList")
    @ResponseBody
    public AjaxResult deptList() {
        List<SysDept> depts = criminalService.selectAllDepts();
        List<Map<String, Object>> result = new ArrayList<>();
        for (SysDept d : depts) {
            Map<String, Object> item = new HashMap<>();
            item.put("deptId", d.getDeptId());
            item.put("deptName", d.getDeptName());
            result.add(item);
        }
        return success(result);
    }

    /**
     * 上级单位切换查看监区档案时，下拉框可选的监区列表。
     * 返回当前用户数据范围内（若依数据权限）的“所有下级部门”，按层级缩进排序；
     * 本监区账号（部门下无子部门）返回空列表，前端据此隐藏下拉框。
     */
    @RequiresPermissions("biz:activeprofile:view")
    @GetMapping("/switchDeptList")
    @ResponseBody
    public AjaxResult switchDeptList() {
        SysUser currentUser = ShiroUtils.getSysUser();
        Long currentDeptId = currentUser.getDeptId();
        List<Map<String, Object>> result = new ArrayList<>();

        // 1. 检查子部门数量
        int childCount = deptService.selectNormalChildrenDeptById(currentDeptId);
        System.out.println("=== 当前部门ID: " + currentDeptId + "，直属下级数量: " + childCount);

        if (currentDeptId == null || childCount <= 0) {
            return success(result);
        }

        // 2. 检查查出来的部门列表
        List<SysDept> depts = deptService.selectDeptList(new SysDept());
        System.out.println("=== 权限内能查到的部门总数: " + depts.size());

        String selfKey = "," + currentDeptId + ",";
        for (SysDept d : depts) {
            // 3. 检查每个部门的 ancestors 匹配情况
            System.out.println("=== 正在比对: " + d.getDeptName() + "，ancestors=" + d.getAncestors());

            if (currentDeptId.equals(d.getDeptId())) {
                continue; // 过滤自己
            }
            String ancestors = d.getAncestors();
            // 这里加个 trim()，防止数据库里有空格导致匹配失败
            if (StringUtils.isEmpty(ancestors) || !("," + ancestors.trim() + ",").contains(selfKey)) {
                System.out.println("    -> 不是当前部门的下级，被过滤");
                continue;
            }

            // 计算层级...
            String[] ids = ancestors.split(",");
            int idx = -1;
            for (int i = 0; i < ids.length; i++) {
                if (String.valueOf(currentDeptId).equals(ids[i].trim())) {
                    idx = i;
                    break;
                }
            }
            int level = idx < 0 ? 0 : Math.max(ids.length - idx - 2, 0);

            Map<String, Object> item = new HashMap<>();
            item.put("deptId", d.getDeptId());
            item.put("deptName", d.getDeptName());
            item.put("parentId", d.getParentId());
            item.put("level", level);
            result.add(item);
            System.out.println("    -> 成功加入下拉框");
        }
        return success(result);
    }

    @RequiresPermissions("biz:activeprofile:view")
    @PostMapping("/countByType")
    @ResponseBody
    public AjaxResult countByType(@RequestParam(required = false) Long deptId) {
        SysUser user = ShiroUtils.getSysUser();
        Map<String, Object> result = new HashMap<>();

        // 我的专管（只统计本人，不受监区切换影响）
        BizCriminal q1 = new BizCriminal();
        q1.setOwnerId(user.getUserId());
        result.put("my", criminalService.selectCriminalList(q1).size());

        // 本部门公共
        BizCriminal q2 = new BizCriminal();
        q2.setOwnerIdNull(true);
        q2.setDeptId(deptId);
        result.put("publicCount", criminalService.selectCriminalList(q2).size());

        // 本部门全部
        BizCriminal q3 = new BizCriminal();
        q3.setDeptId(deptId);
        result.put("all", criminalService.selectCriminalList(q3).size());

        return success(result);
    }

    /**
     * 批量导入 Excel
     */
    @RequiresPermissions("biz:activeprofile:add")
    @PostMapping("/importExcel")
    @ResponseBody
    public AjaxResult importExcel(@RequestParam("file") MultipartFile file) {
        try {
            String msg = criminalService.importExcel(file);
            return success(msg);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    /**
     * 下载导入模板
     */
    @RequiresPermissions("biz:activeprofile:add") // 借用新增权限即可
    @GetMapping("/exportTemplate")
    public void exportTemplate(HttpServletResponse response) {
        ExcelUtil<CriminalImportDto> util = new ExcelUtil<>(CriminalImportDto.class);
        // 传入空列表，就会自动生成一个只有表头的模板
        util.exportExcel(response, new ArrayList<>(), "档案导入模板");
    }
}