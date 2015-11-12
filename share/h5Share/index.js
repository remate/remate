(function(){
	var share={
		init:function(){
			var self=this;
			self.shareContent();
			self.shareData();
		},
		shareContent:function(obj){
			var self=this;
                if(obj){
                    obj = obj
                }else{
                    obj = {
                        "imgUrl":"http://www.xiangqu.com/images/core/logo.png",
                        "title":"想去网-买的起的好设计！",
                        "content":"想去-买的起的好设计！",
                        "targetUrl":"wwww.xiangqu.com"
                    }
                }

                window._bd_share_config={
                    "common":{
                        "bdSnsKey":{},
                        "bdText":obj.title,
                        "bdDesc":obj.content,
                        "bdUrl":obj.targetUrl,
                        "bdPic":obj.imgUrl,
                        "bdStyle":"0",
                        "bdSize":"32",
                        "bdCustomStyle":" "
                    },
                    "share":{},
                    "image":{"viewList": ['sqq','weixin','tsina','qzone'],"viewText":"分享到：","viewSize":"32"},
                    "selectShare":{"bdContainerClass":null,"bdSelectMiniList":["sqq","weixin","tsina","qzone"]}}
                with(document)0[(getElementsByTagName('head')[0]||body).appendChild(createElement('script')).src='http://bdimg.share.baidu.com/static/api/js/share.js?v=89860593.js?cdnversion='+~(-new Date()/36e5)];
		},
		shareData:function(){
			var self=this;
			self.shareData = {
                "imgUrl":"http://xqproduct.xiangqu.com/FpXWbdatmAD82pOmipO9dh6yDKQn?imageView2/2/w/300/q/95/format/jpg/@w/$w$@/@h/$h$@/300x300/",
                "title":"我已在想去领取50元现金红包，你也快来领取吧！",
                "content":"想去双11，有你不孤单。",
                "targetUrl":'http://'+window.location.host+'/activity/11-11/share/${userId}?v34'
            };
            self.shareContent(self.shareData);
		}
	}
	
	$(function(){
		share.init();
	})
})(jQuery);




            
