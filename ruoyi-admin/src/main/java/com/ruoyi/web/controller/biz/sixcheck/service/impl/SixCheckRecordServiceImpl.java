package com.ruoyi.web.controller.biz.sixcheck.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.web.controller.biz.sixcheck.mapper.SixCheckRecordMapper;
import com.ruoyi.web.controller.biz.sixcheck.domain.SixCheckRecord;
import com.ruoyi.web.controller.biz.sixcheck.service.ISixCheckRecordService;
import com.ruoyi.common.core.text.Convert;

/**
 * 六必查记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-07-01
 */
@Service
public class SixCheckRecordServiceImpl implements ISixCheckRecordService {
    @Autowired
    private SixCheckRecordMapper sixCheckRecordMapper;

    /**
     * 查询六必查记录
     * 
     * @param id 六必查记录主键
     * @return 六必查记录
     */
    @Override
    public SixCheckRecord selectSixCheckRecordById(Long id) {
        return sixCheckRecordMapper.selectSixCheckRecordById(id);
    }

    /**
     * 查询六必查记录列表
     * 
     * @param sixCheckRecord 六必查记录
     * @return 六必查记录
     */
    @Override
    public List<SixCheckRecord> selectSixCheckRecordList(SixCheckRecord sixCheckRecord) {
        return sixCheckRecordMapper.selectSixCheckRecordList(sixCheckRecord);
    }

    /**
     * 新增六必查记录
     * 
     * @param sixCheckRecord 六必查记录
     * @return 结果
     */
    @Override
    public int insertSixCheckRecord(SixCheckRecord sixCheckRecord) {
        sixCheckRecord.setCreateTime(DateUtils.getNowDate());
        return sixCheckRecordMapper.insertSixCheckRecord(sixCheckRecord);
    }

    /**
     * 修改六必查记录
     * 
     * @param sixCheckRecord 六必查记录
     * @return 结果
     */
    @Override
    public int updateSixCheckRecord(SixCheckRecord sixCheckRecord) {
        sixCheckRecord.setUpdateTime(DateUtils.getNowDate());
        return sixCheckRecordMapper.updateSixCheckRecord(sixCheckRecord);
    }

    /**
     * 批量删除六必查记录
     * 
     * @param ids 需要删除的六必查记录主键
     * @return 结果
     */
    @Override
    public int deleteSixCheckRecordByIds(String ids) {
        return sixCheckRecordMapper.deleteSixCheckRecordByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除六必查记录信息
     * 
     * @param id 六必查记录主键
     * @return 结果
     */
    @Override
    public int deleteSixCheckRecordById(Long id) {
        return sixCheckRecordMapper.deleteSixCheckRecordById(id);
    }

    @Override
    public List<SixCheckRecord> selectListByMonth(String month, Long deptId) {
        return sixCheckRecordMapper.selectListByMonth(month, deptId);
    }
}
