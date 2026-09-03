package com.ruoyi.web.controller.biz.sixcheck.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.web.controller.biz.sixcheck.domain.SixCheckRecord;

/**
 * 六必查记录Mapper接口
 * 
 * @author ruoyi
 * @date 2026-07-01
 */
public interface SixCheckRecordMapper 
{
    /**
     * 查询六必查记录
     * 
     * @param id 六必查记录主键
     * @return 六必查记录
     */
    public SixCheckRecord selectSixCheckRecordById(Long id);

    /**
     * 查询六必查记录列表
     * 
     * @param sixCheckRecord 六必查记录
     * @return 六必查记录集合
     */
    public List<SixCheckRecord> selectSixCheckRecordList(SixCheckRecord sixCheckRecord);

    /**
     * 新增六必查记录
     * 
     * @param sixCheckRecord 六必查记录
     * @return 结果
     */
    public int insertSixCheckRecord(SixCheckRecord sixCheckRecord);

    /**
     * 修改六必查记录
     * 
     * @param sixCheckRecord 六必查记录
     * @return 结果
     */
    public int updateSixCheckRecord(SixCheckRecord sixCheckRecord);

    /**
     * 删除六必查记录
     * 
     * @param id 六必查记录主键
     * @return 结果
     */
    public int deleteSixCheckRecordById(Long id);

    /**
     * 批量删除六必查记录
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSixCheckRecordByIds(String[] ids);

    List<SixCheckRecord> selectListByMonth(@Param("month") String month,
                                       @Param("deptId") Long deptId);

    public String getLastUpdateBy(@Param("checkDate") String checkDate, @Param("shift") String shift);
}
