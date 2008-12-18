import org.codehaus.groovy.grails.commons.ApplicationHolder

/**
 * IRCログをインポートする。
 * エラー処理は手抜き。
 */
class ImportLogJob {

    def irclogDir = new File(ApplicationHolder.application.config.irclog.importer.targetDirPath)
    def timeout = 30 * 1000 + 1 // msec

    def parser = new HtmlLogParser() // 異なるフォーマットのログファイルをインポートする場合はここを変更すればOK
    def irclogImportService // ServiceはDI対象

    def execute() {
        irclogImportService.importAll(irclogDir, parser)
    }

}
