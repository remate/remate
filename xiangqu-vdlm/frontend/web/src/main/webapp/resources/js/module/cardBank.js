//define(function(require) {
    var $ = require('jquery');
    //var $ = require('jquery/1.11.1/jquery.js');
    $(".card-bank-item").bind('click',function(){
        $(this).siblings().removeClass("card-bank-active");
        $(this).addClass("card-bank-active");
        $("#payType").val($(this).attr('data-pay-type'));
        $("#cardType").val($(this).attr('data-card-type'));
        $("#bankCode").val($(this).attr('data-bank-code'));
        $("#paymentChannel").val($(this).attr('data-payment-channel'));
        if($(this).attr("data-agreement")=="true"){
            $("#payAgreementId").val($(this).attr('data-agreement'));
        }
    });
    var showAll = function () {
        $("#creditCardHotTab").css('display', 'none');
        $("#creditCardAllTab").css('display', 'block');
        $("#showAllBtn").css('display', 'none');
    }
//});