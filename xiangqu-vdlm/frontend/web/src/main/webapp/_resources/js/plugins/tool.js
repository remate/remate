var vd_tool = {};

var __ios = navigator.userAgent.match(/iPhone|iPad|iPod/i);

if(!__ios){
    $('.js-hrefs').each(function(){
        //var hs = '/p/' + $(this).attr('data-href');
        var hs = 'http://www.xiangqu.com/dtl/' + $(this).attr('data-href') + '.html';
        $(this).attr('href',hs);
    });
}

/**
 * [获取ua信息]
 */
vd_tool.browser = function() {
    var userAgentInfo = navigator.userAgent;
    var w = window,
        ver = w.opera ? (opera.version().replace(/\d$/, "") - 0) : parseFloat((/(?:IE |fox\/|ome\/|ion\/)(\d+\.\d)/.exec(userAgentInfo) || [, 0])[1]);

    var Agents = ["Android", "iPhone", "SymbianOS", "Windows Phone", "iPad", "iPod"];
    var isMobile = false;
    for (var v = 0; v < Agents.length; v++) {
        if (userAgentInfo.indexOf(Agents[v]) > 0) {
            isMobile = Agents[v];
            break;
        }
    }
    return {
        //测试是否为ie或内核为trident，是则取得其版本号
        ie: !!w.VBArray && Math.max(document.documentMode || 0, ver), //内核trident
        //测试是否为firefox，是则取得其版本号
        firefox: !!w.netscape && ver, //内核Gecko
        //测试是否为opera，是则取得其版本号
        opera: !!w.opera && ver, //内核 Presto 9.5为Kestrel 10为Carakan
        //测试是否为chrome，是则取得其版本号
        chrome: !!w.chrome && ver, //内核V8
        //测试是否为safari，是则取得其版本号
        safari: /apple/i.test(navigator.vendor) && ver, // 内核 WebCore
        mobile: isMobile
    }
}
/**
 * 格式化货币
 * @param  {[Number]} num [传入的价格]
 * @return {[String]}     [1,111.00]
 */
vd_tool.formatCurrency = function(num) {
    num = num.toString().replace(/\$|\,/g, '');
    if (isNaN(num))
        num = "0";
    sign = (num == (num = Math.abs(num)));
    num = Math.floor(num * 100 + 0.50000000001);
    cents = num % 100;
    num = Math.floor(num / 100).toString();
    if (cents < 10)
        cents = "0" + cents;
    for (var i = 0; i < Math.floor((num.length - (1 + i)) / 3); i++)
        num = num.substring(0, num.length - (4 * i + 3)) + ',' +
        num.substring(num.length - (4 * i + 3));
    return (((sign) ? '' : '-') + num + '.' + cents);
}

/**
 * [运动框架 完善af运动框架的不足]
 * @param  {[type]}   obj   [description]
 * @param  {[type]}   json  [description]
 * @param  {[type]}   times [description]
 * @param  {[type]}   fx    [description]
 * @param  {Function} fn    [description]
 * @return {[type]}         [description]
 */
