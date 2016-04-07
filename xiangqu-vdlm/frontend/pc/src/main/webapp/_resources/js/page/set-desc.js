

require(['jquery','sortRow'], function(jquery,sortRow) {
    sortRow( $('.desc-table') );
});

require(['base/set-desc/add']);

//保存,包括保存是否打开段落描述，以及各段落描述的排序
require(['base/set-desc/save']);

//因为段落部分操作发布商品页面也有用到，所以修改时请谨慎修改

//上传图片
require(['base/set-desc/upload']);

//删除图片
require(['base/set-desc/img-del']);

//修改
require(['base/set-desc/modify']);

//删除
require(['base/set-desc/del']);

//取消按钮
require(['base/set-postage/form-cancel']);

