import org.codehaus.groovy.grails.commons.ApplicationHolder

/**
 * IRCログをインポートする。
 * エラー処理は手抜き。
 */
class ImportLogJob {

    def irclogDir = new File(ApplicationHolder.application.config.irclog.importer.targetDirPath)
    def timeout = 60 * 1000 + 1 // msec

    def parser = new HtmlLogParser() // 異なるフォーマットのログファイルをインポートする場合はここを変更すればOK
    def irclogImportService // ServiceはDI対象

    def execute() {
        def cal = Calendar.getInstance()
        log.info("Begin job of importing log. [started at ${cal.getTime()}](${cal.hashCode()})")
        irclogImportService.importAll(irclogDir, parser)
        log.info("End job of importing log. [started at ${cal.getTime()}](${cal.hashCode()})")
    }

}
