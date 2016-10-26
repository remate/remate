$(function($){
    var setPage= 0, id = "id", isOver=false,_intPage;
    function li_append(length){
        var _html = '';
        for (var i = 0; i < length; i++) {
            _html += '<li></li>';
        }
        $('ul').append(_html);
    }
    function clickEvent(){
        $('.btn').click(function(){
            product_ajax(_intPage,id);
        })
    }
    function product_ajax(intPage,intId){
        if(!isOver){
            isOver=true;
            $.ajax({
                url: 'test.json',
                type: 'get',
                data: {
                    type:'cost',
                    page: intPage,
                    code:intId,
                },
                dateType: 'json',
                success: function (data) {
                    _intPage=intPage;
                    var dataLength=data.length;
                    if(dataLength<15){
                        li_append(dataLength);
                        isOver=true;
                        $('.btn').hide();
                    }
                    else{
                        _intPage++;
                        console.log(_intPage);
                        li_append(15);
                        isOver=false;
                    }
                }
            })
        }
    }

    product_ajax(setPage,id);
    clickEvent();

});