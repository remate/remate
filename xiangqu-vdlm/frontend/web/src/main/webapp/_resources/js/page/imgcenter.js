function imgcenter(imgObj,objWidth){
    if( !imgObj || $(imgObj).length == 0 )return;
    $.each(imgObj,function(index,val){
        var imgWidth = parseInt($(val).attr("imgwidth"));
        var imgHeight = parseInt($(val).attr("imgheight"));
        if((imgHeight - imgWidth)>1){
            var newWidth = parseInt(imgWidth*objWidth/imgHeight);
            $(val).css({'height':objWidth,'width':newWidth});
            $(val).css({'margin-left':parseInt((objWidth - newWidth)/2)});
        }else if((imgWidth - imgHeight)>1 ){
            var newHeight = parseInt(imgHeight*objWidth/imgWidth);
            $(val).css({'height':newHeight,'width':objWidth});
            $(val).css({'margin-top':parseInt((objWidth - newHeight)/2)});
        }else if(imgWidth == imgHeight){
            $(val).css({'width':objWidth,'height':objWidth});
        }
    });

}