define(['jquery','base/utils'], function(jquery,utils) {
    //取消订单
    $(document).on('click','.am-btnCancel',function(){
        utils.tool.confirm.call(this,'确定要取消本订单吗？',function($el){
            var orderObj = $el.parents('table'); 
            var orderId = orderObj.attr('data-orderid');    //订单号
            
            var strUrl = '/sellerpc/order/cancel?orderId=' + orderId;
            $.ajax({
                url: strUrl,
                type: 'POST',
                cache:false,
                dataType: 'json',
                success: function(data) {
                        if (data) {
                            utils.tool.alert('订单取消成功！');
                            location.reload();//订单取消成功，刷新页面
                        }else{
                            utils.tool.alert('订单取消失败！');
                        } 
                    
                },
                error: function() {
                        utils.tool.alert('订单取消失败！');
                }
            });
        });
    });
    return null;
});