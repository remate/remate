    var $ = require('jquery');
    require('../module/num.js');
    require('../module/allSelect.js');

    $(document).ready(function () {
        $('#order-btn').bind('click', function () {
            return false;
        });
        $('input.numed').on('change',function () {
            var obj = this;
            var oldNUmb=$(obj).parent().parent().parent().attr("data-old-numb");
            var newNumb=$(obj).val();
            var skuId = $(obj).parents('tr').attr('data-sku-id');
            var isTrue = false;
            var regu = /^([0-9]+)$/;
            var re = new RegExp(regu);
            if(re.test(newNumb) && Number( $(obj).val() ) <= Number($(obj).closest('tr').attr('data-inventory')) ){
                var params = {
                    skuId: skuId,
                    amount: newNumb
                };
                $.ajax({
                    url: "/cart/update",
                    data: params,
                    type: "POST",
                    traditional: true,
                    success: function (data) {
                        isTrue = $(obj).parents('tr').find('.td0').find('.moni-checkbox').hasClass('moni-checkbox-active');

                        if (data.errorCode==200) {
                            $(obj).parent().parent().parent().attr("data-old-numb",newNumb);

                            numb();
                            if (isTrue) {
                                updatePrice();
                            }
                        }else{
                            $(obj).val(oldNUmb);
                            alert(data.moreInfo);
                            return false;
                        }
                    }
                });
            }else{
                $(obj).val(oldNUmb);
                return false;
            }

        });
        //删除函数
        function del(obj) {
            var parent = $(obj).parent();
            var head= parent.parent();
            //店铺多属商品是否都删除，店铺头信息删除
            $(obj).remove();

            if (parent.children().length == 0) {
                head.remove();
            }
            if($('.table').length==0){
                $('#id-cart').hide();
                $(".cart_no").css('display','block');
            }
        }

        //提交删除宝贝itemid
        function getDelJson(obj, itemid) {
            var Id;
            var isTrue = false;
            if (typeof(itemid) == "string") {
                Id = itemid;
                getJson();
            } else {
                for (i in itemid) {
                    Id = itemid[i];
                    getJson();
                }
            }
            function getJson() {
                var params = {
                    itemId: Id
                };
                var time;
                clearTimeout(time);
                $.ajax({
                    url: '/cart/delete',
                    data: params,
                    traditional: true,
                    success: function (data) {
                        isTrue = $(obj).find('.td0').find('.moni-checkbox').hasClass('moni-checkbox-active');
                        if (data.errorCode==200) {
                            del(obj);//删除
                            numb();//计算总数量
                            //选中获取价格

                            time = setTimeout(function() {
                                if (isTrue) {
                                    updatePrice();
                                }
                            },100);
                            disable();
                        } else {
                            alert(data.moreInfo);
                        }
                    }
                });
            }

        }

        //单个删除
        $('td.td6').bind('click', function () {
            var itemid = $(this).attr('data-item-id');
            if (confirm('确认要从购物车中删除该商品吗?')) {
                getDelJson($(this).parent(), itemid);

            }
        });
        //批量删除
        $('.del').bind('click', function () {
            numb();
            // var itemid = [];
            var obj;
            var checkboxActive = $('.moni-checkbox-active','.td0');
            if(!checkboxActive.length){
                alert('请选择商品！');
                return false;
            }
            if (confirm('确认要从购物车中删除选中商品吗?')) {
                $('.moni-checkbox-active','.td0').each(function (k, item) {//是否要在这判断是否是head
                   /* if($(item).parent().hasClass('.td0')){
                        obj = $(item).parents('thead');
                    }else{
                        itemid.push($(item).parents('tr').find('.td6').attr('data-item-id'));
                        obj = $(item).parent().parent();
                        getDelJson(obj, itemid);
                    }*/
                    // itemid.push($(item).parents('tr').find('.td6').attr('data-item-id'));
                    var itemId = $(item).parents('tr').find('.td6').attr('data-item-id');
                    obj = $(item).parent().parent();
                    getDelJson(obj, itemId);
                })

            }
            //$('.moni-checkbox-active').parents('tr').remove();
        })
        //checkbox
        $('.moni-checkbox').bind('click', function () {
            numb();
            updatePrice();
        })
        //计算总数
        function numb() {
            var total = 0;
            $('.moni-checkbox-active', '.td0').each(function (i, item) {
                total += parseInt($(item).parents('tr').find('.td4').find('.amount').val());
            })
            $("#J-numb").text(total);
        }

        function updatePrice() {
            var skuid = [];

            if ($('.moni-checkbox-active', '.td0').length == 0) {
                $("#J-price").text("0.00");
            } else {
                $('.moni-checkbox-active', '.td0').each(function (i, item) {
                    skuid.push($(item).parent().parent().attr('data-sku-id'));
                })
                var params = {
                    skuIds: skuid
                };
                $.ajax({
                    url: "/cart/pricing",
                    data: params,
                    traditional: true,
                    cache: false,
                    success: function (data) {
                        if (data.errorCode==200) {
                            //totalprice += parseFloat(data.data.totalFee,2);
                            //Number(data.data.totalFee * 1).toFixed(2)
                            $('#J-price').text(Number(data.data.totalFee * 1).toFixed(2));
                        } else {
                            alert(data.moreInfo);
                        }
                    }
                });
            }
        }

        $('#order-btn').bind('click', function () {
            var skuid = [];
            var obj = $('.moni-checkbox-active', '.td0');
            var l = obj.length;
            obj.each(function (i, item) {
                sku = "skuId=" + $(item).parent().parent().attr('data-sku-id');
                if (i === l - 1) {
                    skuid += sku;
                } else {
                    skuid += (sku + "&");
                }
            })

            if ($('input.amount').val() == "") {
                $(".numed").val('1').trigger('change');
                return;
            }
            if(!skuid.length==0){
                location.href = "/xiangqu/web/cart/next" + "?" + skuid;
            }else{
                alert("请选择商品进行结算~");
            }
            return false;
        })
        function disable() {
            if (!$(".table-disable").find('tr').length == 0) {
                //$('.table-disable').find('.moni-checkbox').unmask();
                $('.table-disable').find('.moni-checkbox').each(function(v,obj){
                    $(obj).unbind('click');
                })
                $(".shelves-select").each(function (i,obj) {
                    $(obj).find('.amount').attr("disabled",true);
                });
            }
        }

        disable();
    });
