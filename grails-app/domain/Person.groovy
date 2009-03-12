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
        loginName(blank:false, matches:"[a-zA-Z0-9_-]{3,}+", unique:true, maxSize:255)
        realName(blank:false, unique:true, maxSize:255)
        password(blank:false, minSize:6, maxSize:255, validator:{ val, obj -> obj.repassword == val })
        nicks(matches:"[ 0-9a-zA-Z_-]*", validator:{ val, obj -> val == "" || val.split(/ +/).every{ it.startsWith(obj.loginName) } }, maxSize:255)
        color(matches:"#[0-9A-Fa-f]{3}|#[0-9A-Fa-f]{6}", maxSize:255)
        enabled()
        roles(size:1..1) // ロールは一人ひとつまで
        channels()
    }

    static mapping = {
        roles(column:'roles_id', joinTable:'role_person')
        channels(column:'person_channels_id', joinTable:'person_channel')
    }

    boolean isAdmin() {
        return (roles?.find{it.name == "ROLE_ADMIN"}) != null
    }
}
