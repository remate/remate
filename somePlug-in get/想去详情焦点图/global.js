/**
 * Created by Think-ziyu on 2015/7/19.
 */

(function($){
	var wapGlobl={
		init:function(){
			var self = this;

			self.J_returnTop = $('.J_returnTop');
			self.eventHarding();
		},
		eventHarding:function(){
			var self = this;

			$(window).on('scroll',function(){
				//显示返回顶部
				if($(window).scrollTop() > 370){
					self.J_returnTop.show();
				}else{
					self.J_returnTop.hide();
				}
			});

			//返回顶部
			self.J_returnTop.on('click',function(e){
				e.preventDefault();
				self.scrollTop('0',200);

			});
		},
		scrollTop:function(scrollTo,time){
			var self = this;
			var scrollFrom = parseInt(document.body.scrollTop),
				i = 0,
				runEvery = 5;
			scrollTo = parseInt(scrollTo);
			time /= runEvery;

			var interval = setInterval(function(){
				i++;
				document.body.scrollTop = (scrollTo - scrollFrom)/time * i + scrollFrom;
				if(i >= time){
					clearInterval(interval);
				}
			},runEvery);
		}
	};

	$(function(){
		new FastClick(document.body);
		wapGlobl.init();
	});

})(Zepto);
