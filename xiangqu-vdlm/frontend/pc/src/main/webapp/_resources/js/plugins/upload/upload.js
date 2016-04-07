define(['plugins/upload/plupload.full.min','amaze','base/utils'], function(plup,amaze,utils) {
    function upload(options, success,fail) {
        var uploader = new plupload.Uploader({
            runtimes: 'html5,flash,html4',
            file_data_name: "file[0]",
            browse_button: options.id,
            url: window.host + '/_f/u',
            flash_swf_url: '/sellerpc/_resources/js/plugins/upload/Moxie.swf',
            headers: {
                Accept: "application/json;charset=utf-8"
            },
            filters: {
                max_file_size: '4mb',
                mime_types: [{
                    title: "Image files",
                    extensions: "jpg,gif,png,jpeg"
                }]
            },
            multipart_params: {
                belong: 'PRODUCT'
            },

            init: {
                PostInit: function() {},
                FilesAdded: function(up, files) {
                    uploader.start();
                    if($.AMUI.progress){
                        $.AMUI.progress.start();
                    }
                },
                UploadProgress: function(up, file) {
                },
                Error: function(up, err) {
                    if($.AMUI.progress){
                        $.AMUI.progress.done();
                    }
                    if( err.code == -600 ){
                        //已经有提示了就不再提示了
                        if(!$('.KD-alert').is(':visible')){
                            utils.tool.alert('文件最大尺寸为4M~');
                        }
                        return;
                    }else{
                        //已经有提示了就不再提示了
                        if(!$('.KD-alert').is(':visible')){
                            utils.tool.alert(err.message);
                        }
                    }
                },
                FileUploaded: function(uploader, file, responseObject) {
                    if($.AMUI.progress){
                        $.AMUI.progress.done();
                    }
                    if($.parseJSON(responseObject.response).errorCode == 11001){
                        //已经有提示了就不再提示了
                        if(!$('.KD-alert').is(':visible')){
                            utils.tool.alert('文件最大尺寸为4M~');
                        }
                        return;
                    }
                    success(responseObject.response);
                }
            }
        });
        uploader.init();
    }
    return upload;
});