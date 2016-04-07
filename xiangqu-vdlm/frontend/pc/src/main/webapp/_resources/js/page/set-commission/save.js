define(['jquery', 'base/utils'], function(jquery, utils) {
    var $Proportion = $('#Proportion'),
        $openShopCom = $('.open-shop-com'),
        $openThirdCom = $('.open-third-com');
    //第三方佣金单独保存
    $openThirdCom.on('switchChange.bootstrapSwitch', function() {
        if ($(this).bootstrapSwitch('state')) {
            utils.tool.confirm.call(this, '加入平台后不可退出，是否确认加入', function($el) {
                var $form = $el.parents('tr'),
                    thirdId = $form.attr('data-id'),
                    commissionRate = $form.find('.thirdComm').val();

                if (commissionRate == '' || !/^-?(?:\d+|\d{1,3}(?:,\d{3})+)?(?:\.\d+)?$/.test(commissionRate)) {
                    utils.tool.alert('佣金必须为数字!');
                    $el.bootstrapSwitch('state', false);
                    return;
                }

                if (commissionRate <= 0 || commissionRate >= 50) {
                    utils.tool.alert('佣金必须在0％和50%之间!');
                    $el.bootstrapSwitch('state', false);
                    return;
                }

                var data = {
                    thirdId: thirdId,
                    commissionRate: commissionRate / 100
                };

                utils.api.saveThirdCommission(data, function() {
                    utils.tool.alert('保存成功!');
                    $el.bootstrapSwitch('disabled', true);
                    $('.commissionTitle').append('<span class="commissionsState">后台审核中，暂时无法修改，请耐心等待审核结果</span>');
                }, function() {
                    utils.tool.alert('保存失败!');
                });

            }, function($el) {
                $el.bootstrapSwitch('state', false);
            })
        }
    });
    return {
        save: function() {
            var commisionRate = $.trim($Proportion) == '' ? 0 : ($Proportion.val() / 100).toFixed(4);
            if (!$openShopCom.is(':checked')) {
                commisionRate = 0;
            }
            utils.api.saveShopCommission(commisionRate, function(data) {
                utils.tool.alert('保存成功!');
            }, function(str) {
                utils.tool.alert(str);
            });
        }
    };
});