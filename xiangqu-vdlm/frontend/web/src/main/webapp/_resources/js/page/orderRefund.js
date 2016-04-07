$(document).ready(function() {
    // $('.cart_btn').on('click',function(){
    //        if( ! vd_formCheck($('#order_refund_sorm')) ){
    //            alert('有信息未填');
    //            return;
    //        }
    //        alert('1')
    //    });

    //解决安卓placeholder问题

    $('.refundRequest .refundRequest-bar .refundRequest-ipt').on('focus', function() {
        if ($(this).val() == $(this).attr('j-placeholder')) {
            $(this).val('');
        }
    }).on('blur', function() {
        if ($(this).val() == '') {
            $(this).val($(this).attr('j-placeholder'));
        }
    });

    $('#refundStyle').on('change',function(){
        var obj = this;
        var index=obj.selectedIndex;
        if(index==1){
            $('#refundFee').val('最多退'+$('#refundmax').val());
            $('#refundFee').removeAttr("readonly");
        }else{
            $('#refundFee').val($('#refundmax').val());
            $('#refundFee').attr("readonly","true");
        }
    });

    var doing = false;
    $('.cart_btn_request').on('click', function() {

        var _this = this;
        if (doing) {
            return;
        }
        doing = true;
        setTimeout(function() {
            doing = false;
        }, 600);
        //if (!confirm("确定要提交退款申请吗?"))
        //    return;
        $('.J_tc').show();
        $('.J_cancel').click(function(){
            $('.J_tc').hide();
            return false;
        });
        $('.J_sure').click(function(){
            if (!$(_this).hasClass('disable')) {
                $(_this).addClass('disable');
                var orderId = $('#orderId').val();
                var params = {
                    buyerRequire: $('#buyerRequire').val(),
                    buyerReceived: $('#buyerReceived').val(),
                    refundMemo: $('#refundMemo').val(),
                    refundFee: $('#refundFee').val(),
                    refundReason: $('#refundReason').val(),
                    id: $('#id').val(),
                    orderId: orderId
                };
                $.getJSON('/order/refund/request', params, function(json) {
                    alert(json.msg);
                    if (json.rc == '1') {
                        location.href = "/order/" + orderId;
                    } else
                        $(_this).removeClass('disable');
                });
            }
        });
    });

    var $logisticsCompany = $('#logisticsCompany'),
        $otherCompany = $('#otherCompany'),
        $otherCompanyBar = $('.otherCompanyBar');
    //退款页面物流公司其他输入框的显示与隐藏
    $logisticsCompany.on('change', function() {
        if ($(this).val() == 'other') {
            $otherCompanyBar.show();
        } else {
            $otherCompanyBar.hide();
        }
    });

    //如果是填写退货信息的页面，按钮延迟显示，防止android低端手机出现点击bug
    if($('.cart_btn_ship').length){
        setTimeout(function(){
            $('.cart_btn_ship').css('display','inline-block');
        },800);
    }

    $('.cart_btn_ship').click(function() {
        var _this = this,
            iCompany = '';
        if (!confirm("确定要提交发货信息吗?")) {
            return;
        }
        //如果选择的是其他物流公司，则物流公司从下面自定义名称里取，同时需要判断物流公司名字
        if ($logisticsCompany.val() == 'other') {
            iCompany = $otherCompany.val();
        } else {
            iCompany = $logisticsCompany.val();
        }

        if (iCompany == '') {
            alert('物流公司名称不能为空!');
            return;
        }

        if (!$(_this).hasClass('disable')) {
            $(_this).addClass('disable');
            var orderId = $('#orderId').val();
            var params = {
                logisticsMemo: $('#logisticsMemo').val(),
                logisticsNo: $('#logisticsNo').val(),
                logisticsCompany: iCompany,
                id: $('#id').val()
            };
            $.getJSON('/order/refund/ship', params, function(json) {
                alert(json.msg);
                if (json.rc == '1') {
                    location.href = "/order/" + orderId;
                } else
                    $(_this).removeClass('disable');
            });
        }

    });

    $('.cart_btn_cancel').click(function() {
        var _this = this;
        if (!confirm("确定要取消申请吗?"))
            return;

        if (!$(_this).hasClass('disable')) {
            $(_this).addClass('disable');
            var orderId = $('#orderId').val();
            var id = $('#id').val();
            var params = {
                id: $('#id').val()
            };
            $.getJSON('/order/refund/cancel', params, function(json) {
                alert(json.msg);
                if (json.rc == '1') {
                    location.href = "/order/" + orderId;
                } else
                    $(_this).removeClass('disable');
            });
        }
    });
});