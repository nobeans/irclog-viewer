import grails.util.Holders

beans = {
    sqlHelper(irclog.helper.SqlHelper) { bean ->
        bean.scope = 'prototype'
        sessionFactory = ref('sessionFactory')
    }

    irclogLogAppender(irclog.ircbot.IrclogLogAppender) { bean ->
        defaultChannelName = Holders.config.irclog.ircbot.channel.defaultForLogging
    }

    ircbot(irclog.ircbot.Ircbot) { bean ->
        irclogLogAppender = ref('irclogLogAppender')
    }
}
