location.hash = ''; 
$('.state-modify').css('min-height',$(window).height());

$(window).resize(function() {
    $('.j_frame_detail').find('iframe').width($(window).width())
        .height($(window).height());
});

function formatCurrency(num) {  
    num = num.toString().replace(/\$|\,/g,'');  
    if(isNaN(num))  
        num = "0";  
    sign = (num == (num = Math.abs(num)));  
    num = Math.floor(num*100+0.50000000001);  
    cents = num%100;  
    num = Math.floor(num/100).toString();  
    if(cents<10)  
    cents = "0" + cents;  
    for (var i = 0; i < Math.floor((num.length-(1+i))/3); i++)  
    num = num.substring(0,num.length-(4*i+3))+','+  
    num.substring(num.length-(4*i+3));  
    return (((sign)?'':'-') + num + '.' + cents);  
} 

function closeFrame() {
    $('body').removeClass('callIframe');
    $('.j_frame_detail').hide();
}

function callFrame(){
    var $tar = $('.j_frame_detail');
    $('body').addClass('callIframe');
    $tar.find('iframe').width($(window).width()).height($(window).height());
    $tar.show();
}


$(window).on('hashchange',function(){
    var hs = location.hash;
    if( hs ==  '#address'){
        //show 地址编辑
        $('.state-modify').show();
        $('.state-show').hide();
        $('#main').removeClass('bg_gray');
    } else if(hs == '#youhui'){
        callFrame();
    } else{
        $('.state-modify').hide();
        $('.state-show').show();
        closeFrame();
        $('#main').addClass('bg_gray');
    }
});

//点击优惠码回调
function frameCallBack(arr) {
    //选中优惠券展示
    var html ='';
    $('.cartFavour-link ul').html('');
    if (arr.length) {
        for (var i = 0; i < arr.length; i++) {
            html += '<li data-id="'+ arr[i].id +'" data-coupon-type="'+ arr[i].couponType + '" data-extCouponId="' + arr[i].extCouponId + '" data-numb="' + arr[i].numb +'" data-selected="' + arr[i].selected +'">' +arr[i].title+'('+arr[i].numb+'元)</li>';
        };
        $('.cartFavour-link ul').html(html);
        countPrice();
    }else{
        countPrice();
        html = '<li>去选择您的优惠</li>';
        $('.cartFavour-link ul').html(html);
    }
    
    //价格计算
    
    location.hash = ''; 
}

function frameCallBack_address(data){
    var htmls = '<input type="hidden" id="addressId" name="addressId" value="'+ data.id +'"/><div class="cartUser pos-r">' + 
                '<a class="js-editAddress pos-a" href="#address"><b></b></a>' +
                '<span>' + data.consignee + '</span>' + 
                '</div>' + 
                '<p>' + data.details + '</p>' + 
                '<p>'+ data.phone +'</p>' + 
                '<div class="bird pos-a"></div>';
    $('.cartAddress').html(htmls);

    closeFrame();
}

//优惠价格计算
function countPrice(){
     var rate = 0;
     var lastRate = Number($('.js-totalFee').data('price'));
     $('.cartFavour-link ul li').each(function(index,em){
         var _this = $(em);
         if (_this.find('input').length) {
            return false;
         }
         if (_this.attr('data-numb')) {
            rate += Number(_this.attr('data-numb'));
         _this.append('<input type="hidden" name="coupon['+index+'].activityId" value="'+ _this.attr('data-coupon-type') +'" /><input type="hidden" name="coupon['+index+'].extCouponId" value="'+ _this.attr('data-extcouponid') +'" /><input type="hidden" name="coupon['+index+'].id"  value="'+ _this.attr('data-id') +'" /><input type="hidden" name="coupon['+index+'].discount" value="'+ _this.attr('data-numb') +'" />');
         }
     });
     lastRate = Math.max(Number((lastRate*100-rate*100)/100),0);
     $('.js-totalFee').html('¥' + vd_tool.formatCurrency( lastRate ) ); 
}

//如果默认有优惠

if( $('.cartFavour-link ul li').length){
    countPrice();
}else{
    $('.cartFavour-link ul').append('<li>去选择您的优惠</li>');
}


//修改地址部分
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

    $('#subAddress').bind('click', function() {
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
        var skuid = $('input[name="skuIds"]').val();
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
        $.post(url, params, function(data) {
            try{
                var data = JSON.parse(data);
                console.log(data);
                frameCallBack_address(data.data);
                location.hash = ''; 
            }catch(e){
                alert('服务器好像出了点问题，请稍后重试～');
            }
            //location.href = $('#backUrl').val();
        });

        //请求数据，动态填充邮费
        getShopPriceJson({
            zoneId: zoneId,
            skuIds: skuid,
            qty: $('#qty').val()
        });

        if (!$('#subAddress').is('.disabled')) {
            $('#subAddress').removeClass('red').addClass('disabled').attr('disabled', 'disabled');
            // $('#order_submit_form')[0].submit();
        }
    });
});

function getShopPriceJson(param) {
    $.ajax({
        url: '/cart/pricing-groupby-shop',
        data: param,
        traditional: true,
        success: function(data) {
            console.log(data);
            try{
                var data = JSON.parse(data);
            }catch(e){
                alert('服务器好像出了点问题，请稍后重试～');
            }
            if (data.errorCode == 200) {
                $('.cartItem').each(function(index,em){
                    var shopid = $(em).attr('data-shop-id');
                    $(em).find('.cartTransport .fl-r span').text(Number(data.data.pricesMap[shopid].logisticsFee).toFixed(2));
                    $(em).find('.amountPrice span').text(Number(data.data.pricesMap[shopid].totalFee).toFixed(2));
                });
                $('.cartTotal .cart-red b').attr({'data-goodsfee':Number(data.data.goodsFee).toFixed(2),'data-price':Number(data.data.totalFee).toFixed(2)});
                $('.cartTotal .cart-red b').text(Number(data.data.totalFee).toFixed(2));
                $('.cartTotal .postage b').text('￥' + Number(data.data.logisticsFee).toFixed(2)) ;
                countPrice();
            } else {
              alert(data.moreInfo);
            }
        }
    });
}

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
    },
};

function Address() {
}