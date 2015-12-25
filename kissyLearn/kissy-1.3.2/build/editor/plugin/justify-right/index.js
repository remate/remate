/*
Copyright 2013, KISSY UI Library v1.32
MIT Licensed
build time: Sep 4 17:12
*/
/**
 * justifyRight button.
 * @author yiminghe@gmail.com
 */
KISSY.add("editor/plugin/justify-right/index", function (S, Editor, justifyCenterCmd) {
    function exec() {
        var editor = this.get("editor");
        editor.execCommand("justifyRight");
        editor.focus();
    }

    function justifyRight() {

    }

    S.augment(justifyRight, {
        pluginRenderUI:function (editor) {

            justifyCenterCmd.init(editor);

            editor.addButton("justifyRight", {
                tooltip:"右对齐",
                checkable:true,
                listeners:{
                    click:exec,
                    afterSyncUI:function () {
                        var self = this;
                        editor.on("selectionChange", function () {
                            if (editor.get("mode") == Editor.SOURCE_MODE) {
                                return;
                            }
                            if (editor.queryCommandValue("justifyRight")) {
                                self.set("checked", true);
                            } else {
                                self.set("checked", false);
                            }
                        });
                    }

                },
                mode:Editor.WYSIWYG_MODE
            });

            editor.docReady(function () {
                editor.get("document").on("keydown", function (e) {
                    if (e.ctrlKey && e.keyCode == S.Node.KeyCodes.R) {
                        editor.execCommand("justifyRight");
                        e.preventDefault();
                    }
                });
            });
        }
    });

    return justifyRight;
}, {
    requires:['editor', './cmd']
});