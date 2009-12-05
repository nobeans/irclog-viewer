import grails.util.GrailsUtil

class HttpNoCacheFilters {

    def filters = {
        all(controller:'*', action:'*') {
            before = {
                response.setHeader('Cache-Control', 'no-cache')
                response.addHeader('Cache-Control', 'private') //IE5.x only
                response.setHeader('Pragma', 'no-cache')
                response.setDateHeader('Expires', 0)
            }
            after = {
            }
            afterView = {
            }
        }
    }
}
