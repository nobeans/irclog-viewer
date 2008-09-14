class MyTagLib {

    static namespace = 'my'

    def dateFormat = { attrs ->
        out << new java.text.SimpleDateFormat(attrs.format).format(attrs.value)
    }

    def specifiedLink = { attrs ->
        // 指定日部分と時間部分をフォーマットする。
        def onedayDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(attrs.time)
        def time = new java.text.SimpleDateFormat("hh:mm:ss").format(attrs.time)

        if (attrs.isSpecified) {
            out << """<img src="${createLinkTo(dir:'images', file:'specifiedNow.png')}" />"""
        } else {
            out << g.link(controller:'viewer', action:'specified', id:attrs.id) {
                """<img src="${createLinkTo(dir:'images', file:'specifiedThis.png')}" />"""
            }
        }
    }

    def onedayLink = { attrs ->
        // 指定日部分と時間部分をフォーマットする。
        def onedayDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(attrs.time)
        def time = new java.text.SimpleDateFormat("hh:mm:ss").format(attrs.time)

        // 指定日検索を設定する。
        def params = [*:attrs.params, period:'oneday', 'period-oneday-date':onedayDate]

        out << g.link(controller:'viewer', action:'index', params:params) { "${onedayDate}</a>" }
        out << """&nbsp;&nbsp;${time}"""
    }

    def channelLink = { attrs ->
        def params = [*:attrs.params, channel:"${attrs.channel.id}" ]
        out << g.link(controller:'viewer', action:'index', params:params) { "${attrs.channel.name}</a>" }
    }

    def messageFormat = { attrs ->
        def value = attrs.value?.encodeAsHTML() ?: ''

        // http/httpsをリンクにする。
        value = value.replaceAll('(https?:\\/\\/[-_.!~*\'()a-zA-Z0-9;/?:@&=+$,%#]+)', '<a href="$1">$1</a>')

        out << value
    }

    def calendar = { attrs ->
        out << """
            <input id="${attrs.name}-text" name="${attrs.name}" type="text" value="${attrs.value ?: ''}" maxlength="10" />
            <img class="button" id="${attrs.name}-button" src="${createLinkTo(dir:'images',file:'calendar.png')}" title="${attrs.title ?: ''}" />
            <span id="${attrs.name}-calendar">${attrs.name}</span>
        """
    }

    def createNavLinkIfNotCurrent = { attrs ->
        if (!attrs.action && request['org.codehaus.groovy.grails.CONTROLLER_NAME_ATTRIBUTE'] == attrs.name) return
        if (attrs.action && request['org.codehaus.groovy.grails.ACTION_NAME_ATTRIBUTE'] == attrs.action) return
        out << """ <span class="menuButton">${g.link(class:attrs.name, controller:attrs.name) { g.message(code:attrs.name) }}</span> """
    }
}
