//上传图片
define(['jquery', 'base/utils', 'upload'], function(jquery, utils, upload) {
    //上传店招图片
    upload({
        id: 'J-banner-upload'
    }, function(data) {
        var data = JSON.parse(data);
        if (data.data && data.data.length) {
            var key = data.data[0].id,
                url = data.data[0].url;
            $('.shop-background img').attr('src', url).attr('data-src', key);
        }
    }, function(err) {
        utils.tool.alert('上传失败,请稍后重试！');
        console.log(err);
    });
    //上传头像
    upload({
        id: 'J-img-upload'
    }, function(data) {
        var data = JSON.parse(data);
        if (data.data && data.data.length) {
            var key = data.data[0].id,
                url = data.data[0].url;
            $('.shop-img img').attr('src', url).attr('data-src', key);
        }
    }, function(err) {
        utils.tool.alert('上传失败,请稍后重试！');
        console.log(err);
    });
});
