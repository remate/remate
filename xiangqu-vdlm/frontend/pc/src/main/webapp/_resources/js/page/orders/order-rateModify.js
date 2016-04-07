define(['jquery','validate','base/utils'], function(jquery,validate,utils) {
    //改价
    $(document).on('click','.am-rateEdit',function(){
        //弹窗数据初始化
        var _this = $(this);
        var orderObj = _this.parents('table'); 

        var orderId = orderObj.attr('data-orderid');    //订单id
        var goodsFee = orderObj.attr('data-goodsfee');   //总金额
        var paidFee = orderObj.attr('data-paidfee') || orderObj.attr('data-totalfee');

        var logisticsFee = orderObj.attr('data-logisticsfee') || '0.00';   //运费
        var discountfee = orderObj.attr('data-discountfee') || '0.00';    //优惠券
        var commissionfee = orderObj.attr('data-commissionfee') || '0.00';    //佣金

        $('#J_orderId').val(orderId);
        $('#J_post').val(logisticsFee);
        $('#J_rate').val(goodsFee);
        $('#J_discount').text('￥'+discountfee);
        $('#J_realTotal').text('￥'+paidFee);
        $('#J_commission').text('￥'+commissionfee);

        $('#J_formRate label.error').hide(); //清除弹窗刚打开的时候之前遗留的表单验证样式

        //改价后实收金额改动展示
        $('#J_post,#J_rate').on('blur',function(){
            if (!isNaN(Number($('#J_post').val())) && !isNaN(Number($('#J_rate').val()))) {
                var paidFee = (Number($('#J_post').val()).toFixed(2)*100 + Number($('#J_rate').val()).toFixed(2)*100 - Number(discountfee)*100 - Number(commissionfee)*100)/100;
                $('#J_realTotal').text('￥'+ paidFee.toFixed(2));
            }
        });

        $('.J_rateEditPop').modal({
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
    
    $.validator.addMethod('uneq',function(value,element,param){
    	return value!=param;
    },'not equal to');
    
    //表单验证
    function formEdit(){
        var $formRate = $('#J_formRate');
        $formRate.validate({
            debug: true,     
            focusInvalid: true,
            onkeyup: false,
            ignore: '',
            submitHandler: function(form) {
                $('#J_post,#J_rate').blur();
                //ajax提交表单
                var strUrl = '/sellerpc/order/update-price';
                var params = {
                    orderId : $('#J_orderId').val(),
                    goodsFee : Number($('#J_rate').val()).toFixed(2),
                    logisticsFee : Number($('#J_post').val()).toFixed(2)
                }
                $.ajax({
                    url: strUrl,
                    data: params,
                    type: 'POST',
                    cache:false,
                    dataType: 'json',
                    success: function(data) {
                        //根据返回的价格判断是否改价成功
                        var a = data.logisticsFee.toFixed(2) == Number($('#J_post').val()).toFixed(2);
                        var b = data.totalFee.toFixed(2) == Number($('#J_realTotal').text().slice(1)).toFixed(2);
                        if ( a && b) {//改价成功
                            var currentOrder = $('table[data-orderid="'+ data.id +'"]');
                            currentOrder.attr({'data-logisticsfee':data.logisticsFee,'data-totalfee':data.totalFee,'data-paidfee':data.paidFee});
                            currentOrder.find('.col5 .paidfee').text('￥'+data.totalFee.toFixed(2));
                            currentOrder.find('.col5 .logisticsfee').text('（含运费:￥'+ data.logisticsFee.toFixed(2) +'）');
                            $('.J_rateEditPop').modal('close');
                            utils.tool.alert('改价成功！');
                        }else{
                            utils.tool.alert('改价失败！');
                        }
                        
                    },
                    error: function() {
                        utils.tool.alert('改价失败！');
                    }
                });
            },
            rules: {
                post: {
                    required: true,
                    number:true,
                    min:0,
                    max:999
                },
                rate: {
                    required: true,
                    number:true,
                    min:0,
                    max:99999000,
                    uneq:0
                }
            },
            messages: {
                post: {
                    required: '邮费不能为空！',
                    number: '输入不合法',
                    min:'金额不能小于0',
                    max:'金额不能大于999'
                },
                rate: {
                    required: '总价不能为空！',
                    number: '输入不合法',
                    min:'金额不能小于0',
                    max:'金额不能大于99999000',
                    uneq:'商品价格不能为0'
                }
            }
        });
    }

    return null;
});