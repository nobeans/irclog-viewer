import irclog.Channel;

class BootStrap {

    def init = { servletContext ->
        (1..3).each {
            assert createChannel("#test$it").save()
        }
    }
    
    def destroy = {
    }
    
    private Channel createChannel(name) {
        def channel = new Channel(name:name)
        channel.description = "説明文です"
        channel.isPrivate = true
        channel.isArchived = true
        channel.secretKey = "1234"
        channel
    }
}
