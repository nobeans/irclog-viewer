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
        def files = logFileFinder.findAll(new File(irclogDirPath))
        files.each { file ->
            log.debug(file)
            def channelName = file.toString().replaceAll("${irclogDirPath}/(.*)/.*", '#$1')
            new HtmlLogParser(file).each { log ->
                log.channelName = channelName
                if (!isRegistered(log)) log.save()
            }
        }
    }

    /** DBに追加済みかどうかチェックする。*/
    private boolean isRegistered(Irclog log) {
        return Irclog.executeQuery(
            "select count(*) from Irclog as log where log.time = ? and log.type = ? and log.message = ? and log.nick = ? and log.channelName = ?",
            [log.time, log.type, log.message, log.nick, log.channelName]
        )[0] > 0
    }

}
