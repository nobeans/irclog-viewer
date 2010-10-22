package irclog.utils

class ConvertUtils {

    static Integer toInteger(value, Integer defaultValue=null) {
        (value?.isInteger()) ? value.toInteger() : defaultValue
    }

    static Long toLong(value, Long defaultValue=null) {
        (value?.isLong()) ? value.toLong() : defaultValue
    }

    static Date toDate(String date) {
        switch (date) {
            case ~"[0-9]{4}-[0-9]{2}-[0-9]{2} [0-2]{1}[0-9]{1}:[0-6]{1}[0-9]{1}:[0-6]{1}[0-9]{1}": // not strict
                return Date.parse("yyyy-MM-dd HH:mm:ss", date)
            case ~"[0-9]{4}-[0-9]{2}-[0-9]{2}": // not strict
                return Date.parse("yyyy-MM-dd", date)
            default:
                throw new IllegalArgumentException("Cannot parse as 'yyyy-MM-dd HH:mm:ss' or 'yyyy-MM-dd'")
        }
    }

    static Calendar toCalendar(String date) {
        def cal = Calendar.getInstance()
        cal.time = toDate(date)
        return cal
    }

    static Date resetTimeToOrigin(Date date) {
        date?.clearTime()
        return date
    }

    static Calendar resetTimeToOrigin(Calendar cal) {
        cal?.clearTime()
        return cal
    }
}
