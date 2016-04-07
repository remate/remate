/*global module:false*/

module.exports = function(grunt) {
    // Project configuration.

    var staticBase = '';

    var shellStart = 'node ' + staticBase + 'js/third/r.js -o baseUrl=' + staticBase + 'js ',
        shellEnd = [' paths.amaze=empty: ',
            'paths.jquery=empty: ',
            'paths.doT=empty:',
            'paths.moment=empty: ',
            'paths.pager=empty: ',
            'paths.switchs=empty: ',
            'paths.sortRow=empty: ',
            'paths.underscore=empty: ',
            'paths.validate=empty: ',
            'paths.copy=empty: ',
            'paths.upload=empty: ',
            'paths.qrcode=empty: ',
            'paths.jiathis=empty: ',
            'paths.icheck=empty: ',
            'paths.chosen=empty: ',
            'paths.placeholder=empty: ',
            'paths.my97datepicker=empty: ',
            'paths.kindEditor=empty: ',
            'paths.address=plugins/address',
            'paths.base=page'
        ].join(' '); 

    grunt.initConfig({
        // Metadata.
        // Task configuration.  

        pkg: grunt.file.readJSON('package.json'),

        shell: {
            all: {
                command: shellStart + 'name=base/all out=' + staticBase + 'js/dist/all.js' + shellEnd
            },
            utils: {
                command: shellStart + 'name=base/utils out=' + staticBase + 'js/dist/utils.js' + shellEnd
            },
            setShop: {
                command: shellStart + 'name=base/set-shop out=' + staticBase + 'js/dist/set-shop.js' + shellEnd
            },
            setDesc: {
                command: shellStart + 'name=base/set-desc out=' + staticBase + 'js/dist/set-desc.js' + shellEnd
            },
            setCommission: {
                command: shellStart + 'name=base/set-commission out=' + staticBase + 'js/dist/set-commission.js' + shellEnd
            },
            setPostAge: {
                command: shellStart + 'name=base/set-postage out=' + staticBase + 'js/dist/set-postage.js' + shellEnd
            },
            orderList: {
                command: shellStart + 'name=base/order-list out=' + staticBase + 'js/dist/order-list.js' + shellEnd
            },
            orderDetail: {
                command: shellStart + 'name=base/order-detail out=' + staticBase + 'js/dist/order-detail.js' + shellEnd
            },
            myProduct: {
                command: shellStart + 'name=base/myproduct out=' + staticBase + 'js/dist/myproduct.js' + shellEnd
            },
            product: {
                command: shellStart + 'name=base/product out=' + staticBase + 'js/dist/product.js' + shellEnd
            },
            taobaoMove: {
                command: shellStart + 'name=base/taobao-move out=' + staticBase + 'js/dist/taobao-move.js' + shellEnd
            },
            category: {
                command: shellStart + 'name=base/category out=' + staticBase + 'js/dist/category.js' + shellEnd
            }
        },
        less: {
            zt: {
                files: {
                    'css/login.css': 'less/login.less',
                    'css/admin.css': 'less/admin.less',
                    'css/order-list.css': 'less/order-list.less',
                    'css/order-detail.css': 'less/order-detail.less',
                    'css/product-classify.css': 'less/product-classify.less',
                    'css/set-postage.css': 'less/set-postage.less',
                    'css/set-commission.css': 'less/set-commission.less',
                    'css/set-desc.css': 'less/set-desc.less',
                    'css/set-shop.css': 'less/set-shop.less',
                    'css/taobao-move.css': 'less/taobao-move.less',
                    'css/myproduct.css': 'less/myproduct.less',
                    'css/product.css': 'less/product.less'
                },
                options: {
                    'yuicompress': true
                }
            }
        },
        autoprefixer: {
            options: {
                browsers: [
                    'ie >= 8',
                    'ff >= 10',
                    'chrome >= 20',
                    'safari >= 7',
                    'opera >= 10',
                    'ios >= 7',
                    'android >= 2.3'
                ]
            },
            no_dest: {
                'src': staticBase + 'css/*.css'
            }
        },
        csslint: {
            strict: {
                options: {
                    'important': false,
                    'adjoining-classes': false,
                    'known-properties': false,
                    'box-sizing': false,
                    'box-model': false,
                    'overqualified-elements': false,
                    'display-property-grouping': true,
                    'bulletproof-font-face': true,
                    'compatible-vendor-prefixes': false,
                    'regex-selectors': true,
                    'errors': true,
                    'duplicate-background-images': true,
                    'duplicate-properties': true,
                    'empty-rules': true,
                    'selector-max-approaching': true,
                    'gradients': true,
                    'fallback-colors': true,
                    'font-sizes': false,
                    'font-faces': true,
                    'floats': false,
                    'star-property-hack': false,
                    'outline-none': false,
                    'import': true,
                    'ids': true,
                    'underscore-property-hack': false,
                    'rules-count': true,
                    'qualified-headings': false,
                    'selector-max': true,
                    'shorthand': true,
                    'text-indent': true,
                    'unique-headings': false,
                    'universal-selector': true,
                    'unqualified-attributes': false,
                    'vendor-prefix': false,
                    'zero-units': true,
                    'force': true
                },
                src: [staticBase + 'css/*.css']
            }
        },
        jshint: {
            options: {
                "jquery": true, //检查预定义的全局变量，防止出现$未定义，该项根据实际代码修改
                "bitwise": false, //不检查位运算
                "browser": true, //通过浏览器内置的全局变量检测
                "devel": true, //允许对调试用的alert和console.log的调用
                "camelcase": true, //强制验证驼峰式命名
                "curly": true, //强制使用花括号
                "eqeqeq": false, //不强制使用===比较运算符
                "es3": true, //兼容es3规范，针对旧版浏览器编写的代码
                "esnext": false, //不使用最新的es6规范
                "expr": true, //允许未赋值的函数名表达式，例如console && console.log(1)
                "forin": false, //不强制过滤遍历对象继承的属性    
                "freeze": false, //不限制对内置对象的扩展
                "immed": true, //禁止未用括号包含立即执行函数
                "indent": false, //不强制缩进
                "latedef": true, //禁止先调用后定义
                "maxdepth": false, //不限制代码块嵌套层数
                "maxparams": false, //不限制函数参数个数
                "newcap": false, //不对首字母大写的函数强制使用new
                "noarg": false, //不禁止对arguments.caller和arguments.callee的调用
                "noempty": false, //不禁止空代码块
                "nonew": false, //允许直接new实例化而不赋值给变量
                "plusplus": false, //允许++和--运算符使用
                "quotmark": "single", //字符串使用单引号
                "scripturl": true, //允许javascript伪协议的url
                "smarttabs": false, //允许混合tab和空格缩进
                "strict": false, //不强制使用es5严格模式
                "sub": true, //允许用[]形式访问对象属性
                "undef": true, //禁止明确未定义的变量调用，如果你的变量（myvar）是在其他文件中定义的，可以使用/*global myvar */绕过检测
                "unused": false, //允许定义没用的变量，在某些函数回调中，经常出现多个参数，但不一定会用
                "multistr": false, //禁止多行字符串，改用加号连接
                "globals": {
                    "jQuery": true,
                    "FastClick": true,
                    "define": true,
                    "unescape": true,
                    "require": true,
                    "iScroll": true,
                    "host": true
                }
            },
            with_overrides: {
                files: {
                    src: [staticBase + 'js/page/*.js']
                }
            }
        },
        htmlhint: {
            options: {
                'tagname-lowercase': true,
                'attr-lowercase': true,
                'attr-value-double-quotes': true,
                'attr-value-not-empty': false,
                'attr-no-duplication': true,
                'doctype-first': true,
                'tag-pair': true,
                'tag-self-close': true,
                'spec-char-escape': true,
                'id-unique': true,
                'src-not-empty': true,
                'head-script-disabled': true,
                'img-alt-require': false,
                'doctype-html5': true,
                'id-class-value': true,
                'style-disabled': false,
                'space-tab-mixed-disabled': true,
                'id-class-ad-disabled': true,
                'href-abs-or-rel': true,
                'attr-unsafe-chars': true,
                'force': true
            },
            html1: {
                src: ['html/*.html']
            }
        },
        watch: {
            css: {
                files: [staticBase + 'less/**/*.less', staticBase + 'less/*.less'],
                tasks: ['less', 'autoprefixer', 'csslint'],
                options: {
                    nospawn: false
                }
            },
            js: {
                files: ['js/page/login.js'],
                tasks: ['uglify']
            },
            /*shells: {
                files: [staticBase + 'js/page/*.js',staticBase + 'js/plugins/*.js'],
                tasks: ['jshint']
            },*/
            html: {
                files: ['html/*.html'],
                tasks: ['htmlhint']
            }
        },
        uglify: {
            pc: {
                files: {
                    'js/dist/login.js': ['js/page/login.js']
                }
            }
        }
    });

    // These plugins provide necessary tasks.
    grunt.loadNpmTasks('grunt-contrib-less');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-shell');
    grunt.loadNpmTasks('grunt-htmlhint');
    grunt.loadNpmTasks('grunt-contrib-csslint');
    grunt.loadNpmTasks('grunt-autoprefixer');
    grunt.loadNpmTasks('grunt-contrib-jshint');
    grunt.loadNpmTasks('grunt-contrib-uglify');

    // Default task.
    grunt.registerTask('default', ['less', 'watch', "shell", 'htmlhint', 'csslint', 'autoprefixer', 'jshint']);

};