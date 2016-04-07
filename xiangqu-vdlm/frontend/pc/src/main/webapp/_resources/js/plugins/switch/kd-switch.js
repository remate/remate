define(['plugins/switch/bootstrap-switch.min', 'css!plugins/switch/bootstrap-switch.css'], function(switchjs, css) {
    /**
     * [switchs 开店switch模块]
     * @return {[null]}      [null]
     */
    $('.am-switch').bootstrapSwitch({
        onText: '开启',
        offText: '关闭',
        onSwitchChange: function(){
            $(this).trigger('change');
        }
    });
    return null;
});