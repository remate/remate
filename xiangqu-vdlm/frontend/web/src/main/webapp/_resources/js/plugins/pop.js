    /**
     * obj==>浮层内容
     * layer==>蒙层
     * closebtn==>浮层内容关闭按钮
     * eventbtn==>触发按钮
     * 调用：layer('js-body','js-layer','js-close','j-address')
     */
    function layer(config){
        var obj = document.getElementById(config.obj);
        var layer = document.getElementById(config.objlayer);
        var objbtn = document.getElementById(config.eventbth);
        layer.style.display='none';
        obj.style.display='none';
        objbtn.onclick=function(){
            layer.style.display='block';
            obj.style.display='block';

        }
    }