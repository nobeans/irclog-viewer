import org.grails.plugins.springsecurity.service.AuthenticateService

import org.springframework.security.providers.UsernamePasswordAuthenticationToken as AuthToken
import org.springframework.security.context.SecurityContextHolder as SCH

/**
 * ユーザ登録
 */
class RegisterController {

	AuthenticateService authenticateService
	def daoAuthenticationProvider

	def allowedMethods = [save: 'POST']

	/**
	 * Person save action.
	 */
	def save = {
		if (authenticateService.userDomain() != null) {
			log.info("${authenticateService.userDomain()} user hit the register page")
			redirect(controller:'viewer')
			return
		}

		def person = new Person()
		person.properties = params

		def config = authenticateService.securityConfig
		def defaultRole = config.security.defaultRole

		def role = Role.findByName(defaultRole)
		if (!role) {
			person.password = ''
			flash.message = 'Default Role not found.'
			render(view: 'index', model: [person: person])
			return 
		}

		if (params.password != params.repassword) {
			person.password = ''
			flash.message = 'The passwords you entered do not match.'
			render(view: 'index', model: [person: person])
			return
		}

		def pass = authenticateService.passwordEncoder(params.password)
		person.password = pass
		person.enabled = true
		person.nicks = params.loginName
		person.color = ''
		if (person.save()) {
			role.addToPersons(person)
			person.save(flush: true)

			def auth = new AuthToken(person.loginName, params.password)
			def authtoken = daoAuthenticationProvider.authenticate(auth)
			SCH.context.authentication = authtoken
			redirect(controller:'viewer')
		}
		else {
			person.password = ''
			redirect(controller:'viewer', view: 'index', model: [person: person])
		}
	}
}
