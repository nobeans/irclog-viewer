databaseChangeLog = {

    changeSet(author: "irclog", id: "1367341698151-1") {
        comment "Added ircbot_state for rejoining"

        createTable(tableName: "ircbot_state") {
            column(name: "id", type: "int8") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "ircbot_statePK")
            }

            column(name: "date_created", type: "timestamp") {
                constraints(nullable: "false")
            }
        }
        createTable(tableName: "ircbot_state_channel") {
            column(name: "ircbot_state_id", type: "int8") {
                constraints(nullable: "false")
            }

            column(name: "channel_name", type: "varchar(255)")
        }
        addForeignKeyConstraint(baseColumnNames: "ircbot_state_id", baseTableName: "ircbot_state_channel", constraintName: "FKC88FD5C33D3C5A5B", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "ircbot_state", referencesUniqueColumn: "false")
        createSequence(sequenceName: "ircbot_state_id_seq")
    }

    changeSet(author: "irclog", id: "1367341698151-99") {
        tagDatabase(tag: "v0.4")
    }
}
