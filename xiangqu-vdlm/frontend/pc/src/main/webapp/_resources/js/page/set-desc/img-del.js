/**
 * 删除段落图片
 */
define(['jquery','base/utils','base/set-desc/elements'], function(jquery,utils,elements) {
    $('body').on('click','.desc-pop-img-del',function(){
        utils.tool.confirm.call(this,'是否确认删除这张图片?',function($el){
            $el.parents('.desc-pop-img').remove();
        });
    });
});