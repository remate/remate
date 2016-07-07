'use strict';  
  
var util = require('util');  
var path = require('path');  
var yeoman = require('yeoman-generator');  
var yosay = require('yosay');  
var chalk = require('chalk');  
  
var MygeneratorGenerator = yeoman.generators.Base.extend({  
  init: function () {  
    this.pkg = require('../package.json');  
    this.on('end', function () {  
      if (!this.options['skip-install']) {  
        this.installDependencies();  
      }  
    });  
  },  
  
  askFor: function () {  
    var done = this.async();  
  
    // 建议使用this.log() 而不是console.log， 因为在非命令行环境下console.log()不会显示  
    this.log(yosay('Hi, welcome to DDSC-zuoci!'));  
    var prompts = [
    // {  
    //   type: 'confirm',  
    //   name: 'someOption',  
    //   message: 'Hello boy, would you like to enable this option?',  
    //   default: true  
    // },
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
      done();  
    }.bind(this));  
  },  
  
  app: function () {  
    //创建目录  
    this.mkdir(''+this.projectName+'');  
  
    // this.copy() 第一个参数为源文件名，默认目录为app/templates, 第二个参数为目标文件  
    this.copy('index.html', ''+this.projectName+'/'+this.projectName+'.html');  
    this.copy('index.css', ''+this.projectName+'/css/index.css'); 
    this.copy('jquery.min.js', ''+this.projectName+'/js/jquery.min.js');   
    this.copy('index.js', ''+this.projectName+'/js/index.js'); 
  } 
});  
  
module.exports = MygeneratorGenerator;  