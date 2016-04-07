define(['jquery', 'base/utils', 'base/product/elements', 'underscore'], function(jquery, utils, eles, underscore) {
    return function() {
        var imgArr = [], //商品图片
            skuFormData = {},
            skuData = [],
            skuMappingData = [],
            skuPA = [], //price amount
            index = 0,
            indexJ = 0,
            validArr = [],
            price = 0,
            amount = 0,
            daledate,
            proCate,
            $selects = $('.platform-select'),
            selectsLen = $selects.length,
            cat_id = '',
            allNull = true; //所有商品分类下拉筐选中值都为空
        proCate = $('.product-Category').val();//商品分类
        $(".product-category-box").removeClass('chosen-container-active');
        $(".product-imgs img").each(function(i, item) {
            imgArr.push($(item).attr('data-img'));
        });
        if (window.skuInfo) {
            $.each(window.skuInfo.skus, function(i, el) {
                skuData[i] = {};
                skuPA[i] = {};
                $.each(el.sku, function(j, s) {
                    skuData[i]['spec' + (j + 1)] = s;
                });
                skuPA[i].price = el.price;
                skuPA[i].amount = el.amount;
            });

            $.each(window.skuInfo.mappings, function(i, el) {
                skuMappingData[i] = {};
                skuMappingData[i].specKey = 'spec' + (i + 1);
                skuMappingData[i].specName = el;
            });
        }
        $.each(skuPA, function(i, el) {
            if (el.price && el.price > 0 && el.amount && el.amount > 0) {
                validArr.push(i);
                skuFormData['skus[' + index + '].price'] = el.price;
                skuFormData['skus[' + index + '].amount'] = el.amount;
                index++;
            }
        });
        $.each(skuData, function(i, el) {
            if (_.indexOf(validArr, i) >= 0) {
                for (var attr in el) {
                    skuFormData['skus[' + indexJ + '].' + attr] = el[attr];
                }
                indexJ++;
            }
        });
        $.each(skuMappingData, function(i, el) {
            for (var attr in el) {
                skuFormData['skuMappings[' + i + '].' + attr] = el[attr];
            }
        });
        if (window.skuInfo && window.skuInfo.amount !== undefined && window.skuInfo.amount !== '' && window.skuInfo.amount !== 0 && $('.skus-show-item').length) {
            price = window.skuInfo.price || 0;
            amount = window.skuInfo.amount || 0;
        } else {
            price = eles.proPrice.val();
            amount = eles.proAmount.val();
        }
        //还是要判断下库存为0的时候不让提交，此处app端的处理是保存到本地的草稿箱
        if( amount == 0 ){
            utils.tool.alert('商品库存不能为0！');
            return;
        }
        
        var date = $('.date-picker input').val();
        daledate = date ? new Date((date).replace(new RegExp("-", "gm"), "/")).getTime() : '';
        var param = {
            imgs: imgArr.join(','),
            name: eles.proName.val(),
            description: eles.proDesc.val(),
            recommend: eles.proRecomend.is(':checked') ? 1 : 0,
            status: eles.proStatus.filter(':checked').val(),
            forsaleDate: daledate,
            category: proCate
        };

        //商品分类只保存选中的最小类目
        /*
        for(var i = selectsLen - 1;i>=0;i--){
            console.log(i);
            console.log($selects.eq(i).val());
            if( $selects.eq(i).val() != '' ){
                cat_id = $selects.eq(i).val();
                break;
            }
        }

        */
       
       //判断是不是所有select都没有选择
       for(var i = selectsLen - 1;i>=0;i--){
            if( $selects.eq(i).val() != '' ){
                allNull = false;
                break;
            }
        }

        //只能保存最小节点的类目
        
        if($selects.last().val() == '' && !allNull){
            utils.tool.alert('商品分类请选择叶子节点！');
            return;
        }

        cat_id = $selects.last().val();

        //如果有商品分类 就传递商品分类
        if(cat_id){
           $.extend(param, {
             cat_id: cat_id
           }); 
        }

        $.extend(param, skuFormData);
        if (!$('.skus-show-value').length) {
            $.extend(param, {
                'skus[0].price': price,
                'skus[0].amount': amount,
                'skus[0].spec1': '无'
            });
        }
        if (utils.tool.request('id')) {
            $.extend(param, {
                id: utils.tool.request('id')
            });
        }

        if (eles.proPubType.filter(':checked').val() == 'pc') {
            //如果是富文本编辑器编辑
            editor.sync();
        }

        //ajax
        utils.api.saveProduct(param, function(data) {
            var proId = data.data.id,
                framentList = [];
            //如果段落片段模式
            if (eles.proPubType.filter(':checked').val() == 'all') {
            	
            	$('.desc-tabel-show tr').each(function() {
                    framentList.push($(this).attr('data-id'));
                });
                var fraData = {
                    fragmentIds: framentList.join(','),
                    productId: proId
                };
                utils.api.saveProductFragment(fraData, function(data) {
                    utils.tool.alert('保存成功!');
                    $(window).off('beforeunload.pro');
                    setTimeout(function() {
                        location.href = 'myproduct?status=' + param.status;
                    }, 1000);
                }, function() {
                    utils.tool.alert('保存失败!');
                }); 
            	
                //修改启用段落描述作为商品详情
//                utils.api.changeEnableDesc({
//                    'productId': proId,
//                    'enableDesc': false
//                }, function() {
//                    $('.desc-tabel-show tr').each(function() {
//                        framentList.push($(this).attr('data-id'));
//                    });
//                    var fraData = {
//                        fragmentIds: framentList.join(','),
//                        productId: proId
//                    };
//                    utils.api.saveProductFragment(fraData, function(data) {
//                        utils.tool.alert('保存成功!');
//                        $(window).off('beforeunload.pro');
//                        setTimeout(function() {
//                            location.href = 'myproduct?status=' + param.status;
//                        }, 1000);
//                    }, function() {
//                        utils.tool.alert('保存失败!');
//                    }); 
//                }, function() {
//                    utils.tool.alert('保存失败!');
//                });
            } else {
                //如果是富文本编辑器
//                utils.api.changeEnableDesc({
//                    'productId': proId,
//                    'enableDesc': true
//                }, function() {
//                    var data = {
//                        'productId': proId,
//                        'description': $('#J-editor').val()
//                    };
//                    if(window.productDescId){
//                        $.extend(data,{id: window.productDescId});
//                    }
//                    utils.api.saveProductDesc(data, function() {
//                        utils.tool.alert('保存成功!');
//                        $(window).off('beforeunload.pro');
//                        setTimeout(function() {
//                            location.href = 'myproduct?status=' + param.status;
//                        }, 1000);
//                    }, function() {
//                        utils.tool.alert('保存失败!');
//                    });
//                }, function() {
//                    utils.tool.alert('保存失败!');
//                });
            }
        }, function(str) {
            utils.tool.alert(str);
        });
    }
});