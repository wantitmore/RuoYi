/**
 * 
 * 
 * @author Zack
 * @date Sep 15, 2026
 */
package com.ruoyi.web.controller.biz.activeprofile.service.impl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.web.controller.biz.activeprofile.domain.BizCriminal;
import com.ruoyi.web.controller.biz.activeprofile.domain.CriminalImportDto;
import com.ruoyi.web.controller.biz.activeprofile.mapper.BizCriminalMapper;
import com.ruoyi.web.controller.biz.activeprofile.service.IBizCriminalService;

@Service
public class BizCriminalServiceImpl implements IBizCriminalService {

    @Autowired
    private BizCriminalMapper criminalMapper;

    @Autowired
    private ISysUserService userService;

    @Override
    @DataScope(deptAlias = "d")
    public List<BizCriminal> selectCriminalList(BizCriminal query) {
        return criminalMapper.selectCriminalList(query);
    }

    @Override
    public BizCriminal selectCriminalById(Long id) {
        return criminalMapper.selectCriminalById(id);
    }

    @Override
    public int insertCriminal(BizCriminal record) {

        // 囚号唯一性校验
        BizCriminal exist = criminalMapper.selectCriminalByBh(record.getBh());

        SysUser user = ShiroUtils.getSysUser();
        if (record.getDeptId() == null) {
            record.setDeptId(user.getDeptId());
        }
        if (StringUtils.isBlank(record.getDataSource())) {
            record.setDataSource("manual");
        }
        record.setCreateBy(ShiroUtils.getLoginName());
        return criminalMapper.insertCriminal(record);
    }

    @Override
    public int updateCriminal(BizCriminal record) {
        record.setUpdateBy(ShiroUtils.getLoginName());
        return criminalMapper.updateCriminal(record);
    }

    @Override
    public int deleteCriminalById(Long id) {
        return criminalMapper.deleteCriminalById(id);
    }

    @Override
    public int deleteCriminalByIds(Long[] ids) {
        return criminalMapper.deleteCriminalByIds(ids);
    }

    @Override
    public int claim(Long id) {
        SysUser user = ShiroUtils.getSysUser();
        int rows = criminalMapper.claimCriminal(id, user.getUserId(), user.getUserName());

        return rows;
    }

