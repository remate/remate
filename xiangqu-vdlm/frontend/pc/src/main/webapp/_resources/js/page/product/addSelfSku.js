define(['jquery','base/product/elements','doT','base/utils'],function(jquery,eles,doT,utils){
    var tplSkuItem = $('.tpl-skuType').html(),
        doTtplSkuItem = doT.template(tplSkuItem);
    eles.addSkuSelf.on('click',function(){
        eles.addSkuSelfName.val('');
        eles.addSkuSelfBox.show();
    });
    eles.addSkuSelfHide.on('click',function(){
        eles.addSkuSelfBox.hide();
    });
    eles.addSkuSelfOk.on('click',function(){
        var val = eles.addSkuSelfName.val();
        if($.trim(val) == ''){
            utils.tool.alert('名称不能为空！');
            return;
        }
        var data = {
            name: val,
            isSelf: true
        }
        var $el = $(doTtplSkuItem(data));
        $el.find('input').initCheck();
        eles.skuSelf.append($el); 
        eles.addSkuSelfName.val('');
    });
    $('body').on('click','#J-skuSelf .skuModify-del',function(){
        utils.tool.confirm.call(this,'确定删除此类型？',function($el){
            $el.parents('.step1-skuItem').remove();
        });
    });
});