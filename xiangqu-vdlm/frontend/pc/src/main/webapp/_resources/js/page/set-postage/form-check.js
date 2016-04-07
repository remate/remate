define(['jquery', 'validate','base/set-postage/save','base/utils'], function(jquery,validate,save,utils) {
    var $form = $('#J-post-form');
    $('.set-footer .am-btn-comfirm-yes').on('click',function(){
        $form.submit();
    });

    $form.validate({
        debug: true,     
        focusInvalid: true,  
        onkeyup: false,
        submitHandler: function(form) {
            var postage = $.trim($('#money').val());
            if (parseFloat(postage) == 0) {
                utils.tool.alert('包邮金额必须大于0!');
                return false;
            }
            save.save();
        },
        rules: {
            post: {
                required: true,
                number: true,
                min: 0,
                max: 999999
            },
            money: {
                number : true,
                min : 0,
                max: 999999
            }
        },
        messages: {
            post: {
                required: '邮费不能为空',
                min :'邮费不能小于0元',
                number : '请输入数字',
                max: '邮费最大不能超过999999'
            },
            money: {
                min :'金额不能小于0',
                number : '请输入数字',
                max: '最大值为999999'
            }
        }

    });
    return null;
});