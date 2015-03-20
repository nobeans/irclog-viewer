package irclog

class DynamicCssController {

    def nickColors() {
        def writer = new StringWriter()
        writer.withPrintWriter { pw ->
            Person.list().each { person ->
                if (person.color) {
                    def nicks = [person.loginName] as Set // to avoid duplication
                    nicks.addAll(person.nicks.split(/\s+/)*.trim().findAll { it })
                    def line = nicks.sort().collect { nick -> ".${nick}" }.join(",") + "{color:${person.color} !important} "
                    pw.println line
                }
            }
        }
        render contentType: "text/css", text: writer.toString()
    }
}
