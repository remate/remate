define(['jquery'], function(jquery){
    //改价
    $(document).on('click','#skudd',function(){
        $('#skuEdit').modal({
            relatedTarget: this,
            width: '688',
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