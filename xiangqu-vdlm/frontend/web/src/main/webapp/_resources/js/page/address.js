$(document).ready(function() {
    var address = new Address();
    if ($('#id').length == 0) {
    	address.initProvinces();
    }

    $('#province').on('change', function() {
        $('#district').find('option').each(function() {
            if ($(this).val() > 0) {
                $(this).remove();
            }
        });
        address.initCities($(this).val());
//        address.updatePrice();
    });

    $('#city').on('change', function() {
    	address.initDistricts($(this).val());
    });
    
    $("#phone").bind('blur',function(){
        var regu =/^[1][3-8][0-9]{9}$/;
        var re = new RegExp(regu);
        var phoneobj = $("#phone").val();
        clearTimeout(t);
        $(".phone-msg").css("display","none");
        if (re.test(phoneobj)) {
            $(".phone-msg").css("display","none");
        }else{
            $(".phone-msg").css("display","block");
            var t=setTimeout(function() {
                $(".phone-msg").css("display","none");
            },5000);
        }
    });

    $("#phone").bind('focus',function(){
        $(".phone-msg").css("display","none");
    });

    $('#submitBtn').bind('click', function() {
        var regu =/^[1][3-8][0-9]{9}$/;
        var re = new RegExp(regu);
        if ($('#consignee').val() == '') {
            alert('收货人姓名不能为空');
            return false;
        }
        if (!re.test($("#phone").val())) {
            alert('请输入正确的手机号码!');
            return false;
        }
        if ($('#phone').val() == '') {
            alert('手机号码不能为空');
            return false;
        }
        if ($('#province').val() == '0') {
            alert("省份不能为空");
            return false;
        }
        if ($('#city').find('option').length > 1 && $('#city').val() == '0') {
            alert("市不能为空");
            return false;
        }
        if ($('#district').find('option').length > 1 && $('#district').val() == '0') {
            alert("地区不能为空");
            return false;
        }
        if ($('#street').val() == '') {
            alert("街道地址不能为空");
            return false;
        }
        
        $('#zoneId').val($('#district').val() != '0' ? $('#district').val() : ($('#city').val() != '0' ? $('#city').val() : $('#province').val()));

        var consignee = $('#consignee').val();
        var phone = $('#phone').val();
        var zoneId = $('#zoneId').val();
        var street = $('#street').val();
        var weixinId = $('#weixinId').val();
        var params = {
            consignee:consignee,
            phone:phone,
            zoneId:zoneId,
            street:street,
            weixinId:weixinId
        };
        
        if ($('#id').length > 0) {
        	params.id = $('#id').val();
        };
        
        var url = $('#post_form').attr('action');
        $.post(url, params, function() {
        	location.href = $('#backUrl').val();
        });

        if (!$('#submitBtn').is('.disabled')) {
            $('#submitBtn').removeClass('red').addClass('disabled').attr('disabled', 'disabled');
            // $('#order_submit_form')[0].submit();
        }
    });
});

Address.prototype = {
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
        cityEle.find('option').each(function() {
            if ($(this).val() > 0) {
                $(this).remove();
            }
        });
        $.getJSON(url, function(data) {
            $.each(data, function(index, val) {
                cityEle.append('<option value="' + val.id + '">' + val.name + '</option>');
            });
        });
    },

    initDistricts: function(cityId) {
        var url = '/zone/' + cityId + '/children';
        var districtEle = $('#district');
        districtEle.find('option').each(function() {
            if ($(this).val() > 0) {
                $(this).remove();
            }
        });
        $.getJSON(url, function(data) {
            $.each(data, function(index, val) {
                districtEle.append('<option value="' + val.id + '">' + val.name + '</option>');
            });
        });
    }
};

function Address() {
}