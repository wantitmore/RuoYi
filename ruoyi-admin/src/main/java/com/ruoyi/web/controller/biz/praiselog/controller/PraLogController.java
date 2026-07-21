package com.ruoyi.web.controller.biz.praiselog.controller;

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
import com.ruoyi.web.controller.biz.praiselog.domain.PraLog;
import com.ruoyi.web.controller.biz.praiselog.service.IPraLogService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 执勤扬台账Controller
 * 
 * @author ruoyi
 * @date 2026-07-07
 */
@Controller
@RequestMapping("/praiselog/list")
public class PraLogController extends BaseController {
    private String prefix = "praiselog/list";

    @Autowired
    private IPraLogService praLogService;

    @GetMapping("/test")
    @ResponseBody
    public String test() {
        return "hello";
    }

    @RequiresPermissions("pralog:view")
    @GetMapping()
    public String list(ModelMap mmap) {
        mmap.put("canAdd", ShiroUtils.getSubject().isPermitted("pralog:add"));
        mmap.put("canEdit", ShiroUtils.getSubject().isPermitted("pralog:edit"));
        mmap.put("canRemove", ShiroUtils.getSubject().isPermitted("pralog:remove"));
        mmap.put("canExport", ShiroUtils.getSubject().isPermitted("pralog:export"));
        return prefix + "/list";
    }

    /**
     * 查询执勤扬台账列表
     */
    @RequiresPermissions("pralog:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(PraLog praLog) {
        startPage();
        if (!ShiroUtils.getSysUser().isAdmin()) {
            praLog.setDeptId(ShiroUtils.getSysUser().getDeptId());
        }
        List<PraLog> list = praLogService.selectPraLogList(praLog);
        return getDataTable(list);
    }

    /**
     * 导出执勤扬台账列表
     */
    @RequiresPermissions("pralog:export")
    @Log(title = "执勤扬台账", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(PraLog praLog) {
        List<PraLog> list = praLogService.selectPraLogList(praLog);
        ExcelUtil<PraLog> util = new ExcelUtil<PraLog>(PraLog.class);
        return util.exportExcel(list, "执勤扬台账数据");
    }

    /**
     * 新增执勤扬台账
     */
    @RequiresPermissions("pralog:add")
    @GetMapping("/add")
    public String add() {
        return prefix + "/add";
    }

    /**
     * 新增保存执勤扬台账
     */
    @RequiresPermissions("pralog:add")
    @Log(title = "执勤扬台账", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(PraLog praLog) {
        if (praLog.getDutyDate() != null) {
            praLog.setDeptId(ShiroUtils.getSysUser().getDeptId());
            praLog.setBatchNo(new SimpleDateFormat("yyyy-MM").format(praLog.getDutyDate()));
        } else {
            return error("执勤日期不能为空");
        }
        return toAjax(praLogService.insertPraLog(praLog));
    }

    /**
     * 修改执勤扬台账
     */
    @RequiresPermissions("pralog:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap) {
        PraLog praLog = praLogService.selectPraLogById(id);
        mmap.put("praLog", praLog);
        return prefix + "/edit";
    }

    /**
     * 修改保存执勤扬台账
     */
    @RequiresPermissions("pralog:edit")
    @Log(title = "执勤扬台账", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(PraLog praLog) {
        if (praLog.getDutyDate() != null) {
            praLog.setBatchNo(new SimpleDateFormat("yyyy-MM").format(praLog.getDutyDate()));
        }
        return toAjax(praLogService.updatePraLog(praLog));
    }

    /**
     * 删除执勤扬台账
     */
    @RequiresPermissions("pralog:remove")
    @Log(title = "执勤扬台账", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(praLogService.deletePraLogByIds(ids));
    }
}
