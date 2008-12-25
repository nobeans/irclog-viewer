import org.codehaus.groovy.grails.commons.ApplicationHolder

/**
 * IRCログをインポートする。
 * エラー処理は手抜き。
 */
class ImportLogJob {

    def name = "ImportLogJob"
    def timeout = 60 * 1000 + 1 // msec

    def irclogDir = new File(ApplicationHolder.application.config.irclog.importer.targetDirPath)

    // for DI
    def logParser
    def irclogImportService

    def execute() {
        def cal = Calendar.getInstance()
        log.info("Begin job of importing log. [started at ${cal.getTime()}](${cal.hashCode()})")
        irclogImportService.importAll(irclogDir, logParser)
        log.info("End job of importing log. [started at ${cal.getTime()}](${cal.hashCode()})")
    }

}
