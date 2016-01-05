(function($){
	var ClientWidth = GetClientWidth();//网页可见区域宽
    console.log(ClientWidth);

    var ClientHeight = GetClientHeight();//网页可见区域宽
    console.log(ClientHeight);

    var ScrollTop=GetScrollTop();//页面滚动高度
    console.log(ScrollTop);
//客户端判断
    if(IsApp){
        console.log("isApp");
    }
    else{
        console.log("notApp");
    }
//微信判断
    if(IsWeinXin){
        console.log("isWeinXin");
    }
    else{
        console.log("notWeinXin");
    }
//新浪客户端判断
    if(IsXinLang){
        console.log("isXinLang");
    }
    else{
        console.log("notXinLang");
    }
//iphone系统判断
    if(IsIphone){
        console.log("IsIphone");
    }
    else{
        console.log("notIphone");
    }
//android系统判断
    if(IsAndroid){
        console.log("IsAndroid");
    }
    else{
        console.log("notAndroid");
    }
//嵌入css
    CSSappend("build/test.css");
//嵌入js
    JSappend("build/test.js");
//自定义图片链接配置
    var aaa="http://xqproduct.xiangqu.com/FtdHfHJXjlff-pB0Fv7BPweFbOuw?imageView2/2/w/655/q/100/format/jpg/655x360/";
    $('.ChangeImg').attr('src',ChangeImgSize(aaa,100,100));
//iphone 想去版本号
    console.log(IphoneVersion);
//android 想去版本号
    console.log(AndroidVersion);
})(Zepto);