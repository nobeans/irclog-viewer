class Person {

    String loginName
    String realName
    String password
    String repassword // 確認用(DBには保存しない)
    boolean enabled   // ユーザの有効/無効
    String nicks // 複数のnicksをカンマ区切りで。
    String color // ログ表示時の色づけ(不要かも)

    static hasMany = [channels:Channel, roles:Role]

    static belongsTo = Role

    static transients = ['repassword', 'admin'] // isAdmin()がプロパティと誤解されてしまうのでtransientsに追加した。

    static constraints = {
        loginName(blank:false, matches:"[a-zA-Z0-9_-]{3,}+", unique:true)
        realName(blank:false, unique:true, maxSize:100)
        password(blank:false, minSize:6, validator:{ val, obj -> obj.repassword == val })
        nicks(matches:"[ 0-9a-zA-Z_-]*", validator:{ val, obj -> val == "" || val.split(/ +/).every{ it.startsWith(obj.loginName) } })
        color(matches:"#[0-9A-Fa-f]{3}|#[0-9A-Fa-f]{6}")
        enabled()
        roles(size:1..1) // ロールは一人ひとつまで
        channels()
    }

    boolean isAdmin() {
        return (roles?.find{it.name == "ROLE_ADMIN"}) != null
    }
}
