/**
 * Created by danpike on 2015/11/18.
 */
/**
 * Created by danpike on 2015/11/18.
 */


(function($){
    var wapList2={
        init:function(){
            var self=this;
            self.btn3=$('.btn3');
            self.eventHanding();
        },
        eventHanding:function(){
            var self=this;
            self.btn3.click(function(){
                alert(4);
            })
        }
    };
    $(function(){
        wapList2.init();
    })
})(jQuery);