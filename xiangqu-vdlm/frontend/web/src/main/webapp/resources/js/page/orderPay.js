//define(function(require) {
    var $ = require('jquery');
    //var $ = require('jquery/1.11.1/jquery.js');
    var $$={
        ORDERAGAINBTN:$("#order-again-btn"),
        ORDERPAYFORM:$('#id-cart'),
        PAYTYPE:$('#payType'),
        CARTVIPINPUT:$(".cart-vip-input"),
        VIP2:$("#vip2"),
        AGREEMENTID:$("#payAgreementId"),
        CARTYOUHUIMANAME:$("#cart-youhuima-name"),
        PAYCARD:$('#pay-card'),
        CARTVIPSELECT:$(".cart-vip-select").find(".moni-checkbox")

    }
    function youhuiquanChoose(){
        var j=0;
        $$.CARTVIPINPUT.each(function(i,item){
            if($(item).is(':checked')) {
                var couponType = "<input class='preferential-hiddle' type='hidden' name='coupon"+"[" + j + "]."+"activityId' " + " value='" + $(item).attr('data-coupon-type') + "'>";
                var extCouponId = "<input class='preferential-hiddle' type='hidden' name='coupon"+"[" + j + "]."+"extCouponId' " + " value='" + $(item).attr('data-extcouponid') + "'>";
                var id = "<input class='preferential-hiddle' type='hidden' name='coupon"+"[" + j + "]."+"id' " + " value='" + $(item).attr('data-id') + "'>";
                var code = "<input class='preferential-hiddle youhuima-hiddle' type='hidden' name='coupon"+"[" + j + "]."+"code' " + " value='" + $('#youhuimaInput').val() + "'>";

                var discount="<input class='preferential-hiddle' type='hidden' name='coupon["+j+"].discount'  value='"+$(item).attr("data-numb")+"'>";

                $$.AGREEMENTID.after(couponType);
                $$.AGREEMENTID.after(extCouponId);
                $$.AGREEMENTID.after(id);
                $$.AGREEMENTID.after(discount);
                // if ($(item).hasClass("cart-vip2-input") && !$$.VIP2.attr("data-numb") == "") {
                //     $$.AGREEMENTID.after(couponType);
                //     $$.AGREEMENTID.after(extCouponId);
                //     $$.AGREEMENTID.after(id);
                //     if (!$(".preferential-hiddle").hasClass("youhuima-hiddle")) {
                //         $$.AGREEMENTID.after(code);
                //     }
                // } else {
                //     $$.AGREEMENTID.after(couponType);
                //     $$.AGREEMENTID.after(extCouponId);
                //     if($(item).attr("data-coupon-type")=="XQ.FIRST"){
                //         $$.AGREEMENTID.after(id);
                //     }
                // }
                j++;
            }
        })

    }
    $$.ORDERAGAINBTN.bind('click', function() {
        if($$.CARTVIPSELECT.hasClass("moni-checkbox-active")){
            youhuiquanChoose();
        }
        var T;
        clearTimeout(T);
        if ($$.PAYTYPE.val() == '') {
            //formFouc();
            alert("请选择一种付款方式");
            return false;
        }
        if($$.CARTVIPSELECT.hasClass("moni-checkbox-active") && $$.VIP2.is(':checked') && $$.CARTINPUT.val()==''){
            alert("请填写优惠码,或是把优惠码前面的勾勾去掉哦~");
            return false;
        }
        if($$.CARTVIPSELECT.hasClass("moni-checkbox-active") && $$.VIP2.is(':checked') && $$.CARTYOUHUIMANAME.hasClass("cart-error-red")){
            alert("请填写正确优惠码,或是把优惠码前面的勾勾去掉哦~");
            return false;
        }
        if($$.CARTVIPSELECT.hasClass("moni-checkbox-active") && $$.VIP2.is(':checked') && $$.VIP2.attr('data-numb')=='' ){
            alert("优惠码要兑换才能使用哦,请先点击兑换按钮，或是把优惠码前面的勾勾去掉哦~");
            return false;
        }
        if($$.PAYCARD.is(":checked") && $("#bankCode").val()==''){
            alert("请选择银行卡~");
            $(".cart-card").addClass("cart-card-lh");
            T = setTimeout(function(){
                $(".cart-card").removeClass("cart-card-lh");
            },3000);
            return false;
        }
        $$.ORDERPAYFORM.submit();
    });
//});