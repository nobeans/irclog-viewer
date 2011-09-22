// jQuery.kill_referrer  Version 1.0x (customized)
// http://logic.moo.jp/memo.php/archive/569

// Initialization
jQuery.kill_referrer = {
    init: function() {
        for (module in jQuery.kill_referrer) {
            if (jQuery.kill_referrer[module].init) {
                jQuery.kill_referrer[module].init();
            }
        }
    }
};

jQuery(document).ready(jQuery.kill_referrer.init);

// rewrite
jQuery.kill_referrer.rewrite = {
    init: function() {
        jQuery('a[href^=http]').click(this.click);
    },
    click: function() {
        var url = $(this).attr('href');
        // for IE
        if (navigator.userAgent.indexOf('MSIE',0) != -1) {
            var target = $(this).attr('target');
            var blank_flag = 0;
            if (target=='_blank') {
                subwin = window.open('','','location=yes, menubar=yes, toolbar=yes, status=yes, resizable=yes, scrollbars=yes,');
                subwin.document.open();
                subwin.document.write('<meta http-equiv="refresh" content="0;url='+url+'">');
                subwin.document.close();
            } else {
                document.open();
                document.write('<meta http-equiv="refresh" content="0;url='+url+'">');
                document.close();
            }
            return false;
        }
        // for Safari,Chrome,Firefox
        else if (!url.match(/data:text\/html;charset=utf-8/)) {
            var html = 'javascript:document.write(\'<meta http-equiv="refresh" content="0;url='+url+'">\');';
            $(this).attr('href', 'data:text/html;charset=utf-8,' + encodeURIComponent(html));
        }
    }
};

