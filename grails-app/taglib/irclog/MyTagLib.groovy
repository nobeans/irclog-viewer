package irclog

import org.springframework.web.servlet.support.RequestContextUtils as RCU

class MyTagLib {

    static namespace = 'my'

    def singleLink = { attrs, body ->
        if (!attrs.channelName || !attrs.time) return
        def shortDate = attrs.time.format("yyyyMMdd")
        def fullDate =  attrs.time.format("yyyy-MM-dd")
        def anchor = (attrs.permaId) ?  "#pid-${attrs.permaId}" : ''
        def title = "${attrs.channelName}@${fullDate}"
        out << g.link(url:"/irclog/the/${attrs.channelName.substring(1)}/${shortDate}/${anchor}", title:"${title}") {
            if (attrs.image) {
                return """<img src="${resource(dir:'images', file:attrs.image)}" alt="Link to ${title}" />"""
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
        def shortDate =  attrs.time.format("yyyyMMdd")
        if (shortToday == shortDate) return
        attrs.time = today
        out << singleLink([*:attrs, time:today])
    }

    def timeLink = { attrs ->
        // 指定日部分と時間部分をフォーマットする。
        def onedayDate = attrs.time.format("yyyy-MM-dd")
        def timeHHmmss = attrs.time.format("HH:mm:ss")

        out << g.link(controller:'mixedViewer', action:'index', params:[*:attrs.params, period:'oneday', 'period-oneday-date':onedayDate]) { "${onedayDate}" }
        out << '&nbsp;' << '&nbsp;' << timeHHmmss
    }

    def channelLink = { attrs ->
        def params = [*:attrs.params, channel:"${attrs.channel.name}" ]
        out << g.link(controller:'mixedViewer', action:'index', params:params) { "${attrs.channel.name}" }
    }

    def messageFormat = { attrs ->
        def value = attrs.value?.encodeAsHTML() ?: ''
        out << value.replaceAll('(https?:\\/\\/[-_.!~*\'()a-zA-Z0-9;/?:@&=+$,%#]+)', '<a href="$1">$1</a>')
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
            out << flash.errors.collect{ """<li>${g.message(code:it, args:flash.args)}</li>"""
            }.join("")
            out << """</ul></div>"""
        }
        if (attrs.bean && attrs.bean.hasErrors()) {
            out << """<div class="errors">${g.renderErrors(bean:attrs.bean, as:'list')}</div>"""
        }
    }

    def nickStyle = { attrs  ->
        out << """<style type="text/css">"""
        attrs.persons.each{ person ->
            if (person && person.color) {
                def nicks = [person.loginName] as Set // 重複除外のためSetに
                nicks.addAll(person.nicks.split(/\s+/)*.trim().findAll{it})
                out << nicks.sort().collect{ "${attrs.classPrefix ?: ''}.${it}" }.join(",") + "{color:${person.color} !important} "
            }
        }
        out << """</style>"""
    }

    def searchAllLogsLink = { attrs ->
        out << "/irclog/viewer?channel=${attrs.channel.name.replace(/#/, '%23')}&period=all&nick=&message=&_type=".encodeAsHTML()
    }

    def summaryLink = { attrs ->
        out << ((attrs.count == 0) ? 0 : singleLink([*:attrs, text:attrs.count]))
    }

    def topicLink = { attrs, body ->
        // MEMO:
        // period指定がないとセッションの検索条件よりも優先されないことに注意すること。
        // 個別のtypeによる検索UIを提供していないため、その後ユーザが他の条件と組み合わせて検索しやすいmessage条件を使うようにした。
        // 将来的にtype検索を提供するのであれば、ここでもtype検索にすればよい。
        //def params = [type:"TOPIC", period:"all"]
        def params = [message:"TOPIC:", period:"all"]
        out << g.link(controller:'mixedViewer', action:'index', params:params) { body() }
    }

    def withHelp = { attrs, body ->
        out << """
            <div class="with-help">
              ${body()}
              <img class="help-button" src="${resource(dir:'images', file:'help.gif')}" alt="help" id="${attrs.id}" />
            </div>
        """
    }
    def help = { attrs, body ->
        out << """<div class="help-caption" id="${attrs.for}-caption" ${(attrs.visible == 'true') ? '' : 'style="display:none"'}>${body()}</div>"""
    }

    /**
     * Grails1.1.1の標準タグリブを元に実装した。
     * ラベルはbodyとして指定する。
     * title属性はHTML本来のtitle属性として扱う。
     */
    def sortableColumn = { attrs, body ->
        def writer = out
        if (!attrs.property) throwTagError("Tag [sortableColumn] is missing required attribute [property]")

        def property = attrs.remove("property")
        def action = attrs.action ? attrs.remove("action") : (params.action ? params.action : "list")

        def defaultOrder = attrs.remove("defaultOrder")
        if (defaultOrder != "desc") defaultOrder = "asc"

        // current sorting property and order
        def sort = params.sort
        def order = params.order

        // add sorting property and params to link params
        def linkParams = [sort:property]
        if (params.id) linkParams.put("id",params.id)
        if (attrs.params) linkParams.putAll(attrs.remove("params"))

        // determine and add sorting order for this column to link params
        attrs.class = (attrs.class ? "${attrs.class} sortable" : "sortable")
        if (property == sort) {
            attrs.class = attrs.class + " sorted " + order
            if (order == "asc") {
                linkParams.order = "desc"
            } else {
                linkParams.order = "asc"
            }
        } else {
            linkParams.order = defaultOrder
        }

        def messageSource = grailsAttributes.messageSource
        def locale = RCU.getLocale(request)

        // determine column title
        def title = attrs.remove("title")
        def titleKey = attrs.remove("titleKey")
        if (titleKey) {
            title = messageSource.getMessage(titleKey, null, title, locale)
        }

        // determine column code
        def message
        def code = attrs.remove("code")
        if (code) {
            message = messageSource.getMessage(code, null, code, locale)
        } else {
            message = body()
        }

        writer << "<th "
        // process remaining attributes
        attrs.each { k, v ->
            writer << "${k}=\"${v.encodeAsHTML()}\" "
        }
        writer << ">"
        writer << link(action:action, params:linkParams, title:(title ?: '')) { message }
        writer << "</th>"
    }
}
