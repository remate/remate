;(function($){
    var lightbox = function(){
        var self = this;
        this.winWidth = $(window).width();
        this.winHeight = $(window).height();
        this.thisa = $('.a');
        this.thisa.css({width:this.winWidth/2,height:this.winHeight/2,left:'50%',marginLeft:-this.winWidth/4}).animate({
            top:20,width:200,height:200
        },1000)
        this.show();
    };
    lightbox.prototype = {
        show:function(){
        }
    };
    window["lightbox"] = lightbox;
    $(function(){
        var Lightbox = new lightbox();
    });
})(jQuery);