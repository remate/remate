/*
Copyright 2013, KISSY UI Library v1.32
MIT Licensed
build time: Sep 4 17:12
*/
KISSY.add("editor/plugin/unordered-list/index",function(b,e,c,d){function a(){}b.augment(a,{pluginRenderUI:function(a){d.init(a);c.init(a,{cmdType:"insertUnorderedList",buttonId:"unorderedList",menu:{width:75,children:[{content:"\u25cf \u5706\u70b9",value:"disc"},{content:"\u25cb \u5706\u5708",value:"circle"},{content:"\u25a0 \u65b9\u5757",value:"square"}]},tooltip:"\u65e0\u5e8f\u5217\u8868"})}});return a},{requires:["editor","../list-utils/btn","./cmd"]});
