class Person {

    String loginName
    String password
    boolean enabled

    String nicks // 複数のnicksをカンマ区切りで。
    String color // ログ表示時の色づけ(不要かも)

    static hasMany = [channels:Channel, roles:Role]

    static belongsTo = Role

    static constraints = {
        loginName(blank:false, minSize:4)
        password(blank:false) // ハッシュが入るためここにサイズ指定しても無駄
        nicks()
        color(matches:"#[0-9A-Fa-f]{3}|#[0-9A-Fa-f]{6}")
        enabled()
        roles()
        channels()
    }
}
