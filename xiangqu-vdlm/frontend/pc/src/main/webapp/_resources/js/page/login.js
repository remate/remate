var KD = KD || {};

KD.login = {
    init: function() {
        $('input').placeholder();
        $('.checkbox').iCheck();

        this.renderCode();

        this.callLogin();
        this.loginEvent();

        this.commonEvent();

        this.callCode();
        this.callCopyright();

        this.callReg();
        this.regEvent();

        this.callForget();
        this.forgetEvent();

        this.callAlert();

        this.initForm();
    },

    initForm: function() {
        //cookie
        $('.username').val($.cookie('username'));
        // $('.login-part .password').val($.cookie('password'));
    },

    save: function(username) {
        // var password = $('.login-part .password').val();
        $.cookie('username', username, {
            expires: 7
        });
        // $.cookie('password', password, { expires: 7 });
    },

    verifyPhone: function(num) {
        var phone = /^1\d{10}$/;

        return phone.test(num);
    },

    verifyCaptcha: function(code) {
        var captcha = /^\d{6}$/;

        return captcha.test(code);
    },

    verifyPassword: function(pwd) {
        var password = /^.{6,50}$/;

        return password.test(pwd);
    },

    trimSpace: function(str) {
        return str.replace(/\s/g,'');
    },

    verifyReg: function(username, isSendCaptcha) {
        var that = this;

        var divReg = $('.reg-part');
        var divForget = $('.forget-part');
        var btnSend = $('.common-part .send');

        var url = window.host + '/registered';
        var data = {
            mobile: username
        };

        that.postMethod(url, data, function(result) {
            if (typeof(result) === 'object') {
                switch (result.errorCode) {
                    case 200:
                        {
                            if (result.data) {
                                if (divReg.is(':visible')) {
                                    that.alertMethod('手机号码已被注册使用！');
                                    btnSend.removeClass('btn-enabled').addClass('btn-disabled');
                                } else {
                                    if ((typeof isSendCaptcha === 'boolean') && isSendCaptcha) {
                                        that.sendEvent();
                                    } else {
                                        btnSend.removeClass('btn-disabled').addClass('btn-enabled');
                                    }
                                }
                            } else {
                                if (divForget.is(':visible')) {
                                    that.alertMethod('手机号码未被注册使用！');
                                    btnSend.removeClass('btn-enabled').addClass('btn-disabled');
                                } else {
                                    if ((typeof isSendCaptcha === 'boolean') && isSendCaptcha) {
                                        that.sendEvent();
                                    } else {
                                        btnSend.removeClass('btn-disabled').addClass('btn-enabled');
                                    }
                                }
                            }
                            break;
                        }
                    default:
                        {
                            if ((typeof isSendCaptcha === 'boolean') && isSendCaptcha) {
                                // [VD-1096]
                                btnSend.removeClass('btn-disabled').addClass('btn-enabled');
                            }
                            that.alertMethod(result.moreInfo);
                        }
                }
            }
        });
    },

    verifyForm: function(that, txtUserName, txtCaptcha, txtPassword, txtRePassword) {
        var username, code, password, rePassword;

        username = txtUserName.val();
        code = txtCaptcha.val();
        password = txtPassword.val();
        rePassword = txtRePassword.val();

        if (username.length < 1) {
            that.alertMethod('手机号码不能为空。');
            return false;
        }
        if (!that.verifyPhone(txtUserName.val())) {
            that.alertMethod('手机号码格式不对。');
            return false;
        }
        if (code.length < 1) {
            that.alertMethod('验证码不能为空。');
            return false;
        }
        if (!that.verifyCaptcha(txtCaptcha.val())) {
            that.alertMethod('验证码格式不对。');
            return false;
        }
        if (password.length < 1) {
            that.alertMethod('密码不能为空。');
            return false;
        }
        if (!that.verifyPassword(password)) {
            that.alertMethod('请输入至少6位的密码。');
            return false;
        }
        if (rePassword.length < 1) {
            that.alertMethod('重复密码不能为空。');
            return false;
        }
        if (password !== rePassword) {
            that.alertMethod('两次输入的密码不同。');
            return false;
        }

        return true;
    },

    postMethod: function(url, data, callback) {
        var that = this;

        $.ajax({
            url: url,
            data: data,
            type: 'POST',
            dataType: 'JSON',
            success: function(res) {
                callback(res);
            },
            error: function() {
                that.alertMethod('服务器暂时没有响应，请稍后重试。');
                callback(-1);
            },
            complete: function() {
                callback(0);
            }
        });
    },

    loginEvent: function() {
        var that = this;

        var btnConfirm = $('.login');
        var txtUserName = $('.username');
        var txtPassword = $('.login-part .password');

        var isProcessing = false;

        txtPassword.on('keyup', function(ev){
            var evt = ev || event;
            if (evt.keyCode === 13) {
                btnConfirm.trigger('click');
                return false;
            }
        });

        btnConfirm.on('click', function() {
            var username, password;

            username = txtUserName.val();
            password = txtPassword.val();

            if (username.length < 1) {
                that.alertMethod('手机号码不能为空。');
                return false;
            }
            if (!that.verifyPhone(username)) {
                that.alertMethod('手机号码格式不正确。');
                return false;
            }
            if (password.length < 1) {
                that.alertMethod('密码不能为空。');
                return false;
            }
            if (!that.verifyPassword(password)) {
                that.alertMethod('请输入至少6位的密码。');
                return false;
            }

            if (isProcessing) {
                return false;
            } else {
                isProcessing = true;
            }

            var url = window.host + '/signin_check';
            var data = {
                u: username,
                p: CryptoJS.MD5(password).toString()
            };

            that.postMethod(url, data, function(result) {
                if (typeof(result) === 'object') {
                    switch (result.errorCode) {
                        case 200:
                            { // 密码正确
                                that.save(username);
                                location.href = '/sellerpc/products/myproduct';
                                break;
                            }
                        default:
                            {
                                that.alertMethod('登录失败，手机号码或密码错误!');
                                break;
                            }
                    }
                }
                isProcessing = false;
            });

            return false;
        });
    },

    commonEvent: function() {
        var that = this;

        var txtUserName = $('.username');
        var txtCaptcha = $('.common-part .captcha');
        var btnSend = $('.common-part .send');
        var txtPassword = $('.common-part .new-password');
        var txtRePassword = $('.common-part .re-password');
        var divReg = $('.reg-part');
        var divForget = $('.forget-part');

        txtUserName.add(txtCaptcha).numeric({
            decimal: false,
            negative: false,
            decimalPlaces: -1
        });

        txtUserName.on('keyup', function() {
            var txtUserName = $('.username');
            var btnSend = $('.common-part .send');

            if ((txtUserName.length > 0) && that.verifyPhone(txtUserName.val())) {
                btnSend.removeClass('btn-disabled').addClass('btn-enabled');
            } else {
                btnSend.removeClass('btn-enabled').addClass('btn-disabled');
            }

            return false;
        });

        txtUserName.on('blur', function() {
            var username = txtUserName.val();
            var divLogin = $('.login-part');

            if ((username.length < 1) || divLogin.is(':visible')) {
                return false;
            }

            if (that.verifyPhone(username)) {
                that.verifyReg(username);
            } else {
                btnSend.removeClass('btn-enabled').addClass('btn-disabled');
                that.alertMethod('手机号码格式不正确，请重新输入。');
            }

            return false;
        });

        txtCaptcha.on('blur', function() {
            var code = txtCaptcha.val();
            var username = txtUserName.val();

            if (code.length < 1) {
                return false;
            }

            if ((that.verifyCaptcha(code)) && (that.verifyPhone(username))) {
                var url = window.host;
                var data = {
                    mobile: username,
                    smsCode: code
                };

                if (divReg.is(':visible')) {
                    url = url + '/register/verify';
                } else if (divForget.is(':visible')) {
                    url = url + '/pwdModify/verify';
                } else {
                    return false;
                }

                that.postMethod(url, data, function(result) {
                    if (typeof(result) === 'object') {
                        switch (result.errorCode) {
                            case 200:
                                {
                                    // that.alertMethod('验证码正确！');
                                    break;
                                }
                            case -1:
                                {
                                    that.alertMethod('验证码不正确！');
                                    break;
                                }
                            default:
                                {
                                    that.alertMethod(result.moreInfo);
                                }
                        }
                    }
                });
            } else {
                that.alertMethod('验证码格式不正确！');
            }
        });

        $('body').on('click', '.send.btn-enabled', function() {
            var username = txtUserName.val();

            btnSend.removeClass('btn-count-state').removeClass('btn-enabled').addClass('btn-disabled');

            if (username.length < 1) {
                that.alertMethod('手机号码不能为空。');
                return false;
            }

            if (that.verifyPhone(username)) {
                that.verifyReg(username, true);
            } else {
                that.alertMethod('手机号码格式不正确，请重新输入。');
            }

            return false;
        });

        txtPassword.add(txtRePassword).on('keydown', function(ev){
            var evt = ev || event;
            if (evt.keyCode === 32) {
                return false;
            }
        }).on('paste',function(){
            return false;
        });
    },

    sendEvent: function() {
        var that = this;

        var txtUserName = $('.username');
        var btnSend = $('.common-part .send');
        var divReg = $('.reg-part');
        var divForget = $('.forget-part');

        var waitTime = 60;

        btnSend.removeClass('btn-enabled').removeClass('btn-disabled').addClass('btn-count-state');

        function countdownTimer() {
            if (waitTime === 0) {
                if ((txtUserName.length > 0) && that.verifyPhone(txtUserName.val())) {
                    btnSend.removeClass('btn-count-state').addClass('btn-enabled').text('发送验证码');
                } else {
                    btnSend.removeClass('btn-count-state').addClass('btn-disabled').text('发送验证码');
                }
                waitTime = 60;
            } else {
                btnSend.text('剩余' + waitTime + 's...');
                waitTime--;
                setTimeout(function() {
                    countdownTimer();
                }, 1000);
            }
        }

        countdownTimer();

        var username = txtUserName.val();

        var url = window.host;
        var data = {
            mobile: username
        };

        if (divReg.is(':visible')) {
            url = url + '/send-sms-code';
        } else if (divForget.is(':visible')) {
            url = url + '/forget-pwd';
        } else {
            return false;
        }

        that.postMethod(url, data, function(result) {
            if (typeof(result) === 'object') {
                switch (result.errorCode) {
                    case 200:
                        {
                            //that.alertMethod('验证码发送成功，请查收短信。');
                            break;
                        }
                    default:
                        {
                            // that.alertMethod('验证码发送失败！');
                            that.alertMethod(result.moreInfo);
                        }
                }
            }
        });
    },

    regEvent: function() {
        var that = this;
        var btnConfirm = $('.reg');

        var chkCopyright = $('.reg-part .checkbox');
        var txtUserName = $('.username');
        var txtCaptcha = $('.common-part .captcha');
        var txtPassword = $('.common-part .new-password');
        var txtRePassword = $('.common-part .re-password');

        var isProcessing = false;

        btnConfirm.on('click', function() {
            var username, code, password, rePassword;

            username = txtUserName.val();
            code = txtCaptcha.val();
            password = that.trimSpace(txtPassword.val());
            rePassword = that.trimSpace(txtRePassword.val());

            if (!that.verifyForm(that, txtUserName, txtCaptcha, txtPassword, txtRePassword)) {
                return false;
            }

            if (!chkCopyright.is(':checked')) {
                that.alertMethod('请同意《快快开店服务协议》。');
                return false;
            }

            if (isProcessing) {
                return false;
            } else {
                isProcessing = true;
            }

            var url = window.host + '/register/create';
            var data = {
                mobile: username,
                smsCode: code,
                pwd: CryptoJS.MD5(password).toString()
            };

            that.postMethod(url, data, function(result) {
                if (typeof(result) === 'object') {
                    switch (result.errorCode) {
                        case 200:
                            {
                                that.alertMethod('注册成功！');
                                $('.common-part').hide();
                                $('.reg-part').hide();
                                $('.forget-part').hide();
                                $('.login-part').fadeIn();
                                break;
                            }
                        case -1:
                            {
                                that.alertMethod(result.moreInfo);
                                break;
                            }
                        default:
                            {
                                that.alertMethod(result.moreInfo);
                            }
                    }
                }
                isProcessing = false;
            });

            return false;
        });
    },

    forgetEvent: function() {
        var that = this;
        var btnConfirm = $('.modify');

        var txtUserName = $('.username');
        var txtCaptcha = $('.common-part .captcha');
        var txtPassword = $('.common-part .new-password');
        var txtRePassword = $('.common-part .re-password');

        var isProcessing = false;

        btnConfirm.on('click', function() {
            var username, code, password, rePassword;

            username = txtUserName.val();
            code = txtCaptcha.val();
            password = that.trimSpace(txtPassword.val());
            rePassword = that.trimSpace(txtRePassword.val());

            if (!that.verifyForm(that, txtUserName, txtCaptcha, txtPassword, txtRePassword)) {
                return false;
            }

            if (isProcessing) {
                return false;
            } else {
                isProcessing = true;
            }

            var url = window.host + '/validate-forget-pwd';
            var data = {
                mobile: username,
                smsCode: code,
                pwd: CryptoJS.MD5(password).toString()
            };

            that.postMethod(url, data, function(result) {
                if (typeof(result) === 'object') {
                    switch (result.errorCode) {
                        case 200:
                            {
                                that.alertMethod('修改密码成功！');
                                $('.common-part').hide();
                                $('.reg-part').hide();
                                $('.forget-part').hide();
                                $('.login-part').fadeIn();
                                break;
                            }
                        case -1:
                            {
                                that.alertMethod(result.moreInfo);
                                break;
                            }
                        default:
                            {
                                that.alertMethod(result.moreInfo);
                            }
                    }
                }
                isProcessing = false;
            });

            return false;
        });
    },

    callCode: function() {
        var caller = $('.qr-code');
        var target = $('.code-box');

        caller.on('mouseenter', function() {
            target.show();
        }).on('mouseleave', function() {
            target.fadeOut();
        });

        // target.add(caller).on('click', function(ev) {
        //     ev.stopPropagation();
        // });

        // $(document).on('click', function() {
        //     target.fadeOut();
        // });
    },

    alertMethod: function(alertText) {
        var dimmer = $('.model-dimmer');
        var model = $('.model-alert');
        var title = $('.model-alert .model-title');
        var body = $('.model-alert .model-body');
        var button = $('.model-alert .model-button');

        title.text('提示');
        body.text(alertText);
        button.text('确定');

        dimmer.show();
        model.fadeIn();
    },

    callAlert: function() {
        var dimmer = $('.model-dimmer');
        var model = $('.model-alert');
        var button = $('.model-alert .model-button');

        model.on('click', function(ev) {
            ev.stopPropagation();
        });

        dimmer.add(button).on('click', function() {
            dimmer.fadeOut();
            model.fadeOut();
            return false;
        });
    },

    callCopyright: function() {
        var caller = $('.kkkd-copyright');
        var target = $('.copyright-box');

        caller.on('click', function() {
            if (target.is(':visible')) {
                target.fadeOut();
            } else {
                target.fadeIn();
            }
            return false;
        });

        target.on('click', function(ev) {
            ev.stopPropagation();
        });

        $(document).on('click', function() {
            target.fadeOut();
        });
    },

    callReg: function() {
        var that = this;

        $('.login-part .tip-doing').on('click', function() {
            var txtUserName = $('.username');
            var btnSend = $('.common-part .send');

            if ((txtUserName.length > 0) && that.verifyPhone(txtUserName.val())) {
                if (!btnSend.hasClass('btn-count-state')) {
                    btnSend.removeClass('btn-disabled').addClass('btn-enabled');
                }
            } else {
                if (!btnSend.hasClass('btn-count-state')) {
                    btnSend.removeClass('btn-enabled').addClass('btn-disabled');
                }
            }

            $('.common-part .captcha').val('');
            $('.common-part .new-password[type=password]').val('');
            $('.common-part .re-password[type=password]').val('');
            $('.reg-part .checkbox').iCheck('check');

            $('.login-part').hide();
            $('.forget-part').hide();
            $('.common-part').fadeIn();
            $('.reg-part').fadeIn();

            return false;
        });
    },

    callLogin: function() {
        $('.reg-part .tip-doing, .forget-part .tip-doing').on('click', function() {
            $('.login-part .password[type=password]').val('');

            $('.common-part').hide();
            $('.reg-part').hide();
            $('.forget-part').hide();
            $('.login-part').fadeIn();

            return false;
        });
    },

    callForget: function() {
        var that = this;

        $('.forget').on('click', function() {
            var txtUserName = $('.username');
            var btnSend = $('.common-part .send');

            if ((txtUserName.length > 0) && that.verifyPhone(txtUserName.val())) {
                if (!btnSend.hasClass('btn-count-state')) {
                    btnSend.removeClass('btn-disabled').addClass('btn-enabled');
                }
            } else {
                if (!btnSend.hasClass('btn-count-state')) {
                    btnSend.removeClass('btn-enabled').addClass('btn-disabled');
                }
            }

            $('.common-part .captcha').val('');
            $('.common-part .new-password[type=password]').val('');
            $('.common-part .re-password[type=password]').val('');

            $('.reg-part').hide();
            $('.login-part').hide();
            $('.common-part').fadeIn();
            $('.forget-part').fadeIn();

            return false;
        });
    },

    renderCode: function() {
        var iRender = '';
        try {
            document.createElement('canvas').getContext('2d');
            iRender = 'canvas';
        } catch (e) {
            iRender = 'table';
        }

        var iosAddress = 'http://a.app.qq.com/o/simple.jsp?pkgname=com.ouertech.android.hotshop',
            androidAddress = 'http://a.app.qq.com/o/simple.jsp?pkgname=com.ouertech.android.hotshop';

        $('.code-box .ios dt').qrcode({
            render: iRender,
            text: iosAddress,
            width: 104,
            height: 104
        });
        $('.code-box .android dt').qrcode({
            render: iRender,
            text: androidAddress,
            width: 104,
            height: 104
        });
    }
};

$(function() {
    KD.login.init();
});
