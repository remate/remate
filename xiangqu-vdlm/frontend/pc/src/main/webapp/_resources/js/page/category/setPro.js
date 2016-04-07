define(['jquery', 'base/utils', 'base/category/getData','doT','moment', 'pager','base/all/checkbox'], function(jquery, utils, getData, doT, moment, Pager, checkbox) {
    function setPro(data) {
        if (!data.data.list.length) {
            $('[name="pageNum"]').val($('[name="pageNum"]').val() - 1);
            getData();
        }
        window.moment = moment;
        var tmpl = $('.tpl-proList').html();
        var pager = new Pager();
        //渲染商品
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
                    getData();
                }
                return false;
            }
        });
    }
    return setPro;
});