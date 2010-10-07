package irclog

import irclog.controller.Base

class LogoutController extends Base {
    
    def index = { redirect(uri:"/j_spring_security_logout") }
}
