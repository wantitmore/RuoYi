package com.ruoyi.web.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.system.service.ISysDeptService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

@Controller
@RequestMapping("/system/defaultcopy")
public class DefaultDataCopyController extends BaseController {

    @Autowired
    private DataSource dataSource;

    @Autowired 
    private ISysDeptService deptService;

    // 需要复制的三条 SQL 模板，部门ID占位符 ?
    private static final String[] COPY_SQLS = {
            // 量化考核项目
            "INSERT INTO kpi_item (name, max_score, score_type, dept_id, category, remark, create_by, create_time) " +
                    "SELECT proj.name, proj.max_score, proj.score_type, ?, proj.category, proj.remark, 'admin', NOW() "
                    +
                    "FROM ( " +
                    "SELECT '违反警容风纪或礼节礼貌规定' AS name, 20 AS max_score, 'NUMBER' AS score_type, '纪律作风' AS category, '视情节予以扣1-3分' AS remark "
                    +
                    "UNION ALL SELECT '违反学习、培训或会议纪律', 20, 'NUMBER', '纪律作风', '视情节予以扣1-3分' " +
                    "UNION ALL SELECT '不熟悉岗位职责、应知应会内容，掌握四知道、无册点名情况未达要求', 20, 'NUMBER', '纪律作风', '视情节予以扣1-3分' " +
                    "UNION ALL SELECT '违反考勤、备勤规定', 20, 'NUMBER', '纪律作风', '视情节予以扣1-3分' " +
                    "UNION ALL SELECT '违反警务用品管理规定', 20, 'NUMBER', '纪律作风', '视情节予以扣1-3分' " +
                    "UNION ALL SELECT '其他违反纪律作风管理规定', 20, 'NUMBER', '纪律作风', '视情节予以扣1-3分' " +
                    "UNION ALL SELECT '未按规定落实点名或清点人数', 20, 'NUMBER', '狱政', '视情节予以扣1-3分' " +
                    "UNION ALL SELECT '未按规定落实狱情排查、监管安全隐患排查及闭环处置', 20, 'NUMBER', '狱政', '视情节予以扣1-3分' " +
                    "UNION ALL SELECT '未按规定落实清仓、搜身或对违规违禁品、危化品未按要求处置', 20, 'NUMBER', '狱政', '视情节予以扣1-3分' " +
                    "UNION ALL SELECT '未按规定落实罪犯会见、顾送物品、寄收信件、亲情电话管理', 20, 'NUMBER', '狱政', '视情节予以扣1-3分' " +
                    "UNION ALL SELECT '未按规定落实罪犯互监组管理', 20, 'NUMBER', '狱政', '视情节予以扣1-3分' " +
                    "UNION ALL SELECT '未按规定处置罪犯违纪行为', 20, 'NUMBER', '狱政', '视情节予以扣1-3分' " +
                    "UNION ALL SELECT '未按规定落实罪犯内务卫生管理', 20, 'NUMBER', '狱政', '视情节予以扣1-3分' " +
                    "UNION ALL SELECT '未按规定落实罪犯耳目、信息员管理', 20, 'NUMBER', '狱政', '视情节予以扣1-3分' " +
                    "UNION ALL SELECT '未按规定开展罪犯计分考核', 20, 'NUMBER', '狱政', '视情节予以扣1-3分' " +
                    "UNION ALL SELECT '未按规定落实警察直接管理', 20, 'NUMBER', '狱政', '视情节予以扣1-3分' " +
                    "UNION ALL SELECT '未按时完成计分考核提请', 20, 'NUMBER', '狱政', '视情节予以扣1-3分' " +
                    "UNION ALL SELECT '狱情阅读量未达标（60条/月）', 20, 'NUMBER', '狱政', '视情节予以扣1-3分' " +
                    "UNION ALL SELECT '防自杀专项活动落实情况', 20, 'NUMBER', '狱政', '视情节予以扣1-3分' " +
                    "UNION ALL SELECT '其他违反狱政工作管理规定', 20, 'NUMBER', '狱政', '视情节予以扣1-3分' " +
                    "UNION ALL SELECT '未按规定建立、更新专管罪犯动态档案', 20, 'NUMBER', '教育', '视情节予以扣1-3分' " +
                    "UNION ALL SELECT '未按规定开展个别教育谈话', 20, 'NUMBER', '教育', '视情节予以扣1-3分' " +
                    "UNION ALL SELECT '未按规定开展危险性评估', 20, 'NUMBER', '教育', '视情节予以扣1-3分' " +
                    "UNION ALL SELECT '未及时调整顽危犯、重点犯防控措施', 20, 'NUMBER', '教育', '视情节予以扣1-3分' " +
                    "UNION ALL SELECT '未落实队前讲评工作要求', 20, 'NUMBER', '教育', '视情节予以扣1-3分' " +
                    "UNION ALL SELECT '未落实\"减假暂\"案件实质化审理', 20, 'NUMBER', '教育', '视情节予以扣1-3分' " +
                    "UNION ALL SELECT '未落实重点罪犯教育转化及专项档案登记更新', 20, 'NUMBER', '教育', '视情节予以扣1-3分' " +
                    "UNION ALL SELECT '未及时接访、化解、处置罪犯矛盾诉求', 20, 'NUMBER', '教育', '视情节予以扣1-3分' " +
                    "UNION ALL SELECT '重要节点出现专管罪犯违规违纪', 20, 'NUMBER', '教育', '视情节予以扣1-3分' " +
                    "UNION ALL SELECT '调出监监区动态档案移交情况', 20, 'NUMBER', '教育', '视情节予以扣1-3分' " +
                    "UNION ALL SELECT '其他违反教育工作管理规定', 20, 'NUMBER', '教育', '视情节予以扣1-3分' " +
                    "UNION ALL SELECT '动态档案实时更新进行考核评价', 20, 'NUMBER', '动态档案评价', '视情节予以扣1-3分' " +
                    "UNION ALL SELECT '未按要求落实外协人员陪同管理', 20, 'NUMBER', '生产', '视情节予以扣1-3分' " +
                    "UNION ALL SELECT '未按规定落实劳动工具、危化品管理要求', 20, 'NUMBER', '生产', '视情节予以扣1-3分' " +
                    "UNION ALL SELECT '未按规定落实工厂正门及安全门上开锁要求', 20, 'NUMBER', '生产', '视情节予以扣1-3分' " +
                    "UNION ALL SELECT '未按规定落实收工断电安全检查', 20, 'NUMBER', '生产', '视情节予以扣1-3分' " +
                    "UNION ALL SELECT '专管罪犯未完成月劳动定额任务', 20, 'NUMBER', '生产', '视情节予以扣1-3分' " +
                    "UNION ALL SELECT '未落实安全生产教育培训', 20, 'NUMBER', '生产', '视情节予以扣1-3分' " +
                    "UNION ALL SELECT '其他违反生产工作管理规定', 20, 'NUMBER', '生产', '视情节予以扣1-3分' " +
                    "UNION ALL SELECT '未按规定履行岗位职责', 20, 'NUMBER', '执勤现场管理', '视情节予以扣1-3分' " +
                    "UNION ALL SELECT '执勤期间做与工作无关的事情', 20, 'NUMBER', '执勤现场管理', '视情节予以扣1-3分' " +
                    "UNION ALL SELECT '执勤期间被通报且属于现场管理问题', 20, 'NUMBER', '执勤现场管理', '视情节予以扣1-3分' " +
                    "UNION ALL SELECT '未及时转递交接系统及异常狱情', 20, 'NUMBER', '执勤现场管理', '视情节予以扣1-3分' " +
                    "UNION ALL SELECT '其他违反执勤现场管理工作规定', 20, 'NUMBER', '执勤现场管理', '视情节予以扣1-3分' " +
                    "UNION ALL SELECT '值班领导巡查情况', 20, 'NUMBER', '正负面清单', '视情节予以加扣分' " +
                    "UNION ALL SELECT '各类检查主体检查通报', 20, 'NUMBER', '正负面清单', '视情节予以加扣分' " +
                    "UNION ALL SELECT '监区对三大现场视频回放民警履职情况', 20, 'NUMBER', '正负面清单', '视情节予以加扣分' " +
                    ") proj " +
                    "WHERE NOT EXISTS ( " +
                    "SELECT 1 FROM kpi_item existing " +
                    "WHERE existing.dept_id = ? AND existing.name = proj.name " +
                    ")",

            // 六必查检查项目
            "INSERT INTO six_check_item (dept_id, name, sort_order, create_by, create_time) " +
                    "SELECT ?, proj.name, proj.sort_order, 'admin', NOW() " +
                    "FROM ( " +
                    "SELECT '履职状态、到岗到位和物品定制' AS name, 1 AS sort_order " +
                    "UNION ALL SELECT '安全生产', 2 " +
                    "UNION ALL SELECT '劳动工具管理和外协人员管理', 3 " +
                    "UNION ALL SELECT '搜身', 4 " +
                    "UNION ALL SELECT '监管安全管理', 5 " +
                    "UNION ALL SELECT '清点人数', 6 " +
                    "UNION ALL SELECT '出收工队列和如厕管理', 7 " +
                    "UNION ALL SELECT '互监组管理', 8 " +
                    "UNION ALL SELECT '门禁门锁', 9 " +
                    "UNION ALL SELECT '其他', 10 " +
                    ") proj " +
                    "WHERE NOT EXISTS ( " +
                    "SELECT 1 FROM six_check_item si " +
                    "WHERE si.dept_id = ? AND si.name = proj.name " +
                    ")",

            // 视频回放检查项目
            "INSERT INTO video_check_item (dept_id, check_position, specific_content, sort_order, create_by, create_time) "
                    +
                    "SELECT ?, proj.check_position, proj.specific_content, proj.sort_order, 'admin', NOW() " +
                    "FROM ( " +
                    "SELECT '监舍一楼储物间、多功能厅厕所洗碗间、工厂会议室' AS check_position, '倒查罪犯进出一楼储物间、多功能厅厕所洗碗间情况：是否落实警察管理、是否存在落单；工厂会议室是否有警察私自带罪犯进入' AS specific_content, 1 AS sort_order "
                    +
                    "UNION ALL SELECT '分控室', '倒查警察夜间值班、中午值班、早上值班履职情况', 2 " +
                    "UNION ALL SELECT '小院搜身现场、罪犯夜值班履职情况、罪犯服药现场', '倒查警察搜身情况、夜值班罪犯履职情况、服药管理情况', 3 " +
                    "UNION ALL SELECT '图书室、心理矫治室、教育日各现场、早早班警察组织罪犯下楼时段', '倒查警察组织可视会见情况、非可视会见时段是否有警察进入心理矫治室做与工作无关事宜，教育现场组织情况，早餐组织罪犯下楼警察是否落实双岗、清场', 4 "
                    +
                    "UNION ALL SELECT '断电半小时巡查，工厂仓库、配电房、过道储物间、烤房、下午开工警察组织罪犯下楼时段、组织罪犯上厕所秩序', '断电半小时巡查落实情况；检查罪犯零星进出小房小室情况，下午开工组织罪犯下楼警察是否落实双岗、清场', 5 "
                    +
                    "UNION ALL SELECT '一分区警察一二号岗执勤岗、罪犯厕所、警察实点名时段、发放劳动工具时段', '倒查一分区警察一二号岗执勤岗警察履职情况、罪犯如厕组织是否有落单、警察是否落实实点名、是否落实警察直接发放', 6 "
                    +
                    "UNION ALL SELECT '二分区警察一二号岗执勤岗、罪犯厕所、警察实点名时段、发放劳动工具时段', '倒查二分区警察一二号岗执勤岗警察履职情况、犯如厕组织是否有落单、警察是否落实实点、是否落实警察直接发放', 7 "
                    +
                    "UNION ALL SELECT '二、三楼罪犯进出小房小室时段、拨打亲情电话时段、警察履职时段', '倒查楼层警察履职情况，组织进小房小室是否落清点人数、清场，拨打亲情电话是否落实监听', 8 "
                    +
                    "UNION ALL SELECT '三、四楼罪犯进出小房小室时段、拨打亲情电话时段、警察履职时段', '倒查楼层警察履职情况，组织进小房小室是否落清点人数、清场，拨打亲情电话是否落实监听', 9 "
                    +
                    ") proj " +
                    "WHERE NOT EXISTS ( " +
                    "SELECT 1 FROM video_check_item vci " +
                    "WHERE vci.dept_id = ? AND vci.check_position = proj.check_position " +
                    ")"
    };

    @PostMapping("/copyAll")
    @ResponseBody
    public AjaxResult copyAll(@RequestParam Long deptId) {
        int totalCopied = 0;
        try (Connection conn = dataSource.getConnection()) {
            for (String sql : COPY_SQLS) {
                // 每个 SQL 需要两个占位符（deptId 用于插入列和 NOT EXISTS 子查询）
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    // 根据 SQL 语句的结构，设置两个参数：目标部门ID 和 检查重复的部门ID
                    ps.setLong(1, deptId); // 第一个占位符对应 SELECT 中的列
                    ps.setLong(2, deptId); // 第二个占位符对应 NOT EXISTS 中的部门ID
                    int rows = ps.executeUpdate();
                    totalCopied += rows;
                }
            }
        } catch (Exception e) {
            logger.error("复制默认项目失败", e);
            return AjaxResult.error("复制失败：" + e.getMessage());
        }
        return AjaxResult.success("成功复制 " + totalCopied + " 个默认项目到目标部门！");
    }

    @GetMapping()
    public String index(ModelMap mmap) {
        List<SysDept> depts = deptService.selectDeptList(new SysDept());
        mmap.put("depts", depts);
        return "system/defaultCopy";
    }
}