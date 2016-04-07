/**
 * Created by ziyu on 2016/2/26.
 */
(function($){

    var APP = {
        remindShip:'/ouer/order/remindShip',//提醒卖家发货
        delay:'/ouer/order/delay',//确认收货
        delOrder:'/order/delete-buyer',//删除订单
    }
    var OrderDetail = {
        init:function(){
            var self = this;
            self.J_orderDetail = $('.J_orderDetail');

            self.orderId = self.J_orderDetail.attr('data-orderid');

            self.YCSH = '延迟确认收货';
            self.QRSH = '确认收货';
            self.isYCSH = false;

            self.eventHanding();
        },
        eventHanding:function(){
            var self = this;
            //返回
            self.J_orderDetail.on('click','.J_cartBack',function(e){
                e.preventDefault();
                if(window.history.length > 1){
                    window.history.go(-1);
                }else{
                    window.location.href = window.host;
                }
            });

            //删除订单 ok
            self.J_orderDetail.on('click','.J_delOrder',function(e){
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
                        location.href = '/m/order/list?page=0';
                    }
                });
            });

            //取消订单 ok
            self.J_orderDetail.on('click','.J_cancel',function(e){
                e.preventDefault();
                location.href ='/order/' + self.orderId + '/cancel';
            });

            //延迟确认收货
            self.J_orderDetail.on('click','.J_delay',function(e){
                e.preventDefault();
                self.J_orderDetail.find('.J_hd').hide();
                self.isYCSH = true;
                self.J_orderDetail.find('.J_delayTc').find('.J_tcTitle').html(self.YCSH);
                self.J_orderDetail.find('.J_delayTc').show();
            });

            //付款
            self.J_orderDetail.on('click','.J_pay',function(e){

            });


            //提醒卖家发货
            self.J_orderDetail.on('click','.J_remind',function(e){
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
            self.J_orderDetail.on('click','.J_delaySure',function(e){
                e.preventDefault();
                self.J_orderDetail.find('.J_hd').hide();
                self.isYCSH = false;
                self.J_orderDetail.find('.J_delayTc').find('.J_tcTitle').html(self.QRSH);
                self.J_orderDetail.find('.J_delayTc').show();

            })

            //取消延迟收货
            self.J_orderDetail.find('.J_delayTc').on('click','.J_closeDelayTc',function(e){
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