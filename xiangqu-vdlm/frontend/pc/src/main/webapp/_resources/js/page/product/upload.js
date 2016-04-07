define(['jquery', 'base/utils', 'doT', 'upload'], function(jquery, utils, doT, upload) {
    var tpl = $('.tpl-desc-img').html(),
        doTtpl = doT.template(tpl);
    //上传商品图片
    upload({
        id: 'J-product-img-add'
    }, function(data) {
        var data = JSON.parse(data);
        if (data.data && data.data.length) {
            var key = data.data[0].id,
                url = data.data[0].url,
                str = doTtpl({
                    imgUrl: url,
                    img: key
                });
            $('.product-imgs .desc-pop-imgs-box .desc-pop-add').before(str);
            $('.product-imgs .desc-pop-imgs-box').find('label.error').remove();
        }
    }, function(err) {
        utils.tool.alert('上传失败,请稍后重试！');
    });
});
