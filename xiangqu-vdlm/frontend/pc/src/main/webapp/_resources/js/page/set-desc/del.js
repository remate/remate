define(['jquery', 'base/utils'], function(jquery, utils,pop) {
    $('body').on('click','.desc-del',function(){
        utils.tool.confirm.call(this,'是否确认删除此段落描述？删除后不可恢复！',function($el){
            var $tr = $el.parents('tr');
            utils.api.delDesc($tr.attr('data-id'),function(){
                utils.tool.alert('删除成功！');
                $tr.remove();
            },function(){
                utils.tool.alert('删除失败！请稍后重试！');
            });
        });
    });
});