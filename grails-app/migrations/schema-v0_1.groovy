databaseChangeLog = {
    changeSet(author: "irclog", id: "1355792844804-1") {
        comment "basic data structure for v0.1"

        createTable(tableName: "channel") {
            column(name: "id", type: "int8") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "channel_pkey")
            }
            column(name: "version", type: "int8") {
                constraints(nullable: "false")
            }
            column(name: "description", type: "TEXT") {
                constraints(nullable: "false")
            }
            column(name: "is_private", type: "bool") {
                constraints(nullable: "false")
            }
            column(name: "name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "secret_key", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "is_archived", type: "bool") {
                constraints(nullable: "false")
            }
        }
        createTable(tableName: "irclog") {
            column(name: "id", type: "int8") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "irclog_pkey")
            }
            column(name: "channel_id", type: "int8")
            column(name: "channel_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "message", type: "TEXT") {
                constraints(nullable: "false")
            }
            column(name: "nick", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "perma_id", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "time", type: "TIMESTAMP WITH TIME ZONE") {
                constraints(nullable: "false")
            }
            column(name: "type", type: "VARCHAR(7)") {
                constraints(nullable: "false")
            }
        }
        createTable(tableName: "person") {
            column(name: "id", type: "int8") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "person_pkey")
            }
            column(name: "version", type: "int8") {
                constraints(nullable: "false")
            }
            column(name: "color", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "enabled", type: "bool") {
                constraints(nullable: "false")
            }
            column(name: "login_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "nicks", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "password", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "real_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }
        createTable(tableName: "person_channel") {
            column(name: "person_channels_id", type: "int8")
            column(name: "channel_id", type: "int8")
        }
        createTable(tableName: "role") {
            column(name: "id", type: "int8") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "role_pkey")
            }
            column(name: "version", type: "int8") {
                constraints(nullable: "false")
            }
            column(name: "description", type: "TEXT") {
                constraints(nullable: "false")
            }
            column(name: "name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }
        createTable(tableName: "role_person") {
            column(name: "persons_id", type: "int8") {
                constraints(nullable: "false")
            }
            column(name: "roles_id", type: "int8") {
                constraints(nullable: "false")
            }
        }
        createTable(tableName: "summary") {
            column(name: "id", type: "int8") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "summary_pkey")
            }
            column(name: "channel_id", type: "int8") {
                constraints(nullable: "false")
            }
            column(name: "five_days_ago", type: "int4") {
                constraints(nullable: "false")
            }
            column(name: "four_days_ago", type: "int4") {
                constraints(nullable: "false")
            }
            column(name: "last_updated", type: "TIMESTAMP WITH TIME ZONE") {
                constraints(nullable: "false")
            }
            column(name: "latest_irclog_id", type: "int8") {
                constraints(nullable: "false")
            }
            column(name: "six_days_ago", type: "int4") {
                constraints(nullable: "false")
            }
            column(name: "three_days_ago", type: "int4") {
                constraints(nullable: "false")
            }
            column(name: "today", type: "int4") {
                constraints(nullable: "false")
            }
            column(name: "total_before_yesterday", type: "int4") {
                constraints(nullable: "false")
            }
            column(name: "two_days_ago", type: "int4") {
                constraints(nullable: "false")
            }
            column(name: "yesterday", type: "int4") {
                constraints(nullable: "false")
            }
        }

        addPrimaryKey(columnNames: "persons_id, roles_id", constraintName: "role_person_pkey", tableName: "role_person")

        addUniqueConstraint(columnNames: "name", constraintName: "channel_name_key", deferrable: "false", disabled: "false", initiallyDeferred: "false", tableName: "channel")
        addUniqueConstraint(columnNames: "perma_id", constraintName: "irclog_perma_id_key", deferrable: "false", disabled: "false", initiallyDeferred: "false", tableName: "irclog")
        addUniqueConstraint(columnNames: "login_name", constraintName: "person_login_name_key", deferrable: "false", disabled: "false", initiallyDeferred: "false", tableName: "person")
        addUniqueConstraint(columnNames: "name", constraintName: "role_name_key", deferrable: "false", disabled: "false", initiallyDeferred: "false", tableName: "role")

        addForeignKeyConstraint(baseColumnNames: "channel_id", baseTableName: "irclog", baseTableSchemaName: "public", constraintName: "fkb9a1f4aa1c2e46ba", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "channel", referencedTableSchemaName: "public", referencesUniqueColumn: "false")
        addForeignKeyConstraint(baseColumnNames: "channel_id", baseTableName: "person_channel", baseTableSchemaName: "public", constraintName: "fk946c9a191c2e46ba", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "channel", referencedTableSchemaName: "public", referencesUniqueColumn: "false")
        addForeignKeyConstraint(baseColumnNames: "person_channels_id", baseTableName: "person_channel", baseTableSchemaName: "public", constraintName: "fk946c9a19886e2575", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "person", referencedTableSchemaName: "public", referencesUniqueColumn: "false")
        addForeignKeyConstraint(baseColumnNames: "persons_id", baseTableName: "role_person", baseTableSchemaName: "public", constraintName: "fk28b8c73e28a895d2", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "role", referencedTableSchemaName: "public", referencesUniqueColumn: "false")
        addForeignKeyConstraint(baseColumnNames: "roles_id", baseTableName: "role_person", baseTableSchemaName: "public", constraintName: "fk28b8c73e7e729552", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "person", referencedTableSchemaName: "public", referencesUniqueColumn: "false")
        addForeignKeyConstraint(baseColumnNames: "channel_id", baseTableName: "summary", baseTableSchemaName: "public", constraintName: "fk9146a7a61c2e46ba", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "channel", referencedTableSchemaName: "public", referencesUniqueColumn: "false")
        addForeignKeyConstraint(baseColumnNames: "latest_irclog_id", baseTableName: "summary", baseTableSchemaName: "public", constraintName: "fk9146a7a648c32e2", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "irclog", referencedTableSchemaName: "public", referencesUniqueColumn: "false")

        createIndex(indexName: "channel__is_archived__idx", tableName: "channel", unique: "false") {
            column(name: "is_archived")
        }
        createIndex(indexName: "irclog__channel_id__time__idx", tableName: "irclog", unique: "false") {
            column(name: "channel_id")
            column(name: "time")
        }
        createIndex(indexName: "irclog__type__idx", tableName: "irclog", unique: "false") {
            column(name: "type")
        }
        createIndex(indexName: "person__enabled__idx", tableName: "person", unique: "false") {
            column(name: "enabled")
        }

        createSequence(schemaName: "public", sequenceName: "hibernate_sequence")
    }
}
