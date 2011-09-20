import grails.util.Environment

class RequestTracelogFilters {

    private static final String SEP = System.properties["line.separator"]

    def formatLog(String logTitle, request, params, session) {
        def lines = []
        lines << "[${logTitle}]".padRight(60, ">")
        lines << "Request ID: " + request["org.codehaus.groovy.grails.WEB_REQUEST"]
        lines << "-"*20
        lines << "Request parameters:"
        params.keySet().sort().each {
            lines << "  ${it} = ${params[it]}"
        }
        lines << "-"*20
        lines << "Session attributes:"
        session.getAttributeNames().collect{it}.sort().each {
            lines << "  ${it} = ${session.getAttribute(it)}"
        }
        lines << "-"*20
        def time = (System.currentTimeMillis() - request.startedTime) / 1000.0
        lines << "[${logTitle}] (time: ${time}[sec])".padLeft(60, "<")
        return lines.join(SEP)
    }

    def filters = {
        if (Environment.current == Environment.DEVELOPMENT) { // only in development mode
            all(controller:'*', action:'*') {
                before = {
                    request.startedTime = System.currentTimeMillis()
                    log.debug formatLog("Before Action", request, params, session)
                }
                after = {
                    log.debug formatLog("After Action", request, params, session)
                }
                afterView = {
                    log.debug formatLog("After View", request, params, session)
                }
            }
        }
    }
}

