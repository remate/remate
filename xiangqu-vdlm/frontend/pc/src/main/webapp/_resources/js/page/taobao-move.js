require(['base/taobao-move/init']);
//第二步的验证
require(['base/taobao-move/validate']);
//复制验证码
require(['jquery','copy','base/utils'],function(jquery,copy,utils){
    copy($('.copycode'),function(){
        utils.tool.alert('复制成功！');
    },function(){
        utils.tool.alert('复制失败！');
    });
});

