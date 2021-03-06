define(['jquery', 'base/utils', 'doT', 'moment', 'pager','base/all/checkbox'], function(jquery, utils, doT, moment, Pager,checkbox) {
    window.moment = moment;
    var tmpl = $('.tpl-proList').html();
    var pager = new Pager();
    //获取商品列表
    var isLoading = false;
    function getdata() {
        if(isLoading){
            return;
        }
        isLoading = true;
        var size = $('[name="pageSize"]').val();
        var pageNum = $('[name="pageNum"]').val();
        var pageOffset = (pageNum - 1) * size;
        var orderField = $('[name="orderField"]').val();
        var direction = $('[name="direction"]').val();
        var keyword = $('.am-form-search input').val();
        var status = $('[name="status"]').val();
        var options = {};
        options.order = orderField;

        //批量上下架按钮
        $('#J-downs,#J-ups').hide();
        if (status == 'onsale') {
            $('#J-downs').show();
        } else if (status == 'offsale') {
            $('#J-ups').show();
        }

        if (status == 'delay') {
            $('.am-table-sort .col2 i').html('计划发布时间');
        } else {
            $('.am-table-sort .col2 i').html('销量');
        }

        if (orderField) {
            options.direction = direction;
        }
        if (keyword) {
            options.keyword = keyword;
        }
        options.page = pageNum - 1;
        options.size = size;

        utils.api.getProductList(status, options, function(data) {
            isLoading = false;
            var doTtmpl = doT.template(tmpl);
            $('.order-item-body').html(doTtmpl(data));
            //分页数据请求加载之后重置全选
            $('.checkAll').attr('checked',false);
            $('.kd-checkbox,.kd-radio').initCheck();

            pager.init($('.pagers'), {
                count: data.data.categoryTotal,
                current_page: $('[name="pageNum"]').val() - 1,
                callback: function(pageIndex) {
                    $('[name="pageNum"]').val(pageIndex + 1);
                    if(pageIndex >= 0){
                        getdata();
                    }
                    return false;
                }
            });

        }, function(code) {
            isLoading = false;
        });
    }
    return getdata;
});