define(['jquery','base/product/getSkuData','underscore','doT','base/product/elements'], function(jquery,getSkuData,underscore,doT,eles) {
    return function() {
        var skuInfoTPL = $('.tpl-skuUI').html();
        var doTtmpl = doT.template(skuInfoTPL);
        var types = [];
        $.each(window.skuInfo.mappings, function(i, el) {
            //遍历每一项sku 取 第i项  判断 是否已经存在
            types[i] = types[i] || [];
            $.each(window.skuInfo.skus, function(j, sku) {
                if (_.indexOf(types[i], sku.sku[i]) < 0) {
                    types[i].push(sku.sku[i]);
                }
            });
        });
        $.extend(window.skuInfo, {
            price: getSkuData(window.skuInfo.skus).price,
            amount: getSkuData(window.skuInfo.skus).amount,
            types: types
        });
        if(window.skuInfo.mappings && window.skuInfo.mappings.length){
            eles.proPrice.val(window.skuInfo.price);
            eles.proAmount.val(window.skuInfo.amount);
        }
        $('.skus-show-box').html(doTtmpl(window.skuInfo)).show();
        //如果有sku组合，则默认商品价格隐藏，如果没有则显示
        if($('.skus-show-value').length){
            $('#defaultPrice').hide();
        }else{
            $('#defaultPrice').show();
            $('.skus-show-box').empty();
        }
    }
});