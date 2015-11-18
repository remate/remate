/**
 * Created by danpike on 2015/11/18.
 */


(function($){
    var wapList={
        init:function(){
            var self=this;
            self.btn1=$('.btn1');
            self.eventHanding();
        },
    eventHanding:function(){
        var self=this;
        self.btn1.click(function(){
            alert(2);
        });
        $(".btn2").click(
            function () {
                require(['index_2']);
            }
        );
    }
    };
    $(function(){
        wapList.init();
    })
})(jQuery);