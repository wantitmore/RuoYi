package com.ruoyi.web.controller.biz.issue.service;

import java.util.List;
import com.ruoyi.web.controller.biz.issue.domain.IssueReport;

/**
 * 问题通报Service接口
 * 
 * @author ruoyi
 * @date 2026-07-08
 */
public interface IIssueReportService 
{
    /**
     * 查询问题通报
     * 
     * @param id 问题通报主键
     * @return 问题通报
     */
    public IssueReport selectIssueReportById(Long id);

    /**
     * 查询问题通报列表
     * 
     * @param issueReport 问题通报
     * @return 问题通报集合
     */
    public List<IssueReport> selectIssueReportList(IssueReport issueReport);

    /**
     * 新增问题通报
     * 
     * @param issueReport 问题通报
     * @return 结果
     */
    public int insertIssueReport(IssueReport issueReport);

    /**
     * 修改问题通报
     * 
     * @param issueReport 问题通报
     * @return 结果
     */
    public int updateIssueReport(IssueReport issueReport);

    /**
     * 批量删除问题通报
     * 
     * @param ids 需要删除的问题通报主键集合
     * @return 结果
     */
    public int deleteIssueReportByIds(String ids);

    /**
     * 删除问题通报信息
     * 
     * @param id 问题通报主键
     * @return 结果
     */
    public int deleteIssueReportById(Long id);
}
