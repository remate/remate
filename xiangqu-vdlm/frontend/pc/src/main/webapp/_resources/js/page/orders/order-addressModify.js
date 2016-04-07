//修改信息
define(['jquery','validate','base/utils','plugins/address'],function($,validate,utils,Address){
    //收货人地址省市区初始化
    var zoneId = $('#zoneId').val();
    var address = new Address();
    var strProvince = '';
    var strCity = '';
    var strDistrict = '';
    utils.api.getAddressParent(zoneId,function(data){
        //根据zoneId获取地区parents Id
        strProvince = data.data[1].id;
        strCity = '';
        strDistrict = '';
        if (data.data.length==3) {
            strDistrict = data.data[2].id;
        }
        if (data.data.length==4) {
            strDistrict = data.data[3].id;
        }
        //console.log(strProvince+';'+strCity+';'+strDistrict);
        //获取默认的省市区
        address.init(3,$('#sect_province'),$('#sect_city'),$('#sect_area'),{
            province : strProvince,
            city: strCity,
            district : strDistrict
        });
    });

    //弹窗调用
    $(document).on('click','#J_addressEdit',function(){
        $('.J_addressEditPop label.error').hide(); //清除弹窗刚打开的时候之前遗留的表单验证样式

        $('.J_addressEditPop').modal({
            relatedTarget: this,
            width: '640',
            closeViaDimmer: false, 
            onConfirm: function(options) {
                //点击弹窗的确认按钮时的相关操作
                formEdit();
            },
            onCancel: function() {
                this.close();
            }
        });
    });

    //修改信息并提交表单
    function formEdit(){
        var $formBuyerInfo = $('#form-buyerInfo');
        //自定义规则
        //手机
        $.validator.addMethod("tel", function(value, element) {    
          var length = value.length;    
          return this.optional(element) || (length == 11 && /^1[34578]{1}[0-9]{9}$/.test(value));    
        }, "请正确填写您的手机号码");
        //地区
        $.validator.addMethod("sectArea", function(value, element) {
            //console.log($(element).find('option').length && value || !$(element).find('option').length && !value);
            return $(element).find('option').length && value || !$(element).find('option').length && !value;
        }, "请选择地区");


        $formBuyerInfo.validate({
            debug: true,     
            focusInvalid: true,
            onkeyup: false,
            ignore: '',
            errorPlacement: function(error, element) {  
                error.appendTo(element.parents('li'));  
            },
            submitHandler: function(form) {
                //表单验证通过之后的交互
                
                //组装zoneId
                $('#zoneId').val(Number($('#sect_area').val()) >0 ? $('#sect_area').val() : (Number($('#sect_city').val()) >0 ? $('#sect_city').val() : $('#sect_province').val()));
                var params = {
                    id: $('#orderAddId').val(),
                    orderId: $('#orderId').val(),
                    consignee: $('#J_buyerName').val(),
                    phone: $('#J_buyerTel').val(),
                    weixinId: $('#J_buyerChart').val(),
                    zoneId: $('#zoneId').val(),
                    street: $('#J_addCare').val()
                }
                $.ajax({
                    url: host + '/openapi/order/updateOrderAddress',
                    type: 'POST',
                    data: params,
                    dataType: 'json',
                    success: function(data) {
                        console.log(data);
                        $('.J_addressEditPop').modal('close');
                        if (data.errorCode == 200) {
                            utils.tool.alert('修改成功！');
                            setTimeout(function(){
                                location.reload();
                            },200);    
                        } else {
                            utils.tool.alert(data.moreInfo);
                        }
                        
                    },
                    error: function(state) {
                        if (state.status == 401) {
                            utils.tool.goLogin();
                        } else {
                            utils.tool.alert('服务器暂时没有相应，请稍后重试...');
                        }
                    }
                });
            },
            rules: {
                J_buyerName: {
                    required: true,
                    maxlength: 100
                },
                J_buyerTel: {
                    required: true,
                    tel: true
                },
                J_buyerChart: {
                    maxlength: 40
                },
                J_addCare: {
                    required: true,
                    maxlength: 100
                },
                sect_province: {
                    required: true
                },
                sect_city: {
                    sectArea: true
                },
                sect_area: {
                    sectArea: true
                }
            },
            messages: {
                J_buyerName: {
                    required: '姓名不能为空',
                    maxlength: '最大长度不能超过100个字符'
                },
                J_buyerTel: {
                    required: '电话不能为空',
                    tel: '请输入正确的电话号码'
                },
                J_buyerChart: {
                    maxlength: '微信号码不能超过40个字符'
                },
                J_addCare: {
                    required: '收货地址要尽可能的详细',
                    maxlength: '收货地址最长为100个字符'
                },
                sect_province: {
                    required: '请选择省份'
                },
                sect_city: {
                    sectArea: '请选择城市'
                },
                sect_area: {
                    sectArea: '请选择地区'
                }
            }
        });
    }
    return null;
});