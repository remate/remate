var KKKD = KKKD || {};
KKKD.imDetail = {
    init: function() {
        var that = this;
        /* 获取历史聊天记录 */
        if (conf.history) {
            that.getMsgHistory();
        }
        /* 轮询新消息 */
        setTimeout(function(){
        	that.comet();
        },500);
        /* 渲染商品数据 */
        that.renderPro(conf.data);
        /* 渲染订单数据 */
        that.renderOrder(conf.data);
        /* 发送商品链接 */
        that.event_sendLink_pro();
        /* 发送商品链接 */
        that.event_sendLink_order();
        /* 发送消息 */
        that.event_sendMsg();
        /* 点x的操作 */
        that.goback();
    },
    goback : function(){
        if( conf.data.product && conf.data.product.id ){
            $('.imHeader-oper-close').attr('href','/p/'+conf.data.product.id);
        }else if(conf.data.order && conf.data.order.id){
            $('.imHeader-oper-close').attr('href','/order/'+conf.data.order.id);
        }
    },
    temp: {
        proTplHtml: $('#tplProdcut').html(),
        msgTplHtml: $('#tplMsg').html(),
        orderHtml : $('#tplOrder').html()
    },
    config: {
        sessionId: '1',
        ver: conf.ver,
        to_user_id: conf.sellerId,
        from_user_id: conf.buyerId,
        msg_seq: 0
    },
    conf_msg: {
        MESSAGE_IMAGE_LINK_START: "&$#@~^@[{:",
        MESSAGE_IMAGE_LINK_END: ":}]&$~@#@",
        MESSAGE_PRODUCT_START: "&$#@product~^@[{:",
        MESSAGE_PRODUCT_END: ":}]&$product~@#@",
        MESSAGE_ORDER_START: "&$#@order~^@[{:",
        MESSAGE_ORDER_END: ":}]&$order~@#@"
    },
    getMsgHistory: function() {
        var that = this;
        var data = $.extend(that.config, {
            type: '0',
            hold: 0
        });
        $.ajax({
            url: conf.cometServer + '/query/recvMsg',
            type: "POST",
            data: JSON.stringify(data),
            dataType: "json",
            success: function(data) {
                console.log(data);
                that.renderMsg(data);
            }
        });
    },
    comet: function() {
        var that = this;
        var data = $.extend(that.config, {
            type: '1',
            hold: 1
        });
        $.ajax({
            url: conf.cometServer + '/query/recvMsg',
            type: "POST",
            data: JSON.stringify(data),
            dataType: "json",
            success: function(data) {
                console.log('收到信息 ');
                that.renderMsg(data);
            },
            complete: function() {
            	if( !conf.debug ){
            		that.comet();
            	}
            }
        });
    },
    sendMsg: function(str, type, cb) {
        var that = this;
        var str = that.parseInfo(str, type);
        var data = $.extend(that.config, {
            content: str,
            hold: 0,
            msg_seq: that.config.msg_seq
        });
        $.ajax({
            type: 'POST',
            url: conf.cometServer + '/query/sendMsg',
            data: JSON.stringify(data),
            dataType: 'json',
            success: function(res) {
                if (res.result == 200) {
                    that.config.sessionId = res.imsid ||  that.config.sessionId;
                    cb && cb(str);
                    that.config.msg_seq++;
                } else {
                    alert('消息发送失败，请稍后重试');
                }
            },
            error: function(res) {
                alert('消息发送失败，请稍后重试~');
            },
            complete: function() {

            }
        });
    },
    parseInfo: function(str, type) {
        var that = this,
            str = str;
        switch (type) {
            case 0:
                str = str;
                break;
            case 1:
                str = that.conf_msg.MESSAGE_PRODUCT_START + str + that.conf_msg.MESSAGE_PRODUCT_END;
                break;
            case 2:
                str = that.conf_msg.MESSAGE_ORDER_START + str + that.conf_msg.MESSAGE_ORDER_END;
                break;
            case 3:
                //暂时web不支持发表情
                break;
            default:
                str = str;
        }
        return str;
    },
    parseMsg: function(str) {
        var that = this;
        var res;
        if (str.indexOf(that.conf_msg.MESSAGE_IMAGE_LINK_START) >= 0 && str.indexOf(that.conf_msg.MESSAGE_IMAGE_LINK_END) >= 0) {
            //图片消息占位符  图片还需要再处理
            res = str.split(that.conf_msg.MESSAGE_IMAGE_LINK_START)[1].split(that.conf_msg.MESSAGE_IMAGE_LINK_END)[0];
            return res;
        }
        if (str.indexOf(that.conf_msg.MESSAGE_PRODUCT_START) >= 0 && str.indexOf(that.conf_msg.MESSAGE_PRODUCT_END) >= 0) {
            //商品消息占位符
            res = str.split(that.conf_msg.MESSAGE_PRODUCT_START)[1].split(that.conf_msg.MESSAGE_PRODUCT_END)[0];
            res = JSON.parse(res);
            return '<a href="' + res.productUrl + '" target="_self">' + that.getabsoluteurl(res.productUrl) + '</a>';
        }
        if (str.indexOf(that.conf_msg.MESSAGE_ORDER_START) >= 0 && str.indexOf(that.conf_msg.MESSAGE_ORDER_END) >= 0) {
            //订单消息占位符 
            res = str.split(that.conf_msg.MESSAGE_ORDER_START)[1].split(that.conf_msg.MESSAGE_ORDER_END)[0];
            res = JSON.parse(res);
            return '<a href="/order/' + res.id + '" target="_self">' + that.getabsoluteurl("/order/" + res.id) + '</a>';
        }
        return str;
    },
    getabsoluteurl: function(url) {
        var img = new Image();
        img.src = url; // 设置相对路径给image, 此时会发送出请求
        url = img.src; // 此时相对路径已经变成绝对路径
        img.src = null; // 取消请求
        return url;
    },
    renderPro: function(data) {
        var that = this;
        var doTtmpl_pro = doT.template(that.temp.proTplHtml);
        $('#scroller').append(doTtmpl_pro(data));
    },
    renderOrder : function(data){
    	var that = this;
        var doTtmpl_order = doT.template(that.temp.orderHtml);
        $('#scroller').append(doTtmpl_order(data));
    },
    renderMsg: function(data) {
        var that = this;
        var doTtmpl_msg = doT.template(that.temp.msgTplHtml);
        $('#scroller').append(doTtmpl_msg(data));
        $('#imMain')[0].scrollTop = $('#imMain')[0].scrollHeight;
    },
    event_sendLink_pro: function() {
        var that = this;
        $('body').on('click', '.js-sendProLink', function() {
            var content = $(this).parents('.imProduct').find('.imProduct-img').attr('href');
            var str = JSON.stringify(conf.data.product);
            that.sendMsg(str, 1, function() {
                var msg = new Msger(0, new Date().toString().match(/\d+\:\d+\:\d+/)[0], '<a href="' + content + '" target="_self">' + that.getabsoluteurl(content) + '</a>');
                msg.send();
            });
        })
    },
    event_sendLink_order: function() {
        var that = this;
        $('body').on('click', '.js-sendOrderLink', function() {
            var content = '/order/' + conf.data.order.orderId;
            var str = JSON.stringify(conf.data.order);
            that.sendMsg(str, 2, function() {
                var msg = new Msger(0, new Date().toString().match(/\d+\:\d+\:\d+/)[0], '<a href="' + content + '" target="_self">' + that.getabsoluteurl(content) + '</a>');
                msg.send();
            });
        })
    },
    event_sendMsg: function() {
        var that = this;
        var sendBtn = $('.imFooter-send'),
            sendIpt = $('.imFooter-ipt');
        sendBtn.on('click', function() {
            var vals = sendIpt.val();
            if (vals == '') {
                return;
            }
            that.sendMsg(vals, 0, function() {
                var msg = new Msger(0, new Date().toString().match(/\d+\:\d+\:\d+/)[0], vals);
                msg.send();
                sendIpt.val('');
            });
        });
        sendIpt.on('keydown', function(ev) {
            var ev = ev || event;
            if (ev.keyCode == 13) {
                sendBtn.trigger('click');
                sendIpt.blur();
            }
        });
    }
}

/**
 * [Msger description]
 * @param  {[type]} type [0 自己发的  1:对面发的]
 * @param  {[type]} time [description]
 * @param  {[type]} info [description]
 * @return {[type]}      [description]
 */
function Msger(type, time, info) {
    this.type = type;
    this.time = time;
    this.info = info;
}
Msger.prototype.send = function() {
    var data = {
        "msgList": [{
            "createTime": this.time,
            "from_id": conf.buyerId,
            "msgData": this.info,
            "msgType": 1,
            "to_id": conf.sellerId
        }]
    };
    KKKD.imDetail.renderMsg(data);
}

$(function() {
    KKKD.imDetail.init();
})