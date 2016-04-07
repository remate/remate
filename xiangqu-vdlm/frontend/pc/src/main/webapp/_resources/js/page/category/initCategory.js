define(['jquery', 'base/utils', 'base/category/getData','amaze', 'base/all/checkbox', 'base/category/setPro'], function(jquery, utils, getData, amaze, checkbox, setPro) {
    //渲染商品所有分类
    var cateNameArr= []//存储分类名称
    function setProCateList(data) {
        var dataLength = data.data.length;
        var strArray = [];//字符串数组
        for (var i = 0;i < dataLength;i++) {
            strArray.push('<li data-id="' + data.data[i].id + '" data-index="'+ i + '"><span>' + data.data[i].name + '</span></li>');
            cateNameArr.push(data.data[i].name);
        };
        var lastLi = ['<li class="addcate">+新增分类</li>',
                    '<li class="addcate-editbox">',
                    '<input type="text" value="" />',
                    '<button class="yesbtn" type="button">确定</button>',
                    '<button class="nobtn" type="button">取消</button>',
                    '</li>'];
        $('.am-form-group-cate ul').append(strArray.join(''));
        $('.am-form-group-cate ul').append(lastLi.join(''));
    }
    //加载店铺分类信息
    utils.api.getProCateList(3,null,setProCateList,utils.tool.alert);

    //编辑状态下的店铺分类
    function initEditStatus() {
        $('.am-form-group-cate ul').attr('class','editstate editstatenow');
        $('.am-form-group-cate .editcate').css('margin','8px 5px');
        $('.addcate-editbox').hide();
        $('.am-form-group-cate ul li').append('<em>&#xe624;</em>').end()
                                        .eq(-2).find('em').remove().end()
                                        .eq(0).css('cursor','pointer');
        $('.am-form-group-cate ul li.allproduct em').remove();
        $('.am-form-group-cate ul li span').attr('contenteditable','true').css('cursor','text');
        $('.am-form-group-cate ul li em').css('cursor','pointer');
        $('.allproduct').attr('contenteditable','false');
        $('.addcate').attr('contenteditable','false');
        $('.addcate').hide();
        $(this).find('button').show();
        $(this).find('i').hide();
    }
    //点击编辑分类按钮
    $('body').on('click','.am-form-group-cate .editcate',initEditStatus);
    //编辑分类的取消按钮
    var waitRemoveArr = [];//待移除分类数组
    $('body').on('click','.am-form-group-cate .editcate .nobtn',function() {
        removeCateSuccess();
        //防止事件冒泡
        return false; 
    });
    //编辑分类的删除按钮
    $('body').on('click','.am-form-group-cate ul li em',function() {
        $(this).parents('li:first').hide().addClass('waitRemove');
        waitRemoveArr.push($(this).parents('li:first').attr('data-id'));
        //防止事件冒泡
        return false;
    });
    //编辑分类的确定按钮
    $('body').on('click','.am-form-group-cate span.editcate .yesbtn',function() {
        var changeData = {};//待修改分类
        var changeL = 0;//待修改分类长度
        var tempArray = [];
        $('.am-form-group-cate ul li').each(function(index) {
            if($(this).hasClass('addcate') || $(this).hasClass('allproduct') || $(this).hasClass('addcate-editbox') || $(this).hasClass('waitRemove')){
                return true;
            };
            var text = $(this).find('span').text();
            if(utils.tool.getStringLength(text, 1) > 16){
                tempArray.push(text);
            };
            //因为第一个li是全部商品，所以要-1
            if(text != cateNameArr[index - 1]) {
                changeData['categorys[' + changeL + '].id'] = $(this).attr('data-id');
                changeData['categorys[' + changeL + '].name'] = text;
                changeL++;
            };
        });
        if(tempArray.length) {
            utils.tool.alert('分类名称最多8个汉字或者16个字母。');
            return false;
        }
        if(!waitRemoveArr.length && !changeL){
            $('.am-form-group-cate .waitRemove').removeClass('waitRemove');
            removeCateSuccess();
            return false;
        }else {
            if (changeL && waitRemoveArr.length){
                utils.api.changeCateName(changeData,doubleSuccess,removeCateFail,waitRemoveArr);
            }else {
                if (changeL) {
                    utils.api.changeCateName(changeData,removeCateSuccess,removeCateFail,1);
                }else {
                    if (waitRemoveArr.length) {
                        utils.api.removeProCate(waitRemoveArr,removeCateSuccess,removeCateFail);
                    }
                }
            }
        }
        //防止事件冒泡
        return false;
    });
    function doubleSuccess() {
        cateNameArr = [];
        $('.am-form-group-cate ul li span').each(function(index) {
/*             if($(this).parents('li:first').hasClass('waitRemove')){
                return true;
             }*/
             cateNameArr.push($(this).text());
        });
        utils.api.removeProCate(waitRemoveArr,removeCateSuccess,removeCateFail);
    }
    //删除分类成功or未改变回调函数
    function removeCateSuccess(temp){
        waitRemoveArr = [];
        if(temp){
            cateNameArr = [];
            $('.am-form-group-cate .waitRemove').remove();
            $('.am-form-group-cate ul li span').each(function(index) {
                 if($(this).parents('li:first').hasClass('waitRemove')){
                    return true;
                 }
                 cateNameArr.push($(this).text());
            });
        }else {
            $('.am-form-group-cate ul li span').each(function(index) {
                $(this).text(cateNameArr[index]);
            });
            $('.am-form-group-cate .waitRemove').removeClass('waitRemove');
        }
        $('.am-form-group-cate ul li span br').remove();
        $('.editstate').removeClass();
        $('.addcate-editbox').show();
        $('.am-form-group-cate ul li').show();
        $('.am-form-group-cate .addcate-editbox').hide();
        $('.am-form-group-cate .editcate').css('margin','10px 20px');
        $('.am-form-group-cate ul li em').remove();
        $('.am-form-group-cate .editcate button').hide();
        $('.am-form-group-cate .editcate i').show();
        $('.am-form-group-cate .addcate').show();
        $('.am-form-group-cate ul li span').css('cursor','pointer');
        $('.am-form-group-cate ul li span').attr('contenteditable','false');
        $('.allproduct').attr('contenteditable','false');
        $('.addcate').attr('contenteditable','false');
    }
    //删除分类失败回调函数
    function removeCateFail(msg) {
        utils.tool.alert(msg);
        $('.waitRemove').show().removeClass('waitRemove');
        waitRemoveArr = [];
    };
    //新增分类按钮
    $('body').on('click','.am-form-group-cate .addcate',function() {
        $('.am-form-group-cate .addcate').hide();
        $('.am-form-group-cate .addcate-editbox').css('display','inline-block');
    });
    //新增分类的取消按钮
    $('body').on('click','.am-form-group-cate .addcate-editbox .nobtn',function() {
        $('.am-form-group-cate .addcate-editbox input').val('');
        $('.am-form-group-cate .addcate').show();
        $('.am-form-group-cate .addcate-editbox').hide();
    });
    //新增分类的确定按钮
    $('body').on('click','.am-form-group-cate .addcate-editbox .yesbtn',function() {
        var tempCate = $('.am-form-group-cate .addcate-editbox input').val();
        if(!tempCate || utils.tool.getStringLength(tempCate, 1) > 16) {
            utils.tool.alert('分类名称最多8个汉字或者16个字母。');
            return false;
        }
        utils.api.addProCate(tempCate,function(data) {
            $('.am-form-group-cate ul li').eq(-2).before('<li data-id="'+ data.data.id +'" data-index="' + cateNameArr.length + '"><span></span></li>');
            $('.am-form-group-cate ul li').eq(-3).find('span').text(tempCate);
            cateNameArr.push(tempCate);
            $('.am-form-group-cate .addcate-editbox .nobtn').click();
        },utils.tool.alert);
    });

    //弹出批量分类的窗口
    //待修改分类的商品数组
    var proCateArr = [];
    $('#J-cate').on('click', function() {
        proCateArr = [];
        if (!$('.order-item-body [name="productId"]:checked').length) {
            utils.tool.alert('请选择要分类的商品');
            return;
        }
        $('.order-item-body [name="productId"]:checked').each(function() {
            proCateArr.push($(this).parents('tr').attr('data-id'));
        });
        $('.modifyProCate-pop .kd-modal-title span').eq(-2).html(proCateArr.length);
        utils.api.getProCateList(3,null,setProCateListdialog,utils.tool.alert);
        return false;
    });
    //关闭批量分类的弹窗
    $('body').on('click','.modifyProCate-pop .am-btn-comfirm-no',function() {
        $('#J-modifyProCate-pop').modal('close');
        return false;
    });
    //批量分类弹窗的分类选定
    $('body').on('click','.kd-modal-dialog ul li',function() {
        $('.kd-modal-dialog ul li').removeClass('active').find('em').remove();
        if(!($(this).hasClass('addcate') || $(this).hasClass('addcate-editbox'))){
            var t = $(this).html();
            $('.kd-modal-title span').eq(-1).html(t);
            $(this).addClass('active').append('<em>&#xe625;</em>');
        }else {
        }
        return false;
    });
    //批量弹窗里面的新增分类按钮
    $('body').on('click','.kd-modal-dialog ul li.addcate',function() {
        $('.kd-modal-dialog ul li.addcate-editbox').show();
        $('.kd-modal-dialog ul li.addcate').hide();
        return false;
    });
    //批量弹窗里面的新增分类的确定按钮
    $('body').on('click','.kd-modal-dialog ul li.addcate-editbox .yesbtn',function() {
        var tempCate = $('.kd-modal-dialog ul li.addcate-editbox input').val();
        if(!tempCate|| utils.tool.getStringLength(tempCate, 1) > 16) {
            utils.tool.alert('分类名称最多8个汉字或者16个字母。');
            return false;
        }
        utils.api.addProCate(tempCate,function(data) {
            $('.kd-modal-dialog ul li').eq(-2).before('<li data-id="' + data.data.id + '">' + tempCate + '</li>');
            $('.kd-modal-dialog ul li.addcate-editbox .nobtn').click();
            var $tempUl = $('.kd-modal-dialog ul');
            $tempUl.scrollTop($tempUl.offset().top);
            $('.am-form-group-cate ul li').eq(-2).before('<li data-id="'+ data.data.id +'" data-index="' + cateNameArr.length+ '"><span></span></li>');
            $('.am-form-group-cate ul li').eq(-3).find('span').text(tempCate);
            cateNameArr.push(tempCate);
            $('.kd-modal-dialog ul li.addcate-editbox input').val('');
        },utils.tool.alert);
        return false;
    });
    //批量弹窗里面的新增分类的取消按钮
    $('body').on('click','.kd-modal-dialog ul li.addcate-editbox .nobtn',function() {
        $('.kd-modal-dialog ul li.addcate').show();
        $('.kd-modal-dialog ul li.addcate-editbox').hide();
    });
    //批量弹窗里面的确定按钮
    $("#proCatebtn").on('click',function() {
        var cateId = $('.kd-modal-dialog ul li.active').attr('data-id');
        if(!cateId){
            return false;
        }
        utils.api.changeProCate(cateId,proCateArr,function(){
            $('#J-modifyProCate-pop').modal('close');
            var options = {};
            options.page = $('[name="pageNum"]').val() - 1;
            var initId = $('.am-form-group-cate').find('.active').attr('data-id');
            utils.api.getProCateList(2,initId,setPro,utils.tool.alert,options);
        },utils.tool.alert);
    })
    //弹窗
    function setProCateListdialog(data) {
        var dataLength = data.data.length;
        $('.kd-modal-dialog ul li').remove();
        var strArray = [];
        strArray.push('<li data-id="0">未分类</li>');
        for (var i = 0;i < dataLength;i++) {
            strArray.push('<li data-id=' + data.data[i].id + '>' + data.data[i].name + '</li>');
        };
        var lastLi = ['<li class="addcate">+新增分类</li>',
                    '<li class="addcate-editbox">',
                    '<input type="text" value="" />',
                    '<button class="yesbtn" type="button">确定</button>',
                    '<button class="nobtn" type="button">取消</button>',
                    '</li>'];
        $('.kd-modal-dialog ul').append(strArray.join(''));
        $('.kd-modal-dialog ul').append(lastLi.join(''));
        $('#J-modifyProCate-pop').modal();
    }
    //分类tabs点击事件
    $('body').on('click','.am-form-group-cate ul li',function(){
        if($(this).parents('ul:first').hasClass('editstatenow') || $(this).hasClass('addcate-editbox') || $(this).hasClass('addcate') || $(this).hasClass('editcate')){
            return false;
        }
        var tempType = $(this).hasClass('allproduct')?1:2;
        var tempId = $(this).attr('data-id') || -1;
        var options = {};
        options.page = 0;
        $('[name="pageNum"]').val(1);
        $('.am-form-group-cate ul li').removeClass('active');
        $('.am-form-group-cate .notcate').removeClass('active');
        $(this).addClass('active');
        utils.api.getProCateList(tempType,tempId,setPro,utils.tool.alert,options);
        return false;
    })
    $('body').on('click','.am-form-group-cate ul li span',function(){
        if($(this).parents('ul:first').hasClass('editstatenow')){
            return false;
        }
    })
    //未分类商品列表
    $('body').on('click','.am-form-group-cate .notcate',function(){
        if($(this).hasClass('editcate')){
            return false;
        }
        removeCateSuccess();
        var options = {};
        options.page = 0;
        $('[name="pageNum"]').val(1);
        $('.am-form-group-cate ul li').removeClass('active');
        $(this).addClass('active');
        utils.api.getProCateList(2,0,setPro,utils.tool.alert,options);
        return false;
    })
});