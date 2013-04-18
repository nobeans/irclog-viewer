package irclog

import grails.plugin.spock.IntegrationSpec
import irclog.utils.DateUtils
import irclog.utils.DomainUtils
import spock.lang.IgnoreRest
import spock.lang.Unroll

class ChannelServiceSpec extends IntegrationSpec {

    ChannelService channelService

    Channel ch1, ch2, ch3
    Person user1, user2, user3, userX, admin

    def setup() {
        setupChannel()
        setupPerson()
        setupRelationBetweenPersonAndChannel()
        setupIrclog()
        setupSummary()
    }

    @Unroll
    def "getAccessibleChannelList() should return channel list which is accessible for #person, ordered by #orderByLabel"() {
        expect:
        channelService.getAccessibleChannelList(this[person], orderBy) == expected.collect { this[it] }

        where:
        person  | orderByLabel      | orderBy                             | expected
        "admin" | "nothing"         | [:]                                 | ["ch1", "ch2", "ch3"]
        "admin" | "name desc"       | [sort: 'name', order: 'desc']       | ["ch3", "ch2", "ch1"]
        "admin" | "description asc" | [sort: 'description', order: 'asc'] | ["ch3", "ch2", "ch1"]
        "user1" | "nothing"         | [:]                                 | ["ch1"]
        "user2" | "nothing"         | [:]                                 | ["ch2"]
        "user3" | "nothing"         | [:]                                 | ["ch3"]
    }

    @IgnoreRest
    def "relateToIrclog: makes irclog existed relate with a specified channel as argument"() {
        given:
        assert countIrclogOf("#ch1", null) == 2
        assert countIrclogOf("#ch1", ch1) == 2
        assert countIrclogOf("#ch2", null) == 2
        assert countIrclogOf("#ch2", ch2) == 2

        when:
        int affectedRowsCount = channelService.relateToIrclog(ch1)

        then:
        affectedRowsCount == 2
        countIrclogOf("#ch1", null) == 0 // !!
        countIrclogOf("#ch1", ch1) == 4  // !!
        countIrclogOf("#ch2", null) == 2
        countIrclogOf("#ch2", ch2) == 2
    }

    private countIrclogOf(String channelName, Channel ch) {
        Irclog.findAllByChannelName(channelName).findAll { it.channel == ch }.size()
    }

    @Unroll
    def "getBeforeDate() should return a previous date of the specified one when ignoredOptionType is #isIgnoredOptionType"() {
        given:
        createIrclog(ch2, "2010-10-01 12:34:56", "PRIVMSG")
        createIrclog(ch2, "2010-10-03 23:59:59", "NOTICE")
        createIrclog(ch2, "2010-10-05 12:34:56", "OTHER")
        createIrclog(ch2, "2010-10-07 01:23:45", "PRIVMSG")

        expect:
        channelService.getBeforeDate("2010-10-06", ch2, isIgnoredOptionType) == DateUtils.toDate(expected)

        where:
        isIgnoredOptionType | expected
        true                | "2010-10-03 00:00:00"
        false               | "2010-10-05 00:00:00"
    }

    def "getBeforeDate() should return null when there isn't no date which has any stored irclog"() {
        expect:
        channelService.getBeforeDate("1999-01-01", ch2, true) == null
    }

    @Unroll
    def "getAfterDate() should return a previous date of the specified one when ignoredOptionType is #isIgnoredOptionType"() {
        given:
        createIrclog(ch2, "2010-10-01 12:34:56", "PRIVMSG")
        createIrclog(ch2, "2010-10-03 23:59:59", "OTHER")
        createIrclog(ch2, "2010-10-05 12:34:56", "NOTICE")
        createIrclog(ch2, "2010-10-07 01:23:45", "PRIVMSG")

        expect:
        channelService.getAfterDate("2010-10-02", ch2, isIgnoredOptionType) == DateUtils.toDate(expected)

        where:
        isIgnoredOptionType | expected
        true                | "2010-10-05 00:00:00"
        false               | "2010-10-03 00:00:00"
    }

    def "getAfterDate() should return null when there isn't no date which has any stored irclog"() {
        expect:
        channelService.getAfterDate("2999-01-01", ch2, true) == null
    }

    @Unroll
    def "getLatestDate() should return a previous date of the specified one when ignoredOptionType is #isIgnoredOptionType"() {
        given:
        createIrclog(ch2, "2010-10-01 12:34:56", "PRIVMSG")
        createIrclog(ch2, "2010-10-03 23:59:59", "OTHER")
        createIrclog(ch2, "2010-10-05 12:34:56", "NOTICE")
        createIrclog(ch2, "2010-10-07 01:23:45", "OTHER")

        expect:
        channelService.getLatestDate("2010-10-02", ch2, isIgnoredOptionType) == DateUtils.toDate(expected)

        where:
        isIgnoredOptionType | expected
        true                | "2010-10-05 00:00:00"
        false               | "2010-10-07 00:00:00"
    }

    def "getLatestDate() should return null when there isn't no date which has any stored irclog"() {
        expect:
        channelService.getLatestDate("2999-01-01", ch2, true) == null
    }

    def "getRelatedDates() should return all results of before, after and latest"() {
        given:
        createIrclog(ch2, "2010-10-01 12:34:56", "PRIVMSG")
        createIrclog(ch2, "2010-10-03 23:59:59", "OTHER")
        createIrclog(ch2, "2010-10-05 12:34:56", "NOTICE")
        createIrclog(ch2, "2010-10-07 01:23:45", "TOPIC")

        when:
        def result = channelService.getRelatedDates("2010-10-04", ch2, true)

        then:
        result == [
            before: DateUtils.toDate("2010-10-01 00:00:00"),
            after: DateUtils.toDate("2010-10-05 00:00:00"),
            latest: DateUtils.toDate("2010-10-07 00:00:00"),
        ]
    }

    //----------------------------------------------------
    // Helper methods

    private createIrclog(ch, dateTime, type, chName = "DEFAULT") {
        Date time = DateUtils.toDate(dateTime)
        def channelName = (chName == "DEFAULT") ? ch.name : chName
        DomainUtils.createIrclog(
            permaId: "log:${ch?.name}:${channelName}:${time.format("yyyy-MM-dd_HH:mm:ss")}",
            channelName: channelName,
            time: time,
            channel: ch,
            type: type,
        ).save(failOnError: true)
    }

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
        // #ch1[user1], #ch2[user2], #ch3[user3, userX]
        ch1.addToPersons(user1)
        ch2.addToPersons(user2)
        ch3.addToPersons(user3).addToPersons(userX)
    }

    private setupIrclog() {
        [ch1, ch2, ch3].each { ch ->
            2.times { id ->
                createIrclog(null, "2010-01-01 00:00:0${id}", "PRIVMSG", ch.name)
                createIrclog(ch, "2010-01-01 00:00:0${id}", "PRIVMSG", ch.name)
            }
        }
    }

    private setupSummary() {
        [ch1, ch2, ch3].each { ch ->
            ch.summary.latestIrclog = Irclog.findByChannel(ch)
            ch.summary.save(failOnError: true)
        }
    }
}
