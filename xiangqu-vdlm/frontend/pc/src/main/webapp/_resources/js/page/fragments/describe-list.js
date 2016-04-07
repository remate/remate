define(['jquery','doT', 'base/utils'], function(jquery,doT,utils){
    var domain = 'http://localhost:8888';
    var host = domain + '/v2';
    var desclist = {
            init: function () {
                this.layer();
                this.getData();
                //this.listSave();
                this.submit();
            },
            layer : function () {
                $("#describe-list").on("click", function () {
                    $('#describeList').modal({
                        relatedTarget: this,
                        width: '688',
                        onConfirm: function(options) {
                            this.close();
                        },
                        onCancel: function() {
                            this.close();
                        }
                    });
                });

            },
        //获取段落描述列表
        getFragment : function(success,fail){
            var that = this;
            $.ajax({
                url: host + '/fragment/list',
                type: 'POST',
                dataType: 'json',
                success: function(data) {
                    if (data.errorCode == 200) {
                        success && success(data);
                    } else {
                        fail && fail(data.errorCode);
                    }
                },
                error: function(state) {
                    if (state.status == 401) {
                        that.goLogin();
                    } else {
                        fail && fail('服务器暂时没有相应，请稍后重试...');
                    }
                }
            });
        },
      /*  listSave : function () {
            //段落列表保存
            $("#describeList").delegate("#buttonSave", 'click', function() {
                var proDesibe = $(".pro-desibe");
                $(".pro-desibe").eq(proDesibe.length - 1).clone().appendTo(".sortDrag");
                $(".pro-name").text($("#proName").val());
                $(".pro-text").text($("#describeText").val());
            })
        },*/
            getData: function () {
                var that=this;
                var addTpl = $('.tpl-fragment').html();
                var doTtmpl = doT.template(addTpl);
                var box = $('#describeList .list tbody');

                that.getFragment(function (data) {
                    box.empty();
                    if (!data.data.length) {
                        return false;
                        //$(".tab-list").html("还没有段落列表哦~马上添加吧!").css("padding","20px");
                    };
                    $.each(data.data, function (i, el) {
                        var imgs = [],
                            keys = [],
                            src;
                        $.each(el.imgs, function (index, img) {
                            imgs.push(img.imgUrl);
                            keys.push(img.img);
                        });
                        if (el.imgs[0]) {
                            src = el.imgs[0].imgUrl;
                        }
                        var data = {
                            id: el.id,
                            name: el.name,
                            description: el.description,
                            imgs: imgs.toString(),
                            keys: keys.toString(),
                            src: imgs[0],
                            showModel: el.showModel
                        }
                        box.append(doTtmpl(data));
                    });
                    //initUi(box);
                });
            },
            initUi: function() {
                $('.sortDrag').find('.moveico').off('mousedown');
                $('.sortDrag').sortDrag();
            },
            submit: function() {
                var addTpl = $('.tpl-desc').html();
                var doTtmpl = doT.template(addTpl);
                //buttonSave
                $('#buttonSave').on('click', function() {
                    var list = [];
                    $('#describeList [name="productId"]:checked').each(function(i, el) {
                        var $pare = $(el).closest('tr');
                        var data_id = $pare.attr('data-id');
                        if (!$('.descList .pro-desibe[data-id="' + data_id + '"]').length) {
                            var data = {
                                id: data_id,
                                name: $pare.find('.pro-text').text(),
                                description: $pare.find('.pro-desc').text(),
                                imgs: $pare.find('.pro-img64').attr('srcs'),
                                keys: $pare.find('.pro-img64').attr('keys'),
                                src: $pare.find('.pro-img64').attr('src'),
                                showModel: $pare.attr('data-showmodel')
                            }
                            $('.descList').append(doTtmpl(data));
                        }
                    });
                    //KD.product.initUi();
                    $('#describeList .close').trigger('click');
                });
            }
    }
    desclist.init();
});