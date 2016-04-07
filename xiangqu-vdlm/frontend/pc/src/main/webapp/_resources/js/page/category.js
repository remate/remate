require(['jquery','base/utils'], function(jquery,utils) {

});

require(['base/category/getData','base/utils'],function(getdata,utils){
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
require(['base/category/down']);
//上架操作
require(['base/category/up']);
//排序
require(['base/category/sort'],function(sort){
    sort();
});
//初始化
require(['base/category/initCategory']);