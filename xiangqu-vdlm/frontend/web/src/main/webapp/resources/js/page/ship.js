var $ = require('jquery');
$(document).ready(function(){
     $("#logisticsNo").bind('change',function(){
         var regu =/[^\u4E00-\u9FA5]{10,}/;
         var re = new RegExp(regu);
         if(!re.test($(this).val())){
             alert("请填写正确的快递单号~");
         }

     });

     //选择其他物流公司的时候显示隐藏下面自定义的输入框
     $("#logisticsCompany").on('change', function() {
        if ($(this).val() == 'other') {
            $('.otherCompanyBar').show();
        } else {
            $('.otherCompanyBar').hide();
        }
     });

     
     var canClick = 1;
     var jumpUri = $("#cart_btn_submit").attr("data-uri");
    $("#cart_btn_submit").bind("click",function(){
        if(!canClick){
            return false;
        }
        var tempCompany = ''; 
        // $("#logisticsCompany option:selected").val();
        var tempNumber = $("#logisticsNo").val();
        var tempId = $("#id").val();
        var tempOrderId = $("#orderId").val();
        //如果选择的是其他物流公司，则物流公司从下面自定义名称里取，同时需要判断物流公司名字
        if ($("#logisticsCompany").val() == 'other') {
            tempCompany = $('#otherCompany').val();
        } else {
            tempCompany = $("#logisticsCompany").val();
        }
        if(tempCompany == ''){
            alert("请选择快递公司~");
            return false;
        }
        if($('#logisticsNo').val().length==0){
            alert("请输入快递单号~");
            return false;
        }
        canClick = 0;
        
    	$.ajax({
    		url : '/order/refund/ship' ,
    		type : "GET" ,
    		data : {
    			"id" : tempId,
    			"orderId" : tempOrderId,
    			"logisticsCompany" : tempCompany,
    			"logisticsNo" : tempNumber
    		},
    		dataType : "json",
    		error: function(XMLHttpRequest, textStatus, errorThrown){
    			canClick = 1;
    		},
    		success : function(data){
    			alert("提交成功！");
    			window.location.href = jumpUri+"/user/orders";
    		}
    	})
    	canClick = 1;
    });
    
    $(".cart_btn_cancel").bind("click",function(){
    	window.location.href = jumpUri+"/user/orders";
    })
    
    function testStatus(){
    	if($("#order_refund_sorm").attr("data-status") == "RETURN_ING"){
			alert("请等待卖家确认");
			window.location.href = jumpUri+"/user/orders";
    	}
    }
    testStatus();
 })