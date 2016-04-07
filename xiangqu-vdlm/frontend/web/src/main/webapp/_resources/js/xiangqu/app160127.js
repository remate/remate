var ua = window.navigator.userAgent.toLowerCase();
if(ua.match(/MicroMessenger/i) == 'micromessenger'){
    function hasApp(config){
        var osrc;
        var browser={
            versions:function(){
                var u = navigator.userAgent, app = navigator.appVersion;
                return {         //移动终端浏览器版本信息
                    ios: !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/), //ios终端
                    android: u.indexOf('Android') > -1 || u.indexOf('Linux') > -1 //android终端或uc浏览器
                };
            }(),
            language:(navigator.browserLanguage || navigator.language).toLowerCase()
        }

        if(browser.versions.android){
            osrc=config.androidSrc;
        }else if(browser.versions.ios){
            osrc=config.iosSrc;
        }

        if(!document.getElementsByClassName){
            document.getElementsByClassName = function(className, element){
                var children = (element || document).getElementsByTagName('*');
                var elements = new Array();
                for (var i=0; i<children.length; i++){
                    var child = children[i];
                    var classNames = child.className.split(' ');
                    for (var j=0; j<classNames.length; j++){
                        if (classNames[j] == className){
                            elements.push(child);
                            break;
                        }
                    }
                }
                return elements;
            };
        }

        var list=document.getElementsByClassName(config.eventObj);
        for(var i=0;i<list.length;i++){
            list[i].onclick = function(e){
                var tout, t = 1000,
                    hasApp = true;
                var t1 = Date.now();
                clearTimeout(tout);
                clearTimeout(tr);
                // 通过iframe的方式试图打开APP，如果能正常打开，会直接切换到APP，并自动阻止a标签的默认行为
                // 否则打开a标签的href链接
                var ifr = document.createElement('iframe');
                ifr.src = osrc;
                ifr.style.display = 'none';
                document.body.appendChild(ifr);

                tout = setTimeout(function() {
                    try_to_open_app(t1);
                },t);
                function try_to_open_app(t1) {
                    var t2 = Date.now();
                    if (!t1 || t2 - t1 < t + 200) {
                        hasApp = false;
                    }
                }
                var tr=setTimeout(function() {
                    if (!hasApp) {
                        window.location=config.downSrc;
                    }
                },2000);
            }
        }
    }


    hasApp({
        iosSrc:'https://itunes.apple.com/cn/app/xiang-qu-she-ji-yu-shou-gong/id575466337?mt=8',
        androidSrc:"http://xqres.xiangqu.com/xiangqu_ouertech.apk",
        eventObj:'hasapp',
        downSrc:"http://m.xiangqu.com/app.html?spm=wap"
    });
}
else{
    function hasApp2(config){
        var osrc;
        var browser={
            versions:function(){
                var u = navigator.userAgent, app = navigator.appVersion;
                return {         //移动终端浏览器版本信息
                    ios: !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/), //ios终端
                    android: u.indexOf('Android') > -1 || u.indexOf('Linux') > -1 //android终端或uc浏览器
                };
            }(),
            language:(navigator.browserLanguage || navigator.language).toLowerCase()
        }

        if(browser.versions.android){
            osrc=config.androidSrc;
        }else if(browser.versions.ios){
            osrc=config.iosSrc;
        }

        if(!document.getElementsByClassName){
            document.getElementsByClassName = function(className, element){
                var children = (element || document).getElementsByTagName('*');
                var elements = new Array();
                for (var i=0; i<children.length; i++){
                    var child = children[i];
                    var classNames = child.className.split(' ');
                    for (var j=0; j<classNames.length; j++){
                        if (classNames[j] == className){
                            elements.push(child);
                            break;
                        }
                    }
                }
                return elements;
            };
        }

        var list=document.getElementsByClassName(config.eventObj);
        for(var i=0;i<list.length;i++){
            list[i].onclick = function(e){
                var tout, t = 1000,
                    hasApp = true;
                var t1 = Date.now();
                clearTimeout(tout);
                clearTimeout(tr);
                // 通过iframe的方式试图打开APP，如果能正常打开，会直接切换到APP，并自动阻止a标签的默认行为
                // 否则打开a标签的href链接
                var ifr = document.createElement('iframe');
                ifr.src = osrc;
                ifr.style.display = 'none';
                document.body.appendChild(ifr);

                tout = setTimeout(function() {
                    try_to_open_app(t1);
                },t);
                function try_to_open_app(t1) {
                    var t2 = Date.now();
                    if (!t1 || t2 - t1 < t + 200) {
                        hasApp = false;
                    }
                }
                var tr=setTimeout(function() {
                    if (!hasApp) {
                        if(browser.versions.android){

                            window.location=config.downSrc;
                        }
                        else if(browser.versions.ios){
                            window.location=config.downIos;
                        }

                    }
                },2000);
            }
        }
    }
    hasApp2({
        iosSrc:'xiangqu://',
        androidSrc:"xiangqu://www.xiangqu.com/app/goto",
        eventObj:'hasapp',
        downSrc:"http://xqres.xiangqu.com/xiangqu_ouertech.apk",
        downIos:"https://itunes.apple.com/cn/app/xiang-qu-she-ji-yu-shou-gong/id575466337?mt=8"
    });
}
