package irclog

import grails.test.mixin.integration.Integration
import irclog.utils.DateUtils
import irclog.utils.DomainUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import spock.lang.Unroll
import spock.lang.Specification

@Integration
@Transactional
class IrclogSearchServiceSpec extends Specification {

    @Autowired
    IrclogSearchService irclogSearchService

    def ch1, ch2, ch3
    def user1, user2, user3, userX, admin

    def setup() {
        setupTodayOfDateUtils("2011-01-01 12:34:56")
        setupChannel()
        setupPerson()
        setupRelationBetweenPersonAndChannel()
    }

    @Unroll
    def "search(period:'#period') should return irclog list within the period"() {
        given:
        def logs = []
        logs << saveIrclog(time: "2009-12-31 23:59:59") // 0
        logs << saveIrclog(time: "2010-01-01 00:00:00") // 1: a boundary of year (a boundary must be midnight)
        logs << saveIrclog(time: "2010-06-30 23:59:59") // 2
        logs << saveIrclog(time: "2010-07-01 00:00:00") // 3: a boundary of halfyear
        logs << saveIrclog(time: "2010-11-30 23:59:59") // 4
        logs << saveIrclog(time: "2010-12-01 00:00:00") // 5: a boundary of month
        logs << saveIrclog(time: "2010-12-24 23:59:59") // 6
        logs << saveIrclog(time: "2010-12-25 00:00:01") // 7: a boundary of week
        logs << saveIrclog(time: "2011-01-01 12:34:56") // 8: today
        logs << saveIrclog(time: "2011-01-01 23:59:59") // 9: search result including today's all logs as end period
        logs << saveIrclog(time: "2011-01-02 00:00:00") // 10: tomorrow is out of a boundary

        when:
        def actual = irclogSearchService.search(user3, createCriterion(period: period), [:], order)

        then:
        assertSearchResult(logs[expectedIndexes], actual)

        where:
        period     | order | expectedIndexes
        'all'      | 'asc' | (0..9)
        'year'     | 'asc' | (1..9)
        'halfyear' | 'asc' | (3..9)
        'month'    | 'asc' | (5..9)
        'week'     | 'asc' | (7..9)
        'today'    | 'asc' | (8..9)
    }

    def "search(period:'oneday') should return irclog list of the specified day"() {
        given:
        def logs = []
        logs << saveIrclog(time: "2010-12-31 00:00:00")
        logs << saveIrclog(time: "2010-12-24 23:59:59")
        logs << saveIrclog(time: "2010-12-24 12:34:56")
        logs << saveIrclog(time: "2010-12-24 00:00:00")
        logs << saveIrclog(time: "2010-12-23 23:59:59")

        and:
        def criterion = createCriterion(period: 'oneday', periodOnedayDate: '2010-12-24')

        when:
        def actual = irclogSearchService.search(user3, criterion, [:], 'desc')

        then:
        assertSearchResult(logs[1..3], actual)
    }

