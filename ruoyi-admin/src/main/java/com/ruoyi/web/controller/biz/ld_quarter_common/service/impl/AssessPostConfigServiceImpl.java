package com.ruoyi.web.controller.biz.ld_quarter_common.service.impl;

import com.ruoyi.web.controller.biz.ld_quarter_common.mapper.AssessPostConfigMapper;
import com.ruoyi.web.controller.biz.ld_quarter_common.service.IAssessPostConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;

@Service
public class AssessPostConfigServiceImpl implements IAssessPostConfigService {

    @Autowired
    private AssessPostConfigMapper configMapper;

    @Override
    public List<String> getPostCodesByType(String type) {
        if (type == null || type.isEmpty()) {
            return Collections.emptyList();
        }
        return configMapper.selectPostCodesByType(type);
    }
}

