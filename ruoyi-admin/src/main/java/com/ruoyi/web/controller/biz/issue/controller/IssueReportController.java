package com.ruoyi.web.controller.biz.issue.controller;

import java.text.SimpleDateFormat;
import java.util.List;
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
import com.ruoyi.web.controller.biz.issue.domain.IssueReport;
import com.ruoyi.web.controller.biz.issue.service.IIssueReportService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 问题通报Controller
 * 
 * @author ruoyi
 * @date 2026-07-08
 */
@Controller
@RequestMapping("/issue/list")
public class IssueReportController extends BaseController {
    private String prefix = "issue/list";

    @Autowired
    private IIssueReportService issueReportService;

    @RequiresPermissions("issue:list")
    @GetMapping()
    public String list(ModelMap mmap) {
        mmap.put("canAdd", ShiroUtils.getSubject().isPermitted("issue:add"));
        mmap.put("canEdit", ShiroUtils.getSubject().isPermitted("issue:edit"));
        mmap.put("canRemove", ShiroUtils.getSubject().isPermitted("issue:remove"));
        return prefix + "/list";
    }

    /**
     * 查询问题通报列表
     */
    @RequiresPermissions("issue:list:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(IssueReport issueReport) {
        startPage();
        if (!ShiroUtils.getSysUser().isAdmin()) {
            issueReport.setDeptId(ShiroUtils.getSysUser().getDeptId());
        }
        List<IssueReport> list = issueReportService.selectIssueReportList(issueReport);
        return getDataTable(list);
    }

    /**
     * 导出问题通报列表
     */
    @RequiresPermissions("issue:export")
    @Log(title = "问题通报", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(IssueReport issueReport) {
        List<IssueReport> list = issueReportService.selectIssueReportList(issueReport);
        ExcelUtil<IssueReport> util = new ExcelUtil<IssueReport>(IssueReport.class);
        return util.exportExcel(list, "问题通报数据");
    }

    /**
     * 新增问题通报
     */
    @RequiresPermissions("issue:add")
    @GetMapping("/add")
    public String add() {
        return prefix + "/add";
    }

    /**
     * 新增保存问题通报
     */
    @RequiresPermissions("issue:add")
    @Log(title = "问题通报", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(IssueReport issue) {
        if (issue.getIssueDate() != null) {
            System.out.println("issue.getIssueDate() =通报 " + issue.getIssueDate());
            issue.setDeptId(ShiroUtils.getSysUser().getDeptId());
            issue.setBatchNo(new SimpleDateFormat("yyyy-MM").format(issue.getIssueDate()));
        } else {
            return error("时间不能为空");
        }
        return toAjax(issueReportService.insertIssueReport(issue));
    }

    /**
     * 修改问题通报
     */
    @RequiresPermissions("issue:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap) {
        IssueReport issueReport = issueReportService.selectIssueReportById(id);
        mmap.put("issueReport", issueReport);
        return prefix + "/edit";
    }

    /**
     * 修改保存问题通报
     */
    @RequiresPermissions("issue:edit")
    @Log(title = "问题通报", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(IssueReport issue) {
        if (issue.getIssueDate() != null) {
            issue.setBatchNo(new SimpleDateFormat("yyyy-MM").format(issue.getIssueDate()));
        }
        return toAjax(issueReportService.updateIssueReport(issue));
    }

    /**
     * 删除问题通报
     */
    @RequiresPermissions("issue:remove")
    @Log(title = "问题通报", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(issueReportService.deleteIssueReportByIds(ids));
    }
}
