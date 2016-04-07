/**
 * af.popup - a popup/alert library for html5 mobile apps
 * copyright Indiepath 2011 - Tim Fisher
 * Modifications/enhancements by Intel for App Framework
 *
 */
 
/* EXAMPLE
 $.query("body").popup({
        title:"Alert! Alert!",
        message:"This is a test of the emergency alert system!! Don't PANIC!",
        cancelText:"Cancel me",
        cancelCallback: function(){console.log("cancelled");},
        doneText:"I'm done!",
        doneCallback: function(){console.log("Done for!");},
        cancelOnly:false,
        doneClass:'button',
        cancelClass:'button',
        onShow:function(){console.log("showing popup");}
        autoCloseDone:true, //default is true will close the popup when done is clicked.
        suppressTitle:false //Do not show the title if set to true
  });

  You can programatically trigger a close by dispatching a "close" event to it.

 $.query("body").popup({title:'Alert',id:'myTestPopup'});
 $("#myTestPopup").trigger("close");

 */
/* global af */
(function ($) {
    "use strict";
    $.fn.popup = function (opts) {
        return new popup(this[0], opts);
    };
    var queue = [];
    function iswap(){
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
    
    var popup = (function () {
        var popup = function (containerEl, opts) {

            if (typeof containerEl === "string" || containerEl instanceof String) {
                this.container = document.getElementById(containerEl);
            } else {
                this.container = containerEl;
            }
            if (!this.container) {
                window.alert("Error finding container for popup " + containerEl);
                return;
            }

            try {
            	
                if (typeof (opts) === "string" || typeof (opts) === "number")
                    opts = {
                        message: opts,
                        cancelOnly: "true",
                        cancelText: "OK"
                    };
                this.id = opts.id = opts.id || $.uuid(); //opts is passed by reference
                this.addCssClass = opts.addCssClass ? opts.addCssClass : "";
                this.title = opts.suppressTitle ? "" : (opts.title || "Alert");
                this.message = opts.message || "";
                this.cancelText = opts.cancelText || "Cancel";
                this.cancelCallback = opts.cancelCallback || function () {};
                this.cancelClass = opts.cancelClass || "button";
                this.doneText = opts.doneText || "Done";
                this.doneCallback = opts.doneCallback || function () {
                    // no action by default
                };
                this.doneClass = opts.doneClass || "button";
                this.cancelOnly = opts.cancelOnly || false;
                this.onShow = opts.onShow || function () {};
                this.autoCloseDone = opts.autoCloseDone !== undefined ? opts.autoCloseDone : true;

                queue.push(this);
                if (queue.length === 1){
                	this.show();
                }
                    
            } catch (e) {
                console.log("error adding popup " + e);
            }

        };

        popup.prototype = {
            id: null,
            addCssClass: null,
            title: null,
            message: null,
            cancelText: null,
            cancelCallback: null,
            cancelClass: null,
            doneText: null,
            doneCallback: null,
            doneClass: null,
            cancelOnly: false,
            onShow: null,
            autoCloseDone: true,
            supressTitle: false,
            show: function () {
                var self = this;
                var markup = "<div id='" + this.id + "' class='afPopup hidden "+ this.addCssClass + "'>"+
                            "<header>" + this.title + "</header>"+
                            "<div>" + this.message + "</div>"+
                            "<footer>"+
                                 "<a href='javascript:;' class='" + this.cancelClass + "' id='cancel'>" + this.cancelText + "</a>"+
                                 "<a href='javascript:;' class='" + this.doneClass + "' id='action'>" + this.doneText + "</a>"+
                                 "<div style='clear:both'></div>"+
                            "</footer>"+
                            "</div>";

                $(this.container).append($(markup));
                var $el = $.query("#" + this.id);
                $el.bind("close", function () {
                    self.hide();
                });

                if (this.cancelOnly) {
                    $el.find("A#action").hide();
                    $el.find("A#cancel").addClass("center");
                }
                $el.find("A").each(function () {
                    var button = $(this);
                    button.bind("click", function (e) {
                        if (button.attr("id") === "cancel") {
                            self.cancelCallback.call(self.cancelCallback, self);
                            self.hide();
                        } else {
                            self.doneCallback.call(self.doneCallback, self);
                            if (self.autoCloseDone)
                                self.hide();
                        }
                        e.preventDefault();
                    });
                });
                self.positionPopup();
                $.blockUI(0.5);

                $el.bind("orientationchange", function () {
                    self.positionPopup();
                });

                //force header/footer showing to fix CSS style bugs
                $el.find("header").show();
                $el.find("footer").show();
                setTimeout(function(){
                    $el.removeClass("hidden");
                    self.onShow(self);
                },50);
            },

            hide: function () {
                var self = this;
                $.query("#" + self.id).addClass("hidden");
                $.unblockUI();
                if(!$.os.ie&&!$.os.android){
                    setTimeout(function () {
                        self.remove();
                    }, 250);
                }
                else
                    self.remove();
            },

            remove: function () {
                var self = this;
                var $el = $.query("#" + self.id);
                $el.unbind("close");
                $el.find("BUTTON#action").unbind("click");
                $el.find("BUTTON#cancel").unbind("click");
                $el.unbind("orientationchange").remove();
                queue.splice(0, 1);
                if (queue.length > 0)
                    queue[0].show();
            },

            positionPopup: function () {
                var popup = $.query("#" + this.id);
                if(iswap()){
                	popup.css("top", (window.innerHeight / 2.5 - popup[0].clientHeight / 2)  + "px");
                }else{
                	popup.css("top", ((window.innerHeight / 2.5) + window.pageYOffset) - (popup[0].clientHeight / 2) + "px");
                }
                popup.css("left", (window.innerWidth / 2) - (popup[0].clientWidth / 2) + "px");
            }
        };

        return popup;
    })();
    var uiBlocked = false;
    $.blockUI = function (opacity) {
        if (uiBlocked)
            return;
        opacity = opacity ? " style='opacity:" + opacity + ";'" : "";
        $.query("BODY").prepend($("<div id='mask'" + opacity + "></div>"));
        $.query("BODY DIV#mask").bind("touchstart", function (e) {
            e.preventDefault();
        });
        $.query("BODY DIV#mask").bind("touchmove", function (e) {
            e.preventDefault();
        });
        uiBlocked = true;
    };

    $.unblockUI = function () {
        uiBlocked = false;
        $.query("BODY DIV#mask").unbind("touchstart");
        $.query("BODY DIV#mask").unbind("touchmove");
        $("BODY DIV#mask").remove();
    };

})(af);
