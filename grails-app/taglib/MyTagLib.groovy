class MyTagLib {

    static namespace = 'my'

    def dateFormat = { attrs ->
        out << new java.text.SimpleDateFormat(attrs.format).format(attrs.value)
    }

    def specifiedLink = { attrs ->
        // 指定日部分と時間部分をフォーマットする。
        def onedayDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(attrs.time)
        def time = new java.text.SimpleDateFormat("hh:mm:ss").format(attrs.time)

        // 指定日検索とチャンネルを設定し、また、ニックネームとメッセージ検索を解除する。
        def params = [*:attrs.params, channelId:"${attrs.channel.id}", period:'oneday', 'period-oneday-date':onedayDate]
        params.remove("nick")
        params.remove("message")

        out << """
            <a href="?${params.collect{"${it.key}=${it.value}"}.join("&amp;")}">
              <img src="${createLinkTo(dir:'images',file:'specified.png')}" />
            </a>
        """
    }

    def onedayLink = { attrs ->
        // 指定日部分と時間部分をフォーマットする。
        def onedayDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(attrs.time)
        def time = new java.text.SimpleDateFormat("hh:mm:ss").format(attrs.time)

        // 指定日検索を設定する。
        def params = [*:attrs.params, period:'oneday', 'period-oneday-date':onedayDate]

        out << """<a href="?${params.collect{"${it.key}=${it.value}"}.join("&amp;")}">${onedayDate}</a>&nbsp;&nbsp;${time}"""
    }

    def channelLink = { attrs ->
        def params = [*:attrs.params, channelId:"${attrs.channel.id}" ]
        out << """<a href="?${params.collect{"${it.key}=${it.value}"}.join("&amp;")}">${attrs.channel.name}</a>"""
    }

    def messageFormat = { attrs ->
        def value = attrs.value?.encodeAsHTML() ?: ''

        // http/httpsをリンクにする。
        value = value.replaceAll('(https?:\\/\\/[-_.!~*\'()a-zA-Z0-9;/?:@&=+$,%#]+)', '<a href="$1">$1</a>')

        out << value
    }

    def calendar = { attrs ->
        out << """
            <input id="${attrs.name}-text" name="${attrs.name}" type="text" value="${attrs.value ?: ''}" maxlength="10" />
            <img id="${attrs.name}-button" src="${createLinkTo(dir:'images',file:'calendar.png')}" title="${attrs.title ?: ''}" />
            <span id="${attrs.name}-calendar">${attrs.name}</span>
        """
    }

}
