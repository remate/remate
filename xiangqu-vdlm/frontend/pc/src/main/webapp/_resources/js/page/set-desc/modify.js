define(['jquery', 'base/utils','base/set-desc/show-pop'], function(jquery, utils,pop) {
    $('body').on('click','.desc-modify',function(){
        pop.show($(this).parents('tr'));
    });
});