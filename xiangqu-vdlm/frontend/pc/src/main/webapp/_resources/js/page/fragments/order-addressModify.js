//修改信息
define(['jquery','validate'],function(validate){
    //弹窗调用
    $(document).on('click','#J_addressEdit',function(){
        $('.J_addressEditPop').modal({
            relatedTarget: this,
            width: '640', 
            onConfirm: function(options) {
                formEdit();
            },
            onCancel: function() {
                this.close();
            }
        });
    });

    //修改信息并提交表单
    function formEdit(){
        var $formBuyerInfo = $('#form-buyerInfo');
        $(document).on('click','#form-buyerInfo .am-btn-comfirm-yes',function(){
            $formBuyerInfo.submit();
        });
        $formBuyerInfo.validate({
            debug: true,     
            focusInvalid: true,  
            onkeyup: false,
            submitHandler: function(form) {   
                alert('提交表单');
            },
            rules: {
                J_buyerName: {
                    required: true,
                    number : true,
                    min : 0
                },
                J_buyerTel: {
                    required: true,
                    number : true,
                    min : 0
                },
                J_buyerChart: {
                    required: true,
                    number : true,
                    min : 0
                },
                J_addCare: {
                    required: true,
                    number : true,
                    min : 0
                }
            },
            messages: {
                J_buyerName: {
                    required: '姓名不能为空',
                    min :'邮费不能小于0元',
                    number : '请输入数字'
                },
                J_buyerTel: {
                    required: '电话不能为空',
                    min :'金额不能小于0元',
                    number : '请输入数字'
                },
                J_buyerChart: {
                    required: '微信号码不能为空',
                    min :'邮费不能小于0元',
                    number : '请输入数字'
                },
                J_addCare: {
                    required: '收货地址要尽可能的详细',
                    min :'金额不能小于0元',
                    number : '请输入数字'
                }
            }
        });
    }

    return null;
});