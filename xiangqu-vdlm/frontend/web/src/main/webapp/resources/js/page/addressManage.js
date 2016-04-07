/**
 * Created by Think on 2016/1/7.
 */
(function(){
    var $ = require('jquery');
    var template = require('../plugins/template.js');

    var isTest = location.host.indexOf('dev.www.xiangqutest.com') > -1 ? true :false;
    isTest = false;
    var App = {
        city:isTest ? '/resources/js/testData/addressList/city.json' :'/zone/children',//集联
        saveAddress:isTest ? "" : '/address/save',//新建地址保存
        myAddressList:isTest ? '/resources/js/testData/addressList/list.json' : '/address/list',//收货地址列表

        defaultAddress: isTest ? '/resources/js/testData/addressList/defaultAddress.json' :'/address/asDefault',//设置默认地址
        updateAddress:isTest ? "" :'/address/update'//更新地址
    };
    var MyAddressManage = {
        init:function(){
            var self = this;
            self.J_myAddressBox = $('.J_myAddressBox');
            self.J_editAddressBox = $('.J_editAddressBox');
            self.J_citiesCheck = self.J_editAddressBox.find('.J_citiesCheck');
            self.J_addRessObj = $('.J_addressObj');
            self.eventHanding();

            self.addressList = [];

        },
        eventHanding:function(){
            var self = this;
            //收货地址列表
            self.getAddressListData();

            //渲染省份
            self.initAddressData({
                id:1
            },function(rs){
                if(rs.code == 200){
                    var _html = self.initOption(rs.data);
                    self.J_citiesCheck.find('.J_province').append(_html);
                }
            });

            //渲染市
            self.J_citiesCheck.find('.J_province').on('change',function(e){
               var _id = $(this).val();
                if(_id == 2 || _id == 20 || _id == 2465 || _id == 861){
                    self.J_citiesCheck.find('.J_district').hide();
                }else{
                    self.J_citiesCheck.find('.J_district').show();
                }

                if(_id == 0){
                    self.J_citiesCheck.find('.J_city option[value!=0]').remove();
                    self.J_citiesCheck.find('.J_district option[value!=0]').remove();
                    return false;
                }

                self.initAddressData({id:_id},function(rs){
                    if(rs.code == 200){
                        var _html = self.initOption(rs.data);
                        self.J_citiesCheck.find('.J_city option[value!=0]').remove();
                        self.J_citiesCheck.find('.J_district option[value!=0]').remove();
                        self.J_citiesCheck.find('.J_city option[value=0]').attr('selected',true);
                        self.J_citiesCheck.find('.J_city').append(_html);
                    }
                });

            });

            //渲染地区
            self.J_citiesCheck.find('.J_city').on('change',function(){
                var _id = $(this).val();
                if(_id == 0){
                    self.J_citiesCheck.find('.J_district option[value!=0]').remove();
                    return false;
                }

                self.initAddressData({id:_id},function(rs){
                   if(rs.code = 200){
                       var _html = self.initOption(rs.data);
                       self.J_citiesCheck.find('.J_district option[value!=0]').remove();
                       self.J_citiesCheck.find('.J_district option[value=0]').attr('selected',true);
                       self.J_citiesCheck.find('.J_district').append(_html);
                   }
                });

            });


            //显示全部地址
            self.J_myAddressBox.on('click','.J_showMoreAddress',function(){

                if($(this).attr('data-isshow') == 0){
                    self.J_myAddressBox.find('.address-box ul li[class*=hideitem]').css('display','block');
                    $(this).attr('data-isshow','1').html('显示默认地址');
                }else{
                    self.J_myAddressBox.find('.address-box ul li[class*=hideitem]').removeAttr('style');
                    $(this).attr('data-isshow','0').html('显示全部地址');
                }

            });
            //添加一个新地址
            self.J_myAddressBox.on('click','.J_addNewAddress',function(e){
                e.preventDefault();
                self.J_editAddressBox.find('.edit-title h3').html('添加新地址');
                self.J_editAddressBox.show();
            });

            //关闭地址编辑
            self.J_editAddressBox.on('click','.J_clearEditAddress',function(e){
                e.preventDefault();
                self.clearFrom();
                self.J_editAddressBox.hide();
            });

            //设置为默认地址
            self.J_myAddressBox.on('click','.J_setDefault',function(e){
                e.preventDefault();
                var that = this;
                if($(this).hasClass('active')){
                    return false;
                }
                self.setDefaultSite(that);
            });
            //设置当前发货地址
            self.J_myAddressBox.on('click','.J_checkAdd',function(e){
                e.preventDefault();
                if($(this).hasClass('active')){
                    return false;
                }else{
                    $(this).siblings('.J_checkAdd').removeClass('active');
                    $(this).addClass('active');

                    //设置收货人
                    var _pcd ='';
                    var J_span =  $(this).find('.address span');
                    for(var i = 0,len = J_span.length; i< len; i++ ){
                        _pcd += J_span.eq(i).html();
                    }
                    var _strAddress = _pcd + $(this).find('.address .street').html(),
                        _strName = $(this).find('.name').attr('data-name') + ' ' + $(this).find('.tel').html();

                    self.J_addRessObj.find('.address-cont em').html(_strAddress);
                    self.J_addRessObj.find('.receive em').html(_strName);

                    $('#id-cart').find('#addressId').val($(this).find('.J_editAddress').attr('data-addressid'));


                }
            });
            //编辑地址
            self.J_myAddressBox.on('click','.J_editAddress',function(e){
                e.preventDefault();
                var that = this;
                self.editAddress(that);
            });
            //新建地址
            self.J_editAddressBox.on('click','.J_editAddressSave',function(e){
                e.preventDefault();
                var isNew = self.J_editAddressBox.attr('data-addressid') ? false:true;
                var arg = {
                    url:(function(){
                        return isNew ? App.saveAddress : App.updateAddress;
                    })(),
                    data:{
                        addressId:(function(){
                           return isNew ? '': self.J_editAddressBox.attr('data-addressid');
                        })(),
                        consignee: $.trim(self.J_editAddressBox.find('.J_userName').val()),
                        phone: $.trim(self.J_editAddressBox.find('.J_userTel').val()),
                        zoneId:(function(){
                            return self.J_citiesCheck.find('.J_district').val() != 0 ? self.J_citiesCheck.find('.J_district').val() : self.J_citiesCheck.find('.J_city').val() != 0 ? self.J_citiesCheck.find('.J_city').val() : self.J_citiesCheck.find('.J_province').val();
                        })(),
                        street: $.trim(self.J_editAddressBox.find('.J_addressInfo').val()),
                        zipcode: $.trim(self.J_editAddressBox.find('.J_zipcode').val()),
                        isDefault:(function(){
                           return self.J_editAddressBox.find('.J_defaultAddress:checked').length > 0 ?true:false;
                        })(),
                        t: $.now()
                    }
                };
                if(arg.data){

                    if(self.J_citiesCheck.find('.J_province').val() == 0){
                        alert('省份不能为空!');
                        return false;
                    }
                    if(self.J_citiesCheck.find('.J_city option').length >1 && self.J_citiesCheck.find('.J_city').val() == 0){
                        alert('市不能为空!');
                        return false;
                    }

                    if(self.J_citiesCheck.find('.J_district option').length >1 && self.J_citiesCheck.find('.J_district').val() == 0){
                        alert('地区不能为空!');
                        return false;
                    }

                    if(!arg.data.street){
                        alert('详细地址不能为空!');
                        return false;
                    }
                    if(arg.data.street > 100){
                        alert('详细地址最长100个字！');
                        return false;
                    }
                    if(!arg.data.consignee){
                        alert('姓名不能为空!');
                        return false;
                    }
                    if(arg.data.consignee.length >30){
                        alert('姓名最长20个字!');
                        return false;
                    }
                    if(!arg.data.phone){
                        alert('请填写手机号码!');
                        return false;
                    }
                    if(!self.checkPhone(arg.data.phone,false)){
                        alert('手机号码不正确！');
                        return false;
                    }
                    if(arg.data.zipcode){
                        if(self.reZipCode(arg.data.zipcode)){
                            alert('邮政编码格式不正确！');
                            return false;
                        }
                    }

                }

                self.sendEditAddressData(arg,function(){
                    self.clearFrom();
                    self.J_editAddressBox.hide();
                });
            });

        },
        getAddressListData:function(callBack){
            var self = this;

            $.ajax({
                url:App.myAddressList + '?t=' + $.now(),
                type:'get',
                dataType:'json',
                success:function(rs){

                    if(rs.code != 200){
                        alert(rs.msg);
                        return false;
                    }

                    if(rs.data.length){
                        var b = [];
                        rs.data.sort(function(a,b){
                            return a.createdAt - b.createdAt;
                        });

                         self.addressList.length = 0;

                        for(var i = 0,len = rs.data.length; i < len; i++){
                            if(rs.data[i].isDefault){
                                self.addressList.unshift(rs.data[i]);
                            }else{
                                self.addressList.push(rs.data[i]);
                            }
                        }
                        //b = self.addressList.slice(0,3);
                        b = self.addressList;
                        var _data = {
                            _data:b
                        }

                        var _temp = template('J_tempAddressList',_data);
                        self.J_myAddressBox.find('ul').empty().append(_temp);

                        if(self.addressList.length){
                            var _address = self.addressList[0].details.replace('中国','');
                            var _name = self.addressList[0].consignee + ' ' + self.addressList[0].phone;
                            self.J_addRessObj.find('.address-cont em').html(_address);
                            self.J_addRessObj.find('.receive em').html(_name);
                            $('#id-cart').find('#addressId').val(self.addressList[0].id);
                        }

                        if(callBack){
                            callBack();
                        }
                    }
                }
            });
        },
        editAddress:function(ele){
            var self = this;
            var J_item = $(ele).closest('li');
            var name = J_item.find('.name').attr('data-name') || '',
                add = J_item.find('.street').html() || '',
                zipCode = J_item.find('.J_editAddress').attr('data-zipcode') || '',
                tel = J_item.find('.tel').html() || '',
                isDefault = J_item.find('.J_setDefault').hasClass('active')?true:false,
                addressId =  J_item.find('.J_editAddress').attr('data-addressid')|| '',
                provinceId = J_item.find('.address span').eq(0).attr('data-id')|| '',
                cityId = J_item.find('.address span').eq(1).attr('data-id')|| '',
                disId = J_item.find('.address span').eq(2).attr('data-id')|| '';

            self.J_editAddressBox.attr('data-addressid',addressId);
            self.J_editAddressBox.find('.J_userName').val(name);
            self.J_editAddressBox.find('.J_userTel').val(tel);
            self.J_editAddressBox.find('.J_addressInfo').val(add);
            self.J_editAddressBox.find('.J_zipcode').val(zipCode);

            if(isDefault){
                if(!self.J_editAddressBox.find('.J_defaultAddress').is(':checked')){
                    self.J_editAddressBox.find('.J_defaultAddress').click();
                }
            }else{
                if(self.J_editAddressBox.find('.J_defaultAddress').is(':checked')){
                    self.J_editAddressBox.find('.J_defaultAddress').click();
                }
            }

            //省市区设置
            if(provinceId){
                if(provinceId == 2 || provinceId ==  20 || provinceId ==  2465 || provinceId ==  861){
                    self.J_citiesCheck.find('.J_district').hide();
                }else{
                    self.J_citiesCheck.find('.J_district').show();
                }
                self.J_citiesCheck.find('.J_province option[value='+ provinceId +']').attr('selected',true);

                self.initAddressData({id:provinceId},function(rs){
                   if(rs.code == 200){
                       var _html = self.initOption(rs.data);
                       self.J_citiesCheck.find('.J_city').append(_html);
                       self.J_citiesCheck.find('.J_city option[value='+ cityId +']').attr('selected',true);
                       if(disId){
                           self.initAddressData({id:cityId},function(rs){
                               if(rs.code == 200){
                                   var _html = self.initOption(rs.data);
                                   self.J_citiesCheck.find('.J_district').append(_html);
                                   self.J_citiesCheck.find('.J_district option[value='+ disId +']').attr('selected',true);
                               }
                           });
                       }
                       self.J_editAddressBox.find('.edit-title h3').html('编辑地址');
                       self.J_editAddressBox.show();
                   }
                });

            }



        },
        initAddressData:function(arg,callBack){
            var self = this;
            arg = arg || {};
            $.ajax({
                //url:"/zone/" + arg.id + "/children",
                url:App.city,
                type:'get',
                data:{
                    id:arg.id,
                    t: $.now()
                },
                dataType:'json',
                success:function(rs){
                    if(callBack){
                        callBack(rs);
                    }
                }
            })
        },
        initOption:function(rs){
            var self = this;
            var _temp = '';
            if(rs.length){
                for(var i = 0,len = rs.length; i < len; i++){
                    _temp += '<option value="' + rs[i].id + '">' + rs[i].name + '</option>';
                }
            }
            return _temp;
        },
        setDefaultSite:function(ele){
            var slef = this;
            //设置默认地址
            $.ajax({
                url:App.defaultAddress,
                type:'get',
                data:{
                    addressId:$(ele).siblings('span').attr('data-addressid'),
                    t: $.now()
                },
                dataType:'json',
                success:function(rs){
                    if(rs.code != 200){
                        alert(rs.msg);
                        return false;
                    }
                    $(ele).closest('li').siblings('li').removeClass('default').find('.J_setDefault').removeClass('active');
                    $(ele).closest('li').addClass('default').find('.J_setDefault').addClass('active');
                }
            })
        },
        sendEditAddressData:function(arg,callBack){
            var self = this;
            //编辑地址
            arg = arg || {};
            var _data = arg.data;
            $.ajax({
                url:arg.url,
                type:'post',
                data:_data,
                dataType:'json',
                success:function(rs){
                    if(rs.code != 200){
                        alert(rs.msg);
                        return false;
                    }
                    self.getAddressListData();
                    if(callBack){
                        callBack();
                    }
                }
            })
        },
        clearFrom:function(){
          var self = this;
            self.J_editAddressBox.removeAttr('data-addressid');
            self.J_editAddressBox.find('.J_userName').val('');
            self.J_editAddressBox.find('.J_userTel').val('');
            self.J_editAddressBox.find('.J_province option:selected').removeAttr('selected');
            self.J_editAddressBox.find('.J_city option[value!=0]').remove();
            self.J_editAddressBox.find('.J_district option[value!=0]').remove();
            self.J_editAddressBox.find('.J_addressInfo').val('');
            self.J_editAddressBox.find('.J_zipcode').val('');
            self.J_editAddressBox.find('.J_defaultAddress').attr('checked',false);
        },
        reZipCode:function(str){
            var self = this;
            //验证邮编
            var re = /^[1-9][0-9]{5}$/g;
            if(str){
                if(re.test(str)){
                    return false;
                }else{
                    return true;
                }
            }
        },
        checkPhone:function(str,flag){
            //手机号或电话码验证
            //var re = /^1\d{10}|(\([0-9]+\))?[0-9]{7,8}$/;
            //flag ？true 手机电话：手机
            var self = this;
            var re;

            if(flag){
                re = /^((\+?86)|(\(\+86\)))?1\d{10}|(\([0-9]+\))?[0-9]{7,8}$/g;
            }else{
                re = /^((\+?86)|(\(\+86\)))?1\d{10}$/g;
            }

            if(re.test(str)){
                return true;
            }else{
                return false;
            }
        }
    };

    $(function(){
       MyAddressManage.init();
    });

})();


