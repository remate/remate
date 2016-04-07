$(document).ready(function() {
	var buyHref="";
    var cart = new Cart();
   // cart.initProvinces();

   //商品数量修改+页面数据 初始化
    pageRequestSet();
    function pageRequestSet(){
        if (!request('qty')) {
            $('#qty').val('0');
        }else{
            $('#qty').val(request('qty'));
        }
    }
 
    $('input.amount').on("change",function() {
        var skuId = $(this).parent().parent().parent().parent().attr('sku_id');
        var amount = $(this).val();
        $('#qty').val(amount);
        if (!request('qty')) {//购物车点击过来
            $('#qty').val('0');
            var params = {
                skuId: skuId,
                amount: amount
            };
            $.getJSON('/cart/update', params, function(data) {
                if (!data.data) {
                    alert(data.moreInfo);
                }
                location.reload();
            });
        }else{//立即购买点击过来
            window.location.href = '/cart/next?skuId=' + skuId+'&qty=' + amount;
        }
    });

	$('a.item-del-btn').each(function() {
		var delBtn = $(this);
		delBtn.click(function() {
			if (confirm('确认要从购物车中删除该商品吗?')) {
				var params = {
					itemId: delBtn.attr('itemId')
				};
				$.getJSON('/cart/delete', params, function(data) {
					location.reload();
				});
			}
		});
	});
	
	//没找到ID为checkout-btn，TODO 要删除?
	$('#checkout-btn').bind('click', function() {
        var regu = /^([0-9]+)$/;
        var re = new RegExp(regu);
        var numed= $(".numed");
        var edge = $(".numPlus").attr("edge");
        if($('input.amount').val() === ""){
            numed.val('1').text('1').trigger('change');
            return false;
        }else if (!re.test(numed.val())) {
            alert('请输入数字');
            numed.val('1').text('1').trigger('change');
            return false;
        }else if(Number(numed.val())<1){
            alert('请输入大于1的商品数字');
            numed.val('1').text('1').trigger('change');
            return false;
        }else if(Number(numed.val())>edge){
            alert('亲，手下留情哦，仓库都被你搬回家拉，所选数量大于库存数量了哦~');
            numed.val(edge).text(edge).trigger('change');
            return false;
        }
		var params = {
			skuId: $('#skuId').val(),
			shopId: $('#shopId').val()
		};
		$.getJSON('/cart/validate', params, function(data) {
			if (data.data) {
				location.href = $('#checkout-btn').attr('href');
			} else {
				alert(data.moreInfo);
			}
		});
		return false;
	});

    var paysubmit=function(){
    	//$('#isdanbao').val($('#danbao').hasClass('pay-selected'));
    	if($('#addressId').length == 0 || $('#addressId').val() == ''){
            alert('亲，地址不能为空，请添加地址！');
            return false;
    	}
        if (!$('#submitBtn').is('.disabled')) {
            $('#submitBtn').removeClass('red').addClass('disabled').attr('disabled', 'disabled');
            $('#order_submit_form')[0].submit();
        }
    };
    $('#submitBtn').bind('click', function() {
        var edge =Number($(".numPlus").attr("edge"));
        var amount =$(".amount");
        var regu = /^([0-9]+)$/;
        var re = new RegExp(regu);

        if($('#pay-danbao').length > 0 && !$('#danbao').hasClass('pay-selected')){
            $('#js-layer').css('display','block');
            $('#js-body').css('display','block');
            return false;
        }
        if(Number(amount.val())>edge){
            alert('亲，手下留情哦，仓库都被你搬回家拉，所选数量大于库存数量了哦~');
            amount.val(edge).text(edge).trigger('change');
            return false;
        }else if (!re.test(amount.val())) {
            alert('请输入数字');
            numed.val('1').text('1').trigger('change');
            return false;
        }
        paysubmit();
    });
    $('.l-next').click(function(){  //担保交易  继续按钮
        $('#js-layer').css('display','none');
        $('#js-body').css('display','none');
        paysubmit();
    });
});

Cart.prototype = {
    addToCart: function() {
        var sellerId = $('input[name=sellerId]').val();
        var productId = $('input[name=productId]').val();
        var skuId = $('input[name=skuId]').val();
        var amount = $('input[name=amount]').val();
        var params = {
            'sellerId': sellerId,
            'productId': productId,
            'skuId': skuId,
            'amount':amount
        };
        var url = '/cart/add';
        $.post(url, params, function(data) {
//            console.log(log);
        });
    },
    // TODO 此方法address.js里面有重复，要重构
    updatePrice: function() {
    	//var zoneId = $('#district').val() != '0' ? $('#district').val() : $('#city').val();
    	var zoneId = $('#district').val() != '0' ? $('#district').val() : ($('#city').val() != '0' ? $('#city').val() : $('#province').val());
    	var promotionId = $('[name=promotionId]:checked').length > 0 ? $('[name=promotionId]:checked').val() : '';
    	var couponId = $('[name=couponId]:checked').length > 0 ? $('[name=couponId]:checked').val() : '';
    	var params = {
			skuIds: $('#skuId').val(),
			shopId: $('#shopId').val(),
			zoneId: zoneId,
			promotionId: promotionId,
			couponId: couponId
		};
		$.getJSON('/cart/pricing', params, function(data) {
			if (data.data) {
				var prices = data.data;
				$("#goodsFee").text($("#goodsFee").text().replace(/\d+[\.\d,]*/g, prices.goodsFee));
				$("#logisticsFee").text($("#logisticsFee").text().replace(/\d+[\.\d,]*/g, prices.logisticsFee));
				$("#discountFee").text($("#discountFee").text().replace(/\d+[\.\d,]*/g, prices.discountFee));
				$("#totalFee").text($("#totalFee").text().replace(/\d+[\.\d,]*/g, prices.totalFee));
			} else {
				alert(data.moreInfo);
			}
		});
    }
};

function Cart() {
}

/* 获取URL参数 */
function request(paras){
    var url = location.href;
    var paraString = url.substring(url.indexOf("?")+1,url.length).split(/\&|\#/g);
    var paraObj = {}
    for (i=0; j=paraString[i]; i++){
        paraObj[j.substring(0,j.indexOf("=")).toLowerCase()] = j.substring(j.indexOf("=")+1,j.length);
    }
    var returnValue = paraObj[paras.toLowerCase()];
    if(typeof(returnValue)=="undefined"){
       return "";
    }else{
        return returnValue;
    }
}
