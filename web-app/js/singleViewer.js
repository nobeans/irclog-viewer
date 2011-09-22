jQuery(function($) {

    // Highlighting specified line
    (function() {
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
            $('#clearHighlight').removeAttr('disabled');
        }

        // 特定行が直接URLで指定された場合の処理
        if (location.hash) {
            //previousHighlightLine = $('tr.irclog' + location.hash).addClass('highlight');
            highlightLine($('tr.irclog' + location.hash));
            $('#clearHighlight').removeAttr('disabled');
        }

        // 1行すべてハイライトする。
        $('tr.irclog').click(function() {
            highlightLine($(this));
        });
        $('#clearHighlight').click(function() {
            // ハイライト解除
            if (previousHighlightLine) previousHighlightLine.removeClass('highlight');
            // URLアンカクリア
            document.location = '#';
            // ボタン無効化
            $('#clearHighlight').attr('disabled', 'disabled');
        });
    })();

    // 基本種別のログのみを表示するモードとその他の種別も表示するモードをトグルで切り替える。
    // 初期状態：すべて表示
    (function() {
        $('#toggleTypeFilter-all').click(function() {
            // ボタンのトグル
            $(this).hide();
            $('#toggleTypeFilter-filtered').show();
            // 種別ごとのON/OFF
            $('tr.optionType').show();
        });
        $('#toggleTypeFilter-filtered').click(function() {
            // ボタンのトグル
            $(this).hide();
            $('#toggleTypeFilter-all').show();
            // 種別ごとのON/OFF
            $('tr.optionType').hide();
        });
    })();

    // Calendar
    (function() {
        function refresh() {
            var targetDate = $(".datepicker").val().replace(/-/g, '');
            var channel = $('#select-single').val().replace(/^#/, '');
            var url = '/irclog/the/' + channel + '/' + targetDate + '/';
            document.location = url;
        }

        $('#select-single').change(function() {
            refresh();
        });

        $(".datepicker").datepicker({
            dateFormat: 'yy-mm-dd',
            showOn: "button",
            buttonImage: "/irclog/images/calendar.png",
            buttonImageOnly: true,
            showButtonPanel: true,
            showOtherMonths: true,
            selectOtherMonths: true,
        }).change(function() {
            refresh();
        });
    })();
});
