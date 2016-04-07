define(['jquery', 'base/utils', 'base/set-desc/elements', 'doT', 'upload'], function(jquery, utils, elements, doT, upload) {
    var tpl = $('.tpl-desc-img').html(),
        doTtpl = doT.template(tpl);
    //上传段落描述图片
    upload({
        id: 'J-desc-pop-add'
    }, function(data) {
        var data = JSON.parse(data);
        if (data.data && data.data.length) {
            var key = data.data[0].id,
                url = data.data[0].url,
                str = doTtpl({
                    imgUrl: url,
                    img: key
                });
            elements.popImgAdd.before(str);
            elements.popImgBox.find('label.error').remove();
        }
    }, function(err) {
        utils.tool.alert('上传失败,请稍后重试！');
    });

});