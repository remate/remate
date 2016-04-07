/**
 * [description]
 * @param  {[type]} ) {               return function(el){    }} [description]
 * @return {[type]}   [description]
 * 如果是编辑 -> 当前邮费选中的城市active,其他城市选中的disabled
 * 如果是新增 -> 所有已有邮费的城市全部为disabled
 */
;
define(['jquery'], function(jquery) {
    return function($el) {
        //先假设全是新增,然后再单独处理编辑
        var aCitys = [],
            aModifyCitys = [],
            $modifyCitys = null, //编辑的邮费城市
            $nowAreas = $('.post-area span'),
            $modalAreas = $('.modal-areas dd span'),
            $modalPost = $('.modal-post');
        //不管是不是编辑，先把邮费那一项目清空，然后再根据是不是编辑渲染
        $modalPost.val('');
        //先把现在外面已经有的邮费设置项目存起来 ， 待会做对比用
        $nowAreas.each(function(i, el) {
            aCitys.push($.trim($(el).text()));
        });
        //先全置为初始状态
        $modalAreas.removeClass('disabled active');
        //遍历模版中的邮费城市，如果是外面有的，就先把它置为不可选
        $modalAreas.each(function(i, el) {
            var city = $.trim($(el).text());
            if (_.indexOf(aCitys, city) > -1) {
                $(el).addClass('disabled');
            }
        });
        //现在判断是不是编辑
        if ($el) {
            console.log( $el.attr('data-post') );
            $modalPost.val($el.attr('data-post'));
            $modifyCitys = $el.find('.post-area span');
            //先把现在外面要编辑的城市项目存起来 ， 待会做对比用
            $modifyCitys.each(function(i, el) {
                aModifyCitys.push($.trim($(el).text()));
            });
            //遍历模版，如果是编辑的邮费项目有的，就置为active
            $modalAreas.each(function(i, el) {
                var city = $.trim($(el).text());
                if (_.indexOf(aModifyCitys, city) > -1) {
                    $(el).removeClass('disabled').addClass('active');
                }
            });
        }
    }
});