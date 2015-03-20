package irclog.utils

import irclog.Channel
import irclog.Irclog
import irclog.Person
import irclog.Role

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
        return populateDirectly(new Channel(), defaultProps + propertyMap)
    }

    static Person createPerson(propertyMap = [:]) {
        def id = counter.getAndIncrement()
        def loginName = propertyMap.loginName ?: "LOGIN_NAME${id}"
        def defaultProps = [
            loginName: loginName,
            realName: "Mr. <${loginName}>",
            password: "123456",
            repassword: "123456",
            enabled: true,
            nicks: "${loginName}_",
            color: "#fff",
            role: propertyMap.containsKey("role") ? null : createRole(),
            channels: [],
        ]
        return populateDirectly(new Person(), defaultProps + propertyMap)
    }

    static Role createRole(propertyMap = [:]) {
        def id = counter.getAndIncrement()
        def name = propertyMap.name ?: "ROLE_${id}"
        def defaultProps = [
            name: name,
        ]
        return populateDirectly(new Role(), defaultProps + propertyMap)
    }

    static Irclog createIrclog(propertyMap = [:]) {
        def id = counter.getAndIncrement()
        def defaultProps = [
            time: new Date(),
            type: "PRIVMSG",
            message: "Hello, ${id}!",
            nick: "user_for_${id}",
            //permaId: "PERMID${id}", // should be automatically updated
            channelName: "#channel",
            channel: null,
        ]
        return populateDirectly(new Irclog(), defaultProps + propertyMap)
    }

    // MEMO: It's important to avoid a data-binding mechanism because there are some dedicated rules only for data-binding, e.g. bindable, trimStrings, convertEmptyStringsToNull.
    static populateDirectly(domain, propertyMap) {
        propertyMap.each { name, value ->
            domain[name] = value
        }
        return domain
    }
}
