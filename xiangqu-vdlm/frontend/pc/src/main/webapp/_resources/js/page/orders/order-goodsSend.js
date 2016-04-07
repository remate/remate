define(['jquery','validate','base/utils'], function(jquery,validate,utils) {
    //加载快递公司
    utils.api.getLogistics(function(data){
        var html = '';
        var optTxt = '';
        for (var i = 0; i < data.data.length; i++) {
            var type = data.data[i].name;
            switch(type){
                case 'SF_EXPRESS': optTxt='顺丰快递';
                    break; 
                case 'YTO': optTxt='圆通快递';
                    break;
                case 'STO': optTxt='申通快递';
                    break;
                case 'ZTO': optTxt='中通快递';
                    break;
                case 'BESTEX': optTxt='百世汇通';
                    break;
                case 'YUNDA': optTxt='韵达快递';
                    break;
                case 'TTKD': optTxt='天天快递';
                    break;
                case 'QFKD': optTxt='全峰快递';
                    break;
                case 'ZJS': optTxt='宅急送';
                    break;
                case 'EMS': optTxt='邮政EMS';
                    break;
                case 'OTHER': optTxt='其他';
                    break;
                default: optTxt='其他';
            }
            html += '<option value="'+ data.data[i].name +'">'+optTxt +'</option>';
        };
        $('#J_kdSect').append(html);
        $('#J_kdSect').trigger("chosen:updated.chosen");//触发select事件
    });

    //发货
    $(document).on('click','.am-goodsSendPop',function(){
        //弹窗数据初始化
        var _this = $(this);
        var orderObj = _this.parents('table');

        var orderId = orderObj.attr('data-orderid');    //订单id
        $('#J_orderId').val(orderId);

        $('#J_formGoodsSends label.error').hide(); //清除弹窗刚打开的时候之前遗留的表单验证样式

        $('.J_goodsSendPop').modal({
            relatedTarget: this,
            width: '320', 
            closeViaDimmer: false, 
            onConfirm: function(options) {
                //点击弹窗的确认按钮时的相关操作
                formEdit();
            },
            onCancel: function() {
                this.close();
            }
        });
    });

    //表单验证
    function formEdit(){
        var $formRate = $('#J_formGoodsSends');
        $formRate.validate({
            debug: true,     
            focusInvalid: true,
            onkeyup: false,
            ignore: '',
            submitHandler: function(form) {
               //ajax操作
                var strUrl = '/sellerpc/order/shipped';
                var params = {
                    orderId : $('#J_orderId').val(),
                    logisticsOrderNo : $('#J_logisticsNo').val(),
                    logisticsCompany : $('#J_kdSect').val()
                }
                $.ajax({
                    url: strUrl,
                    data: params,
                    type: 'POST',
                    cache:false,
                    dataType: 'json',
                    success: function(data) {
                        if (data) {
                            //发货成功后，关闭弹窗+信息提示+页面刷新
                            $('.J_goodsSendPop').modal('close');
                            utils.tool.alert('发货成功！');
                            location.reload();
                        }else{
                            utils.tool.alert('发货失败！');
                        }
                    },
                    error: function() {
                        utils.tool.alert('发货失败！');
                    }
                });
            },
            rules: {
                logisticsNo: {
                    required: true,
                    maxlength: 50
                },
                kdSect: {
                    required: true
                }
            },
            messages: {
                logisticsNo: {
                    required: '快运单号不能为空',
                    maxlength: '快运单号不能超过50个字符'
                },
                kdSect: {
                    required: '请选择快递公司'
                }
            },
            errorPlacement: function(error, element) {  
                error.appendTo(element.parent());  
            }
        });
    }
    
    return null;
});