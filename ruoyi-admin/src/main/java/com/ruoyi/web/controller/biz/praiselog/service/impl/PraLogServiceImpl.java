package com.ruoyi.web.controller.biz.praiselog.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.web.controller.biz.praiselog.mapper.PraLogMapper;
import com.ruoyi.web.controller.biz.praiselog.domain.PraLog;
import com.ruoyi.web.controller.biz.praiselog.service.IPraLogService;
import com.ruoyi.common.core.text.Convert;

/**
 * 执勤扬台账Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-07-07
 */
@Service
public class PraLogServiceImpl implements IPraLogService 
{
    @Autowired
    private PraLogMapper praLogMapper;

    /**
     * 查询执勤扬台账
     * 
     * @param id 执勤扬台账主键
     * @return 执勤扬台账
     */
    @Override
    public PraLog selectPraLogById(Long id)
    {
        return praLogMapper.selectPraLogById(id);
    }

    /**
     * 查询执勤扬台账列表
     * 
     * @param praLog 执勤扬台账
     * @return 执勤扬台账
     */
    @Override
    public List<PraLog> selectPraLogList(PraLog praLog)
    {
        return praLogMapper.selectPraLogList(praLog);
    }

    /**
     * 新增执勤扬台账
     * 
     * @param praLog 执勤扬台账
     * @return 结果
     */
    @Override
    public int insertPraLog(PraLog praLog)
    {
        praLog.setCreateTime(DateUtils.getNowDate());
        return praLogMapper.insertPraLog(praLog);
    }

    /**
     * 修改执勤扬台账
     * 
     * @param praLog 执勤扬台账
     * @return 结果
     */
    @Override
    public int updatePraLog(PraLog praLog)
    {
        praLog.setUpdateTime(DateUtils.getNowDate());
        return praLogMapper.updatePraLog(praLog);
    }

    /**
     * 批量删除执勤扬台账
     * 
     * @param ids 需要删除的执勤扬台账主键
     * @return 结果
     */
    @Override
    public int deletePraLogByIds(String ids)
    {
        return praLogMapper.deletePraLogByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除执勤扬台账信息
     * 
     * @param id 执勤扬台账主键
     * @return 结果
     */
    @Override
    public int deletePraLogById(Long id)
    {
        return praLogMapper.deletePraLogById(id);
    }
}
