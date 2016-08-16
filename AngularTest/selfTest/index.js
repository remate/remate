;(function($){
    $.fn.zuoci = function(options) {
        var _default ={
            x:'120px',
            y:'240px'
        }
        var final = $.extend({},_default,options);
        console.log(final)
        $(this).animate({height:final.y,width:final.x},2000);
    }
})(jQuery);