switch (System.getProperty("db")) {
    case "h2":
        println "Configuring for H2..."
        dataSource {
            driverClassName = "org.h2.Driver"
            username = "sa"
            password = ""
            dbCreate = "create-drop"
            pooled = true
        }
        environments {
            development {
                dataSource {
                    dbCreate = "create"
                    url = "jdbc:h2:mem:db/irclog_dev;MVCC=TRUE;LOCK_TIMEOUT=10000;MODE=PostgreSQL"
                }
            }
            test {
                dataSource {
                    dbCreate = "create"
                    url = "jdbc:h2:mem:irclog_test;MVCC=TRUE;LOCK_TIMEOUT=10000;MODE=PostgreSQL"
                }
            }
            production {
                dataSource {
                    dbCreate = "create"
                    url = "jdbc:h2:mem:irclog;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE;MODE=PostgreSQL"
                }
            }
        }
        break

    default:
        println "Configuring for PostgreSQL..."
        def dbHost = System.getProperty("dbHost") ?: "localhost"
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
        environments {
            development {
                dataSource {
                    dbCreate = "create"
                    url = "jdbc:postgresql://$dbHost:5432/irclog_dev"
                }
            }
            test {
                dataSource {
                    dbCreate = "create"
                    url = "jdbc:h2:mem:irclog_test;MVCC=TRUE;LOCK_TIMEOUT=10000;MODE=PostgreSQL"
                }
            }
            production {
                dataSource {
                    dbCreate = "validate"
                    url = "jdbc:postgresql://$dbHost:5432/irclog"
                }
            }
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

