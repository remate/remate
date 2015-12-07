//zuoci 
(function ($) {
    var finalStr,hasAppend;
	$.fn.appendLi = function(options,bb){
		var config={
            tagNum:12,//可以选择的tag数目
            tagId:38,//接口的开始tag id
            everyTag_productNum:10,//每个li中的商品数目
            ul_className:'.J_product_ul',//ul的类名
            spm:'10.2.',//每次活动的商品的spm
            procuct_href:"$('body').attr('data-product')+zc.productList[k][p].products[0].productId+finalStr+k+p",//body中埋点商品链接开头
            hasFav:"zc.productList[k][p].products[0].hasFav",//添加是否为喜欢的class
            product_id:"zc.productList[k][p].products[0].productId",//取商品id
            favNum:"zc.productList[k][p].products[0].faverNum > 0 ? zc.productList[k][p].products[0].faverNum : 0",//喜欢数
            imgSrc:"zc.productList[k][p].products[0].imgUrl",//商品图片的连接
            product_title:"zc.productList[k][p].products[0].title",//商品的标题
            icon_common:false,//每个tag的商品icon是否为相同的判断
            icon_commonText:"common",//为相同时显示的内容
            icon_differentText:['双鱼价','水瓶价','摩羯价','射手价','天蝎价','天秤价','处女价','狮子价','巨蟹价','双子价','金牛价','白羊价'],//为不同时将显示内容组成数组
            countPrice:"zc.productList[k][p].products[0].price",//打折后的价格
            originalPrice:"zc.productList[k][p].products[0].originalPrice"//原价
		};
	$.extend(config,options);
    var ua = window.navigator.userAgent;
        if(ua.indexOf('xiangqu') > -1){
                finalStr='&isApp=1&spm='+config.spm;
            }
            else{
                finalStr='&spm='+config.spm;
            }
	this.each(function () {
        var appendZC={
            init:function(){
                var zc=this;
                zc.startAjax=true;
                zc.scroll=true;
                zc.tf=[false,false,false,false,false,false,false,false,false,false,false,false];
                zc.tags=[];
                zc.finaltag=[];
                zc.bijiao=[false,false,false,false,false,false,false,false,false,false,false,false];
                zc.appendFirst();
                zc.appendSecond();
            },
            appendFirst:function(){
                var zc=this;
                zc.append_html = '';
            for (var b = 0; b < config.everyTag_productNum; b++) {
                zc.append_html += '<li class="product_li">' +
                    '<a class="product_a" href="">' +
                    '<div class="product_pic">' +
                    '<div class="click_more J_likeMore">' +
                    '<div class="like_wrap">' +
                    '<div class="likeWrap_bgOut"><span class="likeWrap_bg notFavor"></span></div>' +
                    '<span class="likeWrap_num"></span>' +
                    '</div></div>' +
                    '<img class="product_img lazyLoad"/>' +
                    '</div>' +
                    '<div class="introduce_main">' +
                    '<h1 class="introduce_title"></h1>' +
                    '<div class="introduce_price">' +
                    '<div class="introduce_priceBody">' +
                    '<div class="price_wrap price_wrap_1 price_wrap_icon"><span class="introduce_icon_1"></span></div>' +
                    '<div class="price_wrap price_wrap_now"><span class="introduce_nowPrice"><i class="i_1"></i><i class="i_2"></i></span></div>' +
                    '<div class="price_wrap_original"><span class="introduce_originalPrice"><i class="i_3"></i><i class="i_4"></i></span></div>' +
                    '</div>' +
                    '</div>' +
                    '</div>' +
                    '</a></li>';
            }
            for(var a=config.tagId;a<(config.tagId+config.tagNum);a++){
                $(config.ul_className+a).append(zc.append_html);
            }
            hasAppend=true;
            zc.ul_height=$(config.ul_className+config.tagId).height();
            zc.setHeight=1200;
            if(zc.ul_height<110){
                zc.setHeight=1150;
            }
            else{
                zc.setHeight=zc.ul_height;
            }
            //alert(config.procuct_href)
            },
            appendSecond:function(){
                var zc=this;
                if(hasAppend){
                     $(window).bind('scroll',function(){
                    if(zc.startAjax) {
                        zc.startAjax = false;
                        if (zc.scroll) {
                            zc.scroll = false;
                            for (var c = 0; c < config.tagNum; c++) {
                                if ($('.img_star' + c).offset().top - zc.setHeight < $(window).scrollTop() && $('.img_star' + c).offset().top + zc.setHeight > $(window).scrollTop()) {
                                    zc.tf[c] = true;
                                    zc.tags = [];
                                    zc.finaltag = [];
                                }
                            }
                            zc.a = true;
                            if (zc.a) {
                                for (var d = 0; d < 12; d++) {
                                    if (zc.tf[d] == true && zc.bijiao[d] == false) {
                                        zc.tags.push(d);
                                        zc.finaltag.push(d + 38);
                                    }
                                }
                                zc.b = true;
                                if (zc.b) {
                                    zc.tf = [false, false, false, false, false, false, false, false, false, false, false, false];
                                    if (zc.tags.length > 0) {
                                        //console.log(1);//长度大于0////
                                        $.ajax({
                                            url: '/activity/tag/tagProducts',
                                            type: 'get',
                                            data: {
                                                activityId: 46,
                                                tags: zc.finaltag,
                                                tag2: 50,
                                                size: 10
                                            },
                                            dateType: 'json',
                                            success: function (data) {
                                                zc.productList=data.data;
                                                for (var e = 0; e < zc.tags.length; e++) {
                                                    zc.bijiao[zc.tags[e]] = true;
                                                }
                                                for(var n=0;n<zc.finaltag.length;n++){
                                                    var k=zc.finaltag[n];
                                                    for(var p=0;p<10;p++){
                                                        //var procuct_href=''+$('body').attr('data-product')+zc.productList[k][p].products[0].productId+finalStr+k+p+'';
                                                        zc.select=$(config.ul_className+k).find(".product_a");
                                                        zc.select.eq(p).attr('href',eval('(' + config.procuct_href + ')'));
                                                        zc.select.find(".J_likeMore").eq(p).addClass(""+eval('(' + config.hasFav + ')')+"").attr("data-productId",eval('(' + config.product_id + ')'));
                                                        zc.select.find('.likeWrap_num').eq(p).html(""+eval('(' + config.favNum + ')')+"");
                                                        zc.select.find('.product_img').eq(p).attr('src',zc.changeImgSize(eval('(' + config.imgSrc + ')'),200,75));
                                                        zc.select.find('.introduce_title').eq(p).html(""+eval('(' + config.product_title + ')')+"");
                                                        zc.select.find('.introduce_icon_1').eq(p).addClass('introduce_icon').html(config.icon_common==true?config.icon_commonText:config.icon_differentText[k-config.tagId]);
                                                        zc.select.find('.i_1').eq(p).html('￥');
                                                        zc.select.find('.i_2').eq(p).html(""+eval('('+config.countPrice+')')+"");
                                                        zc.select.find('.i_3').eq(p).html('原价:');
                                                        zc.select.find('.i_4').eq(p).html(""+eval('('+config.originalPrice+')')+"");
                                                        zc.like_init(k,p);
                                                    }
                                                }
                                                zc.startAjax = true;
                                                zc.scroll = true;
                                                zc.a = false;
                                                zc.b=false;

                                            }
                                        })
                                    }
                                    else {
                                        //console.log("长度为0");////
                                        zc.startAjax = true;
                                        zc.scroll = true;
                                        zc.a = false;
                                        zc.b=false;
                                    }
                                }
                            }
                        }
                    }
                });
            }
            },
            like_init:function(k,p) {//点赞初始化
                if ($(config.ul_className+k).find('.J_likeMore').eq(p).hasClass('true')) {
                    $(config.ul_className+k).find('.J_likeMore').eq(p).find('.likeWrap_bg').removeClass("notFavor").addClass("favor");
                }
                else {
                    $(config.ul_className+k).find('.J_likeMore').eq(p).find('.likeWrap_bg').removeClass("favor").addClass("notFavor");
                }
            },
            changeImgSize:function(k,width,quality){//转换商品图片大小函数
                var zc=this;
                var strSelect=k.indexOf("?");
                zc.originalUrl=k.substring(0,strSelect);
                return(zc.originalUrl + '?imageView2/2' + '/w/' + width+ '/q/' + quality + '/format/jpg');
            }
        };
         appendZC.init();
		});
       
}
})(Zepto);