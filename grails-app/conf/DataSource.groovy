dataSource {
    pooled = true
    driverClassName = "org.h2.Driver"
    username = "sa"
    password = ""
}
hibernate {
    cache.use_second_level_cache = false
    cache.use_query_cache = false
    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory'
}
// environment specific settings
environments {
    development {
        dataSource {
            dbCreate = "create"
            url = "jdbc:h2:file:db/irclog_dev;MVCC=TRUE"
            //loggingSql = true
        }
    }
    postgres {
        dataSource {
            driverClassName = "org.postgresql.Driver"
            username = "postgres"
            password = ""
            url = "jdbc:postgresql://localhost:5432/irclog_dev"
            dbCreate = "create"
            //loggingSql = true
        }
    }
    test {
        dataSource {
            dbCreate = "create-drop"
            url = "jdbc:h2:mem:irclog_test;MVCC=TRUE"
        }
    }
    production {
        dataSource {
            //dbCreate = "update"
            url = "jdbc:h2:irclog;MVCC=TRUE"
            pooled = true
            properties {
               maxActive = -1
               minEvictableIdleTimeMillis=1800000
               timeBetweenEvictionRunsMillis=1800000
               numTestsPerEvictionRun=3
               testOnBorrow=true
               testWhileIdle=true
               testOnReturn=true
               validationQuery="SELECT 1"
            }
        }
    }
}
