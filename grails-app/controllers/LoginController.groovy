import org.springframework.security.DisabledException
import org.springframework.security.ui.webapp.AuthenticationProcessingFilter as APF

class LoginController extends Base {

    def index = {
        if (isLoggedIn) {
            redirect(uri: config.irclogViewer.defaultIndexPath)
        }
        else {
            redirect(action: auth, params: params)
        }
    }

    // ログイン画面を表示する。
    def auth = {
        if (isLoggedIn) {
            redirect(uri: config.irclogViewer.defaultIndexPath)
        }
        else {
            render(view: 'auth')
        }
    }

    // ログイン不許可画面を表示する。
    def denied = {
        redirect(uri: config.irclogViewer.defaultIndexPath)
    }

    // ログイン失敗
    def authfail = {
        def loginName  = session[APF.SPRING_SECURITY_LAST_USERNAME_KEY]
        def exception = session[APF.SPRING_SECURITY_LAST_EXCEPTION_KEY]
        def msg = ''
        if (exception) {
            if (exception instanceof DisabledException) {
                msg = "このアカウントは現在使用できません。"
            }
            else {
                if (loginName == "") {
                    msg = "ログインIDを入力してください。"
                } else {
                    msg = "ログインIDかパスワードが誤っています。"
                }
            }
        }
        flash.message = msg
        flash.loginName = loginName
        redirect(controller:'viewer')
    }
}
