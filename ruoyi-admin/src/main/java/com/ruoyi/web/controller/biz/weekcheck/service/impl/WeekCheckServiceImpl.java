/**
 * 
 * 
 * @author Zack
 * @date Sep 01, 2026
 */
package com.ruoyi.web.controller.biz.weekcheck.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;          

import com.ruoyi.web.controller.biz.weekcheck.domain.WeekCheck;
import com.ruoyi.web.controller.biz.weekcheck.mapper.WeekCheckMapper;
import com.ruoyi.web.controller.biz.weekcheck.service.IWeekCheckService ;

/**
 * 周检查 Service 实现类
 */
@Service
public class WeekCheckServiceImpl implements IWeekCheckService {

    @Autowired
    private WeekCheckMapper weekCheckMapper;

    /**
     * 查询列表（支持按 week、deptId、createBy 等过滤）
     */
    @Override
    public List<WeekCheck> selectCheckList(WeekCheck query) {
        return weekCheckMapper.selectCheckList(query);
    }

    /**
     * 根据 userId + week + createBy 查询单条记录
     */
    @Override
    public WeekCheck selectByUserWeekAndCreator(Long userId, String week, String createBy) {
        return weekCheckMapper.selectByUserWeekAndCreator(userId, week, createBy);
    }

    /**
     * 根据ID查询
     */
    @Override
    public WeekCheck selectWeekCheckById(Long id) {
        return weekCheckMapper.selectWeekCheckById(id);
    }

    /**
     * 插入
     */
    @Override
    public int insertWeekCheck(WeekCheck record) {
        return weekCheckMapper.insertWeekCheck(record);
    }

    /**
     * 更新
     */
    @Override
    public int updateWeekCheck(WeekCheck record) {
        return weekCheckMapper.updateWeekCheck(record);
    }

    /**
     * 删除指定周的所有记录
     */
    @Override
    public int deleteByWeek(String week) {
        return weekCheckMapper.deleteByWeek(week);
    }

    /**
     * 删除指定用户指定周的记录
     */
    @Override
    public int deleteByUserIdAndWeek(Long userId, String week) {
        return weekCheckMapper.deleteByUserIdAndWeek(userId, week);
    }

    /**
     * 从 Map 中安全获取字符串值
     */
    private String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : "";
    }
}