(function($){
	var likeClick={
		init:function(){
			var self=this;
			self.click();
		},
		click:function(){
			var self=this;
			$('section.like_wrap').click(function(event){
				event.preventDefault();
			});
			$('.like_wrap').click(function(){
				var that=$(this);
				if(that.find(".img_icon").hasClass('like_img')){
					that.find(".img_icon").removeClass("like_img").addClass("notLike_img");
				}
				else{
					that.find(".img_icon").removeClass("notLike_img").addClass("like_img");
				}
			})
		}
	};
	$(function(){
		likeClick.init();
	})
})(jQuery);
