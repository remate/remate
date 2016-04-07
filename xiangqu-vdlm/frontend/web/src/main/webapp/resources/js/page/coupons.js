/**
 * Created by ziyu - Think on 2015/11/6.
 */


(function(){
    var $ = require('jquery');
    var hongbaoModule = {
        init:function(){
            var self = this;
            self.J_couponsItems = $('.J_couponsItems');
            self.couponsItems = [];//存储红包数据
            self.currentActIdNum = 0; //当前可使用的同一个activityId的上限数
            self.currentTypeIdNum = 0;//当前可使用的同一个 typeId 的上限数
            self.currentMutexTypeId = [];//与当前操作互斥的typeId
            self.currenntMutexActId = [];//与当前操作互斥的actId
            self.isUseCoupons = false;//当前是否使用红包

            self.checkedList = [];//已经选中的
            self.unCheckedList = [];//没有选中的

            self.getHongbaoData();

            self.eventHanding();
        },
        eventHanding:function(){
            var self = this;

            //
            self.J_couponsItems.on('click','.J_coupCheckBox',function(e){
                var that = this;

                var _data = $(this).data('data');

                // console.log(_data.useConstraint);

                var _typeIdNum = self.getTypeId(_data.useConstraint);
                var _actIdNum = self.getActivityId(_data.useConstraint);


                var rs = {
                    selfTypeId:_data.typeId,
                    selfActId:_data.activityId,
                    selfMaxUseTypeIdNum:(function(){
                        var _num = 0;
                        if(_typeIdNum.length){
                            _num = _typeIdNum[0];
                        }
                        return _num;
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
                                var _d = self.J_couponsItems.find('input[typeid="'+ _mutexTpId[i] +'"]').attr('activityid');
                                if(_d){
                                    _allActId.push(_d);
                                }else{
                                    continue;
                                }
                            }
                            if(_typeIdNum.length < 1){
                                var J_unClickMuteActId = self.J_couponsItems.find('input[isChecked=0][typeid=' + _data.typeId + ']');
                                for(var i = 0 ; i < J_unClickMuteActId.length; i ++){
                                    var _act = J_unClickMuteActId.eq(i).attr('activityid');
                                    _mutexActId.push(_act);
                                }

                                _allActId = _allActId.concat(_mutexActId);
                            }


                        }

                        if(!_mutexActId.length){
                            var _unCheckInput = self.J_couponsItems.find('input[disabled!=disabled]');
                            for(var i = 0; i < _unCheckInput.length;i++){
                                if(self.getUnActivityId($(_unCheckInput).eq(i).attr('useconstraint')).join(',').indexOf(_data.activityId) > -1){
                                    _mutexActId.push($(_unCheckInput).eq(i).attr('activityid'));
                                }
                            }

                        }


                        return _allActId;
                    })()

                };



               // console.log(rs);


                //选中当前
                if($(this).is(':checked')){
                    $(this).attr('isChecked','1');

                    if(_typeIdNum.length){
                        if(self.J_couponsItems.find('input[isChecked=1][typeid='+ rs.selfTypeId +']').length >= _typeIdNum){
                            self.J_couponsItems.find('input[isChecked=0][typeid='+ rs.selfTypeId +']').attr('disabled','disabled');
                        }
                    }

                    if(rs.selfMutexActId.length){
                        for(var i = 0; i < rs.selfMutexActId.length; i++){
                            self.J_couponsItems.find('input[isChecked=0][activityid='+ rs.selfMutexActId[i] +']').attr('disabled','disabled');
                        }
                    }

                    if(rs.selfMaxUseTypeIdNum > 0){
                        self.currentTypeIdNum = rs.selfMaxUseTypeIdNum;
                        var _num = self.J_couponsItems.find('input:checked[activityid="'+ _data.activityId +'"]').length;
                        if(_num >= self.currentTypeIdNum){
                            self.J_couponsItems.find(':checkbox:not(:checked)[activityid="'+ _data.activityId +'"]').attr('disabled','disabled');
                        }
                    }

                    if(rs.selfMaxUseActIdNum > 0){
                        self.currentTypeIdNum = rs.selfMaxUseActIdNum;
                        var _num = self.J_couponsItems.find('input:checked[activityid="'+ _data.activityId +'"]').length;
                        if(_num >= self.currentTypeIdNum){
                            self.J_couponsItems.find(':checkbox:not(:checked)[activityid="'+ _data.activityId +'"]').attr('disabled','disabled');
                        }
                    }



                    var _unCheckInput = self.J_couponsItems.find(':checkbox:not(:checked)[disabled!="disabled"]');
                    //console.log(_unCheckInput);
                    if(_unCheckInput && _unCheckInput.length){
                        $.each(_unCheckInput,function(idx,ele){

                            if(self.getUnTypeId($(ele).attr('useconstraint')).join(',').indexOf(_data.typeId) > -1){
                                //console.log(self.getUnTypeId($(ele).attr('useconstraint')).join(','));
                                $(ele).attr('disabled','disabled');

                            }
                        });

                    }

                    self.unCheckedList=[];
                    for(var i = 0; i < self.J_couponsItems.find(':checkbox:not(:checked)[disabled="disabled"]').length;i++){
                        self.unCheckedList.push(self.J_couponsItems.find(':checkbox:not(:checked)[disabled="disabled"]').eq(i).data('data')
                        );
                    }

                    //console.log(self.unCheckedList);


                }else{
                    //取消选中
                    $(this).attr('isChecked','0');
                    if(self.unCheckedList.length){
                        for(var i = 0; i < self.unCheckedList.length; i++){
                            self.J_couponsItems.find('input[disabled="disabled"][activityid="'+ self.unCheckedList[i].activityId +'"]').removeAttr('disabled');
                        }
                    }

                    self.J_couponsItems.find('input').removeAttr('disabled').attr('checked',false).attr('isChecked','0');

                }

                //计算减免价格
                self.deratePrice();

            });
        },
        deratePrice:function(){
            var self = this;
            var J_price = $('#vip-youhui');
            var J_totalFee = $('#totalFee');
            var checkedinput = self.J_couponsItems.find('input:checked');
            var _price = 0;
            //console.log(checkedinput);
            if(checkedinput.length){
                for(var i = 0; i < checkedinput.length; i++){
                    _price += $(checkedinput).eq(i).data('data').discount
                }
            }

            J_price.html(Number(_price).toFixed(2));

            var _totalPrice = $('#cart-total-price span').html().replace(/\\uffe5|\,|￥/g, "")

            var _totalFee = Number(Number(_totalPrice).toFixed(2) - Number(_price).toFixed(2)).toFixed(2);
            if(_totalFee <= 0){
                _totalFee = 0;
            }
            J_totalFee.html("￥ " + _totalFee);


        },
        getAjaxData:function(url,data,type,callBack){
            var self = this;

            if(data){
                data = data;
            }else{
                data={}
            }

            $.ajax({
                url:url,
                type:type,
                data:data,
                dataType:'json',
                success:function(rs){
                    if(rs.code == 200){
                        var  _coupons = rs.data.userCouponList;
                        if(!_coupons.length){
                            return false;
                        }

                        self.couponsItems = _coupons;

                        //var _html = [];
                        var _html = "";
                        for(var i= 0 ; i < _coupons.length; i++){

                            //var _item += '<li class="cart-vip-item clearfix radio">' +
                            _html += '<li class="cart-vip-item clearfix radio">' +
                                '<label for="vip1">' +
                                '<input class="J_coupCheckBox cart-vip-input" type="checkbox" isChecked="0" ' +
                                'data-id="'+ _coupons[i].id+'" ' +
                                'data-coupon-type="'+ _coupons[i].actCode+'" ' +
                                'id="'+ _coupons[i].id+'" ' +
                                'activityid="'+ _coupons[i].activityId +'" ' +
                                'useConstraint="'+ _coupons[i].useConstraint +'" ' +
                                'typeid="'+ _coupons[i].typeId +'" ' +
                                'minPrice="'+ _coupons[i].minPrice +'" ' +
                                'data-numb="'+ _coupons[i].discount +'">' +
                                '<span class="cart-vip-name">'+ _coupons[i].title +'('+ _coupons[i].discount +'元)' +

                                //' id:' + _coupons[i].id + '  ' +
                                //'activityId:' + _coupons[i].activityId + '  ' +
                                //'useConstraint:' + _coupons[i].useConstraint + '   ' +
                                //'typeid:' + _coupons[i].typeId +

                                '</span>' +
                                self.returnTime(_coupons[i]) +
                                '</label>' +
                                '</li>';
                            //_html.push(_item);

                        }
                        //_html = _html.join(' ').replace(/,/g,'');

                        self.J_couponsItems.empty().append(_html);
                        if(callBack){
                            callBack(_coupons);
                        }

                    }else{
                        return false;
                    }
                }
            });
        },
        returnTime:function(rs){
          var self = this;
            var _html = '';

            if(rs.validFrom && rs.validTo){

                _html = '<span class="act-time">使用期限' + self.formatTime(rs.validFrom) + '至' + self.formatTime(rs.validTo)  +'</span>';
            }else{
                _html = '<span class="act-time">使用时间从'+ self.formatTime(rs.createdAt) +'开始</span>'
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
                _str = date.getFullYear() + '-' + month + '-' + currentDate;
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
        },

        getHongbaoData:function(){
            var self = this;
            /*
             * ouer/cart/coupon/list
             {
             skuJson = "{\"5a0o438z\":\"1\"}";
             zoneId = 4;
             }
             * */
            //var _url = '/resources/testdata/hongbaolist.json'

            var _url = '/ouer/cart/coupon/list'
            var _data = {
                skuJson:(function(){
                    var _skuArry = [];
                    var _item = {};

                    var J_item = $('.J_productItem');
                    for(var i = 0; i < J_item.length; i++){
                        var _sku = J_item.eq(i).attr('data-sku-id');
                        var _num = J_item.eq(i).find('.J_prdAmount').html();
                        _item[_sku] = _num;
                    }

                    _skuArry.push(_item);

                    return JSON.stringify(_skuArry[0]);
                })(),
                t: $.now()
            };
            self.getAjaxData(_url,_data,'post',function(rs){
                var J_coupCheckBox = self.J_couponsItems.find('.J_coupCheckBox');
                // console.log(self.couponsItems);
                for(var i = 0; i < J_coupCheckBox.length; i++){
                    J_coupCheckBox.eq(i).data('data',rs[i]);
                }

            });


        },
        unique:function(arr){
            var self = this;
            //去重
            var result = [],hash = {};
            for(var i = 0,elem; (elem = arr[i]) != null;i++){
                if(!hash[elem]){
                    result.push(elem);
                    hash[elem] = true;
                }
            }
            return result;
        },
        uqiqueObj:function(arr){
            var self = this;
            //数组对象去重
            var _obj = [];
            arr = arr.sort(self.sortStyle('mutexActId'));
            for(var k = 0; k < arr.length; k++){
                _obj.push(arr[k]);
            }

            for(var i = 0,j = i+1;i<_obj.length;i++){
                if(_obj[i].mutexActId == _obj[j].mutexActId && _obj[i].mutexTypeId == _obj[j].mutexTypeId){
                    _obj.splice(j,1);
                }
            }
           // console.log(_obj);
            return _obj;
        },
        sortStyle:function(pro){
            var self = this;
            //数组排序方法
            return function(obj1,obj2){
                var val1 = obj1[pro];
                var val2 = obj2[pro];
                if(val1 < val2){
                    return -1;
                }else if(val1 > val2){
                    return 1;
                }else{
                    return 0;
                }
            }
        }
    }

    $(function(){
        hongbaoModule.init();
    });
})();

