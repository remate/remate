//获取平台类目列表
define(['jquery', 'base/utils', 'chosen'], function(jquery, utils, chosen) {

    function Category() {

    }

    Category.prototype.init = function(id) {
        var _this = this,
            ids = [];
        if (id) {
            //如果是有多级别
            ids = id.split('|');
            ids.unshift('');
        } else {
            ids = [''];
        }
        var len = ids.length;

        $.each(ids, function(i, _id) {
            //数组的下一个是他的当前select的选中值
            var _default = '';
            if (i < len - 1) {
                _default = ids[i + 1];
            }
            //编辑的时候,包括默认为空的情况 ,
            if (i < len || len == 1) {
                $('.platform-category').append('<b></b>');
                _this.getData(_id, _default, function(_str, data) {
                    $('.platform-category').find('b').eq(i).replaceWith(_str);
                    $('.platform-select').chosen();
                });
            }
        });

        this.change();
    }
    /**
     * 获取数据拼接字符串，顺便选中默认值（如果有的话）
     * @param  {[type]}   id       [id]
     * @param  {[type]}   _default [默认值]
     * @param  {Function} callback [回调函数] 传递两个参数（拼接完成的字符串，拿到的数据）
     * @return {[null]}            [null]
     */
    Category.prototype.getData = function(id, _default, callback) {
        utils.api.getCategoryList({
            id: id
        }, function(data) {
            if (!data.data.length) {
                callback && callback('', data);
                return;
            }
            var str = '<select class="am-fl combox platform-select" data-placeholder="请选择"><option value="">请选择</option>';
            $.each(data.data, function(i, el) {
                var selected = '';
                if (_default && _default == el.id) {
                    selected = 'selected="selected"';
                }
                str += '<option value="' + el.id + '" ' + selected + '>' + el.name + '</option>';
            });
            str += '</select>';
            callback && callback(str, data);
        });
    }

    /**
     * 注册select改变时的事件
     * @return {[null]} [null]
     */
    Category.prototype.change = function() {
        var _this = this;
        $('body').on('change.category', '.platform-category select', function() {
            var $el = $(this);
            if ($(this).val()) {
                $el.next('.chosen-container').nextAll('*').remove();
                _this.getData($(this).val(), null, function(str, data) {
                    $el.next('.chosen-container').after(str);
                    $('.platform-select').chosen().trigger('chosen:updated.chosen');
                });
            }
        });
    }

    return Category;

});