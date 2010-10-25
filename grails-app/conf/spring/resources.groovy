beans = {
    sqlHelper(irclog.helper.SqlHelper) { bean ->
        bean.scope = 'prototype'
        sessionFactory = ref('sessionFactory')
    }
    timeProvider(irclog.helper.TimeProvider) { bean ->
        bean.scope = 'prototype'
    }
}
