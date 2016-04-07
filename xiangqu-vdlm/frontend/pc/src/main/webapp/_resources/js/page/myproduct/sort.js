define(['jquery', 'base/myproduct/getData', 'base/myproduct/initParams'], function(jquery, getData, initParams) {
    return function() {
        $('body').on('click', '.j-order', function() {
            if ($(this).hasClass('asc')) {
                //现在是非升序 改成升序
                $(this).removeClass('asc').addClass('desc').attr('data-dir', 'desc');
                $(this).html('&#xe612;');
            } else {
                $(this).removeClass('desc').addClass('asc').attr('data-dir', 'asc');
                $(this).html('&#xe613;');
            }
            $('[name="pageNum"]').val(1);
        });
        $('body').on('click', '.j-order-sales', function() {
            initParams(0, 0, $(this).attr('data-dir'), 0, 0);
            getData();
        });
        $('body').on('click', '.j-order-amount', function() {
            initParams(0, 0, 0, $(this).attr('data-dir'), 0);
            getData();
        });
        $('body').on('click', '.j-order-price', function() {
            initParams(0, 0, 0, 0, $(this).attr('data-dir'));
            getData();
        });
    }
});