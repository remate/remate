define(['jquery','./render-pop', './show-pop'], function(jquery,renderPop, showPop) {
    $('body').on('click','.post-modify', function() {
        var $el = $(this).parents('tr');
        renderPop($el);
        showPop.show.call(this);
    });
    return null;
});