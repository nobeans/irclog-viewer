/**
 * インポート対象のログファイルを収集する。
 */
class LogFileRetriever {

    Collection<File> retrieve(File irclogDir) {
        if (!irclogDir.isDirectory()) {
            throw new RuntimeException("Irclog directory not found.")
        }

        def targets = []
        irclogDir.eachFile { file ->
            // TODO:ディレクトリパスやファイル名からすでにコンプリートしたログかどうか判別。
            // TODO:コンプリート済みの場合、無視して次のループへ。
            targets << file
        }
        targets
    }

}
