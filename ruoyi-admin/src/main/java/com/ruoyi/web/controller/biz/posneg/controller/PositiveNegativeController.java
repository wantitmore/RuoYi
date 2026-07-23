package com.ruoyi.web.controller.biz.posneg.controller;

import io.swagger.v3.oas.models.security.SecurityScheme;
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

import com.alibaba.druid.util.StringUtils;
import com.github.pagehelper.PageHelper;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.web.controller.biz.posneg.domain.PositiveNegative;
import com.ruoyi.web.controller.biz.posneg.service.IPositiveNegativeService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 正负面清单Controller
 * 
 * @author ruoyi
 * @date 2026-07-03
 */
@Controller
@RequestMapping("/posneg/pos_nega")
public class PositiveNegativeController extends BaseController {
    private final SecurityScheme securityScheme;

    private String prefix = "posneg/pos_nega";

    @Autowired
    private IPositiveNegativeService positiveNegativeService;

    @Autowired
    private ISysUserService userService;

    PositiveNegativeController(SecurityScheme securityScheme) {
        this.securityScheme = securityScheme;
    }

    @RequiresPermissions("posneg:pos_nega:view")
    @GetMapping()
    public String pos_nega(ModelMap mmap) {
        mmap.put("canEdit", ShiroUtils.getSubject().isPermitted("posneg:pos_nega:edit"));
        mmap.put("canRemove", ShiroUtils.getSubject().isPermitted("posneg:pos_nega:remove"));
        return prefix + "/pos_nega";
    }

    /**
     * 查询正负面清单列表
     */
    @RequiresPermissions("posneg:pos_nega:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(PositiveNegative positiveNegative) {
        startPage();
        if (!ShiroUtils.getSysUser().isAdmin()) {
            positiveNegative.setDeptId(ShiroUtils.getSysUser().getDeptId());
        }
        List<PositiveNegative> list = positiveNegativeService.selectPositiveNegativeList(positiveNegative);
        // 填充用户姓名
        for (PositiveNegative p : list) {
            SysUser user = userService.selectUserById(p.getUserId());
            if (user != null)
                p.setUserName(user.getUserName());
        }
        return getDataTable(list);
    }

    /**
     * 导出正负面清单列表
     */
    @RequiresPermissions("posneg:pos_nega:export")
    @Log(title = "正负面清单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(PositiveNegative positiveNegative) {
        List<PositiveNegative> list = positiveNegativeService.selectPositiveNegativeList(positiveNegative);
        ExcelUtil<PositiveNegative> util = new ExcelUtil<PositiveNegative>(PositiveNegative.class);
        return util.exportExcel(list, "正负面清单数据");
    }

    /**
     * 新增正负面清单
     */
    @RequiresPermissions("posneg:pos_nega:add")
    @GetMapping("/add")
    public String add(ModelMap mmap) {
        List<SysUser> users = userService.selectUserList(new SysUser());
        mmap.put("users", users);
        return prefix + "/add";
    }

    /**
     * 新增保存正负面清单
     */
    @RequiresPermissions("posneg:pos_nega:add")
    @Log(title = "正负面清单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(PositiveNegative positiveNegative) {
        // 查询是否已存在相同人员、类别、月份的记录
        PositiveNegative query = new PositiveNegative();
        query.setUserId(positiveNegative.getUserId());
        query.setCategory(positiveNegative.getCategory());
        query.setBatchNo(positiveNegative.getBatchNo());
        query.setDeptId(ShiroUtils.getSysUser().getDeptId());
        //positiveNegative.setDeptId(ShiroUtils.getSysUser().getDeptId());
        List<PositiveNegative> existList = positiveNegativeService.selectPositiveNegativeList(query);

        if (existList.size() > 0) {
            // 已存在，则更新
            PositiveNegative exist = existList.get(0);
            exist.setSituation(positiveNegative.getSituation());
            exist.setSuggestion(positiveNegative.getSuggestion());
            exist.setCount(positiveNegative.getCount());
            exist.setUpdateBy(ShiroUtils.getLoginName());
            exist.setDeptId(ShiroUtils.getSysUser().getDeptId());
            return toAjax(positiveNegativeService.updatePositiveNegative(exist));
        } else {
            // 不存在，则新增
            positiveNegative.setCreateBy(ShiroUtils.getLoginName());
            positiveNegative.setDeptId(ShiroUtils.getSysUser().getDeptId());
            return toAjax(positiveNegativeService.insertPositiveNegative(positiveNegative));
        }
    }

    /**
     * 修改正负面清单
     */
    @RequiresPermissions("posneg:pos_nega:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap) {
        PositiveNegative positiveNegative = positiveNegativeService.selectPositiveNegativeById(id);
        mmap.put("positiveNegative", positiveNegative);
        SysUser currentUser = ShiroUtils.getSysUser();
        SysUser query = new SysUser();
        if (!currentUser.isAdmin()) {
            query.setDeptId(currentUser.getDeptId());
        }
        List<SysUser> users = userService.selectUserList(new SysUser());
        mmap.put("users", users);

        return prefix + "/edit";
    }

    /**
     * 修改保存正负面清单
     */
    @RequiresPermissions("posneg:pos_nega:edit")
    @Log(title = "正负面清单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(PositiveNegative positiveNegative) {
        positiveNegative.setDeptId(ShiroUtils.getSysUser().getDeptId());
        return toAjax(positiveNegativeService.updatePositiveNegative(positiveNegative));
    }

    /**
     * 删除正负面清单
     */
    @RequiresPermissions("posneg:pos_nega:remove")
    @Log(title = "正负面清单", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(positiveNegativeService.deletePositiveNegativeByIds(ids));
    }
}
