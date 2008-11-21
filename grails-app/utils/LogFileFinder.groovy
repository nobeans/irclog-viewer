/**
 * インポート対象のログファイルを探す。
 */
class LogFileFinder {

    Collection<File> findAll(File irclogDir) {
        if (!irclogDir.isDirectory()) {
            throw new RuntimeException("Irclog directory not found.")
        }

        def targets = []
        irclogDir.eachFile { channelDir ->
            channelDir.eachFile { file ->
                // .logファイルのみ対象
                if (!(file.name ==~ /.*\.log/)) return

                // TODO:ディレクトリパスやファイル名からすでにコンプリートしたログかどうか判別。
                // TODO:コンプリート済みの場合、無視して次のループへ。
                targets << file
            }
        }
        targets
    }

}
