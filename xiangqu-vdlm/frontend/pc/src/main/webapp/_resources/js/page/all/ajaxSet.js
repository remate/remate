define(['jquery', 'plugins/browser'], function(jquery, browser) {
    if (browser.ie & browser.ie <= 8) {
    } else {
        $(document).ajaxStart(function() {
            $('.ajax-dimmer').show().addClass('active');
            $('#J-kd-modal-loading').show().addClass('active');
            $("#J-kd-modal-loading .loading").stop().css('width', '1%').animate({
                'width': '20%'
            }, function() {
                $(this).animate({
                    'width': '80%'
                }, 10000);
            });
        }).ajaxStop(function() {
            $('.ajax-dimmer').removeClass('active').hide();
            $('#J-kd-modal-loading').removeClass('active').hide();
        });
    }
    return null;
});