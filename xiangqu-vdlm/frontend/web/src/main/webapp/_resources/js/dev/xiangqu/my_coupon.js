/**
 * Created by ziyu on 2016/1/25.
 */

(function(){
    var isTest = location.host.indexOf('dev.www.xiangqutest.com') > -1 ? true :false;
    isTest = false;
        var App = {
            myCouponList:'/ouer/cart/coupon/list'
        };

    var MyCouponModule = {
        init:function(){
            var self = this;
            self.J_couponCheckBox = $('.J_couponCheckBox');//红包
            self.J_wapOrder = $('.J_wapOrder');
            self.J_myAddressBoxShop = $('.J_myAddressBoxShop');//改交互
            self.J_couponBtnBox = self.J_couponCheckBox.find('.J_couponBtnBox');
            self.J_tcMyCouponBox = self.J_wapOrder.find('.J_tcMyCouponBox');
            self.couponsItems = [];//存储红包数据
            self.currentActIdNum = 0; //当前可使用的同一个activityId的上限数
            self.currentTypeIdNum = 0;//当前可使用的同一个 typeId 的上限数
            self.currentMutexTypeId = [];//与当前操作互斥的typeId
            self.currenntMutexActId = [];//与当前操作互斥的actId
            self.isUseCoupons = false;//当前是否使用红包

            self.checkedList = [];//已经选中的
            self.unCheckedList = [];//没有选中的

            self.scrollTop = 0;

            self.eventHanding();
        },
        eventHanding:function(){
            var self = this;
            //获取红包
            self.getHongbaoData();

            //查看优惠券
            self.J_couponBtnBox.on('click',function(e){
                e.preventDefault();
                e.stopPropagation();
                self.scrollTop = $(window).scrollTop();
                self.J_wapOrder.find('.J_home').hide();
                self.J_myAddressBoxShop.find('.J_tcMyCouponBox').show()
                self.J_myAddressBoxShop.show();

            });



            //关闭优惠券
            self.J_myAddressBoxShop.find('.J_tcMyCouponBox').on('click','.J_closeAddressTc',function(e){
                e.preventDefault();
                e.stopPropagation();
                self.J_myAddressBoxShop.find('.J_tcMyCouponBox').hide();
                self.J_wapOrder.find('.J_home').show();

            });

            //选择优惠券
            self.J_myAddressBoxShop.find('.J_tcMyCouponBox').on('click','.J_coupItem',function(e){
                e.preventDefault();
                e.stopPropagation();
                var _hasCurrent = $(this).hasClass('active')?false:true;
                var _isDisabled = $(this).hasClass('disabled')?true:false;
                if(_isDisabled){
                    return false;
                }

                var _data = $(this).data('data');


                var _typeIdNum = self.getTypeId(_data.useConstraint);
                var _actIdNum = self.getActivityId(_data.useConstraint);

                var rs = {
                    selfTypeId:_data.typeId,
                    selfActId:_data.activityId,
                    selfMaxUseTypeIdNum:(function(){
                        var num = 0;
                        if(_typeIdNum.length){
                            num = _typeIdNum[0];
                        }
                        return num;
                    })(),
                    selfMaxUseActIdNum:(function(){
                        var _num = 0;
                        if(_actIdNum.length){
                            _num = _actIdNum[0];
                        }
                        return _num;
                    })(),
                    selfMutexActId:(function(){
                        var _allActId = [];
                        var _mutexTpId = self.getUnTypeId(_data.useConstraint);
                        var _mutexActId = self.getUnActivityId(_data.useConstraint);

                        if(_mutexTpId.length){
                            for(var i = 0; i < _mutexTpId.length; i++){
                                var _d = self.J_tcMyCouponBox.find('.J_coupItem[typeid="'+ _mutexTpId[i] +'"]').attr('activityid');
                                if(_d){
                                    _allActId.push(_d);
                                }else{
                                    continue;
                                }
                            }
                            if(_typeIdNum.length < 1){
                                var J_unClickMuteActId = self.J_tcMyCouponBox.find('.J_coupItem[isChecked="0"][typeid="' + _data.typeId + '"]');
                                for(var i = 0 ; i < J_unClickMuteActId.length; i ++){
                                    var _act = J_unClickMuteActId.eq(i).attr('activityid');
                                    _mutexActId.push(_act);
                                }

                                _allActId = _allActId.concat(_mutexActId);
                            }


                        }

                        if(!_mutexActId.length){
                            var _unCheckInput = self.J_tcMyCouponBox.find('.J_coupItem').not('[class!="disabled"]');
                            for(var i = 0; i < _unCheckInput.length;i++){
                                if(self.getUnActivityId($(_unCheckInput).eq(i).attr('useconstraint')).join(',').indexOf(_data.activityId) > -1){
                                    _mutexActId.push($(_unCheckInput).eq(i).attr('activityid'));
                                }
                            }

                        }


                        return _allActId;


                    })()
                }


                //选中当前
                if(_hasCurrent){
                    $(this).attr('isChecked','1').addClass('active');

                    if(_typeIdNum.length){
                        if(self.J_tcMyCouponBox.find('.J_coupItem[isChecked=1][typeid='+ rs.selfTypeId +']').length >= _typeIdNum){
                            self.J_tcMyCouponBox.find('.J_coupItem[isChecked=0][typeid='+ rs.selfTypeId +']').addClass('disabled');
                        }
                    }

                    if(rs.selfMutexActId.length){
                        for(var i = 0; i < rs.selfMutexActId.length; i++){
                            self.J_tcMyCouponBox.find('.J_coupItem[isChecked=0][activityid='+ rs.selfMutexActId[i] +']').addClass('disabled');
                        }
                    }

                    if(rs.selfMaxUseTypeIdNum > 0){
                        self.currentTypeIdNum = rs.selfMaxUseTypeIdNum;
                        var _num = self.J_tcMyCouponBox.find('.J_coupItem[class*="active"][activityid="'+ _data.activityId +'"]').length;
                        if(_num >= self.currentTypeIdNum){
                            self.J_tcMyCouponBox.find('.J_coupItem[activityid="'+ _data.activityId +'"]').not('[class*="active"]').addClass('disabled');
                        }
                    }

                    if(rs.selfMaxUseActIdNum > 0){
                        self.currentTypeIdNum = rs.selfMaxUseActIdNum;
                        var _num = self.J_tcMyCouponBox.find('.J_copuItem[class*="active"][activityid="'+ _data.activityId +'"]').length;
                        if(_num >= self.currentTypeIdNum){
                            self.J_tcMyCouponBox.find('.J_coupItem:not([class*="disabled"])[activityid="'+ _data.activityId +'"]').addClass('disabled');
                        }
                    }

                    var _unCheckInput = self.J_tcMyCouponBox.find('.J_coupItem:not([class*="active"])').not('[class*="disabled"]');

                    if(_unCheckInput && _unCheckInput.length){
                        $.each(_unCheckInput,function(idx,ele){

                            if(self.getUnTypeId($(ele).attr('useconstraint')).join(',').indexOf(_data.typeId) > -1){
                                //console.log(self.getUnTypeId($(ele).attr('useconstraint')).join(','));
                                $(ele).addClass('disabled');

                            }
                        });

                    }

                    self.unCheckedList=[];
                    for(var i = 0; i < self.J_tcMyCouponBox.find('.J_coupItem:not([class*="active"])[class*="disabled"]').length;i++){
                        self.unCheckedList.push(self.J_tcMyCouponBox.find('.J_coupItem:not([class*="active"])[class*="disabled"]').eq(i).data('data')
                        );
                    }


                }else{
                    $(this).attr('isChecked','0').removeClass('active');
                    if(self.unCheckedList.length){
                        for(var i = 0; i < self.unCheckedList.length; i++){
                            self.J_tcMyCouponBox.find('.J_coupItem[class*="disabled"][activityid="'+ self.unCheckedList[i].activityId +'"]').removeClass('disabled');
                        }
                    }

                    self.J_tcMyCouponBox.find('.J_coupItem').removeClass('disabled').removeClass('active').attr('isChecked','0');
                }

            });


            //确定选中红包
            self.J_tcMyCouponBox.on('click','.J_tcAddressBtn',function(e){
                e.preventDefault();
                e.stopPropagation();
                var J_checkedItem = self.J_tcMyCouponBox.find('.J_coupItem[class*="active"]');
                var _checkArr = [];
                for(var i = 0,len = J_checkedItem.length; i < len; i++){
                    var _data = J_checkedItem.eq(i).data('data');
                    _checkArr.push(_data);
                }
                var _html = '';
                if(_checkArr.length){
                    for(var i = 0,len = _checkArr.length;i<len;i++){
                        _html += '<li data-id="'+ _checkArr[i].id +'" data-coupon-type="'+ _checkArr[i].actCode +'" data-extCouponId="" data-numb="'+_checkArr[i].discount+'" data-selected="">' +
                            '<input type="hidden" name="coupon['+ i +'].activityId" value="'+ _checkArr[i].activityId +'" />'+
                            '<input type="hidden" name="coupon['+ i +'].id" value="'+ _checkArr[i].id +'" />'+
                            '<input type="hidden" name="coupon['+ i +'].discount" value="'+ _checkArr[i].discount +'" />'+
                            '<input type="hidden" name="coupon['+ i +'].extCouponId" value="" />'+
                             _checkArr[i].title + '(' + _checkArr[i].discount + '元)</li>';

                    }


                }
                self.J_couponCheckBox.find('.J_totalCoupon ul').empty().append(_html);
                self.J_tcMyCouponBox.removeAttr('style').find('.J_coupItem').attr('ischecked',0).removeClass('active').removeClass('disabled');



                self.deratePrice();
                self.J_wapOrder.find('.J_home').show();
                //console.log(self.scrollTop);
                $(window).scrollTop(self.scrollTop);

            });

        },
        deratePrice:function(){
          var self = this;
            var J_price = self.J_wapOrder.find('.J_productLists .J_price');
            var J_totalFee = self.J_wapOrder.find('.js-totalFee');
            var J_postage = self.J_wapOrder.find('.postage b');
            var J_checkedCoupon = self.J_couponCheckBox.find('.J_totalCoupon li');
            var _price = 0;//优惠
            var _totalPrice = 0;
            if(J_checkedCoupon.length){
                for(var i = 0,len=J_checkedCoupon.length;i<len;i++){
                    _price += Number(J_checkedCoupon.eq(i).attr('data-numb'));
                }

            }

            if(J_price.length){
                for(var i = 0,len = J_price.length;i < len; i++){
                    _totalPrice += Number(J_price.eq(i).html().replace(/\\uffe5|\,|￥/g, ""));
                }
            }
            var TotalPrice = Number(_totalPrice - _price).toFixed(2);
            if(TotalPrice <= 0){
                TotalPrice = 0;
            }
            J_totalFee.html('￥' + TotalPrice);


        },
        getHongbaoData:function(){
            var self = this;
            var _data = {
                skuJson:(function(){
                    var _skuArry = [];
                    var _item = {};
                    var J_item = self.J_wapOrder.find('.product-item-body');
                    for(var i = 0,len= J_item.length;i < len;i++){
                        var _sku = J_item.eq(i).attr('data-skuid');
                        var _num = J_item.eq(i).attr('data-amount');
                        _item[_sku] = _num;
                    }
                    _skuArry.push(_item);
                    return JSON.stringify(_skuArry[0]);


                })(),
                t:(function(){
                    var _t = new Date();
                    return _t.getTime();
                })()

            };

            self.getAjaxData(App.myCouponList,_data,'post',function(rs){

                if(rs.length){
                    self.J_couponBtnBox.find('.coupon-num em').html(rs.length);
                }else{
                    self.J_couponBtnBox.find('.coupon-num em').html(0);
                }

                var J_coupCheckBox = self.J_tcMyCouponBox.find('.J_coupItem');
                for(var i = 0;i < J_coupCheckBox.length; i++){
                    J_coupCheckBox.eq(i).data('data',rs[i]);
                }

            });

        },
        getAjaxData:function(url,data,type,callBack){
            var self = this;
            if(data){
                data = data;

            }else{
                data = {}
            }
            $.ajax({
                url:url,
                type:type,
                data:data,
                dataType:'json',
                success:function(rs){
                    if(rs.code == 200){
                        self.J_couponCheckBox.find('.J_totalCoupon ul').empty();
                        var  _coupons = rs.data.userCouponList;
                        if(!_coupons.length){
                            self.couponsItems = [];
                            self.J_couponBtnBox.find('.coupon-num em').html(0);
                            self.J_myAddressBoxShop.find(".J_tcMyCouponBox .J_tcContBody ul").empty();
                            return false;
                        }


                        self.couponsItems = _coupons;
                        var _html = '';
                        for(var i = 0,len = _coupons.length;i<len;i++){
                            _html += '<li class="J_coupItem " isChecked="0"  ' +
                                'data-id="'+ _coupons[i].id+'" ' +
                                'data-coupon-type="'+ _coupons[i].actCode+'" ' +
                                'id="'+ _coupons[i].id+'" ' +
                                'activityid="'+ _coupons[i].activityId +'" ' +
                                'useConstraint="'+ _coupons[i].useConstraint +'" ' +
                                'typeid="'+ _coupons[i].typeId +'" ' +
                                'minPrice="'+ _coupons[i].minPrice +'" ' +
                                'data-numb="'+ _coupons[i].discount +'">' +
                                    '<div class="discont">' +
                                        '<span>￥</span>'+
                                        '<em>'+ _coupons[i].discount +'</em>' +
                                    '</div>'+

                                    '<div class="coupon-info">'+
                                        '<p class="title">' + _coupons[i].title  +'</p>' +
                                        '<p class="min-price">' +
                                            '<span>满'+ _coupons[i].minPrice + '元可用</span>' +
                                        '</p>' +
                                        '<p class="valid-from">' +
                                            self.returnTime(_coupons[i]) +

                                        '</p>' +
                                        self.returnPlat(_coupons[i]) +
                                    '</div>'+


                                    //' id:' + _coupons[i].id + '  ' +
                                    //'activityId:' + _coupons[i].activityId + '  ' +
                                    //'useConstraint:' + _coupons[i].useConstraint + '   ' +
                                    //'typeid:' + _coupons[i].typeId +


                                '</li>';
                        }

                        self.J_myAddressBoxShop.find(".J_tcMyCouponBox .J_tcContBody ul").empty().append(_html);
                        if(callBack){
                            callBack(_coupons);
                        }

                    }else{
                        return false;
                    }
                }
            });
        },
        returnPlat:function(rs){
          var self = this;
            var _html = "";
            if(rs.platform){
                _html =  '<span class="platfrom">手机专享</span>';
            }

            return _html;
        },
        returnTime:function(rs){
            var self = this;
            var _html = '';

            if(rs.validFrom && rs.validTo){

                _html = '<span >使用期限</span><em>' + self.formatTime(rs.validFrom) + '至' + self.formatTime(rs.validTo)  +'</em>';
            }else{
                _html = '<span >使用时间从'+ self.formatTime(rs.createdAt) +'开始</span>'
            }


            return _html;
        },
        formatTime:function(time){
            var self = this;
            var _str = '';
            if(time){
                var date = new Date(time);
                var month = date.getMonth() + 1 < 10 ? '0' + (date.getMonth() +1) : date.getMonth() + 1;
                var currentDate = date.getDate() < 10 ? '0' + date.getDate() : date.getDate();
                var H = date.getHours() < 10 ? '0' + date.getHours():date.getHours();
                var M = date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes();
                _str = date.getFullYear() + '-' + month + '-' + currentDate + ' ' + H + ':' + M;
            }

            return _str;
        },
        getTypeId:function(str){
            var self = this;
            //获取（）类型
            //return []
            var str = str || '';
            var _str = '';
            var _rs = [];
            var reg = /\([^\)]+\)/g;
            var obj = reg.exec(str);

            if(obj && obj.length){
                _str = obj[0].replace(/\(|\)/g,'');
            }
            _str = _str.split(',');
            for(var i = 0; i < _str.length; i++){
                if(_str[i] == ''){
                    continue;
                }
                _rs.push(_str[i]);
            }

            return _rs;
        },
        getActivityId:function(str){
            var self = this;
            //获取[]类型
            //return []
            var str = str || '';
            var _str = '';
            var _rs = [];
            var reg = /[^\[]+\]$/g;
            var obj = reg.exec(str);
            //console.log(obj);
            if(obj && obj.length){
                _str = obj[0].replace(/\[|\]/g,'');
            }
            _str = _str.split(',');
            for(var i = 0; i < _str.length; i++){
                if(_str[i] == ''){
                    continue;
                }
                _rs.push(_str[i]);
            }

            return _rs;
        },
        getUnTypeId:function(str){
            var self = this;
            //获取<>类型
            //return [];
            var str = str || '';
            var _str = '';
            var _rs = [];
            var reg = /[^\<]+\>$/g;
            var obj = reg.exec(str);
            //console.log(obj);

            if(obj && obj.length){
                _str = obj[0].replace(/\<|\>/g,'');
            }
            _str = _str.split(',');
            for(var i = 0; i < _str.length; i++){
                if(_str[i] == ''){
                    continue;
                }
                _rs.push(_str[i]);
            }
            return _rs;
        },
        getUnActivityId:function(str){
            var self = this;
            //获取{}类型
            //return [];
            var str = str || '';
            var _str = '';
            var _rs = [];
            var reg = /[^\{]+\}$/g;
            var obj = reg.exec(str);
            //console.log(obj);

            if(obj && obj.length){
                _str = obj[0].replace(/\{|\}/g,'');
            }
            _str = _str.split(',');
            for(var i = 0; i < _str.length; i++){
                if(_str[i] == ''){
                    continue;
                }
                _rs.push(_str[i]);
            }

            return _rs;
        }


    };

    window.MyCouponModule = MyCouponModule;
    $(function(){
        MyCouponModule.init();
    })

})();