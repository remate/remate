define(['jquery', 'base/utils'], function(jquery, utils,pop) {
    $('body').on('click','.desc-del',function(){
        utils.tool.confirm.call(this,'是否确认删除此段落描述?',function($el){
            var $tr = $el.parents('tr');
            $tr.remove();
        });
    });
});