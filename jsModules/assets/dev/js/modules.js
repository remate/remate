/**
 * Created by rematedanpike on 15/12/29.
 */
function GetClientWidth(){//网页可见区域宽
    return document.body.clientWidth;
}
function GetClientHeight(){//网页可见区域宽
    return document.body.clientHeight;
}
function GetScrollTop(){//页面滚动高度
    return document.body.scrollTop;
}
//客户端判断
        var IsApp,IsAppFunction=function() {
            var GetUa = window.navigator.userAgent.toLowerCase();
            var UaIsApp = GetUa.indexOf('xiangqu');
            if (UaIsApp > -1) {
                IsApp = true;
            }
            else {
                IsApp = false;
            }
        };
        IsAppFunction();
//微信判断
        var IsWeinXin,IsWeinXinFunction=function(){
            var GetUa=window.navigator.userAgent.toLowerCase();
            var UaIsWeinXin=GetUa.indexOf('micromessenger');
            if(UaIsWeinXin>-1){
                IsWeinXin=true;
            }
            else{
                IsWeinXin=false;
            }
        };
        IsWeinXinFunction();
//新浪客户端判断
        var IsXinLang,IsXinLangFunction=function(){
            var GetUa=window.navigator.userAgent.toLowerCase();
            var UaIsXinLang=GetUa.indexOf('weibo');
            if(UaIsXinLang>-1){
                IsXinLang=true;
            }
            else{
                IsXinLang=false;
            }
        };
        IsXinLangFunction();
//iphone系统判断
var IsIphone,IsIphoneFunction=function(){
        var GetUa=window.navigator.userAgent.toLowerCase();
        if(GetUa.indexOf('iphone') > -1 || GetUa.indexOf('ipad') > -1 || GetUa.indexOf('mac') > -1){
            IsIphone=true;
    }
    else{
            IsIphone=false;
        }
};
        IsIphoneFunction();
//android系统判断
var IsAndroid,IsAndroidFunction=function(){
    var GetUa=window.navigator.userAgent.toLowerCase();
    if(GetUa.indexOf('android') > -1 || GetUa.indexOf('linux') > -1){
        IsAndroid=true;
    }
    else{
        IsAndroid=false;
    }
};
IsAndroidFunction();
//嵌入css
function CSSappend(url){
        var CSSlink = document.createElement('link');
        CSSlink.rel = 'stylesheet';
        CSSlink.type = 'text/css';
        CSSlink.href = url;
        document.getElementsByTagName('head')[0].appendChild(CSSlink);
}
//嵌入js
function JSappend(url){
    var JSlink = document.createElement('script');
    JSlink.type = 'text/javascript';
    JSlink.src = url;
    document.getElementsByTagName('body')[0].appendChild(JSlink);
}
//自定义图片链接配置
function ChangeImgSize(url,width,quality){
    var ChangeImgSizeSingal=url.indexOf("?");
    var ChangeImgSizeOriginalUrl=url.substring(0,ChangeImgSizeSingal);
    return(ChangeImgSizeOriginalUrl + '?imageView2/2' + '/w/' + width+ '/q/' + quality + '/format/jpg');
}
//iphone 想去版本号
var IphoneVersion,IphoneVersionFunction=function(){
    var IphoneVersionGetUa=window.navigator.userAgent;
    IphoneVersion=parseInt(IphoneVersionGetUa.substring(IphoneVersionGetUa.indexOf("V")+2,IphoneVersionGetUa.indexOf("V")+6));
};
IphoneVersionFunction();
//android 想去版本号
var AndroidVersion,AndroidVersionFunction=function(){
    var AndroidVersionGetUa=window.navigator.userAgent;
    AndroidVersion=parseInt(AndroidVersionGetUa.substring(AndroidVersionGetUa.indexOf("channel")-12,AndroidVersionGetUa.indexOf("channel")-8));
};
AndroidVersionFunction();
//


