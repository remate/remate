var $ = require('jquery');
    $(".tuikuanbtn").bind("click", function (ev) {
        //$(this).addClass("tuikuanbtnico");
        if($(ev.target).hasClass('tuikuan')){
            location.href = "toRequest" + "?orderId=" + $("#orderId").val() + "&buyerRequire=" + 1;
        }else if($(ev.target).hasClass('tuihuotuikuan')){
            location.href = "toRequest" + "?orderId=" + $("#orderId").val() + "&buyerRequire=" + 2;
        }
    })
    $(".tuikuanbtndisable").unbind();
