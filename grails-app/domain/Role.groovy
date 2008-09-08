/**
 * Role class for Authority.
 */
class Role {

	String name = 'ROLE_'
	String description

	static hasMany = [persons: Person]

	static constraints = {
		name(blank:false)
		description()
	}
}