class Person {

    String loginName
    String password
    String repassword // 確認用(DBには保存しない)
    boolean enabled   // ユーザの有効/無効
    String nicks // 複数のnicksをカンマ区切りで。
    String color // ログ表示時の色づけ(不要かも)

    static hasMany = [channels:Channel, roles:Role]

    static belongsTo = Role

    static transients = ['repassword']

    static constraints = {
        loginName(blank:false, matches:"[a-zA-Z0-9_-]{6,}+", unique:true)
        password(blank:false, minSize:6, validator:{ val, obj -> obj.repassword == val })
        nicks()
        color(matches:"#[0-9A-Fa-f]{3}|#[0-9A-Fa-f]{6}")
        enabled()
        roles()
        channels()
    }

}
