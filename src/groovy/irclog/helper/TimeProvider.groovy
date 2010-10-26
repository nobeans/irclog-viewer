package irclog.helper

import static irclog.utils.DateUtils.*

class TimeProvider {

    // If you want to debug your SUT about a system time,
    // you can set a Date instance which you like to the variable. 
    Date today

    Date getToday() {
        return expandDate((today ?: new Date()))
    }

    Date getEpoch() {
        return expandDate(new Date(0))
    }

    private static expandDate(Date date) {
        date.metaClass {
            asCalendar {
                return toCalendar(delegate)
            }
        }
        return date
    }
}
