class Person {

    String loginName
    String password
    boolean enabled

    String nicks // 複数のnicksをカンマ区切りで。
    String color // ログ表示時の色づけ(不要かも)

    static hasMany = [channels:Channel, roles:Role]

    static belongsTo = Role

    static constraints = {
        loginName(nullable:false, blank:false)
        password(nullable:false, blank:false)
        nicks(nullable:false, blank:true)
        color(nullable:false, blank:true, matches:"#[0-9A-Fa-f]{3}|#[0-9A-Fa-f]{6}")
        enabled()
        roles()
        channels()
    }
}
