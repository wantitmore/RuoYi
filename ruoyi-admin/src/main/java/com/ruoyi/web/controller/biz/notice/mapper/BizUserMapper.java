/**
 * 
 * 
 * @author Zack
 * @date Aug 24, 2026
 */
package com.ruoyi.web.controller.biz.notice.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface BizUserMapper {

    /**
     * 查询指定部门下拥有指定角色的用户ID列表
     */
    List<Long> selectUserIdsByDeptAndRole(@Param("deptId") Long deptId,
                                          @Param("roleKey") String roleKey);
}
