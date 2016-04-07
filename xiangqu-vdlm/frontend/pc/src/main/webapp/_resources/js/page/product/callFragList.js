define(['jquery', 'base/utils', 'base/product/elements', 'doT'], function(jquery, utils, eles, doT) {
    var tplDescList = $('.tpl-desc-List').html(),
        doTDescList = doT.template(tplDescList);
    var tplDesc = $('.tpl-desc').html(),
        doTtplDesc = doT.template(tplDesc);
    eles.callDescList.on('click', function() {
        utils.api.getFragList(function(data) {
            $('.descList-pop .desc-table tbody').html(doTDescList(data));
            $('.descList-pop .desc-table .kd-checkbox').initCheck();
            $('.desc-tabel-show tr').each(function() {
                var id = $(this).attr('data-id');
                $('.descList-pop .desc-table tbody').find('tr[data-id="' + id + '"]').find('.kd-checkbox').iCheck('check');
            });
            eles.descList.modal({
                onConfirm: function() {
                    eles.proDescBox.empty();
                    $('.descList-pop [name="fragmentId"]').each(function() {
                        var row = $(this).parents('tr');
                        if ($(this).is(':checked') && !$('.desc-tabel-show tr[data-id="' + row.attr('data-id') + '"]').length) {
                            renderDesc(row);
                        }
                    });
                    this.close();
                },
                onCancel: function() {
                    this.close();
                }
            });
        }, function() {
            utils.tool.alert('获取段落数据出错!');
            return;
        });
    });

    function renderDesc(row) {
        var aImgsModel = [],
            aImgs = [];
        $.each(row.find('.imgsData span'), function(i, el) {
            aImgs.push($(el).attr('data-img'));
            aImgsModel.push({
                imgUrl: $(el).attr('data-imgurl'),
                img: $(el).attr('data-img')
            });
        });
        var data = {
            id: row.attr('data-id'),
            name: $.trim(row.find('.desc-name').text()),
            description: $.trim(row.find('.desc-detail').text()),
            showModel: row.attr('data-showmodel') == 'true' ? 1 : 0,
            imgList: aImgsModel,
            imgs: aImgs.join(',')
        };
        var str = doTtplDesc(data);
        eles.proDescBox.append(str);
    }
});