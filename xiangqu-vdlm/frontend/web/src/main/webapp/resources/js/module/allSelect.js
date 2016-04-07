//var $=require('jquery');
//define(function(require) {
    var $ = require('jquery');
    //var $ = require('jquery/1.11.1/jquery.js');
    var moniCheckbox = $(".moni-checkbox");
    var allSelect = $(".allSelect");
    allSelect.unbind('click');
    moniCheckbox.unbind('click');
    //模拟多选框
    function moniSelect(obj, theadSel) {
        if ($(obj).hasClass("moni-checkbox-active")) {
            $(obj).removeClass("moni-checkbox-active");
            //店铺内产品列表非全选中情况，列表头部设置非选
            $(theadSel).removeClass("moni-checkbox-active");
            $(".allSelect").find(".moni-checkbox").removeClass("moni-checkbox-active");
        } else {
            $(obj).addClass("moni-checkbox-active");
        }
    }
    //店铺产品列表是否都选择，初始店铺多选框
    function shopAll(obj, tbodySel) {
        var activeSelect = $(obj).parents('.table-able').find('tbody').find(".moni-checkbox-active");
        if (tbodySel.length === activeSelect.length) {
            return true;
        } else {
            return false;
        }
    }

    moniCheckbox.bind('click', function () {
        var tbodySel = $(this).parents('.table-able').find('tbody').find(".moni-checkbox");
        var theadSel = $(this).parents('.table-able').find('thead').find('.moni-checkbox');
        //全选
        if ($(this).parent().hasClass("allSelect")) {
            if ($(this).hasClass("moni-checkbox-active")) {
                $(moniCheckbox).removeClass("moni-checkbox-active");
            } else {
                $(moniCheckbox).addClass("moni-checkbox-active");
            }//店铺列表选择
        } else if ($(this).parent().hasClass("shopSelect")) {
            if ($(this).hasClass("moni-checkbox-active")) {
                moniSelect(this, theadSel);
                tbodySel.removeClass("moni-checkbox-active");
            } else {
                moniSelect(this, theadSel);
                tbodySel.addClass("moni-checkbox-active");
            }
        } else {//一般单个选择
            moniSelect(this, theadSel);
        }

        //店铺内产品列表全选中情况，列表头部也选中
        if (shopAll(this, tbodySel)) {
            theadSel.addClass("moni-checkbox-active");
        }
        // 店铺列表都没选的情况，列表头部设置非选
        if (!tbodySel.hasClass("moni-checkbox-active")) {
            theadSel.removeClass("moni-checkbox-active");
        }
    })
//});