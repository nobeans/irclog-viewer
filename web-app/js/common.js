// プライベート名前空間としてのグローバルオブジェクトを定義
var IRCLOG;
if (!IRCLOG) IRCLOG = {};

jQuery(function() {

    // 指定されたURLに遷移する。
    IRCLOG.goto = function(url) {
        document.location = url;
    }

    // Refererをつけずにリンクする。
    // FIXME:この方法ではOperaでは対応できない。
    // (参考)
    //   http://qootas.org/blog/archives/2004/11/referrer.html
    //   http://www.teria.com/~koseki/memo/referrer/index.html
    IRCLOG.openLink = function(anchor) {
        var url = anchor.href;
        var w = window.open();
        w.document.write('<meta http-equiv="refresh" content="0;url=' + url + '">');
        w.document.close();
        return false;
    };

    jQuery(".help-button").click(function() {
        jQuery("#" + this.id + "-caption").toggle();
    });

    jQuery(".defaultFocus").focus();
});
