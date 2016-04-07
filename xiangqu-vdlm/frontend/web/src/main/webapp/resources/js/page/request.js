var $ = require('jquery');

//打开页面初始化，判断有无退款信息
var get_id = $("#getorderInfo").attr("data-orderid");//如果有值，说明已有退款信息
var hadInfo = GetQueryString("id");
var openNext = {
	init:function(){
		$(".nowState").hide();
		setOptionTitle();
		if(!hadInfo){
			$(".refund_step2").attr("Class","refund_step1");
		}else{
			$(".refund_step1").attr("Class","refund_step2");
			var temp_1 = hadInfo;
			var temp_2 = $("#getorderId").val();
			var temp_3 = GetQueryString("buyerRequire")
			getLastInfo(temp_1,temp_2,temp_3);
			var get_reason = $("#getorderInfo").attr("data-reason");
			var get_price = $("#getorderInfo").attr("data-fee");
			var get_explain= $("#getorderInfo").attr("data-memo");
			$(".reason").text($("option[value='"+get_reason+"']").html());
			$(".request_price").text(get_price);
			$(".explain").text(get_explain);
			$(".process_2").show();
			$(".process_1").hide();
			$(".nowState").text("退款状态：退款处理中");
			$(".nowState").show();
		}
	}	
};

openNext.init();
var theMax = $("#getorderId").attr("data-fee");
var thePost = $("#getorderId").attr("data-logisticsfee");
$(".price_box b").html(theMax);
$(".price_box b.post").html(thePost);
 /*判断value*/
var needGet;
function GetQueryString(name) {
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)","i");
	var r = window.location.search.substr(1).match(reg);
	if (r!=null) return unescape(r[2]); return null;
}
function setOptionTitle(){
	if(GetQueryString("buyerRequire") == 1){
		$(".refund_title select:first option").text("仅退款");
		$("[for='refund_reason']").text("退款原因：");
		$(".refund_reason option:first").text("请选择退款原因");
		needGet = 0;
		$(".refund_get_box").hide();
	}
	if(GetQueryString("buyerRequire") == 2){
		$(".refund_get_box").show();
		needGet = 1;
		$(".refund_title select:first option").text("退货及退款");
		$("[for='refund_reason']").text("退款退货原因：");
		$("[name='reasonlable']").text("退款退货原因：");
		$(".refund_reason option:first").text("请选择退款退货原因");
		$(".refund_step").removeClass("refund_style2").addClass("refund_style1");
	}
}

$(".refund_reason").one("focus",function(){
	$(this).find("option:first").remove();
});



 /*第一步提交按钮*/
var refundReason,refundPrice,refundExplain;
var canclickbtn = 1;
$(".process_1 button").bind("click",function(){
	if(!canclickbtn){
		return false;
	}
	if(!theMax){
		return false;
	}
	var refundReason = $(".refund_reason option:selected").val();
	if(needGet){
		var refundGet = $(".refund_get option:selected").val();
		if(refundGet === "0" || refundGet === "1"){
		}else{
			alert("请选择是否收到货");
			return false;
		}
	}
	var refundPrice = $("[name='refund_price']").val();
	var refundExplain = $("[name='refund_explain']").val();
	if(refundReason === "请选择退款原因" || refundReason === "请选择退款退货原因" || !refundReason){
		alert("请选择退款/退货原因");
		return false;
	}
	if(!$("[name='refund_price']").val()){
		alert("请填写退款金额");
		return false;
	}
	if(refundReason === "其他" && !refundExplain){
		alert("请填写说明");
		return false;
	}
	postRequest(refundReason,refundPrice,refundExplain,refundGet);
})

 /*第二步返回*/
var XQweburl = $(".returnToTemp1").attr("data-uri");
$(".returnToTemp1").bind("click",function(){
	window.location.href = XQweburl+"/user/orders";
});

 /*第一步返回*/
$(".returnbtn").bind("click",function(){
	window.location.href = XQweburl+"/user/orders";
});
 /*金额正则表达式*/
$("[name='refund_price']").bind("blur",function(){
	var RE = /^(([0-9]+[\.]?[0-9]{1,2})|[1-9])$/;
	var theNum = $("[name='refund_price']").val();
	if(!theNum){
		return false;
	}
	if(!/^[0-9]*$|[\.]/.test($("[name='refund_price']").val())){
		alert("请输入数字");
		$("[name='refund_price']").val("");
		return false;
	}
	if(RE.test($("[name='refund_price']").val())){
		if(Number(theNum) <= 0){
			alert("请输入大于0的数字");
			$("[name='refund_price']").val("");
			return false;
		}
		if(Number(theNum) > Number(theMax)){
			alert("请输入小于"+theMax+"的数字");
			$("[name='refund_price']").val("");
			return false;
		}
	}else{
		alert("至多输入2位小数");
		$("[name='refund_price']").val("");
		return false;
	}
})

$(".revisebtn").bind("click",function(){
	var temp1 = $(".process_2 .reason").html();
	var temp2 = $(".process_2 .request_price").html();
	var temp3 = $(".process_2 .explain").html();
	$(".process_1 option:contains("+temp1+")").attr("selected","selected");
	$(".process_1 [name='refund_price']").val(temp2);
	$(".process_1 textarea[name='refund_explain']").val(temp3);
	$(".process_1").show();
	$(".process_2").hide();
	$(".refund_step2").attr("Class","refund_step1");
	$(".nowState").hide();
});

function postRequest(temp_reason,temp_price,temp_explain,temp_get){
	var theOrderId = $("#getorderId").val();//订单id
	var post_type = GetQueryString("buyerRequire");
	canclickbtn = 0;
	$.ajax({
		url : '/xiangqu/web/order/refund/request' ,
		type : "POST" ,
		data : {
			"id" : hadInfo,
			"orderId" : theOrderId,
			"refundReason" : temp_reason,
			"refundFee" : temp_price,
			"refundMemo" : temp_explain,
			"buyerRequire" : post_type,
			"buyerReceived" : temp_get
		},
		dataType : "json",
		error: function(XMLHttpRequest, textStatus, errorThrown){
			canclickbtn = 1;
			alert(XMLHttpRequest.status);
		},
		success : function(data){
			if(data.rc==1){
				$(".refund_step1").attr("Class","refund_step2");
				$(".reason").text($(".refund_reason option:selected").html());
				$(".request_price").text(temp_price);
				$(".explain").text(temp_explain);
				$(".process_2").show();
				$(".process_1").hide();
				$(".nowState").text("退款状态：退款处理中");
				$(".nowState").show();
				hadInfo = data.obj;
			}else{
				 alert(data.msg);
			}
			canclickbtn = 1;
		}
	})
};


function getLastInfo(temp_id,temp_orderId,temp_buyerRequire){
	$.ajax({
		url : '/xiangqu/web/order/refund/toRequest' ,
		type : "GET" ,
		data : {
			"id" : temp_id,
			"orderId" : temp_orderId,
			"buyerRequire" : temp_buyerRequire
		},
		dataType : "json",
		error: function(XMLHttpRequest, textStatus, errorThrown){
		},
		success : function(data){
		}
	})
}