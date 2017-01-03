new function () {
    var _self = this;
    _self.width = 640;//设置默认最大宽度
    _self.fontSize = 100;//默认字体大小
    _self.widthProportion = function(){var p = (document.body&&document.body.clientWidth||document.getElementsByTagName("html")[0].offsetWidth)/_self.width;return p>1?1:p<0.5?0.5:p;};
    _self.changePage = function(){
        document.getElementsByTagName("html")[0].setAttribute("style","font-size:"+(document.documentElement.clientWidth / 10)+"px !important");
    }
    _self.changePage();
    window.addEventListener('resize',function(){_self.changePage();},false);
};

//跳转登录页面
function jumpLogin(data){
	var jumptime = 0;
	if(data!=''){
		jumptime = 4;
	}
    var time = setInterval(function(){
        if(jumptime <= 1){
            window.location.href="/bank/login.html";
            clearInterval(time);
        }else{
            jumptime = jumptime-1;
            errorMsg(data+jumptime+"s");
        }
    },1000);
}