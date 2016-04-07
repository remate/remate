/**
 * Created by ziyu on 2016/2/26.
 */
(function($){

    var APP = {
        remindShip:'/order/remindship',//提醒卖家发货
        delay:'/ouer/order/delay',//确认收货
        delOrder:'/order/delete-buyer'//删除订单
    };
    var OrderDetail = {
        init:function(){
            var self = this;
            self.J_orderDetail = $('.J_orderDetail');

            self.orderId = self.J_orderDetail.attr('data-orderid');

            self.YCSH = '延迟确认收货';
            self.QRSH = '确认收货';
            self.QRSHtext = '自动确认收货时间';
            self.YCSHtext = '下一次自动确认收货时间';
            self.isYCSH = false;

            self.eventHanding();
        },
        eventHanding:function(){
            var self = this;
            //返回
            $('.J_cartBack').on('click',function(e){
                e.preventDefault();
                if(window.history.length > 1){
                    window.history.go(-1);
                }else{
                    window.location.href = "http://www.xiangqu.com";
                }
            });

            //删除订单 ok
            $('.J_delOrder').on('click',function(e){
                e.preventDefault();
                var data = {
                    orderId:self.orderId,
                    t:self.returnTime()

                };
                self.getFetchDate(APP.delOrder,'post',data,function(rs){
                    if(rs.code != 200){
                        alert(rs.msg);
                        return false;
                    }else{
                        location.href = 'http://m.xiangqu.com/m/order/list?page=0';
                    }
                });
            });

            //取消订单 ok
            $('.J_cancel').on('click',function(e){
                e.preventDefault();
                location.href ='/order/' + self.orderId + '/cancel';
            });

            //延迟确认收货
            $('.J_delay').on('click',function(e){
                e.preventDefault();
                self.J_orderDetail.find('.J_hd').hide();
                self.isYCSH = true;
                self.J_orderDetail.find('.J_delayTc').find('.J_tcTitle').html(self.YCSH);
                $('.J_getText').html(self.YCSHtext);
                self.J_orderDetail.find('.J_delayTc').show();
                $('.J_QRSH').hide();
                $('.J_YCSH').show();
            });

            //付款
            $('.J_pay').on('click',function(e){

            });


            //提醒卖家发货
            $('.J_remind').on('click',function(e){
                e.preventDefault();
                var data = {
                    orderId:self.orderId
                };
                self.getFetchDate(APP.remindShip,'post',data,function(rs){
                    if(rs.code != 200){
                        alert(rs.msg);
                        return false;
                    }
                    if(rs.code == 200){
                        alert('提醒卖家发货成功');
                        return false;
                    }


                });
            });


            //确认收货
            $('.J_delaySure').on('click',function(e){
                e.preventDefault();
                self.J_orderDetail.find('.J_hd').hide();
                self.isYCSH = false;
                self.J_orderDetail.find('.J_delayTc').find('.J_tcTitle').html(self.QRSH);
                $('.J_getText').html(self.QRSHtext);
                self.J_orderDetail.find('.J_delayTc').show();
                $('.J_QRSH').show();
                $('.J_YCSH').hide();
            })

            //取消延迟收货
            //self.J_orderDetail.find('.J_delayTc').on('click','.J_closeDelayTc',function(e){
            $('.J_closeDelayTc').on('click',function(e){
                e.preventDefault();
                self.J_orderDetail.find('.J_hd').show();
                self.J_orderDetail.find('.J_delayTc').hide();
                var that = $(this).closest('.J_delayTc');
                self.clearForm(that);
            });



        },
        clearForm:function(ele){
            var self = this;
            ele.find('.J_phoneNum').val('');
            ele.find('.J_imgCode').val('');
            ele.find('.J_codeNum').val('');

        },
        getFetchDate:function(url,type,data,cb){
            var self = this;
            $.ajax({
                url:url,
                type:type,
                data:data,
                success:function(rs){
                    if(!rs){
                        alert('服务器异常请刷新页面再试');
                        return false;
                    }
                    if(cb){
                        cb(rs);
                    }
                },
                error:function(req,tst,err){
                    alert('系统正在维护中...');
                }
            });
        },
        returnTime:function(){
            var self = this;
            var _t = new Date();
            return _t.getTime();
        }
    };

    $(function(){
       OrderDetail.init();
    });
})(Zepto);