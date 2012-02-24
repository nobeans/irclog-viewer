beans = {
    sqlHelper(irclog.helper.SqlHelper) { bean ->
        bean.scope = 'prototype'
        dataSource = ref('dataSource')
    }
}
