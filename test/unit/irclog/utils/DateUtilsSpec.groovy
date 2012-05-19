package irclog.utils

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification
import spock.lang.Unroll

@TestMixin(GrailsUnitTestMixin)
class DateUtilsSpec extends Specification {

    @Unroll
    def "toDate: '#dateString' as String is converted to '#expected'"() {
        expect:
        DateUtils.toDate(dateString) == expected

        where:
        dateString            | expected
        "2010-10-18 23:59:59" | getDate("2010-10-18 23:59:59")
        "2010-10-18"          | getDate("2010-10-18 00:00:00")
    }

    @Unroll
    def "toDate: '#dateString' as String is failed to convert"() {
        expect:
        shouldFail(IllegalArgumentException) {
            DateUtils.toDate(dateString)
        }

        where:
        dateString << [
            "",
            "xx",
            "2010-10-18 01:23:xx",
            "2010-10-18 1:23:45",
            "201010-18 01:23:45",
            "2010-1-18 01:23:45",
        ]
    }

    @Unroll
    def "toCalendar: '#dateString' as String is converted to '#expected'"() {
        expect:
        DateUtils.toCalendar(dateString, extraMap) == expected.toCalendar()

        where:
        dateString            | extraMap                                                                 | expected
        "2010-10-18"          | null                                                                     | getDate("2010-10-18 00:00:00")
        "2010-10-18"          | [year: 1234, date: 31, minute: 59]                                       | getDate("1234-10-31 00:59:00")
        "2010-10-18"          | [year: 1234, month: 11, date: 31, hourOfDay: 23, minute: 59, second: 45] | getDate("1234-12-31 23:59:45")
        "2010-10-18 01:23:45" | null                                                                     | getDate("2010-10-18 01:23:45")
        "2010-10-18 01:23:45" | [year: 1234, date: 31, minute: 59]                                       | getDate("1234-10-31 01:59:45")
        "2010-10-18 01:23:45" | [year: 1234, month: 11, date: 31, hourOfDay: 23, minute: 59, second: 45] | getDate("1234-12-31 23:59:45")
    }

    @Unroll
    def "toCalendar: '#dateString' as Date is converted to '#expected'"() {
        expect:
        DateUtils.toCalendar(dateString, extraMap) == expected.toCalendar()

        where:
        dateString                     | extraMap                                                                 | expected
        getDate("2010-10-18 01:23:45") | null                                                                     | getDate("2010-10-18 01:23:45")
        getDate("2010-10-18 01:23:45") | [year: 1234, date: 31, minute: 59]                                       | getDate("1234-10-31 01:59:45")
        getDate("2010-10-18 01:23:45") | [year: 1234, month: 11, date: 31, hourOfDay: 23, minute: 59, second: 45] | getDate("1234-12-31 23:59:45")
    }

    @Unroll
    def "resetTimeToOrigin: #before is reset to #after"() {
        expect:
        DateUtils.resetTimeToOrigin(before) == after

        where:
        before                                     | after
        getDate("2010-10-18 01:23:45")             | getDate("2010-10-18 00:00:00")
        getDateWithMsec("2010-10-18 01:23:45.678") | getDateWithMsec("2010-10-18 00:00:00.000")
    }

    def "getToday: you can get current date"() {
        given:
        def before = new Date()

        when:
        def today = DateUtils.getToday()

        then:
        def after = new Date()
        before <= today
        today <= after
    }

    def "getEpoch: you can get unit epoch time"() {
        expect:
        DateUtils.getEpoch() == new Date(0)
    }

    private static getDate(String date) {
        Date.parse('yyyy-MM-dd HH:mm:ss', date)
    }

    private static getDateWithMsec(String date) {
        Date.parse('yyyy-MM-dd HH:mm:ss.SSS', date)
    }
}
