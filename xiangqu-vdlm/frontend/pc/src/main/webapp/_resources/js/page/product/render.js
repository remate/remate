define(['jquery', 'base/utils', 'base/product/elements', 'doT', 'base/product/renderSku','address','base/product/platform-getCategory'], function(jquery, utils, eles, doT, renderSku,address,GetCategory) {
    var id = utils.tool.request('id');
    var tplImg = $('.tpl-desc-img').html(),
        doTtplImg = doT.template(tplImg),
        tplDesc = $('.tpl-desc').html(),
        doTtplDesc = doT.template(tplDesc),
        tplSkuItem = $('.tpl-skuType').html(),
        doTtplSkuItem = doT.template(tplSkuItem),
        shopProCate = '',
        fullCatId = '';
    var getCategory = new GetCategory();
    if (id) {
        //如果是修改商品 
        utils.api.getProductInfo(id, function(data) {
            var proData = data.data;
            //平台类目属性
            fullCatId = proData.fullCatId;
            //渲染平台类目属性
            getCategory.init(fullCatId);
            //商品图片
            if(proData.imgs){
                $.each(proData.imgs, function(i, img) {
                    var key = img.img,
                        url = img.imgUrl,
                        str = doTtplImg({
                            imgUrl: url,
                            img: key
                        });
                    addImg(str);
                });
            }
            //商品名称
            eles.proName.val(proData.name);
            //商品描述
            eles.proDesc.val(proData.description);
            //价格和库存
            eles.proPrice.val(proData.price);
            eles.proAmount.val(proData.amount);
            //商品类目
            var proCate = '';
            //推荐
            if (proData.recommend) {
                eles.proRecomend.bootstrapSwitch('state', true);
            } else {
                eles.proRecomend.bootstrapSwitch('state', false);
            }
            //发布设置
            eles.proStatus.filter('[value="' + proData.status + '"]').iCheck('check');
            //计划发布时间
            $('.date-picker input').val(timeFormat(proData.forsaleAt, 'yyyy-MM-dd HH:mm:ss'));
            //段落描述
            if(proData.fragments){
                $.each(proData.fragments, function(i, fraData) {
                    renderDesc(fraData);
                });
            }
            //sku 数据
            window.skuInfo = {
                mappings: [],
                skus: [],
            };

            renderSku(proData);

            //渲染编辑sku弹出框里面第一步的sku选项
            $.each(proData.skuMappings, function(i, el) {
                var val = el.specName;
                if (_.indexOf(eles.skuBankData, val) >= 0) {
                    //如果是常用的
                    eles.skuBank.find('input[data-val="' + val + '"]').iCheck('check');
                } else {
                    var data = {
                        name: val,
                        isSelf: true
                    }
                    eles.skuSelf.append(doTtplSkuItem(data));
                }
            });
            eles.skuSelf.find('input').attr('checked', true).initCheck();
            //是否已段落描述作为  enableDesc ： false
            if(!proData.enableDesc){
                eles.proPubType.filter(':eq(1)').iCheck('check');
                $('.desc-show-box').show();
                $('.ueditBox').hide();
            }else{
                eles.proPubType.filter(':eq(0)').iCheck('check');
                $('.desc-show-box').hide();
                $('.ueditBox').show();
                //渲染富文本编辑器内容
                if(proData.productDesc){
                    window.productDescId = proData.productDesc.id;
                    if(window.editor){
                        editor.html(proData.productDesc.description);
                    }else{
                        $('#J-editor').val(proData.productDesc.description);
                    }
                }
            }
            shopProCate = data.data.category? data.data.category.id : null;
            utils.api.getProCateList(3, proData.id, setProCateForm,utils.tool.alert);
        }, function() {
            utils.tool.alert('获取商品数据出错!');
        });
    }else{
        utils.api.getProCateList(3, null, setProCateForm,utils.tool.alert);
        getCategory.init(fullCatId);
    }



    function addImg(str) {
        eles.proImgsWrap.find('.desc-pop-add').before(str);
    }

    function renderDesc(fraData) {
        var aImgsModel = [];
        if(fraData.imgs){
            $.each(fraData.imgs, function(i, el) {
                aImgsModel.push({
                    imgUrl: el.imgUrl,
                    img: el.img
                });
            });
        }
        var data = {
            id: fraData.id,
            name: fraData.name,
            description: fraData.description,
            showModel: fraData.showModel ? 1 : 0,
            imgList: aImgsModel
        };
        var str = doTtplDesc(data);
        eles.proDescBox.append(str);
    }

    function timeFormat(time, format) {
        var t = new Date(time);
        var tf = function(i) {
            return (i < 10 ? '0' : '') + i
        };
        return format.replace(/yyyy|MM|dd|HH|mm|ss/g, function(a) {
            switch (a) {
                case 'yyyy':
                    return tf(t.getFullYear());
                    break;
                case 'MM':
                    return tf(t.getMonth() + 1);
                    break;
                case 'mm':
                    return tf(t.getMinutes());
                    break;
                case 'dd':
                    return tf(t.getDate());
                    break;
                case 'HH':
                    return tf(t.getHours());
                    break;
                case 'ss':
                    return tf(t.getSeconds());
                    break;
            }
        })
    }
//在发布（编辑）商品页设置商品类目
    function setProCateForm(data) {
        var strArray = [];
        strArray.push('<option value="0">未分类</option>')
        var dataLength = data.data.length;
        for (var i = 0;i < dataLength;i++) {
            strArray.push('<option value=' + data.data[i].id + '>' + data.data[i].name + '</option>');
        };
        $('.product-Category').append(strArray.join(''));
        if(shopProCate){
            $('.product-Category').find('option[value="' + shopProCate + '"]').attr('selected', 'selected');
        }
        $('.product-Category').trigger("chosen:updated.chosen"); 
    }
    //新增分类的确定按钮
    $('body').on('click','.product-category-box ul li.addcate-editbox .yesbtn',function() {
        var tempCate = $('.product-category-box ul li.addcate-editbox input').val();
        if(!tempCate|| utils.tool.getStringLength(tempCate, 1) > 16) {
            utils.tool.alert('分类名称最多8个汉字或者16个字母。');
            return false;
        }
        utils.api.addProCate(tempCate,function(data) {
            $('.product-category-box select').append('<option value="' + data.data.id + '"></option>');
            $('.product-category-box select option').eq(-1).text(tempCate);
            $('.product-category-box ul li.addcate-editbox .nobtn').click();
            $('.product-category-box ul li.addcate-editbox input').val('');
            $('.product-Category').val('').trigger("chosen:updated");
            $('.chosen-single').click();
        },utils.tool.alert);
        return false;
    });
    //新增分类的取消按钮
    $('body').on('click','.product-category-box ul li.addcate-editbox .nobtn',function() {
        $('.product-category-box ul li.addcate').show();
        $('.product-category-box ul li.addcate-editbox').hide();
    });
    $('body').on('click','.chosen-single',function(){
        var lastLi = ['<li class="addcate"> +新增 </li>',
                    '<li class="addcate-editbox">',
                    '<input type="text" value="" />',
                    '<button class="yesbtn" type="button">确定</button>',
                    '<button class="nobtn" type="button">取消</button>',
                    '</li>'];
        $('.product-category-box .chosen-container .chosen-drop .chosen-results').append(lastLi.join(''));
        $('.product-category-box .addcate-editbox').hide();
    });
    $('body').on('click','.product-category-box .addcate',function() {
        $('.product-category-box .addcate').hide();
        $('.product-category-box .addcate-editbox').css('display','list-item');
        var rowpos = ($('.active-result').length + 2) * 40;
        $('.chosen-results').scrollTop(rowpos);
    });
});