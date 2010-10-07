import org.springframework.web.servlet.support.RequestContextUtils as RCU

class MyTagLib {

    static namespace = 'my'

    def dateFormat = { attrs ->
        if (!attrs.format || !attrs.value) return
        out << new java.text.SimpleDateFormat(attrs.format).format(attrs.value)
    }

    def singleLink = { attrs, body ->
        if (!attrs.channelName || !attrs.time) return
        def shortDate = new java.text.SimpleDateFormat("yyyyMMdd").format(attrs.time)
        def fullDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(attrs.time)
        def anchor
        if (attrs.timeMarkerJump) {
            anchor = '#timemarker'
        } else if (attrs.permaId) {
            anchor = "#pid-${attrs.permaId}"
        } else {
            anchor = ''
        }
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

    def timeLink = { attrs ->
        // 指定日部分と時間部分をフォーマットする。
        def today = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date())
        def onedayDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(attrs.time)
        def timeHHmm = new java.text.SimpleDateFormat("HH:mm").format(attrs.time)
        def timeHHmmss = new java.text.SimpleDateFormat("HH:mm:ss").format(attrs.time)

        out << g.link(controller:'mixedViewer', action:'index', params:[*:attrs.params, period:'oneday', 'period-oneday-date':onedayDate]) { "${onedayDate}" }
        out << '&nbsp;' << '&nbsp;'
        if (onedayDate == today) {
            out << g.link(controller:'mixedViewer', action:'index', params:[*:attrs.params, period:'today', 'period-today-time':timeHHmm]) { "${timeHHmmss}" }
        } else {
            out << timeHHmmss
        }
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
            <div class="help">
              ${body()}
              <img class="help-button" src="${resource(dir:'images', file:'help.gif')}" alt="help"
                onclick="\$('${attrs.id}').toggle()" />
            </div>
        """
    }
    def help = { attrs, body ->
        out << """<div class="help-caption" id="${attrs.for}" ${(attrs.visible == 'true') ? '' : 'style="display:none"'}>${body()}</div>"""
    }

    /**
     * 標準の拡張版
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

        def messageSource = grailsAttributes.getApplicationContext().getBean("messageSource")
        def locale = RCU.getLocale(request)

        // determine column title
        def title = attrs.remove("title")
        def titleKey = attrs.remove("titleKey") ?: title
        title = messageSource.getMessage(titleKey, null, title, locale)

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