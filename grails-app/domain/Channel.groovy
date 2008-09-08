class Channel {

    String name
    String description
    Boolean isPrivate
    String secretKey

    static constraints = {
        name(nullable:false, blank:false)
        description(nullable:false, blank:true)
        isPrivate(nullable:false)
        secretKey(nullable:false, blank:true)
    }

}
