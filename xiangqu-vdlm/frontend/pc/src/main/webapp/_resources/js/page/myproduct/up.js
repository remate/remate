define(['jquery', 'base/utils', 'base/myproduct/getData'], function(jquery, utils, getData) {
    $('body').on('click', '.btn-proOn', function() {
        utils.tool.confirm.call(this, '确认上架此商品？', function($el) {
            utils.api.batchOnsale([$el.attr('data-id')], function(data) {
                if (data.data.failList.length) {
                    utils.tool.alert('商品由于参加活动未能上架成功！');
                } else {
                    utils.tool.alert('上架成功');
                    if (!$('.order-item-body tr').length) {
                        $('[name=pageNum]').val($('[name=pageNum]').val() > 1 ? Number($('[name=pageNum]').val()) - 1 : 1);
                        that.getProduct();
                    };
                }
                getData();
            }, function(str) {
                utils.tool.alert(str);
            });
        });
    });

    $('#J-ups').on('click', function() {
        var arr = [];
        if (!$('.order-item-body [name="productId"]:checked').length) {
            utils.tool.alert('请选择要上架的商品');
            return;
        }
        $('.order-item-body [name="productId"]:checked').each(function() {
            arr.push($(this).parents('tr').attr('data-id'));
        });
        utils.tool.confirm.call(this, '确认上架这些商品？', function() {
            utils.api.batchOnsale(arr, function(data) {
                if (data.data.failList.length) {
                    utils.tool.alert('部分商品由于参加活动未能上架成功！');
                } else {
                    utils.tool.alert('上架成功');
                    if (!$('.order-item-body tr').length) {
                        $('[name=pageNum]').val($('[name=pageNum]').val() > 1 ? Number($('[name=pageNum]').val()) - 1 : 1);
                        that.getProduct();
                    };
                }
                getData();
            }, function(str) {
                utils.tool.alert(str);
            });
        });
    });
});