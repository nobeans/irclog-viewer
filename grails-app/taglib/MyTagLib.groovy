class MyTagLib {

    def dateFormat = { attrs ->
        out << new java.text.SimpleDateFormat(attrs.format)
        .format(attrs.value)
    }

}
