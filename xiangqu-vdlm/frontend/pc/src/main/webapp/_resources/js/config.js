var dev = location.href.indexOf('www.kkkd.com') == -1 && location.href.indexOf('www.kkkdtest.com') == -1;

//检测IE版本
var userAgentInfo = navigator.userAgent;
var w = window,
    ver = w.opera ? (opera.version().replace(/\d$/, "") - 0) : parseFloat((/(?:IE |fox\/|ome\/|ion\/)(\d+\.\d)/.exec(userAgentInfo) || [, 0])[1]);
var ie = !!w.VBArray && Math.max(document.documentMode || 0, ver);


require.config({
    baseUrl: '/sellerpc/_resources/js/',
    paths: {
        base: dev ? 'page' : 'dist',
        jquery: ie == 8 ? 'third/jquery.1.11.1.min' : 'third/jquery.min',
        amaze: ie == 8 ? 'third/amazeui.legacy.min' : 'third/amazeui.min',
        doT: 'third/doT.min',
        moment: 'third/moment.min',
        pager: 'plugins/pager/kd-pager',
        switchs: 'plugins/switch/kd-switch',
        sortRow: 'plugins/sortable/kd-sort',
        underscore: 'third/underscore.min',
        validate: 'third/jquery.validate.min',
        placeholder: 'third/jquery.placeholder',
        copy: 'plugins/copy/copy',
        address: 'plugins/address',
        upload: 'plugins/upload/upload',
        qrcode: 'plugins/qrcode/qrcode',
        jiathis: 'third/jiathis',
        icheck: 'third/icheck.min',
        chosen: 'plugins/chosen/chosen.jquery.min',
        my97datepicker: 'plugins/My97DatePicker/WdatePicker',
        kindEditor: 'plugins/kindeditor'
    },
    map: {
        '*': {
            'css': ''
        }
    },
    shim: {
        jquery: {
            exports: 'jQuery'
        },
        amaze: {
            deps: ['jquery']
        },
        placeholder: {
            deps: ['jquery']
        },
        sortRow: {
            deps: ['jquery']
        },
        'plugins/switch/bootstrap-switch.min': {
            deps: ['jquery']
        },
        'plugins/pager/jquery.pagination': {
            deps: ['jquery']
        },
        'plugins/pager/jquery.pagination.min': {
            deps: ['jquery']
        },
        'plugins/address': {
            deps: ['jquery']
        },
        'third/amazeui.legacy': {
            deps: ['jquery']
        },
        qrcode: {
            deps: ['jquery']
        },
        moment: {
            exports: 'moment'
        },
        chosen: {
            deps: ['jquery']
        },
        icheck: {
            deps: ['jquery']
        }
    },
    urlArgs: 'v=18'
});