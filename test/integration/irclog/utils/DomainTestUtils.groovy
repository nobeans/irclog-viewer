package irclog.utils

import java.util.concurrent.atomic.AtomicInteger
import grails.test.*
import irclog.*

class DomainTestUtils {

    private static AtomicInteger counter = new AtomicInteger(0)

    static saveSurely = { domain = delegate ->
        domain.save()
        assert domain.hasErrors() == false : domain.errors
        return domain
    }

    private static expandDomainObjectForTests(domain) {
        domain.metaClass.saveSurely = DomainTestUtils.saveSurely
        return domain
    }

    static Channel createChannel(propertyMap) {
        def id = counter.getAndIncrement()
        def name = propertyMap.name ?: "#channel${id}"
        def defaultProps = [
            name: name,
            description: "${name} is nice!",
            isPrivate: true,
            isArchived: true,
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
            repassword: "123456",
            enabled: true,
            nicks: "${loginName}_",
            color: "#fff",
        ]
        return expandDomainObjectForTests(new Person(defaultProps + propertyMap))
    }
}
