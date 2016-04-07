define(['base/taobao-move/goStep', 'jquery', 'base/utils','amaze'], function(step, jquery, utils,amaze) {
    step.goStep1();
    $('.move-step1 .move-next').on('click', function() {
        step.goStep2();
    });
    utils.api.move(function(data) {
        var isMoving = false;
        if (data.data.rnd) {
            //如果有验证码传过来
            $('#code').val(data.data.rnd);
        };
        if (data.data.shopUrl && isMoving) { //搬家中
            step.goStep3(1);
            return false;
        }
        if (data.data.shopUrl) { //再次搬家
            step.goStep3();
            moveAgain(data.data.shopUrl);
        }
        isMoving = true;
    });

    function moveAgain(str) {
        $(".move-step3-done .move-next").on("click", function() {
            $('#J-move-pop').modal({
                onConfirm: function() {
                    var self = this;
                    var type = $('[name="move-type"]:checked').val();
                    utils.api.moveAgain({
                        shopUrl: str,
                        option: type
                    }, function(data) {
                        self.close();
                        step.goStep3(1);
                        if (data.data.statusCode!=200) {
                        	$('.am-text-center').text(data.data.msg);
                        }
                    });
                },
                onCancel: function() {
                    this.close();
                }
            });
            
        });
    }
    return null;
});