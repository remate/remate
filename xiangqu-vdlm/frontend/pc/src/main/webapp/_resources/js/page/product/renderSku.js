define(['jquery', 'base/utils', 'base/product/elements', 'doT', 'base/product/combine', 'base/product/getSkuData', 'base/product/initSkuUI','base/all/checkbox'], function(jquery, utils, eles, doT, combine, getSkuData, initSkuUI,checkbox) {
    return function(data) {
        //整合sku数据
        var skuList = [];
        $.each(data.skuMappings, function(i, el) {
            window.skuInfo.mappings.push(el.specName);
            skuList.unshift(el.mappingValues);
        });
        $.each(combine(skuList), function(i, el) {
            window.skuInfo.skus.push({
                "sku": el,
                "price": 0,
                "amount": 0
            });
        });
        //修改对应sku的价格和数量
        for (var i = 0; i < data.skus.length; i++) {
            //对比数据
            for (var j = 0; j < window.skuInfo.skus.length; j++) {
                var isThis = true;
                $.each(window.skuInfo.skus[j].sku, function(index, el) {
                    if (el != data.skus[i]['spec' + (index + 1)]) {
                        isThis = false;
                    }
                });
                if (isThis) {
                    window.skuInfo.skus[j].price = data.skus[i].price;
                    window.skuInfo.skus[j].amount = data.skus[i].amount;
                }
            }
        }
        initSkuUI();
        //显示sku部分结束
    }
});