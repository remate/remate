//预览商品
define(['jquery','base/utils','qrcode','plugins/slider','jiathis'],function(jquery,utils,qrcode,Slider,jiathis){
    var $proShow = $('.KD-product-show'),
        $proName = $proShow.find('.product-show-name'),
        $proPrice = $proShow.find('.product-price span'),
        $proCode = $proShow.find('.product-code'),
        $sliderBox = $('.sliderBox'),
        $sliderIndex = $('.sliderIndex');
        var slider = new Slider();
    $('body').on('click','[modal-productShow]',function(){
        var id = $(this).attr('modal-productShow');
        utils.api.getProductInfo(id,function(data){
            $proName.text(data.data.name);
            $proPrice.text(data.data.price);
            $proCode.empty();
            qrcode($proCode,{width:145,height:145,url:data.data.productUrl});
            //分享配置
            //微博分享参数配置
            var summary = "商品名称："+data.data.name;
            var proUrl = utils.tool.getAbsUrl('/p/'+data.data.id);
            jiathis_config = {
                title: '快快开店',
                summary: summary,
                url: proUrl,
                pic: data.data.imgs[0] ? data.data.imgs[0].imgUrl : ''
            }
            //渲染图片
            var imgs = data.data.imgs;
            $sliderBox.empty();
            $sliderIndex.empty();
            
            $.each(imgs,function(i,img){
                $sliderBox.append('<img src="'+ img.imgUrl +'" class="am-fl" />'); 
                $sliderIndex.append('<em></em>');
            });
            slider.init($('.product-show-imgs'),{width:390});
            $proShow.modal();
        },function(){
            utils.tool.alert('获取商品数据失败，请稍后重试!');
        });
    });
    //关闭modal
    $('.product-show-close').on('click',function(){
        $proShow.modal('close');
    });
    $proShow.on('close.modal.amui',function(){
        slider.destroy();
    });
    return null;
});