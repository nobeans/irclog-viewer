databaseChangeLog = {

    changeSet(author: "irclog", id: "1366296925241-1") {
        comment "Restructured associations between domain classes"

        createTable(tableName: "channel_persons") {
            column(name: "channel_id", type: "int8") {
                constraints(nullable: "false")
            }

            column(name: "person_channels_id", type: "int8") {
                constraints(nullable: "false")
            }
        }
        addPrimaryKey(columnNames: "channel_id, person_channels_id", tableName: "channel_persons")
        addNotNullConstraint(columnDataType: "int8", columnName: "channel_id", tableName: "person_channel")
        addNotNullConstraint(columnDataType: "int8", columnName: "person_channels_id", tableName: "person_channel")
        addNotNullConstraint(columnDataType: "int4", columnName: "total", tableName: "summary")
        addForeignKeyConstraint(baseColumnNames: "channel_id", baseTableName: "channel_persons", constraintName: "FK7C2B4082FE840176", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "channel", referencesUniqueColumn: "false")
        addForeignKeyConstraint(baseColumnNames: "person_channels_id", baseTableName: "channel_persons", constraintName: "FK7C2B40823582339", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "person", referencesUniqueColumn: "false")
    }
}
