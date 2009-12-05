class MyTagLib {

    static namespace = 'my'

    def dateFormat = { attrs ->
        out << new java.text.SimpleDateFormat(attrs.format).format(attrs.value)
    }

    def specifiedDateLink = { attrs ->
        // �w��������Ǝ��ԕ������t�H�[�}�b�g����B
        def specifiedDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(attrs.value)
        def time = new java.text.SimpleDateFormat("hh:mm:ss").format(attrs.value)

        // �w���������ݒ肵�A�܂��A�j�b�N�l�[���ƃ��b�Z�[�W��������������B
        def params = [*:attrs.params, scope:'specified', 'scope-specified-date':specifiedDate]
        params.remove("nick")
        params.remove("message")

        out << """<a href="?${params.collect{"${it.key}=${it.value}"}.join("&amp;")}">${specifiedDate}</a> ${time}"""
    }

    def irclog = { attrs ->
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
