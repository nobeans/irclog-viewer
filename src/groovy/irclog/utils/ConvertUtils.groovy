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
                return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date)
            case ~"[0-9]{4}-[0-9]{2}-[0-9]{2}": // not strict
                return new java.text.SimpleDateFormat("yyyy-MM-dd").parse(date)
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
        if (!date) return null
        def cal = Calendar.getInstance()
        cal.time = date
        return resetTimeToOrigin(cal).time
    }

    static Calendar resetTimeToOrigin(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal
    }
}
