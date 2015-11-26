(function($) {
    $.fn.flipster = function(options) {
        var isMethodCall = typeof options === 'string' ? true : false;
        if (isMethodCall) {
            var method = options;
            var args = Array.prototype.slice.call(arguments, 1);
        } else {
            var defaults = {
                itemContainer:    'ul',
                itemSelector:     'li',
                style:            'coverflow', // Switch between 'coverflow' or 'carousel' display styles
                start:            'center',
                enableKeyboard:   true,
                enableMousewheel: true,
                enableTouch:      true,
                onItemSwitch:     $.noop,
                disableRotation:  false,
                prevText:         'Prev',
                nextText:         'Next'
            };
            var settings = $.extend({}, defaults, options);
        }
        return this.each(function(){
            var _flipster = $(this);
            var methods;
            if (isMethodCall) {
                methods = _flipster.data('methods');
                return methods[method].apply(this, args);
            }
            var _flipItemsOuter;
            var _flipItems;
            var _current = 0;
            var startTouchX,endTouchX,totalTouchX;
            methods = {
                jump: jump
            };
            _flipster.data('methods', methods);
            function resize() {
                var deviceWidth=$(window).width();
                $('img').css("max-width",deviceWidth*0.7);
                _flipItemsOuter.width(deviceWidth*0.7);
                _flipItemsOuter.height(deviceWidth*0.5);
            }
            _flipster.children('.flipto-prev').on("click", function(e) {
                jump("left");
                e.preventDefault();
            });
            _flipster.children('.flipto-next').on("click", function(e) {
                jump("right");
                e.preventDefault();
            });
            function center() {
                var currentItem = $(_flipItems[_current]).addClass("flip-current");
                _flipItems.removeClass("flip-prev flip-next flip-current flip-past flip-future");
                _flipItems.addClass("flip-hidden");
                var nextItem = $(_flipItems[_current+1]),
                    futureItem = $(_flipItems[_current+2]),
                    prevItem = $(_flipItems[_current-1]),
                    pastItem = $(_flipItems[_current-2]);
                if ( _current === 0 ) {
                    prevItem = _flipItems.last();
                    pastItem = prevItem.prev();
                }
                else if ( _current === 1 ) {
                    pastItem = _flipItems.last();
                }
                else if ( _current === _flipItems.length-2 ) {
                    futureItem = _flipItems.first();
                }
                else if ( _current === _flipItems.length-1 ) {
                    nextItem = _flipItems.first();
                    futureItem = $(_flipItems[1]);
                }
                futureItem.removeClass("flip-hidden").addClass("flip-future");
                pastItem.removeClass("flip-hidden").addClass("flip-past");
                nextItem.removeClass("flip-hidden").addClass("flip-next");
                prevItem.removeClass("flip-hidden").addClass("flip-prev");
                currentItem.addClass("flip-current").removeClass("flip-prev flip-next flip-past flip-future flip-hidden");
                //resize();
                settings.onItemSwitch.call(this);
            }

            function jump(to) {
                if ( _flipItems.length > 1 ) {
                    if ( to === "left" ) {
                        if ( _current > 0 ) { _current--; }
                        else { _current = _flipItems.length-1; }
                    }
                    else if ( to === "right" ) {
                        if ( _current < _flipItems.length-1 ) { _current++; }
                        else { _current = 0; }
                    } else if ( typeof to === 'number' ) {
                        _current = to;
                    } else {
                        _current = _flipItems.index(to);
                    }
                    center();
                }
            }

            function init() {
                _flipster.addClass("flipster flipster-active flipster-"+settings.style).css("visibility","hidden");
                _flipItemsOuter = _flipster.find(settings.itemContainer).addClass("flip-items");
                _flipItems = _flipItemsOuter.find(settings.itemSelector).addClass("flip-item flip-hidden").wrapInner("<div class='flip-content' />");
                if (settings.start && _flipItems.length > 1) {
                    if ( settings.start === 'center' ) {
                        if (!_flipItems.length % 2) {
                            _current = _flipItems.length/2 + 1;
                        } else {
                            _current = Math.floor(_flipItems.length/2);
                        }
                    } else {
                        _current = settings.start;
                    }
                }
                resize();
                $('.img').css('visibility','visible');
                _flipster.hide().css("visibility","visible");
                _flipster.show(400,function(){ center(); });
                jump("right");

                $(window).on("resize.flipster", function() {
                    resize();
                    center();
                });
                _flipItems.on("click", function(e) {
                    if ( !$(this).hasClass("flip-current") ) { e.preventDefault(); }
                    jump(_flipItems.index(this));
                });

                var touchStatus=false,scrollStatus;
                $('.flip-items').on('touchstart', function(event) {
                    //event.preventDefault();//阻止其他事件
                    touchStatus=false;
                    var startTouch = event.targetTouches[0];
                    startTouchX = startTouch.pageX;
                    console.log(startTouchX);
                });
                $('.flip-items').on('touchmove', function(event) {
                    event.preventDefault();//阻止其他事件
                    // 如果这个元素的位置内只有一个手指的话
                    if (event.targetTouches.length == 1) {
                        var endTouch = event.targetTouches[0];
                        endTouchX = endTouch.pageX;
                        totalTouchX = endTouchX - startTouchX;
                        if(totalTouchX>=80){
                            touchStatus=true;
                            scrollStatus='left';
                        }
                        if((-totalTouchX)>=80){
                            touchStatus=true;
                            scrollStatus='right';
                        }
                    };
                }, false);
                $('.flip-items').on('touchend', function(event) {
                   if(touchStatus==true){
                       jump(scrollStatus);
                       touchStatus=false;
                   }
                });

                    // 方法二
                // var mouseDown;
                // LR();
                // function LR(){
                //     var start_X;
                //     var end_X;
                //     mouseDown = false;

                //     function eventDown(e){
                //         //e.preventDefault();
                //         mouseDown = true;
                //         if(e.changedTouches){
                //             e = e.changedTouches[e.changedTouches.length-1];
                //         }
                //         start_X = e.pageX;
                //         end_X = e.pageX;
                //     }
                //     function eventUp(e){
                //         //e.preventDefault();
                //         if(end_X - start_X > 50){
                //             jump("left");
                //         }
                //         if(start_X - end_X > 50){
                //             jump("right");
                //         }

                //         mouseDown = false;

                //     }
                //     function eventMove(e){
                //         e.preventDefault();
                //         if(mouseDown) {
                //             if(e.changedTouches){
                //                 e = e.changedTouches[e.changedTouches.length-1];
                //             }
                //             end_X = e.pageX;
                //         }
                //     }
                //     var wrap = document.getElementsByClassName("flip-items")[0];
                //     wrap.addEventListener('touchstart', eventDown);
                //     wrap.addEventListener('touchend', eventUp);
                //     wrap.addEventListener('touchmove', eventMove);
                // }


            }
            if ( !_flipster.hasClass("flipster-active") ) {
                init();
            }
        });
    };
})(Zepto);
