require(['jquery','base/utils'], function(jquery,utils) {

});

require(['base/myproduct/getData','base/utils'],function(getdata,utils){
    //先判断是不是特定状态
    var kv = {
        "ONSALE":"onsale",
        "DRAFT":"draft",
        "FORSALE":"delay"
    };
    if(utils.tool.request('status')){
        $('select[name="productStatus"]').find('[value="'+ kv[utils.tool.request('status')] +'"]').attr('selected',true);
        $('select[name="productStatus"]').trigger("chosen:updated.chosen");
        $('#pagerForm [name="status"]').val(kv[utils.tool.request('status')]);
    }
    getdata();
});

//下架操作
require(['base/myproduct/down']);
//上架操作
require(['base/myproduct/up']);
//更改商品类型
require(['base/myproduct/change']);
//排序
require(['base/myproduct/sort'],function(sort){
    sort();
});
//搜索
require(['base/myproduct/search']);
