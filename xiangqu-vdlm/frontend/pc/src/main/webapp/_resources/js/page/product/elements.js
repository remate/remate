define(['jquery'],function(jquery){
    return {
        proName: $('.product-name'),
        proDesc: $('.product-info'),
        proPrice: $('.product-price'),
        proAmount: $('.product-amount'),
        proRecomend: $('.proRecomend'),
        proStatus: $('[name="prudct-status"]'),
        editSkus: $('#J-eidt-skus'),
        proPubType: $('[name="prudct-pubType"]'),
        descShowBox: $('.desc-tabel-show'),
        callDescList: $('#J-desc-callList'),
        proImgsWrap: $('.desc-pop-imgs-box'),
        proDescBox: $('.desc-sort-box'),
        descList: $('#J-descList-pop'),
        skuBank: $('#J-skuBank'),
        skuSelf: $('#J-skuSelf'),
        addSkuSelf: $('#J-step1-add'),
        addSkuSelfBox: $('.step1-sku-add-model'),
        addSkuSelfName: $('.step1-sku-add-name'),
        addSkuSelfHide: $('.step1-sku-add-model .skuModify-del'),
        addSkuSelfOk: $('.step1-sku-add-model .skuModify-add'),
        goStep2: $('#goStep2'),
        skuBankData: ['型号', '配置', '图案', '颜色', '含量', '规格', '尺码', '尺寸', '材质', '形状', '工艺', '重量', '套餐']
    }
});