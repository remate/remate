define(['jquery', 'icheck'], function(jquery,icheck) {
    $.fn.extend({
        //initCheck maat
        initCheck: function() {
            this.each(function() {
                $(this).iCheck({
                    checkboxClass: 'icheckbox',
                    radioClass: 'iradiobox'
                });
                $('.icheck').on('ifChecked', function() {
                    var _this = $(this);
                    var cGroup = _this.attr('name');
                    var len = $('.icheckbox input[name="' + cGroup + '"]').length;
                    var lenChecked = $('.icheckbox input[name="' + cGroup + '"]:checked').length;
                    //console.log(len+';'+lenChecked);
                    if (len === lenChecked) {
                        $('input.checkAll[group=' + cGroup + ']').iCheck('check');
                    }
                }).on('ifUnchecked', function() {
                    var _this = $(this);
                    var cGroup = _this.attr('name');
                    $('input.checkAll[group=' + cGroup + ']').iCheck('uncheck');
                });
            });
            //全选
            $('input.checkAll').on('ifChecked', function() {
                $('input[name=' + $(this).attr('group') + ']').iCheck('check');
            }).on('ifClicked', function() {
                if ($('input[name=' + $(this).attr('group') + ']:checked')) {
                    $('input[name=' + $(this).attr('group') + ']').iCheck('uncheck');
                };
            });
            return this;
        }
    });

    $('.kd-checkbox,.kd-radio').initCheck();
});