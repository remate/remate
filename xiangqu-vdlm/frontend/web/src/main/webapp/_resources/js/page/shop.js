$(document).ready(function() {
    $('#moveAgbtn').bind('click', function() {
        var href = $('#moveAgbtn').attr('href');
        $.getJSON(href, function(data) {
            if (data.data) {
                if (data.data.statusCode != 200) {
                	alert(data.data.msg);
//                    if (data.data.statusCode == 601) {
//                        alert('店铺不存在');
//                    } else if (data.data.statusCode==501 || data.data.statusCode==502) {
//                        alert('网络错误,搬家失败');
//                    } else {
//                        alert('未知错误,搬家失败');
//                    }
                } else {
                    location.href ='/shop/step4';
                }
            }
        });

        return false;
    });

});
