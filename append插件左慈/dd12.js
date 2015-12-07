
    $(function(){
        //zc.productList=data.data;*********
        $("body").appendLi({
            tagNum:12,//可以选择的tag数目
            tagId:38,//接口的开始tag id
            everyTag_productNum:10,//每个li中的商品数目
            ul_className:'.J_product_ul',//ul的类名
            spm:'10.2.',//每次活动的商品的spm
            procuct_href:"$('body').attr('data-product')+zc.productList[k][p].products[0].productId+finalStr+k+p",//body中埋点商品链接开头
            hasFav:"zc.productList[k][p].products[0].hasFav",//添加是否为喜欢的class
            product_id:"zc.productList[k][p].products[0].productId",//取商品id
            favNum:"zc.productList[k][p].products[0].faverNum > 0 ? zc.productList[k][p].products[0].faverNum : 0",//喜欢数
            imgSrc:"zc.productList[k][p].products[0].imgUrl",//商品图片的连接
            product_title:"zc.productList[k][p].products[0].title",//商品的标题
            icon_common:false,//每个tag的商品icon是否为相同的判断
            icon_commonText:"common",//为相同时显示的内容
            icon_differentText:['双鱼价','水瓶价','摩羯价','射手价','天蝎价','天秤价','处女价','狮子价','巨蟹价','双子价','金牛价','白羊价'],//为不同时将显示内容组成数组
            countPrice:"zc.productList[k][p].products[0].price",//打折后的价格
            originalPrice:"zc.productList[k][p].products[0].originalPrice"//原价
        });
    })