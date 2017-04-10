(function($){
	var append={
		init:function(){
			var self=this;
			self.setPage=0;
            //self.setSize=15;
            self.id = "id";
            self.isOver=false;
            self.product_ajax(self.setPage,self.id);
            self.scroll_ajax();
		},
		//        商品Ajax
        product_ajax:function(intPage,intId){
            var self=this;
            if(!self.isOver){
                self.isOver=true;
                $.ajax({
                    url: 'test.json',
                    type: 'get',
                    data: {
                        type:'cost',
                        page: intPage,
                        code:intId,
                    },
                    dateType: 'json',
                    success: function (data) {
                        self.intPage=intPage;//0
                        self.dataLength=data.length;
                        if(self.dataLength<15){
                            self.li_append(self.dataLength);
                            self.isOver=true;
                            $('.btn').hide();
                        }
                        else{
                            self.intPage++;
                            console.log(self.intPage)
                            self.li_append(15);
                            self.isOver=false;
                        }
                    }
                })
            }
        },
        li_append:function(length){
            var _html = '';
            for (var i = 0; i < length; i++) {
                    _html += '<li></li>';
            }
            $('ul').append(_html);
        },
        scroll_ajax:function(){
            var self=this;
            $('.btn').click(function(){
                self.product_ajax(self.intPage,self.id);
            })

        }
	}
	$(function(){
		append.init();
	})
})(jQuery);
