import org.springframework.security.DisabledException
import org.springframework.security.ui.webapp.AuthenticationProcessingFilter as APF

class LoginController extends Base {

    def index = {
        redirect(action:auth, params:params)
    }

    /** ログイン画面を表示する。 */
    def auth = {
        if (isLoggedIn) {
            flash.message = null
            flash.errors = null

            // セッション有効期間をカスタマイズ
            session.maxInactiveInterval = config.irclog.session.maxInactiveInterval

            redirect(uri: config.irclog.viewer.defaultTargetUrl)
        }
        else {
            render(view:'auth', params:params)
        }
    }

    /** ログイン不許可画面を表示する。 */
    def denied = {
        redirect(uri: config.irclogViewer.defaultIndexPath)
    }

    /** ログイン失敗 */
    def authfail = {
        def loginName = session[APF.SPRING_SECURITY_LAST_USERNAME_KEY]
        def exception = session[APF.SPRING_SECURITY_LAST_EXCEPTION_KEY]
        if (exception) {
            if (exception instanceof DisabledException) {
                flash.message = "login.loginName.disabled.error"
            }
            else {
                if (loginName == "") {
                    flash.message = "login.loginName.empty.error"
                } else {
                    flash.message = "login.loginName.invalid.error"
                }
            }
        }
        flash.loginName = loginName
        render(view: 'auth')
    }
}
