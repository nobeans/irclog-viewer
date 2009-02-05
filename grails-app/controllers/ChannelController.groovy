/**
 * チャンネル管理コントローラ
 */
class ChannelController extends Base {

    def allowedMethods = [delete:'POST', save:'POST', update:'POST']
    def channelService
    
    def index = { redirect(action:list, params:params) }

    def list = {
        [
            channelList: channelService.getAccessibleChannelList(loginUserDomain, params),
            allJoinedPersons: channelService.getAllJoinedPersons()
        ]
    }

    def show = {
        def channel = Channel.get(params.id)
        if (!channel || !isAccessibleChannel(channel)) {
            flash.errors = ["channel.not.found"]
            flash.args = [params.id]
            redirect(action:list)
        }
        else {
            return [ channel : channel ]
        }
    }

    def delete = {
        def channel = Channel.get(params.id)
        if (!channel || !isAccessibleChannel(channel)) {
            flash.errors = ["channel.not.found"]
            flash.args = [params.id]
            redirect(action:list)
        }
        else {
            channelService.deleteChannel(channel)
            flash.errors = ["channel.not.found"]
            flash.args = [params.id]
            redirect(action:list)
        }
    }

    def edit = {
        def channel = Channel.get(params.id)
        if (!channel || !isAccessibleChannel(channel)) {
            flash.errors = ["channel.not.found"]
            flash.args = [params.id]
            redirect(action:list)
        }
        else {
            return [ channel : channel ]
        }
    }

    def update = {
        def channel = Channel.get(params.id)
        if (!channel || !isAccessibleChannel(channel)) {
            flash.errors = ["channel.not.found"]
            flash.args = [params.id]
            redirect(action:edit,id:params.id)
        }
        else {
            channel.properties = params
            if (!channel.hasErrors() && channel.save()) {
                flash.message = "channel.updated"
                flash.args = [params.id]

                // インポート済みログに対して取りこぼしがあれば関連づける
                channelService.relateToIrclog(channel)

                // 非公開チャンネルの場合、今のユーザを自動的に関連づける(上書き可)
                // 公開チャンネルの場合、対象チャンネルに対する全関連付けを削除しても良いが、
                // 間違って非公開→公開→非公開とすると、関連付けが全部クリアされてしまい
                // 運用上困るかもしれないため、関連付けはそのまま残す。
                if (channel.isPrivate) {
                    Person.get(loginUserDomain.id).addToChannels(channel)
                }

                redirect(action:show,id:channel.id)
            }
            else {
                render(view:'edit',model:[channel:channel])
            }
        }
    }

    def create = {
        def channel = new Channel()
        channel.isPrivate = true // デフォルトは安全サイドに。
        return ['channel':channel]
    }

    def save = {
        def channel = new Channel(params)
        if (!channel.hasErrors() && channel.save()) {
            flash.message = "channel.created"
            flash.args = ["${channel.id}"]

            // インポート済みログに対して取りこぼしがあれば関連づける
            channelService.relateToIrclog(channel)

            // 非公開チャンネルの場合、今のユーザを自動的に関連づける
            // 公開チャンネルの場合、対象チャンネルに対する全関連付けを削除しても良いが、
            // 間違って非公開→公開→非公開とすると、関連付けが全部クリアされてしまい
            // 運用上困るかもしれないため、関連付けはそのまま残す。
            if (channel.isPrivate) {
                Person.get(loginUserDomain.id).addToChannels(channel)
            }

            redirect(action:show,id:channel.id)
        }
        else {
            render(view:'create',model:[channel:channel])
        }
    }

    def join = {
        def channel = Channel.findByNameAndSecretKey(params.channelName, params.secretKey)
        if (!channel || !isAccessibleChannel(channel)) {
            flash.errors = ["channel.join.error"]
            flash.args = [params.channelName]
        } else {
            Person.get(loginUserDomain.id).addToChannels(channel)
            flash.message = "channel.joined"
            flash.args = [params.channelName]
        }
        redirect(action:list)
    }
    
    private isAccessibleChannel(channel) {
        channelService.getAccessibleChannelList(loginUserDomain, params).contains(channel)
    }
}
