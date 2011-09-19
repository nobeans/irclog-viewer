package irclog.utils

import grails.test.mixin.*
import grails.test.mixin.support.*
import org.junit.*

@TestMixin(GrailsUnitTestMixin)
class DateUtilsTests {

    @Test
    void toDate_dateAndTime() {
        assertDate "2010-10-18 01:23:45", DateUtils.toDate("2010-10-18 01:23:45")
        assertDate "2010-10-18 23:59:59", DateUtils.toDate("2010-10-18 23:59:59")
    }

    @Test
    void toDate_date() {
        assertDate "2010-10-18 00:00:00", DateUtils.toDate("2010-10-18")
    }

    @Test
    void toDate_invalid() {
        [
            "",
            "xx",
            "2010-10-18 01:23:xx",
            "2010-10-18 1:23:45",
            "201010-18 01:23:45",
            "2010-1-18 01:23:45",
        ].each { value ->
            shouldFail(IllegalArgumentException) {
                DateUtils.toDate(value)
            }
        }
    }

    @Test
    void toCalendar_String_date() {
        String date = "2010-10-18"
        assertDate "2010-10-18 00:00:00", DateUtils.toCalendar(date)
        assertDate "1234-10-31 00:59:00", DateUtils.toCalendar(date, [year:1234, date:31, minute:59])
        assertDate "1234-12-31 23:59:45", DateUtils.toCalendar(date, [year:1234, month:11, date:31, hourOfDay:23, minute:59, second:45])
    }

    @Test
    void toCalendar_String_dateAndTime() {
        String date = "2010-10-18 01:23:45"
        assertDate "2010-10-18 01:23:45", DateUtils.toCalendar(date)
        assertDate "1234-10-31 01:59:45", DateUtils.toCalendar(date, [year:1234, date:31, minute:59])
        assertDate "1234-12-31 23:59:45", DateUtils.toCalendar(date, [year:1234, month:11, date:31, hourOfDay:23, minute:59, second:45])
    }

    @Test
    void toCalendar_Date_dateAndTime() {
        Date date = Date.parse("yyyy-MM-dd HH:mm:ss", "2010-10-18 01:23:45")
        assertDate "2010-10-18 01:23:45", DateUtils.toCalendar(date)
        assertDate "1234-10-31 01:59:45", DateUtils.toCalendar(date, [year:1234, date:31, minute:59])
        assertDate "1234-12-31 23:59:45", DateUtils.toCalendar(date, [year:1234, month:11, date:31, hourOfDay:23, minute:59, second:45])
    }

    @Test
    void resetTimeToOrigin_Date() {
        Date date = DateUtils.toDate("2010-10-18 01:23:45")
        Date actual = DateUtils.resetTimeToOrigin(date)
        assertDate "2010-10-18 00:00:00", actual
    }

    @Test
    void resetTimeToOrigin_Calendar() {
        Calendar cal = DateUtils.toCalendar("2010-10-18 01:23:45")
        Calendar actual = DateUtils.resetTimeToOrigin(cal)
        assertDate "2010-10-18 00:00:00", actual
    }

    @Test
    void getToday_realtime() {
        def before = new Date()
        def today = DateUtils.getToday()
        def after = new Date()
        assert before <= today
        assert today <= after
    }

    @Test
    void getEpoch() {
        assert DateUtils.getEpoch() == new Date(0)
    }

    private static assertDate(String expected, date) {
        assert date.format('yyyy-MM-dd HH:mm:ss') == expected
    }
}
