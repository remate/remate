define(['jquery','base/product/getSkuData'],function(jquery,getSkuData){
    //检测价格和数量
    $('body').on('click', '.skus-show-box .skus-show-value', function() {
        var self = this;
        var result = [], //skus副本
            resultYes = [], //可用的skus
            resultAmount0 = []; //库存为0的sku
        $.each(window.skuInfo.skus, function(i, el) {
            result.push(el); //存储一份副本 ，不在原来的上操作
            if (el.amount == 0) {
                resultAmount0.push(el);
            }
        });

        $(this).toggleClass('active').siblings('.skus-show-value').removeClass('active');

        var selected = [];
        $('.skus-show-item').each(function(i, el) {
            var v = $(el).find('.active').text();
            selected.push(v);
        });
        //匹配数据 看匹配的总数 和 最低价格,同时排除没有的
        $.each(result, function(i, el) {
            var bYes = true;
            for (var j = 0; j < selected.length; j++) {
                if (!(selected[j] === '' || selected[j] === el.sku[j])) {
                    //如果没匹配
                    bYes = false;
                    break;
                }
            }
            if (bYes) {
                resultYes.push(el);
            }
        });
        var items = getSkuData(resultYes);
        $('.product-skus-show-price').html(items.price);
        $('.product-skus-show-amount').html(items.amount);
    });
    return null;
});