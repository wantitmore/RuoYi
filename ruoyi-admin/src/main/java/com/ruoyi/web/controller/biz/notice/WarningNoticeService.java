/**
 * 
 * 
 * @author Zack
 * @date Aug 21, 2026
 */
package com.ruoyi.web.controller.biz.notice;

import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.domain.SysNotice;
import com.ruoyi.system.domain.SysNoticeRead;
import com.ruoyi.system.mapper.SysNoticeReadMapper;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.ISysNoticeService;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.web.controller.biz.notice.mapper.BizUserMapper;
import com.ruoyi.web.controller.biz.notice.mapper.SysNoticeReceiverMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WarningNoticeService {
    @Autowired
    private ISysNoticeService noticeService;

    @Autowired
    private SysNoticeReadMapper noticeReadMapper;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private BizUserMapper bizUserMapper;

    @Autowired
    private SysNoticeReceiverMapper noticeReceiverMapper;

    private List<String> getReceiverRoleKeys() {
        String config = configService.selectConfigByKey("warning.receiver.role");
        if (StringUtils.isBlank(config)) {
            return Collections.singletonList("dept_main_leader");
        }
        // 直接使用 config 变量，不再重复查询
        return Arrays.asList(config.split(","));
    }

    private List<Long> getReceiverIdsByDeptAndRoles(Long deptId, List<String> roleKeys) {
        List<Long> allUserIds = new ArrayList<>();
        for (String roleKey : roleKeys) {
            List<Long> selectUserIdsByDeptAndRole = bizUserMapper.selectUserIdsByDeptAndRole(deptId, roleKey);
            allUserIds.addAll(selectUserIdsByDeptAndRole);
        }
        // 去重
        return allUserIds.stream().distinct().collect(Collectors.toList());
    }

    private void insertNoticeReadBatch(Long noticeId, List<Long> userIds) {
        if (userIds != null && !userIds.isEmpty()) {
            noticeReceiverMapper.batchInsert(noticeId, userIds);
        }
    }

    /**
     * 发送扣分预警
     */
    @Transactional(rollbackFor = Exception.class)
    public void sendDeductWarning(Long userId, String batchNo, BigDecimal totalScore) {
        SysUser user = userService.selectUserById(userId);
        if (user == null || user.getDeptId() == null) {
            return;
        }
        List<String> roleKeys = getReceiverRoleKeys();
        List<Long> receiverIds = getReceiverIdsByDeptAndRoles(user.getDeptId(), roleKeys);

        // 2. 构建预警内容
        String title = "考核扣分预警";
        String content = String.format("【%s】%s 在 %s 月份累计扣分已达 %d 分，请关注。",
                user.getDept().getDeptName(),
                user.getUserName(),
                batchNo,
                Math.abs(totalScore.intValue()));

        // 3. 插入公告（notice_type = '3' 表示预警）
        SysNotice notice = new SysNotice();
        notice.setNoticeTitle(title);
        notice.setNoticeContent(content);
        notice.setNoticeType("3");
        notice.setStatus("0");
        notice.setCreateBy(ShiroUtils.getLoginName());
        noticeService.insertNotice(notice);
        SysNotice query = new SysNotice();
        query.setNoticeTitle(notice.getNoticeTitle());
        query.setNoticeType(notice.getNoticeType());
        List<SysNotice> list = noticeService.selectNoticeList(query);
        Long noticeId = list.get(0).getNoticeId();
        // 4. 关联接收人
        insertNoticeReadBatch(noticeId, receiverIds);
    }

    /**
     * 发送月末无考核记录预警
     */
    @Transactional(rollbackFor = Exception.class)
    public void sendNoRecordWarning(Long deptId, List<String> userNames, String batchNo) {
        if (userNames == null || userNames.isEmpty()) {
            return;
        }

        List<String> roleKeys = getReceiverRoleKeys();
        List<Long> receiverIds = getReceiverIdsByDeptAndRoles(deptId, roleKeys);
        if (receiverIds.isEmpty()) {
            return;
        }

        // 获取部门名称
        SysUser tempUser = new SysUser();
        tempUser.setDeptId(deptId);
        List<SysUser> users = userService.selectUserList(tempUser);
        String deptName = users.isEmpty() ? "未知部门" : users.get(0).getDept().getDeptName();

        String title = "月末考核提醒";
        String content = String.format("【%s】以下人员 %s 月份无考核记录（加分或扣分），请及时核对：%s",
                deptName,
                batchNo,
                String.join("、", userNames));

        SysNotice notice = new SysNotice();
        notice.setNoticeTitle(title);
        notice.setNoticeContent(content);
        notice.setNoticeType("3");
        notice.setStatus("0");
        notice.setCreateBy("admin");
        noticeService.insertNotice(notice);
        SysNotice query = new SysNotice();
        query.setNoticeTitle(notice.getNoticeTitle());
        query.setNoticeType(notice.getNoticeType());
        List<SysNotice> list = noticeService.selectNoticeList(query);
        Long noticeId = list.get(0).getNoticeId();

        insertNoticeReadBatch(noticeId, receiverIds);
    }

}
