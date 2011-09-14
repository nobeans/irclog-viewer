package irclog

/**
 * チャンネル管理コントローラ
 */
class ChannelController {

    static allowedMethods = [delete:'POST', save:'POST', update:'POST', join:'POST']
    def channelService
    def summaryService

    def index() { redirect(action:'list', params:params) }

    def list() {
        [
            channelList: channelService.getAccessibleChannelList(request.loginUserDomain, params),
            allJoinedPersons: channelService.getAllJoinedPersons(),
            nickPersonList: Person.list()
        ]
    }

    def show() {
        withChannel(params.id) { channel ->
            [
                channel: channel,
                joinedPersons: channelService.getJoinedPersons(channel)
            ]
        }
    }

    def delete() {
        withChannel(params.id) { channel ->
            channelService.deleteChannel(channel)
            flash.message = "channel.deleted"
            flash.args = [params.id]
            redirect(action:'list')
        }
    }

    def edit() {
        withChannel(params.id) { channel ->
            [channel:channel]
        }
    }

    def update() {
        withChannel(params.id) { channel ->
            channel.properties = params
            if (!channel.hasErrors() && channel.save()) {
                // インポート済みログに対して取りこぼしがあれば関連づける
                int relatedCount = channelService.relateToIrclog(channel)
                log.info("Count of irclog records related to the updated channel: channel=${channel.name}, count=${relatedCount}")

                // 非公開チャンネルの場合、今のユーザを自動的に関連づける(上書き可)
                // 公開チャンネルの場合、対象チャンネルに対する全関連付けを削除しても良いが、
                // 間違って非公開→公開→非公開とすると、関連付けが全部クリアされてしまい
                // 運用上困るかもしれないため、関連付けはそのまま残す。
                if (channel.isPrivate) {
                    Person.get(request.loginUserDomain.id).addToChannels(channel) // TODO: request.loginUserDomain.addToChannels()ではどうか？
                }

                flash.message = "channel.updated"
                flash.args = [channel.id]
                redirect(action:'show', id:channel.id)
            }
            else {
                render(view:'edit', model:[channel:channel])
            }
        }
    }

    def create() {
        def channel = new Channel()
        channel.isPrivate = true // デフォルトは安全サイドに。
        return ['channel':channel]
    }

    def save() {
        def channel = new Channel(params)
        if (!channel.hasErrors() && channel.save()) {
            // インポート済みログに対して取りこぼしがあれば関連づける
            int relatedCount = channelService.relateToIrclog(channel)
            log.info("Count of irclog records related to the created channel: channel=${channel.name}, count=${relatedCount}")

            // 非公開チャンネルの場合、今のユーザを自動的に関連づける
            // 公開チャンネルの場合、対象チャンネルに対する全関連付けを削除しても良いが、
            // 間違って非公開→公開→非公開とすると、関連付けが全部クリアされてしまい
            // 運用上困るかもしれないため、関連付けはそのまま残す。
            if (channel.isPrivate) {
                Person.get(request.loginUserDomain.id).addToChannels(channel)
            }

            // 全サマリ更新を実行して、新規チャンネルのログが既に存在する場合にサマリも生成する。
            summaryService.updateAllSummary()

            flash.message = "channel.created"
            flash.args = [channel.id]
            redirect(action:'show', id:channel.id)
        }
        else {
            render(view:'create', model:[channel:channel])
        }
    }

    def join() {
        def channel = Channel.findByNameAndSecretKey(params.channelName, params.secretKey)
        if (!channel) {
            flash.errors = ["channel.join.error"]
            flash.args = [params.channelName]
        } else {
            Person.get(request.loginUserDomain.id).addToChannels(channel)
            flash.message = "channel.joined"
            flash.args = [params.channelName]
        }
        redirect(action:'list')
    }

    def part() {
        withChannel(params.id) { channel ->
            Person.get(request.loginUserDomain.id).removeFromChannels(channel)
            flash.message = "channel.parted"
            redirect(action:'show', id:channel.id)
        }
    }

    def kick() {
        withChannel(params.id) { channel ->
            def person = Person.get(params.personId)
            if (!person) {
                flash.errors = ["person.not.found"]
                flash.args = [personId]
                redirect(action:'list')
                return
            }
            person.removeFromChannels(channel)
            flash.message = "channel.kicked"
            flash.args = [person.loginName]
            redirect(action:'show', id:channel.id)
        }
    }

    private withChannel(channelId, closure) {
        def channel = Channel.get(channelId)
        if (!channel || !isAccessibleChannel(channel)) {
            flash.errors = ["channel.not.found"]
            flash.args = [channelId]
            redirect(action:'list')
            return
        }
        closure(channel)
    }

    private isAccessibleChannel(channel) {
        channelService.getAccessibleChannelList(request.loginUserDomain, params).contains(channel)
    }
}
