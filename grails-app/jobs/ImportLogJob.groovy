import org.codehaus.groovy.grails.commons.ApplicationHolder

/**
 * IRCログをインポートする。
 * エラー処理は手抜き。
 */
class ImportLogJob {

    def irclogDirPath = ApplicationHolder.application.config.irclog.importer.targetDirPath

    def logFileFinder =  new LogFileFinder()
    def timeout = 10 * 1000 + 1 // msec

    def execute() {
        logFileFinder.findAll(new File(irclogDirPath)).each { file ->
            def channelName = file.toString().replaceAll("${irclogDirPath}/(.*)/.*", '#$1')
            new HtmlLogParser(file).each { log ->
                log.channelName = channelName   // FIXME:1行ごとにチャンネル名を反映
                if (!isRegistered(log)) log.save()
            }
            handleCompleted(file)
        }
    }

    /** DBに追加済みかどうかチェックする。*/
    private boolean isRegistered(Irclog log) {
        return Irclog.executeQuery(
            "select count(*) from Irclog as log where log.time = ? and log.type = ? and log.message = ? and log.nick = ? and log.channelName = ?",
            [log.time, log.type, log.message, log.nick, log.channelName]
        )[0] > 0
    }

    /** ファイル最終更新日時が1日以上前のファイルは現役ログではないため、インポート完了ファイルとして登録する。 */
    private void handleCompleted(file) {
        def lastModified = new Date(file.lastModified())
        def yesterday = new Date() - 1
        if (lastModified <= yesterday) new ImportCompletedFile(logFilePath:file.canonicalPath).save()
    }

}
