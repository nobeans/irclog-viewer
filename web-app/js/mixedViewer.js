jQuery(function() {
    // 指定した種別の文字列中に、検索した文字列があればハイライト表示する。
    function highlightSearchedWord(type) {
        var searchKey = jQuery('#search-' + type).val();
        if (!searchKey) return;

        // <strong>要素で挟んでおいて、色づけはCSSで。
        var pattern = new RegExp("(" + searchKey.replace(/\s+/g, '|') + ")", 'gi');
        var replaceToStrong = function(text) {
            return text.replace(pattern, "<strong>$1</strong>");
        }

        jQuery('td.irclog-' + type).each(function() {
            var td = jQuery(this);
            console.log(td.html());
            var children = td.children();
            if (children.size() > 0) {
                children.each(function() {
                    var anchor = jQuery(this);
                    anchor.html(replaceToStrong(anchor.html()));
                });
            } else {
                td.html(replaceToStrong(td.html()));
            }
        });
    }
    highlightSearchedWord('nick');
    highlightSearchedWord('message');
});

jQuery(function() {
    // enter = submit
    jQuery("input,select").keydown(function(e) {
        if (e.keyCode == 13) {
            jQuery("form[name=search]").submit();
            //jQuery("#search-submit").click();
            return false;
        }
    });
});

// Calendar to select a day
jQuery(function() {
    jQuery.datepicker.setDefaults(jQuery.datepicker.regional["ja"]);
    jQuery("input.datepicker").datepicker({
        dateFormat: 'yy-mm-dd',
        showOn: "button",
        buttonImage: "/irclog/images/calendar.png",
        buttonImageOnly: true,
        showButtonPanel: true,
        showOtherMonths: true,
        selectOtherMonths: true,
    });

    function updateDatepicker() {
        if (jQuery("#search-period").val() === "oneday") {
            jQuery("#period-oneday-select").show();
            jQuery("input.datepicker").removeAttr('disabled');
        } else {
            jQuery("#period-oneday-select").hide();
            jQuery("input.datepicker").attr('disabled', 'disabled');
        }
    }
    updateDatepicker(); // just after loading
    jQuery("#search-period").change(updateDatepicker);
});

