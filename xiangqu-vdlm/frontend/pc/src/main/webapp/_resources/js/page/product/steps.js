define(['jquery', 'doT', 'underscore', 'base/utils', 'base/product/combine', 'base/product/initSkuUI'], function(jquery, doT, underscore, utils, combine,initSkuUI) {

    var step2Tpl = $('.tpl-step2').html(),
        doTstep2Tpl = doT.template(step2Tpl);
    var step2ItemTpl = $('.tpl-step2-item').html(),
        doTstep2ItemTpl = doT.template(step2ItemTpl);
    // 第一步点击下一步
    $('.step1 .goNext').on('click', function() {
        //先判断选择的类型有没有超过2个
        if ($(".step1-skuList").find('input:checked').length > 2) {
            utils.tool.alert('所选类型不能超过2个!');
            return;
        }
        //如果少于1个也不能进行下一步
        if ($(".step1-skuList").find('input:checked').length == 0) {
            utils.tool.alert('至少选择一个类型!');
            return;
        }

        if(!window.skuInfo){
            window.skuInfo = {
                mappings: [],
                skus: []
            };
        }

        if (!window.typeList) {
            window.typeList = {};
            $.each(window.skuInfo.mappings, function(i, el) {
                window.typeList[el] = window.skuInfo.types[i];
            });
        }
        var keyArry = [];
        $(".step1-skuList").find('input').each(function(i, item) {
            if ($(item).is(':checked')) {
                keyArry.push($(item).attr('data-val'));
            }
        });

        for (var attr in window.typeList) {
            if (_.indexOf(keyArry, attr) < 0) {
                //如果不存在 删掉
                delete window.typeList[attr];
            }
        }
        var i = 0,
            j = keyArry.length;

        var data = {
            keyArr: keyArry,
            typeList: window.typeList
        };
        $("#J-sku-step2").html(doTstep2Tpl(data));
        $('.step1,.step2,.step3').hide();
        $('.step2').show();
        recordSku();
    });

    //第二步时点击上一步
    $('.step2 .goPrev').on('click', function() {
        $('.step1,.step2,.step3').hide();
        $('.step1').show();
    });

    //第二步页面的操作
    //删除整个类型
    $('body').on('click', '.step2-skuType-delAll', function() {
        utils.tool.confirm.call(this, '确认删除此类型？', function($el) {
            var val = $el.parents('.step2-item').attr('data-name');
            $el.parents('.step2-item').remove();
            delete window.typeList[val];
            $('.step1 input[data-val="' + val + '"]').attr('checked', false).initCheck();
        });
    });
    //点击添加类型
    $('body').on('click', '.step2-skuType-add a', function() {
        $(this).parents('.step2-item').find('.step2-item-list').append(doTstep2ItemTpl({}));
    });
    //点击删除单个类型
    $('body').on('click', '.step2-skuType-del', function() {
        //如果是最后一个就不让删
        if($(this).parents('.step2-item').find('.step2-skuType').length <= 1){
            utils.tool.alert('再删就不没有了～');
            return;
        }
        utils.tool.confirm.call(this, '确认删除此类型？', function($el) {
            $el.parents('.step2-skuType').remove();
            getTypeList();
        });
    });

    //
    $('body').on('change', '.step2-skuType input', function() {
        $(this).attr('data-remVal',$(this).val());
        getTypeList();
    });

    //第二步页面点击下一步
    $('.step2 .goNext').on('click', function() {
        //先判断是否每个输入框都有值
        var isNoEmpty = true;
        $('.step2 input').each(function() {
            if ($.trim($(this).val()) == '') {
                isNoEmpty = false;
            }
        });
        if (!isNoEmpty) {
            utils.tool.alert('类型名称不能为空！');
            return;
        }
        var arr = [];

        function skuItem(i) {
            arr[i] = [];
            $('.step2-item-list').eq(i).find('input').each(function(j, el) {
                arr[i].push($(el).val());
            });
        }
        for (var i = 0; i < $(".step2-item-list").length; i++) {
            skuItem(i);
        }
        var res = combine(arr);

        var tableTpl = $('.tpl-table').html();
        var doTtmpl = doT.template(tableTpl);

        window.skuInfoModify = {
            mappings: [],
            skus: []
        };

        $.each($(".step2-item"), function(i, el) {
            //先判断是否已经有,如果是没有的 则加上
            var val = $.trim($(el).find('.step2-item-key').text());
            window.skuInfoModify.mappings.unshift(val);
        });

        $.each(res, function(i, el) {
            window.skuInfoModify.skus.push({
                'sku': el,
                'price': 0,
                'amount': 0
            });
        });
        //与原数据对比修改价格和库存
        if (window.skuInfo) {
            $.each(window.skuInfoModify.skus, function(i, el) {
                //遍历所有的skus 如果 back 中的每一项sku 和 base中的一样  替换价格和数量
                $.each(window.skuInfo.skus, function(j, e) {
                    var ek = reverse(e.sku);
                    //全部相等再替换
                    var isAll = true;
                    for (var k = 0; k < ek.length; k++) {
                        var dataV = $('.step2-skuType input[data-remVal="'+ el.sku[k] +'"]').attr('data-val');
                        if (!(el.sku[k] && (ek[k] == el.sku[k]) || ek[k] == dataV)) {
                            isAll = false;
                            break;
                        }
                    }
                    if (isAll) {
                        el.price = e.price;
                        el.amount = e.amount;
                    }
                });
                //有时候数据又不是反过来的
                $.each(window.skuInfo.skus, function(j, e) {
                    //全部相等再替换
                    var isAll = true;
                    for (var k = 0; k < e.sku.length; k++) {
                        var dataV = $('.step2-skuType input[data-remVal="'+ el.sku[k] +'"]').attr('data-val');
                        if (!(el.sku[k] && (e.sku[k] == el.sku[k] || e.sku[k] == dataV))) {
                            isAll = false;
                            break;
                        }
                    }
                    if (isAll) {
                        el.price = e.price;
                        el.amount = e.amount;
                    }
                });
            });
        }
        $(".step3-table-box").html(doTtmpl(window.skuInfoModify));
        $('.step1,.step2,.step3').hide();
        $('.step3').show();
    });

    //第三步页面操作
    //第三步点击上一步
    $('.step3 .goPrev').on('click', function() {
        $('.step1,.step2,.step3').hide();
        $('.step2').show();
        // recordSku();
    });
    //第三步删除一行
    $('body').on('click', '.step3 td a', function() {
        utils.tool.confirm.call(this, '确认删除此组合？', function($el) {
            $el.parents('tr').remove();
        });
    });
    //库存输入框blur 必须是数字 必须大于0
    $('body').on('blur','input[data-name="amount"]',function(e){
        if(!/^[1-9]\d*$/.test($(this).val())){
            if (!$(e.relatedTarget).hasClass('am-btn-comfirm')) {
                utils.tool.alert('库存必须为大于0的正整数');
            }
            $(this).val('').focus();
            return;
        }
    });
    var reg = /^\-?[0-9]*\.[0-9]*$|^([0-9]+)$/;
    $('body').on('blur','input[data-name="price"]',function(e){
        if(!reg.test($(this).val())){
            if (!$(e.relatedTarget).hasClass('am-btn-comfirm')) {
                utils.tool.alert('价格必须为大于0的数字');
            };
            $(this).val('').focus();
            return;
        }
        if($(this).val() <= 0){
            if (!$(e.relatedTarget).hasClass('am-btn-comfirm')) {
                utils.tool.alert('价格必须为大于0的数字');
            };
            $(this).val('').focus();
            return;
        }
        if($(this).val() > 99999000){
            if (!$(e.relatedTarget).hasClass('am-btn-comfirm')) {
                utils.tool.alert('价格最大为99999000');
            };
            $(this).val('').focus();
            return;
        }
    });
    //第三步点击完成的操作
    $('#complete').on('click',function(){
        var isNoEmpty = true;
        //如果组合全部被删除完了 则提示
        if(!$('.step3-table-box tbody tr').length){
            utils.tool.alert('当前没有类型组合！');
            return;
        }
        //检查输入框是否有是空的
        $('.step3-table-box tbody input').each(function(){
            if($.trim($(this).val()) == ''){
               isNoEmpty = false;
            }
        });
        if(!isNoEmpty){
            utils.tool.alert('输入框不能为空!');
            return;
        }
        //生成数据
        var skuTable = $('.step3-table-box tbody');
        //先全部价格和库存置为0
        for (var j = 0; j < window.skuInfoModify.skus.length; j++) {
            window.skuInfoModify.skus[j].amount = 0;
            window.skuInfoModify.skus[j].price = 0;
        }

        for (var i = 0; i < skuTable.find('tr').length; i++) {
            for (var j = 0; j < window.skuInfoModify.skus.length; j++) {
                var isThis = true;
                //遍历skus 取key组  得到匹配的项
                $.each(window.skuInfoModify.skus[j].sku, function(k, e) {
                    //判断每一个key是不是和表格的值对应
                    var tdVal = $.trim($(skuTable.find('tr')[i]).find('td div').eq(k).text());
                    if (e != tdVal) {
                        isThis = false;
                    }
                });
                if (isThis) {
                    window.skuInfoModify.skus[j].amount = $(skuTable.find('tr')[i]).find('[data-name="amount"]').val();
                    window.skuInfoModify.skus[j].price = $(skuTable.find('tr')[i]).find('[data-name="price"]').val();
                }
            }
        }
        window.skuInfo = window.skuInfoModify;
        initSkuUI();
        $('#J-modifySku-pop').modal('close');
        recordSku();

    });
    function reverse(arr) {
        var arrNew = [];
        for (var i = 0; i < arr.length; i++) {
            arrNew.unshift(arr[i]);
        }
        return arrNew;
    }

    function getTypeList() {
        //获取skuMapping列表缓存数据
        window.typeList = {};
        $('.step2 .step2-item').each(function(i, el) {
            var key = $(this).attr('data-name');
            window.typeList[key] = [];
            $(el).find('.step2-skuType input').each(function(j, e) {
                window.typeList[key].push($(e).val());
            });
        });
    }

    function recordSku(){
        $('.step2-skuType input').each(function(){
            $(this).attr('data-val',$(this).val());
        });
    }
});