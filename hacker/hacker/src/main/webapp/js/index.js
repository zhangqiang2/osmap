$(function() {
    UISegmentedControl.initControl();
});
(function(global, $) {
    function UISegmentedControl() {
        var that = this;
        var app = { pages: 1, pageNum: 5, showPageNum: 5, source: [] };
        that.initControl = function() {
            interactionUtil.initInteraction();
        }
        that.opt = {
            importFile: function(file) {
                $.ajaxFileUpload({
                    url: '/hacker/opensource/importFile',
                    secureuri: false,
                    fileElementId: 'fileImport',
                    type: 'post',
                    dataType: 'text',
                    success: function(result) {
                        if (result == "success") {
                            swal("导入成功!", "", "success");
                            return;
                        }
                        swal("导入失败", "", "error");
                    },
                    error: function(result) {
                        swal("导入失败", "", "error");
                    }
                });
            },
            exportFile: function() {
                var src = "/hacker/opensource/downloadFile?type=" + $("#key_type").val() + "&value=" + $("#keyword").val() + "&fileType=" + $("#file_format").val();
                $("#exportIFrame").remove();
                var exportIFrame = document.createElement("iframe");
                exportIFrame.src = src;
                exportIFrame.id = "exportIFrame";
                exportIFrame.style.display = "none";
                document.body.appendChild(exportIFrame);
            },
            page: function(pageIndex) {
                $(".source-list").empty();
                var sourceListDivHtml = '';
                for (var i = (pageIndex - 1) * app.pageNum; i < app.source.length && i < pageIndex * app.pageNum; i++) {
                    sourceListDivHtml += sourceInfoUtil.drawOneSource(app.source[i], i);
                }
                $("a.paginate_button").removeClass('active');
                $(".paginate_button[data-value=" + pageIndex + "]").addClass('active');
                $(".source-list").append(sourceListDivHtml);
            },
            editSource: function(el) {
                var index = $(el).attr("data-value");
                $("#myModalLabel").attr("data-type", "edit");
                var sourceInfo = app.source[index];
                $("input.form-control").each(function(index, el) {
                    var id = $(this).attr("id");
                    $(this).val(sourceInfo[id]);
                });
                $("#sourceDialog").modal('show');
            },
            gotoDetail: function(el) {
                var id = $(el).attr("data-id");
                localStorage.setItem("sourceInfo", JSON.stringify(app.source[id]));
                location.href = "./page/detail.html";
            },
            addBaseInfo: function(el) {
                $("input.form-control").each(function(index, el) {
                    $(this).val("");
                });
                $("#myModalLabel").attr("data-type", "add");
                $("#myModalLabel").html("Add");
                $("#sourceDialog").modal('show');
            }
        }
        var sourceInfoUtil = {
            drawOneSource: function(val, index) {
                var divStr = '';
                var foundationName = val.foundationName ? val.foundationName : '无';
                var updateTime = val.updateTime.substring(0, val.updateTime.length - 2);
                divStr += '<div class="raw">';
                divStr += '<header><a data-id="' + index + '"onclick="UISegmentedControl.opt.gotoDetail(this)">' + val.projectName + '</a><a id="edit" data-value="' + index + '" onclick="UISegmentedControl.opt.editSource(this)">编辑</a><span style = "float: right;margin-right:20px"> ' + updateTime + '</span></header>';
                divStr += '<div class="content">';
                divStr += '<div class="col-md-3"><div class="block"><div class="rating start' + val.vitality + '"> </div>' + '<span class="titile">活跃度级别</span></div></div>';
                divStr += '<div class="col-md-3"><div class="block"><span class="value">' + val.licenseName + '</span>' + '<span class="titile">许可证</span></div></div>';
                divStr += '<div class="col-md-3"><div class="block"><span class="value">' + val.prjUrl + '</span>' + '<span class="titile">项目主页</span></div></div>';
                divStr += '<div class="col-md-3"><div class="block"><span class="value">' + foundationName + '</span>' + '<span class="titile">基金会</span></div></div>';
                divStr += '</div></div>';
                return divStr;
            },

        }
        var interfaceUtil = {
            updateResouce: function(url, updateData) {
                $.ajax({
                    url: url,
                    type: 'POST',
                    dataType: "json",
                    data: JSON.stringify(updateData),
                    contentType: 'application/json',
                    success: function(result) {
                        if (result.retCode == 0) {
                            swal("更新成功", "", "success");
                        } else {
                            swal("更新失败", "", "error");
                        }

                    }
                });
            },
            queryResouce: function(val, isByName) {
                if (val.trim() == "") {
                    swal("请输入搜索条件！", "", "warning");
                    return;
                }
                $("#toolbar").show();
                $("#background").hide();
                var url = '/hacker/opensource/queryResourceByName',
                    json = { "projectName": val };
                if (!isByName) {
                    url = '/hacker/opensource/queryResourceByUrl';
                    json = { "url": val };
                }
                $.ajax({
                    url: url,
                    type: 'POST',
                    dataType: "json",
                    data: JSON.stringify(json),
                    contentType: 'application/json',
                    success: function(result) {
                        if (result instanceof Array && result.length > 0) {
                            app.source = result;
                            var souceListDivHtml = '',
                                pageHtml = '';
                            app.pages = Math.ceil(result.length / app.pageNum);
                            for (var i = 0; i < app.source.length && i < app.pageNum; i++) {
                                souceListDivHtml += sourceInfoUtil.drawOneSource(app.source[i], i);
                            }
                            $(".source-list").empty();
                            $(".source-list").append(souceListDivHtml);
                            $(".page").append('<ul class="pagination" id="pagination1"></ul>');
                            $.jqPaginator('#pagination1', {
                                totalPages: app.pages,
                                visiblePages: app.showPageNum,
                                currentPage: 1,
                                onPageChange: function(num, type) {
                                    that.opt.page(num);
                                }
                            });
                        } else {
                            $(".source-list").empty();
                            swal("暂无数据", "", "warning");
                        }
                    }
                });
            },

        }
        var interactionUtil = {
            initInteraction: function() {
                this.infoInteraction();
                this.queryInteraction();
            },
            infoInteraction: function() {
                $("a.zte-info").click(function(event) {
                    $("#zte-info").toggle("fast");
                });
                $("#export_file").click(function(event) {
                    that.opt.exportFile();
                });
                $("#save_info").click(function(event) {
                    var data = {};
                    $("input.form-control").each(function(index, el) {
                        var id = $(this).attr("id");
                        data[id] = $(this).val();

                    });
                    var url = '/hacker/opensource/modifyProjectBase';
                    if ($("#myModalLabel").attr("data-type") == "add") {
                        url = "/hacker/opensource/insertProjectBase";
                    };
                    interfaceUtil.updateResouce(url, data);
                    $("#sourceDialog").modal('hide');
                });
            },
            queryInteraction: function() {
                $('#keyword').bind('keypress', function(event) {
                    if (event.keyCode == 13) {
                        var isByName = $("#key_type").val() == "name";
                        interfaceUtil.queryResouce($("#keyword").val(), isByName);
                    }

                });
                $("i.fa-search").click(function(event) {
                    var isByName = $("#key_type").val() == "name";
                    interfaceUtil.queryResouce($("#keyword").val(), isByName);
                });
            }
        }
    }
    global.UISegmentedControl = new UISegmentedControl();
})(this, jQuery)
