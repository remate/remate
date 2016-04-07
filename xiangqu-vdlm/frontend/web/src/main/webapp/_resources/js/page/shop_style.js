//实时响应装修变化
function bigimg(proInfor,proOuter,imgObj){

    proInfor.css({
        "display":"block",
        'width':'95%',
        'float':'none',
        'margin':"0 auto 10px",
        'position':'static'
    });
    proOuter.css({
        'width':'100%',
        'height':'auto'
    });
    imgObj.css({
        'width':'100%',
        'height':'auto',
        'margin':'0'
    });
    //proInfor.css({"margin-left":parseInt((window.screen.availWidth-(window.screen.availWidth*0.95))/2),"margin-bottom":"10px","margin-top":"0","margin-right":"0"});
}

function smallimg(proInfor,proOuter,imgObj){
    proInfor.css({
        'display': 'inline-block',
        'width': "145px",
        'float': 'left',
        'position':'static'
    });
    proOuter.css({
        'width':'145px',
        'height': "145px"
    });
    $(".pro-item-text .widthhuanh").css('width','140px');
    var mL = function () {
            var l =proInfor.length;//产品个数
            var screenW = $(window).width();
            var marginTotal = screenW %145;//总间距
            var objLength = parseInt(screenW/145);//一屏能容纳的个数
            if(objLength>l){

                marginL = Math.round((screenW-145*l)/(l+1));
                return marginL;
            }else{
                marginL = Math.round(marginTotal/(objLength+1));
                return marginL;

            }
    }
    proInfor.css({"margin-left":mL(),"margin-bottom":"10px","margin-top":"0","margin-right":"0"});
    imgcenter(imgObj,145);

}
function waterfall(proInfor,proOuter,imgObj,contain) {

    proInfor.css({
        "display":"block",
        'width':'145px',
        'float':'none',
        'margin':'0'
    })
    proOuter.css({
        'width':'145px',
        'height': 'auto'
    });
    imgObj.each(function(j,obj){
        $(obj).css("width","145px");
    })
    $(".pro-item-text .widthhuanh").css('width','140px');
    $(proInfor).wookmark({
        autoResize: true, // This will auto-update the layout when the browser window is resized.
        container: contain, // Optional, used for some extra CSS styling
        imgobj: imgObj,
        proItemText:'pro-item-text',
        offset:10,
        outerOffset: 10, // Optional, the distance between grid items
        itemWidth: 145 // Optional, the width of a grid item
    });

}
function shopStyle(obj){
    var proInfor = $('.pro-infor');
    var proOuter = $('.img-outer');
    var imgObj= $(".js-img-size");
    var contain= $("#pro-all");
    var allGoods = $("#allGoods");
    $('body')[0].className='';
    $('#init-img').remove();
    for(i in obj){
        if(i !=='imageimg'&& i !=='bgimg'){
            $('body').addClass(obj[i]);
        }
    }
    $(".img-mohu").attr("src",obj["bgimg"]);
    $("#shop_img").attr("src",obj["imageimg"]);

    if(obj['imgstyle']=='smallimg'){
        smallimg(proInfor,proOuter,imgObj);
    }else if(obj['imgstyle']=='bigimg'){
        bigimg(proInfor,proOuter,imgObj);
    }else if(obj['imgstyle']=='waterfall'){
        waterfall(proInfor,proOuter,imgObj,contain);
    }
}
