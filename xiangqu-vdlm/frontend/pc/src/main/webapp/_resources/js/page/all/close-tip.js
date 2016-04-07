define(['jquery','base/utils'],function(){
    $(window).on('beforeunload.pro',function(){
        return '是否确定离开此页面？';
    });
    return null;
});