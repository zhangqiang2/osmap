$(function() {
    DetailUiControl.initControl();
});
(function(global, $) {
    function DetailUiControl() {
        var that = this;
        var sourceInfo;
        that.initControl = function() {
            sourceInfo = JSON.parse(localStorage.getItem("sourceInfo"));
            interactionUtil.init(sourceInfo);
            tableUtil.init();
            interfaceUtil.initVersionSelect(sourceInfo.versions);
            interfaceUtil.queryAllCommitInfo("current");
        }
        var interfaceUtil = {
            queryAllCommitInfo(versionName) {
                this.queryResouceByVersion(versionName);
                this.queryResouceByVersionAndZte(versionName);
            },
            queryResouceByVersion: function(versionName) {
                $.ajax({
                    url: '/hacker/opensource/queryChangesByVersion',
                    type: 'POST',
                    dataType: "json",
                    data: JSON.stringify({ versionname: versionName, projectName: sourceInfo.projectName }),
                    contentType: 'application/json',
                    success: function(result) {
                        if (result instanceof Array && result.length > 0 && result[0].versionCompanyContribute.length > 0) {
                            var companyInfo = result[0].versionCompanyContribute;
                            tableUtil.refreshTableData("companyTable", companyInfo);
                            chart.drawChart("companyChart", interfaceUtil.dataConvert(companyInfo));
                            $("#zte_info").show();
                        }
                    }
                });
            },
            queryResouceByVersionAndZte: function(versionname) {
                $.ajax({
                    url: '/hacker/opensource/getZteContribute',
                    type: 'POST',
                    dataType: "json",
                    data: JSON.stringify({ versionname: versionname, projectName: sourceInfo.projectName }),
                    contentType: 'application/json',
                    success: function(result) {
                        if (result && result.personContribute) {
                            var contributeInfo = result.personContribute;
                            tableUtil.refreshTableData("contributorTable", contributeInfo);
                            chart.drawChart("contributorChart", interfaceUtil.dataConvert(contributeInfo));
                        }
                    }
                });
            },
            initVersionSelect: function(data) {
                if (!(data instanceof Array)) {
                    return;
                }
                $("#select_version").append("<option  value='current'>当前版本</option >");
                $.each(data, function(index, item) {
                    $("#select_version").append("<option  value='" + this.versionName + "'>" + this.versionName + "</option >");
                });
                $("#select_version").append("<option  value='all'>全部版本</option >");
                $("#select_version").change(function(event) {
                    var versionName = $(this).val();
                    interfaceUtil.queryAllCommitInfo(versionName);
                });
            },
            dataConvert: function(data) {
                var pieData = { "name": [], "map": [] };
                for (var k = 0; k < data.length; k++) {
                    var name = data[k].company || data[k].contributor;                  
                    pieData.name.push(name);
                    pieData.map.push({ value: data[k].commitNumber, name: name })
                }             
                return pieData;
            }
        }
        var tableUtil = {
            init: function() {
                this.initTable("companyTable", [{
                    title: '排名',
                    formatter: function(value, row, index) {
                        return index + 1;
                    }
                }, {
                    field: 'company',
                    title: '公司'
                }, {
                    field: 'commitNumber',
                    title: '提交数'
                }]);
                this.initTable("contributorTable", [{
                    title: '排名',
                    formatter: function(value, row, index) {
                        return index + 1;
                    }
                }, {
                    field: 'contributor',
                    title: '贡献者'
                }, {
                    field: 'commitNumber',
                    title: '提交数'
                }]);
            },
            initTable: function(tableId, columns) {
                $('#' + tableId).bootstrapTable({
                    pagination: true,
                    pageSize: 5,
                    contentType: "application/json",
                    dataType: "json",
                    columns: columns

                });
            },
            refreshTableData: function(tableId, dataArray) {
                $('#' + tableId).bootstrapTable('load', dataArray);
            }
        }
        var chart = {
            drawChart: function(id, data) {
                var $chart = this.initChart($("div#" + id));              
                var option = this.generateChartOption(data);
                $chart.setOption(option);
            },
            initChart: function($el) {
                return echarts.init($el.get(0));
            },
            generateChartOption: function(data) {
                return option = {
                    color: ['#ff5800', '#4bb2c5', '#eaa228', '#c5b47f', '#579575', '#839557', '#958c12', '#953579', '#4b5de4', '#d8b83f'],
                    tooltip: {
                        trigger: 'item',
                        formatter: "{b} : {c} ({d}%)"
                    },
                    legend: {
                        orient: 'vertical',
                        left: 'right',
                        data: data.name
                    },
                    series: [{
                        type: 'pie',
                        radius: '80%',
                        center: ['40%', '50%'],
                        data: data.map,
                        itemStyle: {
                            normal: {
                                label: {
                                    position: 'inner',
                                    formatter: function(params) {
                                        return (params.percent - 0).toFixed(0) + '%'
                                    }
                                },
                                labelLine: {
                                    show: false
                                }
                            }
                        }
                    }]
                };
            }
        }
        var interactionUtil = {
            init: function(sourceInfo) {
                this.gotoHome();
                this.initSourceInfo(sourceInfo);
            },
            gotoHome: function() {
                $("img#home").click(function(event) {
                    location.href = "../index.html";
                });
            },

            initSourceInfo: function(sourceInfo) {
                $("#source_info header").text(sourceInfo.projectName);
                for (var key in sourceInfo) {
                    var tr = "无";
                    if (sourceInfo[key]) {
                        tr = sourceInfo[key];
                        if (key.indexOf("Url") > -1) {
                            tr = '<a href="' + sourceInfo[key] + '">' + sourceInfo[key] + '</a>';
                        }
                    }
                    $("#source_info>.content #" + key + " .value").append(tr);
                }
            }
        }
    }
    global.DetailUiControl = new DetailUiControl();
})(this, jQuery)
