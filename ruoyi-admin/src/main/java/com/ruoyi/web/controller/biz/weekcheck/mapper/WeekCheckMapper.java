/**
 * 
 * 
 * @author Zack
 * @date Aug 31, 2026
 */
package com.ruoyi.web.controller.biz.weekcheck.mapper;

import com.ruoyi.web.controller.biz.weekcheck.domain.WeekCheck;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface WeekCheckMapper {

        /**
         * 查询列表（支持按 week、deptId、createBy 等过滤）
         */
        List<WeekCheck> selectCheckList(WeekCheck query);

        /**
         * 根据 userId + week + createBy 查询单条记录
         */
        WeekCheck selectByUserWeekAndCreator(@Param("userId") Long userId,
                        @Param("week") String week,
                        @Param("createBy") String createBy);

        /**
         * 根据ID查询
         */
        WeekCheck selectWeekCheckById(@Param("id") Long id);

        /**
         * 插入
         */
        int insertWeekCheck(WeekCheck record);

        /**
         * 更新
         */
        int updateWeekCheck(WeekCheck record);

        /**
         * 删除指定周的所有记录
         */
        int deleteByWeek(@Param("week") String week);

        /**
         * 删除指定用户指定周的记录
         */
        int deleteByUserIdAndWeek(@Param("userId") Long userId,
                        @Param("week") String week);
}