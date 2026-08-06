/**
 * 通用加扣分弹窗 / 撤销功能
 * 依赖：jQuery、layer（若依自带）、ctx（项目根路径）
 */

/**
 * 通用关联加扣分弹窗
 * @param {Object} opts
 *   opts.title          - 弹窗标题（检查项名称）
 *   opts.defaultUser    - 默认被考核人姓名（如值班领导）
 *   opts.sourceId       - 来源检查项ID（六必查 itemId 或视频回放 itemId）
 *   opts.saveUrl        - 后端保存接口URL（如 "kpi/score/quickDeduct"）
 *   opts.extraData      - 额外提交参数（如 { sixCheckItemId, checkDate, shift } 或 { videoItemId, videoDate }）
 */
function openScoreDeductModal(opts) {
    // 从全局变量或 Thymeleaf 注入的 currentDeptId 获取部门ID
    var deptId = typeof currentDeptId !== 'undefined' ? currentDeptId : null;
    if (!deptId) {
        alert("无法获取部门信息");
        return;
    }

    // 同时加载本部门用户和考核项目
    $.when(
        $.get(ctx + "kpi/score/getUsersByDept?deptId=" + deptId),
        $.get(ctx + "kpi/score/getItemsByDept?deptId=" + deptId)
    ).done(function (usersRes, itemsRes) {
        var users = usersRes[0].data || [];
        var items = itemsRes[0].data || [];

        // 生成被考核人下拉框
        var userOptions = '<option value="">请选择</option>';
        $.each(users, function (i, u) {
            var selected = (u.userName === opts.defaultUser) ? ' selected' : '';
            userOptions += '<option value="' + u.userId + '"' + selected + '>' + u.userName + '</option>';
        });

        // 提取所有考核类别
        var categories = [];
        $.each(items, function (i, item) {
            if (item.category && categories.indexOf(item.category) === -1) {
                categories.push(item.category);
            }
        });
        var catOptions = '<option value="">请选择类别</option>';
        $.each(categories, function (i, cat) {
            catOptions += '<option value="' + cat + '">' + cat + '</option>';
        });

        // 弹窗内容
        var contentHtml = '<div style="padding:15px;">' +
            '<form class="form-horizontal">' +
            '<div class="form-group"><label class="col-sm-3 control-label">被考核人：</label><div class="col-sm-8"><select id="deductUserId" class="form-control">' + userOptions + '</select></div></div>' +
            '<div class="form-group"><label class="col-sm-3 control-label">考核类别：</label><div class="col-sm-8"><select id="deductCategory" class="form-control">' + catOptions + '</select></div></div>' +
            '<div class="form-group"><label class="col-sm-3 control-label">考核项目：</label><div class="col-sm-8"><select id="deductItemId" class="form-control"><option value="">请先选择类别</option></select></div></div>' +
            '<div class="form-group"><label class="col-sm-3 control-label">分数：</label><div class="col-sm-8"><input type="number" id="deductScore" class="form-control" step="0.5" /></div></div>' +
            '<div class="form-group"><label class="col-sm-3 control-label">备注：</label><div class="col-sm-8"><textarea id="deductRemark" class="form-control" rows="2"></textarea></div></div>' +
            '</form></div>';

        layer.open({
            type: 1,
            title: '关联加扣分 - ' + opts.title,
            shadeClose: false,
            shade: 0.3,
            area: ['550px', '400px'],
            btn: ['确定', '取消'],
            content: contentHtml,
            yes: function (index, layero) {
                var userId = $(layero).find('#deductUserId').val();
                var itemId = $(layero).find('#deductItemId').val();
                var score = $(layero).find('#deductScore').val();
                var remark = $(layero).find('#deductRemark').val();
                if (!userId || !itemId || !score) {
                    alert("请填写完整信息");
                    return;
                }
                // 构建提交数据，合并额外参数
                var data = {
                    userId: userId,
                    itemId: itemId,
                    score: parseFloat(score),
                    remark: remark
                };
                $.extend(data, opts.extraData);

                $.ajax({
                    url: ctx + opts.saveUrl,
                    type: "POST",
                    contentType: "application/json",
                    data: JSON.stringify(data),
                    success: function (res) {
                        if (res.code == 0) {
                            alert("加扣分成功！");
                            layer.close(index);
                            // 回调刷新表格（如果外部有 loadData 函数）
                            if (typeof loadData === 'function') loadData();
                        } else {
                            alert("操作失败：" + res.msg);
                        }
                    }
                });
            },
            btn2: function (index) {
                layer.close(index);
            },
            success: function (layero, index) {
                // 类别联动考核项目
                $(layero).find('#deductCategory').on('change', function () {
                    var cat = $(this).val();
                    var itemSelect = $(layero).find('#deductItemId');
                    var html = '<option value="">请选择项目</option>';
                    if (cat) {
                        $.each(items, function (i, item) {
                            if (item.category === cat) {
                                html += '<option value="' + item.id + '">' + item.name + '</option>';
                            }
                        });
                    }
                    itemSelect.html(html);
                });
            }
        });
    });
}

/**
 * 通用撤销扣分
 * @param {String} cancelUrl  - 后端撤销接口URL（如 "sixcheck/record/cancelDeduct"）
 * @param {Object} params     - 传给后端的参数（至少包含 itemId 和 deductInfo，以及模块特定字段）
 * @param {Function} callback - 成功后回调（通常刷新表格）
 */
function cancelScoreDeduct(cancelUrl, params, callback) {
    if (!confirm("确定要撤销这条加扣分吗？\n" + params.deductInfo)) return;
    $.ajax({
        url: ctx + cancelUrl,
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(params),
        success: function (res) {
            if (res.code == 0) {
                alert("撤销成功");
                if (typeof callback === 'function') callback();
            } else {
                alert("操作失败：" + res.msg);
            }
        }
    });
}