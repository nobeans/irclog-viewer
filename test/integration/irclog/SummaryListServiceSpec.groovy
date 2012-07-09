package irclog

import grails.plugin.spock.IntegrationSpec
import irclog.utils.DateUtils
import irclog.utils.DomainUtils
import spock.lang.Unroll

class SummaryListServiceSpec extends IntegrationSpec {

    SummaryListService summaryListService

    def ch1, ch2, ch3, ch4
    def irclog1, irclog2, irclog3, irclog4
    def summary1, summary2, summary3

    def setup() {
        setupChannel()
        setupIrclog()
        setupSummary()
    }

    @Unroll
    def "getSummaryList() should return #expectedLabel when #channelLabel specified"() {
        given:
        def params = [:]
        def channelList = channelNameList.collect { this[it] }

        when:
        def actual = summaryListService.getSummaryList(params, channelList)

        then:
        actual == expected.collect { this[it] }

        where:
        channelLabel       | channelNameList       | expectedLabel   | expected
        "empty list is"    | []                    | "empty list"    | []
        "one channel is"   | ["ch1"]               | "one summary"   | ["summary1"]
        "two channels are" | ["ch1", "ch3"]        | "two summaries" | ["summary3", "summary1"]
        "all channels are" | ["ch1", "ch2", "ch3"] | "all summaries" | ["summary3", "summary2", "summary1"]
    }

    def "getSummaryList() should return all summary including dummy summary as last element when a channel which has no summary is specified"() {
        given:
        def params = [:]
        def channelList = [ch1, ch2, ch3, ch4]

        when:
        def actual = summaryListService.getSummaryList(params, channelList)

        then:
        actual[0..2] == [summary3, summary2, summary1]
        actual[3].channel == ch4
        actual[3].lastUpdated == DateUtils.epoch
    }

    // -------------------------------------
    // Test helpers

    private setupChannel() {
        (1..4).each { num ->
            this."ch${num}" = DomainUtils.createChannel(name: "#ch${num}", description: "${10 - num}").save(failOnError: true)
        }
    }

    private setupIrclog() {
        (1..4).each { num ->
            this["irclog${num}"] = saveIrclog(this["ch${num}"])
        }
    }

    private saveIrclog(ch, propMap = [:]) {
        if (propMap.time in String) {
            propMap.time = DateUtils.toDate(propMap.time)
        }
        if (propMap.channel) {
            propMap.channelName = propMap.channel.name
        }
        def defaultMap = [
            channel: ch,
            channelName: ch.name,
            time: DateUtils.today,
            nick: "user1",
            type: "PRIVMSG",
        ]
        def mergedMap = defaultMap + propMap
        def permaId = mergedMap.toString() // to avoid stack overflow
        mergedMap.permaId = permaId
        return DomainUtils.createIrclog(mergedMap).save(failOnError: true)
    }

    private setupSummary() {
        (1..3).each { num ->
            this["summary${num}"] = saveSummary(this["ch${num}"], this["irclog${num}"])
        }
    }

    private Summary saveSummary(ch, latestIrclog) {
        DomainUtils.createSummary(
            channel: ch,
            latestIrclog: latestIrclog,
        ).save(failOnError: true)
    }
}
