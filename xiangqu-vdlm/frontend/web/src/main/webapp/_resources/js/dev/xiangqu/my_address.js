/**
 * Created by ziyu on 2016/1/22.
 */
(function(){

    var isTest = location.host.indexOf('dev.www.xiangqutest.com') > -1 ? true :false;
    isTest = false;
    var App = {
        city:isTest ? '/resources/js/testData/addressList/city.json' :'/zone/children',//集联
        saveAddress:isTest ? "" : '/address/save',//新建地址保存
        myAddressList:isTest ? '/resources/js/testData/addressList/list.json' : '/address/list',//收货地址列表
        delAddress:isTest ? '':'/address/delete',
        defaultAddress: isTest ? '/resources/js/testData/addressList/defaultAddress.json' :'/address/asDefault',//设置默认地址
        updateAddress:isTest ? "" :'/address/update',//更新地址
        getCalculateFee:isTest ? '/_resources/testdata/calculateFee.json' : '/xiangqu/wap/order/calculateFee'//获取定单信息
    };

    var MyAddressMangeW = {
        init:function(){
            var self = this;
            self.J_wapOrder = $('.J_wapOrder');
            self.J_myAddressBoxShop = self.J_wapOrder.find('.J_myAddressBoxShop');//改交互

            self.J_myAddressLists = self.J_wapOrder.find('.J_myAddressLists');
            self.J_noAddress = self.J_myAddressLists.find('.J_noAddress');
            self.J_haveAddress = self.J_myAddressLists.find('.J_haveAddress');

            self.J_tc = $('body').find('.J_tc');//弹出层

            self.isqty = location.href.indexOf('qty=') > -1?true:false;//是否是立即下单
            //console.log(self.isqty);

            self.addressList = [];//用户收货地址
            self.isfirst = true;
            self.browser();
            self.eventHanding();

        },
        eventHanding:function(){
            var self = this;

            //是否是weixin
            self.isWeiXin();

            //是否是立即下单
            if(self.isqty){
                self.J_wapOrder.find('.J_applyNow').show();
            }

            self.J_wapOrder.find('.J_applyNow').on('click','.J_jian',function(e){
                e.preventDefault();
                e.stopPropagation();
                var _currentNum = Number($(this).siblings('.J_currNum').html());
                var that = this;
                if(_currentNum <=1){
                    alert('商品数量不能小于1件！');
                    return false;
                }else{
                    _currentNum--;
                    //$(this).siblings('.J_currNum').html(_currentNum);
                    $(that).closest('li').find('.product-item-body').attr('data-amount',_currentNum);
                    //self.reloadUrl('qty=',_currentNum);
                    //self.deratePrice(_currentNum);
                    self.resetIqyPrice(_currentNum,function(rs){
                        if(rs.code == 200){
                            $(that).siblings('.J_currNum').html(_currentNum);
                            self.J_wapOrder.find('.J_productLists .product-price .num').html('x' + _currentNum);
                            self.J_wapOrder.find('.J_productLists .J_amountPrice .product-num em').html(_currentNum);
                            self.J_wapOrder.find('#qty').val(_currentNum);
                        }
                    });
                }


            });

            self.J_wapOrder.find('.J_applyNow').on('click','.J_add',function(e){
                e.preventDefault();
                e.stopPropagation();
                var _currentNum = Number($(this).siblings('.J_currNum').html());
                var _totalNum = Number($(this).attr('data-amount'));
                var that = this;
                _currentNum++;
                if(_currentNum > _totalNum){

                    alert('当前库存小于'+ _currentNum +'件!');
                    _currentNum--;
                    return false;
                }

                //self.reloadUrl('qty=',_currentNum);
                //self.deratePrice(_currentNum);
                $(that).closest('li').find('.product-item-body').attr('data-amount',_currentNum);

                self.resetIqyPrice(_currentNum,function(rs){
                   if(rs.code == 200){
                       self.J_wapOrder.find('.J_productLists .product-price .num').html('x' + _currentNum);
                       self.J_wapOrder.find('.J_productLists .J_amountPrice .product-num em').html(_currentNum);
                       $(that).siblings('.J_currNum').html(_currentNum);
                       self.J_wapOrder.find('#qty').val(_currentNum);
                   }
                });


            });

            //初次获取收货地址
            self.getAddressListData(function(){
                self.renderAddress();
            });

            //新建地址
            self.J_noAddress.on('click',function(e){
                e.preventDefault();
                e.stopPropagation();
                //$('body').css('overflow','hidden');
                //self.J_tc.show();
                //self.J_tc.find('.J_tcAddressManage').show();

                self.J_wapOrder.find('.J_home').hide();
                self.J_myAddressBoxShop.find('.J_tcAddressManage').show();
                self.J_myAddressBoxShop.show();
            })

            //已有地址管理
            self.J_haveAddress.on('click',function(e){
                e.preventDefault();
                e.stopPropagation();
                if(self.J_myAddressBoxShop.attr('data-isempty') == 0){
                    self.J_wapOrder.find('.J_home').hide();
                    self.J_myAddressBoxShop.find('.J_tcMyAddress').show();
                    self.J_myAddressBoxShop.show();

                    //$('body').css('overflow','hidden');
                    return false;
                }
                self.renderAddress();


            });

            //关闭地址管理弹出层
            self.J_myAddressBoxShop.find('.J_tcMyAddress').on('click','.J_closeAddressTc',function(e){
                e.preventDefault();
                e.stopPropagation();
               //self.J_myAddressBoxShop.removeAttr('style');
               self.J_myAddressBoxShop.hide();
                self.J_myAddressBoxShop.find('.J_tcMyAddress').hide();
                self.J_wapOrder.find('.J_home').show();

            });

            //添加新地址
            self.J_myAddressBoxShop.find('.J_tcMyAddress').on('click','.J_tcAddressBtn',function(e){
                e.preventDefault();
                e.stopPropagation();
                self.J_myAddressBoxShop.find('.J_tcMyAddress').hide();
                self.J_myAddressBoxShop.find('.J_tcAddressManage').show();

            });

            //关闭添加新地址弹出层
            self.J_myAddressBoxShop.find('.J_tcAddressManage').on('click','.J_closeAddressTc',function(e){
                e.preventDefault();
                e.stopPropagation();
                $(this).closest('.J_tcAddressManage').hide();
                self.J_myAddressBoxShop.find('.J_tcMyAddress').show();
                self.clearFrom();
            });

            //添加新地址提交
            self.J_myAddressBoxShop.find('.J_tcAddressManage').on('click','.J_tcAddressBtn',function(e){
                e.preventDefault();
                e.stopPropagation();
                var J_current = $(this).closest('.J_tcAddressManage');
                var _addRessId = J_current.attr('data-addressid');
                var isNew = _addRessId?false:true;
                var arg = {
                    url:(function(){
                        return isNew ? App.saveAddress:App.updateAddress;
                    })(),
                    data:{
                        addressId:(function(){
                            return isNew ? "": _addRessId;
                        })(),
                        consignee:self.trim(J_current.find('.J_newName').val()),
                        phone:self.trim(J_current.find('.J_newPhone').val()),
                        zoneId:(function(){
                            var J_cityCheck = J_current.find('.J_ciytsCheck');
                            return J_cityCheck.find('.J_district').val() != 0 ?J_cityCheck.find('.J_district').val():J_cityCheck.find('.J_city').val() != 0?J_cityCheck.find('.J_city').val():J_cityCheck.find('.J_province').val();
                        })(),
                        street:self.trim(J_current.find('.J_newStreet').val()),
                        zipcode:self.trim(J_current.find('.J_newZipCode').val()),
                        isDefault:(function(){
                            return J_current.find('.J_setDefault .check-default').hasClass('active')?true:false;
                        })(),
                        t:(function(){
                            var _t = new Date();
                            return _t.getTime();
                        })()

                    }
                };

                if(arg.data){
                    if(!arg.data.consignee){
                        alert('姓名不能为空!');
                        return false;
                    }
                    if(arg.data.consignee.length >30){
                        alert('姓名最长30个字!');
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

                    if(J_current.find('.J_province').val() == 0){
                        alert('省份不能为空！');
                        return false;
                    }
                    if(J_current.find('.J_city option').length > 1 && J_current.find('.J_city').val() == 0){
                        alert('市不能为空！');
                        return false;
                    }
                    if(J_current.find('.J_district option').length > 1 && J_current.find('.J_district').val() == 0){
                        alert('地区不能为空！');
                        return false;
                    }
                    if(!arg.data.street){
                        alert('详细地址不能为空！');
                        return false;
                    }
                    if(!arg.data.street > 100){
                        alert('详细地址最长100个字！');
                        return false;
                    }
                }

                self.sendAddressData(arg,function(rs){
                    if(rs.code != 200){
                        alert(rs.msg);
                        return false;
                    }
                    self.clearFrom();
                    self.J_myAddressBoxShop.find('.J_tcAddressManage').removeAttr('style');
                    self.updateMyAddressList(function(){
                        self.renderAddress();
                        if(!isNew){
                            var _haveCurrentId = self.J_haveAddress.find('input').val(),
                                _checkedItem = self.J_myAddressBoxShop.find('.J_tcMyAddress .J_addressInfo.active');
                            if(_checkedItem){
                                var _checkId = _checkedItem.closest('li').find('.J_addRessShop').attr('data-addressid');
                                if(_haveCurrentId != _checkId){
                                    return false;
                                }else{

                                    self.setCurrentAddress(_checkedItem,true);
                                }
                            }

                        }
                    });

                });

            });

            //编辑地址
            self.J_myAddressBoxShop.find('.J_tcMyAddress').on('click','.J_editAddress',function(e){
                e.preventDefault();
                e.stopPropagation();
                var that = this;
                self.editAddress(that);
            });

            //设置当前为发货地址
            self.J_myAddressBoxShop.on('click','.J_addressInfo',function(e){
                e.preventDefault();
                e.stopPropagation();
                var _that = this;
                var _hasAct = $(this).hasClass('active');
                if(_hasAct){
                    self.J_myAddressBoxShop.hide();
                    self.J_myAddressBoxShop.find('.J_tcMyAddress').hide();
                    self.J_wapOrder.find('.J_home').show();
                    return false;
                }

                $(this).closest('li').siblings().find('.J_addressInfo').removeClass('active');
                $(this).addClass('active');
                self.setCurrentAddress(_that);
                if(self.J_noAddress.attr('style')){
                    self.J_noAddress.hide();
                    self.J_haveAddress.show();
                }

            });

            //设置默认地址
            self.J_myAddressBoxShop.on('click','.J_setDefaultAddress',function(e){
                e.preventDefault();
                e.stopPropagation();
                var that = this;
                var _addressId = $(this).closest('.J_addRessShop').attr('data-addressid');
                var _hasDef = $(this).hasClass('active');
                if(_hasDef){
                    return false;
                }

                if(_addressId && self.isfirst){
                    self.isfirst = false;
                    self.setDefautAddress(_addressId,function(rs){
                        self.isfirst = true;
                        if(rs.code != 200){
                            alert(rs.msg);
                            return false;
                        }
                        $(that).closest('li').siblings('li').find('.J_aaressInfo').removeClass('active');
                        $(that).closest('li').siblings('li').find('.J_setDefaultAddress').removeClass('active').html('设置默认地址');
                        $(that).closest('.J_addressInfo').addClass('active');
                        $(that).closest('li').find('.J_setDefaultAddress').addClass('active').html('[默认地址]');
                    })
                }
            });

            //删除地址
            self.J_myAddressBoxShop.on('click','.J_delAddress',function(e){
                e.preventDefault();
                e.stopPropagation();
                var that = this;
                var _addressId = $(this).closest('.J_addRessShop').attr('data-addressid');
                //var _hasChecked = $(this).closest('.J_addressInfo').hasClass('active');

                self.J_tc.find('.J_tips').attr('data-addressid',_addressId).show();
                self.J_tc.show();





            });
            //取消删除
            self.J_tc.on('click','.J_cancel',function(e){
                e.preventDefault();
                e.stopPropagation();
                var that = this;
                self.J_tc.find('.J_tips').hide();
                self.J_tc.find('.J_tips').removeAttr('data-addressid');
                self.J_tc.hide();


            });
            //确定删除
            self.J_tc.on('click','.J_sure',function(e){
                e.preventDefault();
                e.stopPropagation();
                var that = this;
                var _addressid = $(this).closest('.J_tips').attr('data-addressid');
                if(_addressid && self.isfirst){
                    self.isfirst = false;
                    self.delAddressList(_addressid,function(rs){
                        self.isfirst = true;
                        if(typeof rs == 'string'){
                            rs = $.parseJSON(rs);
                        }

                        if(rs.code != 200){
                            alert(rs.msg);
                            return false;
                        }
                        self.J_myAddressBoxShop.find('.J_tcMyAddress').find('.J_addRessShop[data-addressid*="'+_addressid+'"]').closest('li').remove();
                        var _currentId = self.J_haveAddress.find('input').val();
                        if(_addressid == _currentId){
                            self.J_haveAddress.find('.J_currentAddress').empty().html('<span class="tips">请选择收货地址</span>');
                        }
                        self.J_tc.find('.J_tips').hide();
                        self.J_tc.find('.J_tips').removeAttr('data-addressid');
                        self.J_tc.hide();


                    });

                }
            });

            //编辑设置默认
            self.J_myAddressBoxShop.find('.J_tcAddressManage').on('click','.J_setDefault .check-default',function(e){
                e.preventDefault();
                e.stopPropagation();
                var _hasChecked = $(this).hasClass('active');
                if(_hasChecked){
                    $(this).removeClass('active');
                }else{
                    $(this).addClass('active');
                }
            });




        },
        resetIqyPrice:function(num,callBack){
          var self = this;

            $.ajax({
                url:App.getCalculateFee,
                data:self.returnPriceData(),
                type:'post',
                dataType:'json',
                success:function(rs){
                    if(rs.code == 200){

                        var res = rs.data;
                        var pricList = rs.data.pricingList;
                        var J_productLists = self.J_wapOrder.find('.J_productLists');
                        for(var i = 0,len = pricList.length;i<len;i++){
                            self.J_wapOrder.find('.J_productLists[data-shop-id="'+ pricList[i].shopId +'"]').find('.item-shop-youfei .J_youfei').html('￥' + Number(pricList[i].logisticsFee).toFixed(2));
                            self.J_wapOrder.find('.J_productLists[data-shop-id="'+ pricList[i].shopId +'"]').find('.J_amountPrice .J_price').html('￥' + Number(pricList[i].totalFee).toFixed(2));
                            self.J_wapOrder.find('.js-totalFee').html('￥' + Number(res.totalFee).toFixed(2));

                        }

                        self.J_wapOrder.find('.postage b').html('￥' + Number(res.logisticsFee).toFixed(2));
                        MyCouponModule.getHongbaoData();
                        if(callBack){
                            callBack(rs);
                        }
                    }else{
                        alert(rs.msg);
                        return false;
                    }

                }
            });

        },
        deratePrice:function(num){
          var self = this;
            //重新算加减价
            var J_unitPrice = self.J_wapOrder.find('.J_productLists .product-price .price');
            var _unitPrice = Number(J_unitPrice.html().replace(/\\uffe5|\,|￥/g, ""));
            var J_num = self.J_wapOrder.find('#qty');
            var J_showPrice = self.J_wapOrder.find('.J_productLists .J_price');
            var J_totalFee = self.J_wapOrder.find('.js-totalFee');
            var J_checkedCoupon = self.J_wapOrder.find('.J_couponCheckBox').find('.J_totalCoupon li');

            var youfei = Number(self.J_wapOrder.find('.postage b').html().replace(/\\uffe5|\,|￥/g, ""));
            var _price = 0;//优惠

            var _totalPricce = _unitPrice;
            if(J_checkedCoupon.length){
                for(var i = 0,len=J_checkedCoupon.length;i<len;i++){
                    _price += Number(J_checkedCoupon.eq(i).attr('data-numb'));
                }

            }

            if(num){
                num = Number(num);
                _totalPricce = Number(_unitPrice*num).toFixed(2);
                J_num.val(num);
                self.J_wapOrder.find('.J_productLists .product-price .num').html('x' + num);
                self.J_wapOrder.find('.J_productLists .J_amountPrice .product-num em').html(num);
                J_showPrice.html('￥' + Number(_totalPricce).toFixed(2));
                J_totalFee.html('￥' + Number(_totalPricce - _price - youfei).toFixed(2));

            }




        },
        editAddress:function(ele){
            var self = this;
            var J_item = $(ele).closest('li');
            var name = J_item.find('.J_name').attr('data-name') || '',
                addres = J_item.find('.J_street .st').html() || '',
                zipCode = J_item.find('.J_addRessShop').attr('data-zipcode') || '',
                tel = J_item.find('.J_phone').attr('data-phone') || '',
                isDefault = J_item.find('.J_setDefaultAddress').hasClass('active')?true:false,
                addressId = J_item.find('.J_addRessShop').attr('data-addressid') || '',
                provinceId = J_item.find('.J_street .pcd').eq(0).attr('data-id') || '',
                cityId = J_item.find('.J_street .pcd').eq(1).attr('data-id') || '',
                disId = J_item.find('.J_street .pcd').eq(2).attr('data-id') || '';
            var J_current = self.J_myAddressBoxShop.find('.J_tcAddressManage');
            J_current.attr('data-addressid',addressId);
            J_current.find('.J_newName').val(name);
            J_current.find('.J_newPhone').val(tel);
            J_current.find('.J_newZipCode').val(zipCode);
            J_current.find('.J_newStreet').val(addres);
            if(isDefault){
                J_current.find('.J_setDefault .check-default').addClass('active');
            }else{
                J_current.find('.J_setDefault .check-default').removeClass('active');
            }

            //省市区设置
            if(provinceId){

                CityCheckModule.addressData({id:1},function(rs){
                    if(rs.code == 200){
                        if(provinceId == 2 || provinceId == 20 || provinceId == 2465 || provinceId == 861){
                            J_current.find('.J_district').closest('div').hide();

                        }else{
                            J_current.find('.J_district').closest('div').show();
                        }

                        var _html = CityCheckModule.initOption(rs.data);
                        J_current.find('.J_province option[value!=0]').remove();
                        J_current.find('.J_province').append(_html);
                        J_current.find('.J_province option[value="'+ provinceId +'"]').attr('selected','selected');

                        self.J_wapOrder.find('.J_tcAddressManage').show();

                        CityCheckModule.addressData({id:provinceId},function(rs){
                            if(rs.code == 200){
                                var _html = CityCheckModule.initOption(rs.data);
                                J_current.find('.J_city').append(_html);
                                J_current.find('.J_city option[value="'+ cityId +'"]').attr('selected','selected');
                                if(disId){
                                    CityCheckModule.addressData({id:cityId},function(rs){
                                        if(rs.code == 200){
                                            var _html = CityCheckModule.initOption(rs.data);
                                            J_current.find('.J_district').append(_html);
                                            J_current.find('.J_district option[value="'+disId+'"]').attr('selected','selected');
                                        }
                                    });
                                }

                                J_current.find('.J_tcContTitle .title').html('编辑地址');
                                self.J_myAddressBoxShop.find('.J_tcMyAddress').hide();
                                J_current.show();

                                self.J_myAddressBoxShop.show();
                            }
                        });
                    }
                });

            }
        },
        reloadUrl:function(str,num){
          var self = this;

            //str = 'qty='

            var obj = location.href;
            var argIndex = obj.indexOf('?');
            var arg = obj.substr(argIndex + 1);
            var args = arg.split('&');
            var valArg = '';
            var _str='';
            for(var i = 0,len = args.length; i< len;i++){
                _str = args[i];
                if(_str.indexOf(str)>-1){
                    valArg = _str;
                }

            }
            var objStr = obj.replace(valArg,'') + str  + num;
            location.href = objStr;
            //return objStr;

        },
        clearFrom:function(){
            var self = this;
            var J_currentFrom = self.J_myAddressBoxShop.find('.J_tcAddressManage');
            J_currentFrom.removeAttr('data-addressid');
            J_currentFrom.find('.J_tcContTitle .title').html('添加新地址');
            J_currentFrom.find('.J_newName').val('');
            J_currentFrom.find('.J_newPhone').val('');
            J_currentFrom.find('.J_newZipCode').val('');
            J_currentFrom.find('.J_province').val(0);
            J_currentFrom.find('.J_city option').not('[value="0"]').remove();
            J_currentFrom.find('.J_district option').not('[value="0"]').remove();
            J_currentFrom.find('.J_newStreet').val('');
            J_currentFrom.find('.J_setDefault .check-default').removeClass('active');

        },
        sendAddressData:function(arg,callBack){
            var self = this;
            arg = arg || {};
            var _data = arg.data;
            $.ajax({
                url:arg.url,
                type:'post',
                data:_data,
                dataType:'json',
                success:function(rs){

                    if(callBack){
                        callBack(rs);
                    }
                }
            })
        },
        delAddressList:function(id,callBack){
            var self = this;
            $.ajax({
                url:App.delAddress,
                type:'get',
                data:{
                    addressId:id,
                    t: (function(){
                        var _str = new Date();
                        return _str.getTime();
                    })()
                },
                success:function(rs){
                    if(callBack){
                        callBack(rs);
                    }
                }
            })
        },
        setDefautAddress:function(id,callBack){
            var self = this;
            $.ajax({
                url:App.defaultAddress,
                type:'get',
                data:{
                    addressId:id,
                    t: (function(){
                        var _str = new Date();
                        return _str.getTime();
                    })()
                },
                dataType:'json',
                success:function(rs){
                    if(callBack){
                        callBack(rs);
                    }
                }
            });
        },
        setCurrentAddress:function(ele,flgs){
          var self = this;
            //设置当前为收货地址
            var J_current = $(ele).closest('.J_myAddressItem');
            var _addressId = J_current.find('.J_addRessShop').attr('data-addressid'),
                _zipcode = J_current.find('.J_addRessShop').attr('data-zipcode'),
                _name = J_current.find('.J_name').attr('data-name'),
                _phone = J_current.find('.J_phone').attr('data-phone'),
                _street = self.trim(J_current.find('.street-info').text()).replace(/\s/g,''),
                _zoneId = J_current.find('.J_addRessShop').attr('data-zoneid');


            var _data = {
                address:_street,
                name:_name,
                phone:_phone,
                addressId:_addressId,
                zoneId:_zoneId
            };
            var _temp = template('J_tempCurrentAddress',_data);
            self.J_haveAddress.find('.J_currentAddress').empty().append(_temp);

            if(flgs){
                self.J_myAddressBoxShop.show();
                self.J_myAddressBoxShop.find('.J_tcMyAddress').show();
                self.J_wapOrder.find('.J_home').hide();
            }else{
                self.J_myAddressBoxShop.hide();
                self.J_myAddressBoxShop.find('.J_tcMyAddress').hide();
                self.J_wapOrder.find('.J_home').show();
            }
            //设置当前为收货地址后，需要要调接口，看运费有没有变化
            self.getCalulateFeeData();

        },
        returnPriceData:function(){
            var self = this;
            var _data = {
                couponJson:(function(){
                    var couponJson = [];
                    var _item = {};
                    var J_checkCouponList = self.J_wapOrder.find('.J_totalCoupon ul li');
                    if(self.isqty){
                        self.J_wapOrder.find('.J_totalCoupon ul').empty();
                        return couponJson;
                    }
                    if(!J_checkCouponList.length){
                        return couponJson;
                    }
                    for(var i = 0,len = J_checkCouponList.length;i<len;i++ ){
                        _item = {
                            id:J_checkCouponList.eq(i).attr('data-id'),
                            discount:J_checkCouponList.eq(i).attr('data-numb')
                        }
                        couponJson.push(_item);
                    }
                    return JSON.stringify(couponJson);
                })(),
                skuJson:(function(){
                    var shopArry=[];

                    var J_ShopItem = self.J_wapOrder.find('.J_productLists');
                    for(var i = 0,len = J_ShopItem.length;i<len;i++){
                        var _item = {
                            shopId:(function(){
                                var _shopId = J_ShopItem.eq(i).attr('data-shop-id');
                                return _shopId;
                            })(),
                            orderSkuVO:(function(){
                                var _skus = [];
                                var _skuItem = {};
                                var J_ShopProductItem = J_ShopItem.eq(i).find('.product-item-body');
                                for(var j = 0,j_len = J_ShopProductItem.length; j < j_len; j++){
                                    _skuItem = {
                                        skuId:(function(){
                                            var _skuId = J_ShopProductItem.eq(j).attr('data-skuid');
                                            return _skuId;
                                        })(),
                                        qty:(function(){
                                            var _num = J_ShopProductItem.eq(j).attr('data-amount');

                                            return _num;
                                        })(),
                                        cartItemId:(function(){
                                           var _str = J_ShopProductItem.eq(j).attr('data-cartid')||'';
                                            return _str;
                                        })()
                                    }
                                    _skus.push(_skuItem);
                                }
                                return _skus;
                            })()
                        }
                        shopArry.push(_item);
                    }
                    return JSON.stringify(shopArry);
                })(),
                zoneId:(function(){
                    var _str = self.J_haveAddress.find('#addressId').attr('data-zoneid');
                    if(!_str){
                        alert('请选择收货地址');
                        return false;
                    }
                    return _str;
                })(),
                t: $.now()
            };
            //console.log(_data);
            return _data;
        },
        getCalulateFeeData:function(){
            var self = this;
            //回调当前定单信息，用于重新计算价格
            $.ajax({
                url:App.getCalculateFee,
                type:'post',
                data:self.returnPriceData(),
                dataType:'json',
                success:function(rs){
                    self.resetDeratePrice(rs);
                }
            })

        },
        resetDeratePrice:function(rs){
            var self = this;
            if(rs.code != 200){
                alert(rs.msg);
                return false;
            }

            var res = rs.data;
            var pricList = rs.data.pricingList;
            var J_productLists = self.J_wapOrder.find('.J_productLists');
            if(!self.isqty){
                for(var i = 0,len = pricList.length;i<len;i++){
                    self.J_wapOrder.find('.J_productLists[data-shop-id="'+ pricList[i].shopId +'"]').find('.item-shop-youfei .J_youfie').html('￥' + Number(pricList[i].logisticsFee).toFixed(2) );
                    self.J_wapOrder.find('.J_productLists[data-shop-id="'+ pricList[i].shopId +'"]').find('.J_amountPrice .J_price').html('￥' + Number(pricList[i].totalFee).toFixed(2));

                }
                self.J_wapOrder.find('.js-totalFee').html('￥' + Number(res.totalFee).toFixed(2));
                self.J_wapOrder.find('.postage b').html('￥' + Number(res.logisticsFee).toFixed(2));

            }else{
                for(var i = 0,len = pricList.length;i<len;i++){
                    self.J_wapOrder.find('.J_productLists[data-shop-id="'+ pricList[i].shopId +'"]').find('.item-shop-youfei .J_youfie').html('￥' + Number(pricList[i].discountFee).toFixed(2) );
                    self.J_wapOrder.find('.J_productLists[data-shop-id="'+ pricList[i].shopId +'"]').find('.J_amountPrice .J_price').html('￥' + Number(pricList[i].totalFee).toFixed(2));

                }
                self.J_wapOrder.find('.js-totalFee').html('￥' + Number(res.totalFee).toFixed(2));
                self.J_wapOrder.find('.postage b').html('￥' + Number(res.logisticsFee).toFixed(2));
            }


        },
        renderAddress:function(){
            var self = this;

            var b = self.addressList;
            if(b.length){
                var _data = {
                    _data:b
                };
                var _temp = template('J_tempAddressList',_data);
                self.J_myAddressBoxShop.find('.J_tcMyAddress').find('.J_tcContBody ul').empty().append(_temp);
                var _checkedId = self.J_haveAddress.find('input').val();
                self.J_myAddressBoxShop.find('.J_tcMyAddress').find('.J_addressInfo').removeClass('active');
                self.J_myAddressBoxShop.find('.J_tcMyAddress').find('.J_addRessShop[data-addressid*="'+_checkedId+'"]').closest('li').find('.J_addressInfo').addClass('active');
                //$('body').css('overflow','hidden');
                self.J_myAddressBoxShop.attr('data-isempty','0').show();
                self.J_wapOrder.find('.J_home').hide();
                self.J_myAddressBoxShop.find('.J_tcMyAddress').show();
            }


        },
        updateMyAddressList:function(callBack){
          var self = this;
            //更新地址数据
            $.ajax({
                url: App.myAddressList,
                type: 'get',
                data: {
                    t: (function () {
                        var _t = new Date();
                        return _t.getTime();
                    })()
                },
                dataType: 'json',
                success: function (rs) {

                    if (rs.code != 200) {
                        alert(rs.msg);
                        return false;
                    }
                    if(rs.data.length){
                        var b = [];
                        rs.data.sort(function(a,b){
                            return b.createdAt - a.createdAt;
                        });

                        self.addressList.length = 0;

                        for(var i = 0,len = rs.data.length; i < len; i++){
                            if(rs.data[i].isDefault){
                                self.addressList.unshift(rs.data[i]);
                            }else{
                                self.addressList.push(rs.data[i]);
                            }
                        }

                        if(callBack){
                            callBack();
                        }
                    }
                }
            });
        },
        getAddressListData:function(callBack){
            var self = this;
            //获取收获地址数据
            $.ajax({
                url:App.myAddressList,
                type:'get',
                data:{
                  t:(function(){
                      var _t = new Date();
                      return _t.getTime();
                  })()
                },
                dataType:'json',
                success:function(rs){

                    if(rs.code != 200){
                        alert(rs.msg);
                        return false;
                    }

                    if(rs.data.length){
                        var b = [];
                        rs.data.sort(function(a,b){
                            return b.createdAt - a.createdAt;
                        });

                        self.addressList.length = 0;

                        for(var i = 0,len = rs.data.length; i < len; i++){
                            if(rs.data[i].isDefault){
                                self.addressList.unshift(rs.data[i]);
                            }else{
                                self.addressList.push(rs.data[i]);
                            }
                        }

                        b = self.addressList;

                        if(b.length){
                            var _address = b[0].details.replace('中国','');
                            var _name = b[0].consignee;
                            var _phone = b[0].phone;
                            var _addressId = b[0].id;
                            var _zoneId = b[0].zoneId;
                            var _data = {
                                address:_address,
                                name:_name,
                                phone:_phone,
                                addressId:_addressId,
                                zoneId:_zoneId
                            };
                            var _temp = template('J_tempCurrentAddress',_data);

                            self.J_haveAddress.find('.J_currentAddress').empty().append(_temp);
                            self.J_haveAddress.show();

                        }


                    }else{
                        self.J_noAddress.show();

                    }
                }
            });

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
                re = /^1[3|4|5|7|8]\d{9}$/g;
            }

            if(re.test(str)){
                return true;
            }else{
                return false;
            }
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
        trim:function(str) {

            return str.replace(/(^\s*)|(\s*$)/g,'');
        },
        isWeiXin:function(){
            var self = this;
            //是否是微信
            if(browser.mobile){

                var  ua = navigator.userAgent.toLowerCase();
                if(ua.match(/MicroMessenger/i) == 'micromessenger'){

                    self.J_wapOrder.find('.J_payStyle .J_weixinBox').show();
                }
            }
        },
        browser:function(){
            var u = window.navigator.userAgent,
                app = window.navigator.appVersion;
            window.browser = {};
            window.browser = {
                trident: u.indexOf('Trident') > -1, //IE内核

                presto: u.indexOf('Presto') > -1, //opera内核

                webKit: u.indexOf('AppleWebKit') > -1, //苹果、谷歌内核

                gecko: u.indexOf('Gecko') > -1 && u.indexOf('KHTML') == -1, //火狐内核

                mobile: !!u.match(/AppleWebKit.*Mobile.*/), //是否为移动终端

                ios: !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/), //ios终端

                android: u.indexOf('Android') > -1 || u.indexOf('Linux') > -1, //android终端或uc浏览器

                iPhone: u.indexOf('iPhone') > -1, //是否为iPhone或者QQHD浏览器

                iPad: u.indexOf('iPad') > -1, //是否iPad

                webApp: u.indexOf('Safari') == -1 //是否web应该程序，没有头部与底部
            }
        }

    };

    var CityCheckModule = {
        init:function(){
            var self = this;
            self.J_citysCheck = $('body').find('.J_ciytsCheck');

            self.eventHanding();
        },
        eventHanding:function(){
            var self = this;

            //渲染省份
            self.addressData({id:1},function(rs){
                if(rs.code == 200){
                    var _html = self.initOption(rs.data);
                    self.J_citysCheck.find('.J_province').append(_html);
                }
            });

            //渲染市
            self.J_citysCheck.find('.J_province').on('change',function(e){
               var _id = $(this).val();
                if(_id == 2 || _id == 20 || _id == 2465 || _id ==861){
                    self.J_citysCheck.find('.J_district').closest('div').hide();
                    self.J_citysCheck.find('.J_city').closest('div').show();
                }else if(_id == 3513 || _id == 3514 || _id == 3515){
                    self.J_citysCheck.find('.J_city').closest('div').hide();
                    self.J_citysCheck.find('.J_district').closest('div').hide();
                }else{
                    self.J_citysCheck.find('.J_city').closest('div').show();
                    self.J_citysCheck.find('.J_district').closest('div').show();
                }

                if(_id == 0){
                    self.J_citysCheck.find('.J_city option[value!="0"]').remove();
                    self.J_citysCheck.find('.J_district option[value!="0"]').remove();
                    return false;
                }
                self.addressData({id:_id},function(rs){
                    if(rs.code == 200){
                        if(!rs.data.length){
                            return false;
                        }
                        var _html = self.initOption(rs.data);
                        self.J_citysCheck.find('.J_city option[value!="0"]').remove();
                        self.J_citysCheck.find('.J_district option[value!="0"]').remove();
                        self.J_citysCheck.find('.J_city option[value="0"]').attr('selected','selected');
                        self.J_citysCheck.find('.J_city').append(_html);
                    }
                })

            });

            //渲染地区
            self.J_citysCheck.find('.J_city').on('change',function(){
               var _id = $(this).val();
                if(_id == 0){
                    self.J_citysCheck.find('.J_district option[value!="0"]').remove();
                    return false;
                }
                self.addressData({id:_id},function(rs){
                    if(rs.code == 200){
                        if(!rs.data.length){
                            return false;
                        }
                        var _html = self.initOption(rs.data);
                        self.J_citysCheck.find('.J_district option[value!="0"]').remove();
                        self.J_citysCheck.find('.J_district option[value="0"]').attr('selected','selected');
                        self.J_citysCheck.find('.J_district').append(_html);
                    }
                })
            });

        },
        addressData:function(arg,callBack){
            arg = arg || {};
            $.ajax({
                url:App.city,
                type:'get',
                data:{
                    id:arg.id,
                    t:(function(){
                        var str = new Date();
                        return str.getTime();
                    })()
                },
                dataType:'json',
                success:function(rs){
                    if(callBack){
                        callBack(rs);
                    }

                }
            });
        },
        initOption:function(rs){
            var self = this;
            var _temp = '';
            if(rs.length){
                for(var i = 0,len = rs.length; i < len; i++){
                    _temp += '<option value="' + rs[i].id + '">' + rs[i].name + '</option>';
                }
                return _temp;
            }
        }
    };

    $(function(){
        MyAddressMangeW.init();
        CityCheckModule.init();
    });

})();