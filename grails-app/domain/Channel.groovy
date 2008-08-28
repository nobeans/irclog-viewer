class Channel {

    String name
    String description
    Boolean isPublic

    static constraints = {
        name(nullable:false, blank:false)
        description(nullable:false, blank:true)
        isPublic(nullable:false)
    }

}