    @Override
    public int returnToPublic(Long id) {
        SysUser user = ShiroUtils.getSysUser();
        int rows = criminalMapper.returnToPublic(id, user.getUserId());

        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int transferToUser(Long id, Long targetUserId) {
        SysUser target = userService.selectUserById(targetUserId);

        return criminalMapper.transferToUser(id, targetUserId, target.getUserName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchTransferToUser(Long[] ids, Long targetUserId) {
        SysUser target = userService.selectUserById(targetUserId);

        return criminalMapper.batchTransferToUser(ids, targetUserId, target.getUserName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int transferToDept(Long id, Long targetDeptId) {

        return criminalMapper.transferToDept(id, targetDeptId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchTransferToDept(Long[] ids, Long targetDeptId) {
        return criminalMapper.batchTransferToDept(ids, targetDeptId);
    }

    @Override
    public List<SysDept> selectAllDepts() {
        return criminalMapper.selectAllDepts();
    }

    @Override
    public int upsertBySync(BizCriminal record) {
        return criminalMapper.upsertBySync(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String importExcel(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "上传文件不能为空";
        }

        try (InputStream is = file.getInputStream()) {
            // 1. 解析 Excel
            ExcelUtil<CriminalImportDto> util = new ExcelUtil<>(CriminalImportDto.class);
            List<CriminalImportDto> list = util.importExcel(is);

            if (list == null || list.isEmpty()) {
                return "Excel 中没有数据或格式不正确";
            }

            int successCount = 0;
            int failCount = 0;
            StringBuilder failMsg = new StringBuilder();
            Set<String> bhSet = new HashSet<>(); // 用于拦截 Excel 内部的重复行

            // 2. 遍历处理每一行
            for (int i = 0; i < list.size(); i++) {
                CriminalImportDto dto = list.get(i);
                int rowNum = i + 2;

                try {
                    // 必填校验
                    if (StringUtils.isBlank(dto.getBh()) || StringUtils.isBlank(dto.getXm())) {
                        failCount++;
                        failMsg.append("第").append(rowNum).append("行缺少编号或姓名； ");
                        continue;
                    }
                    String bhTrim = dto.getBh().trim();

                    // Excel 内部重复拦截
                    if (bhSet.contains(bhTrim)) {
                        failCount++;
                        failMsg.append("第").append(rowNum).append("行在Excel中重复出现； ");
                        continue;
                    }
                    bhSet.add(bhTrim);

                    // 3. 查询数据库里是否已存在（包含已软删除的）
                    BizCriminal exist = criminalMapper.selectCriminalByBh(bhTrim);

                    BizCriminal record = new BizCriminal();
                    record.setBh(bhTrim);
                    record.setXm(dto.getXm().trim());
                    record.setDataSource("manual");
                    record.setCreateBy("import_excel");

                    // 4. 自动分配专管警察
                    if (StringUtils.isNotBlank(dto.getZgjc())) {
                        SysUser queryUser = new SysUser();
                        queryUser.setUserName(dto.getZgjc().trim());
                        List<SysUser> users = userService.selectUserList(queryUser).stream()
                                .filter(u -> u.getUserName().equals(dto.getZgjc().trim()))
                                .collect(Collectors.toList());

                        if (users != null && users.size() == 1) {
                            SysUser police = users.get(0);
                            record.setOwnerId(police.getUserId());
                            record.setOwnerName(police.getUserName());
                            record.setDeptId(police.getDeptId());
                            System.out.println(">>> 第" + rowNum + "行 匹配到干警: " + police.getUserName());
                        } else if (users != null && users.size() > 1) {
                            failCount++;
                            failMsg.append("第").append(rowNum).append("行警察重名无法分配； ");
                            continue;
                        } else {
                            failCount++;
                            failMsg.append("第").append(rowNum).append("行查无此人：").append(dto.getZgjc()).append("； ");
                            continue;
                        }
                    } else {
                        // 如果没填专管警察，可以把 deptId 设为入监监区，或者拦截
                        failCount++;
                        failMsg.append("第").append(rowNum).append("行未填写专管警察； ");
                        continue;
                    }

                    // 5. ====== 核心：组装并合并 JSON ======
                    Map<String, Object> dataMap = new HashMap<>();
                    // 如果数据库里已有记录，先解析出旧的数据
                    if (exist != null && StringUtils.isNotBlank(exist.getDataJson())) {
                        try {
                            dataMap = JSON.parseObject(exist.getDataJson(), new TypeReference<Map<String, Object>>() {
                            });
                        } catch (Exception e) {
                            // 旧数据格式坏了，就重新建一个空的
                            dataMap = new HashMap<>();
                        }
                    }

                    // 取出旧的 module1，如果不存在就 new 一个
                    Map<String, Object> m1 = new HashMap<>();
                    if (dataMap.get("module1") instanceof Map) {
                        m1 = (Map<String, Object>) dataMap.get("module1");
                    }

                    // 把 Excel 里有的字段 put 进去覆盖，没有的（比如家庭关系）不会被碰到
                    m1.put("zgjc", dto.getZgjc());
                    m1.put("bczy", dto.getBczy());
                    m1.put("csny", dto.getCsny());
                    m1.put("jg", dto.getJg());
                    m1.put("rjrq", dto.getRjrq());
                    m1.put("zm", dto.getZm());
                    m1.put("ypxq", dto.getYpxq());
                    m1.put("xqzr", dto.getXqzr());
                    m1.put("zdzf", dto.getZdzf());
                    m1.put("slzf", dto.getSlzf());
                    m1.put("stzk", dto.getStzk());

                    // 放回主 Map
                    dataMap.put("module1", m1);
                    record.setDataJson(JSON.toJSONString(dataMap));
                    record.setRawJson("{}");

                    // 6. 执行插入/更新
                    // 这里依赖 mapper 里的 ON DUPLICATE KEY UPDATE 自动恢复 del_flag 并更新数据
                    criminalMapper.insertCriminal(record);
                    successCount++;

                } catch (Exception e) {
                    failCount++;
                    failMsg.append("第").append(rowNum).append("行解析异常； ");
                    e.printStackTrace(); // 调试时可以打开
                }
            }

            return "导入完成！成功：" + successCount + "条，失败：" + failCount + "条。 "
                    + (failCount > 0 ? "详情：" + failMsg.toString() : "");

        } catch (Exception e) {
            return "导入失败：" + e.getMessage();
        }
    }
}