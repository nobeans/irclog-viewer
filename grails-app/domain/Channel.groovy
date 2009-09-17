class Channel {

    String name
    String description
    Boolean isPrivate
    Boolean isArchived
    String secretKey

    public String toString() {
        return """${name} {
    description: ${description}
    isPrivate: ${isPrivate}
    isArchived: ${isArchived}
    secretKey: ${(secretKey) ? '****' : ''}
}"""
    }

    static constraints = {
        name(blank:false, unique:true, maxSize:100, matches:"^#.*")
        description()
        isPrivate()
        isArchived()
        secretKey(maxSize:100, validator:{ val, obj ->
            if (obj.isPrivate) {
                return val != ''
            } else {
                return val == ''
            }
        })
    }

    static mapping = {
        description(type:'text')
    }
}
