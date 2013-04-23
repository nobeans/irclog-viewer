package irclog

class IrclogTagLib {

    static namespace = 'irclog'

    def springSecurityService

    def singleLink = { attrs, body ->
        if (!attrs.channelName || !attrs.time) return
        def fullDate = attrs.time.format("yyyy-MM-dd")
        def title = "${attrs.channelName}@${fullDate}"
        out << g.link(controller: "singleViewer", action: "index", params: [date: fullDate, channel: attrs.channelName.substring(1), permaId: attrs.permaId], title: title) {
            if (attrs.image) {
                return """<img src="${resource(dir: 'images', file: attrs.image)}" alt="Link to ${title}" />"""
            } else if (attrs.text) {
                return attrs.text
            } else {
                return body()
            }
        }
    }

    def singleTodayLink = { attrs ->
        // AfterDateが存在して、更にそれが「今日」でない場合に、一気に「今日」に勧める追加リンクを表示する。
        if (!attrs.time) return
        def today = DateUtils.today
        def shortToday = today.format("yyyyMMdd")
        def shortDate = attrs.time.format("yyyyMMdd")
        if (shortToday == shortDate) return
        attrs.time = today
        out << singleLink([*: attrs, time: today])
    }

    def timeLink = { attrs ->
        // 指定日部分と時間部分をフォーマットする。
        def onedayDate = attrs.time.format("yyyy-MM-dd")
        def timeHHmmss = attrs.time.format("HH:mm:ss")

        out << g.link(controller: 'mixedViewer', action: 'index', params: [*: attrs.params, period: 'oneday', periodOnedayDate: onedayDate]) { "${onedayDate}" }
        out << '&nbsp;' << '&nbsp;' << timeHHmmss
    }

    def channelLink = { attrs ->
        def params = [*: attrs.params, channel: "${attrs.channel.name}"]
        out << g.link(controller: 'mixedViewer', action: 'index', params: params) { "${attrs.channel.name}" }
    }

    def messageFormat = { attrs ->
        def value = attrs.value?.encodeAsHTML() ?: ''
        out << value.replaceAll('(https?:\\/\\/[-_.!~*\'()a-zA-Z0-9;/?:@&=+$,%#]+)', '<a href="$1">$1</a>')
    }

    def createNavLinkIfNotCurrent = { attrs ->
        def controlName = request['org.codehaus.groovy.grails.CONTROLLER_NAME_ATTRIBUTE']
        def actionName = request['org.codehaus.groovy.grails.ACTION_NAME_ATTRIBUTE']
        def actualKey = controlName + (attrs.action ? '.' + actionName : '')
        def key = attrs.controller + (attrs.action ? '.' + attrs.action : '')
        if (actualKey == key) {
            out << """ <li class="menuButton active">${g.message(code: key)}</li> """
        } else {
            out << """ <li class="menuButton">${g.link(class: key, controller: attrs.controller, action: attrs.action) { g.message(code: key) }}</li> """
        }
    }

    def flashMessage = { attrs ->
        if (flash.message) {
            out << """<div class="message">${g.message(code: flash.message, args: flash.args, default: flash.defaultMessage)}</div>"""
        }
        if (flash.errors) {
            out << """<div class="errors"><ul>"""
            out << flash.errors.collect {
                """<li>${g.message(code: it, args: flash.args)}</li>"""
            }.join("")
            out << """</ul></div>"""
        }
        if (attrs.bean && attrs.bean.hasErrors()) {
            out << """<div class="errors">${g.renderErrors(bean: attrs.bean, as: 'list')}</div>"""
        }
    }

    def searchAllLogsLink = { attrs ->
        out << "/irclog/viewer?channel=${attrs.channel.name.replace(/#/, '%23')}&period=all&nick=&message=&_type=".encodeAsHTML()
    }

    def summaryLink = { attrs ->
        out << ((attrs.count == 0) ? 0 : singleLink([*: attrs, text: attrs.count]))
    }

    def withHelp = { attrs, body ->
        out << """
            <div class="with-help">
              ${body()}
              <img class="help-button" src="${resource(dir: 'images', file: 'help.gif')}" alt="help" id="${attrs.id}" />
            </div>
        """
    }
    def help = { attrs, body ->
        out << """<div class="help-caption" id="${attrs.for}-caption" ${(attrs.visible == 'true') ? '' : 'style="display:none"'}>${body()}</div>"""
    }

    def loggedInPersonInfo = { attrs ->
        out << springSecurityService.currentUser[attrs.field]
    }
}
