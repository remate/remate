

define(['jquery', 'amaze', 'base/utils'], function(jquery, amaze, utils) {
    var $shopName = $('#shop-name'),
        $shopWeixin = $('#shop-weixin'),
        $shopDesc = $('#shop-desc'),
        $shopProv = $('.shop-province'),
        $shopCity = $('.shop-city'),
        $shopBulletin = $('.shop-notice'),
        $shopBannel = $('.shop-background img'),
        $shopImg = $('.shop-img img'),
        $shopDanbao = $('.shop-danbao'),
        $shopTel = $('#shop-mobile'),
        $shopMobileType = $('#shop-mobileType');
    return {
        save: function() {
            //if (!$shopDanbao.find('.danbao-readed').is(':checked') || !$shopDanbao.find('.danbao-open').is(':checked')) {
            if (!$shopDanbao.find('.danbao-readed').is(':checked') ) {
                utils.tool.alert('请阅读并同意担保交易！');
                return false;
            }
            var dataMobile = {
                mobileType: $shopMobileType.val(),
                mobile: $shopTel.val()
            }
            var dataUpdate = {
                name: $shopName.val(),
                wechat: $shopWeixin.val(),
                description: $shopDesc.val(),
                provinceId: $shopProv.val(),
                cityId: $shopCity.val(),
                bulletin: $shopBulletin.val(),
                banner: $shopBannel.attr('data-src'),
                img: $shopImg.attr('data-src'),
                danbao: $shopDanbao.find('.danbao-open').is(':checked'),
            };
            utils.api.editMobile(dataMobile,function(mdata){
                utils.api.updateShop(dataUpdate, function(data) {
                    if (mdata.errorCode == 200) {
                        utils.tool.alert('您的数据提交成功！');
                    }else{
                        utils.tool.alert('联系电话保存失败！');
                    }
                    setTimeout(function(){
                        location.reload();
                    },1000);
                }, function(data) {
                    utils.tool.alert(data);
                });
            });
        }
    };
});

