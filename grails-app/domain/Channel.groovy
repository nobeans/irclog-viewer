class Channel {

    String name
    String description
    Boolean isPrivate
    String secretKey

    public String toString() {
        return """${name} {
    description: ${description}
    isPrivate: ${isPrivate}
    secretKey: ${(secretKey) ? '****' : ''}
}"""
    }

    static constraints = {
        name(blank:false, unique:true, maxSize:100, matches:"^#.*")
        description()
        isPrivate()
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
