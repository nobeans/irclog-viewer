/**
 * 以下の形式のIRCログをパースする。
 *
 * <div class="message"><span class="time">19:47:37</span><span class="nick type">message</span></div>
 */
class HtmlLogParser implements Iterator {

    private static final LINE_REGEXP = '^<div class="message"><span class="time">([0-9:]{8})</span><span class="([a-zA-Z-_]+) ([a-z]+)">(?:(?:&lt;|\\{)\\2(?:&gt;|\\}) )?([^<]*)</span></div>$'

    def date
    def lineIterator

    HtmlLogParser(logFile) {
        this.lineIterator = logFile.readLines().iterator()
        this.date = resolveDate(logFile)
    }

    public boolean hasNext() {
        lineIterator.hasNext()
    }

    public Object next() {
        def line = lineIterator.next()
        parseLine(line)
    }

    public void remove() {
        throw new UnsupportedOperationException('unnecessary')
    }

    private parseLine(line) {
        def irclog
        (line =~ LINE_REGEXP).each { all, time, nick, type, message ->
            def datetime = DateUtils.parse(date + ' ' + time)
            irclog = new Irclog(time:datetime, nick:nick, type:type, message:message)
        }
        irclog
    }

    private resolveDate(file) {
        file.name.replaceFirst(/([0-9]{2})([0-9]{2})([0-9]{2}).log$/, '20$1/$2/$3')
    }

    private decodeAsHTML(text) {
        // FIXME:統合した暁には単にEncodeを使う。
        //text.replaceAll("&gt;", ">").
        //     replaceAll("&lt;", "<").
        //     replaceAll("&quot;", '"').
        //     replaceAll("&amp;", '&')
        text.decodeAsHTML()
    }

}
