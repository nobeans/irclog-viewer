import liquibase.statement.core.RawSqlStatement

def raw = { String sql ->
    new RawSqlStatement(sql)
}

databaseChangeLog = {

    changeSet(author: "irclog", id: "1355793743722-1") {
        comment "Changed relationship between role and person to many-to-one without a join table"

        // adding role_id to person
        addColumn(tableName: "person") {
            column(name: "role_id", type: "int8") {
                constraints(nullable: true)
            }
        }
        grailsChange {
            change {
                sqlStatement raw("UPDATE person SET role_id = (SELECT role_person.persons_id FROM role_person WHERE person.id = role_person.roles_id)")
                sqlStatement raw("ALTER TABLE person ALTER COLUMN role_id SET NOT NULL")
            }
        }
        addForeignKeyConstraint(baseColumnNames: "role_id", baseTableName: "person", constraintName: "FKC4E39B55F84F28DE", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "role", referencesUniqueColumn: "false")

        // deleting role_person
        grailsChange {
            change {
                sqlStatement raw("DROP TABLE role_person")
            }
            rollback {
                sqlStatement raw("CREATE TABLE role_person (persons_id bigint NOT NULL, roles_id bigint NOT NULL)") // persons_id = role#id, roles_id = person#id
                sqlStatement raw("INSERT INTO role_person (persons_id, roles_id) SELECT role_id, id FROM person")
                sqlStatement raw("ALTER TABLE ONLY role_person ADD CONSTRAINT role_person_pkey PRIMARY KEY (persons_id, roles_id)")
                sqlStatement raw("ALTER TABLE ONLY role_person ADD CONSTRAINT fk28b8c73e28a895d2 FOREIGN KEY (persons_id) REFERENCES role(id)")
                sqlStatement raw("ALTER TABLE ONLY role_person ADD CONSTRAINT fk28b8c73e7e729552 FOREIGN KEY (roles_id) REFERENCES person(id)")
            }
        }
    }

    changeSet(author: "irclog", id: "1355793743722-2") {
        comment "Restructured summary"

        renameColumn(tableName: "summary", oldColumnName: "today", newColumnName: "today_")

        addColumn(tableName: "summary") {
            column(name: "total", type: "int4", value: 0) {
                constraints(nullable: true)
            }
        }

        dropNotNullConstraint(columnDataType: "int8", columnName: "latest_irclog_id", tableName: "summary")
    }

    changeSet(author: "irclog", id: "1355793743722-3") {
        comment "Dropped unnecessary indexes"

        grailsChange {
            change {
                sqlStatement raw("DROP INDEX channel__is_archived__idx")
                sqlStatement raw("DROP INDEX irclog__channel_id__time__idx")
                sqlStatement raw("DROP INDEX irclog__type__idx")
                sqlStatement raw("DROP INDEX person__enabled__idx")
            }
            rollback {
                sqlStatement raw("CREATE INDEX channel__is_archived__idx ON channel USING btree (is_archived)")
                sqlStatement raw('CREATE INDEX irclog__channel_id__time__idx ON irclog USING btree (channel_id, "time")')
                sqlStatement raw("CREATE INDEX irclog__type__idx ON irclog USING btree (type)")
                sqlStatement raw("CREATE INDEX person__enabled__idx ON person USING btree (enabled)")
            }
        }
    }

    changeSet(author: "irclog", id: "1355793743722-4") {
        comment "Added unique indexes"

        createIndex(indexName: "real_name_unique_1355793743519", tableName: "person", unique: "true") {
            column(name: "real_name")
        }
        createIndex(indexName: "channel_id_unique_1355793743527", tableName: "summary", unique: "true") {
            column(name: "channel_id")
        }
    }

    changeSet(author: "irclog", id: "1355793743722-5") {
        comment "Removed unnecessary column: role.description"

        grailsChange {
            change {
                sqlStatement raw("ALTER TABLE role DROP COLUMN description")
            }
            rollback {
                sqlStatement raw("ALTER TABLE role ADD COLUMN description TEXT")
                sqlStatement raw("UPDATE role SET description = ''")
                sqlStatement raw("ALTER TABLE role ALTER COLUMN description SET NOT NULL")
            }
        }
    }

    changeSet(author: "irclog", id: "1355793743722-6") {
        comment "Using individual sequences instead of hibernate_sequence"

        createSequence(sequenceName: "channel_id_seq")
        createSequence(sequenceName: "irclog_id_seq")
        createSequence(sequenceName: "person_id_seq")
        createSequence(sequenceName: "role_id_seq")
        createSequence(sequenceName: "summary_id_seq")
        grailsChange {
            change {
                sqlStatement raw("SELECT setval('channel_id_seq', (SELECT COALESCE(MAX(id), 0) + 1 FROM channel))")
                sqlStatement raw("SELECT setval('irclog_id_seq',  (SELECT COALESCE(MAX(id), 0) + 1 FROM irclog))")
                sqlStatement raw("SELECT setval('person_id_seq',  (SELECT COALESCE(MAX(id), 0) + 1 FROM person))")
                sqlStatement raw("SELECT setval('role_id_seq',    (SELECT COALESCE(MAX(id), 0) + 1 FROM role))")
                sqlStatement raw("SELECT setval('summary_id_seq', (SELECT COALESCE(MAX(id), 0) + 1 FROM summary))")
            }
        }

        grailsChange {
            change {
                sqlStatement raw("DROP SEQUENCE hibernate_sequence")
            }
            rollback {
                sqlStatement raw("CREATE SEQUENCE hibernate_sequence START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1")
                sqlStatement raw("""\
                    |SELECT setval('hibernate_sequence', (
                    |    SELECT MAX(value) FROM (
                    |              SELECT COALESCE(MAX(id), 0) + 1 AS value FROM channel
                    |        UNION SELECT COALESCE(MAX(id), 0) + 1 AS value FROM irclog
                    |        UNION SELECT COALESCE(MAX(id), 0) + 1 AS value FROM person
                    |        UNION SELECT COALESCE(MAX(id), 0) + 1 AS value FROM role
                    |        UNION SELECT COALESCE(MAX(id), 0) + 1 AS value FROM summary
                    |    ) AS t
                    |))
                    |""".stripMargin())
            }
        }
    }

    changeSet(author: "irclog", id: "1355793743722-99") {
        tagDatabase(tag: "v0.2")
    }
}
