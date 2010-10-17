package irclog.utils

class ConvertUtils {

    static toInteger(value, Integer defaultValue=null) {
        (value?.isInteger()) ? value.toInteger() : defaultValue
    }

    static toLong(value, Long defaultValue=null) {
        (value?.isLong()) ? value.toLong() : defaultValue
    }

    static toDate(String date) {
        switch (date) {
            case ~"[0-9]{4}-[0-9]{2}-[0-9]{2} [0-2]{1}[0-9]{1}:[0-6]{1}[0-9]{1}:[0-6]{1}[0-9]{1}": // not strict
                return new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(date)
            case ~"[0-9]{4}-[0-9]{2}-[0-9]{2}": // not strict
                return new java.text.SimpleDateFormat("yyyy-MM-dd").parse(date)
            default:
                throw new IllegalArgumentException("Cannot parse as 'yyyy-MM-dd hh:mm:ss' or 'yyyy-MM-dd'")
        }
    }
}
