package irclog

import grails.test.*
import static irclog.utils.DomainUtils.*
import static irclog.utils.ConvertUtils.*

class IrclogSearchServiceTests extends GrailsUnitTestCase {
    
    def irclogSearchService
    def ch1, ch2, ch3
    def user1, user2, user3, userX, admin
    
    IrclogSearchServiceTests() {
        // Setup shared fixture 
        Irclog.withTransaction {
        setUpChannel()
        setUpPerson()
        setUpRelationBetweenPersonAndChannel()
        setUpIrclog()
        }
    }
    
    private setUpChannel() {
        (1..3).each { num ->
            this."ch${num}" = createChannel(name:"#ch${num}", description:"${10 - num}").saveSurely()
        }
    }
    private setUpPerson() {
        admin = Person.findByLoginName("admin") // setup in Bootstrap
        def roleUser = Role.findByName("ROLE_USER")
        ["1", "2", "3", "X"].each { id ->
            def user = createPerson(loginName:"user${id}").saveSurely()
            user.addToRoles(roleUser)
            this."user${id}" = user
        }
    }
    private setUpRelationBetweenPersonAndChannel() {
        // #ch1[user1], #ch2[user2, userX], #ch3[user3, userX]
        user1.addToChannels(ch1)
        user2.addToChannels(ch2)
        user3.addToChannels(ch3)
        userX.addToChannels(ch2)
        userX.addToChannels(ch3)
    }
    private setUpIrclog() {
        def today = toDate("2010-01-01 00:00:00")
        [ch1, ch2, ch3].each { ch ->
            ["PRIVMSG", "JOIN"].each { type ->
                [user1, user2, user3].each { user ->
                    // second
                    (1..59).each { second ->
                        saveIrclog(ch, toCalendar(today.clone(), [second:second]).time, type, user)
                    }
                    // today
                    (0..23).each { hour ->
                        saveIrclog(ch, toCalendar(today.clone(), [hourOfDay:hour]).time, type, user)
                    }
                    // a week
                    (1..7).each { deltaDay ->
                        saveIrclog(ch, toCalendar((today.clone() - deltaDay).clone()).time, type, user)
                    }
                    // future
                    saveIrclog(ch, toCalendar((today + 1).clone()).time, type, user)
                    // a year + a month
                    (1..13).each { deltaMonth ->
                        saveIrclog(ch, toCalendar(today.clone(), [month:today.month - deltaMonth, day:1]).time, type, user)
                    }
                }
            }
        }
    }
    private saveIrclog(Channel ch, Date date, String type, Person user) {
        def permaId = "log:${ch.name}:${date.format('yyyy-MM-dd HH:mm:ss')}:${type}:${user}"
        println permaId
        createIrclog(permaId:permaId, channelName:ch.name, channel:ch, time:date, type:type, nick:user.loginName).saveSurely()
    }
    
    void testSearch_periodYear_allChannel_filtered_neitherNickNorMessage() {
        def criterion = [
            period:  'all',
            channel: 'all',
            type:    'filtered',
            nick:    '',
            message: '',
        ]
        println irclogSearchService.search(user3, criterion, [:], 'asc')
    }
    
    //        // 今日(すべて)
    //        def criterion = [
    //            period:  'today',
    //            channel: 'all',
    //            type:    'filtered',
    //            nick:    '',
    //            message: '',
    //        ]
    //        // 今日＆時刻指定有りの場合
    //        def criterion = [
    //            period:  'today',
    //            'period-today-time': '12:34',
    //            channel: 'all',
    //            type:    'filtered',
    //            nick:    '',
    //            message: '',
    //        ]
    //        def criterion = [
    //            period:  'ondeday',
    //            'period-oneday-date': '2010-10-05',
    //            channel: 'all',
    //            type:    'filtered',
    //            nick:    '',
    //            message: '',
    //        ]
}
