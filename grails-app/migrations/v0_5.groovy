import liquibase.statement.core.RawSqlStatement

def raw = { String sql ->
    new RawSqlStatement(sql)
}

databaseChangeLog = {

    changeSet(author: "irclog", id: "1372753569626-1") {
        comment "Fixed relation between person and channel"
        // because converting data was forgotton...

        grailsChange {
            change {
                // Data convert person_channel (old) -> channel_persons (new)
                sqlStatement raw("INSERT INTO channel_persons (person_channels_id, channel_id) SELECT person_channels_id, channel_id FROM person_channel")
                sqlStatement raw("DROP TABLE person_channel")
                sqlStatement raw("ALTER TABLE channel_persons RENAME COLUMN person_channels_id TO person_id")
            }
            rollback {
                // Data convert channel_persons (new) -> person_channel (old)
                // Nobody knows that which data is originaly on the channel_persons and
                // which data comes from person_channel.
                // So all data brings back to person_channel table and
                // truncate channel_persons thoroughly.
                sqlStatement raw("ALTER TABLE channel_persons RENAME COLUMN person_id TO person_channels_id")
                sqlStatement raw("CREATE TABLE person_channel (person_channels_id bigint NOT NULL, channel_id bigint NOT NULL)")
                sqlStatement raw("INSERT INTO person_channel (person_channels_id, channel_id) SELECT person_channels_id, channel_id FROM channel_persons")
                sqlStatement raw("TRUNCATE TABLE channel_persons")
            }
        }
    }

//    changeSet(author: "irclog", id: "1372753569626-99") {
//        tagDatabase(tag: "v0.5")
//    }
}
