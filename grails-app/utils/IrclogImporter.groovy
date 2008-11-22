/**
 * IRCログをファイルからインポートする。
 */
class IrclogImporter {

    def importAll(File irclogDir, def parser) {
        if (!irclogDir.isDirectory()) throw new RuntimeException("Irclog directory not found.")
        findAll(irclogDir).each { file ->
            parser.parse(file).each { irclog ->
                if (isCompleted(irclog)) return
                if (!irclog.save()) {
                    throw new RuntimeException('Import Error: ' + irclog)
                }
            }
            handleCompleted(file)
        }
    }

    /** インポート対象のログファイルを探す。 */
    private Collection<File> findAll(File irclogDir) {
        def targets = []
        irclogDir.eachFile { channelDir ->
            channelDir.eachFile { file ->
                if (!(file.name ==~ /.*\.log/)) return   // .logファイル以外は無視する。
                if (ImportCompletedFile.countByFilePath(file.canonicalPath) > 0) return  // すでにコンプリートしたログは無視する。
                targets << file
            }
        }
        targets
    }

    /** インポート完了済みファイルかどうかチェックする。*/
    private boolean isCompleted(Irclog irclog) {
        return Irclog.executeQuery(
            'select count(*) from Irclog as l where l.time = ? and l.type = ? and l.message = ? and l.nick = ? and l.channelName = ?',
            [irclog.time, irclog.type, irclog.message, irclog.nick, irclog.channelName]
        )[0] > 0
    }

    /** ファイル最終更新日時が1日以上前のファイルは現役ログではないため、インポート完了ファイルとして登録する。 */
    private void handleCompleted(file) {
        def lastModified = new Date(file.lastModified())
        def yesterday = new Date() - 1
        if (lastModified <= yesterday) new ImportCompletedFile(filePath:file.canonicalPath).save()
    }

}
