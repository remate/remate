// app/index.js
var generators = require('yeoman-generator');

module.exports = generators.Base.extend({
  // ���캯��
  constructor: function () {

    // ���ø��๹�캯��
    generators.Base.apply(this, arguments);

    // ִ�е�ʱ����� `--coffee` ����
    this.option('coffee'); 
  },
  method1: function () {
    console.log('method 1 just ran');
  },
  method2: function () {
    console.log('method 2 just ran');
  }
});