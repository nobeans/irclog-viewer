class ChannelController extends Base {
    
    def index = { redirect(action:list, params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        params.max = Channel.count() // 必ず全チャンネルが一度に取得できるように最大件数を設定する。
        if (!params.sort) params.sort = 'name'
        [ channelList: Channel.list( params ) ]
    }

    def show = {
        def channel = Channel.get( params.id )

        if(!channel) {
            flash.message = "channel.not.found"
            flash.args = [params.id]
            flash.defaultMessage = "Channel not found with id ${params.id}"
            redirect(action:list)
        }
        else { return [ channel : channel ] }
    }

    def delete = {
        def channel = Channel.get( params.id )
        if(channel) {
            channel.delete()
            flash.message = "channel.deleted"
            flash.args = [params.id]
            flash.defaultMessage = "Channel ${params.id} deleted"
            redirect(action:list)
        }
        else {
            flash.message = "channel.not.found"
            flash.args = [params.id]
            flash.defaultMessage = "Channel not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def channel = Channel.get( params.id )

        if(!channel) {
            flash.message = "channel.not.found"
            flash.args = [params.id]
            flash.defaultMessage = "Channel not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ channel : channel ]
        }
    }

    def update = {
        def channel = Channel.get( params.id )
        if(channel) {
            channel.properties = params
            if(!channel.hasErrors() && channel.save()) {
                flash.message = "channel.updated"
                flash.args = [params.id]
                flash.defaultMessage = "Channel ${params.id} updated"
                redirect(action:show,id:channel.id)
            }
            else {
                render(view:'edit',model:[channel:channel])
            }
        }
        else {
            flash.message = "channel.not.found"
            flash.args = [params.id]
            flash.defaultMessage = "Channel not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def channel = new Channel()
        channel.properties = params
        return ['channel':channel]
    }

    def save = {
        def channel = new Channel(params)
        if(!channel.hasErrors() && channel.save()) {
            flash.message = "channel.created"
            flash.args = ["${channel.id}"]
            flash.defaultMessage = "Channel ${channel.id} created"
            redirect(action:show,id:channel.id)
        }
        else {
            render(view:'create',model:[channel:channel])
        }
    }
}
