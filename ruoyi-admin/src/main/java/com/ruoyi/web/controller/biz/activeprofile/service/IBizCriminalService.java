/**
 * 
 * 
 * @author Zack
 * @date Sep 15, 2026
 */
package com.ruoyi.web.controller.biz.activeprofile.service;

import java.util.List;

import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.web.controller.biz.activeprofile.domain.BizCriminal;

public interface IBizCriminalService {

    List<BizCriminal> selectCriminalList(BizCriminal query);

    BizCriminal selectCriminalById(Long id);

    int insertCriminal(BizCriminal record);

    int updateCriminal(BizCriminal record);

    int deleteCriminalById(Long id);

    int deleteCriminalByIds(Long[] ids);

    /** 认领：返回 0 表示已被他人认领 */
    int claim(Long id);

    /** 退回公共：返回 0 表示无权退回 */
    int returnToPublic(Long id);

    /** 转给人 */
    int transferToUser(Long id, Long targetUserId);

    /** 批量转给人 */
    int batchTransferToUser(Long[] ids, Long targetUserId);

    /** 转给部门 */
    int transferToDept(Long id, Long targetDeptId);

    /** 批量转给部门 */
    int batchTransferToDept(Long[] ids, Long targetDeptId);

    List<SysDept> selectAllDepts();

    int upsertBySync(BizCriminal record);
}
