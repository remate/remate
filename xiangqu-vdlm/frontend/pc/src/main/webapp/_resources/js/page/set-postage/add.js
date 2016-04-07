define(['jquery','./render-pop','./show-pop'], function(jquery,renderPop,showPop) {
    $('.set-add a').on('click', function() {
        renderPop();
        showPop.show.call(this);
    });
    return null;
});