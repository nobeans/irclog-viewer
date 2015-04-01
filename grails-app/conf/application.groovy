//
// irclog-viewer
//
import org.jggug.kobo.gircbot.reactors.Debugger
import org.jggug.kobo.gircbot.reactors.Dictionary
import org.jggug.kobo.gircbot.reactors.InviteAndByeResponder
import org.jggug.kobo.gircbot.reactors.Logger
import org.jggug.kobo.gircbot.reactors.OpDistributor
import org.jggug.kobo.gircbot.jobs.Reminder
import irclog.vertx.VertxPublishLogAppender

irclog {
    viewer {
        defaultMax = 100
        typeVisible = true
    }

    ircbot {
        enable = true
        limitOfSavedStates = 7
        server {
            host = "localhost"
            port = 6667
        }
        nick = "ircbot"
        channel {
            autoJoinTo = ["LATEST_SAVED_CHANNELS", "#test1", "#test2", "#test4"]
            asDefault = "#lounge"
        }
        reactors = [
            new Dictionary(new File("${userHome}/.gircbot-dictionary")),
            new OpDistributor(),
            new InviteAndByeResponder(),
            new Logger(new VertxPublishLogAppender()),
        ]
        environments {
            development {
                reactors << new Debugger()
            }
        }
        jobs = [
            new Reminder(new File("${userHome}/.gircbot-reminder")),
        ]
    }
}

// FIXME: couldn't apply system properties via command arguments as "-Ddb=h2"
System.setProperty("db", "h2")

//
// DataSource
//
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = false
//    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory' // Hibernate 3
    cache.region.factory_class = 'org.hibernate.cache.ehcache.EhCacheRegionFactory' // Hibernate 4
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

