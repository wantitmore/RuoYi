package com.ruoyi.web.controller.biz.ld_quarter_common.mapper;

import com.ruoyi.web.controller.biz.ld_quarter_common.domain.AssessPostConfig;
import java.util.List;

public interface AssessPostConfigMapper {
     List<AssessPostConfig> selectConfigList(AssessPostConfig config);
    List<String> selectPostCodesByType(String type);
}
