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
                // .logファイル以外は無視する。
                if (!(file.name ==~ /.*\.log/)) return

                // すでにコンプリートしたログは無視する。
                if (ImportCompletedFile.countByLogFilePath(file.canonicalPath) > 0) return

                targets << file
            }
        }
        targets
    }

}
