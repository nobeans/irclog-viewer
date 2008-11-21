/**
 * 以下の形式のIRCログをパースする。
 *
 * <div class="message"><span class="time">19:47:37</span><span class="nick type">message</span></div>
 */
class HtmlLogParser {
    Iterator parse(File logFile) {
        new HtmlLogIterator(logFile)
    }
}

/**
 * 1行ずつファイルの内容をパースして返すイテレータ。
 * MEMO:Groovyでは内部クラスに対応していないため、不本意ながら1ファイル2クラス構成にした。本来はHtmlLogParserの内部クラス。
 */
class HtmlLogIterator implements Iterator {

    private static final LINE_REGEXP = '^<div class="message"><span class="time">([0-9:]{8})</span><span class="([a-zA-Z-_]+) ([a-z]+)">(?:(?:&lt;|\\{)\\2(?:&gt;|\\}) )?([^<]*)</span></div>$'

    private Iterator lineIterator
    private String date
    private String channelName

    HtmlLogIterator(File logFile) {
        this.lineIterator = logFile.readLines().iterator()
        this.date = resolveDate(logFile)
        this.channelName = resolveChannelName(logFile)
    }

    public boolean hasNext() {
        lineIterator.hasNext()
    }

    public Object next() {
        def line = lineIterator.next()
        println line
        def log = parseLine(line)
        println log
        log
    }

    public void remove() {
        throw new UnsupportedOperationException('unnecessary')
    }

    private parseLine(line) {
        def irclog
        (line =~ LINE_REGEXP).each { all, time, nick, type, message ->
            def datetime = DateUtils.parse(date + ' ' + time)
            irclog = new Irclog(time:datetime, nick:nick, type:type.toUpperCase(), message:decodeAsHTML(message), channelName:channelName, isHidden:false)
        }
        irclog
    }

    private decodeAsHTML(text) {
        // FIXME:統合した暁には単にEncodeを使う。
        text.replaceAll("&gt;", ">").
             replaceAll("&lt;", "<").
             replaceAll("&quot;", '"').
             replaceAll("&amp;", '&')
        //text.decodeAsHTML()
    }

    private resolveDate(file) {
        file.name.replaceFirst(/([0-9]{2})([0-9]{2})([0-9]{2}).log$/, '20$1/$2/$3')
    }

    private String resolveChannelName(file) {
        file.toString().replaceAll('^.*/([^/]+)/[^/]+$', '#$1')
    }
}
