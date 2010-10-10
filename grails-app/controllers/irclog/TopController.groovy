package irclog

import irclog.controller.Base
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class TopController extends Base {
    def index = {
        log.info "Passing through via Top"
        flash.message = flash.message // メッセージがあった場合は引き継ぐ
        redirect(uri: ConfigurationHolder.config.irclog.viewer.defaultTargetUrl)
    }
}
