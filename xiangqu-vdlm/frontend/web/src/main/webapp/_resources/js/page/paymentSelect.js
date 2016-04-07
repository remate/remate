iswap = function(){
	
	var agentcheck=navigator.userAgent.toLowerCase();  
	var chesys=true; 
	var isWAP=false;
	var keywords = ["mobile", "android",
					"symbianos", "iphone","windows phone",
					"mqqbrowser", "nokia", "samsung", "midp-2",
					"untrusted/1.0", "windows ce", "blackberry","ucweb",
					"brew", "j2me", "yulong", "coolpad", "tianyu", "ty-",
					"k-touch", "haier", "dopod", "lenovo", "huaqin", "aigo-",
					"ctc/1.0", "ctc/2.0", "cmcc", "daxian", "mot-",
					"sonyericsson", "gionee", "htc", "zte", "huawei", "webos",
					"gobrowser", "iemobile", "wap2.0","wapi"];
	//排除 windows,苹果等桌面系统 和ipad 、iPod  
	var rekeywords=["Windows 98", "Windows ME","Windows 2000","Windows XP","Windows NT","Ubuntu","ipad","ipod","macintosh"];
	
	if (agentcheck!=null){
		for (var i = 0; i < rekeywords.length; i++) {
			if ( agentcheck.indexOf(rekeywords[i].toLowerCase())>-1){
			chesys=false;
			}
		}
	}

	if (chesys){
		for (var i = 0; i < keywords.length; i++) {
			if ( agentcheck.indexOf(keywords[i].toLowerCase())>-1) {
				isWAP = true;
				break;
			}
		}
	}
	return isWAP;
};