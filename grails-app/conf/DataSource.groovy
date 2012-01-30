dataSource {
    pooled = true
    driverClassName = "org.postgresql.Driver"
    username = "postgres"
    password = ""
}
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = true
    cache.provider_class = 'net.sf.ehcache.hibernate.EhCacheProvider'
}
// environment specific settings
environments {
    development {
        dataSource {
            //url = "jdbc:postgresql://localhost:5432/irclog_dev"
            driverClassName = "org.h2.Driver"
            dbCreate = "create" // one of 'create', 'create-drop','update'
            url = "jdbc:h2:mem:irclog_dev"
            username = "sa"
            password = ""
            //loggingSql = true
        }
    }
    test {
        dataSource {
            driverClassName = "org.h2.Driver"
            dbCreate = "update"
            url = "jdbc:h2:mem:irclog_test"
            username = "sa"
            password = ""
        }
    }
    production {
        dataSource {
            url = "jdbc:postgresql://localhost:5432/irclog"
        }
    }
}
