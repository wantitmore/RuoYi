package com.ruoyi.web.controller.biz.kpi.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.web.controller.biz.kpi.domain.KpiScore;
import com.ruoyi.web.controller.biz.kpi.domain.ScoreSummary;

/**
 * 考核分数Mapper接口
 * 
 * @author ruoyi
 * @date 2026-06-07
 */
public interface KpiScoreMapper {
        /**
         * 查询考核分数
         * 
         * @param id 考核分数主键
         * @return 考核分数
         */
        public KpiScore selectKpiScoreById(Long id);

        List<ScoreSummary> selectSummary(@Param("batchNo") String batchNo, @Param("deptId") Long deptId,
                        @Param("postId") Long postId);

        /**
         * 查询考核分数列表
         * 
         * @param kpiScore 考核分数
         * @return 考核分数集合
         */
        public List<KpiScore> selectKpiScoreList(KpiScore kpiScore);

        /**
         * 新增考核分数
         * 
         * @param kpiScore 考核分数
         * @return 结果
         */
        public int insertKpiScore(KpiScore kpiScore);

        /**
         * 修改考核分数
         * 
         * @param kpiScore 考核分数
         * @return 结果
         */
        public int updateKpiScore(KpiScore kpiScore);

        /**
         * 删除考核分数
         * 
         * @param id 考核分数主键
         * @return 结果
         */
        public int deleteKpiScoreById(Long id);

        /**
         * 批量删除考核分数
         * 
         * @param ids 需要删除的数据主键集合
         * @return 结果
         */
        public int deleteKpiScoreByIds(String[] ids);

        void deleteByUserAndRemark(@Param("userName") String userName,
                        @Param("remark") String remark,
                        @Param("checkDate") String checkDate,
                        @Param("deptId") Long deptId);

        List<ScoreSummary> selectAvgSummary(@Param("months") List<String> months,
                        @Param("deptId") Long deptId,
                        @Param("postId") Long postId);

        List<KpiScore> selectByMonths(@Param("months") List<String> months,
                        @Param("userId") Long userId);
}
