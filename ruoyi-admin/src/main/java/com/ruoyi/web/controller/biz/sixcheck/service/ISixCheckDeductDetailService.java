/**
 * 
 * 
 * @author Zack
 * @date Aug 13, 2026
 */
package com.ruoyi.web.controller.biz.sixcheck.service;

import com.ruoyi.web.controller.biz.sixcheck.domain.SixCheckDeductDetail;
import java.util.List;

public interface ISixCheckDeductDetailService {
    int insert(SixCheckDeductDetail detail);
    int update(SixCheckDeductDetail detail);
    SixCheckDeductDetail selectById(Long id);
    List<SixCheckDeductDetail> selectList(SixCheckDeductDetail query);
}
