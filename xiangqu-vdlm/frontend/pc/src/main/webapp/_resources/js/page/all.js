

require(['jquery', 'amaze', 'underscore', 'switchs', 'placeholder', 'plugins/browser'], function(jquery, amaze, underscore, switchs, place, browser) {
    if (browser.ie & browser.ie < 8) {
        //检测浏览器
    } else if (browser.ie == 8) {
        $('input[type=text],textarea').placeholder();
    }
});

//登出
require(['jquery', 'base/utils'], function(jquery, utils) {
    $('#J_logout').on('click', function() {
        utils.tool.confirm('确认退出当前账户？', function() {
            utils.api.logout();
        });
    });
});

//顶部导航点击按钮事件
require(['jquery'], function() {
    $('.am-nav button').on('click', function() {
        location.href = $(this).attr('data-href');
    });
});

//预览商品
require(['base/all/product-show']);

//ajax设置
require(['base/all/ajaxSet']);

//设置复选框单选框
require(['base/all/checkbox']);

//设置select
require(['chosen'],function(){
    $('.combox').chosen(); 
});

