package irclog.helper

import grails.test.mixin.*
import grails.test.mixin.support.*
import org.junit.*

@TestMixin(GrailsUnitTestMixin)
class TimeMarkerTests {

    TimeMarker marker = new TimeMarker("01:23")

    @Test
    void getTime() {
        assert marker.getTime().format('yyyy-MM-dd HH:mm:ss') == new Date().format('yyyy-MM-dd 01:23:00')
    }

    @Test
    void toString_() {
        assert marker.toString() == "01:23"
    }
}
