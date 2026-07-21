package com.ruoyi.web.controller.biz.praiselog.mapper;

import java.util.List;
import com.ruoyi.web.controller.biz.praiselog.domain.PraLog;

/**
 * 执勤扬台账Mapper接口
 * 
 * @author ruoyi
 * @date 2026-07-07
 */
public interface PraLogMapper 
{
    /**
     * 查询执勤扬台账
     * 
     * @param id 执勤扬台账主键
     * @return 执勤扬台账
     */
    public PraLog selectPraLogById(Long id);

    /**
     * 查询执勤扬台账列表
     * 
     * @param praLog 执勤扬台账
     * @return 执勤扬台账集合
     */
    public List<PraLog> selectPraLogList(PraLog praLog);

    /**
     * 新增执勤扬台账
     * 
     * @param praLog 执勤扬台账
     * @return 结果
     */
    public int insertPraLog(PraLog praLog);

    /**
     * 修改执勤扬台账
     * 
     * @param praLog 执勤扬台账
     * @return 结果
     */
    public int updatePraLog(PraLog praLog);

    /**
     * 删除执勤扬台账
     * 
     * @param id 执勤扬台账主键
     * @return 结果
     */
    public int deletePraLogById(Long id);

    /**
     * 批量删除执勤扬台账
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePraLogByIds(String[] ids);
}
