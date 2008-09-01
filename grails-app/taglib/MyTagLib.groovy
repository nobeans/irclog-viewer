class MyTagLib {

    static namespace = 'my'

    def dateFormat = { attrs ->
        out << new java.text.SimpleDateFormat(attrs.format).format(attrs.value)
    }

    def specifiedDateLink = { attrs ->
        // 指定日部分と時間部分をフォーマットする。
        def specifiedDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(attrs.value)
        def time = new java.text.SimpleDateFormat("hh:mm:ss").format(attrs.value)

        // 指定日検索を設定し、また、ニックネームとメッセージ検索を解除する。
        def params = [*:attrs.params, scope:'specified', 'scope-specified-date':specifiedDate]
        params.remove("nick")
        params.remove("message")

        out << """<a href="?${params.collect{"${it.key}=${it.value}"}.join("&amp;")}">${specifiedDate}</a> ${time}"""
    }

    def irclog = { attrs ->
        def value = attrs.value?.encodeAsHTML() ?: ''

        // http/httpsをリンクにする。
        value = value.replaceAll('(https?:\\/\\/[-_.!~*\'()a-zA-Z0-9;/?:@&=+$,%#]+)', '<a href="$1">$1</a>')

        out << value
    }

    def calendar = { attrs ->
        out << """
            <input id="${attrs.name}" name="${attrs.name}" type="text" value="${attrs.value ?: ''}" />
            <span id="yui-calendar">${attrs.name}</span>
        """
    }

}
