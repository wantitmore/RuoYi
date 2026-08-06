package com.ruoyi.web.controller.biz.ld_quarter_common.mapper;
import com.ruoyi.web.controller.biz.ld_quarter_common.domain.QuarterScore;
import java.util.List;

public interface QuarterScoreMapper {
    List<QuarterScore> selectQuarterScoreList(QuarterScore score);
    QuarterScore selectQuarterScoreById(Long id);
    int insertQuarterScore(QuarterScore score);
    int updateQuarterScore(QuarterScore score);
    int deleteQuarterScoreById(Long id);
    int deleteQuarterScoreByIds(String[] ids);
    int insert(QuarterScore record);
    int update(QuarterScore record);
}