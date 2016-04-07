//var $=require('jquery');
//define(function(require) {
    var $ = require('jquery');
    //var $ = require('jquery/1.11.1/jquery.js');
    var numed = $(".numed");
    //数量输入框限定只能输入数字
    numed.on('change', function () {
        var regu = /^([0-9]+)$/;
        var re = new RegExp(regu);
        var inventory = $(this).closest('tr').attr('data-inventory');

        if (!re.test($(this).val())) {
            alert('请输入数字');
            numed.val('1').text('1').trigger('change');
        }else if(Number($(this).val())<1){
            numed.val('1').text('1').trigger('change');
        }else if(Number($(this).val()) > Number($(this).closest('tr').attr('data-inventory'))){
            //alert('亲，手下留情哦，仓库都被你搬回家拉，所选数量大于库存数量了哦~');
            $(this).val(inventory).text(inventory).trigger('change');
        }
    });
    $('.numMinus').on('click',function () {
        var self = $(this), numedInput = self.next('.numed'),
            edge = self.attr('edge'),
            oldVal = parseInt(numedInput.val()),
            oldVal = isNaN(oldVal) ? 0 : oldVal,
            newVal = oldVal - 1;
        if(Number(newVal)<1){
            //alert('亲，所选数量不能小于1件哦~');
            numedInput.val(edge).trigger('change');
        }else{
            numedInput.val(oldVal - 1).trigger('change');
        }
        return false;
    });

    $('.numPlus').on('click',function () {
        var self = $(this), numedInput = self.prev('.numed'),
            edge = self.attr('edge'),
            oldVal = parseInt(numedInput.val()),
            oldVal = isNaN(oldVal) ? 0 : oldVal,
            newVal = oldVal + 1;
        var inventory = $(this).closest('tr').attr('data-inventory');
        if (Number(newVal)>Number(inventory)){
            //alert('亲，手下留情哦，仓库都被你搬回家拉，所选数量大于库存数量了哦~');
            numedInput.val(inventory).trigger('change');
        }else{
            numedInput.val(newVal).trigger('change');
            //numedInput.val(newVal).text(newVal).trigger('change');
        }
        return false;
    });
  /*  function calculate(obj){
        var self = $(obj), numed = self.parent().children('.numed'),
            delta = self.hasClass('numInc') ? 1 : -1, edge = self.attr('edge'),
            newVal = oldVal + delta;

        *//*  if (!!edge && (delta > 0 && newVal > edge || delta < 0 && newVal < edge)) {
              newVal = edge;
          }*//*
    }*/

//});