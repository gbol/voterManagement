import bz.voter.management.*

class BootStrap {

	def springSecurityService
	def sessionFactory
	def grailsApplication
	def messageSource

    def init = { servletContext ->

	 	grailsApplication.domainClasses.each{domainClass ->
			if(domainClass.clazz.name.contains("bz.voter.management")){
				domainClass.metaClass.retrieveErrors = {
					def list = delegate?.errors?.allErrors.collect{messageSource.getMessage(it,null)}
					return list?.join("\n")
				}
			}
		}


	 	new Sex(name:'Male', code:'M').save()
	 	new Sex(name:'Female', code:'F').save()

		new Pledge(name:'Yes').save()
		new Pledge(name:'No').save()
		new Pledge(name:'Undecided').save()

		new Affiliation(name:'PUP').save()
		new Affiliation(name:'UDP').save()
		new Affiliation(name:'Unknown').save()

		new Ethnicity(name:'Creole').save()
		new Ethnicity(name:'Indian').save()
		new Ethnicity(name:'Garifuna').save()
		new Ethnicity(name:'Chinese').save()

		new IdentificationType(name:'Passport').save()
		new IdentificationType(name:'Social Security Card').save()
		new IdentificationType(name:'Voter Id').save()

		new ElectionType(name:'General',code:'GN').save()
		new ElectionType(name:'Municipal',code:'MN').save()

		new District(name:'Corozal',code: 'CZ').save()
		new District(name:'Orange Walk', code:'OW').save()
		new District(name:'Belize', code:'BZ').save()
		new District(name:'Cayo', code:'CY').save()
		new District(name:'Stann Creek',code:'SC').save()
		new District(name:'Toledo',code:'TO').save(flush:true)

		new Municipality(name:'Belmopan',district:District.findByCode('CY')).save()

		def userRole = SecRole.findByAuthority('ROLE_USER') ?: new SecRole(authority: 'ROLE_USER').save(failOnError: true)
      def adminRole = SecRole.findByAuthority('ROLE_ADMIN') ?: new SecRole(authority: 'ROLE_ADMIN').save(failOnError: true)
      def pollStation = SecRole.findByAuthority('ROLE_POLL_STATION') ?: new SecRole(authority: 'ROLE_POLL_STATION').save(failOnError: true)
		def adminUser = SecUser.findByUsername('admin') ?: new SecUser(
                username: 'admin',
                password: 'p4ssw0rd',
                enabled: true).save(failOnError: true)
 
        if (!adminUser.authorities.contains(adminRole)) {
            SecUserSecRole.create adminUser, adminRole
        }


		sessionFactory.currentSession.flush()

    }

    def destroy = {

    }

}
