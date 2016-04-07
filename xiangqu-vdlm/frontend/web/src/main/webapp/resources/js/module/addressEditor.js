//define(function(require) {
    var $ = require('jquery');
    //var $ = require('jquery/1.11.1/jquery.js');

    var Address = (function() {
        return function () {
            this.initProvinces = function() {
                var provinceEle = $('#province');
                $.ajax({
                    url: '/zone/1/children',
                    success: function(data) {
                        $.each(data, function(i, item) {
                            provinceEle.append('<option value="' + data[i].id + '">' + data[i].name + '</option>');
                        });
                    }
                });
            }
            this.initCities = function(provinceId) {
                var cityEle = $('#city');
                cityEle.find('option').each(function() {
                    if ($(this).val() > 0) {
                        $(this).remove();
                    }
                });
                $.ajax({
                    url: '/zone/' + provinceId + '/children',
                    success: function(data) {
                        $.each(data, function(index, val) {
                            cityEle.append('<option value="' + val.id + '">' + val.name + '</option>');
                        });
                    }
                });
            }
            this.initDistricts = function(cityId) {
                var districtEle = $('#district');
                districtEle.find('option').each(function() {
                    if ($(this).val() > 0) {
                        $(this).remove();
                    }
                });
                $.ajax({
                    url: '/zone/' + cityId + '/children',
                    success: function(data) {
                        $.each(data, function(index, val) {
                            districtEle.append('<option value="' + val.id + '">' + val.name + '</option>');
                        });
                    }
                });
            }
        }
    })();




    var address = new Address();
    if ($('#id').length == 0) {
        address.initProvinces();
    }

    $('#province').on('change', function() {
        var addressShowDistrict = $(".address-show-district");
        //判断是否是市，去掉地区选择
        if ($(this).val() == "2" || $(this).val() == "20" || $(this).val() == "2465" || $(this).val() == "861") {
            $("#district").hide();
            if (!$(".address-show-district").length == 0) {
                addressShowDistrict && addressShowDistrict.remove('.address-show-district');
            }

        } else {
            $("#district").show();
            if ($(".address-show-district").length == 0) {
                $('.address-show-city').after('<span class="address-show-district"></span>');
            }

        }
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

    $("#phone").bind('blur', function() {
        var regu = /^[1][3-8][0-9]{9}$/;
        var re = new RegExp(regu);
        var phoneobj = $("#phone").val();
        clearTimeout(t);
        $(".phone-msg").css("display", "none");
        if (re.test(phoneobj)) {
            $(".phone-msg").css("display", "none");
        } else {
            $(".phone-msg").css("display", "block");
            var t = setTimeout(function() {
                $(".phone-msg").css("display", "none");
            }, 5000);
        }
    });

    $("#phone").bind('focus', function() {
        $(".phone-msg").css("display", "none");
    });

    $('#addressBtn').bind('click', function() {
        var regu = /^[1][3-8][0-9]{9}$/;
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
        var T;
        clearTimeout(T);
        var consignee = $('#consignee').val();
        var phone = $('#phone').val();
        var zoneId = $('#zoneId').val();
        var street = $('#street').val();
        var params = {
            consignee: consignee,
            phone: phone,
            zoneId: zoneId,
            street: street
        };
            if ($('#addressId').length > 0) {
                params.id = $("#addId").val();
            }


        var url = $('#post_form').attr('action');
        /* $.post(url, params, function() {

         });*/
        $.ajax({
            type:"POST",
            dataType:'json',
            url: url,
            data: params,
            success: function(data) {
                $(".address-show-name").text(data.data.consignee);
                $(".address-show-tel").text(data.data.phone);
                $(".address-show-address").text(data.data.details);
                $("#addressId").val(data.data.id);
                $("#zoneId").val(data.data.zoneId);
                $(".address-modify").hide();
                $(".address-show").show();
            }
        });

    });


//});