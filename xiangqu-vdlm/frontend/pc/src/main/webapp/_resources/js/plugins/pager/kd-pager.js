

define(['jquery', 'plugins/pager/jquery.pagination.min', 'css!plugins/pager/pagination', 'base/utils'], function(jquery, pagerjs, pagercss, utils) {

    function kdPager() {}

    /**
     * [init description]
     * @param  {[type]} $el     [description]
     * @param  {[type]} options [description]
     * @return {[type]}         [description]
     * ?page=2  link_to:?page=__id__
     */
    kdPager.prototype.init = function($el, options) {
        var self = this;
        this.defaults = {
            num_edge_entries: 3, //边缘页数
            num_display_entries: 4, //主体页数
            callback: function(index) {},
            items_per_page: 8, //每页显示1项,
            prev_text: '&#xe61d;',
            next_text: '&#xe61c;',
            link_to: 'javascript:;',
            goto_page: true,
            beyondCallback: function() {
                utils.tool.alert('页码不正确');
            }
        }
        $.extend(self.defaults, options);
        $el.pagination(self.defaults.count,self.defaults);
    }
    return kdPager;
});

