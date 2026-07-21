package com.ruoyi.web.controller.biz.issue.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.web.controller.biz.issue.mapper.IssueReportMapper;
import com.ruoyi.web.controller.biz.issue.domain.IssueReport;
import com.ruoyi.web.controller.biz.issue.service.IIssueReportService;
import com.ruoyi.common.core.text.Convert;

/**
 * 问题通报Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-07-08
 */
@Service
public class IssueReportServiceImpl implements IIssueReportService 
{
    @Autowired
    private IssueReportMapper issueReportMapper;

    /**
     * 查询问题通报
     * 
     * @param id 问题通报主键
     * @return 问题通报
     */
    @Override
    public IssueReport selectIssueReportById(Long id)
    {
        return issueReportMapper.selectIssueReportById(id);
    }

    /**
     * 查询问题通报列表
     * 
     * @param issueReport 问题通报
     * @return 问题通报
     */
    @Override
    public List<IssueReport> selectIssueReportList(IssueReport issueReport)
    {
        return issueReportMapper.selectIssueReportList(issueReport);
    }

    /**
     * 新增问题通报
     * 
     * @param issueReport 问题通报
     * @return 结果
     */
    @Override
    public int insertIssueReport(IssueReport issueReport)
    {
        issueReport.setCreateTime(DateUtils.getNowDate());
        return issueReportMapper.insertIssueReport(issueReport);
    }

    /**
     * 修改问题通报
     * 
     * @param issueReport 问题通报
     * @return 结果
     */
    @Override
    public int updateIssueReport(IssueReport issueReport)
    {
        issueReport.setUpdateTime(DateUtils.getNowDate());
        return issueReportMapper.updateIssueReport(issueReport);
    }

    /**
     * 批量删除问题通报
     * 
     * @param ids 需要删除的问题通报主键
     * @return 结果
     */
    @Override
    public int deleteIssueReportByIds(String ids)
    {
        return issueReportMapper.deleteIssueReportByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除问题通报信息
     * 
     * @param id 问题通报主键
     * @return 结果
     */
    @Override
    public int deleteIssueReportById(Long id)
    {
        return issueReportMapper.deleteIssueReportById(id);
    }
}
