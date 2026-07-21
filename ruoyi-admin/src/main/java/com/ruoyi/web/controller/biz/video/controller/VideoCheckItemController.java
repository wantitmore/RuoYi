package com.ruoyi.web.controller.biz.video.controller;

import java.util.Comparator;
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
import com.ruoyi.web.controller.biz.video.domain.VideoCheckItem;
import com.ruoyi.web.controller.biz.video.domain.VideoPlaybackRecord;
import com.ruoyi.web.controller.biz.video.service.IVideoCheckItemService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 视频回放检查项目Controller
 * 
 * @author ruoyi
 * @date 2026-07-12
 */
@Controller
@RequestMapping("/video/checkitem")
public class VideoCheckItemController extends BaseController {
    private String prefix = "video/checkitem";

    @Autowired
    private IVideoCheckItemService videoCheckItemService;

    @RequiresPermissions("video:checkitem:view")
    @GetMapping()
    public String checkitem() {
        return prefix + "/checkitem";
    }

    /**
     * 查询视频回放检查项目列表
     */
    @RequiresPermissions("video:checkitem:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(VideoCheckItem videoCheckItem) {
        startPage();
        if (!ShiroUtils.getSysUser().isAdmin()) {
            videoCheckItem.setDeptId(ShiroUtils.getSysUser().getDeptId());
        }
        List<VideoCheckItem> list = videoCheckItemService.selectVideoCheckItemList(videoCheckItem);
        return getDataTable(list);
    }

    /**
     * 导出视频回放检查项目列表
     */
    @RequiresPermissions("video:checkitem:export")
    @Log(title = "视频回放检查项目", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(VideoCheckItem videoCheckItem) {
        List<VideoCheckItem> list = videoCheckItemService.selectVideoCheckItemList(videoCheckItem);
        ExcelUtil<VideoCheckItem> util = new ExcelUtil<VideoCheckItem>(VideoCheckItem.class);
        return util.exportExcel(list, "视频回放检查项目数据");
    }

    /**
     * 新增视频回放检查项目
     */
    @RequiresPermissions("video:checkitem:add")
    @GetMapping("/add")
    public String add() {
        return prefix + "/add";
    }

    /**
     * 新增保存视频回放检查项目
     */
    @RequiresPermissions("video:checkitem:add")
    @Log(title = "视频回放检查项目", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(VideoCheckItem item) {
        item.setDeptId(ShiroUtils.getSysUser().getDeptId());
        return toAjax(videoCheckItemService.insertVideoCheckItem(item));
    }

    /**
     * 修改视频回放检查项目
     */
    @RequiresPermissions("video:checkitem:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap) {
        VideoCheckItem videoCheckItem = videoCheckItemService.selectVideoCheckItemById(id);
        mmap.put("videoCheckItem", videoCheckItem);
        return prefix + "/edit";
    }

    /**
     * 修改保存视频回放检查项目
     */
    @RequiresPermissions("video:checkitem:edit")
    @Log(title = "视频回放检查项目", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(VideoCheckItem videoCheckItem) {
        return toAjax(videoCheckItemService.updateVideoCheckItem(videoCheckItem));
    }

    /**
     * 删除视频回放检查项目
     */
    @RequiresPermissions("video:checkitem:remove")
    @Log(title = "视频回放检查项目", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(videoCheckItemService.deleteVideoCheckItemByIds(ids));
    }

}
