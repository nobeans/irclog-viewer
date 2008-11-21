import org.codehaus.groovy.grails.commons.ApplicationHolder

/**
 * IRCログをインポートする。
 * エラー処理は手抜き。
 */
class ImportLogJob {

    def irclogDir = new File(ApplicationHolder.application.config.irclog.importer.targetDirPath)
    def timeout = 10 * 1000 + 1 // msec

    def irclogImporter = new IrclogImporter()
    def parser = new HtmlLogParser() // 異なるフォーマットのログファイルをインポートする場合はここを変更すればOK

    def execute() {
        irclogImporter.importAll(irclogDir, parser)
    }

}
