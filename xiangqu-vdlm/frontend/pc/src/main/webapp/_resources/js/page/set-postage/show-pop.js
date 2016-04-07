

define(['jquery', 'amaze', 'doT','base/utils'], function(jquery, amaze, doT,utils) {
    var $modalAreas = $('.modal-areas dd span'),
        $modalAreasName = $('.modal-areas dt'),
        tplPost = $('.tpl-post').html();
    /**
     * [Pop description]
     * @type {Object}
     * show 方法 外面调用要使用call 把 this传进来 ,要不然Modal的this会指向pop
     */
    var Pop = {
        show: function(el) {
            var self = Pop;
            $('#J-post-pop').modal({
                relatedTarget: this,
                onConfirm: function(options) {
                    var iMsg = '';
                    if ($(this.relatedTarget).is('.set-add a')) {
                        //如果是新增,价格不能与之前有的重合
                        iMsg = self.check().msg;
                        if (iMsg) {
                            utils.tool.alert(iMsg);
                            return;
                        }
                        self.save();
                    } else {
                        //如果是编辑
                        var $thisTr = $(this.relatedTarget).parents('tr'),
                            iEditName = $thisTr.attr('data-post');
                        iMsg = self.check(iEditName).msg;
                        if (iMsg) {
                            utils.tool.alert(iMsg);
                            return;
                        }
                        self.save($thisTr);
                    }
                    this.close();
                },
                onCancel: function() {
                    this.close();
                }
            });
        },
        check: function(editName) {
            /**
             * 检测 1.邮费是不是为空 2.城市是不是为空 3.如果是编辑，是不是和其他价格重复 4.如果是新增，不能和现有的重复
             * 5.邮费价格是不是数字
             * @type {Array}
             */
            var aOtherPost = [],
                postVal = $.trim($('.modal-post').val());
            $('.post-table tr').each(function() {
                aOtherPost.push($(this).attr('data-post'));
            });
            if (editName) {
                aOtherPost = _.without(aOtherPost, editName);
            }
            if ($.trim($('.modal-post').val()) == '') {
                return {
                    code: 400,
                    msg: '邮费不能为空'
                }
            }
            if ($.trim($('.modal-post').val()) > 999999) {
                return {
                    code: 400,
                    msg: '邮费不能超过999999'
                }
            }
            if ($.trim($('.modal-post').val()) < 0) {
                return {
                    code: 400,
                    msg: '邮费不能为负数'
                }
            }
            if (!$modalAreas.filter('.active').length) {
                return {
                    code: 400,
                    msg: '城市不能为空'
                }
            }
            if (checkExist(aOtherPost, postVal)) {
                return {
                    code: 400,
                    msg: '已经有邮费为' + $.trim($('.modal-post').val()) + '元的配置，请在原基础上修改'
                }
            }
            if (!$.trim($('.modal-post').val()).match(/^-?(?:\d+|\d{1,3}(?:,\d{3})+)?(?:\.\d+)?$/)) {
                return {
                    code: 400,
                    msg: '邮费必须为大于等于0的数字'
                }
            }
            return {
                code: 200,
                msg: ''
            }
            function checkExist(arr,val){
                for(var i = 0;i < arr.length;i++){
                    if(parseFloat(arr[i]) == parseFloat(val)){
                        return true;
                    }
                }
                return false;
            }
        },
        save: function(isModify) {
            var data = {
                money: $.trim($('.modal-post').val()),
                citys: []
            };
            var postModel = doT.template(tplPost);
            $modalAreas.filter('.active').each(function() {
                data.citys.push({
                    name: $.trim($(this).text()),
                    id: $.trim($(this).attr('data-id'))
                });
            });

            if (isModify) {
                isModify.replaceWith(postModel(data));
            } else {
                $('.post-table tbody').append(postModel(data));
            }

        }
    };
    //弹窗里面的城市点击事件
    $modalAreas.on('click', function() {
        if (!$(this).hasClass('disabled')) {
            $(this).toggleClass('active');
        }
    });
    //点击区域总称 后面的全部选中，方便操作
    $modalAreasName.on('click', function() {
        $(this).next('dd').find('span').not('.disabled').toggleClass('active');
    });

    return Pop;
});

