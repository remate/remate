function imgSize(imgObj,listObj){
	if( !listObj || $(listObj).length == 0 )return;
    var outerWidth= parseInt($(listObj)[0].clientWidth) - 15;
    $(imgObj).parent().css({"display":"block","width":outerWidth,"height":outerWidth,"overflow":"hiddle"});
    $.each(imgObj,function(index,val){
        var oldWidth = parseInt($(val).attr("imgwidth"));
        var oldHeight = parseInt($(val).attr("imgheight"));
        if(oldHeight > oldWidth){
            $(val).css('width',outerWidth);
            var newHeight = parseInt(outerWidth * oldHeight / oldWidth);
            $(val).css('height',newHeight);
            $(val).css({'margin-top':parseInt(-(newHeight - outerWidth)/2)});
        }else if(oldWidth > oldHeight ){
            $(val).css('height',outerWidth);
            var newWidth = parseInt(outerWidth * oldWidth / oldHeight);
            $(val).css('width',newWidth);
            $(val).css({'margin-left':parseInt(-(newWidth - outerWidth)/2)});
        }else if(oldWidth == oldHeight && oldWidth > outerWidth){
            $(val).css({'width':outerWidth,'height':outerWidth});
        }
    });

}