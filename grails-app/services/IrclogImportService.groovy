import org.apache.commons.logging.LogFactory

/**
 * IRCログをファイルからインポートする。
 */
class IrclogImportService {

    boolean transactional = false

    // キー：チャンネル名、値：Channelインスタンス or null
    private channelMap = [:]

    /**
     * 全ファイルを対象にインポートを実行する。
     * 同一のファイルに対して処理を実行しないように、他のスレッド/Jobとの排他制御をする。
     * FIXME:別ファイルに対して並列実行出来るように排他制御できるとよいが、
     * ここでは単にメソッドレベルでsynchronizedとして、シーケンシャルに実行制御するようにした。
     */
    def synchronized importAll(File irclogDir, def parser) {
        if (!irclogDir.isDirectory()) throw new RuntimeException("Irclog directory not found.")
        findAll(irclogDir, parser).each { file ->
            Irclog.withTransaction { status ->
                if (isCompetedFile(file)) {
                    log.debug("This file was completed to import. -> ${file.path}")
                    return
                }

                // ファイルからインポートする。
                log.info("Begin parsing -> ${file.path}")
                parser.parse(file).each { irclog ->
                    if (isImportedLogRecord(irclog)) return
                    irclog.channel = getChannel(irclog.channelName)
                    if (!irclog.save()) {
                        log.error('Import Error: ' + irclog)
                    }
                    if (log.isDebugEnabled()) {
                        print "." // JUnit風進捗マーク
                    }
                }
                if (log.isDebugEnabled()) {
                    println "" // JUnit風進捗マークの後始末
                }
                log.info("End parsing   -> ${file.path}")

                // ファイル最終更新日時が1日以上前のファイルは現役ログではないため、インポート完了ファイルとして登録する。
                // ファイル内のログレコードのインポート状況には依存しないことに注意すること。
                if (isFreezedFile(file)) {
                    log.info("Completed to import (because this file has been freezed). -> ${file.path}")
                    new ImportCompletedFile(filePath:file.canonicalPath).save()
                }
            }
        }
    }

    /** Channelインスタンスを取得する。 */
    private Channel getChannel(String channelName) {
        if (channelMap.any{it.key == channelName}) {
            return channelMap[channelName]
        } else {
            def channel = Channel.findByName(channelName)
            channelMap[channelName] = channel
            return channel
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
                targets << file
            }
        }
        return targets.sort() // できるだけチャンネル・日付順にIDが振られるようにファイル名(パス)でソートする
    }

    /** インポート完了済みログファイルかどうかチェックする。*/
    private boolean isCompetedFile(File file) {
        return ImportCompletedFile.countByFilePath(file.canonicalPath) > 0
    }

    /** インポート済みログレコードかどうかチェックする。*/
    private boolean isImportedLogRecord(Irclog irclog) {
        return isImportedLogRecordWithMinimumCondition(irclog) && isImportedLogRecordWithFullCondition(irclog)
    }
    private boolean isImportedLogRecordWithMinimumCondition(Irclog irclog) { // timeとnickだけで相当ユニークがあがる
        return Irclog.executeQuery(
            'select count(*) from Irclog as l where l.time = ? and l.nick = ?',
            [irclog.time, irclog.nick]
        )[0] > 0
    }
    private boolean isImportedLogRecordWithFullCondition(Irclog irclog) {
        return Irclog.executeQuery(
            'select count(*) from Irclog as l where l.time = ? and l.nick = ? and l.channelName = ? and l.type = ? and l.message = ?',
            [irclog.time, irclog.nick, irclog.channelName, irclog.type, irclog.message]
        )[0] > 0
    }

    /**
     * 更新が停止したログファイルかどうかチェックする。
     * ファイル最終更新日時が1日以上前のファイルは現役ログではない、と判断する。
     */
    private boolean isFreezedFile(file) {
        def lastModified = new Date(file.lastModified())
        def yesterday = new Date() - 1
        return (lastModified <= yesterday)
    }

}
