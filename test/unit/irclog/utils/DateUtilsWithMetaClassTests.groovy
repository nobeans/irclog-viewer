package irclog.utils

import grails.test.*

class DateUtilsWithMetaClassTests extends GrailsUnitTestCase {

    void setUp() {
        super.setUp()
        DateUtils.expandMetaClass()
    }

    void testToDate_date() {
        assert "2010-10-18".toDate().toString() == "Mon Oct 18 00:00:00 JST 2010"
    }

    void testToDate_dateAndTime() {
        assert "2010-10-18 01:23:45".toDate().toString() == "Mon Oct 18 01:23:45 JST 2010"
        assert "2010-10-18 23:59:59".toDate().toString() == "Mon Oct 18 23:59:59 JST 2010"
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
                value.toDate()
            }
        }
    }

    void testToCalendar_date() {
        assert "2010-10-18".toCalendar().time.toString() == "Mon Oct 18 00:00:00 JST 2010"
    }

    void testToCalendar_dateAndTime() {
        assert "2010-10-18 01:23:45".toCalendar().time.toString() == "Mon Oct 18 01:23:45 JST 2010"
        assert "2010-10-18 23:59:59".toCalendar().time.toString() == "Mon Oct 18 23:59:59 JST 2010"
    }

    void testResetTimeToOrigin_Date() {
        Date date = DateUtils.toDate("2010-10-18 01:23:45")
        Date actual = date.resetTimeToOrigin()
        assert actual.toString() == "Mon Oct 18 00:00:00 JST 2010"
    }

    void testResetTimeToOrigin_Calendar() {
        Calendar cal = DateUtils.toCalendar("2010-10-18 01:23:45")
        Calendar actual = cal.resetTimeToOrigin()
        assert actual.time.toString() == "Mon Oct 18 00:00:00 JST 2010"
    }

}