    @Unroll
    def "search(channel:'#channelName') should return irclog list #expectedLabel for #user as #roleLabel"() {
        given:
        def logs = [ch1, ch2, ch3].collectEntries { ch ->
            [ch.name, saveIrclog(channel: ch)]
        }

        when:
        def actual = irclogSearchService.search(this[user], createCriterion(channel: channelName), [:], 'asc')

        then:
        def expected = logs.findAll { it.key in expectedChannels }.collect { it.value }
        assertSearchResult(expected, actual)

        where:
        channelName | roleLabel    | user    | expectedLabel                                          | expectedChannels
        "all"       | "ROLE_ADMIN" | "admin" | "of all channel regardless joining or not the channel" | ["#ch1", "#ch2", "#ch3"]
        "all"       | "ROLE_USER"  | "user1" | "of all joining channel"                               | ["#ch1"]
        "all"       | "ROLE_USER"  | "user2" | "of all joining channel"                               | ["#ch1", "#ch2"]
        "all"       | "ROLE_USER"  | "user3" | "of all joining channel"                               | ["#ch1", "#ch2", "#ch3"]
        "all"       | "ROLE_USER"  | "userX" | "of all joining channel"                               | []
        "#ch1"      | "ROLE_ADMIN" | "admin" | "of the specified channel regardless joining or not"   | ["#ch1"]
        "#ch1"      | "ROLE_USER"  | "user1" | "of the specified joining channel"                     | ["#ch1"]
        "#ch1"      | "ROLE_USER"  | "user2" | "of the specified joining channel"                     | ["#ch1"]
        "#ch1"      | "ROLE_USER"  | "user3" | "of the specified joining channel"                     | ["#ch1"]
        "#ch1"      | "ROLE_USER"  | "userX" | "of the specified joining channel"                     | []
        "#ch2"      | "ROLE_ADMIN" | "admin" | "of the specified channel regardless joining or not"   | ["#ch2"]
        "#ch2"      | "ROLE_USER"  | "user1" | "of the specified joining channel"                     | []
        "#ch2"      | "ROLE_USER"  | "user2" | "of the specified joining channel"                     | ["#ch2"]
        "#ch2"      | "ROLE_USER"  | "user3" | "of the specified joining channel"                     | ["#ch2"]
        "#ch2"      | "ROLE_USER"  | "userX" | "of the specified joining channel"                     | []
        "#ch3"      | "ROLE_ADMIN" | "admin" | "of the specified channel regardless joining or not"   | ["#ch3"]
        "#ch3"      | "ROLE_USER"  | "user1" | "of the specified joining channel"                     | []
        "#ch3"      | "ROLE_USER"  | "user2" | "of the specified joining channel"                     | []
        "#ch3"      | "ROLE_USER"  | "user3" | "of the specified joining channel"                     | ["#ch3"]
        "#ch3"      | "ROLE_USER"  | "userX" | "of the specified joining channel"                     | []
    }

    @Unroll
    def "search(type:'#type') should return irclog list #expectedLabel"() {
        given:
        def logs = [:]
        Irclog.ESSENTIAL_TYPES.each { logs[it] = saveIrclog(type: it) }
        Irclog.OPTION_TYPES.each { logs[it] = saveIrclog(type: it) }

        when:
        def actual = irclogSearchService.search(admin, createCriterion(type: type), [:], 'asc')

        then:
        def expected = logs.findAll { it.key in expectedType }.collect { it.value }
        assertSearchResult(expected, actual)

        where:
        type       | expectedLabel        | expectedType
        'all'      | "of all types"       | Irclog.ESSENTIAL_TYPES + Irclog.OPTION_TYPES
        'filtered' | "of essential types" | Irclog.ESSENTIAL_TYPES
        'PRIVMSG'  | "specified types"    | 'PRIVMSG'
        'JOIN'     | "specified types"    | 'JOIN'
        'PART'     | "specified types"    | 'PART'
    }

    @Unroll
    def "search(nick:'#nick') should return #expectedLabel"() {
        given:
        def logs = ["john", "jojo", "mike"].collectEntries { [it, saveIrclog(nick: it)] }

        when:
        def actual = irclogSearchService.search(admin, createCriterion(nick: nick), [:], 'asc')

        then:
        def expected = logs.findAll { it.key in expectedNick }.collect { it.value }
        assertSearchResult(expected, actual)

        where:
        nick       | expectedLabel                                             | expectedNick
        "j"        | "irclog list including 'j' as part of nick"               | ["john", "jojo"]
        "jO"       | "irclog list including 'jo' ignored case as part of nick" | ["john", "jojo"]
        "NO_MATCH" | "empty list"                                              | []
        ""         | "all irclog list because blank is included in all nick"   | ["john", "jojo", "mike"]
    }

    @Unroll
    def "search(message:'#message') should return #expectedLabel"() {
        given:
        def logs = ["john", "jojo", "mike"].collectEntries { [it, saveIrclog(message: "Hello, ${it}")] }

        when:
        def actual = irclogSearchService.search(admin, createCriterion(message: message), [:], 'asc')

        then:
        def expected = logs.findAll { it.key in expectedMessage }.collect { it.value }
        assertSearchResult(expected, actual)

        where:
        message    | expectedLabel                                                | expectedMessage
        "j"        | "irclog list including 'j' as part of message"               | ["john", "jojo"]
        "jO"       | "irclog list including 'jo' ignored case as part of message" | ["john", "jojo"]
        "NO_MATCH" | "empty list"                                                 | []
        ""         | "all irclog list because blank is included in all message"   | ["john", "jojo", "mike"]
        "hello"    | "all irclog list because it's included in all message"       | ["john", "jojo", "mike"]
    }

