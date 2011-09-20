// Highlighting specified line
jQuery(function() {
    var previousHighlightLine = null;

    function highlightLine(targetLine) {
        // ハイライト行の移動
        targetLine.addClass('highlight');
        if (previousHighlightLine) previousHighlightLine.removeClass('highlight');
        previousHighlightLine = targetLine;
        // アンカに移動
        var hash = "#" + targetLine.attr('id');
        document.location = hash;
        // ハイライト解除ボタンの有効化
        jQuery('#clearHighlight').removeAttr('disabled');
    }

    // 特定行が直接URLで指定された場合の処理
    if (location.hash) {
        //previousHighlightLine = jQuery('tr.irclog' + location.hash).addClass('highlight');
        highlightLine(jQuery('tr.irclog' + location.hash));
        jQuery('#clearHighlight').removeAttr('disabled');
    }

    // 1行すべてハイライトする。
    jQuery('tr.irclog').click(function() {
        highlightLine(jQuery(this));
    });
    jQuery('#clearHighlight').click(function() {
        // ハイライト解除
        if (previousHighlightLine) previousHighlightLine.removeClass('highlight');
        // URLアンカクリア
        document.location = '#';
        // ボタン無効化
        jQuery('#clearHighlight').attr('disabled', 'disabled');
    });
});

// 基本種別のログのみを表示するモードとその他の種別も表示するモードをトグルで切り替える。
// 初期状態：すべて表示
jQuery(function() {
    jQuery('#toggleTypeFilter-all').click(function() {
        // ボタンのトグル
        jQuery(this).hide();
        jQuery('#toggleTypeFilter-filtered').show();
        // 種別ごとのON/OFF
        jQuery('tr.optionType').show();
    });
    jQuery('#toggleTypeFilter-filtered').click(function() {
        // ボタンのトグル
        jQuery(this).hide();
        jQuery('#toggleTypeFilter-all').show();
        // 種別ごとのON/OFF
        jQuery('tr.optionType').hide();
    });
});
