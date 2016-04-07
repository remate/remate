var $ = require('jquery');
//module.exports = function() {
 $(document).ready(function(){
     var iheader =$("#iframe-header");
     var ifooter =$("#iframe-footer");
     iheader.attr("src",iheader.attr("data-src"));
     ifooter.attr("src",ifooter.attr("data-src"));
 })

//};