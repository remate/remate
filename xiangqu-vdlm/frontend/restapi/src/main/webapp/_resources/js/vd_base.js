$.wxShare = function(title, desc, img, link, appid) {
    function shareFriend() {
        WeixinJSBridge.invoke('sendAppMessage', {
            "appid" : appid,
            "img_url" : img,
            "img_width" : "200",
            "img_height" : "200",
            "link" : link,
            "desc" : desc,
            "title" : title
        }, function(res) {
            // _report('send_msg', res.err_msg);
        });
    }
    function shareTimeline() {
        WeixinJSBridge.invoke('shareTimeline', {
            "img_url" : img,
            "img_width" : "200",
            "img_height" : "200",
            "link" : link,
            "desc" : desc,
            "title" : title
        }, function(res) {
            // _report('timeline', res.err_msg);
        });
    }
    function shareWeibo() {
        WeixinJSBridge.invoke('shareWeibo', {
            "img_url" : img,
            "link" : link,
            "desc" : desc,
            "title" : title
        }, function(res) {
            // _report('weibo', res.err_msg);
        });
    }

    document.addEventListener('WeixinJSBridgeReady', function onBridgeReady() {
        WeixinJSBridge.on('menu:share:appmessage', function(argv) {
            shareFriend();
        });
        WeixinJSBridge.on('menu:share:timeline', function(argv) {
            shareTimeline();
        });
        WeixinJSBridge.on('menu:share:weibo', function(argv) {
            shareWeibo();
        });
    }, false);
};
$(document).ready( function() {
    $('.numInc,.numDec').click(function() {
        var self = $(this), numed = self.parent().children('.numed'), 
        	delta = self.hasClass('numInc') ? 1 : -1, edge = self.attr('edge'), oldVal = parseInt(numed.val());
        oldVal = isNaN(oldVal) ? 0 : oldVal;
        var newVal = oldVal + delta;
        if (!!edge && (delta > 0 && newVal > edge || delta < 0 && newVal < edge)) {
        	newVal = edge;
        }
        if (oldVal != newVal) {
            numed.val(newVal).text(newVal).trigger('change');
        }
        return false;
    });
    
    $('#backButton').click(function() {
        history.back();
        return false;
    });

    var obj= $(".footer-bottom");
    var objs= obj.siblings();

    if( typeof(obj) !== "undefined"){
        $(objs.get(objs.length-1)).addClass('pb90');
    }

});