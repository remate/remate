

//修改地址
require(['base/orders/order-addressModify']);
//改价
require(['base/orders/order-rateModify']);
//发货
require(['base/orders/order-goodsSend']);
//订单取消
require(['base/orders/order-cancel']);
//商品回复评论
require(['base/orders/pro-reply']);

require(['jquery','base/utils','plugins/address'], function(jquery,utils,Address) {
    var orders = {
        init : function () {
            
        }
    }
    orders.init();
});

