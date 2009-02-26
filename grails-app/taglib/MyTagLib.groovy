class MyTagLib {

    static namespace = 'my'

    def dateFormat = { attrs ->
        out << new java.text.SimpleDateFormat(attrs.format).format(attrs.value)
    }

    def singleLink = { attrs ->
        if (!attrs.channelName || !attrs.time) return
        def shortDate = new java.text.SimpleDateFormat("yyyyMMdd").format(attrs.time)
        def fullDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(attrs.time)
        def anchor = attrs.permaId ? '#pid-' + attrs.permaId : ''
        def title = "${attrs.channelName}@${fullDate}"
        out << g.link(url:"/irclog/the/${attrs.channelName.substring(1)}/${shortDate}/${anchor}", title:"${title}") {
            """<img src="${createLinkTo(dir:'images', file:attrs.image)}" alt="Link to ${title}" />"""
        }
    }

    def singleTodayLink = { attrs ->
        // AfterDateが存在して、更にそれが「今日」でない場合に、一気に「今日」に勧める追加リンクを表示する。
        if (!attrs.time) return
        def today = new Date()
        def shortToday = new java.text.SimpleDateFormat("yyyyMMdd").format(today)
        def shortDate = new java.text.SimpleDateFormat("yyyyMMdd").format(attrs.time)
        if (shortToday == shortDate) return
        attrs.time = today
        out << singleLink([*:attrs, time:today])
    }

    def selectChannelForSingle = { attrs ->
        out << g.select(id:'select-single', name:'channel', from:attrs.from, value:attrs.value,
                 optionKey:'key', optionValue:'value',
                 onchange:"document.location='/irclog/the/'+this.options[selectedIndex].value.substring(1)+'/${attrs.date.replaceAll('-', '')}'"
        )
    }

    def onedayLink = { attrs ->
        // 指定日部分と時間部分をフォーマットする。
        def onedayDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(attrs.time)
        def time = new java.text.SimpleDateFormat("HH:mm:ss").format(attrs.time)

        // 指定日検索を設定する。
        def params = [*:attrs.params, period:'oneday', 'period-oneday-date':onedayDate]

        out << g.link(controller:'mixedViewer', action:'index', params:params) { "${onedayDate}" }
        out << """&nbsp;${time}"""
    }

    def channelLink = { attrs ->
        def params = [*:attrs.params, channel:"${attrs.channel.name}" ]
        out << g.link(controller:'mixedViewer', action:'index', params:params) { "${attrs.channel.name}" }
    }

    def messageFormat = { attrs ->
        def value = attrs.value?.encodeAsHTML() ?: ''

        // http/httpsをリンクにする。
        value = value.replaceAll('(https?:\\/\\/[-_.!~*\'()a-zA-Z0-9;/?:@&=+$,%#]+)', '<a href="$1" onclick="return IRCLOG.openLink(this)" target="_blank">$1</a>')

        out << value
    }

    def createNavLinkIfNotCurrent = { attrs ->
        def controlName = request['org.codehaus.groovy.grails.CONTROLLER_NAME_ATTRIBUTE']
        def actionName  = request['org.codehaus.groovy.grails.ACTION_NAME_ATTRIBUTE']
        def actualKey = controlName + (attrs.action ? '.' + actionName : '')
        def key = attrs.controller + (attrs.action ? '.' + attrs.action : '')
        if (actualKey == key) {
            out << """ <li class="menuButton active">${g.message(code:key)}</li> """
        } else {
            out << """ <li class="menuButton">${g.link(class:key, controller:attrs.controller, action:attrs.action) { g.message(code:key) }}</li> """
        }
    }

    def flashMessage = { attrs ->
        if (flash.message) {
            out << """<div class="message">${g.message(code:flash.message, args:flash.args, default:flash.defaultMessage)}</div>"""
        }
        if (flash.errors) {
            out << """<div class="errors"><ul>"""
            out << flash.errors.collect{ """<li>${g.message(code:it, args:flash.args)}</li>"""}.join("")
            out << """</ul></div>"""
        }
        if (attrs.bean && attrs.bean.hasErrors()) {
            out << """<div class="errors">${g.renderErrors(bean:attrs.bean, as:'list')}</div>"""
        }
    }

    def nickStyle = { attrs  ->
        out << """<style type="text/css">"""
        attrs.persons.each{ person ->
            if (person && person.color && person.nicks) {
                out << person.nicks.split(/\s+/).collect{ ".irclog-nick.${it}" }.join(",") + "{color:${person.color}} "
            }
        }
        out << """</style>"""
    }

    def searchAllLogsLink = { attrs ->
        out << "/irclog/viewer?channel=${attrs.channel.name.replace(/#/, '%23')}&period=all&nick=&message=&_type="
    }
}
