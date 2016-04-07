$(document).ready(function() {
	$('#payBtn').bind('click', function() {
		
    	if($('#addressId').length == 0 || $('#addressId').val() == ''){
            alert('亲，地址不能为空，请添加地址！');
            return false;
    	}
		
		if ($('#payType').val() == '') {
            alert("请选择一种付款方式");
            return false;
        }
        
        if (!$('#payBtn').is('.disabled')) {
            $('#payBtn').addClass('disabled').attr('disabled', 'disabled');
            $('#order_pay_form')[0].submit();
        }
	});
});