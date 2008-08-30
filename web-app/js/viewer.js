(function() {
    Event.observe(window, 'load', function() {
        highlightSearchedWord('nick');
        highlightSearchedWord('message');
    });

    // 指定した種別の文字列中に、検索した文字列があればハイライト表示する。
    // <strong>要素で挟むので、色づけはCSSで。
    function highlightSearchedWord(type) {
        var pattern = new RegExp("(" + $('search-' + type).value.replace(/\s+/g, '|') + ")", 'g');
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
})()
