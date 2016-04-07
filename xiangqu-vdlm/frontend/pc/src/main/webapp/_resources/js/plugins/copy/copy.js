/**
 * [description]
 * @example
 * require(['jquery','copy','base/utils'],function(jquery,copy,utils){
      copy($('.copycode'),function(){
          utils.tool.alert('复制成功！');
      },function(){
          utils.tool.alert('复制失败！');
      });
  });
 */
;define(['plugins/copy/ZeroClipboard.min'], function(ZeroClipboard) {
    //$handel上需要标注data-clipboard-target="要复制的元素的ID"
    return function($handel, success, fail) {
        var clip = new ZeroClipboard($handel.eq(0), {
            moviePath: "/sellerpc/_resources/js/plugins/copy/ZeroClipboard.swf"
        });
        clip.on('complete', function(client, args) {
            if (args.text) {
                success && success(args.text);
            } else {
                fail && fail();
            }
        });
    }
});