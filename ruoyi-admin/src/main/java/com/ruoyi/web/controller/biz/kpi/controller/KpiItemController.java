package com.ruoyi.web.controller.biz.kpi.controller;

import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.web.controller.biz.kpi.domain.KpiItem;
import com.ruoyi.web.controller.biz.kpi.service.IKpiItemService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 考核项目Controller
 * 
 * @author 莲塘
 * @date 2026-05-31
 */
@Controller
@RequestMapping("/kpi/item")
public class KpiItemController extends BaseController {
    private String prefix = "kpi/item";

    @Autowired
    private IKpiItemService kpiItemService;

    @Autowired
    private ISysDeptService deptService;

    // ---------- 页面跳转 ----------
    @RequiresPermissions("kpi:item:view")
    @GetMapping()
    public String item(ModelMap mmap) {
        mmap.put("categoryReqMap", kpiItemService.selectCategoryRequirementMap());
        return prefix + "/item";
    }

    // ---------- 列表查询 ----------
    @RequiresPermissions("kpi:item:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(KpiItem item) {
        startPage();
        // 数据权限：非管理员只能看自己部门的考核项目
        if (!ShiroUtils.getSysUser().isAdmin()) {
            item.setDeptId(ShiroUtils.getSysUser().getDeptId());
        }
        List<KpiItem> list = kpiItemService.selectKpiItemList(item);
        return getDataTable(list);
    }

    // ---------- 导出 ----------
    @RequiresPermissions("kpi:item:export")
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(KpiItem item) {
        List<KpiItem> list = kpiItemService.selectKpiItemList(item);
        ExcelUtil<KpiItem> util = new ExcelUtil<>(KpiItem.class);
        return util.exportExcel(list, "考核项目");
    }

    // ---------- 新增页面 ----------
    @GetMapping("/add")
    public String add(ModelMap mmap) {
        mmap.put("categoryReqMap", kpiItemService.selectCategoryRequirementMap());
        if (!ShiroUtils.getSysUser().isAdmin()) {
            SysDept dept = ShiroUtils.getSysUser().getDept();
            mmap.put("deptId", dept.getDeptId());
            mmap.put("deptName", dept.getDeptName());
            mmap.put("deptDisabled", true);
        } else {
            mmap.put("depts", deptService.selectDeptList(new SysDept()));
            mmap.put("deptDisabled", false);
        }
        return prefix + "/add";
    }

    // ---------- 新增保存 ----------
    @RequiresPermissions("kpi:item:add")
    @Log(title = "考核项目", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(KpiItem item) {
        // 非管理员强制绑定自己部门，忽略前端传来的 deptId
        if (!ShiroUtils.getSysUser().isAdmin()) {
            item.setDeptId(ShiroUtils.getSysUser().getDeptId());
        }
        item.setCreateBy(ShiroUtils.getLoginName());
        return toAjax(kpiItemService.insertKpiItem(item));
    }

    // ---------- 编辑页面 ----------
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap) {
        KpiItem item = kpiItemService.selectKpiItemById(id);
        if (item == null) {
        return "error/404";
    }
        mmap.put("kpiItem", item);
        mmap.put("categoryReqMap", kpiItemService.selectCategoryRequirementMap());
        if (!ShiroUtils.getSysUser().isAdmin()) {
            SysDept dept = ShiroUtils.getSysUser().getDept();
            if (!dept.getDeptId().equals(item.getDeptId())) {
                // 越权：跳转错误页或直接抛异常
                return "error/unauthorized";
            }
            mmap.put("deptId", dept.getDeptId());
            mmap.put("deptName", dept.getDeptName());
            mmap.put("deptDisabled", true);
        } else {
            mmap.put("depts", deptService.selectDeptList(new SysDept()));
            mmap.put("deptDisabled", false);
        }
        return prefix + "/edit";
    }

    // ---------- 编辑保存 ----------
    @RequiresPermissions("kpi:item:edit")
    @Log(title = "考核项目", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(KpiItem item) {
        if (!ShiroUtils.getSysUser().isAdmin()) {
            // 非管理员只能修改自己部门的项目，且不能更改部门
            item.setDeptId(ShiroUtils.getSysUser().getDeptId());
        }
        item.setUpdateBy(ShiroUtils.getLoginName());
        return toAjax(kpiItemService.updateKpiItem(item));
    }

    // ---------- 删除 ----------
    @RequiresPermissions("kpi:item:remove")
    @Log(title = "考核项目", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        // 如果需要校验部门权限，可以在 Service 中实现
        return toAjax(kpiItemService.deleteKpiItemByIds(ids));
    }
}
