define(['base/taobao-move/goStep', 'jquery', 'base/utils'], function(step, jquery, utils) {
    $('.move-step2 .move-next').on('click', function() {
        var rnd = $('#code').val(),
            itemId = $('#productid').val();
        if (itemId == '' || rnd == '') {
            utils.tool.alert('商品ID不能为空!');
            return false;
        }
        utils.api.checkMoveCode(rnd, itemId, function(data) {
            if (data.data.statusCode == 404) {
                utils.tool.confirm('验证码已失效，请返回后重新获取！!',function(){
                    location.reload();
                });
                return false;
            } else {
                if (data.data.statusCode == 200) {
                    step.goStep3(1);
                } else {
                    if (data.data.statusCode == 601) {
                        utils.tool.alert('网络代理异常，请稍后再试!');
                        return false;
                    } else {
                        utils.tool.alert('未找到您的店铺，请确定ID是否正确输入!');
                        return false;
                    }
                }
            }
        });
    });

});