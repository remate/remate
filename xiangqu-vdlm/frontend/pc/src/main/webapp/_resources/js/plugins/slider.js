define(['jquery'], function() {
    function Slider() {}

    Slider.prototype.init = function($el, options) {
        this.settings = {
            step: 3000,
            auto: true,
            width: 200
        };
        $.extend(this.settings, options);
        this.el = $el;
        this.len = $el.find('.sliderBox img').length;
        this.index = 0;
        $el.find('.sliderIndex em:eq(0)').addClass('active');
        $el.find('.sliderBox').append($el.find('.sliderBox').html());
        $el.find('.sliderBox').css('left', 0);
        $el.find('.sliderBox').width(this.settings.width * $el.find('.sliderBox img').length);
        if (this.settings.auto && $el.find('.sliderBox img').length > 2 ) {
            this.auto();
        }
        this.clickIndex();
    }
    Slider.prototype.clean = function(){
        var self = this;
        this.el.on('mouseenter',function(){
            self.destroy();
        }).on('mouseleave',function(){
            if (self.settings.auto && self.el.find('.sliderBox img').length > 2) {
                self.auto();
            }
        });
    }
    Slider.prototype.auto = function() {
        var self = this;
        this.timer = setInterval(function() {
            self.index ++;
            self.index  %= self.len * 2;
            self.goIndex();
        }, this.settings.step);
    }
    Slider.prototype.destroy = function() {
        clearInterval(this.timer);
        this.timer = null;
    }
    Slider.prototype.goIndex = function() {
        var self = this;
        if (this.index  == 0) {
            //如果是第一个
            this.el.find('.sliderBox').css('left', -(this.len - 1) * this.settings.width);
            this.index  = this.len;
        }
        console.log(-self.index  * self.settings.width);
        self.el.find('.sliderBox').stop().animate({
            left: -self.index  * self.settings.width
        }, 200, '', function() {
            self.el.find('.sliderIndex em').removeClass('active').filter(':eq(' + self.index  % self.len + ')')
                .addClass('active');
        });
    }
    Slider.prototype.clickIndex = function(){
        var self = this;
        this.el.find('.sliderIndex em').on('click',function(){
            self.index = $(this).index();
            self.goIndex();
        });
    }
    return Slider;
});