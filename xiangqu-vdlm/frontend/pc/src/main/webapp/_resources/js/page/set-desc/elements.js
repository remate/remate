define(['jquery'], function(jquery) {
    var $elements = {
        pop: $('#J-desc-pop'),
        popName: $('#J-desc-pop').find('.desc-pop-name'),
        popDesc: $('#J-desc-pop').find('.desc-pop-info'),
        popShowModel: $('#J-desc-pop').find('[name="desc-pop-position"]'),
        popImgBox: $('#J-desc-pop').find('.desc-pop-imgs-box'),
        popImgAdd: $('#J-desc-pop').find('.desc-pop-add')
    }
    return $elements;
});