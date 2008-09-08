import org.grails.plugins.springsecurity.service.AuthenticateService

import org.springframework.security.providers.UsernamePasswordAuthenticationToken as AuthToken
import org.springframework.security.context.SecurityContextHolder as SCH

/**
 * Actions over Person object.
 */
class RegisterController {

	AuthenticateService authenticateService
	def daoAuthenticationProvider

	def allowedMethods = [save: 'POST', update: 'POST']

	/**
	 * User Registration Top page
	 */
	def index = {

		//if logon user.
		if (authenticateService.userDomain()) {
			log.info("${authenticateService.userDomain()} user hit the register page")
			redirect(action: 'show')
			return
		}

		if (session.id) {
			def person = new Person()
			person.properties = params
			return [person: person]
		}

		redirect(uri: '/')
	}

	/**
	 * User Information page for current user.
	 */
	def show = {

		//get user id from session's domain class.
		def user = authenticateService.userDomain()
		if (user) {
			render(view: 'show', model: [person: Person.get(user.id)])
		}
		else {
			redirect(action: 'index')
		}
	}

	/**
	 * Edit page for current user.
	 */
	def edit = {

		def person
		def user = authenticateService.userDomain()
		if (user) {
			person = Person.get(user.id)
		}

		if (!person) {
			flash.message = "[Illegal Access] User not found with id ${params.id}"
			redirect(action: 'index')
			return
		}

		[person: person]
	}

	/**
	 * update action for current user's edit page
	 */
	def update = {

		def person
		def user = authenticateService.userDomain()
		if (user) {
			person = Person.get(user.id)
		}
		else {
			redirect(action: 'index')
			return
		}

		if (!person) {
			flash.message = "[Illegal Access] User not found with id ${params.id}"
			redirect(action: 'index', id: params.id)
			return
		}

		//if user want to change password. leave password field blank, password will not change.
		if (params.password && params.password.length() > 0
				&& params.repassword && params.repassword.length() > 0) {
			if (params.password == params.repassword) {
				person.password = authenticateService.passwordEncoder(params.password)
			}
			else {
				person.password = ''
				flash.message = 'The passwords you entered do not match.'
				render(view: 'edit', model: [person: person])
				return
			}
		}

		if (person.save()) {
			redirect(action: 'show', id: person.id)
		}
		else {
			render(view: 'edit', model: [person: person])
		}
	 }

	/**
	 * Person save action.
	 */
	def save = {

		if (authenticateService.userDomain() != null) {
			log.info("${authenticateService.userDomain()} user hit the register page")
			redirect(action: 'show')
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
		person.nicks = ''
		person.color = ''
		if (person.save()) {
			role.addToPersons(person)
			person.save(flush: true)

			def auth = new AuthToken(person.loginName, params.password)
			def authtoken = daoAuthenticationProvider.authenticate(auth)
			SCH.context.authentication = authtoken
			redirect(uri: '/')
		}
		else {
			person.password = ''
			render(view: 'index', model: [person: person])
		}
	}
}
