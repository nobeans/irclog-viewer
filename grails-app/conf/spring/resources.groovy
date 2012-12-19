beans = {
    sqlHelper(irclog.helper.SqlHelper) { bean ->
        bean.scope = 'prototype'
        sessionFactory = ref('sessionFactory')
    }

    ircbot(irclog.ircbot.Ircbot) { bean ->
        irclogAppendService = ref('irclogAppendService')
    }
}
