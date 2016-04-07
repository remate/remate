define(['jquery', 'validate','base/set-commission/save'], function(jquery,validate,save) {
    var $form = $('#J-commission-form');
    $('#J-all-save').on('click',function(){
        $form.submit();
    });
    $form.validate({
        debug: true,     
        focusInvalid: true,  
        onkeyup: false,
        submitHandler: function(form) {   
            save.save();
        },
        rules: {
            Proportion: {
                number : true,
                min: 1,
                max: 50
            }
        },
        messages: {
            Proportion: {
                number : '请输入数字',
                min :'佣金比例范围必须为1%－50%',
                max: '佣金比例范围必须为1%－50%'
            }
        }

    });
    return null;
});