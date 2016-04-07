/**
 * Created by ziyu on 2016/1/26.
 */

var _weixinLoginInfo = {};

function getWeixinData(callBack){
    var _data = $('#order_submit_form').serializeArray();

    $.ajax({
        url:'/xiangqu/wap/order/submit/wx',
        type:'get',
        data:_data,
        dataType:'json',
        success:function(rs){

            if(callBack){
                callBack(rs);
            }
        }
    })
}



function getUrlParam(name) {
    //构造一个含有目标参数的正则表达式对象
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    //匹配目标参数
    var r = window.location.search.substr(1).match(reg);
    //返回参数值
    if (r != null){
        return unescape(r[2]);
    }

    return null;
}

function onBridgeReady() {
    var appId = _weixinLoginInfo.payInfo.appId;
    var timeStamp = _weixinLoginInfo.payInfo.timeStamp;
    var nonceStr = _weixinLoginInfo.payInfo.nonceStr;
    var Package = _weixinLoginInfo.payInfo.package;
    var signType = _weixinLoginInfo.payInfo.signType;
    var paySign = _weixinLoginInfo.payInfo.paySign;
    var signature = _weixinLoginInfo.payInfo.signature;
    //WeixinJSBridge.invoke('getBrandWCPayRequest', {
    //    "appId" : appId, //公众号名称，由商户传入
    //    "timeStamp" : timeStamp, //时间戳，自1970年以来的秒数
    //    "nonceStr" : nonceStr, //随机串
    //    "package" : Package,//"prepay_id=u802345jgfjsdfgsdg888",
    //    "signType" : signType,//"MD5", //微信签名方式:
    //    "paySign" : paySign,//"70EA570631E4BB79628FBCA90534C63FF7FADD89" //微信签名
    //}, function(res) { // 使用以上方式判断前端返回,微信团队郑重提示：res.err_msg将在用户支付成功后返回    ok，但并不保证它绝对可靠。
    //    //alert(res.err_msg);
    //    if (res.err_msg == "get_brand_wcpay_request:ok") {
    //        alert("支付成功");
    //    }
    //    if (res.err_msg == "get_brand_wcpay_request:cancel") {
    //        alert("交易取消");
    //    }
    //    if (res.err_msg == "get_brand_wcpay_request:fail") {
    //        alert("支付失败");
    //    }
    //});

    wx.config({
        debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
        appId: appId, // 必填，公众号的唯一标识
        timestamp: timeStamp, // 必填，生成签名的时间戳
        nonceStr: nonceStr, // 必填，生成签名的随机串
        signature: signature,// 必填，签名，见附录1
        jsApiList: [
            'checkJsApi',
            'chooseWXPay'] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
    });

    wx.error(function(res){

    });

    wx.checkJsApi({
       jsApiList:['chooseWXPay'],
        success:function(res){

        }
    });

    wx.ready(function(){
        wx.chooseWXPay({
            timestamp: timeStamp, // 支付签名时间戳，注意微信jssdk中的所有使用timestamp字段均为小写。但最新版的支付后台生成签名使用的timeStamp字段名需大写其中的S字符
            nonceStr: nonceStr, // 支付签名随机串，不长于 32 位
            package: Package, // 统一支付接口返回的prepay_id参数值，提交格式如：prepay_id=***）
            signType: signType, // 签名方式，默认为'SHA1'，使用新版支付需传入'MD5'
            paySign: paySign, // 支付签名
            success: function (res) {
                // 支付成功后的回调函数
                //WeixinJSBridge.log(res.err_msg);
                //alert("支付接口:"+res.err_code + res.err_desc + res.err_msg);
                if(!res.err_msg){
                    //支付完后.跳转到成功页面.
                    //alert('成功')
                    //alert(JSON.stringify(res));
                    //location.href="orderconfirm?orderId=${StringUtil.wrapString(requestAttributes.out_trade_no)!}";
                    location.href='http://' + location.host +  _weixinLoginInfo.callBackUrl;
                }
            }
        });
    });
}

function callPay() {
    if (typeof WeixinJSBridge == "undefined") {
        if (document.addEventListener) {
            document.addEventListener('WeixinJSBridgeReady', onBridgeReady,
                false);
        } else if (document.attachEvent) {
            document.attachEvent('WeixinJSBridgeReady', onBridgeReady);
            document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
        }
    } else {
        onBridgeReady();
    }
}