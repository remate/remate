

//改价
require(['base/orders/order-rateModify']);
//发货
require(['base/orders/order-goodsSend']);
//订单取消
require(['base/orders/order-cancel']);

require(['jquery','base/utils','pager','plugins/upload/plupload.full.min'], function(jquery,utils,Pager,plup) {
    var orders = {
        init : function () {
            //页面基本数据或者Dom展示的初始化
            this.pageInit();
            //订单类型切换
            this.orderTypeChange();
            //订单搜索
            this.orderSearch();
            //导入订单
            this.orderImport();
            //导出订单
            this.orderExport();
            //分页
            this.pageSet();
            //订单字段排序
            this.orderSort();
        },
        pageInit : function () {
            //订单列表状态初始化
            var orderStatus = utils.tool.request('status');
            var orderName = utils.tool.request('orderName');
            var isDesc = utils.tool.request('isDesc');
            var searchTxt = utils.tool.request('key');//搜索
            $('[name="searchTxt"]').val(searchTxt);
            $('.order-list .am-form-search input').val(searchTxt);
            if (orderStatus) {
                $('#J_sectOrderType,[name="orderStatus"]').val(orderStatus);
                $('#J_sectOrderType').trigger("chosen:updated.chosen");//触发select事件
            }
            if (orderName && isDesc) {
                //console.log(isDesc);
                var sortObj = $('.am-table-sort span[data-ordername="' + orderName + '"]');
                $('[name="orderName"]').val(orderName);
                if (isDesc=='true') {
                    sortObj.attr('data-sort','false');
                    $('[name="isDesc"]').val('true');
                    sortObj.find('em').html('&#xe612;');
                }
                if (isDesc=='false') {
                    sortObj.attr('data-sort','true');
                    $('[name="isDesc"]').val('false');
                }
            }
        },
        orderTypeChange : function () {
            $('#J_sectOrderType').on('change',function(){
                var _this = $(this);
                var strOrderType = _this.val();
                if (strOrderType == 'All') {
                    window.location.href = '/sellerpc/orders/list';
                }else{
                    window.location.href = '/sellerpc/orders/list?status='+strOrderType;
                }
            });
        },
        orderSearch : function () {
            var sObj_ipt = $('.am-form-search').find('.am-form-field');
            var sObj_i = $('.am-form-search').find('.am-icon-search');
            sObj_ipt.on('keydown', function(ev) {
                var ev = ev || event;
                if (ev.keyCode == 13) {
                    ev.preventDefault();//阻止触发表单的默认提交事件
                    searchFunc();
                }
            });
            sObj_i.on('click',function(){
                searchFunc();
            });

            function searchFunc () {
                var sType = $('#J_sectOrderType').val();
                var sText = $('.am-form-search').find('.am-form-field').val();
                var reg = /^\s*$/;
                if (reg.test(sText)) {
                    sText = '';
                };
                var orderName = $('[name="orderName"]').val();
                var isDesc = $('[name="isDesc"]').val();
                var status = $('[name="orderStatus"]').val();
                window.location.href = '?orderName='+ orderName + '&isDesc=' + isDesc +'&status=' +status + '&key='+ sText;
            }
        },
        orderImport : function () {
            //方式一：
            // $('.excelFile').on('change',function(){
            //     var _this = $(this);
            //     if (!_this.val()) {
            //         utils.tool.alert('请选择需要导入的Excel文件！');
            //     }
            //     utils.tool.confirm('确定要导入订单吗(需要保证订单为待发货状态)？',function(){
            //         setTimeout(function(){
            //             $('#form-import').submit();
            //         },200);
            //         // $.ajax({
            //         //     url: $('#form-import').attr('action'),
            //         //     type: 'POST',
            //         //     data: $('#form-import').serialize(),
            //         //     cache:false,
            //         //     dataType: 'json',
            //         //     success: function(data) {
            //         //         console.log(data);
            //         //     },
            //         //     error: function() {
            //         //         setTimeout(function(){
            //         //             utils.tool.alert('导入订单失败！');
            //         //         },10);
            //         //     }
            //         // });
            //     });
            // }).on('mouseover',function(){
            //     $('#J_btnOrderImport').addClass('am-active');
            // }).on('mouseout',function(){
            //     $('#J_btnOrderImport').removeClass('am-active');
            // });

            //方式二：
            upload({
                id: 'J_btnOrderImport'
            },function(data){
                console.log(data);
                //返回值为字符串，200为成功，206为部分导入成功，500为失败
                data = $.trim(data);
                if (data == '200') {
                    utils.tool.alert('订单导入成功！');
                    setTimeout(function(){
                        location.reload();
                    },500);
                }else if (data=='206') {
                    utils.tool.alert('订单导入成功(部分订单由于数据原因导入失败)！');
                }else{
                    utils.tool.alert('订单导入失败！');
                }
            },function(data){
                utils.tool.alert('订单导入失败！');
            });
            function upload(options, success,fail) {
                var uploader = new plupload.Uploader({
                    runtimes: 'html5,flash,html4',
                    file_data_name: "excelFile",
                    browse_button: options.id,
                    url: '/sellerpc/order/import',
                    flash_swf_url: '/sellerpc/_resources/js/plugins/upload/Moxie.swf',
                    headers: {
                        Accept: "application/json;charset=utf-8"
                    },
                    filters: {
                        max_file_size: '10mb',
                        mime_types: [{
                            title: "Excel files",
                            extensions: "xls,xlsx"
                        }]
                    },
                    init: {
                        PostInit: function() {},
                        FilesAdded: function(up, files) {
                            uploader.start();
                        },
                        UploadProgress: function(up, file) {
                        },
                        Error: function(up, err) {
                            fail && fail(err);
                        },
                        FileUploaded: function(uploader, file, responseObject) {
                            success(responseObject.response);
                        }
                    }
                });
                uploader.init();
            }
        },
        orderExport : function () {
            var status = $('[name="orderStatus"]').val();//将要导出的订单状态
            $('#status_kwd').val(status);
            $('#shopId_kwd').val(shopId);
            $('#J_btnOrderExport').on('click',function(){
                $('#form-orderExport')[0].submit();
                // var parms = {
                //     status_kwd : status,
                //     shopId_kwd : shopId
                // };
                // $.ajax({
                //     url: '/sellerpc/order/exportExcel',
                //     type: 'POST',
                //     data: parms,
                //     dataType: 'json',
                //     cache:false,
                //     success: function(data) {
                //         if (data.rc=='1') {
                //             utils.tool.alert('导出订单成功！请到C盘KKKD目录查看导出文件。');
                //         }else{
                //             utils.tool.alert('导出订单失败！');
                //         }
                //     },
                //     error: function() {
                //         utils.tool.alert('导出订单失败！');
                //     }
                // });
            });
        },
        pageSet : function () {
            var pager = new Pager();
            var count = Number($('[name="pageTotal"]').val());
            var size0 = 10;
            pager.init($('.pagers'), {
                count: count,
                items_per_page: size0,
                current_page: Number($('[name="pageNum"]').val()),
                num_edge_entries: 2, //边缘页数
                num_display_entries: 3, //主体页数
                callback: function(pageIndex) {
                    //获取当前页面的状态信息
                    var orderName = $('[name="orderName"]').val();
                    var isDesc = $('[name="isDesc"]').val();
                    var status = $('[name="orderStatus"]').val();
                    var sText = $('[name="searchTxt"]').val();
                    var size = size0;
                    var page = pageIndex;
                    window.location.href = '/sellerpc/orders/list?orderName='+ orderName + '&isDesc=' + isDesc +'&status=' +status + '&size=' + size + '&page=' + page + '&key=' + sText;
                    return false;
                }
            });
        },
        orderSort : function () {
            $('.am-table-sort span').on('click',function(){
                var _this = $(this);
                //console.log(_this.data('sort'))
                if(_this.find('em').length){//是否可以根据该字段进行排序
                    var orderName = _this.attr('data-ordername');
                    var isDesc = _this.attr('data-sort');
                    var status = $('[name="orderStatus"]').val();
                    var sText = $('[name="searchTxt"]').val();
                    var reg = /^\s*$/;
                    if (reg.test(sText)) {
                        sText = '';
                    };
                    window.location.href = '/sellerpc/orders/list?orderName='+ orderName + '&isDesc=' + isDesc +'&status=' +status + '&key=' + sText;
                }else{
                    return false;
                }
            });
        }
    }
    orders.init();
});

