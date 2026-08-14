/*
 * @Author: xuyongke xuyongke123@163.com
 * @Date: 2026-08-13 16:23:15
 * @LastEditors: xuyongke xuyongke123@163.com
 * @LastEditTime: 2026-08-13 16:38:55
 * @FilePath: \RuoYi\ruoyi-admin\src\main\java\com\ruoyi\web\controller\biz\sixcheck\mapper\SixCheckDeductDetailMapper.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
/**
 * 
 * 
 * @author Zack
 * @date Aug 13, 2026
 */
package com.ruoyi.web.controller.biz.sixcheck.mapper;

import com.ruoyi.web.controller.biz.sixcheck.domain.SixCheckDeductDetail;
import java.util.List;

public interface SixCheckDeductDetailMapper {
    int insert(SixCheckDeductDetail detail);
    int update(SixCheckDeductDetail detail);
    SixCheckDeductDetail selectById(Long id);
    List<SixCheckDeductDetail> selectList(SixCheckDeductDetail query);
}
