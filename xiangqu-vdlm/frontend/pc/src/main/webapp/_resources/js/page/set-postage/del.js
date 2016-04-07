define(['jquery','base/utils'], function(jquery,utils) {
    $('body').on('click','.post-del',function(){
        utils.tool.confirm.call(this,'确认删除此邮费配置？',function($el){
            $el.parents('tr').animate({opacity:0},300,function(){
                $(this).remove();
            });
        });
    });
    return null;
});