/*global module:false*/
module.exports = function(grunt) {

  // Project configuration.
  grunt.initConfig({
    // Task configuration.
    pkg: grunt.file.readJSON('package.json'),
    currDir:'test1',//当前的频道或页面名
    branchName:'160204',//打包到dist下的文件名，如果想在dist下生成一个与之前版本不一样的文件，请改之，以六位时间戳命名
    path:{
      src:'<%= currDir%>',
      temp:'temp',
      dist:'<%= currDir%>/<%= branchName%>'
    },
    clean:{
      options:{
        force:true
      },
      build:{
        src:[
          '<%= path.dist%>/'
        ]
      },
      temp:{
        src:['<%= path.temp%>']
      }
    },
    copy:{
      main:{
        files:[
          {
            expand:true,
            cwd:'<%= path.src%>/images/',
            src:['**'],
            dest:'<%= path.dist%>/images/'
          }
        ]
      }
    },
    sass:{
      build:{
        options:{
          style:'expanded',
          sourcemap:'none',
          force:true
        },
        files:[
          {//示例,真实项目请按实际路径修改
            src:['<%= path.src%>/index.scss'
            ],
            dest:'<%= path.temp%>/index.css'
          }
        ]
      }
    },
    concat:{
      css:{
        files:[
          {//详情
            src:['<%= path.temp%>/index.css'],
            dest:'<%= path.dist%>/index.css'
          }
        ]
      },
      js:{
        files:[
          {//详情
            src:['<%= path.src%>/index.js'],
            dest:'<%= path.dist%>/index.js'
          },
          {//详情
            src:['<%= path.src%>/flexible.js'],
            dest:'<%= path.dist%>/flexible.js'
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
        cwd:'<%= path.dist%>/',
        src:['*\*/\*.css','!*.min.css'],
        dest:'<%= path.dist%>/',
        ext:'.min.css'
      }
    },
    uglify:{
      build:{
        files:[
          {
            expand:true,
            cwd:'<%= path.dist%>/',
            src: ['*\*/\*.js', '!*.min.js'],
            dest:'<%= path.dist%>/',
            ext:'.min.js'
          }
        ]
      }
    },

    watch: {
      css:{
        files:'<%= path.src%>/*.scss',
        tasks:['clean:css','sass','concat:css','cssmin','clean:temp']
      },
      js:{
        files:['<%= path.src%>/*.js'],
        tasks:['clean:js','concat:js','uglify']
      }
    }
  });

  // These plugins provide necessary tasks.

  require('load-grunt-tasks')(grunt);

  // Default task.
  grunt.registerTask('default', ['clean', 'copy','sass','concat:css','concat:js','cssmin','uglify','clean:temp']);
  grunt.registerTask('dev',['watch']);

};
