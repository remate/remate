define(['jquery', 'base/myproduct/getData', 'base/myproduct/initParams'], function(jquery, getData, initParams) {
    var $orderHandel = $('.am-table-sort em'),
        $selectStatus = $('[name="productStatus"]');

    $selectStatus.on('change', function() {
        var v = $(this).val();
        var _this = $(this);
        if (v == 'search') {
            return;
        } else if (v == 'onsale') {
            $orderHandel.show();
            $('.j-downs').css('display', 'inline-block');
            $('.j-ups').hide();
        } else if (v == 'offsale') {
            $orderHandel.hide();
            $('.j-downs').hide();
            $('.j-ups').css('display', 'inline-block');
        } else {
            $orderHandel.hide();
            $('.j-downs').hide();
            $('.j-ups').hide();
        }
        initParams(0, 0, 0, 0, 0, v);
        $('[name="pageNum"]').val(1);
        if (v != 'search' && $selectStatus.find('option[value="search"]').length) {
            $selectStatus.find('option[value="search"]').remove();
            $selectStatus.trigger("chosen:updated.chosen");
        }

        getData();
    });
});