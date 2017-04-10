/**
 * Created by Think-ziyu on 2015/7/9.
 */
(function($){
    /*detail function*/

    var DetailModule = {
        init:function(){
            var self = this;
            self.J_wapSiteShop = $('.J_wapSiteShop');
            self.J_detailInfos = $('.J_detailInfos');
            self.J_detailInfo = $('#J_detailInfo');
            self.J_skuBox = $('.J_skuBox');

            self.J_buyCart = $('.J_buyCart');

            self.isDetailLoading = true;
            self.isLoading = false;
            self.isFistClick = true;
            self.isEnd = false;
            self._page = 0;

			self.isAddCart = false;
			self.skuInit();
            //self.getAlikeDate();
            self.eventHanding();
            self.isLogin();

        },
        eventHanding:function(){
            var self = this;


            //加载评论数据
            $(window).on('scroll',function(e){

                self.setTabFixed();

                if(self.J_detailInfo.find('.J_comment').hasClass('active')){
                    var isH = $(window).scrollTop() + $(window).height() >= $(document).height() - 370;

                    if(isH) {

                        if(self.isLoading){
                            return false;
                        }

                        self.isLoading = true;

                        self.getUserComment();
                    }
                }

                //加载商品描述内容
                /*if(self.J_detailInfo.find('.J_detailDes').hasClass('active')){

                    if(self.isDetailLoading && $(window).scrollTop() > 100){
                        self.getDetailPics();
                    }
                }*/




            });

            //点击购物车
            self.J_wapSiteShop.on('click','.J_shopCart',function(e){
                //e.preventDefault();
                if(XIANGQU.userId == ""){
                    window.location.href = XIANGQU.loginUrl + "?redirectUrl=" + window.location.href;
                    return false;
                }
            });

            //喜欢
            self.J_detailInfos.on('click','.J_detailLike',function(e){
                e.preventDefault();
                if(XIANGQU.userId == ''){
                    window.location.href = XIANGQU.loginUrl + "?redirectUrl=" + window.location.href;
                    return false;
                }
                var isLike = $(this).hasClass('has-faver-true');

                if(isLike){
                    self.unLike();
                }else{
                    self.addLike();
                }


            })



            //切换详情评论
            self.J_detailInfo.on('click','.detail-tab-nav li',function(e){

                 e.preventDefault();
                 var i = $(this).index();

                 self.setTabFixed();

                 $(this).addClass('active').siblings('li').removeClass('active');
                 $(this).closest('.detail-tab-nav').siblings('.detail-tab-cont').find('.tab-item').eq(i).show().siblings('.tab-item').hide();

                 if($(this).hasClass('J_comment')){

                     if(self.isFistClick){
                         self.getUserComment();
                         self.isFistClick = false;
                     }

                 }
             });

            //是否显示 加入购物车
            /*if(XIANGQU.fromType != 1){

            }*/

            //立即购买

            self.J_buyCart.on('click','.J_btnBuy',function(e){
                e.preventDefault();
				if($(this).hasClass('disable-selected-btn')){
					alert('该商品已经下架！');
					return false;
				}
                if(XIANGQU.userId == ''){
                    window.location.href = XIANGQU.loginUrl + "?redirectUrl=" + window.location.href;
                    return false;
                }else{
                    //已登录，显示sku
                    $('html,body').css({'overflow':'hidden'})
					self.isAddCart = false;
                    self.J_skuBox.find('.J_btnSubmitSku').attr('data-spm',$(this).attr('data-spm'));
					self.J_skuBox.show();
                }

            });
            //加入购物车
            self.J_buyCart.on('click','.J_btnAddCart',function(e){
                e.preventDefault();
				if($(this).hasClass('disable-selected-btn')){
					alert('该商品已经下架！');
					return false;
				}
                if(XIANGQU.userId == ''){
                    window.location.href = XIANGQU.loginUrl + "?redirectUrl=" + window.location.href;
                    return false;
                }else{
                    //已登录，显示sku
                    $('html,body').css({'overflow':'hidden'})
					self.isAddCart = true;
                    self.J_skuBox.find('.J_btnSubmitSku').attr('data-spm',$(this).attr('data-spm'));
					self.J_skuBox.show();
                }

            });

        //    关闭sku
            self.J_skuBox.on('click','.J_closeSku',function(e){
                e.preventDefault();

				self.isAddCart = false;
                $('html,body').css({'overflow':'visible'});
                self.J_skuBox.addClass('fadeInDownBig');
				self.J_skuBox.find('.proskuitem').removeClass('sku-selected');
				self.J_skuBox.find('#skuId').val('');
				self.J_skuBox.find('.J_detailNum').val('1');
				self.J_skuBox.find('.J_skuPrice dd').html('');
               	self.J_skuBox.removeClass('fadeInUpBig');
				self.J_skuBox.removeAttr('style');
            });


        },
        addLike:function(){
            var self = this;
            $.ajax({
                url: '/feed/faver',
                type: 'POST',
                data:{
                    "favUserId":XIANGQU.userId,
                    "productId": self.J_detailInfos.attr('data-id'),
                    "otherRefer":document.referrer
                },
                dataType: 'json',
                success:function(rs){
                    if(rs.code != 2000){
                        return false;
                    }
                    self.J_detailInfos.find('.J_likeNum').html(rs.favNum);
                    self.J_detailInfos.find('.J_detailLike').addClass('has-faver-true');
                }
            })
        },
        unLike:function(){
            var self = this;
            $.ajax({
                url:'/feed/notfaver',
                type: 'POST',
                data:{
                    "favUserId":XIANGQU.userId,
                    "productId": self.J_detailInfos.attr('data-id'),
                    "otherRefer":document.referrer
                },
                dataType: 'json',
                success:function(rs){
                    if(rs.code != 2000){
                        return false;
                    }
                    self.J_detailInfos.find('.J_likeNum').html(rs.favNum);
                    self.J_detailInfos.find('.J_detailLike').removeClass('has-faver-true');
                }
            })
        },
        isLogin:function(){
            //获取购物车数
            var self = this;
            if(XIANGQU.userId != ''){
                self.getCartNum();
            }

        },
        addCart:function(){
            var self = this;
            //加入购物车
            var _url = '/product/addToCart';
            var skuId = self.J_skuBox.find('#skuId').val();
            var amount = self.J_skuBox.find('.J_detailNum').val();
            var spm = self.J_buyCart.find('.J_btnAddCart').attr('data-spm');

            $.ajax({
                type:'post',
                url:_url,
                dataType:'json',
                data:{
                    skuId: skuId,
                    amount:amount,
                    spm:spm,
                },
                success:function(rs){
					if(rs.code != 200){
						return false;
					}

					if(rs.data){
						self.J_wapSiteShop.find('.J_carNum').html(rs.data).show();
						$('html,body').removeAttr('style');
						self.J_skuBox.addClass('fadeInDownBig');
						self.J_skuBox.find('.proskuitem').removeClass('sku-selected');
						self.J_skuBox.find('#skuId').val('');
						self.J_skuBox.find('.J_detailNum').val('1');
						self.J_skuBox.find('.J_skuPrice dd').html('');
						self.J_skuBox.removeClass('fadeInUpBig ');
						self.J_skuBox.removeAttr('style');
					}
                }
            })

        },

        setTabFixed:function(){
            /*设置商品菜单fixed*/
            var self = this;
            var FIXED = 'tab-nav-fixed';
            var sT = $(window).scrollTop();
            var objOffset = $('.detail-tab-box').offset();
            var headH = $('.wap-site-shop').height() ;
            if(sT > objOffset.top - headH){
                self.J_detailInfo.find('.detail-tab-nav').addClass(FIXED).animate({
                    translate3d:'0,4rem,0'
                },50,'ease-out');
                self.J_detailInfo.find('.detail-tab-nav-tips').show();
            }else{
                self.J_detailInfo.find('.detail-tab-nav').removeClass(FIXED).animate({
                    translate3d:'0,0,0'
                },50,'ease-in');;
                 self.J_detailInfo.find('.detail-tab-nav-tips').hide();
            }
        },
        getAlikeDate:function(){
            var self = this;
            var _data = {
                id:self.J_detailInfos.attr('data-id')
            };
            var _url = ' /m/product/revelentList';

            $.ajax({
                type:'post',
                url:_url,
                data:_data,
                dataType:'json',
                success:function(data){
                    if(data.code == 200){
                        var rs = data.data;
                        var html = '';
                        for(var i = 0; i < rs.length; i++){
                            html += '<li><a href="/product/detail/'+ rs[i].id +'.html"><img src="' + rs[i].imageUrl +'"></a> </li>';
                        }
                        self.J_detailInfos.find('.alike-items').html(html);
                    }
                },
                complete:function(e){
                    self.J_detailInfos.find('.detail-alike .J_loading').hide();
                }
            });

        },
        getUserComment:function(){
            var self = this;
            var _url = ' /m/action/comment/list';
            self._page++;

            if(self.isEnd){
                return false;
            }

            $.ajax({
                type:'post',
                url:_url,
                data:{
                    objectId:self.J_detailInfos.attr('data-id'),
                    type:0,
                    page: self._page,
                    size:5
                },
                dataType:'json',
                beforeSend:function(){
                    self.isLoading = true;
                    self.J_detailInfo.find('.J_userComment .J_loading').show();
                },
                complete:function(){
                    self.isLoading = false;
                    self.J_detailInfo.find('.J_userComment .J_loading').hide();
                },
                success:function(data){
                    if(data.code != 200){
                        return false;
                    }

                    var rs = data.data;
                    if(!rs.length){
                        self.isEnd = true;
                        return false;
                    }
                    if(rs.length <5){
                        self.isEnd = true;
                    }
                    self.J_detailInfo.find('.J_userComment .J_loading').hide();

                    var html = [];

                    for(var i = 0; i < rs.length; i++){
                        var item = ['<li>',
                                    '<div class="user-icon">',
                                        '<img src="' + rs[i].avaPath +'">',
                                    '</div>',
                                    '<dl class="user-comment">',
                                        '<dt>',
                                            '<h3>' + rs[i].nick + '</h3>',
                                            '<span>' + rs[i].time + '</span>',
                                        '</dt>',
                                        '<dd>' + rs[i].content + '</dd>',
                                    '</dl>',
                                '</li>'];
                        html.push(item);
                    }
                    html = html.join(' ').replace(/,/g,'');

                    self.J_detailInfo.find('.J_userComment ul').append($(html));

                }
            });


        },
        getCartNum:function(){
            var self = this;
            var _url = '/m/cart/count';

            $.ajax({
                type:'post',
                url:_url,
                dataType:'json',
                success:function(rs){
                    if(rs.code != 200){
                        return false;
                    }

                    if(rs.data.count){
                        self.J_wapSiteShop.find('.J_shopCart').attr('href',rs.data.kdH5CartUrl);
                        self.J_wapSiteShop.find('.J_carNum').html(rs.data.count).show();
                    }

                }
            })

        },
        getDetailPics:function(){
            var self = this;
            var _url = '/m/product/chips'
            $.ajax({
                type:'post',
                url:_url,
                data:{
                    id: self.J_detailInfos.attr('data-id')
                },
                dataType:'json',
                beforeSend:function(){
                    self.isDetailLoading = false;
                    self.J_detailInfo.find('.J_detailDescription .J_loading').show();
                },
                complete:function(){
                    self.isDetailLoading = false;
                    self.J_detailInfo.find('.J_detailDescription .J_loading').hide();
                },
                success:function(rs){

                    if(rs.code != 200){
                        return false;
                    }

                    var data = rs.data;
                    var html = [];

					if(!data){
						var _str = '店家很忙，敬请期待！';
						self.J_detailInfo.find('.J_detailDescription .J_detailPicBox').append(_str);
						self.J_detailInfo.find('.J_detailDescription .J_loading').hide();
						return false;
					}
                    for(var i = 0; i< data.length; i++){
                        var item = '' ;
                        if(data[i].isImg){
                            item = ['<img src="' + data[i].content + '">'];
                        }else{
                            item = ['<div class="detail-text">' + data[i].content + '</div>' ];
                        }

                        html.push(item);

                    }

                    html = html.join(' ').replace(/,/g,'');

                    self.J_detailInfo.find('.J_detailDescription .J_detailPicBox').append($(html));
                    self.J_detailInfo.find('.J_detailDescription .J_loading').hide();


                }
            })
        },
        isObjEmpty:function(obj){
            /*
             * 判断{}是否是空
             * obj: {}
             * 为空 返回 false
             * 不为空 返回 true
             * */
                var self  = this;
            if(typeof obj === 'object' && !(obj instanceof Array)){
                var hasProp = false;
                for(var porp in obj){
                    hasProp = true;
                    break;
                }
                if(hasProp){
                    obj = [obj];
                    return true;
                }else{
                    return false;
                }
            }
        },
        skuInit:function(){
            var self = this;
            self.J_prosku = self.J_skuBox.find('.J_prosku');

            self.J_skuBox.find('.J_skuPrice dd').html("");
            self.J_skuBox.find('#skuId').val('');
            var objHtml = '';
            var newSku ='',
                mapping='',
                amountHtml='';
            //var skuApp = 'http://' + location.host + '/testData/sku.json';
            var skuApp = '/ajax/product/sku';
            var _fromType = XIANGQU.fromType,
                _soldOut = XIANGQU.soldOut;
            //console.log(_fromType, _soldOut);
            /*
             * _fromType == 1; kkkd
             * _fromType == 0; taobao
             * _soldOut == 1; 下架
             * _soldOut == 'ONSALE'; 上架
             * */
            if(_fromType == 1){
                if(_soldOut == 'ONSALE'){
                    getSkuData(skuApp);
                }
            }

            //sku init start

            function getSkuData(url){
                $.ajax({
                    url:url,
                    type:'post',
                    data:{
                        productId: self.J_detailInfos.attr('data-id')
                    },
                    dataType:'json',
                    timeout:5000,
                    success:function(data){

                        if(data.code == 200 && (_fromType == 1)){
                            window.skuObj = self.skuObj = data.data;
                            var _sku = self.skuObj.skus,
                                _skuMappings = self.skuObj.skuMappings;


                            //数量
                            var kcNum = 0;
                            if(_skuMappings.length == 0){
                                if(_sku.length != 0){
                                    kcNum = _sku[0].amount;
									self.J_skuBox.find(".J_btnSubmitSku").attr("data-href", _sku[0].skuUrl);
                                }else{
                                    kcNum = 0;
                                }
                            }else{
                                for(var i = 0; i < _sku.length; i++){
                                    kcNum += parseInt(_sku[i].amount);
                                }
                            }
                            if(kcNum == 0){
                                /*var _soldOutHtml = '<div class="name"><p style="padding:18px 0 20px 20px;margin:0;font-size:20px;"> 该商品已下架</p></div>';
                                self.J_skuBox.append(_soldOutHtml);*/
                                //alert('该商品已下架！');
								self.J_buyCart.find('a').addClass('disable-selected-btn');
                                return false;
                            }


                            if(data.data && data.data.skuMappings && _skuMappings.length == 0){
                                self.J_skuBox.find('.J_skuPrice dt').html('￥' + _sku[0].price);
                            }


                            for(var i = 0; i < _sku.length;i++){
                                newSku += '<sku class="js-sku" data-amount="'+ _sku[i].amount +'" data-price="'+ getNumFloor(_sku[i].price,2) + '" data-sku-id="'+ _sku[i].id + '" data-spec1="' + _sku[i].spec1 + '" data-spec2="' + _sku[i].spec2 + '" data-url="' + _sku[i].skuUrl + '"></sku>'
                            }

                            for(var j = 0; j < _skuMappings.length; j++){
                                mapping  += '<dl class="proskuinfor"><dt class="proskutitle">'+ _skuMappings[j].specName +'</dt><dd><ul class="proskulist clearfix" data-mapping-key="'+ _skuMappings[j].specKey +'"> ' + getSkuMappingValue(_skuMappings[j].mappingValues) + '</ul></dd></dl>';
                            }



                            /*var _amountHtml = '<dl class="numb clearfix"><dt class="numb-name">数量</dt><dd><div class="numEditor fl-l"><button type="button" edge="1" class="numDec numMinus fl-l" style="padding:0;">-</button><input class="numed" id="buy_amount" value="1">' +
                                '<button type="button" edge="' + kcNum +
                                '" class="numInc numPlus" style="padding:0;">+</button>' +
                                '</div><span class="kucun fl-l">库存 </span>' +
                                '<span class="kucun fl-l" id="amount">' + kcNum +
                                '</span>' +
                                '<span class="kucun fl-l"> 件</span></dd></dl>';


                            //购买按钮

                            amountHtml = _amountHtml;*/
                            objHtml = newSku + mapping ;

							self.J_prosku.html('');
							self.J_skuBox.find('#skuId').val('');
							self.J_skuBox.find('.J_detailNum').val('1');
                            self.J_prosku.html(objHtml);

                            self.J_skuBox.find('.J_detailNum').attr('data-allKC',kcNum);
                            self.J_skuBox.find('.plus').attr('edge',kcNum);
                            //self.J_skuBox.show('fast');
                            //self.J_skuBox.addClass('fadeInUpBig');
                            //self.J_skuBox.removeClass('fadeInDownBig');

                            //self.J_skuBox.find('.J_proskuInfo').show();

                            //纬度选择
                            self.skuSelected();
                        }else{
//                            console.log('网络正忙，请稍后再试！');
                        }
                    }
                });
            }

            function getSkuMappingValue(obj){
                var _html = '';
                if(obj){
                    for(var i = 0; i < obj.length; i++){
                        _html += '<li class="proskuitem"><span>'+ obj[i] +'</span></li>';
                    }

                }
                return _html;
            }

            function getNumFloor(num,len){
                //返回小数
                var baseNum = 10;
                if(len == 1){
                    baseNum = 10;
                }else if(len == 2){
                    baseNum = 100;
                }else if(len == 3){
                    baseNum = 1000;
                }
                return (Math.round(num*baseNum)/baseNum).toFixed(len);
            }
            //sku init end

        },
        skuSelected:function(){
            //纬度选择
            var self = this;

            // //数量输入框限定只能输入数字
            var numed = self.J_skuBox.find('.J_detailNum');

            numed.on('blur', function () {
                var regu = /^([0-9]+)$/;
                var re = new RegExp(regu);


                if (!re.test($(this).val())) {
                    alert('请输入数字');
                    numed.val('1').trigger('change');
                }else if($(this).val()<1){
                    numed.val('1').trigger('change');
                }
            });
            numed.change(function() {
                if(1<parseInt(self.J_skuBox.find('.J_detailNum').val())&&parseInt(self.J_skuBox.find('.J_detailNum').val())>parseInt(self.J_skuBox.find('.J_detailNum').attr("data-allkc"))){
                    self.J_skuBox.find('.J_detailNum').val(self.J_skuBox.find('.J_detailNum').attr('data-allkc'));
                    alert("亲手下留情哦，您选的数量已经大于库存拉~");
                    //$("#buy_amount").val($("#amount").text());
                    return false;
                }else if(parseInt(self.J_skuBox.find('.J_detailNum').val())<1){
                    alert("请选择1件以上商品~");
                    self.J_skuBox.find('.J_detailNum').val(1);
                    return false;
                }
            });
            //减数量
            self.J_skuBox.on('click','.J_mimusNum',function () {
                var _that = $(this),
                    numedInput = _that.parent().children('.J_detailNum'),
                    edge = _that.attr('edge'),
                    oldVal = parseInt(numedInput.val()),
                    oldVal = isNaN(oldVal) ? 0 : oldVal,
                    edge = _that.attr('edge'),
                    newVal = oldVal - 1;
                if(newVal<1){
                    alert('亲，所选数量不能小于1件哦~');
                    self.J_skuBox.find('.J_detailNum').val(1);
                    return false;
                }else{
                    numedInput.val(oldVal - 1).trigger('change');
                }
                return false;
            });
            //加数量
            self.J_skuBox.on('click','.J_plusNum',function () {
                var _that = $(this),
                    numedInput = _that.parent().find('.J_detailNum'),
                    edge = _that.attr('edge'),
                    oldVal = parseInt(numedInput.val()),
                    oldVal = isNaN(oldVal) ? 0 : oldVal,
                    newVal = oldVal + 1;

                if(newVal > edge){
                    alert('亲手下留情哦，您选的数量已经大于库存啦！');
                    self.J_skuBox.find('.J_detailNum').val(self.J_skuBox.find('.J_detailNum').attr('data-allkc'));
                    return false;
                }else{
                    numedInput.val(oldVal + 1).trigger('change');
                }
                return false;
            });

            var _dataPro = XIANGQU.fromType;
            if(_dataPro !== "0") {
                //选择sku
                if (self.J_prosku.find('.proskulist').length == 0) {
                    if(!self.isObjEmpty(skuObj)){
                        return false;
                    }else{
                        self.J_skuBox.find("#skuId").val(skuObj.skus[0].id);
                    }

                }
                var selectItem = [];
                /*if ($(".sku-item").length == 0) {

                    $("#btn_buy").attr("data-href", skuObj.skus[0].skuUrl);
                }*/
                function selsectsku() {
                    //获取已选择的sku单项
                    selectItem = [];
                    var J_proskulist = self.J_skuBox.find('.proskulist');
                    J_proskulist.find('.sku-selected').each(function () {
                        var item = {
                            key: $(this).parent().attr('data-mapping-key'), //如spec1
                            value: $(this).text() //如红色
                        };
                        selectItem.push(item);
                    });
                    return selectItem;
                }

                self.J_skuBox.on('click','.proskuitem', function (e) {
                    var _that = $(this);
                    //同一维度sku之能选择一个

                    if (_that.hasClass('sku_disabled')) {
                        return false;
                    }
                    if (_that.hasClass('sku-selected')) {
                        _that.removeClass('sku-selected');
                        self.J_skuBox.find("#skuId").val('');
                    }
                    else {
                        _that.closest('.proskulist').find('.sku-selected').removeClass('sku-selected');
                        _that.addClass('sku-selected');
                        if (self.J_skuBox.find('.sku-selected').length == self.J_skuBox.find('.proskulist').length) {
                            skuid(_that);//sku都选时获取skuid
                        } else {
                            self.J_skuBox.find("#skuId").val('');
                        }
                    }
                    shows(_that);
                });
                //判断书否可选
                /*if($('.proskuitem').hasClass("no-seleted")){
                 $('.proskuitem').unbind('click');
                 }*/
                /*$('.price').attr("data-price", $('.price').text());
                $('#amount').attr("data-amount", $('#amount').text());*/
                var skulist = [];

                function skuid(sf) {
                    skulist = [];
                    var selectitem = selsectsku();
                    var parent = $(sf).closest('.proskulist');
                    var specKey = parent.attr("data-mapping-key");//获取当前key即维度
                    self.J_skuBox.find("sku").each(function (k, item) {//几条数据代表几个纬度，与所需更新数据的坑一致，通过key，obj对应
                        if ($(item).attr("data-" + specKey) == $(sf).text()) {//筛选当前所选sku对应的项
                            skulist.push(item);
                        }
                    });
                    //console.log(skulist)
                    //sku多维度都选定，重置库存、价格、赋值skuid
                    $(skulist).each(function (k, item) {
                        if (self.J_skuBox.find('.proskulist').length == 1 && $(item).attr("data-" + $(selectitem).get(0).key) == $(selectitem).get(0).value) {
                            self.J_skuBox.find("#skuId").val($(item).attr("data-sku-id"));
                            self.J_skuBox.find(".J_detailNum").attr('data-allkc',$(item).attr("data-amount"));

                            self.J_skuBox.find('.J_plusNum').attr('edge', $(item).attr("data-amount"));
                            self.J_skuBox.find('.J_skuPrice dt').html('￥' + $(item).attr("data-price"));
                            self.J_skuBox.find('.J_skuPrice dd').html('已选：' + $(item).attr('data-spec1'));
                            self.J_skuBox.find(".J_btnSubmitSku").attr("data-href", $(item).attr("data-url"));
                        } else if (self.J_skuBox.find('.proskulist').length == 2 && $(item).attr("data-" + $(selectitem).get(0).key) == $(selectitem).get(0).value && $(item).attr("data-" + $(selectitem).get(1).key) == $(selectitem).get(1).value) {
                            self.J_skuBox.find("#skuId").val($(item).attr("data-sku-id"));
                            self.J_skuBox.find(".J_detailNum").attr('data-allkc',$(item).attr("data-amount"));
                            self.J_skuBox.find('.J_plusNum').attr('edge', $(item).attr("data-amount"));
                            self.J_skuBox.find('.J_skuPrice dt').html('￥' + $(item).attr("data-price"));
                            self.J_skuBox.find('.J_skuPrice dd').html('已选：尺寸：' + $(item).attr('data-spec1') + '；颜色分类：' + $(item).attr('data-spec2'));
                            self.J_skuBox.find(".J_btnSubmitSku").attr("data-href", $(item).attr("data-url"));
                        }
                    });
                }

                //加入购物或直接购买，每个维度必须全选择
                /*function shows(self){
                 var selectitem =selsectsku();
                 //理论上所有的sku
                 var specKey = $(self).parent().attr('data-mapping-key'); //spec1
                 var specValue = $(self).text();    //10kg
                 //未选中的
                 var bInclude = false;
                 $('.proskulist').find('.proskuitem').each(function(i, allItem){
                 //已选中的不做处理
                 $('.proskulist').find(':not(.sku-selected)').addClass('sku_disabled');

                 if($(allItem).hasClass('sku-selected')){
                 return true;
                 }
                 //本商品的sku
                 var keyitem;
                 function key(){
                 $.each(skuObj.skuMappings,function(j, existItem){
                 if(existItem["specKey"] == specKey){ //找到有10kg的sku
                 //已选择spec1 10kg

                 keyitem =  existItem["specKey"];
                 }
                 });
                 return keyitem;
                 }
                 $.each(skuObj.skus,function(i,val){
                 var keyv=key();
                 $.each(selectitem, function(m, item){
                 if($(val)[0]["spec1"] == item.value && $(".proskuitem").text()==$(val)[0]["spec2"]){//匹配的sku信息和所储存的数据匹配
                 bInclude = true;
                 }else if($(val)[0]["spec2"] == item.value && $(".proskuitem").text()==$(val)[0]["spec1"]){
                 bInclude = true;
                 }
                 });
                 });

                 //sku 未选
                 if(selectitem.length == 0){
                 $('.price').text($('.price').attr("data-price"));
                 $('#amount').find("span").text($('#amount').attr("data-amount"));
                 bInclude = true;
                 }
                 //选中一个
                 //console.log(selectitem[0].key);
                 if(selectitem.length == 1){
                 if(selectitem[0].key == 'spec1' && specKey == 'spec1'){
                 bInclude = true;
                 } else if(selectitem[0].key == 'spec2' && specKey == 'spec2'){
                 bInclude = true;
                 }
                 }

                 if(bInclude){
                 $(allItem).removeClass('sku_disabled');

                 }
                 });
                 }*/


                function shows(ele) {

                    var selectitem = selsectsku();
                    var J_proskulist = $(ele).closest('.proskulist');
                    J_proskulist.find('.proskuitem:not(.sku-selected)').addClass('sku_disabled');

                    //理论上所有的sku
                    J_proskulist.find('.proskuitem').each(function (i, allItem) {
                        //已选中的不做处理
                        if ($(allItem).hasClass('sku-selected')) {
                            return true;
                        }
                        //未选中的
                        var specKey = $(allItem).closest('.proskulist').attr('data-mapping-key'); //spec1
                        var specValue = $(allItem).text();    //10kg
                        var bInclude = false;
                        //本商品的sku
                        self.J_skuBox.find('.js-sku').each(function (j, existItem) {
                            if ($(existItem).attr('data-' + specKey) == specValue) { //找到有10kg的sku
                                //已选择spec1 10kg
                                $.each(selectitem, function (m, item) {
                                    if ($(existItem).attr('data-' + item.key) == item.value) {
                                        bInclude = true;
                                    }

                                });
                            }
                        });
                        //sku 未选
                        if (selectitem.length == 0) {
                            self.J_skuBox.find(".J_skuPrice dt").html(self.J_detailInfos.find('.detail-price-box dt').html());
                            //self.J_skuBox.find("#amount").text(self.J_skuBox.find("#amount").attr("data-amount"));
                            bInclude = true;
                        }
                        //选中一个
                        if (selectitem.length == 1) {

                            if (selectitem[0].key == 'spec1' && specKey == 'spec1') {
                                bInclude = true;
                            }else if (selectitem[0].key == 'spec2' && specKey == 'spec2') {
                                bInclude = true;
                            }
                        }

                        if (bInclude){
                            $(allItem).removeClass('sku_disabled');
                        }

                    });
                }
            }


            //立刻购买 加入购物车
            self.J_skuBox.on('click','.J_btnSubmitSku',function (e) {
                e.preventDefault();

                    if(self.J_skuBox.find("#skuId").val()==""){
                        alert("请选择规格");
                        return false;
                    }
                    var _tuid = self.J_buyCart.find('.J_btnBuy').attr('data-tuid');
                    if(_tuid == undefined){
                        _tuid = '';
                    }

					//console.log(self.isAddCart);

					if(!self.isAddCart){
						//立刻购买
						var url=unescape(self.J_skuBox.find('.J_btnSubmitSku').attr("data-href")+"&amount="+self.J_skuBox.find('.J_detailNum').val() + '&spm=' + $(this).attr('data-spm')) + '&tuid=' + _tuid;
						self.J_skuBox.find("#skuId").val('');
						window.location.href=url;
					}else{
					//	加入购物车
						self.addCart();

					}
            });

            //加入购物车
            /*function popupWin(msg) {
                if (!msg){
                    msg = "成功!";
                }
                var popObj = $("#msg-popup");
                popObj.find('div').html(msg);
                var scrollTop = $(window).scrollTop();
                var popupWidth = popObj.width();
                var windowWidth = document.body.clientWidth;

                popObj.css({"position": "fixed", "left": windowWidth / 2 - popupWidth / 2, "top": 200});
                popObj.show();
                window.setTimeout(function () {
                    popObj.hide()
                }, 2000);
            }*/

            /*self.J_skuBox.on('click','#btn_cart',function (e) {
                if (XIANGQU.userId == "") {
                    e.preventDefault();
                    var url = window.location.href;
                    XQTOOL.loginBox(url);
                    return false;
                } else {
                    if(1<parseInt($("#buy_amount").val())&&parseInt($("#buy_amount").val())>parseInt($("#amount").text())){
                        $("#buy_amount").val($("#amount").text());
                        alert("亲手下留情哦，您选的数量已经大于库存拉~");
                        $("#buy_amount").val($("#amount").text());
                        return false;
                    }else if(parseInt($("#buy_amount").val())<1){
                        alert("请选择1件以上商品~");
                        $("#buy_amount").val(1);
                        return false;
                    }
                    if($("#skuId").val()==""){
                        alert("请选择规格");
                        return false;
                    }
                    var index = $(".sku-selected").index();
                    $.ajax({
                        url: '/product/addToCart',
                        type: 'POST',
                        data: {"skuId": $("#skuId").val(),
                            "amount": $('#buy_amount').val(),
                            "spm": $('#btn_cart').attr('data-spm')
                        },
                        dataType: 'json',
                        timeout: 50000,
                        error: function (XMLHttpRequest, textStatus, errorThrown) {
                        },
                        success: function (data) {
                            if (data && data.code) {
                                if (data.code == 200) {
                                    popupWin("已加入购物车!");
                                    $('#u_cart').html(data.data);
                                } else if (data.code == 700) {
                                    popupWin(data.msg);
                                } else if (data.code == 4000) {
                                    var url = window.location.href;
                                    XQTOOL.loginBox(url);
                                }
                            } else {
                                popupWin("处理是失败!");
                            }
                        }
                    });
                }
            });*/

        },


    };


   $(function(){

       new FastClick(document.body);

       DetailModule.init();

       $('#J_detailPics').slider({
           className:'ks_wt_2',
           tick:3000
       });



   });

})(Zepto);