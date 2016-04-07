define(['jquery', 'base/utils'], function(jquery, utils) {
    $('#J-all-save').on('click', function() {
        var isOpen = $('.open-desc').is(':checked');
        utils.api.setFragment(isOpen, function() {
            /*
            //再保存排序
            if ($('.desc-sort-box tr').length) {
                var idx = [];
                $('.desc-sort-box tr').each(function() {
                    idx.push($(this).attr('data-id'));
                });
                utils.api.setFragmentIdx(idx.join(','), function() {
                    utils.tool.alert('保存成功！');
                }, function() {
                    utils.tool.alert('保存失败！请稍后再试!');
                });
                
            } else {
                utils.tool.alert('保存成功！');
            }
            */
           utils.tool.alert('保存成功！');
        }, function() {
            utils.tool.alert('保存失败！请稍后再试!');
        });
    });
});