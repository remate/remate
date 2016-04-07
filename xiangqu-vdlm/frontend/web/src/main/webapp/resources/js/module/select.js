//define(function(require){
    var $ = require('jquery');
    //var $ = require('jquery/1.11.1/jquery.js');
        //模拟多选框
        var moniCheckbox = $(".moni-checkbox");
        moniCheckbox.bind('click',function() {
            if (moniCheckbox.hasClass("moni-checkbox-active")) {
                moniCheckbox.removeClass("moni-checkbox-active");
            } else {
                moniCheckbox.addClass("moni-checkbox-active");
            }
        })
//})
