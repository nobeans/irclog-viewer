import org.apache.commons.logging.LogFactory

/**
 * IRCログをファイルからインポートする。
 */
class IrclogImporter {

    private log = LogFactory.getLog("IrclogImporter")

    def importAll(File irclogDir, def parser) {
        if (!irclogDir.isDirectory()) throw new RuntimeException("Irclog directory not found.")
        findAll(irclogDir, parser).each { file ->
            log.info("Parsing... -> ${file.path}")
            parser.parse(file).each { irclog ->
                if (isImportedLogRecord(irclog)) return
                if (!irclog.save()) {
                    throw new RuntimeException('Import Error: ' + irclog)
                }
            }
            handleCompleted(file)
        }
    }

    /** インポート対象のログファイルを探す。 */
    private Collection<File> findAll(File irclogDir, def parser) {
        def targets = []
        irclogDir.eachFile { channelDir ->
            if (!channelDir.isDirectory()) return
            channelDir.eachFile { file ->
                if (!parser.isTarget(file)) {
                    log.debug("This file is not target. -> ${file.path}")
                    return
                }
                if (isCompetedFile(file)) {
                    log.debug("This file was completed to import. -> ${file.path}")
                    return
                }
                targets << file
            }
        }
        targets
    }

    /** インポート完了済みログファイルかどうかチェックする。*/
    private boolean isCompetedFile(File file) {
        return ImportCompletedFile.countByFilePath(file.canonicalPath) > 0
    }

    /** インポート済みログレコードかどうかチェックする。*/
    private boolean isImportedLogRecord(Irclog irclog) {
        return Irclog.executeQuery(
            'select count(*) from Irclog as l where l.time = ? and l.type = ? and l.message = ? and l.nick = ? and l.channelName = ?',
            [irclog.time, irclog.type, irclog.message, irclog.nick, irclog.channelName]
        )[0] > 0
    }

    /**
     * ファイル最終更新日時が1日以上前のファイルは現役ログではないため、インポート完了ファイルとして登録する。
     * ファイル内のログレコードのインポート状況には依存しないことに注意すること。
     */
    private void handleCompleted(file) {
        def lastModified = new Date(file.lastModified())
        def yesterday = new Date() - 1
        if (lastModified <= yesterday) new ImportCompletedFile(filePath:file.canonicalPath).save()
    }

}
