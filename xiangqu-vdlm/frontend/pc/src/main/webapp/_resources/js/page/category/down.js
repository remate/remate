define(['jquery', 'base/utils', 'base/category/getData', 'base/category/setPro'], function(jquery, utils, getData, setPro) {
    $('body').on('click', '.btn-proOff', function() {
        utils.tool.confirm.call(this, '确认下架此商品？', function($el) {
            utils.api.batchInstock([$el.attr('data-id')], function(data) {
                if (data.data.failList.length) {
                    utils.tool.alert('商品由于参加活动未能下架成功！');
                } else {
                    utils.tool.alert('下架成功');
                    var tempId = null,
                    $nowTab = $('.am-form-group-cate .active'),
                    tempType = 2,
                    options = {};
                    if($nowTab.attr('data-id')) {
                        tempId = $nowTab.attr('data-id');
                    }else {
                        if($nowTab.hasClass('notcate')) {
                            tempId = 0;
                        }else {
                            tempType = 1;
                        }
                    }
                    options.page = $('[name="pageNum"]').val() - 1;
                    utils.api.getProCateList(tempType,tempId,setPro,utils.tool.alert,options);
                }
            }, function(str) {
                utils.tool.alert(str);
            });
        });
    });

    $('#J-downs').on('click', function() {
        var arr = [];
        if (!$('.order-item-body [name="productId"]:checked').length) {
            utils.tool.alert('请选择要下架的商品');
            return;
        }
        $('.order-item-body [name="productId"]:checked').each(function() {
            arr.push($(this).parents('tr').attr('data-id'));
        });
        utils.tool.confirm.call(this, '确认下架这些商品？', function() {
            utils.api.batchInstock(arr, function(data) {
                if (data.data.failList.length) {
                    utils.tool.alert('部分商品由于参加活动未能下架成功！');
                } else {
                    utils.tool.alert('下架成功');
                    var tempId = null,
                        $nowTab = $('.am-form-group-cate .active'),
                        tempType = 2,
                        options = {};
                    if($nowTab.attr('data-id')) {
                        tempId = $nowTab.attr('data-id');
                    }else {
                        if($nowTab.hasClass('notcate')) {
                            tempId = 0;
                        }else {
                            tempType = 1;
                        }
                    }
                    options.page = $('[name="pageNum"]').val() - 1;
                    utils.api.getProCateList(tempType,tempId,setPro,utils.tool.alert,options);
                }
            }, function(str) {
                utils.tool.alert(str);
            });
        });
    });
});