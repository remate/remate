//日期选择插件
require(['my97datepicker']);
//上传图片, 
require(['base/product/upload']);
//发布设置选项
require(['jquery'],function(){
    $('[name="prudct-status"]').on('ifChanged',function(){
        if( $(this).filter(':checked').val()  == 'FORSALE'){
            $('.date-picker').removeClass('hidden');
        }else{
            $('.date-picker').addClass('hidden');
        }
    });
});
//加载富文本编辑器
require(['base/product/setUeditor']);

//修改商品时渲染商品数据
require(['base/product/render']);

//sku点击与显示价格库存
require(['base/product/selectSku']);

//显示常见sku列表
require(['base/product/renderSkuBank']);
//增加自定义类型sku
require(['base/product/addSelfSku']);
//弹出编辑sku的窗口
require(['jquery','base/product/elements','amaze'],function(jquery,eles,amaze){
    eles.editSkus.on('click',function(){
        $('#J-modifySku-pop').modal();
    });
});
//关闭编辑sku的弹窗
require(['jquery','amaze'],function(){
    $('.modifySku-pop .am-btn-comfirm-no').on('click',function(){
        $('#J-modifySku-pop').modal('close');
    });
});
//sku每一步操作
require(['base/product/steps']);

//选择编辑方式
require(['base/product/editType']);

//验证并保存商品
require(['base/product/form-check']);

//删除段落描述
require(['base/product/delDesc']);

//段落描述列表
require(['base/product/callFragList']);

require(['jquery','sortRow'], function(jquery,sortRow) {
    sortRow( $('.desc-table') );
});

//直接调用段落设置里面的新增段落
require(['base/set-desc/add']);

//上传段落描述图片
require(['base/set-desc/upload']);

//删除图片
require(['base/set-desc/img-del']);

//修改段落描述
require(['base/set-desc/modify']);

//离开页面提醒
require(['base/all/close-tip']);