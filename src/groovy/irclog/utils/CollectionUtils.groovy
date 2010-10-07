package irclog.utils

class CollectionUtils {

    // 先頭の1要素を返す。サイズが0の場合はnullを返す。
    static getFirstOrNull(collection) {
        if (!collection) return null
        def list = collection as List
        return (list.size() == 0) ? null : list.first()
    }

    // とにかくList型に変換する。
    // 本メソッドを通せば、List型として取り扱うことができることを保証する。
    // 変換ルールはソース参照。
    static asList(list) {
        if (list == null) return [] // nullは空リスト
        if (list instanceof Object[]) return list as List
        if (list instanceof Collection) return list as List
        return [list] // それ以外は、対象オブジェクト1つだけを含むListとして返す
    }
}
