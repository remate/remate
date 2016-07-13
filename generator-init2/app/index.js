'use strict';
var generators = require('yeoman-generator');
var chalk = require('chalk');
var yosay = require('yosay');
var path = require('path');
module.exports = generators.Base.extend({
    initializing: function () {    //初始化准备工作
    },

    prompting: function () {  //接受用户输入
        var done = this.async(); //当处理完用户输入需要进入下一个生命周期阶段时必须调用这个方法

        //yeoman-generator 模块提供了很多内置的方法供我们调用，如下面的this.log , this.prompt , this.template , this.spawncommand 等

        // Have Yeoman greet the user.
        this.log(yosay('Hi, welcome to DDSC-zuoci!'));       
        var prompts = [
            {
		      type: 'input',
		      name: 'project',
		      message: '请输入文件名字name',
		      default: 'project'
		    },
		    {
		      type: 'input',
		      name: 'title',
		      message: '请输入页面的标题Title',
		      default: 'title'
		    }
        ];
        this.prompt(prompts, function (props) {
            this.someOption = props.someOption;  
      		this.projectName = props.project;
      		this.title = props.title;

            done();  //进入下一个生命周期阶段
        }.bind(this));
    },

    writing: {  //生成目录结构阶段
        app: function () {      //默认源目录就是生成器的templates目录，目标目录就是执行`yo example`时所处的目录。调用this.template用Underscore模板语法去填充模板文件
            this.copy('index.html', ''+this.projectName+'.html'); 
            this.template('_package.json', 'package.json');  //
            this.template('_gulpfile.js', 'gulpfile.js');
            this.copy('_src/scss/index.scss', 'src/scss/index.scss');
            this.copy('_src/js/index.js', 'src/js/index.js');
            this.copy('_src/js/jquery-1.9.1.js', 'src/js/jquery.js');
        }
    },

    install: function () {
        var done = this.async();
        this.spawnCommand('cnpm', ['install'])  //安装项目依赖
            .on('exit', function (code) {
                if (code) {
                    done(new Error('code:' + code));
                } else {
                    done();
                }
            })
            .on('error', done);
    },
    end: function () {
        var done = this.async();
        this.spawnCommand('gulp')   //生成器退出前运行gulp，开启watch任务
            .on('exit', function (code) {
                if (code) {
                    done(new Error('code:' + code));
                } else {
                    done();
                }
            })
            .on('error', done);
    }
});