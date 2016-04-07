    $(document).ready(function(){
        //数量输入框限定只能输入数字
        var numed=$(".numed");
        var edge = 0;
        if($("#inventory_amount").length!==0){
            edge =  parseInt($("#inventory_amount").text().replace(',',''));
        }else{
            edge = $(".numPlus").attr('edge');
        }
        $(".prodOption").on("click", function () {
            edge =  parseInt($("#inventory_amount").text().replace(',',''));
        })
        numed.on('change',function() {
            var regu = /^([0-9]+)$/;
            var re = new RegExp(regu);

            if (!re.test($(this).val())) {
                alert('请输入数字');
                numed.val('1').text('1').trigger('change');
                return false;
            }else if(Number($(this).val())<edge && Number($(this).val())<1){
                alert('请输入大于1的商品数字');
                numed.val('1').text('1').trigger('change');
                return false;
            }else if(Number($(this).val())>edge){
                alert('亲，手下留情哦，仓库都被你搬回家拉，所选数量大于库存数量了哦~');
                $(this).val(edge).text(edge).trigger('change');
                return false;
            }
        });
        $('.numInc,.numDec').on("click",function() {
            var self = $(this), numed = self.parent().children('.numed'),
                delta = self.hasClass('numInc') ? 1 : -1,  oldVal = parseInt(numed.val());
            oldVal = isNaN(oldVal) ? 0 : oldVal;
            var newVal = oldVal + delta;
            if(newVal<1){
                alert('商品数量只能大于等于1哦~');
                numed.val('1').text('1').trigger('change');
            }else if(newVal>edge){
                alert('亲，手下留情哦，仓库都被你搬回家拉，所选数量大于库存数量了哦~');
                numed.val(edge).text(edge).trigger('change');
            }else{
                numed.val(newVal).text(newVal).trigger('change');
            }
            return false;
        });
        $('#backButton').click(function() {
            history.back();
            return false;
        });

        var obj= $(".footer-bottom");
        var objs= obj.siblings();


        if( typeof(obj) !== "undefined"){
            $(objs.get(objs.length-1)).addClass('pb90');
        }

        $.wxShare = function(title, desc, img, link, appid) {
            function shareFriend() {
                WeixinJSBridge.invoke('sendAppMessage', {
                    "appid" : appid,
                    "img_url" : img,
                    "img_width" : "200",
                    "img_height" : "200",
                    "link" : link,
                    "desc" : desc,
                    "title" : title
                }, function(res) {
                    // _report('send_msg', res.err_msg);
                });
            }
            function shareTimeline() {
                WeixinJSBridge.invoke('shareTimeline', {
                    "img_url" : img,
                    "img_width" : "200",
                    "img_height" : "200",
                    "link" : link,
                    "desc" : desc,
                    "title" : title
                }, function(res) {
                    // _report('timeline', res.err_msg);
                });
            }
            function shareWeibo() {
                WeixinJSBridge.invoke('shareWeibo', {
                    "img_url" : img,
                    "link" : link,
                    "desc" : desc,
                    "title" : title
                }, function(res) {
                    // _report('weibo', res.err_msg);
                });
            }

            try{

                document.addEventListener('WeixinJSBridgeReady', function onBridgeReady() {
                    WeixinJSBridge.on('menu:share:appmessage', function(argv) {
                        shareFriend();
                    });
                    WeixinJSBridge.on('menu:share:timeline', function(argv) {
                        shareTimeline();
                    });
                    WeixinJSBridge.on('menu:share:weibo', function(argv) {
                        shareWeibo();
                    });
                }, false);
            }catch(e){

            }
        };
    });


