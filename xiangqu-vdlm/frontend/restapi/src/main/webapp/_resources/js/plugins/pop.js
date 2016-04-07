$.pop=function(btnObj,bodyObj,darkLayerObj,closeBtn){
    btnObj.click(function(ev){
        //蒙版层
        darkLayerObj.removeClass("hiddle");
        //弹出层
        bodyObj.removeClass("hiddle").css("margin-top", ("-"+ bodyObj.height()/2) *1 +'px');

    })
    //关闭按钮

   closeBtn.click(function(){
        darkLayerObj.addClass('hiddle');
        bodyObj.addClass('hiddle');
    })



}
