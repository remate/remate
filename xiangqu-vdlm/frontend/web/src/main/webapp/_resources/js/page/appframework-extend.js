(function($) {

    if(!window.jQuery){
        window.jQuery = af;
    }

    var rword = /[^, ]+/g ; //切割字符串为一个个小块，以空格或豆号分开它们，结合replace实现字符串的forEach
    
    
    $.fn.isHide = function() {
        return this.width() <= 0 && this.height() <= 0 || this.css('display') == 'none';
    }

    var cssHooks = {};

    "Width,Height".replace(rword, function(name) { //fix 481
        var method = name.toLowerCase(),
                clientProp = "client" + name,
                scrollProp = "scroll" + name,
                offsetProp = "offset" + name;
        cssHooks[method + ":get"] = function(node, which, override) {
            var boxSizing = -4
            if (typeof override === "number") {
                boxSizing = override
            }
            which = name === "Width" ? ["Left", "Right"] : ["Top", "Bottom"]

            $(node).isHide() ? $(node).show() : null;

            var ret = node[offsetProp] // border-box 0
            if (boxSizing === 2) { // margin-box 2
                ret = ret +  getComputedStyle(node,false)["margin" + which[0]]  + getComputedStyle(node,false)["margin" + which[1]];

            }

            $(node).isHide() ? $(node).hide() : null;
            return ret
        }
        
        $.fn["outer" + name] = function(includeMargin) {
            return cssHooks[method + ":get"](this[0], void 0, includeMargin === true ? 2 : 0)
        }
    });

    //扩展extend
    (function($){
        var ext = $.extend;
        $.isWindow = function( obj ) {
            /* jshint eqeqeq: false */
            return obj != null && obj == obj.window;
        };
        

        $.extend = function() {
            var src, copyIsArray, copy, name, options, clone,
                target = arguments[0] || {},
                i = 1,
                length = arguments.length,
                deep = false;

            // Handle a deep copy situation
            if ( typeof target === "boolean" ) {
                deep = target;

                // skip the boolean and the target
                target = arguments[ i ] || {};
                i++;
            }

            // Handle case when target is a string or something (possible in deep copy)
            if ( typeof target !== "object" && !jQuery.isFunction(target) ) {
                target = {};
            }

            // extend jQuery itself if only one argument is passed
            if ( i === length ) {
                target = this;
                i--;
            }

            for ( ; i < length; i++ ) {
                // Only deal with non-null/undefined values
                if ( (options = arguments[ i ]) != null ) {
                    // Extend the base object
                    for ( name in options ) {
                        src = target[ name ];
                        copy = options[ name ];

                        // Prevent never-ending loop
                        if ( target === copy ) {
                            continue;
                        }

                        // Recurse if we're merging plain objects or arrays
                        if ( deep && copy && ((copyIsArray = jQuery.isArray(copy)) ) ) {
                            if ( copyIsArray ) {
                                copyIsArray = false;
                                clone = src && jQuery.isArray(src) ? src : [];

                            } else {
                                clone = src && 1 ? src : {};
                            }

                            // Never move original objects, clone them
                            target[ name ] = jQuery.extend( deep, clone, copy );

                        // Don't bring in undefined values
                        } else if ( copy !== undefined ) {
                            target[ name ] = copy;
                        }
                    }
                }
            }

            // Return the modified object
            return target;
        };
    })(af);

    //扩展data
    (function($){
        var data = $.fn.data;
        $.fn.data = function(){
            if(arguments.length == 2){
                return data.apply(this,arguments);
            }
            var res = data.apply(this,arguments);
            if(res === null){
                res = [];
            }else if(parseInt(res) == res){
                return parseInt(res) ;
            }
        }
    })(af);

    //扩展makeArray
    $.makeArray =  function( arr, results ) {
        var ret = results || [];

        if ( arr != null ) {
            if ( isArraylike( Object(arr) ) ) {
                jQuery.merge( ret,
                    typeof arr === "string" ?
                    [ arr ] : arr
                );
            } else {
                push.call( ret, arr );
            }
        }

        return ret;
    }

    $.merge = function( first, second ) {
        var len = +second.length,
            j = 0,
            i = first.length;

        while ( j < len ) {
            first[ i++ ] = second[ j++ ];
        }

        first.length = i;

        return first;
    }

    function isArraylike( obj ) {
        var length = obj.length,
            type = typeof( obj );

        if ( type === "function" || jQuery.isWindow( obj ) ) {
            return false;
        }

        if ( obj.nodeType === 1 && length ) {
            return true;
        }

        return type === "array" || length === 0 ||
            typeof length === "number" && length > 0 && ( length - 1 ) in obj;
    }
    //扩展is(':visbel')
    (function($){
        var e_is = $.fn.is;
        $.fn.is = function(arg){
            if(typeof arg  == 'string' && arg.trim() == ':visible'){
                return !this.isHide();
            }
            return e_is(arg);
        }
    })(af)
   
})(af)