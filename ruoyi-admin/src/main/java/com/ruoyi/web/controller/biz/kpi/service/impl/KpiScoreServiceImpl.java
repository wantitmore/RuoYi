package com.ruoyi.web.controller.biz.kpi.service.impl;

import java.util.List;
import java.util.Map;

import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruoyi.web.controller.biz.kpi.mapper.KpiScoreMapper;
import com.ruoyi.web.controller.biz.kpi.domain.KpiScore;
import com.ruoyi.web.controller.biz.kpi.domain.KpiScoreDetailVo;
import com.ruoyi.web.controller.biz.kpi.domain.ScoreSummary;
import com.ruoyi.web.controller.biz.kpi.service.IKpiScoreService;
import com.ruoyi.common.core.text.Convert;

/**
 * 考核分数Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-06-07
 */
@Service
public class KpiScoreServiceImpl implements IKpiScoreService {
    @Autowired
    private KpiScoreMapper kpiScoreMapper;

    /*
     * @Override
     * public List<ScoreSummary> selectSummary(String batchNo, Long deptId, Long
     * postId) {
     * return kpiScoreMapper.selectSummary(batchNo, deptId, postId);
     * }
     */
    /**
     * 查询考核分数
     * 
     * @param id 考核分数主键
     * @return 考核分数
     */
    @Override
    public KpiScore selectKpiScoreById(Long id) {
        return kpiScoreMapper.selectKpiScoreById(id);
    }

    /**
     * 查询考核分数列表
     * 
     * @param kpiScore 考核分数
     * @return 考核分数
     */
    @Override
    public List<KpiScore> selectKpiScoreList(KpiScore kpiScore) {
        return kpiScoreMapper.selectKpiScoreList(kpiScore);
    }

    /**
     * 新增考核分数
     * 
     * @param kpiScore 考核分数
     * @return 结果
     */
    @Override
    public int insertKpiScore(KpiScore kpiScore) {
        kpiScore.setCreateTime(DateUtils.getNowDate());
        return kpiScoreMapper.insertKpiScore(kpiScore);
    }

    /**
     * 修改考核分数
     * 
     * @param kpiScore 考核分数
     * @return 结果
     */
    @Override
    public int updateKpiScore(KpiScore kpiScore) {
        kpiScore.setUpdateTime(DateUtils.getNowDate());
        return kpiScoreMapper.updateKpiScore(kpiScore);
    }

    /**
     * 批量删除考核分数
     * 
     * @param ids 需要删除的考核分数主键
     * @return 结果
     */
    @Override
    public int deleteKpiScoreByIds(String ids) {
        return kpiScoreMapper.deleteKpiScoreByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除考核分数信息
     * 
     * @param id 考核分数主键
     * @return 结果
     */
    @Override
    public int deleteKpiScoreById(Long id) {
        return kpiScoreMapper.deleteKpiScoreById(id);
    }

    @Override
    public void deleteByUserAndRemark(String userName, String remark, String checkDate, Long deptId) {
        kpiScoreMapper.deleteByUserAndRemark(userName, remark, checkDate, deptId);
    }

    @Override
    public List<ScoreSummary> selectAvgSummary(List<String> months, Long deptId, Long postId) {
        if (months != null && !months.isEmpty()) {
            return kpiScoreMapper.selectAvgSummary(months, deptId, postId);
        }
        return null;
    }

    @Override
    public List<KpiScore> selectByMonths(Long userId, List<String> months) {
        return kpiScoreMapper.selectByMonths(months, userId);
    }

    @Override
    public List<KpiScoreDetailVo> selectAllDetail(List<String> months, Long deptId, Long postId) {
        return kpiScoreMapper.selectAllDetail(months, deptId, postId);
    }
}
