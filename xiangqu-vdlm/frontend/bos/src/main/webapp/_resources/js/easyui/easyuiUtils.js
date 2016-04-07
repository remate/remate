/**
 *panel关闭时回收内存，主要用于layout使用iframe嵌入网页时的内存泄漏问题 
 */
$.fn.panel.defaults.onBeforeDestroy = function() {
	var frame = $('iframe', this);
	try {
		if (frame.length > 0) {
			for ( var i = 0; i < frame.length; i++) {
				frame[i].src = '';
				frame[i].contentWindow.document.write('');
				frame[i].contentWindow.close();
			}
			frame.remove();
			if (navigator.userAgent.indexOf("MSIE") > 0) {// IE特有回收内存方法
				try {
					CollectGarbage();
				} catch (e) {
				}
			}
		}
	} catch (e) {
	}
};

/**
 * 防止panel/window/dialog组件超出浏览器边界
 */
var easyuiPanelOnMove = function(left, top) {
	var l = left;
	var t = top;
	if (l < 1) {
		l = 1;
	}
	if (t < 1) {
		t = 1;
	}
	var width = parseInt($(this).parent().css('width')) + 14;
	var height = parseInt($(this).parent().css('height')) + 14;
	var right = l + width;
	var buttom = t + height;
	var browserWidth = $(window).width();
	var browserHeight = $(window).height();
	if (right > browserWidth) {
		l = browserWidth - width;
	}
	if (buttom > browserHeight) {
		t = browserHeight - height;
	}
	$(this).parent().css({/* 修正面板位置 */
		left : l,
		top : t
	});
};
$.fn.dialog.defaults.onMove = easyuiPanelOnMove;
$.fn.window.defaults.onMove = easyuiPanelOnMove;
$.fn.panel.defaults.onMove = easyuiPanelOnMove;

/**
 * 通用错误提示
 */
var easyuiErrorFunction = function(XMLHttpRequest) {
	$.messager.progress('close');
	//$.messager.alert('错误', XMLHttpRequest.responseText);
	var _errorDialog = commonDialog({
		title : '500错误',
		maximizable : true,
		resizable:true,
		width:500,
		height:300,
		overflow:scroll,
		buttons:[{
			text:'关闭',
			iconCls:'icon-no',
			handler:function(){
				_errorDialog.dialog("close");
			}
		}],
		content : XMLHttpRequest.responseText
	});
};
$.fn.datagrid.defaults.onLoadError = easyuiErrorFunction;
$.fn.treegrid.defaults.onLoadError = easyuiErrorFunction;
$.fn.tree.defaults.onLoadError = easyuiErrorFunction;
$.fn.combogrid.defaults.onLoadError = easyuiErrorFunction;
$.fn.combobox.defaults.onLoadError = easyuiErrorFunction;
$.fn.form.defaults.onLoadError = easyuiErrorFunction;

/**
 * 
 * 创建一个通用的dialog
 * 
 * @returns commonDialog.handler 弹出的dialog句柄
 */
commonDialog = function(options) {
	if (commonDialog.handler == undefined) {// 避免重复弹出
		var opts = $.extend({
			title : '',
			width : 840,
			height : 400,
			modal : true,
			onClose : function() {
				commonDialog.handler = undefined;
				$(this).dialog('destroy');
			}
		}, options);
		opts.modal = true;// 强制dialog模式化
		
		var dvObj = '<div style="overflow:hidden"/>';
		if(options.overflow)
			dvObj = '<div style="overflow:'+options.overflow+'"/>';
		
		return commonDialog.handler = $(dvObj).dialog(opts);
	}
};