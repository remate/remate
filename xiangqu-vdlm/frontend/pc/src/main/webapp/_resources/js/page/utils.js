define(['jquery', 'amaze', 'underscore'], function(jquery, amaze, underscore) {
    var utils = {
        api: {
            logout: function(success, fail) {
                var that = this;
                $.ajax({
                    url: host + '/logout',
                    type: 'POST',
                    dataType: 'json',
                    success: function(data) {
                        if (data.errorCode == 200) {
                            $(window).off('beforeunload.pro');
                            utils.tool.goLogin(1);
                        } else {
                            fail && fail(data.moreInfo);
                        }
                    },
                    error: function(state) {
                        fail && fail('服务器暂时没有相应，请稍后重试...');
                    }
                });
            },
            move: function(success, fail) {
                var that = this;
                $.ajax({
                    url: host + '/shop/movestart',
                    type: 'POST',
                    dataType: 'json',
                    success: function(data) {
                        if (data.errorCode == 200) {
                            success && success(data);
                        } else {
                            fail && fail(data.moreInfo);
                        }
                    },
                    error: function(state) {
                        if (state.status == 401) {
                            utils.tool.goLogin();
                        } else {
                            fail && fail('服务器暂时没有相应，请稍后重试...');
                        }
                    }
                });
            },
            moveAgain: function(data, success, fail) {
                var that = this;
                $.ajax({
                    url: host + '/shop/step5',
                    data: {
                        shopUrl: data.shopUrl,
                        option: data.option
                    },
                    type: 'POST',
                    dataType: 'json',
                    success: function(data) {
                        if (data.errorCode == 200) {
                            success && success(data);
                        } else {
                            fail && fail(data.moreInfo);
                        }
                    },
                    error: function(state) {
                        if (state.status == 401) {
                            utils.tool.goLogin();
                        } else {
                            fail && fail('服务器暂时没有相应，请稍后重试...');
                        }
                    }
                });
            },
            checkMoveCode: function(rnd, itemId, success, fail) {
                var that = this;
                $.ajax({
                    url: host + '/shop/step2check',
                    type: 'POST',
                    dataType: 'json',
                    data: {
                        rnd: rnd,
                        itemId: itemId
                    },
                    success: function(data) {
                        if (data.errorCode == 200) {
                            success && success(data);
                        } else {
                            fail && fail(data.moreInfo);
                        }
                    },
                    error: function(state) {
                        if (state.status == 401) {
                            utils.tool.goLogin();
                        } else {
                            fail && fail('服务器暂时没有相应，请稍后重试...');
                        }
                    }
                });
            },
            //根据zoneId获取省市区id
            getAddressParent: function(zoneId,success,fail) {
                if (!zoneId) {
                    return false;
                }
                $.ajax({
                    url: host + '/zone/' + zoneId + '/parents',
                    type: 'POST',
                    dataType: 'json',
                    success: function(data) {
                        if (data.data.length) {
                            success && success(data);
                        } else {
                            fail && fail();
                        }
                    },
                    error: function(state) {
                        if (state.status == 401) {
                            utils.tool.goLogin();
                        } else {
                            fail && fail('服务器暂时没有相应，请稍后重试...');
                        }
                    }
                });
            },
            address: function(id, success, fail) {
                if (!id) {
                    return;
                }
                var that = this;
                $.ajax({
                    url: host + '/zone/' + id + '/children',
                    type: 'POST',
                    dataType: 'json',
                    success: function(data) {
                        if (data.data.length) {
                            success && success(data);
                        } else {
                            fail && fail(data.moreInfo);
                        }
                    },
                    error: function(state) {
                        if (state.status == 401) {
                            utils.tool.goLogin();
                        } else {
                            fail && fail('服务器暂时没有相应，请稍后重试...');
                        }
                    }
                });
            },
            getShopInfo: function(shopId, success, fail) {
                var that = this;
                $.ajax({
                    url: host + '/shop/' + shopId,
                    type: 'POST',
                    dataType: 'json',
                    success: function(data) {
                        if (data.errorCode == 200) {
                            success && success(data);
                        } else {
                            fail && fail(data.moreInfo);
                        }
                    },
                    error: function(state) {
                        if (state.status == 401) {
                            utils.tool.goLogin();
                        } else {
                            fail && fail('服务器暂时没有相应，请稍后重试...');
                        }
                    }
                });
            },
            updateShop: function(data, success, fail) {
                var that = this;
                $.ajax({
                    url: host + '/shop/update',
                    type: 'POST',
                    data: data,
                    dataType: 'json',
                    success: function(data) {
                        if (data.errorCode == 200) {
                            success && success(data);
                        } else {
                            fail && fail(data.moreInfo);
                        }
                    },
                    error: function(state) {
                        if (state.status == 401) {
                            utils.tool.goLogin();
                        } else {
                            fail && fail('服务器暂时没有相应，请稍后重试...');
                        }
                    }
                });
            },
            editMobile: function(data, success, fail) {
                var that = this;
                $.ajax({
                    url: host + '/shop/saveMobileAndTel',
                    type: 'POST',
                    data: data,
                    dataType: 'json',
                    success: function(data) {
                        if (data.errorCode == 200) {
                            success && success(data);
                        } else {
                            fail && fail(data.moreInfo);
                        }
                    },
                    error: function(state) {
                        if (state.status == 401) {
                            utils.tool.goLogin();
                        } else {
                            fail && fail('服务器暂时没有相应，请稍后重试...');
                        }
                    }
                });
            },
            updatePostAge: function(data, success, fail) {
                var that = this;
                $.ajax({
                    url: host + '/shop/updatePostageSet',
                    type: 'POST',
                    data: data,
                    dataType: 'json',
                    success: function(data) {
                        if (data.errorCode == 200) {
                            success && success(data);
                        } else {
                            fail && fail(data.moreInfo);
                        }
                    },
                    error: function(state) {
                        if (state.status == 401) {
                            utils.tool.goLogin();
                        } else {
                            fail && fail('服务器暂时没有相应，请稍后重试...');
                        }
                    }
                });
            },
            //保存段落描述
            saveDesc: function(data, success, fail) {
                var that = this;
                $.ajax({
                    url: host + '/fragment/save',
                    type: 'POST',
                    data: data,
                    dataType: 'json',
                    success: function(data) {
                        if (data.errorCode == 200) {
                            success && success(data);
                        } else {
                            fail && fail(data.moreInfo);
                        }
                    },
                    error: function(state) {
                        if (state.status == 401) {
                            utils.tool.goLogin();
                        } else {
                            fail && fail('服务器暂时没有相应，请稍后重试...');
                        }
                    }
                });
            },
            //删除段落描述
            delDesc: function(id, success, fail) {
                var that = this;
                $.ajax({
                    url: host + '/fragment/delete',
                    type: 'POST',
                    data: {
                        id: id
                    },
                    dataType: 'json',
                    success: function(data) {
                        if (data.errorCode == 200) {
                            success && success(data);
                        } else {
                            fail && fail(data.moreInfo);
                        }
                    },
                    error: function(state) {
                        if (state.status == 401) {
                            utils.tool.goLogin();
                        } else {
                            fail && fail('服务器暂时没有相应，请稍后重试...');
                        }
                    }
                });
            },
            setFragment: function(isEnable, success, fail) {
                var that = this;
                $.ajax({
                    url: host + '/shop/set_fragment',
                    type: 'POST',
                    data: {
                        enable: isEnable
                    },
                    dataType: 'json',
                    success: function(data) {
                        if (data.errorCode == 200) {
                            success && success(data);
                        } else {
                            fail && fail(data.moreInfo);
                        }
                    },
                    error: function(state) {
                        if (state.status == 401) {
                            utils.tool.goLogin();
                        } else {
                            fail && fail('服务器暂时没有相应，请稍后重试...');
                        }
                    }
                });
            },
            setFragmentIdx: function(ids, success, fail) {
                var that = this;
                $.ajax({
                    url: host + '/fragment/saveIdx',
                    type: 'POST',
                    data: {
                        ids: ids
                    },
                    dataType: 'json',
                    success: function(data) {
                        if (data.errorCode == 200) {
                            success && success(data);
                        } else {
                            fail && fail(data.moreInfo);
                        }
                    },
                    error: function(state) {
                        if (state.status == 401) {
                            utils.tool.goLogin();
                        } else {
                            fail && fail('服务器暂时没有相应，请稍后重试...');
                        }
                    }
                });
            },
            getFragList: function(success, fail){
                var that = this;
                $.ajax({
                    url: host + '/fragment/list',
                    type: 'POST',
                    dataType: 'json',
                    success: function(data) {
                        if (data.errorCode == 200) {
                            success && success(data);
                        } else {
                            fail && fail(data.moreInfo);
                        }
                    },
                    error: function(state) {
                        if (state.status == 401) {
                            utils.tool.goLogin();
                        } else {
                            fail && fail('服务器暂时没有相应，请稍后重试...');
                        }
                    }
                });
            },
            changeEnableDesc: function(data,success,fail){
                var that = this;
                $.ajax({
                    url: host + '/product/changeEnableDesc',
                    type: 'POST',
                    data: data,
                    dataType: 'json',
                    success: function(data) {
                        if (data.errorCode == 200) {
                            success && success(data);
                        } else {
                            fail && fail(data.moreInfo);
                        }
                    },
                    error: function(state) {
                        if (state.status == 401) {
                            utils.tool.goLogin();
                        } else {
                            fail && fail('服务器暂时没有相应，请稍后重试...');
                        }
                    }
                });
            },
            saveProductDesc: function(data,success,fail){
                var that = this;
                $.ajax({
                    url: host + '/product/saveProductDesc',
                    type: 'POST',
                    data: data,
                    dataType: 'json',
                    success: function(data) {
                        if (data.errorCode == 200) {
                            success && success(data);
                        } else {
                            fail && fail(data.moreInfo);
                        }
                    },
                    error: function(state) {
                        if (state.status == 401) {
                            utils.tool.goLogin();
                        } else {
                            fail && fail('服务器暂时没有相应，请稍后重试...');
                        }
                    }
                });
            },
            saveProductFragment: function(data,success,fail){
                var that = this;
                $.ajax({
                    url: host + '/productFragment/save',
                    type: 'POST',
                    data: data,
                    dataType: 'json',
                    success: function(data) {
                        if (data.errorCode == 200) {
                            success && success(data);
                        } else {
                            fail && fail(data.moreInfo);
                        }
                    },
                    error: function(state) {
                        if (state.status == 401) {
                            utils.tool.goLogin();
                        } else {
                            fail && fail('服务器暂时没有相应，请稍后重试...');
                        }
                    }
                });
            },
            saveProduct: function(data,success,fail){
                var that = this;
                $.ajax({
                    url: host + '/product/save',
                    type: 'POST',
                    data: data,
                    dataType: 'json',
                    success: function(data) {
                        if (data.errorCode == 200) {
                            success && success(data);
                        } else {
                            fail && fail(data.moreInfo);
                        }
                    },
                    error: function(state) {
                        if (state.status == 401) {
                            utils.tool.goLogin();
                        } else {
                            fail && fail('服务器暂时没有相应，请稍后重试...');
                        }
                    }
                });
            },
            getProductInfo: function(id, success, fail) {
                var that = this;
                $.ajax({
                    url: host + '/product/' + id,
                    type: 'POST',
                    dataType: 'json',
                    success: function(data) {
                        if (data.errorCode == 200) {
                            success && success(data);
                        } else {
                            fail && fail(data.moreInfo);
                        }
                    },
                    error: function(state) {
                        if (state.status == 401) {
                            utils.tool.goLogin();
                        } else {
                            fail && fail('服务器暂时没有相应，请稍后重试...');
                        }
                    }
                });
            },
            //获取商品列表
            getProductList: function(type, options, success, fail) {
                var that = this;
                var link = '';
                switch (type) {
                    //在售
                    case "onsale":
                        link = '/product/list';
                        // options.order = 'onsaleAt';
                        break;
                        //草稿 
                    case "draft":
                        link = '/product/list';
                        options.order = 'statusDraft';
                        break;
                        //计划
                    case "delay":
                        link = '/product/list/forsalebyPC';
                        break;
                        // 下架
                    case "offsale":
                        link = '/product/list';
                        options.order = 'soldout';
                        break;
                    case "search":
                        link = '/product/searchbyPc/' + window.shopId + '/' + options.keyword;
                        delete options.keyword;
                        break;
                    default:
                        link = '/product/list';
                }

                var settings = {
                    order: '',
                    direction: '',
                    pageable: true,
                    page: 0,
                    size: 8
                }
                $.extend(settings, options);
                $.ajax({
                    url: host + link,
                    type: 'POST',
                    data: settings,
                    dataType: 'json',
                    success: function(data) {
                        if (data.errorCode == 200) {
                            data.type = type;
                            success && success(data);
                        } else {
                            fail && fail(data.moreInfo);
                        }
                    },
                    error: function(state) {
                        if (state.status == 401) {
                            utils.tool.goLogin();
                        } else {
                            fail && fail('服务器暂时没有相应，请稍后重试...');
                        }
                    }
                });
            },
            batchInstock: function(ids, success, fail) {
                var that = this;
                var data = {
                    'ids': ids.join(',')
                };
                $.ajax({
                    url: host + '/product/batch-instock',
                    type: 'POST',
                    data: data,
                    dataType: 'json',
                    success: function(data) {
                        if (data.errorCode == 200) {
                            success && success(data);
                        } else {
                            fail && fail(data.moreInfo);
                        }
                    },
                    error: function(state) {
                        if (state.status == 401) {
                            utils.tool.goLogin();
                        } else {
                            fail && fail('服务器暂时没有相应，请稍后重试...');
                        }
                    }
                });
            },
            batchOnsale: function(ids, success, fail) {
                var that = this;
                var data = {
                    'ids': ids.join(',')
                };
                $.ajax({
                    url: host + '/product/batch-onsale',
                    type: 'POST',
                    data: data,
                    dataType: 'json',
                    success: function(data) {
                        if (data.errorCode == 200) {
                            success && success(data);
                        } else {
                            fail && fail(data.moreInfo);
                        }
                    },
                    error: function(state) {
                        if (state.status == 401) {
                            utils.tool.goLogin();
                        } else {
                            fail && fail('服务器暂时没有相应，请稍后重试...');
                        }
                    }
                });
            },
            /**
             * 保存店铺佣金
             * @param  {[Number]} commisionRate  [佣金价格，如果是null，则代表不打开佣金设置]
             * @param  {[Function]} success       [description]
             * @param  {[Function]} fail          [description]
             * @return {[type]}               [description]
             */
            saveShopCommission: function(commisionRate,success,fail){
                var that = this;
                $.ajax({
                    url: host + '/shop/saveShopCommission',
                    type: 'POST',
                    data: {
                        commisionRate: commisionRate
                    },
                    dataType: 'json',
                    success: function(data) {
                        if (data.errorCode == 200) {
                            success && success(data);
                        } else {
                            fail && fail(data.moreInfo);
                        }
                    },
                    error: function(state) {
                        if (state.status == 401) {
                            utils.tool.goLogin();
                        } else {
                            fail && fail('服务器暂时没有相应，请稍后重试...');
                        }
                    }
                });
            },
            saveThirdCommission: function(data,success,fail){
                var that = this;
                $.ajax({
                    url: host + '/shop/saveThirdCommission',
                    type: 'POST',
                    data: data,
                    dataType: 'json',
                    success: function(data) {
                        if (data.errorCode == 200) {
                            success && success(data);
                        } else {
                            fail && fail(data.moreInfo);
                        }
                    },
                    error: function(state) {
                        if (state.status == 401) {
                            utils.tool.goLogin();
                        } else {
                            fail && fail('服务器暂时没有相应，请稍后重试...');
                        }
                    }
                });
            },
            //获取快递公司
            getLogistics: function(success,fail){
                $.ajax({
                    url: host + '/logistics/list',
                    type: 'POST',
                    dataType: 'json',
                    success: function(data) {
                        if (data.errorCode == 200) {
                            success && success(data);
                        } else {
                            fail && fail(data.moreInfo);
                        }
                    },
                    error: function(state) {
                        if (state.status == 401) {
                            utils.tool.goLogin();
                        } else {
                            fail && fail('获取快递公司失败！');
                        }
                    }
                });
            },
            /**
             * 获取店铺分类信息
             * @type {Number}        [选择ajax地址]
             * @cateId {Number}      [有则获取该分类下的商品列表，
             *                        无则获取店铺所有分类列表]
             * @success  {Function}  [成功回调]
             * @fail  {Function}     [失败回调]
             * author baize
             */
            getProCateList: function(type, cateId, success, fail, options) {
                var ajaxUrl = '';
                var ajaxCateId = cateId || 0;
                switch (type) {
                    //全部商品
                    case 1:
                        ajaxUrl = '/category/product/list';
                        ajaxCateId = -1;
                        break;
                    //某分类下的商品或者全部未分类的商品
                    case 2:
                        ajaxUrl = '/category/product/list';
                        break;
                    //店铺分类列表
                    case 3:
                        ajaxUrl = '/shop/category/list';
                        break;
                    default:
                        ajaxUrl = '/category/product/list';
                };
                var settings = {
                    page: 0,
                    size: 8,
                    id: ajaxCateId
                }
                $.extend(settings, options);
                $.ajax({
                    url: host + ajaxUrl,
                    type: 'POST',
                    dataType: 'json',
                    data: settings,
                    success: function(data) {
                        if (data.errorCode == 200) {
                            success && success(data);
                        } else {
                            fail && fail(data.moreInfo);
                        }
                    },
                    error: function(state) {
                        if (state.status == 401) {
                            utils.tool.goLogin();
                        } else {
                            fail && fail('获取店铺分类信息失败！');
                        }
                    }
                })
            },
            /**
             * [新增分类]
             * @param {[String]} cateName [分类名称]
             * @param {[Function]} success  [成功回调]
             * @param {[Function]} fail     [失败回调]
             */
            addProCate: function(cateName, success, fail) {
                $.ajax({
                    url: host + '/shop/category/save',
                    type: 'POST',
                    data: {
                        name: cateName
                    },
                    dataType: 'JSON',
                    success: function(data){
                        if (data.errorCode == 200) {
                            success && success(data);
                        } else {
                            fail && fail(data.moreInfo);
                        }
                    },
                    error: function(state) {
                        if (state.status == 401) {
                            utils.tool.goLogin();
                        } else {
                            fail && fail('获取店铺分类信息失败！');
                        }
                    }
                })
            },
            /**
             * [批量删除分类]
             * @param  {[Array]} ajaxData [待移除分类id]
             * @param  {[Function]} success  [成功回调]
             * @param  {[Function]} fail     [失败回调]
             */
            removeProCate: function(ajaxData, success, fail) {
                $.ajax({
                    url: host + '/shop/category/batchRemove',
                    type: 'POST',
                    dataType: 'json',
                    traditional: true,
                    data: {
                        ids : ajaxData
                    },
                    success: function(data){
                        if (data.errorCode == 200) {
                            success && success(data);
                        }else {
                            fail && fail(data.moreInfo);
                        }
                    },
                    error: function(state) {
                        if (state.status == 401) {
                            utils.tool.goLogin();
                        } else {
                            fail && fail('获取店铺分类信息失败！');
                        }
                    }
                })
            },
            /**
             * [批量修改商品所属分类]
             * @param  {[Number]} cateId  [待移动的分类id]
             * @param  {[Array]} proId   [待移动的商品id]
             * @param  {[Function]} success [成功回调]
             * @param  {[Function]} fail    [失败回调]
             */
            changeProCate: function(cateId, proId, success, fail) {
                $.ajax({
                    url: host + '/category/product/add',
                    type: 'POST',
                    dataType: 'json',
                    traditional: true,
                    data: {
                        categoryId : cateId,
                        productIds : proId
                    },
                    success: function(data){
                        if (data.errorCode == 200) {
                            success && success(data);
                        } else {
                            fail && fail(data.moreInfo);
                        }
                    },
                    error: function(state) {
                        if (state.status == 401) {
                            utils.tool.goLogin();
                        } else {
                            fail && fail('获取店铺分类信息失败！');
                        }
                    }
                })
            },
            changeCateName: function(changeData, success, fail, changeCateName) {
                $.ajax({
                    url: host + '/shop/category/batchUpdate',
                    type: 'POST',
                    dataType: 'json',
                    traditional: true,
                    data: changeData,
                    success: function(data){
                        if (data.errorCode == 200) {
                            success && success(changeCateName);
                        } else {
                            fail && fail(data.moreInfo);
                        }
                    },
                    error: function(state) {
                        if (state.status == 401) {
                            utils.tool.goLogin();
                        } else {
                            fail && fail('修改店铺分类信息失败！');
                        }
                    }
                })
            },
            /**
             * [getCategoryList 获取平台类目列表]
             * @param  {[type]} data    [穿过来的参数]
             * @param  {[type]} success [description]
             * @param  {[type]} fail    [description]
             * @return {[null]}         [null]
             */
            getCategoryList: function(data,success,fail){
                if( data.id  == ''){
                    data = {};
                }
                $.ajax({
                    url: host + '/domain/category',
                    type: 'GET',
                    dataType: 'json',
                    data: data,
                    success: function(data){
                        if (data.errorCode == 200) {
                            success && success(data);
                        } else {
                            fail && fail(data.moreInfo);
                        }
                    },
                    error: function(state) {
                        if (state.status == 401) {
                            utils.tool.goLogin();
                        } else {
                            fail && fail('服务器暂时没有相应，请稍后重试...');
                        }
                    }
                })
            }
        },
        tool: {
            /**
             * [alert弹出框]
             * @param  {[String]} msg [显示的消息]
             * @return {null}     [description]
             * @example  utils.tool.alert('这是消息!');
             * @author maat
             */
            alert: function(msg) {
                var $alertMsg = $('#J-KD-alert-msg'),
                    $alertBox = $('#J-KD-alert'),
                    $dimmer = $('.am-dimmer');
                $alertMsg.text(msg);
                $alertBox.modal({
                    autoClose: true,
                    closeTime: 3000,
                    onConfirm: function() {
                        this.close();
                    }
                });
            },
            /**
             * [confirm效果]
             * @param  {[String]} sMsg      [消息内容]
             * @param  {[Function]} fnConfirm [确认的回调函数],如果是和当前点击的对象有关，需要使用call形式把this指向改变一下
             * @return {[null]}          
             * @example     utils.tool.confirm.call(this,'确认?',function($el){
                                console.log($el.attr('id'));
                            });
             * @author maat
             */
            confirm: function(sMsg, fnConfirm,fnCancel) {
                var $confirmMsg = $('#J-KD-confirm-msg'),
                    $confirmBox = $('#J-KD-confirm');
                $confirmMsg.text(sMsg);
                window.confirmFn = fnConfirm;
                window.cancelFn = fnCancel;
                $confirmBox.modal({
                    relatedTarget: this,
                    onConfirm: function() {
                        //确认按钮的回调，把当前点击打开弹窗的handel传递给回调函数
                        window.confirmFn && window.confirmFn($(this.relatedTarget));
                        this.close();
                    },
                    onCancel: function() {
                        window.cancelFn&&window.cancelFn($(this.relatedTarget));
                        this.close();
                    }
                });
            },
            getAbsUrl: function(url) {
                var img = new Image();
                img.src = url; // 设置相对路径给image, 此时会发送出请求
                url = img.src; // 此时相对路径已经变成绝对路径
                img.src = null; // 取消请求
                return url;
            },
            /**
             * [request 获取url参数]
             * @param  {[string]} param [参数名称]
             * @return {[string]}       [返回参数值]
             * @example 调用：utils.tool.request(参数名称); 
             * @author apis
             */
            request : function(param) {
                var url = location.href;
                var paraString = url.substring(url.indexOf("?")+1,url.length).split(/\&|\#/g);
                var paraObj = {}
                for (i=0; j=paraString[i]; i++){
                    paraObj[j.substring(0,j.indexOf("=")).toLowerCase()] = j.substring(j.indexOf("=")+1,j.length);
                }
                var returnValue = paraObj[param.toLowerCase()];
                if(typeof(returnValue)=="undefined"){
                    return "";
                }else{
                    return returnValue;
                }
            },
            goLogin: function(noMsg){
                if(noMsg){
                    utils.tool.alert('退出成功～');
                }else{
                    utils.tool.alert('由于您长时间没有操作，请重新登录～');
                }
                setTimeout(function(){
                    location.href = '/sellerpc/pc/login.html';
                },1000);
            },
            /**
             * [商品评论'星星'列表生成]
             * @param  {[obj]} params [参数]
             * @author apis
             */
            scoreStarShow: function (params){
                var defaults = {
                    trigger: '.mod-score',//星星列表外层容器
                    font: '12px',//星星大小
                    colorS: '#f9b700',//已经评论星星颜色
                    colorE: '#e9e9e9',//未评论星星颜色
                    scoreAll: 5,//星星总数
                    scoreShow: 0,//已经评论星星个数
                    eventClk: true//星星是否具有点击事件
                };
                var opts = $.extend(defaults,params);
                var html = '';
                for (var i = 0; i < opts.scoreAll; i++) {
                    if (i<Math.round(opts.scoreShow)) {
                        html += '<em class="solid">&#xe620;</em>';
                    }else{
                        html +='<em class="empty">&#xe61f;</em>';
                    }
                }
                $(opts.trigger).append(html);

                if (opts.eventClk) {
                    $(document).on('click',opts.trigger+' em',function(){
                        var _this = $(this);
                        if (_this.next('em').hasClass('solid') || _this.prev('em').hasClass('empty')) {
                            return false;
                        }
                        if (_this.hasClass('solid')) {
                            _this.removeClass('solid').addClass('empty').html('&#xe61f;');
                        }else{
                            _this.removeClass('empty').addClass('solid').html('&#xe620;');
                        }
                    });
                };
            },
            /**
             * [获得字符串的字节长度，超出一定长度在后面加符号]
             * @param  {[String]} str  [待查字符串]
             * @param  {[Number]} len  [指定长度]
             * @param  {[Number]} type [1为返回长度,2为截取后的字符串]
             * @param  {[String]} more [替换超出字符的符号]
             */
            getStringLength: function(str, type, len, more) {
                var str_length = 0;
                var str_len = 0;
                str_cut = new String();
                str_len = str.length;
                if (type = 1) {
                    for (var i = 0; i < str_len; i++) {
                        a = str.charAt(i);
                        str_length++;
                        if (escape(a).length > 4) {
                            str_length++;
                        }
                    }
                    return str_length;
                };
                if (type = 2) {
                    for (var i = 0; i < str_len; i++) {
                        a = str.charAt(i);
                        str_length++;
                        if (escape(a).length > 4) {
                            str_length++;
                        }
                        str_cut = str_cut.concat(a);
                        if (str_length >= len) {
                            if (more && more.length > 0)
                                str_cut = str_cut.concat(more);
                            return str_cut;
                        }
                    }
                    if (str_length < len) {
                        return str;
                    }
                }
            }
        }
    };
    return utils;
});