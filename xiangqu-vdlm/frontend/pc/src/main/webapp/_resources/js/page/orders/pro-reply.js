define(['jquery','base/utils','doT'], function(jquery,utils,doT) {
    //回复商品评论
    var reply = {
        init: function () {
            //请求获取商品评论列表
            var orderId = $('#orderId').val();
            $.ajax({
                url: host + '/comment/list?orderId=' + orderId,
                type: 'POST',
                dataType: 'json',
                success: function(data) {
                    //console.log(data);
                    if (data.data.length) {
                        var tmpl = $('.dot-replyTemplate').html();
                        var doTtmpl = doT.template(tmpl);
                        $('.pannel-reply').html(doTtmpl(data));
                        $('.reply-item .mod-score').each(function(index,em){
                            utils.tool.scoreStarShow({
                                trigger: em,
                                scoreShow: $(em).data('score'),
                                eventClk: false
                            });
                        });
                    }else{
                         $('.pannel-reply').hide();
                    }
                },
                error: function(state) {
                    if (state.status == 401) {
                        utils.tool.goLogin();
                    } else {
                        utils.tool.alert('服务器暂时没有相应，请稍后重试...');
                    }
                }
            });
            
            //评论回复操作
            this.handle();
        },
        handle: function () {
            //回复按钮的伸缩操作
            $(document).on('click','.am-replay',function(){
                var _this = $(this);
                _this.parents('.item-contents').next('.item-reply').toggleClass('am-hide');
                if (!_this.parents('.item-contents').next('.item-reply').hasClass('am-hide')) {//评论输入框显示的时候优化
                    var top = $(window).scrollTop();
                    $('html,body').animate({scrollTop:top+100},100);
                    _this.parents('.reply-item').find('.editArea').focus();
                }
            });
            //提交评论和删除评论的单机事件，
            $(document).on('click','.am-submitReplay',function(){
                var _this = $(this);
                if (_this.hasClass('am-replyed')) {
                    //有评论，执行删除评论操作
                    utils.tool.confirm.call(_this,'确定要删除回复吗？',function($el){
                        var parms = {
                            id: $el.parents('.reply-item').attr('data-id'),
                        }
                        var strUrl = host + '/comment/removeReplay';
                        $.ajax({
                            url: strUrl,
                            type: 'POST',
                            data: parms,
                            cache:false,
                            dataType: 'json',
                            success: function(data) {
                                //console.log(data);
                                //删除评论成功操作
                                if (data.errorCode==200) {
                                    utils.tool.alert('评论删除成功！');
                                    _this.text('提交回复').removeClass('am-replyed');
                                    _this.parents('.reply-item').find('.reply-tip').removeClass('am-hide');
                                    _this.parents('.reply-item').find('.editArea').html('').removeClass('edit-disabeld').attr('contenteditable','true');
                                    _this.parents('.reply-item').find('.item-reply').addClass('am-hide');  
                                }else{
                                    utils.tool.alert(data.moreInfo);
                                }
                                
                            },
                            error: function() {
                                utils.tool.alert('删除评论失败！');
                            }
                        });
                    });

                }else{
                    //无评论，执行提交评论操作
                    var strReply = $.trim(_this.siblings('.editArea').text());
                    var parms = {
                        id: _this.parents('.reply-item').attr('data-id'),
                        reply: strReply
                    }
                    var strUrl = host + '/comment/reply';
                    if (strReply) {
                        $.ajax({
                            url: strUrl,
                            type: 'POST',
                            data: parms,
                            cache:false,
                            dataType: 'json',
                            success: function(data) {
                                //console.log(data);
                                if (data.errorCode==200) {
                                    utils.tool.alert('评论提交成功！');
                                    _this.text('删除回复').addClass('am-replyed');
                                    _this.parents('.reply-item').find('.reply-tip').addClass('am-hide');
                                    _this.parents('.reply-item').find('.editArea').addClass('edit-disabeld').removeAttr('contenteditable'); 
                                }else{
                                    utils.tool.alert(data.moreInfo);
                                }
                                
                            },
                            error: function() {
                                utils.tool.alert('评论提交失败！');
                            }
                        });
                    }else{
                        utils.tool.alert('评论信息不能为空！');
                        setTimeout(function(){
                            _this.siblings('.editArea').focus();
                        },100); 
                    }
                }
            });
        }
    };
    reply.init();

    return null;
});


