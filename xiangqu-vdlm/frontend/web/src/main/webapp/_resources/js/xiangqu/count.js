$(document).ready( function() {
    $('.numInc,.numDec').click(function() {
        var self = $(this), numed = self
            .parent().children('.numed'), delta = self
            .hasClass('numInc') ? 1 : -1, edge = self
            .attr('edge'), oldVal = parseInt(numed
            .val()), newVal = oldVal
            + delta;
        if (!!edge
            && (delta > 0 && newVal > edge || delta < 0
            && newVal < edge))
            newVal = edge;
        if (oldVal != newVal) {
            numed.val(newVal).text(newVal)
                .trigger('change');
        }
        return false;
    });
    $('#backButton').click(function() {
        history.back();
        return false;
    });

    var obj= $(".footer-bottom");
    var objs= obj.siblings();


    if( typeof(obj) !== "undefined"){

        $(objs.get(objs.length-1)).addClass('pb90');
    }


});