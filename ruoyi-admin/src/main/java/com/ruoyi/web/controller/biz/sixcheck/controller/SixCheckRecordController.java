package com.ruoyi.web.controller.biz.sixcheck.controller;

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
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.web.controller.biz.sixcheck.domain.SixCheckItem;
import com.ruoyi.web.controller.biz.sixcheck.domain.SixCheckRecord;
import com.ruoyi.web.controller.biz.sixcheck.service.ISixCheckItemService;
import com.ruoyi.web.controller.biz.sixcheck.service.ISixCheckRecordService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.ShiroUtils;

/**
 * 六必查记录Controller
 * 
 * @author ruoyi
 * @date 2026-07-01
 */
@Controller
@RequestMapping("/sixcheck/record")
public class SixCheckRecordController extends BaseController {
    private String prefix = "sixcheck/record";

    @Autowired
    private ISixCheckRecordService sixCheckRecordService;

    @Autowired
    private ISixCheckItemService sixCheckItemService; // 确保注入项目Service

    @GetMapping("/input")
    public String input(ModelMap mmap) {
        // 判断当前用户是否拥有 sixcheck:record:edit 权限
        boolean canEdit = ShiroUtils.getSubject().isPermitted("sixcheck:record:edit");
        mmap.put("canEdit", canEdit);
        return "sixcheck/input"; // 跳转到我们创建的 input.html
    }

    @RequiresPermissions("sixcheck:record:view")
    @GetMapping()
    public String record() {
        return prefix + "/record";
    }

    /**
     * 查询六必查记录列表
     */
    @RequiresPermissions("sixcheck:record:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(SixCheckRecord sixCheckRecord) {
        startPage();
        if (!ShiroUtils.getSysUser().isAdmin()) {
            sixCheckRecord.setDeptId(ShiroUtils.getSysUser().getDeptId());
        }
        List<SixCheckRecord> list = sixCheckRecordService.selectSixCheckRecordList(sixCheckRecord);
        return getDataTable(list);
    }

    /**
     * 导出六必查记录列表
     */
    @RequiresPermissions("sixcheck:record:export")
    @Log(title = "六必查记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(SixCheckRecord sixCheckRecord) {
        List<SixCheckRecord> list = sixCheckRecordService.selectSixCheckRecordList(sixCheckRecord);
        ExcelUtil<SixCheckRecord> util = new ExcelUtil<SixCheckRecord>(SixCheckRecord.class);
        return util.exportExcel(list, "六必查记录数据");
    }

    /**
     * 新增六必查记录
     */
    @RequiresPermissions("sixcheck:record:add")
    @GetMapping("/add")
    public String add() {
        return prefix + "/add";
    }

    /**
     * 新增保存六必查记录
     */
    @RequiresPermissions("sixcheck:record:add")
    @Log(title = "六必查记录", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(SixCheckRecord sixCheckRecord) {
        return toAjax(sixCheckRecordService.insertSixCheckRecord(sixCheckRecord));
    }

    /**
     * 修改六必查记录
     */
    @RequiresPermissions("sixcheck:record:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap) {
        SixCheckRecord sixCheckRecord = sixCheckRecordService.selectSixCheckRecordById(id);
        mmap.put("sixCheckRecord", sixCheckRecord);
        return prefix + "/edit";
    }

    /**
     * 修改保存六必查记录
     */
    @RequiresPermissions("sixcheck:record:edit")
    @Log(title = "六必查记录", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(SixCheckRecord sixCheckRecord) {
        return toAjax(sixCheckRecordService.updateSixCheckRecord(sixCheckRecord));
    }

    /**
     * 删除六必查记录
     */
    @RequiresPermissions("sixcheck:record:remove")
    @Log(title = "六必查记录", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(sixCheckRecordService.deleteSixCheckRecordByIds(ids));
    }

    @GetMapping("/load")
    @ResponseBody
    public AjaxResult load(@RequestParam String batchNo) {
        // 查询所有检查项目
        SixCheckItem queryItem = new SixCheckItem();
        queryItem.setDeptId(ShiroUtils.getSysUser().getDeptId());
        List<SixCheckItem> items = sixCheckItemService.selectSixCheckItemList(queryItem);
        // 查询该月份的已有记录
        SixCheckRecord query = new SixCheckRecord();
        query.setBatchNo(batchNo);
        List<SixCheckRecord> records = sixCheckRecordService.selectSixCheckRecordList(query);
        // 转为 Map<itemId, recordValue>
        Map<Long, String> recordMap = new HashMap<>();
        for (SixCheckRecord r : records) {
            recordMap.put(r.getItemId(), r.getRecordValue());
        }
        Map<String, Object> result = new HashMap<>();
        result.put("items", items);
        result.put("records", recordMap);
        return success().put("data", result);
    }

    @PostMapping("/save")
    @ResponseBody
    public AjaxResult save(@RequestBody List<SixCheckRecord> recordList) {
        if (!ShiroUtils.getSubject().isPermitted("sixcheck:record:edit")) {
            return error("您没有权限编辑六必查记录");
        }
        for (SixCheckRecord r : recordList) {
            // 检查是否存在相同 item_id + batch_no 的记录
            SixCheckRecord exist = new SixCheckRecord();
            exist.setItemId(r.getItemId());
            exist.setBatchNo(r.getBatchNo());
            r.setDeptId(ShiroUtils.getSysUser().getDeptId());
            exist.setDeptId(ShiroUtils.getSysUser().getDeptId());
            List<SixCheckRecord> list = sixCheckRecordService.selectSixCheckRecordList(exist);
            if (list.size() > 0) {
                // 更新
                SixCheckRecord update = list.get(0);
                update.setRecordValue(r.getRecordValue());
                update.setUpdateBy(ShiroUtils.getLoginName());
                sixCheckRecordService.updateSixCheckRecord(update);
            } else {
                // 插入
                r.setCreateBy(ShiroUtils.getLoginName());
                sixCheckRecordService.insertSixCheckRecord(r);
            }
        }
        return success("保存成功");
    }
}
