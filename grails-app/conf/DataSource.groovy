hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = false
//    cache.region.factory_class = 'org.hibernate.cache.SingletonEhCacheRegionFactory' // Hibernate 3
    cache.region.factory_class = 'org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory' // Hibernate 4
    singleSession = true // configure OSIV singleSession mode
    flush.mode = 'manual' // OSIV session flush mode outside of transactional context
    format_sql = true
    use_sql_comments = true
    jdbc.batch_size = 50
}

switch (System.getProperty("db")) {
    case "h2":
        println "Configuring for H2..."
        dataSource {
            driverClassName = "org.h2.Driver"
            username = "sa"
            password = ""
        }
        environments {
            development {
                dataSource {
                    dbCreate = "create"
                    url = "jdbc:h2:mem:irclog_dev;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE;MODE=PostgreSQL"
                }
            }
            test {
                dataSource {
                    dbCreate = "create"
                    url = "jdbc:h2:mem:irclog_test;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE;MODE=PostgreSQL"
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
            dialect = irclog.helper.MyPostgreSQLDialect
            username = "postgres"
            password = ""
            pooled = true
            jmxExport = true
            properties {
                // See http://grails.org/doc/latest/guide/conf.html#dataSource for documentation
                jmxEnabled = true
                initialSize = 5
                maxActive = 50
                minIdle = 5
                maxIdle = 25
                maxWait = 10000
                maxAge = 10 * 60000 // NOTE: The use of "maxAge" and "pool" for h2:mem causes vanishing database.
                timeBetweenEvictionRunsMillis = 5000
                minEvictableIdleTimeMillis = 60000
                validationQuery = "SELECT 1"
                validationQueryTimeout = 3
                validationInterval = 15000
                testOnBorrow = true
                testWhileIdle = true
                testOnReturn = false
                jdbcInterceptors = "ConnectionState"
                defaultTransactionIsolation = java.sql.Connection.TRANSACTION_READ_COMMITTED
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
                    driverClassName = "org.h2.Driver"
                    username = "sa"
                    password = ""
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

