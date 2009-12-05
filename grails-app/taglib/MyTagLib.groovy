class MyTagLib {

    static namespace = 'my'

    def dateFormat = { attrs ->
        out << new java.text.SimpleDateFormat(attrs.format).format(attrs.value)
    }

    def specifiedLink = { attrs ->
        // �w��������Ǝ��ԕ������t�H�[�}�b�g����B
        def onedayDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(attrs.time)
        def time = new java.text.SimpleDateFormat("hh:mm:ss").format(attrs.time)

        // �w��������ƃ`�����l����ݒ肵�A�܂��A�j�b�N�l�[���ƃ��b�Z�[�W��������������B
        def params = [*:attrs.params, channelId:"${attrs.channel.id}", period:'oneday', 'period-oneday-date':onedayDate]
        params.remove("nick")
        params.remove("message")

        out << """<a href="?${params.collect{"${it.key}=${it.value}"}.join("&amp;")}">${g.message(code:'viewer.list.specified.link')}</a>"""
    }

    def onedayLink = { attrs ->
        // �w��������Ǝ��ԕ������t�H�[�}�b�g����B
        def onedayDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(attrs.time)
        def time = new java.text.SimpleDateFormat("hh:mm:ss").format(attrs.time)

        // �w���������ݒ肷��B
        def params = [*:attrs.params, period:'oneday', 'period-oneday-date':onedayDate]

        out << """<a href="?${params.collect{"${it.key}=${it.value}"}.join("&amp;")}">${onedayDate}</a>&nbsp;&nbsp;${time}"""
    }

    def channelLink = { attrs ->
        def params = [*:attrs.params, channelId:"${attrs.channel.id}" ]
        out << """<a href="?${params.collect{"${it.key}=${it.value}"}.join("&amp;")}">${attrs.channel.name}</a>"""
    }

    def messageFormat = { attrs ->
        def value = attrs.value?.encodeAsHTML() ?: ''

        // http/https�������N�ɂ���B
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
