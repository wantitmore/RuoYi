package com.ruoyi.web.controller.biz.kpi.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.ruoyi.web.controller.biz.kpi.domain.KpiScore;
import com.ruoyi.web.controller.biz.kpi.domain.KpiScoreDetailVo;
import com.ruoyi.web.controller.biz.kpi.domain.ScoreSummary;

/**
 * 考核分数Service接口
 * 
 * @author ruoyi
 * @date 2026-06-07
 */
public interface IKpiScoreService 
{
    /**
     * 查询考核分数
     * 
     * @param id 考核分数主键
     * @return 考核分数
     */
    public KpiScore selectKpiScoreById(Long id);

    // List<ScoreSummary> selectSummary(String batchNo, Long deptId, Long postId);

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
     * 批量删除考核分数
     * 
     * @param ids 需要删除的考核分数主键集合
     * @return 结果
     */
    public int deleteKpiScoreByIds(String ids);

    /**
     * 删除考核分数信息
     * 
     * @param id 考核分数主键
     * @return 结果
     */
    public int deleteKpiScoreById(Long id);

    void deleteByUserAndRemark(String userName, String remark, String checkDate, Long deptId);

     List<ScoreSummary> selectAvgSummary(List<String> months, Long deptId,
                    Long postId);

    List<KpiScore> selectByMonths(Long userId, List<String> months);
    List<KpiScoreDetailVo> selectAllDetail(List<String> months, Long deptId, Long postId);
    BigDecimal calcTotalScoreMinus100(Long userId, String batchNo);

    List<Long> selectUserIdsByBatchNoAndDept(Long deptId, String batchNo);
}
