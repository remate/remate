define(['jquery', 'validate','base/product/save','base/utils','base/product/elements'], function(jquery,validate,save,utils,eles) {
    var $form = $('#J-product-form');
    $('#J-all-save').on('click',function(){
        $form.submit();
    });

    $form.validate({
        debug: true,
        focusInvalid: true,
        onkeyup: false,
        submitHandler: function(form) {
            // save.save();

            //判断商品图片是否为空
            if(!$('.product-imgs .desc-pop-imgs-box img').length){
                utils.tool.alert('商品图片不能为空!');
                return;
            }
            //如果选择计划发布，计划时间必须是未来时间
            if(eles.proStatus.filter(':checked').val() == 'FORSALE'){
                var timePlan = $('.date-picker input').val();
                if(timePlan == ''){
                    utils.tool.alert('计划发布时间不能为空~');
                    return false;
                }
                timePlan = new Date(timePlan);
                if (timePlan) {
                    var timeNow = new Date();
                    if (timePlan.getTime() <= timeNow.getTime()) {
                        utils.tool.alert('计划发布时间必须大于当前时间~');
                        return false;
                    };
                }
            }
            //判断商品维度是否超过
            if (window.skuInfo && window.skuInfo.mappings !== undefined && window.skuInfo.mappings.length > 2) {
                utils.tool.alert('所选类型不能超过2个!');
                return;
            }
            save();
        },
        rules: {
            'product-name': {
                required: true,
                maxlength : 50
            },
            'product-info': {
                required: true,
                maxlength : 500
            },
            'product-price':{
                required: true,
                min: 0.000000001,
                max: 99999000
            },
            'product-amount':{
                required: true,
                min: 0,
                digits: true,
                max: 99999999
            }
        },
        messages: {
            'product-name': {
                required: '商品名称不能为空',
                maxlength :'长度不能多于50个字符'
            },
            'product-info': {
                required: '商品描述不能为空',
                maxlength :'长度不能多于500个字符'
            },
            'product-price':{
                required: '商品价格不能为空',
                min: '商品价格必须大于0',
                max: '商品价格不能超过99999000'
            },
            'product-amount':{
                required: '库存数量不能为空',
                min: '库存数量必须大于0',
                digits: '库存必须为整数',
                max: '商品库存不能超过99999999'
            }
        }

    });
    return null;
});