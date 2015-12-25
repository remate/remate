/**
 * Created by Remate-zuoci on 2015/7/2.
 */
/*global module:false*/
module.exports = function(grunt) {
    // Project configuration.
    grunt.initConfig({
        // Metadata.
        pkg: grunt.file.readJSON('package.json'),
        banner: '/*! <%= pkg.title || pkg.name %> - v<%= pkg.version %> - ' +
            '<%= grunt.template.today("yyyy-mm-dd") %>\n' +
            '<%= pkg.homepage ? "* " + pkg.homepage + "\\n" : "" %>' +
            '* Copyright (c) <%= grunt.template.today("yyyy") %> <%= pkg.author.name %>;' +
            ' Licensed <%= _.pluck(pkg.licenses, "type").join(", ") %> */\n',
        // Task configuration.
        actName:'act_name',         //******每次新增活动页面必须要改，名字即活动名******
        path:{
            src:'dev',//开发目录
            temp:'../temp',//临时目录
            dist:'../build'//发布目录
        },
        clean:{
            options:{
                force:true
            },
            temp:{
                src:['<%= path.temp%>/']
            },
            css:{
                src:['<%= path.dist%>/css/*']
            },
            js:{
                src:['<%= path.dist%>/js/*']
            }
        },
        copy:{
            main:{
                files:[
                    {
                        expand:true,
                        cwd:'<%= path.src%>/images/',
                        src:'**',
                        dest:'<%= path.dist%>/images/'
                    }
                ]
            }
        },
		less:{
			temp:{
				files:[
					{//示例,真实项目请按实际路径修改
						src:['<%= path.src%>/css/xiangqu.less'
						],
						dest:'<%= path.temp%>/dev/css/xiangqu.css'
					}

				]
			}
		},
        concat: {
            /*合并文件*/
            css:{
                files:[
                    {
                        src:[
                            '<%= path.temp%>/dev/css/xiangqu.css'
                        ],
                        dest:'<%= path.dist%>/css/xiangqu.css'
                    }
                ]
            },
            js:{
                files:[
                    {
                        src:['<%= path.src%>/js/xiangqu.js'
                        ],
                        dest:'<%= path.dist%>/js/xiangqu.js'
                    }
                ]
            }
        },
        cssmin:{
            options:{
                compatibility:'ie8',
                noAdvanced:true
            },
            build:{
                expand:true,
                cwd:'<%= path.dist%>/css/',
                src:['*\*/\*.css','!*.min.css'],
                dest:'<%= path.dist%>/css/',
                ext:'.min.css'
            }
        },
        uglify: {
            build:{
                files:[
                    {
                        expand:true,
                        cwd:'<%= path.dist%>/js/',
                        src:['*\*/\*.js','!*.min.js'],
                        dest:'<%= path.dist%>/js/',
                        ext:'.min.js'
                    }

                ]
            }
        },
        watch: {
            css:{
                files:[
                    '<%= path.src%>/css/**/*.less',
					'<%= path.src%>/css/**/*.css'
				],
                tasks:['clean:css','less','concat:css','cssmin']
            },
            minjs:{
                files:['<%= path.src%>/js/**/*.js'],
                tasks:['clean:js','concat:js','uglify']
            }
        },
        "bower": {
            "install": {
                "options": {
                    "targetDir": "dev/js/lib",
                    "layout": "byComponent",
                    //"layout": "byType",
                    "install": true,
                    "verbose": false,
                    "cleanTargetDir": false
                }
            }
        }
    });

    require('load-grunt-tasks')(grunt);

    // Default task.
    grunt.registerTask('default', ['copy','clean','less','concat','cssmin','uglify','clean:temp']);
    grunt.registerTask('bowerCopy',['bower']);
    grunt.registerTask('dev',['watch']);
};
