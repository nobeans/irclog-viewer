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
            redirect(uri: config.irclogViewer.defaultIndexPath)
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
                flash.message = "このアカウントは現在使用できません。"
            }
            else {
                if (loginName == "") {
                    flash.message = "ログインIDを入力してください。"
                } else {
                    flash.message = "ログインIDかパスワードが誤っています。"
                }
            }
        }
        flash.loginName = loginName
        render(view: 'auth')
    }
}
