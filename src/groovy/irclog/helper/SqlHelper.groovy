package irclog.helper

import groovy.sql.Sql

class SqlHelper {

    def dataSource

    void execute(query, option = null) {
        withSql { Sql sql ->
            if (option) {
                sql.execute(query, option)
            } else {
                sql.execute(query)
            }
        }
    }

    int executeUpdate(query, option = null) {
        return withSql { Sql sql ->
            if (option) {
                return sql.executeUpdate(query, option)
            } else {
                return sql.executeUpdate(query)
            }
        }
    }

    def withSql(Closure closure) {
        def sql = new Sql(dataSource)
        return closure.call(sql)
    }

}
