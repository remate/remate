function imgSize(imgObj,listObj){
    var imgWidth= listObj.width();
    $(imgObj).parent().css({"display":"block","width":imgWidth,"height":imgWidth,"overflow":"hiddle"});
    $.each(imgObj,function(index,val){
       var imgurl = val.src.substr(0);
        var url = imgurl.split("?");
        val.src=url[0]+"?imageView2/1/w/"+imgWidth+"/q/100";
    });

}