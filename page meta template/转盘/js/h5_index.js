(function($){
	var midCongratulation={
		init:function(){
			var self=this;
            self.J_rotateStart = $('.J_rotateStart');
            self.J_rotateTc = $('.J_rotateTc');
            self.isAble = self.J_rotateStart.attr('data-isable');
            self.actId = '';
			self.onlyOneClick = true;
            self.eventListener();
            self.setIsAble();
		},
        eventListener:function(){
            var self=this;
            //抽奖
            self.J_rotateStart.rotate({
                bind:{
                    click: function(){
                        //if($(this).attr('data-isable') != 1){
                        //    alert('还没有开始，敬请期待！');
                        //    return false;
                        //}
                        //if(!self.onlyOneClick){
							//return false;
                        //}
                        //var time = [0,1];
                        //time = time[Math.floor(Math.random()*time.length)];
                        //if(time==0){
                        //    //self.timeOut(); //网络超时
                        //    time = 1;
                        //}
                        //if(time==1){


                            //var st = self.getLuckyData();
                            //
                            //if(st){
                            //    data = [];
                            //    data.push(st);
                            //}

                            //data = data[Math.floor(Math.random()*data.length)];
                            //var datakey = data[0].index + 1;
                        var datakey = 3;
                            if(datakey==1){
                                self.rotateFunc(1,0);
                            }
                            if(datakey==2){
                                self.rotateFunc(2,60);
                            }
                            if(datakey==3){
                                self.rotateFunc(3,120);
                            }
                            if(datakey==4){
                                self.rotateFunc(3,180);
                            }
                            if(datakey==5){
                                self.rotateFunc(3,240);
                            }
                            if(datakey==6){
                                self.rotateFunc(3,300);
                            }

                        //}
                    }
                }

            });
            $('.close-window').on('click',function(){
                $('.alert-box').show(200) ;
            });
            $('.J_alertOk').on('click',function(){
                $('.alert-box').hide(200);
                $('.J_rotateTc').css('display','none');
            })

        },
        setIsAble:function(){
            var self = this;
            if(self.isAble != 1){
                var isableImg = 'img/rotate-static-isable.png';
                self.J_rotateStart.find('img').attr('src',isableImg);
            }
        },
        timeOut:function(){
            //超时函数
            var self = this;
            self.J_rotateStart.rotate({
                angle:0,
                duration: 10000,
                animateTo: 2160, //这里是设置请求超时后返回的角度，所以应该还是回到最原始的位置，2160是因为我要让它转6圈，就是360*6得来的
                callback:function(){
                    alert('网络超时')
                }
            })
        },
        rotateFunc:function(awards,angle){
            var self = this;
            //awards:奖项，angle:奖项对应的角度

            self.J_rotateStart.stopRotate();
            self.J_rotateStart.rotate({
                angle:0,
                duration: 5000,
                animateTo: angle+1440, //angle是图片上各奖项对应的角度，1440是我要让指针旋转4圈。
                callback:function(){
                    self.J_rotateTc.show();
					self.onlyOneClick = true;
                }
            })
        },
        getLuckyData:function(){
            var self = this;
            //var url = '/assets/activity/xiangqu_Lottery/testData/luckyData.json';
            var url = '/activity/doRaffle';
            var _data = {
                key: self.J_rotateStart.attr('data-key'),
                typeId:8,
                activityId:17
            };
            var _rs ;
			self.onlyOneClick = false;
            $.ajax({
                type:'post',
                url:url,
                data:_data,
                async:false,
                dataType:'json',
                success:function(rs){
                    if(rs.code == 200){
                        _rs = rs.data;
                        self.actId = _rs.actorId;
                    }else if(rs.code == 700){
						self.onlyOneClick = true;
                        alert(rs.msg);
                        var ua = window.navigator.userAgent;
                        if(ua.indexOf('xiangqu') > -1){
                            window.location.reload(true);
                            return false;
                        }else{
                            window.location.href = "http://www.xiangqu.com/list.html";
                        }

                    }else{
                        alert('未知错误！');
                        return false;
                    }

                }
            });
            return _rs;
        }
};
	$(function(){
		midCongratulation.init();
	})
})(jQuery);