vd_tool.move = function(obj, json, times, fx, fn) {
    var startTime = now();
    var iCur = {};
    for (var attr in json) {

        iCur[attr] = 0;
        if (attr == "opacity") {

            iCur[attr] = Math.round(getStyle(obj, attr) * 100);
        } else {

            iCur[attr] = parseInt(getStyle(obj, attr));
        }

    }
    clearInterval(obj.timer);
    obj.timer = setInterval(function() {
        var changeTime = now();

        var scale = 1 - Math.max(0, startTime - changeTime + times) / times;



        for (var attr in json) {

            var cvalue = parseInt(json[attr]) - iCur[attr];

            var value = Tween[fx](scale * times, iCur[attr], cvalue, times);


            if (attr == "opacity") {

                obj.style.filter = "alpha(opacity=" + value + ")";
                obj.style.opacity = value / 100;

            } else {

                obj.style[attr] = value + "px";

            }

        }
        if (scale == 1) {
            clearInterval(obj.timer);
            if (fn) {
                fn.call(obj);
            }
        }


    }, 13);

    //取得当前时间      
    function now() {
        return (new Date()).getTime();
    }

    //获得样式值     
    function getStyle(obj, sty) {
        if (obj.currentStyle) {

            return obj.currentStyle[sty];

        } else {

            return window.getComputedStyle(obj, false)[sty];

        }

    }

    //运动公式     
    var Tween = {

        //t : 当前时间   b : 初始值  c : 变化值   d : 总时间
        //return : 当前的位置 

        linear: function(t, b, c, d) { //匀速
            return c * t / d + b;
        },
        easeIn: function(t, b, c, d) { //加速曲线
            return c * (t /= d) * t + b;
        },
        easeOut: function(t, b, c, d) { //减速曲线
            return -c * (t /= d) * (t - 2) + b;
        },
        easeBoth: function(t, b, c, d) { //加速减速曲线
            if ((t /= d / 2) < 1) {
                return c / 2 * t * t + b;
            }
            return -c / 2 * ((--t) * (t - 2) - 1) + b;
        },
        easeInStrong: function(t, b, c, d) { //加加速曲线
            return c * (t /= d) * t * t * t + b;
        },
        easeOutStrong: function(t, b, c, d) { //减减速曲线
            return -c * ((t = t / d - 1) * t * t * t - 1) + b;
        },
        easeBothStrong: function(t, b, c, d) { //加加速减减速曲线
            if ((t /= d / 2) < 1) {
                return c / 2 * t * t * t * t + b;
            }
            return -c / 2 * ((t -= 2) * t * t * t - 2) + b;
        },
        elasticIn: function(t, b, c, d, a, p) { //正弦衰减曲线（弹动渐入）
            if (t === 0) {
                return b;
            }
            if ((t /= d) == 1) {
                return b + c;
            }
            if (!p) {
                p = d * 0.3;
            }
            if (!a || a < Math.abs(c)) {
                a = c;
                var s = p / 4;
            } else {
                var s = p / (2 * Math.PI) * Math.asin(c / a);
            }
            return -(a * Math.pow(2, 10 * (t -= 1)) * Math.sin((t * d - s) * (2 * Math.PI) / p)) + b;
        },
        elasticOut: function(t, b, c, d, a, p) { //正弦增强曲线（弹动渐出）
            if (t === 0) {
                return b;
            }
            if ((t /= d) == 1) {
                return b + c;
            }
            if (!p) {
                p = d * 0.3;
            }
            if (!a || a < Math.abs(c)) {
                a = c;
                var s = p / 4;
            } else {
                var s = p / (2 * Math.PI) * Math.asin(c / a);
            }
            return a * Math.pow(2, -10 * t) * Math.sin((t * d - s) * (2 * Math.PI) / p) + c + b;
        },
        elasticBoth: function(t, b, c, d, a, p) {
            if (t === 0) {
                return b;
            }
            if ((t /= d / 2) == 2) {
                return b + c;
            }
            if (!p) {
                p = d * (0.3 * 1.5);
            }
            if (!a || a < Math.abs(c)) {
                a = c;
                var s = p / 4;
            } else {
                var s = p / (2 * Math.PI) * Math.asin(c / a);
            }
            if (t < 1) {
                return -0.5 * (a * Math.pow(2, 10 * (t -= 1)) *
                    Math.sin((t * d - s) * (2 * Math.PI) / p)) + b;
            }
            return a * Math.pow(2, -10 * (t -= 1)) *
                Math.sin((t * d - s) * (2 * Math.PI) / p) * 0.5 + c + b;
        },
        backIn: function(t, b, c, d, s) { //回退加速（回退渐入）
            if (typeof s == 'undefined') {
                s = 1.70158;
            }
            return c * (t /= d) * t * ((s + 1) * t - s) + b;
        },
        backOut: function(t, b, c, d, s) {
            if (typeof s == 'undefined') {
                s = 3.70158; //回缩的距离
            }
            return c * ((t = t / d - 1) * t * ((s + 1) * t + s) + 1) + b;
        },
        backBoth: function(t, b, c, d, s) {
            if (typeof s == 'undefined') {
                s = 1.70158;
            }
            if ((t /= d / 2) < 1) {
                return c / 2 * (t * t * (((s *= (1.525)) + 1) * t - s)) + b;
            }
            return c / 2 * ((t -= 2) * t * (((s *= (1.525)) + 1) * t + s) + 2) + b;
        },
        bounceIn: function(t, b, c, d) { //弹球减振（弹球渐出）
            return c - Tween['bounceOut'](d - t, 0, c, d) + b;
        },
        bounceOut: function(t, b, c, d) {
            if ((t /= d) < (1 / 2.75)) {
                return c * (7.5625 * t * t) + b;
            } else if (t < (2 / 2.75)) {
                return c * (7.5625 * (t -= (1.5 / 2.75)) * t + 0.75) + b;
            } else if (t < (2.5 / 2.75)) {
                return c * (7.5625 * (t -= (2.25 / 2.75)) * t + 0.9375) + b;
            }
            return c * (7.5625 * (t -= (2.625 / 2.75)) * t + 0.984375) + b;
        },
        bounceBoth: function(t, b, c, d) {
            if (t < d / 2) {
                return Tween['bounceIn'](t * 2, 0, c, d) * 0.5 + b;
            }
            return Tween['bounceOut'](t * 2 - d, 0, c, d) * 0.5 + c * 0.5 + b;
        }
    }

}