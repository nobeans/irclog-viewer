jQuery(function($) {

    // Highlight of search key
    // 指定した種別の文字列中に、検索した文字列があればハイライト表示する。
    (function() {
        function highlightSearchedWord(type) {
            var searchKey = $('#search-' + type).val();
            if (!searchKey) return;

            var keys = searchKey.replace(/(^\s+)|(\s+$)/g, "").replace(/\s+/g," ").replace(/\$/g, "\$").split(' ');
            $(keys).each(function() {
                $('td.irclog-' + type).highlight(this);
            });
        }
        highlightSearchedWord('nick');
        highlightSearchedWord('message');
    })();

    // Enter = submit
    (function() {
        $("input,select").keydown(function(e) {
            if (e.keyCode == 13) {
                $("form[name=search]").submit();
                return false;
            }
        });
    })();

    // Calendar to select a day
    (function() {
        $.datepicker.setDefaults($.datepicker.regional["ja"]);
        $("input.datepicker").datepicker({
            dateFormat: 'yy-mm-dd',
            showOn: "button",
            buttonImage: "/irclog/images/calendar.png",
            buttonImageOnly: true,
            showButtonPanel: true,
            showOtherMonths: true,
            selectOtherMonths: true,
        });

        function updateDatepicker() {
            if ($("#search-period").val() === "oneday") {
                $("#period-oneday-select").show();
                $("input.datepicker").removeAttr('disabled');
            } else {
                $("#period-oneday-select").hide();
                $("input.datepicker").attr('disabled', 'disabled');
            }
        }
        updateDatepicker(); // just after loading
        $("#search-period").change(updateDatepicker);
    })();
});

