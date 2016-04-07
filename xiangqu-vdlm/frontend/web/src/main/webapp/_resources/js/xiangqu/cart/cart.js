/**
 * Created by ziyu on 2016/2/19.
 */

(function($){

    new FastClick(document.body);
    var isTest = false;
    var APP = {
        pricing:'/cart/pricing',//店铺更新价格
        del:'/cart/delete',//删除
        update:'/cart/update',//更新购物车
        bindPhoneIsPic:isTest ? '/_resources/testdata/phoneCheck.json' :'/xiangqu/wap/user/phoneCheck',//是否要显示图片验证码
        sendSms:isTest?'/_resources/testdata/sendSms.json':'/xiangqu/wap/user/sendSms',
        bindPhone:'/xiangqu/wap/user/bindPhone'//绑定手机
    };
    var CartModule = {
        init:function(){
            var self = this;
            self.J_cart = $('.J_cart');
            self.J_tc = self.J_cart.find('.J_tc');
            self.J_bindPhoneBox = self.J_cart.find('.J_bindPhoneBox');
            self.J_js_hrefs=$('.J_js_hrefs');
            self.SMSTimer = null;
            self.key = '';

            self.eventHanding();

        },
        eventHanding:function(){
            var self  = this;

            //app跳转
            self.J_js_hrefs.on('tap',function(e){
                e.preventDefault();
                var _that=$(this);
                var _productId = _that.attr('data-href');
                self.ifHasApp({
                    src:'xiangqu://',
                    skipSrc:'xiangqu://'+'skip#{"destinationPage":"1","isNeedLogin":"0","param":{"productId":"'+ _productId +'"}}',
                    productId:(function(){
                        return _productId;
                    })(),
                    failSrc:"http://www.xiangqu.com/dtl/" + _productId +".html"
                })
            })
            //返回
            self.J_cart.on('tap','.J_cartBack',function(e){
                e.preventDefault();
                if(window.history.length > 1){
                    window.history.go(-1);
                }else{
                    window.location.href = window.host;
                }
            });

            //结算
            self.J_cart.on('tap','.J_cartPay',function(e){
                e.preventDefault();

                if(!self.isChecked()){
                    alert('请选择商品进行结算');
                    return false;
                }else{
                    var isBindPhone = self.J_cart.attr('data-isbindphone');
                    isBindPhone = isBindPhone == 'true' ? true:false;
                    isBindPhone = true;
                    if(!isBindPhone){
                        self.J_cart.find('.J_hd').hide();
                        self.J_cart.find('.J_bindPhoneBox').show();
                    }else{
                        self.returnSkus(function(str){
                            window.location.href = '/xiangqu/wap/cart/next?' + str;
                        });
                    }
                }
            });

            //取消绑定
            self.J_bindPhoneBox.on('tap','.J_bindCancel',function(e){
                e.preventDefault();
                self.J_bindPhoneBox.hide();
                self.J_cart.find('.J_hd').show();
                self.clearTime();
                self.clearBindForm();
            });

            //是否显示 图片
            self.J_bindPhoneBox.on('blur','.J_phoneNum',function(){
                var _str = $(this).val();
                var that = this;
                if(!_str){
                    $(this).attr('placeholder','请填写手机号码');
                    return false;
                }
                if(!self.checkPhone(_str)){
                    alert('手机号码不正确');
                    $(this).focus();
                    return false;
                }else{
                    var data = {
                        phone:_str,
                        verifyFrom:'BIND_PHONE',
                        t: self.returnTime()
                    };
                    self.getIsPicCode(data,function(rs){
                        if(rs.code != 200){
                            alert(rs.msg);
                            return false;
                        }

                        if(rs.data && rs.data.isShowPicCode){
                            var ele = $(that).closest('ul').find('.J_picCodeBox');
                            ele.attr('data-isshowpiccode',1);
                            var _data = {
                                uniqueKey:'',
                                verifyFrom:'BIND_PHONE'
                            }
                            self.updatePicCode(ele,_data);
                        }
                        self.key = rs.data.key;

                    });
                }
            });
            //刷新图片验证码
            self.J_bindPhoneBox.on('click','.J_codeNumBtn',function(e){
                e.preventDefault();
                var _data = {
                    uniqueKey:'',
                    verifyFrom:'BIND_PHONE'
                }
                var ele = $(this).closest('ul').find('.J_picCodeBox');
                self.updatePicCode(ele,_data);

            });

            //获取手机验证码
            self.J_bindPhoneBox.on('click','.J_phoneCode',function(e){
                e.preventDefault();

                var _that = this,
                    url = APP.sendSms;
                if($(_that).attr('data-isable') != 1){
                    return false;
                }
                var _val = self.J_bindPhoneBox.find('.J_phoneNum').val();
                if(!_val || !self.checkPhone(_val)){
                    self.J_bindPhoneBox.find('.J_phoneNum').attr('placeholder','请填写正确的手机号码').focus();
                    return false;
                }
                if(!self.key){
                    return false;
                }
                var data = {};
                if(self.J_bindPhoneBox.find('.J_picCodeBox').attr('data-isshowpiccode') == 1){
                    var _data = {
                        uniqueKey:'',
                        verifyFrom:'BIND_PHONE'
                    }
                    var ele = $(_that).closest('ul').find('.J_picCodeBox');


                    data = {
                        phone:_val,
                        source:'BIND_PHONE',
                        imgCode:(function(){
                            var _imgCode = $(_that).closest('ul').find('.J_codeNum');
                            if(!self.trim(_imgCode.val())){
                                _imgCode.attr('placeholder','请输入图片验证码').focus();
                                self.updatePicCode(ele,_data);

                            }
                            return self.trim(_imgCode.val());
                        })(),
                        sign:(function(){
                            var _str = _val + 'BIND_PHONE' + self.key;
                            return $.md5(_str);
                        })(),
                        t:self.returnTime()
                    };
                }else{
                    data = {
                        phone:_val,
                        source:'BIND_PHONE',
                        imgCode:(function(){
                            return '';
                        })(),
                        sign:(function(){
                            var _str = _val + 'BIND_PHONE' + self.key;
                            return $.md5(_str);
                        })(),
                        t:self.returnTime()
                    };
                }

                if(self.J_bindPhoneBox.find('.J_picCodeBox').attr('data-isshowpiccode') == 1){
                    if(!data.imgCode){
                        self.updatePicCode(ele,_data);
                        return false;
                    }
                }

                self.getPhoneCode(url,data,function(rs){
                    if(rs.code == 400){
                        alert(rs.msg);

                    }else if(rs.code == 200){
                        $(_that).attr('data-isable',0);
                        if(rs.data.isShowPicCode){
                            self.J_bindPhoneBox.find('.J_picCodeBox').show();
                        }else{
                            self.J_bindPhoneBox.find('.J_picCodeBox').removeAttr('data-isshowpiccode').hide();
                        }
                        self.sendSMSsuccess(_that,120);
                        return false;
                    }else if(rs.code == 401){
                        location.href = '/login.html?redirectUrl=' + encodeURI(location.href);
                    }else{
                        alert(rs.msg);
                        self.updatePicCode(ele,_data);
                    }
                });
            });

            //确定绑定
            self.J_bindPhoneBox.on('click','.J_bindPhoneSure',function(e){
                e.preventDefault();

                self.sureBindPhone();

            });

            //店铺更新价格
            self.J_cart.on('click','.J_checkProAll',function(e){
                e.preventDefault();
                var _hasChecked = $(this).hasClass('active');
                if(!_hasChecked){
                    // skuIds=715zxi7o&skuIds=cakyfjan&_=1455952392104
                    var _skuid = [];
                    $(this).closest('.J_cartItems').siblings('.J_cartItems').find('.J_cratProCheck[class*="active"]').each(function(i,item){
                        _skuid.push($(item).closest('.cart-product-item').attr('data-sku-id'));
                    });
                    $(this).closest('.J_cartItems').find('.cart-product-item').each(function(i,item){
                        _skuid.push($(item).attr('data-sku-id'));
                    });

                    $(this).addClass('active');
                    $(this).closest('.J_cartItems').find('.J_cratProCheck').addClass('active');

                    self.isCheckedAll();

                    self.getTotalFee(_skuid);

                }else{
                    $(this).removeClass('active');
                    $(this).closest('.J_cartItems').find('.J_cratProCheck').removeClass('active');

                    self.isCheckedAll();

                    var _skuid = [];
                    $(this).closest('.J_cartItems').siblings('.J_cartItems').find('.J_cratProCheck[class*="active"]').each(function(i,item){
                        _skuid.push($(item).closest('.cart-product-item').attr('data-sku-id'));
                    });
                    if(!_skuid.length){
                        self.J_cart.find('.J_cartTotal').find('.cart-red em').html("0.00");
                        return false;
                    }

                    self.getTotalFee(_skuid);


                }
            });

            //全选
            self.J_cart.find('.J_cartTotal').on('click','.J_checkAll .check-all ',function(e){
                e.preventDefault();

                var _isCheck = $(this).hasClass('active');
                if(!_isCheck){
                    self.J_cart.find('.J_checkProAll').addClass('active');
                    self.J_cart.find('.J_cratProCheck').addClass('active');
                    $(this).addClass('active');
                    var _skuid = [];

                    self.J_cart.find('.cart-product-item').each(function(i,item){
                        _skuid.push($(item).attr('data-sku-id'));
                    });

                    self.getTotalFee(_skuid);
                }else{
                    self.J_cart.find('.J_checkProAll').removeClass('active');
                    self.J_cart.find('.J_cratProCheck').removeClass('active');
                    $(this).removeClass('active');
                    self.J_cart.find('.J_cartTotal').find('.cart-red em').html("0.00");
                    return false;
                }

            });

            //散选
            self.J_cart.on('click','.J_cratProCheck',function(e){
                e.preventDefault();

                var _isCheck = $(this).hasClass('active');

                if(!_isCheck){
                    $(this).addClass('active');

                    self.isCheckedAll();

                    var _skuid = [];
                    self.J_cart.find('.J_cratProCheck[class*="active"]').each(function(i,item){
                        _skuid.push($(item).closest('.cart-product-item').attr('data-sku-id'));
                    });

                    var _J_cartItems = $(this).closest('.J_cartItems');

                    var _siblings = _J_cartItems.find('.J_cratProCheck[class*="active"]');
                    var _currents = _J_cartItems.find('.cartProduct');
                    if(_currents.length == _siblings.length){
                        _J_cartItems.find('.J_checkProAll').addClass('active');
                    }else{
                        _J_cartItems.find('.J_checkProAll').removeClass('active');
                    }

                    self.getTotalFee(_skuid);

                }else{
                    $(this).removeClass('active');

                    self.J_cart.find('.J_checkAll .check-all').removeClass('active');


                    var _skuid = [];
                    self.J_cart.find('.J_cratProCheck[class*="active"]').each(function(i,item){
                        _skuid.push($(item).closest('.cart-product-item').attr('data-sku-id'));
                    });

                    var _J_cartItems = $(this).closest('.J_cartItems');

                    var _siblings = _J_cartItems.find('.J_cratProCheck[class*="active"]');
                    var _currents = _J_cartItems.find('.cartProduct');
                    if(_currents.length == _siblings.length){
                        _J_cartItems.find('.J_checkProAll').addClass('active');
                    }else{
                        _J_cartItems.find('.J_checkProAll').removeClass('active');
                    }

                    self.delTotalFee();

                }

            });

            //删除
            self.J_cart.on('tap','.J_delProduct',function(e){
                e.preventDefault();
                //?itemId=2415776

                var _J_cruuent = $(this).closest('.cartProduct');
                var itemId = $(this).attr('data-item-id');

                self.J_tc.find('.J_tips').attr('data-itemid',itemId).show();

                self.J_tc.show();



            });
            // 取消删除
            self.J_tc.on('click','.J_cancel',function(e){
                e.preventDefault();
                self.J_tc.find('.J_tips').removeAttr('data-itemid').hide();
                self.J_tc.hide();
            });

            //确定删除
            self.J_tc.on('click','.J_sure',function(e){
                e.preventDefault();
                var itemId = $(this).closest('.J_tips').attr('data-itemid');
                var  _data = {
                    itemId:itemId
                };

                self.delProduct(_data,function(rs){
                    if(!rs){
                        alert('服务器异常，请刷新页面后再试！');
                        return false;
                    }

                    if(rs && rs.code != 200){
                        alert(rs.msg);
                        return false;
                    }

                    var _J_current = self.J_cart.find('.J_delProduct[data-item-id*="'+ itemId +'"]').closest('.J_cartItems');
                    var _currentLen = _J_current.find('.cartProduct');
                    var _current = self.J_cart.find('.J_delProduct[data-item-id*="'+ itemId +'"]').closest('.cartProduct');
                    if(_currentLen.length <= 1){
                        _J_current.remove();
                    }else{
                        _current.remove();
                    }

                    self.delTotalFee();

                    self.J_tc.find('.J_tips').removeAttr('data-itemid').hide();
                    self.J_tc.hide();

                });
            });

            //加购物车
            self.J_cart.on('tap','.J_addNum',function(e){
                e.preventDefault();
                var that = this;
                self.updateCart(that);
            });

            self.J_cart.on('tap','.J_jianNum',function(e){
                e.preventDefault();
                var that = this;
                self.updateCart(that);
            });

        },
        sureBindPhone:function(){
            var self = this;
            var _data = {
                phone:self.J_bindPhoneBox.find('.J_phoneNum').val(),
                smsCode:self.J_bindPhoneBox.find('.J_imgCode').val(),
                t:self.returnTime()
            };

            if(!_data.phone || !self.checkPhone(_data.phone)){
                self.J_bindPhoneBox.find('.J_phoneNum').attr('placeholder','请填写正确的手机号码').focus();
                return false;
            }
            if(!_data.smsCode){
                self.J_bindPhoneBox.find('.J_imgCode').focus();
                return false;
            }

            $.ajax({
                url:APP.bindPhone,
                type:'post',
                data:_data,
                dataType:'json',
                success:function(rs){
                    if(!rs){
                        alert('服务器异常请刷新页面再试');
                        return false;
                    }
                    if(rs && rs.code != 200){
                        alert(rs.msg);
                        return false;
                    }
                    self.returnSkus(function(str){
                        window.location.href = '/xiangqu/wap/cart/next?' + str;
                    });
                },
                error:function(req,tst,err){
                    alert('系统正在维护中...');
                }
            });

        },
        returnSkus:function(callBack){
            var self = this;

            var objSkuStr;
            var skuid = [];

            var obj = self.J_cart.find('.J_cratProCheck[class*="active"]');
            obj.each(function(i,item){
                skuid.push($(item).closest('.cart-product-item').attr('data-sku-id'));
            });

            objSkuStr = 'skuId=' + skuid.join('&skuId=');

            if(callBack){
                callBack(objSkuStr);
            }

        },
        returnTime:function(){
            var self = this;
            var _t = new Date();
            return _t.getTime();
        },
        getPhoneCode:function(url,data,callBack){
            //获取手机验证码
            var isSending = false;
            $.ajax({
                url:url,
                type:'post',
                data:data,
                dataType:'json',
                timeout:50000,
                beforeSend:function(){
                    isSending = true;
                },
                complete:function(){
                    isSending = false;
                },
                success:function(rs){

                    if(callBack){
                        callBack(rs);
                    }
                },
                error:function(req,tst,err){
                    alert('系统正在维护中...');
                }
            });
        },
        sendSMSsuccess:function(ele,time){
            var self = this;
            ele = $(ele);
            var waitTime = time,
                oldBtnText = ele.html(),
                TIMESTR = '重新获取';
            ele.html(waitTime + 's');
            ele.addClass('checked');
            intervalSMS();
            function intervalSMS(){
                var start = waitTime;
                self.SMSTimer = setInterval(function(){
                    start --;
                    ele.html(start + 's');
                    if(start == 0){
                        ele.attr('data-isable',1);
                        clearInterval(self.SMSTimer);
                        ele.html(TIMESTR);
                        ele.removeClass('checked');
                    }
                },1000);
            }
        },
        clearTime:function(){
            var self =this;
            if(self.SMSTimer){
                clearInterval(self.SMSTimer);
                self.J_bindPhoneBox.find('.J_phoneCode').html('获取验证码').attr('data-isable',1).removeClass('checked');

            }
        },
        clearBindForm:function(){
            var self = this;
            self.J_bindPhoneBox.find('.J_phoneNum').val('');
            self.J_bindPhoneBox.find('.J_imgCode').val('');
            self.J_bindPhoneBox.find('.J_codeNum').val('');

            self.J_bindPhoneBox.find('.J_picCodeBox').removeAttr('data-isshowpiccode').hide().find('.J_codeNum').val();
        },
        getIsPicCode:function(data,cb){
            var self = this;
            $.ajax({
                url:APP.bindPhoneIsPic,
                data:data,
                dataType:'json',
                success:function(rs){
                    if(!rs){
                        alert('服务器异常请刷新页面再试');
                        return false;
                    }

                    if(cb){
                        cb(rs);
                    }
                },
                error:function(req,tst,err){
                    alert('系统正在维护中...');
                }
            });
        },
        updatePicCode:function(ele,option){
            var self = this;
            var HOST = window.location.host.indexOf('xiangqutest') > -1?"http://www.xiangqutest.com" :'http://www.xiangqu.com';
            var _picUrl =  '/common/getVerifyCode';
            var _url = '';
            if(option){
                _url = HOST + _picUrl + '?uniqueKey='+ option.uniqueKey  +'&verifyFrom=' + option.verifyFrom;
            }else{
                _url = HOST + _picUrl + '?uniqueKey=&verifyFrom=';
            }
            var t = (new Date()).getTime();
            ele.find('.J_codeNumBtn img').attr('src',_url + '&t=' + t);
            ele.show();
            ele.find('J_codeNum').val('');

        },
        updateCart:function(ele,callback){
            var self = this;
            var J_curent = $(ele).closest('.cart-product-item');

            var amount = J_curent.attr('data-inventory');
            var skuId = J_curent.attr('data-sku-id');

            var oldNumb = J_curent.find('.J_current').html();
            var isAdd = $(ele).hasClass('J_addNum');

            var newNumb = isAdd ? (Number(oldNumb) + 1):(Number(oldNumb - 1));

            if(!isAdd && newNumb < 0){
                newNumb = 1;
            }
            if(newNumb > amount){
                alert('已经大于当前库存');
                newNumb = amount;
                return false;
            }

            $.ajax({
                url:APP.update,
                type:'post',
                data:{
                    skuId:skuId,
                    amount:newNumb
                },
                dataType:'json',
                success:function(rs){
                    if(!rs){
                        alert('服务器异常，请刷新页面后再试');
                        return false;
                    }
                    if(rs && rs.code != 200){
                        alert(rs.msg);
                        return false;
                    }

                    J_curent.find('.J_current').html(newNumb);

                    self.delTotalFee();

                    if(callback){
                        callback(rs);
                    }
                },
                error:function(req,tst,err){
                    alert('系统正在维护中...');
                }

            });
        },
        delProduct:function(_data,callback){
            var self = this;
            $.ajax({
                url:APP.del,
                type:'get',
                data:_data,
                dataType:'json',
                traditional:true,
                success:function(rs){
                    if(callback){
                        callback(rs);
                    }
                },
                error:function(req,tst,err){
                    alert('系统正在维护中...');
                }
            })
        },
        delTotalFee:function(){
            var self = this;
            var _skuid = [];
            self.J_cart.find('.J_cratProCheck[class*="active"]').each(function(i,item){
                _skuid.push($(item).closest('.cart-product-item').attr('data-sku-id'));
            });

            var _data = {
                skuIds:_skuid
            };

            if(!_skuid.length){
                self.J_cart.find('.J_cartTotal').find('.cart-red em').html("0.00");
                self.J_cart.find('.J_checkAll').find('.check-all').removeClass('active');

            }else{
                self.getPriceing(_data,function(rs){
                    if(!rs){
                        alert('服务器异常，请刷新页面后再试');
                        return false;
                    }
                    if(rs && rs.code != 200){
                        alert(rs.msg);
                        return false;
                    }
                    self.J_cart.find('.J_cartTotal').find('.cart-red em').html(rs.data.totalFee);

                });
            }

        },
        getTotalFee:function(skuId,callback){
            var self = this;
            var _data = {
                skuIds:skuId
            };
            self.getPriceing(_data,function(rs){
                if(!rs){
                    alert('服务器异常，请刷新页面后再试');
                    return false;
                }
                if(rs && rs.code != 200){
                    alert(rs.msg);
                    return false;
                }
                self.J_cart.find('.J_cartTotal').find('.cart-red em').html(rs.data.totalFee);

                if(callback){
                    callback();
                }
            });
        },
        isCheckedAll:function(){
            var self = this;
            var _J_checkedAll = self.J_cart.find('.J_cratProCheck[class*="active"]');
            var _J_checkAll = self.J_cart.find('.J_cratProCheck');
            if(_J_checkedAll.length == _J_checkAll.length){
                self.J_cart.find('.J_checkAll .check-all').addClass('active');
            }else{
                self.J_cart.find('.J_checkAll .check-all').removeClass('active');
            }
        },
        isChecked:function(){
            var self = this;
            var _isChecked = self.J_cart.find('.J_cratProCheck[class*="active"]');
            if(_isChecked.length){
                return true;
            }else{
                return false;
            }

        },
        getPriceing:function(data,callback){
            var slef = this;
            $.ajax({
                url:APP.pricing,
                type:'get',
                data:data,
                traditional:true,
                cache:false,
                dataType:'json',
                success:function(rs){
                    if(callback){
                        callback(rs);
                    }
                },
                error:function(req,tst,err){
                    alert('系统正在维护中...');
                }
            })
        },
        checkPhone:function(str){
            var self = this;
            var re = /^1[3|4|5|7|8]\d{9}$/g;
            return re.test(str)?true:false;
        },
        trim:function(str){
            var self = this;
            return str.replace(/(^\s*)|(\s*$)/g,'');
        },
        ifHasApp:function(config){
            var self = this;
            //检测当前设备有没有安装 app，没有返回false;
            var osrc,
                t = 1000,
                hasApp = true;

            var browser = {
                versions:(function(){
                    var u = navigator.userAgent,
                        app = navigator.appVersion;
                    return {
                        ios: !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/),
                        android: u.indexOf('Android') > -1 || u.indexOf('Linux') > -1
                    }
                })(),
                language:(navigator.browserLanguage || navigator.language).toLowerCase()
            };

            if(browser.versions.android){
                osrc=config.skipSrc;
            }else if(browser.versions.ios){
                osrc=config.skipSrc;
            }
            function try_to_open_app(t1){
                var t2 = Date.now();
                if(!t1 || t2 - t1 < t + 200){
                    hasApp = false;
                }
            }
            window.location.href = config.failSrc;
            var tout,tr,
                t1 = Date.now();
            clearTimeout(tout);
            clearTimeout(tr);
            if(browser.versions.android){
                var ifr = document.createElement('iframe');
                ifr.src = osrc;
                ifr.style.display = 'none';
                document.body.appendChild(ifr);
            }
            if(browser.versions.ios){
                //alert(osrc);
                var ifr = document.createElement('iframe');
                ifr.src = osrc;
                ifr.style.display = 'none';
                document.body.appendChild(ifr);
                //window.location.href=osrc;
            }
            tout = setTimeout(function(){
                try_to_open_app(t1);
            },t);
        }
    };

    $(function(){
        CartModule.init();
    })
})(Zepto);

