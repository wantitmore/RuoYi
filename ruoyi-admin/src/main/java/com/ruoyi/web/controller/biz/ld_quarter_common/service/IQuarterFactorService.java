package com.ruoyi.web.controller.biz.ld_quarter_common.service;

import java.util.List;

import com.ruoyi.web.controller.biz.ld_quarter_common.domain.QuarterFactor;

public interface IQuarterFactorService {
    List<QuarterFactor> selectQuarterFactorList(QuarterFactor factor);
    QuarterFactor selectQuarterFactorById(Long id);
    int insertQuarterFactor(QuarterFactor factor);
    int updateQuarterFactor(QuarterFactor factor);
    int deleteQuarterFactorByIds(String ids);
}