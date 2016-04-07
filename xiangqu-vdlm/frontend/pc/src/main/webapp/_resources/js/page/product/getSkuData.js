define(['jquery'], function(jquery) {
    return function(skus) {
        var skuArr = []; //副本
        $.each(skus, function(i, el) {
            //计算价格不为0的数据
            if (el.price && el.price > 0) {
                skuArr.push(el);
            }
        });
        //用来获取一组skus里面最小的价格 和 总的库存
        if (!skuArr.length) {
            return {
                "price": 0,
                "amount": 0
            };
        }
        skuArr.sort(function(a, b) {
            return Number(a.price) > Number(b.price);
        });
        var total = 0;
        $.each(skuArr, function(i, el) {
            total += Number(el.amount);
        });
        return {
            "price": skuArr[0].price,
            "amount": total
        }
    }
});