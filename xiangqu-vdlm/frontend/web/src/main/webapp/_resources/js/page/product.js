$(document).ready(function() {
    $('#addtocart').bind('click', function() {
        var product = new Product();
        product.addToCart();
    });
    $('#buy').bind('click', function() {
        var product = new Product();
        product.buy();
    });
    $('#xqbuy').bind('click', function() {
        var product = new Product();
        product.xqbuy();
    });
    $('#province').bind('click', function() {
        var product = new Product();
        product.updateZoneSelector(this, 1);
    });
    $('#city').bind('click', function() {
        var product = new Product();
        product.updateZoneSelector(this, $('#province').val());
    });
    $('#district').bind('click', function() {
        var product = new Product();
        product.updateZoneSelector(this, $('#city').val());
    });

    $('#addToCartForm').bind('submit', function() {
        var skuId = $('#skuId').val();
        if (!skuId) {
            alert('请选择规格');
            return false;
        }
        return true;
    });
    //选择sku
    if($(".pro-sku-val").length==0){
        $("#skuId").val($(".js-sku").attr("data-sku-id"));
    }
    var selectItem = [];
    function selsectsku(){
        //获取已选择的sku单项
        selectItem = [];
        $('.pro-sku-val').find('.selected').each(function(){
            var item = {
                key: $(this).parent().attr('mapping_key'), //如spec1
                value: $(this).text() //如红色
            };
            selectItem.push(item);
        });
        return selectItem;
    }
    $('.prodOption').bind('click',function() {
        var self = $(this);
        var currentText="<div class='currentText'>"+$(self).text()+"</div>";
        //同一维度sku之能选择一个
        var skuData=[];
        if(self.hasClass('sku_disabled')){
            return ;
        }
        
        if(self.hasClass('selected')){
            self.removeClass('selected');
            $("#skuId").val('');
        }else{
            self.parent().find('.selected').removeClass('selected');
            self.addClass('selected');
            if($('.selected').length==$(".pro-sku-val").length){
                skuid(self);//sku都选时获取skuid
            }else{
                $("#skuId").val('');
            }
        }
        shows(self);
    });
    //判断书否可选
    /*if($('.prodOption').hasClass("no-seleted")){
     $('.prodOption').unbind('click');
     }*/
    $("#price").attr("data-price",$("#price").text());
    $("#J-amount").attr("data-amount",$("#J-amount").find('span').text());
    var skulist=[];
    function skuid(self){
        skulist=[];
        var selectitem =selsectsku();
        var parent = $(self).parent();
        var specKey = parent.attr("mapping_key");//获取当前key即维度
        $("sku").each(function(k,item){//几条数据代表几个纬度，与所需更新数据的坑一致，通过key，obj对应
            if($(item).attr("data-"+specKey)==self.text()){//筛选当前所选sku对应的项
                skulist.push(item);
            }
        });

        //sku多维度都选定，重置库存、价格、赋值skuid
        $(skulist).each(function(k,item){
           if($(".pro-sku-val").length==1 && $(item).attr("data-"+$(selectitem).get(0).key)==$(selectitem).get(0).value || 
        		   $(".pro-sku-val").length==2 && $(item).attr("data-"+$(selectitem).get(0).key)==$(selectitem).get(0).value && $(item).attr("data-"+$(selectitem).get(1).key)==$(selectitem).get(1).value){
        	    $("#skuId").val($(item).attr("data-sku-id"));
                $("#inventory_amount").text($(item).attr("data-amount"));
                $("#price").text("￥" + $(item).attr("data-price"));
                $("#market_price").text("￥" + $(item).attr("data-marketprice"));
            } 
        });
    }
    //加入购物或直接购买，每个维度必须全选择
    function shows(){
        var selectitem =selsectsku();
        $('.pro-sku-val').find(':not(.selected)').addClass('sku_disabled');
        //理论上所有的sku
        $('.pro-sku-val').find('.prodOption').each(function(i, allItem){
            //已选中的不做处理
            if($(allItem).hasClass('selected')){
                return true;
            }
            
            //未选中的
            var specKey = $(allItem).parent().attr('mapping_key'); //spec1
            var specValue = $(allItem).text();    //10kg
            var bInclude = false;
            
            
            //本商品的sku
            $('.js-sku').each(function(j, existItem){
                if($(existItem).attr('data-' + specKey) == specValue){ //找到有10kg的sku
                    //已选择spec1 10kg
                    $.each(selectitem, function(m, item){
                        if($(existItem).attr('data-' + item.key) == item.value){
                            bInclude = true;
                        }
                        //sku多维度都选定，重置库存、价格、赋值skuid
                        /* if($('.selected').length==$(".pro-sku-val").length){
                         if($(selectitem).get(1)!==undefined && $(item).attr("data-"+$(selectitem).get(0).key)==$(selectitem).get(0).value && $(item).attr("data-"+$(selectitem).get(1).key)==$(selectitem).get(1).value){
                         $("#skuId").val($(item).attr("data-sku-id"));
                         $("#inventory_amount").text($(item).attr("data-amount"));
                         $("#price").text("￥" + $(item).attr("data-price"));
                         }
                         }else{
                         $("#skuId").val('');
                         }*/

                    });
                }
            });
            
            //sku 未选
            if(selectitem.length == 0){
                $("#price").text($("#price").attr("data-price"));
                $("#J-amount").find("span").text($("#J-amount").attr("data-amount"));
                bInclude = true;
            }
            
            
            //选中一个
            if(selectitem.length == 1){
                if(selectitem[0].key == 'spec1' && specKey == 'spec1'){
                    bInclude = true;
                } else if(selectitem[0].key == 'spec2' && specKey == 'spec2'){
                    bInclude = true;
                }
            }
            //console.log($(allItem));
            if(bInclude)
                $(allItem).removeClass('sku_disabled');
        });
    }
});

