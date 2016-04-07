define(['jquery'], function(jquery){
    var descrip={
        init: function () {
            var that = this;
            that.delete();
            that.newAdd();
            that.editor();

        },
        delete: function () {
            $(".edit-choose").delegate('.detele', 'click', function() {
                var _this = this;
                alertMsg.confirm("确认删除此段落描述？", {
                    okCall: function() {
                        if ($(".descrip").length == 1) {
                            $(_this).parent().parent().addClass("fn-hide");
                        } else {
                            $(_this).parent().parent().remove();
                        }
                    }
                });
            });
        },

        newAdd : function () {
            var that=this;
            //新建段落
            $(".newClassify").delegate("#new-describe", 'click', function() {
                that.fillDesc();
                //$('#describeAdd').after('<div id="dialogMask"></div>');
                $(".am-modal").css("opacity",1);
                    $('#describeAdd').modal({
                        relatedTarget: this,
                        width: '688',
                        onConfirm: function(options) {
                            this.close();
                        },
                        onCancel: function() {
                            this.close();
                        }
                    });
            });
        },
        editor: function() {
            $(".edit-type-list").delegate('.input-edit', 'click', function() {
                $(".pageEditor").hide();
                $(".sortDrag").hide();
                if ($("#editWeb").attr("checked")) {
                    $(".sortDrag").hide();
                    $(".pageEditor").show();
                } else if ($("#editApp").attr("checked")) {
                    $(".pageEditor").hide();
                    $(".sortDrag").show();
                }
            })
        },
        //填充段落描述弹出层
        fillDesc: function(data) {
            $('#describeAdd .imgBox').empty();
            if (data) {
                //编辑状态
                $('#describeName').val(data.name);
                $('#describeText').val(data.description);
                if (data.imgs[0]) {
                    for (var i = 0; i < data.imgs.length; i++) {
                        $('#describeAdd .imgBox').append('<span><img src="' + data.imgs[i] + '" key="' + data.keys[i] + '" /><i>&#xe602;</i></span>');
                    }
                }
                $('.pageFormContent [name="paraPlace"]').filter('[value=' + data.showModel + ']').attr('checked', true);
                $("#describeAdd").attr('state', 'modify').attr('_from', data.index);
            } else {
                //新增状态
                $('#describeName').val('');
                $('#describeText').val('');
                $('.pageFormContent [name="paraPlace"]:eq(0)').attr('checked', "checked");
                $("#describeAdd").attr('state', '').attr('_from', '');
                $('#describeAdd .imgBox').empty();
            }
        },
        modifyDesc: function() {
            var that = this;
            $('body').off('click', '.pro-desibe .edit')
            $('body').on('click', '.pro-desibe .edit', function() {
                //$('#skuEdit').after('<div id="dialogMask"></div>');
                var $this = $(this).closest('.pro-desibe');
                var data = {
                    name: $this.find('.pro-name span').text(),
                    description: $this.find('.pro-text').text(),
                    imgs: $this.find('.pro-img img').attr('srcs').split(','),
                    keys: $this.find('.pro-img img').attr('keys').split(','),
                    showModel: $this.attr('data-showModel'),
                    index: $this.attr('data-id')
                }
                that.fillDesc(data);
                $("#describeAdd").show();
            });

        },
        closeDescPop: function() {
            var that = this;
            $('body').on('off', '.formBar-detele');
            $('body').on('click', '.formBar-detele', function() {
                that.fillDesc();
                $("#describeAdd").hide();
                $('#dialogMask').fadeOut().remove();
            });
        },
        submitDesc: function() {
            var that = this;
            var addTpl = $('.tpl-desc').html();
            var doTtmpl = doT.template(addTpl);
            $('body').off('click', '#describeAdd button[type="submit"]');
            $('body').on('click', '#describeAdd button[type="submit"]', function() {
                var _this = this;
                var pop = $("#describeAdd");
                for (var i = 0; i < pop.find('.required').length; i++) {
                    if (pop.find('.required').eq(i).val() == '') {
                        alertMsg.error('标有*号的项不能为空~');
                        return;
                    }
                }
                var imgs = [];
                var keys = [];
                var descId = '';
                pop.find('.imgBox img').each(function(i, el) {
                    imgs.push($(el).attr('src'));
                    keys.push($(el).attr('key'));
                });
                if (!imgs.length) {
                    alertMsg.error('段落描述图片为必选项~');
                    return;
                }
                var data = {
                    id: '',
                    name: pop.find('#describeName').val(),
                    description: pop.find('#describeText').val(),
                    imgs: imgs.join(','),
                    keys: keys.join(','),
                    src: imgs[0],
                    showModel: pop.find('[name="paraPlace"]:checked').val()
                }
                var ajaxData = {
                    id: data.id,
                    name: data.name,
                    description: data.description,
                    showModel: data.showModel,
                    imgs: data.keys
                }
                //保存或新建在这里处理
                if (pop.attr('state') == 'modify') {
                    //如果是修改
                    ajaxData.id = data.id = pop.attr('_from');
                    var str = doTtmpl(data);
                    $('.pro-desibe[data-id="' + data.id + '"]').replaceWith(str);
                } else {
                    //如果是新建
                    delete ajaxData.id;
                    delete data.id;
                    $('.descList').append(doTtmpl(data));
                }
                //发送ajax请求
                KD.API.saveDesc(ajaxData, function(res) {
                    if (res.moreInfo) {
                        alertMsg.error(res.moreInfo);
                        return;
                    }
                    if (!ajaxData.id) {
                        $('.pro-desibe:last').attr('data-id', res.data.id);
                    }
                    $('#dialogMask').fadeOut().remove();
                    //如果是编辑商品级别的还要请求一次,具体代码编辑商品时修改
                    if (that.productId) {
                        var productId = that.productId;
                        KD.API.saveproDesc({
                            productId: productId,
                            fragmentId: res.data.id
                        }, function() {

                        }, function() {
                            console.log('error');
                        });
                    }

                });
                //防止出现绑定多次事件
                that.initUi();
                $("#describeAdd").hide();
            });
        },
        initUi: function() {
            $('.sortDrag').find('.moveico').off('mousedown');
            $('.sortDrag').sortDrag();
        },

        delImg: function() {
            var that = this;
            $('body').off('click', '#describeAdd .imgBox i');
            $('body').on('click', '#describeAdd .imgBox i', function() {
                var _this = this;
                alertMsg.confirm("确认删除此段落描述图片？", {
                    okCall: function() {
                        $(_this).parent('span').remove();
                    }
                });
            });
        },
        delProImg: function() {
            var that = this;
            $('body').off('click', '.productImgs .imgBox i');
            $('body').on('click', '.productImgs .imgBox i', function() {
                var _this = this;
                alertMsg.confirm("确认删除此商品图片？", {
                    okCall: function() {
                        $(_this).parent('span').remove();
                    }
                });
            });
        },
      /*  addImg: function(src, key) {
            var that = this;
            $('#describeAdd .imgBox').append('<span><img src="' + src + '" key="' + key + '" /><i>&#xe602;</i></span>');
        },
        addProductImg: function(src, key) {
            var that = this;
            $('.productImgs .imgBox').append('<span><img src="' + src + '" key="' + key + '" /><i>&#xe602;</i></span>');
        },*/
        fillForm: function() {
            var that = this;
            var addTpl = $('.tpl-desc').html();
            var doTtmpl = doT.template(addTpl);
            var link = $('.navTab-tab li[tabId="addProduct"]').attr('url');
            if (link.indexOf('?') < 0) {
                that.productId = '';
                return;
            }

            var search = link.split('?')[1];
            that.productId = KD.main.query('id', search);
            KD.API.getProduct(that.productId, function(res) {
                console.log(res)
                that.skusEditBuild(res);
                $('#proName').val(res.data.name);
                $('#description').val(res.data.description);
                $('#proPrice').val(res.data.marketPrice);
                $('#proNumb').val(res.data.amount);
                $('#productAdd .danbanbtn').removeClass('notneed need');
                $('.pageFormContent [name=status]').filter('[value="' + res.data.status + '"]').attr('checked', 'checked');
                if (res.data.recommend) {
                    $('#productAdd .danbanbtn').addClass('need');
                } else {
                    $('#productAdd .danbanbtn').addClass('notneed');
                }

                //计划发布时间填充显示
                var timePlan = timeFormat(res.data.forsaleAt, 'yyyy-MM-dd HH:mm:ss');
                if ($('.radio-status[name="status"]:checked').val() == 'FORSALE') {
                    $('#data-choose').show();
                    $('#data-choose input[name="forsaleDate"]').val(timePlan);
                };

                //商品图片
                $.each(res.data.imgs, function(i, el) {
                    that.addProductImg(el.imgUrl, el.img);
                });

                //整合sku数据
                that.skuInfo = {
                    mappings: [],
                    skus: []
                }


                var skuList = [];

                $.each(res.data.skuMappings, function(i, el) {
                    that.skuInfo.mappings.push(el.specName);
                    skuList.unshift(el.mappingValues);
                });

                $.each(that.combine(skuList), function(i, el) {
                    that.skuInfo.skus.push({
                        "sku": el,
                        "price": 0,
                        "amount": 0
                    });
                });

                //修改对应sku的价格和数量
                for (var i = 0; i < res.data.skus.length; i++) {
                    //对比数据
                    for (var j = 0; j < that.skuInfo.skus.length; j++) {
                        var isThis = true;
                        $.each(that.skuInfo.skus[j].sku, function(index, el) {
                            if (el != res.data.skus[i]['spec' + (index + 1)]) {
                                isThis = false;
                            }
                        });
                        if (isThis) {
                            that.skuInfo.skus[j].price = res.data.skus[i].price;
                            that.skuInfo.skus[j].amount = res.data.skus[i].amount;
                        }
                    }
                }


                that.initSkuUI();
                //显示sku部分结束

                //如果当前商品存在型号，则隐藏价格可库存的单独输入框
                var lenType = $('.sku-item.trueItem').length;
                if (res.data.name && lenType) {
                    $('#proPrice').parent('.fl-l').parent('li').hide();
                } else {
                    $('.sku-item').hide();
                }

                //notneed no
                //need yes
                //显示片段
                if (res.data.fragments && res.data.fragments.length) {
                    $('.descList').empty();
                    $.each(res.data.fragments, function(i, el) {
                        var imgs = [],
                            keys = [],
                            src;
                        $.each(el.imgs, function(index, img) {
                            imgs.push(img.imgUrl);
                            keys.push(img.img);
                        });
                        if (el.imgs[0]) {
                            src = el.imgs[0].imgUrl;
                        }
                        var data = {
                            id: el.id,
                            name: el.name,
                            description: el.description,
                            imgs: imgs.toString(),
                            keys: keys.toString(),
                            src: imgs[0],
                            showModel: el.showModel
                        }
                        $('.descList').append(doTtmpl(data));
                    });
                    that.initUi();
                }

            });
        }
    }
    descrip.init();
    return null;
});