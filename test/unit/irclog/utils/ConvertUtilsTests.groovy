package irclog.utils

import grails.test.*

class ConvertUtilsTests extends GrailsUnitTestCase {

    void testToInteger_convertable() {
        assert ConvertUtils.toInteger("1") == 1I
        assert ConvertUtils.toInteger("12") == 12I
        assert ConvertUtils.toInteger("1234567890") == 1234567890I
        assert ConvertUtils.toInteger("1").class == Integer
    }

    void testToInteger_default() {
        assert ConvertUtils.toInteger("a") == null
        assert ConvertUtils.toInteger("a", 12345I) == 12345I
        assert ConvertUtils.toInteger(null, 12345I) == 12345I
        assert ConvertUtils.toInteger(null, 12345I).class == Integer
    }

    void testToLong_convertable() {
        assert ConvertUtils.toLong("1") == 1L
        assert ConvertUtils.toLong("12") == 12L
        assert ConvertUtils.toLong("1234567890") == 1234567890L
        assert ConvertUtils.toLong("1").class == Long
    }

    void testToLong_default() {
        assert ConvertUtils.toLong("a") == null
        assert ConvertUtils.toLong("a", 12345L) == 12345L
        assert ConvertUtils.toLong(null, 12345L) == 12345L
        assert ConvertUtils.toLong(null, 12345L).class == Long
    }

    void testToDate_date() {
        Date date = ConvertUtils.toDate("2010-10-18")
        assert date.toString() == "Mon Oct 18 00:00:00 JST 2010"
    }

    void testToDate_dateAndTime() {
        Date date = ConvertUtils.toDate("2010-10-18 01:23:45")
        assert date.toString() == "Mon Oct 18 01:23:45 JST 2010"
    }

    void testToDate_invalid() {
        [
            "",
            "xx",
            "2010-10-18 01:23:xx",
            "201010-18 01:23:45",
            "2010-1-18 01:23:45",
        ].each { value ->
            shouldFail(IllegalArgumentException) {
                ConvertUtils.toDate(value)
            }
        }
    }
}
