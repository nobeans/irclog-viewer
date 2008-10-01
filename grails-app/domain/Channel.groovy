class Channel {

    String name
    String description
    Boolean isPrivate
    String secretKey

    static constraints = {
        name(blank:false)
        description()
        isPrivate()
        secretKey()
    }

    static mapping = {
        description(type:'text')
    }
}
