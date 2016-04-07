var notice_height;

var shopID = $("#shopID").html();
//var startPos = $('.shop-tab').offset().top;
var startPos = startposF();

var initzx = function(){
    //初始化布局 start
    //初始化小图状态（后端返回目前状态）
    var proInfor = $('.pro-infor');
    if( !proInfor || $(proInfor).length == 0 )return;
    var proOuter = $('.img-outer');
    var imgObj= $(".js-img-size");
    var contain = $("#pro-all");
    var marginL;
    if($('body').hasClass('smallimg')){
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
    }else if($('body').hasClass('waterfall')) {
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


    }else if($('body').hasClass('bigimg')){
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
    //初始化布局 end
};

function startposF(){
    if($('.shop-notice').css("height")=="0px"){
        //startPos = $('.shop-tab').offset().top;
        startPos = $('.shop-intro-canvas')[0].offsetHeight;
        return startPos;
    }else{
        startPos = $('.shop-intro-canvas')[0].offsetHeight+$('.shop-notice')[0].offsetHeight;
        return startPos;
    }
}
function isscroll(){
    startPos =startposF();
    var scrolltop = getScrollTop();

    if(parseInt(scrolltop) > parseInt(startPos)){
        $('.shop-tab').css({'position':'fixed','top':0});
    }else{
        $('.shop-tab').css('position','static');
    }
    isTop();
}

function getScrollTop() {
    var scrollPos;
    if (window.pageYOffset) {
        scrollPos = window.pageYOffset; }
    else if (document.compatMode && document.compatMode != 'BackCompat')
    { scrollPos = document.documentElement.scrollTop; }
    else if (document.body) { scrollPos = document.body.scrollTop; }
    return scrollPos;
}

$(document).ready(function() {
    //oAjax("01",0);
    initzx();
    initClick();
    initSelPos();
    notice_height = $('.shop-notice').height();
    isTop();


});
function after(){
    var  inner = document.getElementsByClassName('inner')
    for(var i =0 ; i<inner.length;i++){
        var elem = inner[i]
        var div = document.createElement('div')
        div.innerHTML = 'aaaa'
        elem.parentNode.insertBefore(div,elem.nextSibling)
    }
}
function oAjax(index,cateID){
    var proAll = document.getElementById("allGoods");
    var ajaxURL;
    var dataNum;//总个数
    var offSet =1;
    var T;
    clearTimeout(T);

    if(index == "01"){
        ajaxURL = "/shop/" + shopID + "/recommendProduct";
        dataNum = $(".recommand").attr("data-numb");
        showNotice();
    }else if (index == "02") {
        ajaxURL = "/shop/" + shopID + "/hotSaleProduct";
        hideNotice();
    }else if (index == "03") {
        ajaxURL = "/shop/" + shopID + "/productByPage";
        dataNum = $(".all").attr("data-numb");
        hideNotice();
    }else if(index == 0){//单个类目
        ajaxURL = "/shop/" + shopID + "/category/"+cateID;
        $(".category-list").children().each(function(i,item){
           if($(item).attr("data-cateid")==cateID){
               dataNum = $(item).attr("data-numb");
           }
        });
    }
    var param={
        type : "post",
        size :  "20",
        page : 0
    }
    $.ajax({
        url: ajaxURL,
        data:param,
        success : function(data) {
            $("#pro-all").html(data);
            initzx();
        }
    });
    function addCls(){
        if (Number(dataNum)>20 && !$(".shop_all_btn").length>0) {
            var obj=document.createElement("img");//新建一个div元素节点
            obj.src='/_resources/images/shop/all_btn.png';
            $(obj).addClass("shop_all_btn");
            proAll.appendChild(obj);

        }
    }
    function removeCls(){
        if(offSet== Math.ceil(dataNum/20)-1){
            $(".shop_all_btn").remove();
        }
    }
    if(index == "03"){
    	removeCls();
        addCls();
     
    }else{
        $(".shop_all_btn").remove();
    }
    //T=setTimeout(function(){
        $(".shop_all_btn").bind("click",function () {
            //$(".shop_all_btn").remove();
            if (offSet < Math.ceil(dataNum/20)) {
                var param = {
                    type: "post",
                    size: "20",
                    page: offSet
                }
                $.ajax({
                    url: ajaxURL,
                    url: ajaxURL,
                    data: param,
                    success: function (data) {
                        $("#pro-all").append(data);
                        offSet++;
                        initzx();
                        if (Number(dataNum) == $('.pro-infor').length) {
                            $(".shop_all_btn").remove();
                        }
                    }
                });
            }
        });
    //},1000)

}


function tabheightlight(obj){
    var sel_left = $(obj).offset().left + ($(obj).offset().width - 48) / 2;
    $('.tab-sel').css3Animate({
        x: sel_left,
        time: "300ms"
    })
}
function tabitem() {
    $('.tab-wrap span').click(function () {
        // tab条是否居顶
        var elm = $('.shop-tab');

        $('.tab-wrap span').each(function () {
            $(this).removeClass('sel');
        });
        $(this).addClass('sel');
        tabheightlight(this);
        var index = $(this).attr("index");
        oAjax(index,0);
        startPos =startposF();
        //类目list是否显示
        if ($('#category').css("display") == "block") {
            $('.shop-tab').css({"position": "fixed", "top": '0'});
            hideCategory();
        }

        isscroll();
    })
}

$(window).resize(function() {
    tabheightlight('.sel');
});
function initClick() {
    $('.btn-notice').click(function() {
        if ($('.shop-notice').height() > 0) {
            $('.shop-notice').css3Animate({
                time : '200ms',
                opacity : 0,
                success : function() {
                }
            })
            $('.shop-notice').css("height", "0px");
            $('.shop-notice').css("padding-top", "0px");
            $('.shop-notice').css("padding-bottom", "0px");
        } else {
            $('.shop-notice').css3Animate({
                time : '200ms',
                opacity : 1,
                success : function() {
                }
            })
            $('.shop-notice').css("height", notice_height + "px");
            $('.shop-notice').css("padding-top", "15px");
            $('.shop-notice').css("padding-bottom", "15px");

        }

    })

    $('.category-list').children().each(function (i, item) {
        $(item).click(function (e) {
            //请求类目数据
            var cateID = $(item).attr("data-cateid");
            oAjax(0,cateID);
            $(".tab-wrap").hide();
            $(".tab-category").html($(e.target).text()).show();
            hideCategory();
        })
    })

    tabitem();
    var show_category_flag = false;

    $('#btn-category').click(function(){

        if(!show_category_flag){
            showCategory();
        }else{
            hideCategory();
        }

    })

    $("#btn-category-close").click(function(){
        hideCategory();
    })
    if($(".wechat-id").text()==""){
        $(".btn-wechat").hide();
    }
    $(".btn-wechat").click(function(){
        if(!show_wechat_flag){
            showWechatTip();
        }else{
            hideWechatTip();
        }
    })
}

function initSelPos() {
    /**
     * @recommandNum  推荐栏的商品数
     * 如果商品数为0，则删除tab里面的推荐栏，并默认显示全部
     */
    var recommandNum = parseInt($(".recommand").attr("data-numb"),10);
    if (recommandNum === 0) {
        $(".recommand").remove();
        $(".all").addClass('sel').click();
    };
    $('.tab-wrap span').each(function() {
        if ($(this).hasClass('sel')) {
            var sel_left = $(this).offset().left + ($(this).offset().width - 48) / 2;
            $('.tab-sel').css3Animate({
                x : sel_left,
                time : "0ms"
            })
        }
    })
}


function isTop(){
    if($("#category").css("display")=='none'){
        $(window).bind("scroll", listenTopEvent);
        $('body').bind('touchmove',listenTopEvent);
    }

}

function listenTopEvent(){
    var elm = $('.shop-tab');
    var p = document.body.scrollTop || document.documentElement.scrollTop;
    $(elm).css('position',(parseInt(p) > parseInt(startPos)) ? 'fixed' : 'static');
    $(elm).css('top',(parseInt(p) > parseInt(startPos)) ? '0px' : '');
}

var show_wechat_flag = false;
function showCategory(){
    $('.info-section').css3Animate({
        time:"300ms"
    });
    $('body').unbind('touchmove',listenTopEvent);
    $(window).unbind("scroll",listenTopEvent);
    $('.shop-tab').css({"position":"fixed","top":"0"});

    $("#category").show();
    $("#category").css3Animate({
        time:"300ms"
    })
    $('.category-list').css("height", $(window).height() - $(".shop-tab")[0].offsetHeight - $(".footer-bottom")[0].offsetHeight-$(".category-title")[0].offsetHeight);
    $(".category-home").bind('click', function () {
        $(".tab-category").hide();
        $(".tab-wrap").show();
        hideCategory();
        tabitem();
    })
    /*  var canvas_height = $(window).height() - 50;
     $("#category").css("min-height",canvas_height +"px");*/
    $("#category").css("top","40px");
    show_category_flag = true;
}
function hideCategory(){

    $('.info-section').css3Animate({
        time:"300ms",
        success:function(){
        }
    })
    $('.info-section').css("margin-top","0px");
    $('.shop-tab').css("position","static");
    $("#category").css3Animate({
        time:"300ms"
    })
    $("#category").css("min-height","100px");
    $("#category").css("top","200px");
    $("#category").hide();
    isscroll();
    show_category_flag = false;
}



function showWechatTip(){
    $(".wechat-tip").css3Animate({
        time:"200ms",
        opacity:1
    })
    $(".wechat-tip").css("height","30px");
    show_wechat_flag = true;
}

function hideWechatTip(){
    $(".wechat-tip").css3Animate({
        time:"200ms",
        opacity:0
    })
    $(".wechat-tip").css("height","0px");
    show_wechat_flag = false;
}
function hideNotice(){
    $('.shop-notice').css3Animate({
        time : '200ms',
        opacity : 0,
        success : function() {
        }
    })
    $('.shop-notice').css("height", "0px");
    $('.shop-notice').css("padding-top", "0px");
    $('.shop-notice').css("padding-bottom", "0px");
}

function showNotice(){
    $('.shop-notice').css3Animate({
        time : '200ms',
        opacity : 1,
        success : function() {
        }
    })
    $('.shop-notice').css("height", notice_height + "px");
    $('.shop-notice').css("padding-top", "15px");
    $('.shop-notice').css("padding-bottom", "15px");
}