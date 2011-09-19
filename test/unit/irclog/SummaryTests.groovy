package irclog

import grails.test.mixin.*
import org.junit.*
import static irclog.utils.DomainUtils.*

@TestFor(Summary)
class SummaryTests {

    @Test
    void total() {
        def summary = createSummary()
        assert summary.today == 1
        assert summary.totalBeforeYesterday == 8
        assert summary.total() == 9
    }
}
