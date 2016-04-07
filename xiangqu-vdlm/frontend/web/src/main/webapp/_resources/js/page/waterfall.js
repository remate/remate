// 瀑布流插件
// pannysp _num013.4.9
(function ($) {
    $.fn.waterfall = function(options) {
        var df = {
            'item': '.pro-infor',
            'itemWidth':300,
            'margin': 0
        };
        options = $.extend(df, options);

        /*
         原理：1.把所有的item的高度值放到数组里面
         2.第一行的top都为0
         3.计算高度值最小的值是哪个item
         4.把接下来的item放到那个item的下面
         */
        function itemuxiaofan(){
            var item= $(options['item']);//这里是区块名称
            var	item_W = options['itemWidth'];
            //取区块的实际宽度（包含间距，这里使用源生的offsetWidth函数，不适用jQuery的width()函数是因为它不能取得实际宽度，例如元素内有pandding就不行了）
            var h=[];//记录区块高度的数组
            var numb =item.length;
            $("#pro-all").css("width",item_W*numb);
            var n = document.documentElement.offsetWidth/item_W|0;//窗口的宽度除以区块宽度就是一行能放几个区块
            for(var i = 0;i < numb;i++) {//有多少个item就循环多少次
                item_H = item[i].offsetHeight;//获取每个item的高度
                if(i < n) {//n是一行最多的item，所以小于n就是第一行了
                    h[i]=item_H;//把每个item放到数组里面
                    item.eq(0).css("top",0);//第一行的item的top值为0
                    item.eq(i).css("left",i * item_W);//第i个item的左坐标就是i*item的宽度
                }
                else{
                    min_H =Math.min.apply(null,h) ;//取得数组中的最小值，区块中高度值最小的那个
                    minKey = getarraykey(h, min_H);//最小的值对应的指针
                    h[minKey] += item_H+margin ;//加上新高度后更新高度值
                    item.eq(i).css("top",min_H+margin);//先得到高度最小的item，然后把接下来的item放到它的下面
                    item.eq(i).css("left",minKey * item_W);	//第i个item的左坐标就是i*item的宽度
                }
                //$("h3").eq(i).text("编号："+i+"，高度："+item_H);//把区块的序号和它的高度值写入对应的区块H3标题里面
            }
        }
        /* 使用for in运算返回数组中某一值的对应项数(比如算出最小的高度值是数组里面的第几个) */
        function getarraykey(s, v) {for(k in s) {if(s[k] == v) {return k;}}}
        /*这里一定要用onload，因为图片不加载完就不知道高度值*/
        window.onload = function() {itemuxiaofan();};
        /*浏览器窗口改变时也运行函数*/
        window.onresize = function() {itemuxiaofan();};

    }
})(af);