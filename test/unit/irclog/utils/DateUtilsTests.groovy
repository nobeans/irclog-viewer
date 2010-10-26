package irclog.utils

import grails.test.*

class DateUtilsTests extends GrailsUnitTestCase {

    void testToDate_date() {
        assert DateUtils.toDate("2010-10-18").toString() == "Mon Oct 18 00:00:00 JST 2010"
    }

    void testToDate_dateAndTime() {
        assert DateUtils.toDate("2010-10-18 01:23:45").toString() == "Mon Oct 18 01:23:45 JST 2010"
        assert DateUtils.toDate("2010-10-18 23:59:59").toString() == "Mon Oct 18 23:59:59 JST 2010"
    }

    void testToDate_invalid() {
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

    void testToCalendar_date() {
        assert DateUtils.toCalendar("2010-10-18").time.toString() == "Mon Oct 18 00:00:00 JST 2010"
    }

    void testToCalendar_dateAndTime() {
        assert DateUtils.toCalendar("2010-10-18 01:23:45").time.toString() == "Mon Oct 18 01:23:45 JST 2010"
        assert DateUtils.toCalendar("2010-10-18 23:59:59").time.toString() == "Mon Oct 18 23:59:59 JST 2010"
    }

    void testResetTimeToOrigin_Date() {
        Date date = DateUtils.toDate("2010-10-18 01:23:45")
        Date actual = DateUtils.resetTimeToOrigin(date)
        assert actual.toString() == "Mon Oct 18 00:00:00 JST 2010"
    }

    void testResetTimeToOrigin_Calendar() {
        Calendar cal = DateUtils.toCalendar("2010-10-18 01:23:45")
        Calendar actual = DateUtils.resetTimeToOrigin(cal)
        assert actual.time.toString() == "Mon Oct 18 00:00:00 JST 2010"
    }

   void testToday_realtime() {
        def before = new Date()
        def today = DateUtils.getToday()
        def after = new Date()
        assert before <= today
        assert today <= after
    }

    void testEpoch() {
        assert DateUtils.getEpoch() == new Date(0)
    }

}
