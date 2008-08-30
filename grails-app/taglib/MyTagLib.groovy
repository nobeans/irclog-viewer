class MyTagLib {

    def dateFormat = { attrs ->
        out << new java.text.SimpleDateFormat(attrs.format).format(attrs.value)
    }

    def irclog = { attrs ->
        def value = attrs.value?.encodeAsHTML() ?: ''

        // http/https‚ğƒŠƒ“ƒN‚É‚·‚éB
        value = value.replaceAll('(https?:\\/\\/[-_.!~*\'()a-zA-Z0-9;/?:@&=+$,%#]+)', '<a href="$1">$1</a>')

        out << value
    }

}
