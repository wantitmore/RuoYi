package com.ruoyi.web.controller.biz.ld_quarter_common.mapper;

import com.ruoyi.web.controller.biz.ld_quarter_common.domain.QuarterFactor;
import java.util.List;

public interface QuarterFactorMapper {
    List<QuarterFactor> selectQuarterFactorList(QuarterFactor factor);
    QuarterFactor selectQuarterFactorById(Long id);
    int insertQuarterFactor(QuarterFactor factor);
    int updateQuarterFactor(QuarterFactor factor);
    int deleteQuarterFactorById(Long id);
    int deleteQuarterFactorByIds(String[] ids);
}
