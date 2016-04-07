define(['jquery', 'amaze', 'base/utils'], function(jquery, amaze, utils) {
    return {
        save: function() {
            var $postage = $('#post'),
                $freeShippingPrice = $('#money'),
                $customizedPostages = $('.post-table tr'),
                data = {},
                cusData = {},
                index = 0;
            $customizedPostages.each(function(i, el) {
                cusData['customizedPostage[' + index + '].poatage'] = $(el).attr('data-post');
                var j = 0;
                $(el).find('.post-area span').each(function() {
                    cusData['customizedPostage[' + index + '].areas[' + j + '].id'] = $(this).attr('data-id');
                    cusData['customizedPostage[' + index + '].areas[' + j + '].name'] = $.trim($(this).text());
                    j++;
                });
                index++;
            });

            var freeShipVal = (Number($('#money').val()).toFixed(2) == '0.00') ? '-1' : Number($('#money').val()).toFixed(2),
                postageVal = parseInt($postage.val()*100)/100;
            data = {
                postageStatus: 1,
                freeShippingPrice: freeShipVal,
                postage: postageVal
            }

            if ($customizedPostages.length) {
                $.extend(data, cusData);
            }
            utils.api.updatePostAge(data,function(callData){
                if (callData.errorCode == 200) {
                    utils.tool.alert('保存成功!');
                }else{
                    utils.tool.alert(callData.moreInfo);
                }                
            });
        }
    };
});

