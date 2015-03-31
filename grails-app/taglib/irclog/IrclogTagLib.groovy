package irclog

import irclog.security.SpringSecurityContext

class IrclogTagLib implements SpringSecurityContext {

    static namespace = 'irclog'

    def detailLink = { attrs, body ->
        if (!attrs.channelName || !attrs.time) return
        def fullDate = attrs.time.format("yyyy-MM-dd")
        def title = "${attrs.channelName}@${fullDate}"
        out << g.link(controller: "detail", action: "index", params: [date: fullDate, channel: attrs.channelName.substring(1), permaId: attrs.permaId], title: title) {
            if (attrs.image) {
                return """<img src="${asset.assetPath(src: attrs.image)}" alt="Link to ${title}" />"""
            } else if (attrs.text) {
                return attrs.text
            } else {
                return body()
            }
        }
    }

    def detailTodayLink = { attrs ->
        // AfterDateが存在して、更にそれが「今日」でない場合に、一気に「今日」に勧める追加リンクを表示する。
        if (!attrs.time) return
        def today = DateUtils.today
        def shortToday = today.format("yyyyMMdd")
        def shortDate = attrs.time.format("yyyyMMdd")
        if (shortToday == shortDate) return
        attrs.time = today
        out << detailLink([*: attrs, time: today])
    }

    def timeLink = { attrs ->
        // 指定日部分と時間部分をフォーマットする。
        def onedayDate = attrs.time.format("yyyy-MM-dd")
        def timeHHmmss = attrs.time.format("HH:mm:ss")

        out << g.link(controller: 'search', action: 'index', params: [*: attrs.params, period: 'oneday', periodOnedayDate: onedayDate]) { "${onedayDate}" }
        out << '&nbsp;' << '&nbsp;' << timeHHmmss
    }

    def channelLink = { attrs ->
        def params = [*: attrs.params, channel: "${attrs.channel.name}"]
        out << g.link(controller: 'search', action: 'index', params: params) { "${attrs.channel.name}" }
    }

    def createNavLinkIfNotCurrent = { attrs ->
        def isActive = { key ->
            def controlName = request['org.codehaus.groovy.grails.CONTROLLER_NAME_ATTRIBUTE']
            def actionName = request['org.codehaus.groovy.grails.ACTION_NAME_ATTRIBUTE']
            def actualKey = controlName + (attrs.action ? '.' + actionName : '')
            return key == actualKey
        }
        def key = attrs.controller + (attrs.action ? '.' + attrs.action : '')
        if (isActive(key)) {
            out << """<li class="menuButton active">${g.message(code: "nav.${key}.label")}</li>"""
        } else {
            out << """<li class="menuButton">${g.link(class: key, controller: attrs.controller, action: attrs.action) { g.message(code: "nav.${key}.label") }}</li>"""
        }
    }

    def flashMessage = { attrs ->
        if (flash.message) {
            out << """<div class="message">${flash.message}</div>"""
        }
        if (flash.errors) {
            out << """<div class="errors"><ul>"""
            out << flash.errors.collect {
                """<li>${it}</li>"""
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
        out << ((attrs.count == 0) ? 0 : detailLink([*: attrs, text: attrs.count]))
    }

    def withHelp = { attrs, body ->
        out << """
            <div class="with-help">
              ${body()}
              <img class="help-button" src="${asset.assetPath(src: 'help.gif')}" alt="help" id="${attrs.id}" />
            </div>
        """
    }
    def help = { attrs, body ->
        out << """<div class="help-caption" id="${attrs.for}-caption" ${(attrs.visible == 'true') ? '' : 'style="display:none"'}>${body()}</div>"""
    }

    def loginUserInfo = { attrs ->
        def user = currentUser
        if (user instanceof Person) {
            out << g.message(code: "nav.login.info", args: [user.realName, user.loginName])
        } else {
            out << g.message(code: 'nav.login.info.guest')
        }
    }
}
