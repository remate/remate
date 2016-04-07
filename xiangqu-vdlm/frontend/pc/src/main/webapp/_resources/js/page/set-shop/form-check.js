define(['jquery', 'validate','base/set-shop/save'], function(jquery,validate,saveshop) {
    var $form = $('#J-shop-form');
    $('.set-footer .am-btn-comfirm-yes').on('click',function(){
        if(window.canSubmit){
           $form.submit();
        }
    });

    $.validator.addMethod("sellerMobile", function(value, element) {    
        var length = value.length;    
        return this.optional(element) || (/^[\+,\-,\d]{0,16}$/.test(value));    
    }, "请正确填写您的手机号码");

    $form.validate({
        debug: false,     
        focusInvalid: true,  
        onkeyup: false,
        errorPlacement: function(error, element) {  
            error.appendTo(element.parents('.am-form-group'));  
        },
        submitHandler: function(form) {   
            saveshop.save();
        },
        rules: {
            'shop-name': {
                required: true,
                minlength: 2,
                maxlength: 50
            },
            'shop-weixin': {
                maxlength: 40
            },
            'shop-desc': {
                required: true,
                maxlength: 200
            },
            'shop-province' : {
                required: true
            },
            'shop-notice': {
                maxlength: 200
            },
            'shop-mobile': {
                required: true,
                sellerMobile: true
            }
        },
        messages: {
            'shop-name': {
                required: '店铺名称不能为空',
                minlength: '店铺名称长度必须在2到50个字之间',
                maxlength: '店铺名称长度必须在2到50个字之间'
            },
            'shop-weixin': {
                maxlength: '微信长度最长为40个字符'
            },
            'shop-desc': {
                required: '店铺说明不能为空',
                maxlength: '店铺说明长度最长为200个字符'
            },
            'shop-province': {
                required: '店铺所在地不能为空'
            },
            'shop-notice': {
                maxlength: '店铺公告长度最长为200个字符'
            },
            'shop-mobile': {
                required: '联系电话不能为空',
                sellerMobile: '联系电话只允许输入' + '+、-' + '和数字，不能大于16位'
            }
        }

    });
    return null;
});

