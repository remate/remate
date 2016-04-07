function layer(obj,layer,closebtn,eventbth){
    var obj = document.getElementById('js-body');
    var btn = document.getElementById('js-close');
    var layer = document.getElementById('js-layer');
    var objbtn = document.getElementById('j-address');
    layer.style.display='none';
    obj.style.display='none';
    objbtn.onclick=function(){
        layer.style.display='block';
        obj.style.display='block';

    }
    btn.onclick=function(){
        layer.style.display='none';
        obj.style.display='none';
        body.className = '';

    }
}