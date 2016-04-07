

define(['jquery','base/set-desc/elements','base/utils','validate','doT'], function(jquery,elements,utils,validate,doT) {
    var $form = $('#J-per-desc'),
        tpl = $('.tpl-desc-img').html(),
        doTtpl = doT.template(tpl);
    var tplDesc = $('.tpl-desc').html(),
        doTtplDesc = doT.template(tplDesc);
    var pop = {
        show: function($tr) {
            elements.pop.find('label.error').remove();
            elements.popImgBox.find('span').remove();
            if (!$tr) {
                //如果是新增模式
                elements.pop.attr('data-target','');
                elements.popName.val('');
                elements.popDesc.val('');
                elements.popShowModel.filter(':eq(0)').attr('checked',true);
                elements.popShowModel.initCheck();
            } else {
                //如果是编辑模式
                elements.pop.attr('data-target',$tr.attr('data-id'));
                elements.popName.val($.trim($tr.find('.desc-name').text()));
                elements.popDesc.val($.trim($tr.find('.desc-detail').text()));
                if($tr.attr('data-showmodel') == "false"){
                    elements.popShowModel.filter(':eq(1)').attr('checked',true);
                }else{
                    elements.popShowModel.filter(':eq(0)').attr('checked',true);
                }
                elements.popShowModel.initCheck();
                //图片
                $tr.find('.imgsData span').each(function(i,el){
                    var str = doTtpl({
                        imgUrl: $(el).attr('data-imgurl'),
                        img: $(el).attr('data-img')
                    });
                    elements.popImgAdd.before(str);
                });
            }

            elements.pop.modal({
                onConfirm: function() {
                    pop.modal = this;
                    $form.submit();
                },
                onCancel: function() {
                    this.close();
                }
            });
        },
        save: function($tr){
            var imgs = elements.popImgBox.find('span img'),
                aImgs = [],
                aImgsModel = [],
                $el;
            $.each(imgs,function(i,el){
                aImgs.push($(el).attr('data-img'));
                aImgsModel.push({
                    imgUrl: $(el).attr('src'),
                    img: $(el).attr('data-img')
                });
            });
            var data = {
                name: elements.popName.val(),
                description: elements.popDesc.val(),
                showModel: elements.popShowModel.eq(0).is(':checked') ? 1 : 0,
                imgs: aImgs.join(',')
            };
            if(elements.pop.attr('data-target')){
                $el = $('.desc-sort-box tr[data-id="'+ elements.pop.attr('data-target') +'"]');
                $.extend(data,{
                    id:$el.attr('data-id')
                });
            }
            utils.api.saveDesc(data,function(res){
                $.extend(data, {    
                    id: res.data.id,
                    imgList: aImgsModel
                });
                var str = doTtplDesc(data);
                if(!$el){
                //如果时新增
                    $('.desc-sort-box').append(str);
                }else{
                    $el.replaceWith($(str));
                }
                pop.modal.close();
                utils.tool.alert('保存成功!');
            },function(){
                utils.tool.alert('保存失败！请稍后再试!');
            });
        }
    }

    $form.validate({
        debug: false,
        focusInvalid: true,
        onkeyup: false,
        submitHandler: function(form) {
            if(!elements.popImgBox.find('.desc-pop-img').length){
                //如果没有段落图片
                elements.popImgBox.find('label.error').remove();
                elements.popImgBox.append('<label class="error">段落图片不能为空</label>');
            }else{
                pop.save(pop.tr);
            }
        },
        rules: {
            'desc-pop-name': {
                required: true,
                maxlength: 30
            }
        },
        messages: {
            'desc-pop-name': {
                required: '段落名称不能为空',
                maxlength: '段落名称不能超过30个字符'
            }
        }

    });
    return pop;
});

