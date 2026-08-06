package com.ruoyi.web.controller.biz.ld_quarter_common.service;

import com.ruoyi.web.controller.biz.ld_quarter_common.domain.QuarterScore;
import java.util.List;

public interface IQuarterScoreService {
    List<QuarterScore> selectQuarterScoreList(QuarterScore score);
    QuarterScore selectQuarterScoreById(Long id);
    int insertQuarterScore(QuarterScore score);
    int updateQuarterScore(QuarterScore score);
    int deleteQuarterScoreByIds(String ids);
}
