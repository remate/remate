var video = document.getElementById('myVideo'); 
$('.btnPlay').on('click', function() {  
	if(video.paused) {  
		video.play(); 	 
	}  
	else {  
		video.pause();  
	}  
	return false;  
});

　//获得视频的时间总长度  
video.onloadedmetadata=function(){
	$('.duration').text(video.duration); 
};

//// 更新当前HTML5视频播放时间  
//video.ontimeupdate=function() {  
//	$('.current').text(video.currentTime);  
//};


//播放进度条显示
video.ontimeupdate=function() {  
	$('.current').text(video.currentTime);  
var currentPos = video.currentTime; //获得当前播放时间  
var maxduration = video.duration; //获得影片播放时间  
var percentage = 100 * currentPos / maxduration; //百分比  
$('.timeBar').css('width', percentage+'%');  
};


//进度条可拖动
var timeDrag = false; 
var prossessWidth=$('.progressBar').width();
$('.progressBar').mousedown(function(e) {  
	timeDrag = true;  
	updatebar(e.pageX);  
});  
$(document).mouseup(function(e) {  
	if(timeDrag) {  
		timeDrag = false; //停止拖动，设置timeDrag为false  
		updatebar(e.pageX);  
	}  
});  
$(document).mousemove(function(e) {  
	if(timeDrag) {  
		updatebar(e.pageX);  
	}  
});  
var updatebar = function(x) {  
	var progress = $('.progressBar');  
	var maxduration = video.duration;  
	var position = x - progress.offset().left;  
//	var percentage = 100 * position;  
	var percentage = parseFloat(position/prossessWidth)*100;
	//检查拖动进度条的范围是否合法  
	if(percentage > 100) {  
		percentage = 100;  
	}  
	if(percentage < 0) {  
		percentage = 0;  
	}  
	$('.timeBar').css('width', percentage+'%');  
	video.currentTime = maxduration * percentage / 100;  
};

