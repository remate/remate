

define(['jquery'], function() {
    return {
        goStep1 : function(){
            $('.move-step').hide();
            $('.move-step1').show();
            $('.move-bar').removeClass('step-1 step-2 step-3').addClass('step-1');
        },
        goStep2 : function(){
            $('.move-step').hide();
            $('.move-step2').show();
            $('.move-bar').removeClass('step-1 step-2 step-3').addClass('step-2');
        },
        goStep3 : function(isDoing){
            var $el = isDoing ? '.move-step3-doing' : '.move-step3-done';
            $('.move-step').hide();
            $('.move-step3' + $el).show();
            $('.move-bar').removeClass('step-1 step-2 step-3').addClass('step-3');
        },
    }
});

