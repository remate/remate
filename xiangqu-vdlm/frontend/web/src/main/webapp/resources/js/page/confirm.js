//define(function(require) {
var $ = require('jquery');
//var $ = require('jquery/1.11.1/jquery.js');
require('../module/num.js');
//require('../module/select.js');
//require('../module/addressEditor.js');
require('../plugins/tab.js');
require('../module/cardBank.js');

$(document).ready(function () {
    var $$ = {
        ADDRESSSHOW: $('.address-show'),
        MONICHECKBOX: $('.moni-checkbox'),
        ADDRESSEDITOR: $(".addrss-editor"),//编辑地址按钮
        CARDTABITEM: $('.card-tab-item a'),
        PAYCARD: $('#pay-card'),
        PAYZHIFUBAO: $('#pay-zhifubao'),
        CARTCARD: $(".cart-card"),
        CARTVIPLIST: $('.cart-vip-list'),
        ADDRESSMODIFY: $(".address-modify"),
        TABLEABLE: $('.table-able'),
        TOTALFEEID: $("#totalFee"),//实付款
        TOTALFEE: $("#cart-total-price").find('span'),//订单总额
        ADDRESSBTN: $('#addressBtn'),
        ADDRESSID: $("#addressId"),
        ORDERBTN: $('#order-btn'),
        PAYTYPE: $('#payType'),
        ORDERPAYFORM: $('#id-cart'),
        CONFIRMTR: $('.confirm-tr'),
        cartcheckout: $('#vip2choose'),
        cartVIPBTN: $('#cart-vip-btn'),
        cartcheckoutVIP3LIST: $('.cart-vip3-list'),
        CARTYOUHUIMANAME: $("#cart-youhuima-name"),
        J_couponsItems : $('.J_couponsItems'),
        CARTVIPINPUT: $(".cart-vip-input"),
        SKUS: $("#skus"),
        CARTVIPSELECT: $(".cart-vip-select").find(".moni-checkbox"),
        VIP1: $("#vip1"),
        VIP2: $("#vip2"),
        VIPYOUHUI: $("#vip-youhui"),//减免多少
        AGREEMENTID: $("#payAgreementId"),
        CARTINPUT: $(".cart-input")
    };
    /*   $$.MONICHECKBOX.bind('click',function(){
     $$.CARTVIPLIST.toggle();
     })*/

    //编辑收货地址
    $$.ADDRESSEDITOR.bind('click', function () {
        $$.ADDRESSMODIFY.show();
        $$.ADDRESSSHOW.hide();
        $$.ADDRESSID.val('');
    })
    $$.CARDTABITEM.click(function (e) {
        e.preventDefault();
        $(this).tab('show');
    })
    //字数限制
    function number(obj, numb) {
        obj.each(function (i, item) {
            if ($(item).text().length > numb) {
                $(item).text($(item).text().substring(0, numb) + "...");
            }
        })
    }

    //产品标题2行限制
    number($('.J-number'), 20);
    //sku2行限制
    number($('.confirm-sku'), 24);
    //qty参数修改
    if (!request('qty')) {
        $('#qty').val('0');
    } else {
        var amount = parseInt(request('qty'));
        $('#qty').val(amount);
    }
    /* 获取URL参数 */
    function request(paras) {
        var url = location.href;
        var paraString = url.substring(url.indexOf("?") + 1, url.length).split(/\&|\#/g);
        var paraObj = {}
        for (i = 0; j = paraString[i]; i++) {
            paraObj[j.substring(0, j.indexOf("=")).toLowerCase()] = j.substring(j.indexOf("=") + 1, j.length);
        }
        var returnValue = paraObj[paras.toLowerCase()];
        if (typeof(returnValue) == "undefined") {
            return "";
        } else {
            return returnValue;
        }
    }

    function getShopPriceJson(param) {
        $.ajax({
            url: '/cart/pricing-groupby-shop',
            data: param,
            traditional: true,
            success: function (data) {
                if (data.errorCode == 200) {
                    $$.TABLEABLE.each(function (i, item) {
                        var shopid = $(item).attr('data-shop-id');
                        $(item).next().find(".totalprice").text(Number(data.data.pricesMap[shopid].totalFee).toFixed(2));
                        $(item).next().find(".footer-youfei-price").text(Number(data.data.pricesMap[shopid].logisticsFee).toFixed(2));
                    })
                    $$.TOTALFEE.text(Number(data.data.totalFee).toFixed(2));
                    $$.TOTALFEEID.html('￥' + Number(data.data.totalFee).toFixed(2));
                    vipPrice();
                } else {
                    alert(data.moreInfo);
                }
            }
        });
    }

    //初始优惠码状态，选择框是否显示
    function vipHongbao() {
        if ($$.cartcheckout.is(':checked')) {
            $$.cartcheckoutVIP3LIST.show();
            vipPrice(); //计算优惠总金额
        } else {
            $$.cartcheckoutVIP3LIST.hide();
            if ($$.VIP1.is(':checked')) {
                $$.TOTALFEEID.text("￥" + Math.max(((Number($$.TOTALFEE.text().replace(/\￥|\,/g, '') * 100) - Number($$.VIP1.attr('data-numb') * 100)) / 100).toFixed(2), 0.00));
                //$$.VIPYOUHUI.text(Math.min(Number($$.VIP1.attr('data-numb')).toFixed(2)),Number($$.TOTALFEE.text().replace('￥','').replace(',','')));
                $$.VIPYOUHUI.text(Math.min(Number($$.VIP1.attr('data-numb')).toFixed(2), Number($$.TOTALFEE.text().replace(/\￥|\,/g, '')).toFixed(2)));
            } else {
                $$.TOTALFEEID.text("￥" + Math.max(Number($$.TOTALFEE.text().replace(/\￥|\,/g, '')).toFixed(2), 0.00).toFixed(2));
                vipPrice();
            }
        }
    }

    //VIP2
    $$.cartcheckout.bind('click', function () {
        vipHongbao();
    });
    $$.cartVIPBTN.bind('click', function () {
        var self = $(this);
        var param = {
            code: self.prev().val()
        };
        if (!self.prev().val()) {
            alert('优惠券号码不能为空！');
            self.prev().focus();
            return false;
        }
        $.ajax({
            url: '/coupon/detail',
            data: param,
            traditional: true,
            success: function (data) {
                if (data.errorCode == 200) {
                    $$.CARTINPUT.name = "preferential";
                    $$.VIP2.attr("data-extCouponId", data.data.extCouponId);
                    $$.VIP2.attr("data-id", data.data.id);
                    $$.CARTYOUHUIMANAME.text("获得   " + data.data.discount + '  元红包');
                    $$.CARTYOUHUIMANAME.prev().attr("data-numb", data.data.discount);

                    checkboxPrice();
                    vipPrice();
                    if ($$.CARTYOUHUIMANAME.hasClass("cart-error-red")) {
                        $$.CARTYOUHUIMANAME.removeClass("cart-error-red");
                    }
                } else {
                    $$.CARTYOUHUIMANAME.text(data.moreInfo);
                    $$.VIP2.attr("data-numb", "");
                    checkboxPrice();
                    vipPrice();
                    if (!$$.CARTYOUHUIMANAME.hasClass("cart-error-red")) {
                        $$.CARTYOUHUIMANAME.addClass("cart-error-red");
                    }
                }
            }
        });
    });
    //已选择的优惠价格和
    function checkboxPrice() {
        var n = 0;
        $(".cart-vip-input:checked").each(function (i, item) {
            n = n + Number($(item).attr('data-numb'));
            //n = n + Number($(item).attr('numb'));
        });
        //console.log(n);
        return n;
    }

    //计算优惠金额
    function vipPrice() {
        if ($('.moni-checkbox').hasClass('moni-checkbox-active')) {
            //$$.VIPYOUHUI.text(Math.min(Number(checkboxPrice()).toFixed(2), Number($$.TOTALFEE.text().replace(/\￥|\,/g, '')).toFixed(2)).toFixed(2));
            $$.VIPYOUHUI.html(Math.min(Number(checkboxPrice()).toFixed(2), Number($$.TOTALFEE.html().replace(/￥|\,/g, '')).toFixed(2)).toFixed(2));
        }
        //if ((Number($$.TOTALFEE.text().replace(/\￥|\,/g, '')) - Number($$.VIPYOUHUI.text())).toFixed(2) > 0) {

        if ((Number($$.TOTALFEE.html().replace(/￥|\,/g, '')) - Number($$.VIPYOUHUI.html())).toFixed(2) > 0) {
            //$$.TOTALFEEID.text("￥" + Math.max(((Number($$.TOTALFEE.text().replace(/\￥|\,/g, '')) * 100 - Number($$.VIPYOUHUI.text()) * 100) / 100).toFixed(2), 0.00).toFixed(2));

            $$.TOTALFEEID.html("￥" + Math.max(((Number($$.TOTALFEE.html().replace(/￥|\,/g, '')) * 100 - Number($$.VIPYOUHUI.html()) * 100) / 100).toFixed(2), 0.00).toFixed(2));
        } else {
            //$$.TOTALFEEID.text("￥" + 0.00);
            $$.TOTALFEEID.html("￥" + 0.00);
        }
    }

    $$.CARTVIPSELECT.bind('click', function () {
        var moniCheckbox = $(".moni-checkbox");
        if ($$.CARTVIPSELECT.hasClass('moni-checkbox-active')) {
            moniCheckbox.removeClass("moni-checkbox-active");
            $$.CARTVIPLIST.hide();
            //$$.VIPYOUHUI.text("0.00");
            $$.VIPYOUHUI.html("0.00");
            //$$.TOTALFEEID.text("￥" + Number($$.TOTALFEE.text().replace(/\￥|\,/g, '')).toFixed(2));
            $$.TOTALFEEID.html("￥" + Number($$.TOTALFEE.html().replace(/￥|\,/g, '')).toFixed(2));
        } else {
            moniCheckbox.addClass("moni-checkbox-active");
            //$$.VIPYOUHUI.text(Math.min(Number(checkboxPrice()).toFixed(2), Number($$.TOTALFEE.text().replace(/\￥|\,/g, '')).toFixed(2)).toFixed(2));
            $$.VIPYOUHUI.html(Math.min(Number(checkboxPrice()).toFixed(2), Number($$.TOTALFEE.html().replace(/￥|\,/g, '')).toFixed(2)).toFixed(2));
            //$$.TOTALFEEID.text("￥" + Math.max(Number($$.TOTALFEE.text().replace(/\￥|\,/g, '')).toFixed(2), 0.00));
            $$.TOTALFEEID.html("￥" + Math.max(Number($$.TOTALFEE.html().replace(/￥|\,/g, '')).toFixed(2), 0.00));
            $$.CARTVIPLIST.show();
            vipPrice(); //计算优惠总金额
            vipHongbao(); //初始优惠码状态，选择框是否显示
        }
    })
    //优惠券是否勾选
    //var lenChecked = $('input.cart-vip-input:checked').length;
    //if (lenChecked) {
    //  $$.CARTVIPSELECT.click();
    //}

    //默认显示优惠券
    $$.CARTVIPSELECT.click();

    $$.CARTVIPINPUT.bind('click', function () {
        vipPrice();
    })
    //地址变更
    var t;
    clearTimeout(t);
    $$.ADDRESSBTN.bind('click', function () {
        t = setTimeout(function () {
            var Id = $$.ADDRESSID.val();
            var skuid = [];
            var param;
            $$.CONFIRMTR.each(function (i, item) {
                skuid.push($(item).attr('data-sku-id'));
                var zoneid = $("#zoneId").val();
                param = {
                    zoneId: zoneid,
                    skuIds: skuid,
                    qty: $('#qty').val()
                };
            })
            getShopPriceJson(param);
        }, 100)
    })

    function youhuiquanChoose() {
        var j = 0;
        $$.J_couponsItems.find('.cart-vip-input').each(function (i, item) {
            if ($(item).is(':checked')) {
                var couponType = "<input class='preferential-hiddle' type='hidden' name='coupon" + "[" + j + "]." + "activityId' " + " value='" + $(item).attr('data-coupon-type') + "'>";
                var extCouponId = "<input class='preferential-hiddle' type='hidden' name='coupon" + "[" + j + "]." + "extCouponId' " + " value='" + $(item).attr('data-extcouponid') + "'>";
                var id = "<input class='preferential-hiddle' type='hidden' name='coupon" + "[" + j + "]." + "id' " + " value='" + $(item).attr('data-id') + "'>";
                var code = "<input class='preferential-hiddle youhuima-hiddle' type='hidden' name='coupon" + "[" + j + "]." + "code' " + " value='" + $('#youhuimaInput').val() + "'>";

                var discount="<input class='preferential-hiddle' type='hidden' name='coupon["+j+"].discount'  value='"+ $(item).attr("data-numb")+"'>";

                $$.AGREEMENTID.after(couponType);
                $$.AGREEMENTID.after(extCouponId);
                $$.AGREEMENTID.after(id);
                $$.AGREEMENTID.after(discount);

                // if ($(item).hasClass("cart-vip2-input") && !$$.VIP2.attr("data-numb") == "") {
                //   /*    $$.AGREEMENTID.after(couponType);
                //       $$.AGREEMENTID.after(extCouponId);*/
                //   $$.AGREEMENTID.after(id);
                //   if (!$(".preferential-hiddle").hasClass("youhuima-hiddle")) {
                //     $$.AGREEMENTID.after(code);
                //   }
                // } else {
                //   /*  $$.AGREEMENTID.after(couponType);
                //     $$.AGREEMENTID.after(extCouponId);*/
                //   if ($(item).attr("data-coupon-type") == "XQ.FIRST") {
                //     $$.AGREEMENTID.after(id);
                //   }
                // }
                j++;
            }
        })
    }

    $$.ORDERBTN.bind('click', function (e) {
        e.preventDefault();
        var T, checkSubmitFlg = false;
        clearTimeout(T);
        //传入danbao addressId payType payAgreementId （协议支付id） cardType bankCode
        if ($$.ADDRESSID.length == 0 || $$.ADDRESSID.val() == '') {
            alert('亲，地址不能为空，请添加地址！');
            $("html,body").animate({
                scrollTop: $(".cart-address").offset().top
            }, 100);
            $("#province").focus();
            return false;
        }
        if ($$.PAYTYPE.val() == '') {
            //formFouc();
            alert("请选择一种付款方式");
            return false;
        }
        if ($$.CARTVIPSELECT.hasClass("moni-checkbox-active") && $$.cartcheckout.is(':checked')) {
            if ($$.VIP2.is(':checked') && $$.CARTINPUT.val() == '') {
                alert("请填写优惠码,或是把优惠码前面的勾勾去掉哦~");
                return false;
            }
            if ($$.VIP2.is(':checked') && $$.CARTYOUHUIMANAME.hasClass("cart-error-red")) {
                alert("请填写正确优惠码,或是把优惠码前面的勾勾去掉哦~");
                return false;
            }
            if ($$.VIP2.is(':checked') && $$.VIP2.attr('data-numb') == '') {
                alert("优惠码要兑换才能使用哦,请先点击兑换按钮，或是把优惠码前面的勾勾去掉哦~");
                return false;
            }
        }
        if ($$.PAYCARD.is(":checked") && $("#bankCode").val() == '') {
            alert("请选择银行卡~");
            $(".cart-card").addClass("cart-card-lh");
            T = setTimeout(function () {
                $(".cart-card").removeClass("cart-card-lh");
            }, 3000);
            return false;
        }
        if ($$.CARTVIPSELECT.hasClass("moni-checkbox-active")) {
            youhuiquanChoose();
        }
        if (checkSubmitFlg == true) {
            return false; //当表单被提交过一次后checkSubmitFlg将变为true,根据判断将无法进行提交。
        }
        checkSubmitFlg == true;
        //$("#id-cart").attr("action","/xiangqu/web/order/submit");
        $$.ORDERPAYFORM.submit();
    });

    function payShow() {
        if ($$.PAYCARD.is(':checked')) {
            $$.CARTCARD.show();
        } else {
            $$.CARTCARD.hide();
        }
    }

    payShow();
    $(".cart-pay-item").bind('click', function () {
        payShow();
        $$.PAYTYPE.val($(this).attr('data-pay-type'));
        $$.AGREEMENTID.val($(this).attr('data-agreementid'));
    })

    function hbIsShow() {
        var hongbaoList = $(".vart-hongbao-list");
        if (hongbaoList.attr("data-coupon-valid") == 'false') {
            hongbaoList.addClass('disabled');
            hongbaoList.children('input').attr('disabled', true);
            hongbaoList.children('.cart-msg').removeClass('fn-hiddle');
        }
    }

    hbIsShow();

});


//});