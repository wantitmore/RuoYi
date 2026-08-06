package com.ruoyi.web.controller.biz.ld_quarter_common.service.impl;

import com.ruoyi.web.controller.biz.ld_quarter_common.domain.QuarterFactor;
import com.ruoyi.web.controller.biz.ld_quarter_common.mapper.QuarterFactorMapper;
import com.ruoyi.web.controller.biz.ld_quarter_common.service.IQuarterFactorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class QuarterFactorServiceImpl implements IQuarterFactorService {

    @Autowired
    private QuarterFactorMapper quarterFactorMapper;

    @Override
    public List<QuarterFactor> selectQuarterFactorList(QuarterFactor factor) {
        return quarterFactorMapper.selectQuarterFactorList(factor);
    }

    @Override
    public QuarterFactor selectQuarterFactorById(Long id) {
        return quarterFactorMapper.selectQuarterFactorById(id);
    }

    @Override
    public int insertQuarterFactor(QuarterFactor factor) {
        return quarterFactorMapper.insertQuarterFactor(factor);
    }

    @Override
    public int updateQuarterFactor(QuarterFactor factor) {
        return quarterFactorMapper.updateQuarterFactor(factor);
    }

    @Override
    public int deleteQuarterFactorByIds(String ids) {
        return quarterFactorMapper.deleteQuarterFactorByIds(ids.split(","));
    }
}