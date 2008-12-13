import org.apache.commons.logging.*

/**
 * 以下の形式のIRCログをパースする。
 *
 * <div class="message"><span class="time">19:47:37</span><span class="nick type">message</span></div>
 */
class HtmlLogParser {
    Iterator parse(File logFile) {
        new HtmlLogIterator(logFile)
    }

    boolean isTarget(File logFile) {
        (logFile.path ==~ '.*/html-CS-([^/]+)/[^/]+.log$')
    }
}

/**
 * 1行ずつファイルの内容をパースして返すイテレータ。
 * MEMO:Groovyでは内部クラスに対応していないため、不本意ながら1ファイル2クラス構成にした。本来はHtmlLogParserの内部クラス。
 */
class HtmlLogIterator implements Iterator {

    private static final LINE_REGEXP = '^<div class="message"><span class="time">([0-9:]{8})</span><span class="([0-9a-zA-Z-_]+) ([a-z]+)">(?:(?:&lt;|\\{)\\2(?:&gt;|\\}) )?(.*)</span></div>$'
    private final log = LogFactory.getLog(this.class.name)

    private Iterator lineIterator
    private File logFile
    private String date
    private String channelName

    HtmlLogIterator(File logFile) {
        this.lineIterator = logFile.readLines().iterator()
        this.logFile = logFile
        this.date = resolveDate(logFile)
        this.channelName = resolveChannelName(logFile)
    }

    public boolean hasNext() {
        lineIterator.hasNext()
    }

    public Object next() {
        while (true) {
            try {
                def line = lineIterator.next()
                return parseLine(line)
            } catch (RuntimeException e) {
                log.warn("${e.message} in ${logFile.path}")
            }
            if (!hasNext()) return null
        }
    }

    public void remove() {
        throw new UnsupportedOperationException('Unnecessary')
    }

    private parseLine(line) {
        def irclog
        (line =~ LINE_REGEXP).each { all, time, nick, type, message ->
            irclog = new Irclog(
                time:new java.text.SimpleDateFormat('yyyy-MM-dd HH:mm:ss').parse(date + ' ' + time),
                nick:nick,
                type:type.toUpperCase(),
                message:decodeAsHTML(message),
                channelName:channelName,
                isHidden:false
            )
            irclog.rawString = line // インポート時のエラー処理用
        }
        if (!irclog) throw new RuntimeException('Parse error: ' + line)
        irclog
    }

    private decodeAsHTML(text) {
        // FIXME:単にEncodeでやりたい。
        text.replaceAll("&gt;", ">").
             replaceAll("&lt;", "<").
             replaceAll("&quot;", '"').
             replaceAll("&amp;", '&')
        //text.decodeAsHTML()
    }

    private resolveDate(file) {
        file.name.replaceFirst(/([0-9]{2})([0-9]{2})([0-9]{2}).log$/, '20$1-$2-$3')
    }

    private String resolveChannelName(file) {
        file.toString().replaceAll('^.*/html-CS-([^/]+)/[^/]+.log$', '#$1')
    }
}
