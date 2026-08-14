/**
 * 
 * 
 * @author Zack
 * @date Aug 13, 2026
 */
package com.ruoyi.web.controller.biz.sixcheck.service.impl;

import com.ruoyi.web.controller.biz.sixcheck.domain.SixCheckDeductDetail;
import com.ruoyi.web.controller.biz.sixcheck.mapper.SixCheckDeductDetailMapper;
import com.ruoyi.web.controller.biz.sixcheck.service.ISixCheckDeductDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SixCheckDeductDetailServiceImpl implements ISixCheckDeductDetailService {

    @Autowired
    private SixCheckDeductDetailMapper sixCheckDeductDetailMapper;

    @Override
    public int insert(SixCheckDeductDetail detail) {
        return sixCheckDeductDetailMapper.insert(detail);
    }

    @Override
    public int update(SixCheckDeductDetail detail) {
        return sixCheckDeductDetailMapper.update(detail);
    }

    @Override
    public SixCheckDeductDetail selectById(Long id) {
        return sixCheckDeductDetailMapper.selectById(id);
    }

    @Override
    public List<SixCheckDeductDetail> selectList(SixCheckDeductDetail query) {
        return sixCheckDeductDetailMapper.selectList(query);
    }
}