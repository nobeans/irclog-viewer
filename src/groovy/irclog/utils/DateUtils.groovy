package irclog.utils

class DateUtils {

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

    static Date getToday() {
        return new Date()
    }

    static Date getEpoch() {
        return new Date(0)
    }

}
