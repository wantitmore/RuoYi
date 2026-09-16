/**
 * 
 * 
 * @author Zack
 * @date Sep 15, 2026
 */
package com.ruoyi.web.controller.biz.activeprofile.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.web.controller.biz.activeprofile.domain.BizCriminal;

public interface BizCriminalMapper {

    List<BizCriminal> selectCriminalList(BizCriminal query);

    BizCriminal selectCriminalById(Long id);

    BizCriminal selectCriminalByBh(String bh);

    int insertCriminal(BizCriminal record);

    int updateCriminal(BizCriminal record);

    int deleteCriminalById(Long id);

    int deleteCriminalByIds(Long[] ids);

    int claimCriminal(@Param("id") Long id,
                      @Param("ownerId") Long ownerId,
                      @Param("ownerName") String ownerName);

    int returnToPublic(@Param("id") Long id,
                       @Param("ownerId") Long ownerId);

    int transferToUser(@Param("id") Long id,
                       @Param("targetUserId") Long targetUserId,
                       @Param("targetUserName") String targetUserName);

    int transferToDept(@Param("id") Long id,
                       @Param("targetDeptId") Long targetDeptId);

    int batchTransferToUser(@Param("ids") Long[] ids,
                            @Param("targetUserId") Long targetUserId,
                            @Param("targetUserName") String targetUserName);

    int batchTransferToDept(@Param("ids") Long[] ids,
                            @Param("targetDeptId") Long targetDeptId);

    List<SysDept> selectAllDepts();

    int upsertBySync(BizCriminal record);
}
