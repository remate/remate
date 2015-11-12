(function($){
	var append={
		init:function(){
			var self=this;
			self.setPage=0;
            self.setSize=10;
            self.isOver=false;
            self.product_ajax(self.setPage,self.setSize);
		},
		//        商品Ajax
        product_ajax:function(intPage,intSize){
            var self=this;
            if(self.isOver==false){
                self.isOver=true;
                $.ajax({
                    url: '/activity/products',
                    type: 'get',
                    data: {
                        activityId: 35,
                        page: intPage,
                        size:intSize
                    },
                    dateType: 'json',
                    beforeSend:function(){
                        $('.xiaoyu').css('display','block');
                    },
                    complete:function(){
                        $('.xiaoyu').css('display','none');
                    },
                    success: function (data) {
                        $('.xiaoyu').css('display','none');
                        self.size=intSize;//10
                        self.intPage=intPage;//0
                        self.productList=data.data;
                        self.dataLength=self.productList.length;
                        if(self.dataLength<self.size){
                            self.li_append(self.dataLength);
                            self.scroll_ajax();
                        }
                        else{
                            self.li_append(self.size);
                            self.isOver=false;
                            self.scroll_ajax();
                        }
                    }
                })
            }
        },
        li_append:function(length){
            var self=this;
            var _html = '';
            for (var i = 0; i < length; i++) {
                var ua = window.navigator.userAgent;
                var u = navigator.userAgent, app = navigator.appVersion;
                var isAndroid = u.indexOf('Android') > -1 || u.indexOf('Linux') > -1; //android终端或者uc浏览器
                var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
                    _html += '....';
            }
            $('ul').append(_html);
            self.like_init(self.intPage*self.size);
            //self.lazyload();
        },
        scroll_ajax:function(){
            var self=this;
            $(window).bind('scroll',function(){
                var bot = 353;
                if ((bot + $(window).scrollTop()) >= ($(document).height() - $(window).height())) {
                    if(self.isOver==true){
                        $(window).unbind('scroll');
                    }
                    else if(self.isOver==false){
                        self.product_ajax(++self.intPage,self.setSize);
                    }
                }
            })

        }
	}
	$(function(){
		append.init();
	})
})(jQuery);
