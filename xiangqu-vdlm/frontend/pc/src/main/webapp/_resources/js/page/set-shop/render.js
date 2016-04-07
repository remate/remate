//渲染店铺数据
define(['jquery', 'amaze', 'base/utils', 'address'], function(jquery, amaze, utils, address) {
    var $shopName = $('#shop-name'),
        $shopWeixin = $('#shop-weixin'),
        $shopDesc = $('#shop-desc'),
        $shopProv = $('select.shop-province'),
        $shopCity = $('select.shop-city'),
        $shopBulletin = $('.shop-notice'),
        $shopBannel = $('.shop-background img'),
        $shopImg = $('.shop-img img'),
        $shopDanbao = $('.shop-danbao'),
        $shopTel = $('#shop-mobile'),
        $shopMobileType = $('#shop-mobileType');
        //$shopProCate = $('.product-category');


    utils.api.getShopInfo(window.shopId, function(data) {
        $shopName.val(data.data.name);
        $shopTel.val(data.data.mobile).attr({'data-num1':data.data.mobile,'data-num2':''});
        $shopMobileType.val(data.data.mobileType);
        $shopMobileType.trigger("chosen:updated.chosen");   //联系方式选择
        $shopWeixin.val(data.data.wechat);
        $shopDesc.val(data.data.description);
        $shopBulletin.val(data.data.bulletin);
        if(data.data.banner){
            $shopBannel.attr('src', data.data.bannerUrl).attr('data-src', data.data.banner);
        }
        if( data.data.imgUrl ){
            $shopImg.attr('src',data.data.imgUrl).attr('data-src',data.data.img);
        }
        var addr = new address();
        addr.init(2, $shopProv, $shopCity, null, {
            province: data.data.provinceId,
            city: data.data.cityId
        });

        //如果是担保交易的 －》 复选框选中，开启按钮显示并开启状态，
        //如果不是担保交易的 －》 复选框未选中，开启按钮不显示
        if (data.data.danbao) {
            $shopDanbao.find('.danbao-readed').iCheck('check');
            $shopDanbao.find('.danbao-open').attr('checked', true).show().trigger('change');
           // $shopDanbao.find('.danbao-open').parents('.bootstrap-switch').css('display', 'inline-block');
            $shopDanbao.find('.danbao-open').parents('.bootstrap-switch').hide();
        }
        window.canSubmit = true;
    });
    //担保交易复选框change的事件，checked -> switch show , unchecked -> switch  hide
    $shopDanbao.find('.danbao-readed').on('change', function() {
        var _this = this;
        if ($(this).is(':checked')) {
            $shopDanbao.find('.danbao-open').parents('.bootstrap-switch').css('display', 'inline-block');
        } else {
            $shopDanbao.find('.danbao-open').parents('.bootstrap-switch').css('display', 'none');
        }
    });

    $('#shop-mobileType').on('change',function(){
        var iptTel = $('#shop-mobile');
        iptTel.toggleClass('active');
        if (iptTel.hasClass('active')) {
            iptTel.attr('data-num1',iptTel.val());
            iptTel.val(iptTel.attr('data-num2'));
        }else{
            iptTel.attr('data-num2',iptTel.val());
            iptTel.val(iptTel.attr('data-num1'));
        }
        iptTel.focus();
    });
});