    @Unroll
    def "search('#order') should return irclog list ordered by time #order"() {
        given:
        def logs = []
        logs << saveIrclog(time: "2010-01-01 00:00:00")
        logs << saveIrclog(time: "2010-01-02 00:00:00")
        logs << saveIrclog(time: "2010-01-03 00:00:00")

        when:
        def actual = irclogSearchService.search(admin, createCriterion(), [:], order)

        then:
        assertSearchResult(logs[expectedIndexes], actual)

        where:
        order  | expectedIndexes
        'asc'  | [0, 1, 2]
        'desc' | [2, 1, 0]
    }

    @Unroll
    def "search(max:'#max', offset:'#offset') should return #expectedLabel"() {
        given:
        def logs = []
        logs << saveIrclog(time: "2010-01-01 00:00:00")
        logs << saveIrclog(time: "2010-01-02 00:00:00")
        logs << saveIrclog(time: "2010-01-03 00:00:00")

        when:
        def actual = irclogSearchService.search(admin, createCriterion(), [max: max, offset: offset], 'asc')

        then:
        assertSearchResult(logs[expectedIndexes], actual, logs.size())

        where:
        max | offset | expectedLabel                                             | expectedIndexes
        0   | 0      | "all irclogs because '0' means no condition"              | [0, 1, 2]
        1   | 0      | "1 irclog"                                                | [0]
        2   | 0      | "2 irclogs"                                               | [0, 1]
        3   | 0      | "3 irclogs"                                               | [0, 1, 2]
        4   | 0      | "3 irclogs because there are only 3 irclogs"              | [0, 1, 2]
        1   | 1      | "1 irclogs after 1st row"                                 | [1]
        2   | 1      | "2 irclogs after 1st row"                                 | [1, 2]
        3   | 1      | "2 irclogs because there is no irclog afte after 1st row" | [1, 2]
        2   | 2      | "1 irclog because there is no irclog after 2nd row"       | [2]
        3   | 2      | "1 irclog because there is no irclog after 2nd row"       | [2]
        3   | 3      | "no irclog because there is no irclog after 3rd row"      | []
        3   | 4      | "no irclog because there is no irclog after 4th row"      | []
    }

    // -------------------------------------
    // Test helpers

    private setupChannel() {
        (1..3).each { num ->
            this."ch${num}" = DomainUtils.createChannel(name: "#ch${num}", description: "${10 - num}").save(failOnError: true)
        }
    }

    private setupPerson() {
        admin = Person.findByLoginName("admin") // setup in Bootstrap
        def roleUser = Role.findByName("ROLE_USER")
        ["1", "2", "3", "X"].each { id ->
            def user = DomainUtils.createPerson(
                loginName: "user${id}",
                role: roleUser,
            ).save(failOnError: true)
            this."user${id}" = user
        }
    }

    private setupRelationBetweenPersonAndChannel() {
        // #ch1[user1, user2, user3], #ch2[user2, user3], #ch3[user3]
        // userX isn't allowed to access any channel.
        ch1.addToPersons(user1).addToPersons(user2).addToPersons(user3)
        ch2.addToPersons(user2).addToPersons(user3)
        ch3.addToPersons(user3)
    }

    private setupTodayOfDateUtils(dateStr) {
        DateUtils.metaClass.static.getToday = {->
            return DateUtils.toDate(dateStr)
        }
    }

    private saveIrclog(propMap = [:]) {
        if (propMap.time in String) {
            propMap.time = DateUtils.toDate(propMap.time)
        }
        if (propMap.channel) {
            propMap.channelName = propMap.channel.name
        }
        def defaultMap = [
            channel: ch2,
            channelName: ch2.name,
            time: DateUtils.today,
            nick: "user1",
            type: "PRIVMSG",
        ]
        def mergedMap = defaultMap + propMap
        def permaId = mergedMap.toString() // to avoid stack overflow
        mergedMap.permaId = permaId
        return DomainUtils.createIrclog(mergedMap).save(failOnError: true)
    }

    private createCriterion(map = [:]) {
        return [
            period: 'all',
            channel: 'all',
            type: 'all',
            nick: '',
            message: '',
        ] + map
    }

    private void assertSearchResult(expected, actual, expectedTotalCount = null) {
        assert actual.list*.id == expected*.id
        assert actual.totalCount == (expectedTotalCount ?: expected.size())
    }

}
