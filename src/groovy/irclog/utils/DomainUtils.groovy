package irclog.utils

import irclog.Channel
import irclog.Irclog
import irclog.Person
import irclog.Role
import irclog.Summary

import java.util.concurrent.atomic.AtomicInteger

class DomainUtils {

    private static AtomicInteger counter = new AtomicInteger(0)

    static Channel createChannel(propertyMap = [:]) {
        def id = counter.getAndIncrement()
        def name = propertyMap.name ?: "#channel${id}"
        def defaultProps = [
            name: name,
            description: "${name} is nice!",
            isPrivate: true,
            isArchived: false,
            secretKey: "1234",
        ]
        new Channel(defaultProps + propertyMap)
    }

    static Person createPerson(propertyMap = [:]) {
        def id = counter.getAndIncrement()
        def loginName = propertyMap.loginName ?: "LOGIN_NAME${id}"
        def defaultProps = [
            loginName: loginName,
            realName: "Mr. <${loginName}>",
            password: "123456",
            //repassword: "123456",
            enabled: true,
            nicks: "${loginName}_",
            color: "#fff",
            //role: propertyMap.role ?: createRole(),
            channels: [],
        ]
        def person = new Person(defaultProps + propertyMap)
        // explicitly assign to property for a curious behavior of 'bindable'
        person.repassword = propertyMap.repassword ?: propertyMap.password ?: "123456"
        person.role = propertyMap.role ?: createRole()
        person
    }

    static Role createRole(propertyMap = [:]) {
        def id = counter.getAndIncrement()
        def name = propertyMap.name ?: "ROLE_${id}"
        def defaultProps = [
            name: name,
        ]
        new Role(defaultProps + propertyMap)
    }

    static Irclog createIrclog(propertyMap = [:]) {
        def id = counter.getAndIncrement()
        def defaultProps = [
            time: new Date(),
            type: "PRIVMSG",
            message: "Hello, ${id}!",
            nick: "user_for_${id}",
            permaId: "PERMID${id}",
            channelName: "#channel",
            channel: null,
        ]
        new Irclog(defaultProps + propertyMap)
    }

    static Summary createSummary(propertyMap = [:]) {
        def id = counter.getAndIncrement()
        def defaultProps = [
            today: 1,
            yesterday: 2,
            twoDaysAgo: 3,
            threeDaysAgo: 4,
            fourDaysAgo: 5,
            fiveDaysAgo: 6,
            sixDaysAgo: 7,
            totalBeforeYesterday: 8,
            lastUpdate: new Date(id),
            latestIrclog: createIrclog(),
            channel: createChannel(),
        ]
        new Summary(defaultProps + propertyMap)
    }
}
