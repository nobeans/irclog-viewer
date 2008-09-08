import org.grails.plugins.springsecurity.service.AuthenticateService

import org.springframework.security.DisabledException
import org.springframework.security.ui.AbstractProcessingFilter
import org.springframework.security.ui.webapp.AuthenticationProcessingFilter

class LoginController {

	/**
	 * Dependency injection for the authentication service.
	 */
	AuthenticateService authenticateService

	def index = {
		if (isLoggedIn()) {
			redirect(uri: '/')
		}
		else {
			redirect(action: auth, params: params)
		}
	}

	/**
	 * Show the login page.
	 */
	def auth = {
		nocache(response)
		if (isLoggedIn()) {
			redirect(uri: '/')
		}

		if (authenticateService.securityConfig.security.useOpenId) {
			render(view: 'openIdAuth')
		}
		else {
			render(view: 'auth')
		}
	}

	// Login page (function|json) for Ajax access.
	def authAjax = {
		nocache(response)
		//this is example:
		render """
		<script type='text/javascript'>
		(function() {
			loginForm();
		})();
		</script>
		"""
	}

	/**
	 * The Ajax success redirect url.
	 */
	def ajaxSuccess = {
		nocache(response)
		render '{success: true}'
	}

	/**
	 * Show denied page.
	 */
	def denied = {
		redirect(uri: '/')
	}

	// Denial page (data|view|json) for Ajax access.
	def deniedAjax = {
		//this is example:
		render "{error: 'access denied'}"
	}

	/**
	 * login failed
	 */
	def authfail = {

		def loginName = session[AuthenticationProcessingFilter.SPRING_SECURITY_LAST_USERNAME_KEY]
		def msg = ''
		def exception = session[AbstractProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY]
		if (exception) {
			if (exception instanceof DisabledException) {
				msg = "[$loginName] is disabled."
			}
			else {
				msg = "[$loginName] wrong loginName/password."
			}
		}

		if (isAjax()) {
			render("{error: '${msg}'}")
		}
		else {
			flash.message = msg
			redirect(action: auth, params: params)
		}
	}

	/**
	 * Check if logged in.
	 */
	private boolean isLoggedIn() {
		def authPrincipal = authenticateService.principal()
		return authPrincipal != null && authPrincipal != 'anonymousUser'
	}

	private boolean isAjax() {
		return authenticateService.isAjax(request)
	}

	/** cache controls */
	private void nocache(response) {
		response.setHeader('Cache-Control', 'no-cache') // HTTP 1.1
		response.addDateHeader('Expires', 0)
		response.setDateHeader('max-age', 0) 
		response.setIntHeader ('Expires', -1) //prevents caching at the proxy server 
		response.addHeader('cache-Control', 'private') //IE5.x only
	}
}
