package com.ruoyi.web.controller.biz.common.service;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.web.controller.biz.kpi.domain.KpiScore;
import com.ruoyi.web.controller.biz.kpi.service.IKpiScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通用扣分/撤销服务
 */
@Service
public class CommonDeductService {

    @Autowired
    private ISysUserService userService;

    @Autowired
    private IKpiScoreService kpiScoreService;

    /**
     * 保存考核扣分记录
     * 
     * @param userId 被考核人ID
     * @param itemId 考核项目ID
     * @param score  分数
     * @param remark 备注
     */
    public void saveKpiScore(Long userId, Long itemId, BigDecimal score, String remark) {
        KpiScore kpiScore = new KpiScore();
        kpiScore.setUserId(userId);
        kpiScore.setItemId(itemId);
        kpiScore.setScore(score);
        kpiScore.setRemark(remark);
        kpiScore.setBatchNo(new SimpleDateFormat("yyyy-MM").format(new Date()));
        kpiScore.setCreateBy(ShiroUtils.getLoginName());
        kpiScoreService.insertKpiScore(kpiScore);
    }

    /**
     * 生成扣分描述字符串
     * 
     * @param userId 被考核人ID
     * @param remark 备注
     * @return （关联加扣分-用户名-备注）
     */
    public String buildDeductDesc(Long userId, String remark) {
        SysUser user = userService.selectUserById(userId);
        String userName = user != null ? user.getUserName() : "未知";
        return String.format("（关联加扣分-%s-%s）",
                userName,
                StringUtils.defaultString(remark, "无备注"));
    }

    /**
     * 将扣分描述追加到现有内容末尾
     * 
     * @param oldValue   原内容
     * @param deductDesc 扣分描述
     * @return 追加后的内容
     */
    public String appendDeductDesc(String oldValue, String deductDesc) {
        if (StringUtils.isNotBlank(oldValue) && !"正常".equals(oldValue)) {
            return oldValue + "\n" + deductDesc;
        } else {
            return deductDesc;
        }
    }

    /**
     * 从现有内容中移除扣分描述
     * 
     * @param oldValue   原内容
     * @param deductDesc 扣分描述
     * @return 移除后的内容，如果为空则返回"正常"
     */
    public String removeDeductDesc(String oldValue, String deductDesc) {
        if (oldValue == null || !oldValue.contains(deductDesc)) {
            return oldValue == null ? "正常" : oldValue;
        }
        String newValue = oldValue.replace("\n" + deductDesc, "").replace(deductDesc, "");
        return newValue.trim().isEmpty() ? "正常" : newValue.trim();
    }

    /**
     * 更新考核打分记录（扣分或撤销）
     * 
     * @param userName    被考核人用户名
     * @param batchNo     月份（YYYY-MM）
     * @param deductDesc  扣分描述（用于匹配记录）
     * @param scoreChange 分数变化量（撤销时传入负数）
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateKpiScoreByDeduct(String userName, String batchNo, String deductDesc, BigDecimal scoreChange) {
        SysUser queryUser = new SysUser();
        queryUser.setUserName(userName);
        List<SysUser> userList = userService.selectUserList(queryUser);
        if (userList.isEmpty())
            return;

        Long userId = userList.get(0).getUserId();
        KpiScore query = new KpiScore();
        query.setUserId(userId);
        query.setBatchNo(batchNo);
        List<KpiScore> scoreList = kpiScoreService.selectKpiScoreList(query);

        for (KpiScore score : scoreList) {
            if (score.getRemark() != null && score.getRemark().contains(deductDesc)) {
                if (score.getScore() != null && scoreChange != null) {
                    score.setScore(score.getScore().add(scoreChange));
                }
                String newRemark = score.getRemark().replace(deductDesc, "").trim();
                score.setRemark(newRemark.isEmpty() ? null : newRemark);
                score.setUpdateBy(ShiroUtils.getLoginName());
                kpiScoreService.updateKpiScore(score);
                System.out.println("=== updateKpiScoreByDeduct: 更新考核打分记录，userName=" + userName + ", batchNo=" + batchNo
                        + ", scoreId=" + score.getId() + ", 新分数=" + score.getScore() + ", 新备注=" + score.getRemark());
                break;
            }
        }
    }

    /**
     * 从扣分描述中解析用户名
     */
    public String parseUserName(String deductDesc) {
        Pattern p = Pattern.compile("（关联加扣分-(.+?)-.+）");
        Matcher m = p.matcher(deductDesc);
        return m.find() ? m.group(1).trim() : null;
    }

    /**
     * 从扣分描述中解析分数变化量
     * 
     * @return BigDecimal 分数变化量（用于撤销时扣回分数），如果没有分数则返回 null
     */
    public BigDecimal parseScoreChange(String deductDesc) {
        Pattern p = Pattern.compile("（关联加扣分-.+?-(-?\\d+\\.?\\d*)分-.+）");
        Matcher m = p.matcher(deductDesc);
        if (m.find()) {
            return new BigDecimal(m.group(1));
        }
        return null;
    }
}
