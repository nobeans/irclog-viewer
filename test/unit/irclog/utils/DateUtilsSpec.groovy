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
