package irclog.helper

import grails.test.mixin.integration.Integration
import groovy.sql.Sql
import javax.sql.DataSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

@Integration
@Transactional
class SqlHelperSpec extends Specification {

    @Autowired
    DataSource dataSource

    @Autowired
    SqlHelper sqlHelper

    def setup() {
        def sql = new Sql(dataSource.connection)
        sql.execute("CREATE TABLE hoge ( id integer, value VARCHAR(5) )")
        assert sql.executeUpdate("INSERT INTO hoge VALUES (1, 'A')") == 1
        assert sql.executeUpdate("INSERT INTO hoge VALUES (2, 'B')") == 1
        sql.close()
    }

    def cleanup() {
        def sql = new Sql(dataSource.connection)
        sql.execute("DROP TABLE hoge")
        sql.close()
    }

    def "execute: without args"() {
        when:
        sqlHelper.execute("UPDATE hoge SET value = 'X' WHERE id = 1")

        then:
        firstRow("SELECT * FROM hoge WHERE id = 1").value == 'X'
    }

    def "execute: with placeholder and args list"() {
        when:
        sqlHelper.execute("UPDATE hoge SET value = ? WHERE id = ?", ["X", 1])

        then:
        firstRow("SELECT * FROM hoge WHERE id = 1").value == 'X'
    }

    def "executeUpdate: without args"() {
        when:
        assert sqlHelper.executeUpdate("UPDATE hoge SET value = 'X' WHERE id = 1") == 1

        then:
        firstRow("SELECT * FROM hoge WHERE id = 1").value == 'X'
    }

    def "executeUpdate: with placeholder and args list"() {
        when:
        assert sqlHelper.executeUpdate("UPDATE hoge SET value = ? WHERE id = ?", ["X", 1]) == 1

        then:
        firstRow("SELECT * FROM hoge WHERE id = 1").value == 'X'
    }


    def "withSql: block returns result of closure"() {
        when:
        def rows = sqlHelper.withSql { sql ->
            sql.rows("SELECT * FROM hoge")
        }

        then:
        rows*.value == ['A', 'B']
    }

    private firstRow(sql) {
        new Sql(dataSource.connection).firstRow(sql)
    }
}
