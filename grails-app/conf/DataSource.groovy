dataSource {
    driverClassName = "org.postgresql.Driver"
    username = "postgres"
    password = ""
    dialect = irclog.helper.MyPostgreSQLDialect
    pooled = true
    properties {
        maxActive = -1
        minEvictableIdleTimeMillis = 1800000
        timeBetweenEvictionRunsMillis = 1800000
        numTestsPerEvictionRun = 3
        testOnBorrow = true
        testWhileIdle = true
        testOnReturn = true
        validationQuery = "SELECT 1"
    }
}
hibernate {
    cache.use_second_level_cache = false
    cache.use_query_cache = false
    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory'
    format_sql = true
    use_sql_comments = true
    jdbc.batch_size = 50
}
// environment specific settings
environments {
    development {
        //dataSource {
        //    dbCreate = "create"
        //    url = "jdbc:h2:file:db/irclog_dev;MVCC=TRUE;LOCK_TIMEOUT=10000;MODE=PostgreSQL"
        //}
        dataSource {
            dbCreate = "create"
            url = "jdbc:postgresql://localhost:5432/irclog_dev"
        }
    }
    test {
        dataSource {
            driverClassName = "org.h2.Driver"
            username = "sa"
            password = ""
            dbCreate = "create-drop"
            url = "jdbc:h2:mem:irclog_test;MVCC=TRUE;LOCK_TIMEOUT=10000;MODE=PostgreSQL"
            pooled = true
        }
    }
    production {
        dataSource {
            url = "jdbc:postgresql://localhost:5432/irclog"
        }
    }
}
