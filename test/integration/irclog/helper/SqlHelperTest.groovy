package irclog.helper



import groovy.sql.Sql

public class SqlHelperTest extends GroovyTestCase {

    def dataSource
    def sqlHelper

    @Override
    void setUp() {
        def sql = new Sql(dataSource.connection)
        sql.execute("CREATE TABLE hoge ( id integer, value VARCHAR(5) )")
        assert sql.executeUpdate("INSERT INTO hoge VALUES (1, 'A')") == 1
        assert sql.executeUpdate("INSERT INTO hoge VALUES (2, 'B')") == 1
        sql.close()
    }

    @Override
    void tearDown() {
        def sql = new Sql(dataSource.connection)
        sql.execute("DROP TABLE hoge")
        sql.close()
    }

    void testExecute_WithoutArgs() {
        // Exercise
        sqlHelper.execute("UPDATE hoge SET value = 'X' WHERE id = 1")
        // Verify
        assert new Sql(dataSource.connection).firstRow("SELECT * FROM hoge WHERE id = 1").value == 'X'
    }

    void testExecute_WithArgs() {
        // Exercise
        sqlHelper.execute("UPDATE hoge SET value = ? WHERE id = ?", ["X", 1])
        // Verify
        assert new Sql(dataSource.connection).firstRow("SELECT * FROM hoge WHERE id = 1").value == 'X'
    }

    void testExecuteUpdate_WithoutArgs() {
        // Exercise
        assert sqlHelper.executeUpdate("UPDATE hoge SET value = 'X' WHERE id = 1") == 1
        // Verify
        assert new Sql(dataSource.connection).firstRow("SELECT * FROM hoge WHERE id = 1").value == 'X'
    }

    void testExecuteUpdate_WithArgs() {
        // Exercise
        assert sqlHelper.executeUpdate("UPDATE hoge SET value = ? WHERE id = ?", ["X", 1]) == 1
        // Verify
        assert new Sql(dataSource.connection).firstRow("SELECT * FROM hoge WHERE id = 1").value == 'X'
    }

    void testWithSql_allAreWithinBlock() {
        // Exercise
        sqlHelper.withSql { sql ->
            def rows = sql.rows("SELECT * FROM hoge")
            // Verify
            assert rows.size == 2
            assert rows[0].value == 'A'
            assert rows[1].value == 'B'
        }
    }

    void testWithSql_BlockReturnResultValue() {
        // Exercise
        def rows = sqlHelper.withSql { sql ->
            sql.rows("SELECT * FROM hoge")
        }
        // Verify
        assert rows.size == 2
        assert rows[0].value == 'A'
        assert rows[1].value == 'B'
    }
}