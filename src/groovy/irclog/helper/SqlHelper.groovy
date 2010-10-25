package irclog.helper

import groovy.sql.Sql

class SqlHelper {

    def sessionFactory

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
        def sql = new Sql(sessionFactory.currentSession.connection())
        try {
            return closure.call(sql)
        } finally {
            // clear caches to get latest value from database via Hibernate
            sessionFactory.currentSession.clear()

            // the connection should not be closed because it's managed by sessionFactory
            //sql.close()
        }
    }

}
