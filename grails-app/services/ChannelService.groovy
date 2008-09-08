class ChannelService {

    def getAccessableChannels() {
        Channel.findAll('from Channel as c where c.isPrivate = false order by c.name')
    }

}
