// プライベート名前空間としてのグローバルオブジェクトを定義
var IRCLOG;
if (!IRCLOG) IRCLOG = {};

(function() {

    // Initialize
    Event.observe(window, 'load', function() {
        highlightSearchedWord('nick');
        highlightSearchedWord('message');

        $('search-period').observe('change', handleChangePeriod);
        handleChangePeriod(); // 表示時にも実行する (イベントの強制発火ができればいいのだが)

        setupCalendar();

        setOnEnterClickButton('search-submit', "search");
    });

    // 指定した種別の文字列中に、検索した文字列があればハイライト表示する。
    // <strong>要素で挟むので、色づけはCSSで。
    var highlightSearchedWord = (function() {
        function replaceToStrong(targetValue, pattern) {
            return (targetValue.match(pattern)) ? targetValue.replace(pattern, "<strong>$1</strong>") : targetValue;
        }
        return function(type) {
            var searchKey = $('search-' + type).value;
            if (!searchKey) return;
            var pattern = new RegExp("(" + searchKey.replace(/\s+/g, '|') + ")", 'gi');
            $$('td.irclog-' + type).each(function(td) {
                td.innerHTML = $A(td.childNodes).inject('', function(resultHTML, child) {
                    if (child.tagName == 'A') {
                        var replaced = replaceToStrong(child.firstChild.nodeValue, pattern);
                        return resultHTML + '<a href="' + child.href + '" onclick="return IRCLOG.openLink(this)" target="_blank">' + replaced + '</a>';
                    } else {
                        return resultHTML + replaceToStrong(child.nodeValue, pattern);
                    }
                });
            });
        }
    })();

    var handleChangePeriod = function(event) {
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
    };

    /**
     * Enterキーを押した場合に押したいボタンを設定する.
     * @param targetButtonId 押したいボタンのid
     * @mara formName フォームの名前 値を渡さない場合は、デフォルトのフォームを設定
     * http://d.hatena.ne.jp/c9katayama/20081017/1224220628
     */
    var setOnEnterClickButton = (function() {
        function isIgnoreEnterKeySubmitElement(ele) {
            var tag = ele.tagName;
            if (tag.toLowerCase() == "textarea") {
                return true;
            }
            switch (ele.type) {
                case "button":
                case "submit":
                case "reset":
                case "image":
                case "file":
                    return true;
            }
            return false;
        }
        function isInputElement(ele){
            switch (ele.type) {
                case "text":
                case "radio":
                case "checkbox":
                case "password":
                    return true;
            }
            return false;
        }
        return function(targetButtonId, formName){
            var form = (formName == null) ? document.forms[0] : document.forms[formName];
            var targetButton = document.getElementById(targetButtonId);
            document.onkeypress = function(e) {
                e = e ? e : event; 
                var keyCode= e.charCode ? e.charCode : ((e.which) ? e.which : e.keyCode);
                var ele = e.target ? e.target : e.srcElement;
                if (Number(keyCode) == 13) { // 13=EnterKey
                    if (!isIgnoreEnterKeySubmitElement(ele)) {
                        targetButton.click();
                    }
                    return !isInputElement(ele);
                }
            }
        }
    })();

    // カレンダ設定
    var setupCalendar = function() {
        var baseId = "period-oneday-date";
        var calendarId = baseId + "-calendar";
        var buttonId = baseId + "-button";
        var textId = baseId + "-text";

        var createHandleSelect = function(calendar, toggleFunc) {
            return function(type, args, obj) {
                var date = args[0];
                var date = date[0];
                var year = date[0], month = date[1], day = date[2];
                var textField = $(textId);
                textField.value = year + "-" + (month < 10 ? "0" + month : month) + "-" + (day < 10 ? "0" + day : day);
                toggleFunc(); // このトグル関数で非表示にしないと内部状態がずれてしまうため。
            }
        }

        var calendar = IRCLOG.createCalendar(calendarId);
        var toggleFunc = IRCLOG.createCalendarToggleHandler(calendar, function() { return $(textId).value }, $(textId));
        Event.observe(buttonId, "click", toggleFunc);
        calendar.selectEvent.subscribe(createHandleSelect(calendar, toggleFunc), calendar, true);
    }

})()
