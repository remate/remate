$(document).ready(function() {
	$('#addtocart').bind('click', function() {
		var product = new Product();
		product.addToCart();
	});
	$('#buy').bind('click', function() {
		var product = new Product();
		product.buy();
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
		var sellerId = $('#sellerId').val();
		var productId = $('#productId').val();
		var skuId = $('#skuId').val();
		var amount = $('input[name=amount]').val();
		if (!skuId) {
			alert('请选择规格');
			return false;
		}
		return true;
	});

	var prodOptions = $('.prodOption').click(function() {
		var self = $(this);
		prodOptions.removeClass('selected');
		self.addClass('selected');
		$('#skuId').val(self.attr('sku_id'));
		$('#inventory_amount').text(self.attr('sku_amount'));
		$('#price').text(self.attr('sku_price'));
	});
});

Product.prototype = {
	addToCart: function() {
		var sellerId = $('#sellerId').val();
		var productId = $('#productId').val();
		var skuId = $('#skuId').val();
		var amount = $('input[name=amount]').val();
		if (!skuId) {
			alert('请选择规格');
			return;
		}
		var params = {
			'sellerId': sellerId,
			'productId': productId,
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
		var amount = $('input[name=amount]').val();
		if (!skuId) {
			alert('请选择型号');
			return;
		}
		var params = {
			'skuId': skuId,
			'amount': amount
		};
		$.post("/cart/update", params, function(r) {
			var data = $.parseJSON(r);
			if (data.data) {
				location.href = "/cart?skuId=" + skuId;
			} else {
				alert(data.moreInfo);
			}
		});
	},
	selectProvince: function() {
		var url = '/zone/1/children';
		$.getJSON(url, function(data) {
			for (var i = 0; i < data.length; i++) {
				console.log(data[i]);
				var option = '<option value="' + data[i].id + '">' + data[i].name + '</option>';
				$('#province').append(option);
			}
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
	submitForm: function() {
		
	}
};

function Product() {
}