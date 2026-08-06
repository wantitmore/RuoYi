package com.ruoyi.web.controller.biz.ld_quarter_common.service.impl;

import com.ruoyi.web.controller.biz.ld_quarter_common.domain.QuarterScore;
import com.ruoyi.web.controller.biz.ld_quarter_common.mapper.QuarterScoreMapper;
import com.ruoyi.web.controller.biz.ld_quarter_common.service.IQuarterScoreService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class QuarterScoreServiceImpl  implements IQuarterScoreService {

    @Autowired
    private QuarterScoreMapper quarterScoreMapper;

    

    @Override
    public List<QuarterScore> selectQuarterScoreList(QuarterScore score) {
        return quarterScoreMapper.selectQuarterScoreList(score);
    }

    @Override
    public QuarterScore selectQuarterScoreById(Long id) {
        return quarterScoreMapper.selectQuarterScoreById(id);
    }

    @Override
    public int insertQuarterScore(QuarterScore score) {
        return quarterScoreMapper.insertQuarterScore(score);
    }

    @Override
    public int updateQuarterScore(QuarterScore score) {
        return quarterScoreMapper.updateQuarterScore(score);
    }

    @Override
    public int deleteQuarterScoreByIds(String ids) {
        return quarterScoreMapper.deleteQuarterScoreByIds(ids.split(","));
    }

    public void saveQuarterScore(QuarterScore score) {
        // 根据 grade 自动计算 score
        score.setScore(convertGradeToScore(score.getGrade()));
        
        // 判断是新增还是更新
        if (score.getId() != null && score.getId() > 0) {
            quarterScoreMapper.update(score);  // 更新
        } else {
            quarterScoreMapper.insert(score);  // 新增
        }
    }

    /**
     * 等级转分数
     */
    private int convertGradeToScore(String grade) {
        switch (grade) {
            case "好": return 95;
            case "较好": return 85;
            case "一般": return 70;
            case "差": return 55;
            default: return 0;
        }
    }
}
