define(['jquery'], function(jquery) {
    //改价
    $(document).on('click','.am-rateEdit',function(){
        $('.J_rateEditPop').modal({
            relatedTarget: this,
            width: '320', 
            onConfirm: function(options) {
                this.close();
            },
            onCancel: function() {
                this.close();
            }
        });
    });
    return null;
});