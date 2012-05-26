package irclog

import grails.plugin.spock.IntegrationSpec
import irclog.utils.DateUtils
import irclog.utils.DomainUtils
import spock.lang.Shared
import spock.lang.Unroll

class ChannelServiceSpec extends IntegrationSpec {

    def channelService

    @Shared
    def ch1, ch2, ch3
    @Shared
    def user1, user2, user3, userX, admin

    def setupSpec() {
        setupChannel()
        setupPerson()
        setupRelationBetweenPersonAndChannel()
        setupIrclog()
        setupSummary()
    }

    @Unroll
    def "getAccessibleChannelList: returns channel list which is accessible for #person, ordered by #orderBy"() {
        expect:
        channelService.getAccessibleChannelList(person, orderBy) == expected

        where:
        person | orderBy                             | expected
        admin  | [:]                                 | [ch1, ch2, ch3]
        admin  | [sort: 'name', order: 'desc']       | [ch3, ch2, ch1]
        admin  | [sort: 'description', order: 'asc'] | [ch3, ch2, ch1]
        user1  | [:]                                 | [ch1]
        user2  | [:]                                 | [ch2]
        user3  | [:]                                 | [ch3]
    }

    @Unroll
    def "getJoinedPersons: returns person list on #channel specified"() {
        expect:
        channelService.getJoinedPersons(channel) == expected

        where:
        channel | expected
        ch1     | [user1]
        ch2     | [user2]
        ch3     | [user3, userX]
    }

    def "getAllJoinedPersons returns all person list who has been already joined any channel"() {
        when:
        def channels = channelService.getAllJoinedPersons()

        then:
        channels.size() == 3
        channels[ch1] == [user1]
        channels[ch2] == [user2]
        channels[ch3] == [user3, userX]
    }

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
        if (ch) {
//            Irclog.countByChannelAndChannelName(ch, channelName) // FIXME it doesn't work expectedly
            Irclog.findAllByChannelName(channelName).findAll { it.channel == ch }.size()
        } else {
//            Irclog.countByChannelNameAndChannelIsNull(channelName) // FIXME it doesn't work expectedly
            Irclog.findAllByChannelName(channelName).findAll { it.channel == null }.size()
        }
    }

    def "deleteChannel: deletes the specified channel and all relationship with it"() {
        given:
        assert Person.findByLoginName("user3").channels.contains(ch3)
        assert Irclog.count() == 12
        assert Irclog.findAllByChannelName("#ch3").any { it.channel == ch3 }
        assert Summary.countByChannel(ch3) == 1
        assert Channel.get(ch3.id) == ch3

        when:
        channelService.deleteChannel(ch3)

        then:
        Person.findByLoginName("user3").channels.contains(ch3) == false
        Irclog.count() == 12
        Irclog.findAllByChannelName("#ch3").every { it.channel == null }
        Summary.countByChannel(ch3) == 0
        Channel.get(ch3.id) == null
    }

    @Unroll
    def "getBeforeDate: returns a previous date of the specified one when ignoredOptionType is #isIgnoredOptionType"() {
        given:
        createIrclog(ch2, "2010-10-01 12:34:56", "PRIVMSG")
        createIrclog(ch2, "2010-10-03 23:59:59", "NOTICE")
        createIrclog(ch2, "2010-10-05 12:34:56", "OTHER")
        createIrclog(ch2, "2010-10-07 01:23:45", "PRIVMSG")

        expect:
        channelService.getBeforeDate("2010-10-06", ch2, isIgnoredOptionType) == expected

        where:
        isIgnoredOptionType | expected
        true                | DateUtils.toDate("2010-10-03 00:00:00")
        false               | DateUtils.toDate("2010-10-05 00:00:00")
    }

    def "getBeforeDate: returns null when there isn't no date which has any stored irclog"() {
        expect:
        channelService.getBeforeDate("1999-01-01", ch2, true) == null
    }

    @Unroll
    def "getAfterDate: returns a previous date of the specified one when ignoredOptionType is #isIgnoredOptionType"() {
        given:
        createIrclog(ch2, "2010-10-01 12:34:56", "PRIVMSG")
        createIrclog(ch2, "2010-10-03 23:59:59", "OTHER")
        createIrclog(ch2, "2010-10-05 12:34:56", "NOTICE")
        createIrclog(ch2, "2010-10-07 01:23:45", "PRIVMSG")

        expect:
        channelService.getAfterDate("2010-10-02", ch2, isIgnoredOptionType) == expected

        where:
        isIgnoredOptionType | expected
        true                | DateUtils.toDate("2010-10-05 00:00:00")
        false               | DateUtils.toDate("2010-10-03 00:00:00")
    }

    def "getAfterDate: returns null when there isn't no date which has any stored irclog"() {
        expect:
        channelService.getAfterDate("2999-01-01", ch2, true) == null
    }

    @Unroll
    def "getLatestDate: returns a previous date of the specified one when ignoredOptionType is #isIgnoredOptionType"() {
        given:
        createIrclog(ch2, "2010-10-01 12:34:56", "PRIVMSG")
        createIrclog(ch2, "2010-10-03 23:59:59", "OTHER")
        createIrclog(ch2, "2010-10-05 12:34:56", "NOTICE")
        createIrclog(ch2, "2010-10-07 01:23:45", "OTHER")

        expect:
        channelService.getLatestDate("2010-10-02", ch2, isIgnoredOptionType) == expected

        where:
        isIgnoredOptionType | expected
        true                | DateUtils.toDate("2010-10-05 00:00:00")
        false               | DateUtils.toDate("2010-10-07 00:00:00")
    }

    def "getLatestDate: returns null when there isn't no date which has any stored irclog"() {
        expect:
        channelService.getLatestDate("2999-01-01", ch2, true) == null
    }

    def "getRelatedDates: returns all results of before, after and latest"() {
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
            def user = DomainUtils.createPerson(loginName: "user${id}").save(failOnError: true)
            user.addToRoles(roleUser)
            this."user${id}" = user
        }
    }

    private setupRelationBetweenPersonAndChannel() {
        // #ch1[user1], #ch2[user2], #ch3[user3, userX]
        user1.addToChannels(ch1)
        user2.addToChannels(ch2)
        user3.addToChannels(ch3)
        userX.addToChannels(ch3)
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
        DomainUtils.createSummary(channel: ch1, latestIrclog: Irclog.findByChannel(ch1)).save(failOnError: true)
        DomainUtils.createSummary(channel: ch2, latestIrclog: Irclog.findByChannel(ch2)).save(failOnError: true)
        DomainUtils.createSummary(channel: ch3, latestIrclog: Irclog.findByChannel(ch3)).save(failOnError: true)
    }
}
