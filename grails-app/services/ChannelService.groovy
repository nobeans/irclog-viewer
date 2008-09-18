class ChannelService {

    // アクセス可能な全チャンネルを取得する。
    def getAccessibleChannelList(person, params) {
        params.max = Channel.count() // 必ず全チャンネルが一度に取得できるように最大件数を設定する。
        if (!params.sort || !Channel.constraints.keySet().contains(params.sort)) params.sort = 'name'
        if (!params.order || !['asc', 'desc'].contains(params.order)) params.order = 'asc'
        if (person) {
            return Person.executeQuery("""
                select
                    ch
                from
                    Person as p
                right outer join
                    p.channels as ch
                where
                    p.id = ?
                or
                    (ch.isPrivate = false and p is null)
                order by
                    ch.${params.sort} ${params.order}
            """, person.id)
        } else {
            return Channel.executeQuery("""
                select
                    ch
                from
                    Channel as ch
                where
                    ch.isPrivate = false
                order by
                    ch.${params.sort} ${params.order}
            """)
        }
    }

}
