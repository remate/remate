$(function() {
    var $pay_btn = $('.cartPay');

    //结算
    $pay_btn.on('click',function(){
        var skuIds = '';
        $('.changeNum').each(function(i,el){
            var sku = $(el).attr('data-sku-id');
            skuIds += '&skuId='+sku ;
        });
        skuIds = skuIds.substring(1);
        if(skuIds != ''){
            location.href = '/xiangqu/wap/cart/next?' + skuIds;
        }
    });

    //进入编辑状态
    $('.operation').bind('click', function() {
        var _this = this;
        if ($(_this).attr('data-show') == null || $(_this).attr('data-show') == 'true') {
            //未编辑状态进入编辑状态
            $(_this).parents('.cartItem').find('.js-info').hide();
            $(_this).parents('.cartItem').find('.js-editor').show();
            $(_this).attr('data-show', false);
        } else {
            //保存状态 
            $(_this).parents('.cartItem').find('.js-info').show();
            $(_this).parents('.cartItem').find('.js-editor').hide();
            $(_this).attr('data-show', true);
        }
    });

    //删除商品
    $('.cartDelete').bind('click', function() {
        var _this = this;
        var itemId = $(_this).parents('.cartProduct').find('.changeNum').attr('data-item-id');
        var params = {
            'itemId' : itemId
        }
        $.ajax({
            url: '/cart/delete',
            data: params,
            dataType: 'json',
            success: function(data) {
                if (data.data) {
                    deleteCb(function(){
                        updatePrice();
                    });
                } else {
                    alert(data.moreInfo);
                }
            }
        });

        function deleteCb(cb) {
            if ($(_this).parents('.cartItem').find('.cartProduct').size() == 1) {
                vd_tool.move($(_this).parents('.cartItem')[0], {
                    height: 0,
                    opacity: 0,
                    padding: 0
                }, 300, 'linear', function() {
                    $(_this).parents('.cartItem').remove();
                    cb && cb();
                })
                if ($('.cartItem').size() == 0) {
                    //如果全部都删除完了
                    return;
                }
                return;
            }
            vd_tool.move($(_this).parents('.cartProduct')[0], {
                height: 0,
                opacity: 0,
                padding: 0
            }, 300, 'linear', function() {
                $(_this).parents('.cartProduct').remove();
                cb && cb();
            });
        }
    });

    
    $('input.amount').each(function(){
        var _this = this;
        $(_this).attr('data-ori',$(_this).val());
    })

    $('input.amount').change(function() {
        var skuId = $(this).parents('.changeNum').attr('data-sku-id');
        var amount = $(this).val();
        var _this = this;
        
        if( !amount.match( /^\d+$/ )  ){
            $(_this).val( $(_this).attr('data-ori') );
            return;
        }

        
        var params = {
            skuId: skuId,
            amount: amount
        };
        $.ajax({
            url: "/cart/update",
            data: params,
            dataType: 'json',
            success: function(data) {
                if (data.data) {
                    numb(_this);
                    updatePrice();
                    $(_this).attr('data-ori',$(_this).val());
                } else {
                    alert(data.moreInfo);
                    $(_this).val( $(_this).attr('data-ori') );
                }
            }
        });
    });

    function numb(obj) {
        var n = $(obj).val();
        $(obj).parents('.cartProduct').find('.productNum span').html(n);
    }

    function updatePrice() {
        var amount = 0;
        $('.cartProduct').each(function() {
            var priceOne = $(this).find('.cartPriceNum span').html();
            priceOne = Number(priceOne.replace(/\￥|\,/g, ''));
            var num = $(this).find('.productNum span').html();
            var total = priceOne * num;
            amount += total;
        });
        $('.cartTotal .cart-red b').html('¥' + vd_tool.formatCurrency(amount.toFixed(2)) );
    }

});