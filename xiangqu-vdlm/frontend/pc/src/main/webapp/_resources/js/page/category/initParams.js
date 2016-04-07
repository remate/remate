define(['jquery'], function(jquery) {
    return function(search, offset, orderSales, orderCount, orderPrice, status) {
        if (!search) {
            $('.am-form-search input').val('');
        } else {
            $('[name="keyword"]').val(search);
            $('.am-table-sort em').hide();
        }
        $('[name="pageOffset"]').val(offset);

        if (orderSales) {
            $('[name="orderField"]').val('sales');
            $('[name="direction"]').val(orderSales);
        } else if (orderCount) {
            $('[name="orderField"]').val('amount');
            $('[name="direction"]').val(orderCount);
        } else if (orderPrice) {
            $('[name="orderField"]').val('price');
            $('[name="direction"]').val(orderPrice);
        } else {
            $('[name="orderField"]').val('');
            $('[name="direction"]').val('');
        }

        if (status) {
            $('[name="status"]').val(status);
        }
    }
});