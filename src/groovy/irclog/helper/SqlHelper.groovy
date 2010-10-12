package irclog.helper

import groovy.sql.Sql

public class SqlHelper {

    def dataSource
    def sessionFactory

    int executeUpdate(query) {
        return executeNqtiveQuery { Sql db ->
            db.executeUpdate(query)
        }
    }

    def executeQuery(query, Closure closure) {
        return executeNqtiveQuery { Sql db ->
            closure.call(db.executeQuery(query))
        }
    }

    def executeNqtiveQuery(Closure closure) {
        def db = new Sql(dataSource)
        try {
            return closure.call(db)
        } finally {
            sessionFactory.currentSession.clear()
            db.close()
        }
    }

}
