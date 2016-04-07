var KKKD = KKKD || {};
KKKD.imList = {
    init: function() {
        var that = this;
        /* 获取联系人列表 */
        that.getContacts();
    },
    temp: {
        msgTplHtml: $('#tplMsg').html()
    },
    config: {
        sessionId: '1',
        ver: conf.ver,
        from_user_id: conf.buyerId
    },
    getContacts: function() {
        var that = this;
        var data = $.extend(that.config, {
            type: '0',
            hold: 0
        });
        $.ajax({
            url: conf.cometServer + '/query/getContactLists',
            type: "POST",
            data: JSON.stringify(data),
            dataType: "json",
            success: function(data) {
            	if(data.msgList.length){
            		var doTtmpl_pro = doT.template(that.temp.msgTplHtml);
                    $('.imListWrap').append(doTtmpl_pro(data));
            	}
            }
        });
    }
}

$(function() {
    KKKD.imList.init();
})