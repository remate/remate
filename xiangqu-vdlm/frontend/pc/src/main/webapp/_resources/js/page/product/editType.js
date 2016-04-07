define(['jquery'],function(jquery){
    $('[name="prudct-pubType"]').on('ifChanged',function(){
        if( $(this).filter(':checked').val()  == 'pc'){
            //显示富文本编辑器
            $('.ueditBox').show();
            $('.desc-show-box').hide();
        }else{
            $('.ueditBox').hide();
            $('.desc-show-box').show();
        }
    });
});