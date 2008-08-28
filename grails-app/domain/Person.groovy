class Person {

    String loginName
    String password
    String nicks // 複数のnicksをカンマ区切りで。
    String color // ログ表示時の色づけ

    static hasMany = [channels:Channel]

    static constraints = {
        loginName(nullable:false, blank:false)
        password(nullable:false, blank:false)
        nicks(nullable:false, blank:true)
        color(nullable:false, blank:true, matches:"#[0-9A-Fa-f]{3}|#[0-9A-Fa-f]{6}")
    }
}
