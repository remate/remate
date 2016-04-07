define(['jquery','kindEditor/kindeditor-min'], function(jquery) {
    KindEditor.ready(function(K) {
        window.editor = K.create('#J-editor', {
            uploadJson: window.host + '/_f/u_desc',
            allowFileManager: false,
            width: '800px',
            height: '300px',
            items:[  'undo', 'redo', '|', 'preview', 'print',  'cut', 'copy', 'paste',
        'plainpaste', 'wordpaste', '|', 'justifyleft', 'justifycenter', 'justifyright',
        'justifyfull', 'insertorderedlist', 'insertunorderedlist', 'indent', 'outdent', 'subscript',
        'superscript', 'clearhtml', 'quickformat', 'selectall', '|',
        'formatblock', 'fontname', 'fontsize', '|', 'forecolor', 'hilitecolor', 'bold',
        'italic', 'underline', 'strikethrough', 'lineheight', 'removeformat', '|', 'image', 
         'table', 'hr', 'emoticons',
        'anchor']
        });
    });
});