define(['jquery', 'plugins/qrcode/jquery.qrcode'], function(jquery, qrcode) {
    /**
     * [qrcode description]
     * @param  {[type]} $el     [要渲染的元素]
     * @param  {[type]} options [width , url , height]
     * @return {[type]}         [description]
     */
    function qrcode($el,options) {
        var iRender = '';
        try {
            document.createElement('canvas').getContext('2d');
            var addDiv = document.createElement('div');
            iRender = 'canvas';
        } catch (e) {
            iRender = 'table';
        };
        $el.qrcode({
            render: iRender,
            text: options.url,
            width: options.width,
            height: options.height
        });
    }
    return qrcode;
});