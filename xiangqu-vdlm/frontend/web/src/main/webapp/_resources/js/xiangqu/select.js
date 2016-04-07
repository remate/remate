/**
 * Created by ziyu on 2016/1/23.
 */

$(function(){
    function selectCopy () {
        $('select').css('',function () {
            var thisID = $(this).attr('id');
            var thisVALUE = $(this).val();
            var thisValText = $('#'+thisID+'>option[value='+thisVALUE+']').text();
            var optionLength = $('#'+thisID+'>option').length;
            var addHtml = '<span id="select-'+thisID+'" class="select" data-selectid="'+thisID+'"><span>'+thisValText+'</span>';
            var addHtmlEnd = '</span>';
            var addOpt = '<ul>';
            for (i = 1; i < optionLength+1; i++) {
                var thisOptVal = $('#'+thisID+'>option:nth-child('+i+')').val();
                var thisOptText = $('#'+thisID+'>option:nth-child('+i+')').text();
                if (thisOptVal != thisVALUE) {
                    addOpt+='<li data-value="'+thisOptVal+'">'+thisOptText+'</li>';
                } else {
                    addOpt+='<li data-value="'+thisOptVal+'" class="select-cat">'+thisOptText+'</li>';
                };
            };
            addOpt += '</ul>';
            $(this).after(addHtml+addOpt+addHtmlEnd);
            $(this).hide();
        });
    };
    if((!navigator.userAgent.match(/iPhone/i)) || (!navigator.userAgent.match(/iPod/i))){
        selectCopy();
    };

    $(document).on('click','.select>span',function(event) {
        var parentID = $(this).parent();
        var thisID = parentID.attr('id');
        var thisSelectID = $('#'+thisID).data('selectid');
        $('#'+thisID+'>ul').fadeIn('fast');
    });
    $(document).on('click','.select>ul>li',function(event) {
        var parentUL = $(this).parent();
        var parentID = $(parentUL).parent();
        var thisID = parentID.attr('id');
        // var thisNO = $(this).index();
        var thisTEXT = $(this).text();
        var thisVALUE = $(this).data('value');
        var thisSelectID = $('#'+thisID).data('selectid');
        $('#'+thisID+'>ul>li').removeClass('select-cat');
        $(this).addClass('select-cat');
        $('#'+thisSelectID).val(thisVALUE);
        $('#'+thisID+'>span').text(thisTEXT);
    });
    $('html').click(function(event) {
        $('.select>ul').fadeOut("fast");
    });
});