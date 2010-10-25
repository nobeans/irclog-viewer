package irclog.helper

import grails.test.*
import static irclog.utils.ConvertUtils.*

class TimeProviderTests extends GrailsUnitTestCase {

    void testToday_realtime() {
        def before = new Date()
        def today = new TimeProvider().today
        def after = new Date()
        assert before < today
        assert today < after
    }

    void testToday_replacement() {
        def provider = new TimeProvider()
        def replacement = toDate("2010-12-31 23:59:59")
        provider.today = replacement
        assert provider.today == replacement
    }

    void testEpoch() {
        assert new TimeProvider().epoch == new Date(0)
    }

    void testToday_asCalender() {
        def provider = new TimeProvider()
        def replacement = toDate("2010-12-31 23:59:59")
        provider.today = replacement
        def actual = provider.today.asCalendar()
        assert actual == toCalendar("2010-12-31 23:59:59")
        assert actual in Calendar
    }

}
