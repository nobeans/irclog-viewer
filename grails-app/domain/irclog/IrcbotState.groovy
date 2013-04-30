package irclog

class IrcbotState {

    Date dateCreated

    static hasMany = [channels: String]

    static mapping = {
        version false
        channels joinTable: [
            name: 'ircbot_state_channel',
            key: 'ircbot_state_id',
            column: 'channel_name'
        ]
    }

    @Override
    String toString() {
        "${channels}@${dateCreated?.format('yyyy-MM-dd HH:mm:ss.SSS')}"
    }
}
