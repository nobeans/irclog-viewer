(function() {

    // Initialize
    Event.observe(window, 'load', function() {
        highlightSearchedWord('nick');
        highlightSearchedWord('message');

        $('search-period').observe('change', handleChangePeriod);
        handleChangePeriod(); // 表示時にも実行する (イベントの強制発火ができればいいのだが)
    });

    // 指定した種別の文字列中に、検索した文字列があればハイライト表示する。
    // <strong>要素で挟むので、色づけはCSSで。
    function highlightSearchedWord(type) {
        var searchKey = $('search-' + type).value;
        if (!searchKey) return;
        var pattern = new RegExp("(" + searchKey.replace(/\s+/g, '|') + ")", 'gi');
        $$('td.irclog-' + type).each(function(td) {
            td.innerHTML = $A(td.childNodes).inject('', function(resultHTML, child) {
                if (child.tagName == 'A') {
                    var replaced = replaceToStrong(child.firstChild.nodeValue, pattern);
                    return resultHTML + '<a href="' + child.href + '">' + replaced + '</a>';
                } else {
                    return resultHTML + replaceToStrong(child.nodeValue, pattern);
                }
            });
        });
    }
    function replaceToStrong(targetValue, pattern) {
        return (targetValue.match(pattern)) ? targetValue.replace(pattern, "<strong>$1</strong>") : targetValue;
    }

    function handleChangePeriod(event) {
        // イベントから取得したいが、load時にも実行したい。
        // prototype.js 1.5系では、イベントの強制Fireは対応してないので、
        // 対象が固定要素だということもあって、固定で書いてみた。
        //var ele = Event.element(event);
        var ele = $('search-period');

        var selectedValue = ele.options[ele.selectedIndex].value;
        if (selectedValue == 'oneday') {
            $('period-oneday-select').show();
            $('period-oneday-date-text').disabled = false;
        } else {
            $('period-oneday-select').hide();
            $('period-oneday-date-text').disabled = true;
        }
    }

})()
