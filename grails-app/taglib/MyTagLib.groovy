class MyTagLib {

    def dateFormat = { attrs ->
        out << new java.text.SimpleDateFormat(attrs.format).format(attrs.value)
    }

    def irclog = { attrs ->
        def value = attrs.value?.encodeAsHTML() ?: ''

        // http/https�������N�ɂ���B
        // ���A�����Ƀq�b�g�����������n�C���C�g�\������B
        //value = value.replaceAll('(https?:\\/\\/[-_.!~*\'()a-zA-Z0-9;/?:@&=+$,%#]+)', '$1;$1')
        //value = value.replaceAll('(https?:\\/\\/[-_.!~*\'()a-zA-Z0-9;/?:@&=+$,%#]+)', '<a href="$1">$1</a>')
        attrs.highlightKeys.each { value = value.replaceAll(it, """<span class="criteria">${it}</span>""") }

        out << value
    }

}
