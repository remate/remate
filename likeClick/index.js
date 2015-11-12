(function($){
	var likeClick={
		init:function(){
			var self=this;
			self.click();
		},
		click:function(){
			var self=this;
			$('section').click(function(event){
				event.preventDefault();
				alert(1)
			})
		}
	};
	$(function(){
		likeClick.init();
	})
})(jQuery);
