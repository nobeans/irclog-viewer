class ChannelService {

    def getAccessableChannels() {
        Channel.findAll('from Channel as c where c.isPublic = true order by c.name')
    }

}
