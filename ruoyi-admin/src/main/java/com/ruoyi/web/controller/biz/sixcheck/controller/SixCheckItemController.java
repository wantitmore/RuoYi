package com.ruoyi.web.controller.biz.sixcheck.controller;

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
import com.ruoyi.web.controller.biz.sixcheck.domain.SixCheckItem;
import com.ruoyi.web.controller.biz.sixcheck.service.ISixCheckItemService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 六必查项目Controller
 * 
 * @author zack
 * @date 2026-07-01
 */
@Controller
@RequestMapping("/sixcheck/item")
public class SixCheckItemController extends BaseController {
    private String prefix = "sixcheck/item";

    @Autowired
    private ISixCheckItemService sixCheckItemService;

    @RequiresPermissions("sixcheck:item:view")
    @GetMapping()
    public String item() {
        return prefix + "/item";
    }

    /**
     * 查询六必查项目列表
     */
    @RequiresPermissions("sixcheck:item:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(SixCheckItem sixCheckItem) {
        startPage();
        if (!ShiroUtils.getSysUser().isAdmin()) {
            sixCheckItem.setDeptId(ShiroUtils.getSysUser().getDeptId());
        }
        List<SixCheckItem> list = sixCheckItemService.selectSixCheckItemList(sixCheckItem);
        return getDataTable(list);
    }

    /**
     * 导出六必查项目列表
     */
    @RequiresPermissions("sixcheck:item:export")
    @Log(title = "六必查项目", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(SixCheckItem sixCheckItem) {
        List<SixCheckItem> list = sixCheckItemService.selectSixCheckItemList(sixCheckItem);
        ExcelUtil<SixCheckItem> util = new ExcelUtil<SixCheckItem>(SixCheckItem.class);
        return util.exportExcel(list, "六必查项目数据");
    }

    /**
     * 新增六必查项目
     */
    @RequiresPermissions("sixcheck:item:add")
    @GetMapping("/add")
    public String add() {
        return prefix + "/add";
    }

    /**
     * 新增保存六必查项目
     */
    @RequiresPermissions("sixcheck:item:add")
    @Log(title = "六必查项目", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(SixCheckItem sixCheckItem) {
        sixCheckItem.setDeptId(ShiroUtils.getSysUser().getDeptId());
        return toAjax(sixCheckItemService.insertSixCheckItem(sixCheckItem));
    }

    /**
     * 修改六必查项目
     */
    @RequiresPermissions("sixcheck:item:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap) {
        SixCheckItem sixCheckItem = sixCheckItemService.selectSixCheckItemById(id);
        mmap.put("sixCheckItem", sixCheckItem);
        return prefix + "/edit";
    }

    /**
     * 修改保存六必查项目
     */
    @RequiresPermissions("sixcheck:item:edit")
    @Log(title = "六必查项目", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(SixCheckItem sixCheckItem) {
        return toAjax(sixCheckItemService.updateSixCheckItem(sixCheckItem));
    }

    /**
     * 删除六必查项目
     */
    @RequiresPermissions("sixcheck:item:remove")
    @Log(title = "六必查项目", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(sixCheckItemService.deleteSixCheckItemByIds(ids));
    }
}
