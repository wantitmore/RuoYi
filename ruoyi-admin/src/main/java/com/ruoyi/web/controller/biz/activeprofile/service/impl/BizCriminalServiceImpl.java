/**
 * 
 * 
 * @author Zack
 * @date Sep 15, 2026
 */
package com.ruoyi.web.controller.biz.activeprofile.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.web.controller.biz.activeprofile.domain.BizCriminal;
import com.ruoyi.web.controller.biz.activeprofile.mapper.BizCriminalMapper;
import com.ruoyi.web.controller.biz.activeprofile.service.IBizCriminalService;

@Service
public class BizCriminalServiceImpl implements IBizCriminalService {

    @Autowired
    private BizCriminalMapper criminalMapper;

    @Autowired
    private ISysUserService userService;

    @Override
    @DataScope(deptAlias = "d")
    public List<BizCriminal> selectCriminalList(BizCriminal query) {
        return criminalMapper.selectCriminalList(query);
    }

    @Override
    public BizCriminal selectCriminalById(Long id) {
        return criminalMapper.selectCriminalById(id);
    }

    @Override
    public int insertCriminal(BizCriminal record) {

        // 囚号唯一性校验
        BizCriminal exist = criminalMapper.selectCriminalByBh(record.getBh());

        SysUser user = ShiroUtils.getSysUser();
        if (record.getDeptId() == null) {
            record.setDeptId(user.getDeptId());
        }
        if (StringUtils.isBlank(record.getDataSource())) {
            record.setDataSource("manual");
        }
        record.setCreateBy(ShiroUtils.getLoginName());
        return criminalMapper.insertCriminal(record);
    }

    @Override
    public int updateCriminal(BizCriminal record) {
        record.setUpdateBy(ShiroUtils.getLoginName());
        return criminalMapper.updateCriminal(record);
    }

    @Override
    public int deleteCriminalById(Long id) {
        return criminalMapper.deleteCriminalById(id);
    }

    @Override
    public int deleteCriminalByIds(Long[] ids) {
        return criminalMapper.deleteCriminalByIds(ids);
    }

    @Override
    public int claim(Long id) {
        SysUser user = ShiroUtils.getSysUser();
        int rows = criminalMapper.claimCriminal(id, user.getUserId(), user.getUserName());

        return rows;
    }

    @Override
    public int returnToPublic(Long id) {
        SysUser user = ShiroUtils.getSysUser();
        int rows = criminalMapper.returnToPublic(id, user.getUserId());

        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int transferToUser(Long id, Long targetUserId) {
        SysUser target = userService.selectUserById(targetUserId);

        return criminalMapper.transferToUser(id, targetUserId, target.getUserName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchTransferToUser(Long[] ids, Long targetUserId) {
        SysUser target = userService.selectUserById(targetUserId);

        return criminalMapper.batchTransferToUser(ids, targetUserId, target.getUserName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int transferToDept(Long id, Long targetDeptId) {

        return criminalMapper.transferToDept(id, targetDeptId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchTransferToDept(Long[] ids, Long targetDeptId) {
        return criminalMapper.batchTransferToDept(ids, targetDeptId);
    }

    @Override
    public List<SysDept> selectAllDepts() {
        return criminalMapper.selectAllDepts();
    }

    @Override
    public int upsertBySync(BizCriminal record) {
        return criminalMapper.upsertBySync(record);
    }
}