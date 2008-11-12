import org.codehaus.groovy.grails.commons.ApplicationHolder

/**
 * IRCログをインポートする。
 */
class ImportLogJob {

    def irclogDirPath = ApplicationHolder.application.config.irclog.importer.targetDirPath

    def logFileRetriever =  new LogFileRetriever()
    def timeout = 10 * 000 + 1 // msec

    def execute() {
        def files = logFileRetriever.retrieve(new File(irclogDirPath))
        files.each { file ->
            new HtmlLogParser(file).each {
                println it
                // TODO:DBに追加済みかどうかチェックしながら、saveしていく。
                // TODO:まさにLogImporterなところ。これも別クラスにするか。
            }
        }
    }
}
