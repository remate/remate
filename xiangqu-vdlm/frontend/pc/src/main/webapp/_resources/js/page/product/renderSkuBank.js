define(['jquery', 'doT','base/product/elements','base/product/renderSku'], function(jquery, doT,eles,renderSku) {
    var tplSkuItem = $('.tpl-skuType').html(),
        doTtplSkuItem = doT.template(tplSkuItem);
    var banks = eles.skuBankData;
    $.each(banks, function(i, el) {
        eles.skuBank.append(doTtplSkuItem({
            'name': el
        }));
    });
    eles.skuBank.find('input').initCheck();
});