Product.prototype = {
    addToCart: function() {
        var skuId = $('#skuId').val();
        var amount = $('input[name=amount]').val();
        if (!skuId) {
            alert('请选择规格');
            return;
        }
        var params = {
            'skuId': skuId,
            'amount':amount
        };
        var url = '/cart/add';
        $.post(url, params, function(data) {
            var r = $.parseJSON(data);
            if (r.data) {
                alert('商品成功加入购物车');
            } else {
                alert(r.moreInfo);
            }
        });
    },
    buy: function() {
        var skuId = $('#skuId').val();
        var qty = $('input[name=amount]').val();
        var regu = /^([0-9]+)$/;
        var re = new RegExp(regu);
        var numed=$(".numed");
        var inventory=parseInt($("#inventory_amount").text().replace(',',''));
        if (!skuId) {
            alert('请选择规格');
            return;
        }
        if (!re.test(numed.val())) {
            alert('请输入数字');
            numed.val('1').text('1').trigger('change');
            return false;
        }else if(Number(numed.val())<1){
            alert('请输入大于1的商品数字');
            numed.val('1').text('1').trigger('change');
            return false;
        }else if(Number(numed.val())>inventory){
            alert('亲，手下留情哦，仓库都被你搬回家拉，所选数量大于库存数量了哦~');
            $(".amount").val(inventory).text(inventory).trigger('change');
            return false;
        }
        //需求修改，不在发送请求，直接拼写url跳转到next页面
        window.location.href = '/cart/next?skuId=' + skuId+'&qty=' + qty;
        /*var params = {
            'skuId': skuId,
            'amount': amount
        };
        $.post("/cart/update", params, function(r) {
            var data;
            try{
                data = $.parseJSON(r);
            }catch(e){
                data = eval(r);
            }
            if (data.data) {
                //location.href = "/cart?skuId=" + skuId;
                location.href = "/cart/next?skuId=" + skuId;
            } else {
                alert(data.moreInfo);
            }
        });*/
    },
    xqbuy: function() {
        var skuId = $('#skuId').val();
        var qty = $('input[name=amount]').val();
        if (!skuId) {
            alert('请选择规格');
            return;
        }
        window.location.href = '/cart/next?skuId=' + skuId+'&qty=' + qty;
       /* var params = {
            'skuId': skuId,
            'amount': amount
        };
        $.post("/cart/update", params, function(r) {
            var data;
            try{
                data = $.parseJSON(r);
            }catch(e){
                data = eval(r);
            }
            if (data.data) {
                location.href = "/cart/next?skuId=" + skuId;
            } else {
                alert(data.moreInfo);
            }
        });*/
    },
    selectProvince: function() {
        var url = '/zone/1/children';
        $.getJSON(url, function(data) {
            var i=0;
            var dataL=data.length;
            for (i; i < dataL; i++) {
                //console.log(data[i]);
                var option = '<option value="' + data[i].id + '">' + data[i].name + '</option>';
                $('#province').append(option);
            }
        });
    },
    updateZoneSelector: function(zoneEle, parentZoneId) {
        var url = '/zone/' + parentZoneId + '/children';
        $.getJSON(url, function(data) {
            var i=0;
            var dataL=data.length;
            for (i; i < dataL; i++) {
                var option = '<option value="' + data[i].id + '">' + data[i].name + '</option>';
                $(zoneEle).append(option);
            }
        });
    },
    submitForm: function() {

    }
};

function Product() {
}