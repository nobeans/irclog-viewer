package irclog

import irclog.security.SpringSecurityContext
import grails.transaction.Transactional

/**
 * チャンネル管理コントローラ
 */
@Transactional(readOnly = true)
class ChannelController implements SpringSecurityContext {

    static allowedMethods = [delete: 'POST', save: 'POST', update: 'POST', join: 'POST']

    ChannelService channelService

    def index() { redirect(action: 'list', params: params) }

    def list() {
        [
            channelList: channelService.getAccessibleChannelList(currentUser, params),
            nickPersonList: Person.list()
        ]
    }

    def show() {
        withChannel(params.id) { channel ->
            [channel: channel]
        }
    }

    @Transactional
    def delete() {
        withChannel(params.id) { channel ->
            channel.delete(flush: true)
            flash.message = message(code: "channel.deleted.message")
            redirect(action: 'list')
        }
    }

    def edit() {
        withChannel(params.id) { channel ->
            [channel: channel]
        }
    }

    @Transactional
    def update() {
        withChannel(params.id) { Channel channel ->
            channel.properties = params
            if (channel.save(flush: true)) {
                // インポート済みログに対して取りこぼしがあれば関連づける
                int relatedCount = channelService.relateToIrclog(channel)
                log.info("Count of irclog records related to the updated channel: channel=${channel.name}, count=${relatedCount}")

                flash.message = message(code: "default.updated.message", args: [message(code: "channel.label"), params.id])
                redirect(action: 'show', id: channel.id)
            } else {
                render(view: 'edit', model: [channel: channel])
            }
        }
    }

    def create() {
        def channel = new Channel()
        channel.isPrivate = true // デフォルトは安全サイドに。
        return ['channel': channel]
    }

    @Transactional
    def hoge() {
        def r = new Channel()
        //def r = new IrcbotState()
        r.save(flush: true)


//        def s = new Summary()
//        s.save(flush:true)

//        def channel = new Channel()
//        println channel.dump()
//        channel.summary.save(flush: true)
//        channel.save(flush: true)
        render text: "OK: ${r.class}: ${r.errors}"
    }

    @Transactional
    def save() {
        def channel = new Channel(params)
//        def channel = new Channel()
        println ">"*50
        println channel?.dump()
        println "-"*50
        println channel?.validate()
        println "-"*50
        try {
        println channel?.save()
        } catch (Throwable e) {
            e.printStackTrace()
        }
        System.err.flush()
        println "<"*50
        if (channel.save(flush: true)) {
            // インポート済みログに対して取りこぼしがあれば関連づける
            int relatedCount = channelService.relateToIrclog(channel)
            log.info("Count of irclog records related to the created channel: channel=${channel.name}, count=${relatedCount}")

            // 非公開チャンネルの場合、今のユーザを自動的に関連づける
            // 公開チャンネルの場合、対象チャンネルに対する全関連付けを削除しても良いが、
            // 間違って非公開→公開→非公開とすると、関連付けが全部クリアされてしまい
            // 運用上困るかもしれないため、関連付けはそのまま残す。
            if (channel.isPrivate) {
                channel.addToPersons(currentUser)
            }

            // TODO 全サマリが更新されるのは深夜のバッチの後だ、というメッセージを表示する

            flash.message = message(code: "default.created.message", args: [message(code: "channel.label"), params.id])
            redirect(action: 'show', id: channel.id)
        } else {
            render(view: 'create', model: [channel: channel])
        }
    }

    @Transactional
    def join() {
        def channel = Channel.findByNameAndSecretKey(params.channelName, params.secretKey)
        if (!channel) {
            flash.errors = [message(code: "channel.join.error", args: [params.channelName])]
        } else {
            channel.addToPersons(currentUser)
            flash.message = message(code: "channel.joined.message", args: [params.channelName])
        }
        redirect(action: 'list')
    }

    @Transactional
    def part() {
        withChannel(params.id) { channel ->
            currentUser.removeFromChannels(channel)
            flash.message = message(code: "channel.parted.message")
            redirect(action: 'show', id: channel.id)
        }
    }

    @Transactional
    def kick() {
        withChannel(params.id) { channel ->
            def person = Person.get(params.personId)
            if (!person) {
                flash.errors = [message(code: "default.not.found.message", args: [message(code: "person.label"), personId])]
                redirect(action: 'list')
                return
            }
            person.removeFromChannels(channel)
            flash.message = message(code: "channel.kicked.message", args: [person.loginName])
            redirect(action: 'show', id: channel.id)
        }
    }

    private withChannel(channelId, closure) {
        def channel = Channel.get(channelId)
        if (!channel || !channelService.getAccessibleChannelList(currentUser, [:]).contains(channel)) {
            flash.errors = [message(code: "default.not.found.message", args: [message(code: "channel.label"), channelId])]
            redirect(action: 'list')
            return
        }
        closure(channel)
    }
}
