package irclog.utils

import java.util.concurrent.atomic.AtomicInteger
import grails.test.*
import irclog.*

class DomainUtils {

    private static AtomicInteger counter = new AtomicInteger(0)

    static saveSurely = { domain = delegate ->
        domain.save()
        assert domain.hasErrors() == false : domain.errors
        return domain
    }

    private static expandDomainObjectForTests(domain) {
        domain.metaClass.saveSurely = DomainUtils.saveSurely
        return domain
    }

    static Channel createChannel(propertyMap) {
        def id = counter.getAndIncrement()
        def name = propertyMap.name ?: "#channel${id}"
        def defaultProps = [
            name: name,
            description: "${name} is nice!",
            isPrivate: true,
            isArchived: false,
            secretKey: "1234",
        ]
        return expandDomainObjectForTests(new Channel(defaultProps + propertyMap))
    }

    static Person createPerson(propertyMap) {
        def id = counter.getAndIncrement()
        def loginName = propertyMap.loginName ?: "LOGIN_NAME${id}"
        def defaultProps = [
            loginName: loginName,
            realName: "Mr. <${loginName}}>",
            password: "123456",
            repassword: propertyMap.password ?: "123456",
            enabled: true,
            nicks: "${loginName}_",
            color: "#fff",
        ]
        return expandDomainObjectForTests(new Person(defaultProps + propertyMap))
    }

    static Role createRole(propertyMap) {
        def id = counter.getAndIncrement()
        def name = propertyMap.name ?: "#role${id}"
        def defaultProps = [
            name: name,
            description: "${name} is nice!",
        ]
        return expandDomainObjectForTests(new Role(defaultProps + propertyMap))
    }
}
