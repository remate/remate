/*global module:false*/

module.exports = function(grunt) {
    // Project configuration.
    grunt.initConfig({
        // Metadata.
        // Task configuration.  

        pkg: grunt.file.readJSON('package_grunt.json'),
        path:{
            src:'../_resources',
            temp:'temp',
            dist:'../_resources'
        },

        clean:{
            options:{
                force:true
            },
            temp:{
                src:['<%= path.temp %>']
            },
            css:{
                src:['<%= path.dist%>/css/scss']
            }

        },

        sass:{
          build:{
              options:{
                  style:'expanded',
                  sourcemap:'none'
              },
              files:[{
                  src:['<%= path.src%>/scss/my_address.scss'],
                  dest:'<%= path.temp%>/scss/my_address.css'
              }]
          }
        },
        concat:{
          css:{
              files:[
                  {//详情
                      src:['<%= path.temp%>/scss/my_address.css'],
                      dest:'<%= path.dist%>/css/scss/my_address.css'
                  }
              ]
          }
        },

        watch:{
          css:{
              files:[
                  '<%= path.src%>/scss/**/*.scss'
              ],
              tasks:['clean:css','sass','concat:css','clean:temp']
          }
        },


        less: {
            zt: {
                files: {
                    'css/xiangqu/refund.css': 'less/xiangqu/refund.less'
                },
                options: {
                    'yuicompress': true
                }
            }
        }
    });

    // These plugins provide necessary tasks.
    require('load-grunt-tasks')(grunt);

    // Default task.
    grunt.registerTask('default', [/*'less'*/'clean','sass','concat:css','clean:temp']);
    grunt.registerTask('dev',['watch']);
};