$(document).ready(function() {
//	$('#province').bind('click', function() {
//		console.log('province');
//		var cart = new Cart();
//		cart.updateZoneSelector(this, 1);
//	});
	
	var cart = new Cart();
	cart.initProvinces();
	$('#province').on('change', function() {
		$('#district').find('option').each(function() {
			if ($(this).val() > 0) {
				$(this).remove();
			}
		});
		cart.initCities($(this).val());
    });
	
	$('#city').on('change', function() {
		cart.initDistricts($(this).val()); 
    });
	
	$('input.amount').change(function() {
		var skuId = $(this).parent().parent().parent().parent().attr('sku_id');
		var amount = $(this).val();
		var params = {
			skuId: skuId,
			amount: amount
		};
		$.getJSON('/cart/update', params, function(data) {
			if (!data.data) {
				alert(data.moreInfo);
			}
			location.reload();
//			$('#goodsFee').text(data.data.goodsFee);
//			$('#logisticsFee').text(data.data.logisticsFee);
//			$('#discountFee').text(data.data.discountFee);
//			$('#totalFee').text(data.data.totalFee);
		});
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
	
	$('#checkout-btn').bind('click', function() {
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
	
	$('#submitBtn').bind('click', function() {
		if ($('#consignee').val() == '') {
			alert('签收人不能为空');
			return false;
		}
		if ($('#phone').val() == '') {
			alert('手机号码不能为空');
			return false;
		}
		if ($('#province').val() == '0') {
			alert("省份不能为空");
			// $('#province').setCustomValidity('省份不能为空');
			return false;
		}
		if ($('#city').val() == '0') {
			alert("市不能为空");
			// $('#city').setCustomValidity('市不能为空');
			return false;
		}
		if ($('#district').find('option').length > 1 && $('#district').val() == '0') {
			alert("地区不能为空");
			// $('#district').setCustomValidity('地区不能为空');
			return false;
		}
		if ($('#street').val() == '') {
			alert("街道地址不能为空");
			// $('input[name=street]').setCustomValidity('街道地址不能为空');
			return false;
		}
		
		$('#zoneId').val($('#district').val() != '0' ? $('#district').val() : $('#city').val());
		
//		$.ajax('/cart/validate', function(data) {
//			if (data.data) {
//				$('#order_submit_form').submit();
//			} else {
//				alert(data.moreInfo);
//			}
//		});
		// $('#order_submit_form').submit();
		if (!$('#submitBtn').is('.disabled')) {
			$('#submitBtn').removeClass('red').addClass('disabled').attr('disabled', 'disabled');
			$('#order_submit_form')[0].submit();
		}
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
			console.log(log);
		});
	},
	
	updateZoneSelector: function(zoneEle, parentZoneId) {
		var url = '/zone/' + parentZoneId + '/children';
		$.getJSON(url, function(data) {
			for (var i = 0; i < data.length; i++) {
				var option = '<option value="' + data[i].id + '">' + data[i].name + '</option>';
				$(zoneEle).append(option);
			}
		});
	},
	
	initProvinces: function() {
		var url = '/zone/1/children';
		var provinceEle = $('#province');
		$.getJSON(url, function(data) {
			$.each(data, function(i, item) {
				provinceEle.append('<option value="' + data[i].id + '">' + data[i].name + '</option>');
			});
		});
	},
	
	initCities: function(provinceId) {
		var url = '/zone/' + provinceId + '/children';
		var cityEle = $('#city');
		$.getJSON(url, function(data) {
			cityEle.find('option').each(function() {
				if ($(this).val() > 0) {
					$(this).remove();
				}
			});
			$.each(data, function(index, val) {
				cityEle.append('<option value="' + val.id + '">' + val.name + '</option>');
			});
		});
	}, 
	
	initDistricts: function(cityId) {
		var url = '/zone/' + cityId + '/children';
		var districtEle = $('#district');
		$.getJSON(url, function(data) {
			$.each(districtEle.find('option'), function(i, item) {
				if (i > 0) {
					item.remove();
				}
			});
			$.each(data, function(i, item) {
				districtEle.append('<option value="' + data[i].id + '">' + data[i].name + '</option>');
			});
		});
	}
};

function Cart() {
}