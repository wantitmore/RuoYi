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

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.web.controller.biz.activeprofile.domain.BizCriminal;
import com.ruoyi.web.controller.biz.activeprofile.service.IBizCriminalService;

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
        System.out.println("=== canTransferDept = " + canTransferDept);
        return prefix + "/list";
    }

    /** 列表数据 */
    @RequiresPermissions("biz:activeprofile:view")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(BizCriminal query,
            @RequestParam(required = false, defaultValue = "my") String filterType) {
        System.out.println("=== filterType = " + filterType);
        System.out.println(">>> @RequestParam filterType = {}" + filterType);
        System.out.println(">>> BizCriminal.filterType = {}" + query.getFilterType());
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

    @RequiresPermissions("biz:activeprofile:view")
    @PostMapping("/countByType")
    @ResponseBody
    public AjaxResult countByType() {
        SysUser user = ShiroUtils.getSysUser();
        Map<String, Object> result = new HashMap<>();

        // 我的专管
        BizCriminal q1 = new BizCriminal();
        q1.setOwnerId(user.getUserId());
        result.put("my", criminalService.selectCriminalList(q1).size());

        // 本部门公共
        BizCriminal q2 = new BizCriminal();
        q2.setOwnerIdNull(true);
        result.put("publicCount", criminalService.selectCriminalList(q2).size());

        // 本部门全部
        BizCriminal q3 = new BizCriminal();
        result.put("all", criminalService.selectCriminalList(q3).size());

        return success(result);
    }
}