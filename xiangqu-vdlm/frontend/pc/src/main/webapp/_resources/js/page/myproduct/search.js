define(['jquery', 'base/myproduct/getData', 'base/myproduct/initParams', 'base/utils'], function(jquery, getData, initParams, utils) {
    //搜索操作，回车和搜索icon都绑定搜索事件，当搜索框的内容为空的时候，默认展现当前combox状态下的全部商品，搜索框有内容的时候，按照“search”状态走搜索流程
    var $orderHandel = $('.am-table-sort em'),
        $selectStatus = $('select[name="productStatus"]');

    var ipt = $('.am-form-search input');

    function searchFunc() {
        var reg = /^\s*$/;
        if (reg.test(ipt.val())) {
            utils.tool.alert('搜索关键字不能为空！');
            return;
        };
        if(!$selectStatus.find('option[value="search"]').length){
            $selectStatus.append('<option value="search">所有商品</option>').val('search').trigger("chosen:updated.chosen"); 
        }
        if (ipt.val().length > 100) {
            ipt.val(ipt.val().slice(0, 100));
        };
        initParams(1, 0, 0, 0, 0, 'search');
        $('[name="pageNum"]').val(1);
        getData();
    }
    ipt.on('keydown', function(ev) {
        var ev = ev || event;
        if (ev.keyCode == 13) {
            searchFunc();
        }
    });
    $('.am-form-search .am-icon-search').on('click', function() {
        searchFunc();
    });
});