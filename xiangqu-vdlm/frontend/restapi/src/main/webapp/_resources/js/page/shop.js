$(document).ready(function() {
        $('.move-footer .no').on('click',function(){
            $('.move-pop').hide();
        });
        $('.move-footer .ok').on('click',function(){
            var params = {
                shopUrl: $('#J-shopUrl').val(),
                option: $('[name="move-type"]:checked').val()
            };
            var href1 = $('#J-href1').val();
            var href2 = $('#J-href2').val();
            $.getJSON(href2, params, function(data) {
                if (data.data) {
                    if (data.data.statusCode != 200) {
                    		alert(data.data.msg);
//                        if (data.data.statusCode == 601) {
//                                alert('店铺不存在');
//                        } else if (data.data.statusCode==501 || data.data.statusCode==502) {
//                                alert('网络错误,搬家失败');
//                        } else {
//                                alert('未知错误,搬家失败');
//                        }
                    } else {
                        location.href = href1;
                    }
                }
            });
        });
        $('#moveAgbtn').bind('click', function() {
            $('.move-pop').show();
            return false;
        });

	
	$('#J-moveProduct-step2').bind('click', function() {
		if ($('#J-itemId').val() == '') {
			alert('商品ID不能为空');
			return false;
		}
		var params = {
			rnd: $('#J-rnd').val(),
			itemId: $('#J-itemId').val()
		};
		var href1 = $('#J-href1').val();
		var href2 = $('#J-href2').val();
		$.getJSON(href1, params, function(data) {
			if (data.data) {
				if (data.data.statusCode==404) {
					alert('验证码已失效，请返回后重新获取');
				}
				else if (data.data.statusCode==200) {
					location.href = href2
						+ '?U=' + (new Date().getTime())
						+ '&shopName=' + data.data.shopName
						+ '&shopType=' + data.data.shopType
						+ '&rnd=' + params.rnd
						+ '&itemId=' + params.itemId;
				} else {
					alert(data.data.msg);
				}
//				else if (data.data.statusCode==601) {
//					alert('网络代理异常，请稍候重试');
//				}
//				else {
//					alert('未找到您的店铺，请确定ID是否正确输入');
//				}
			} else {
				alert(data.moreInfo);
			}
		});
		return false;
	});
});
