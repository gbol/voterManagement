import bz.voter.management.*

class BootStrap {

	def springSecurityService
	def sessionFactory

    def init = { servletContext ->
	 	new Sex(name:'Male', code:'M').save()
	 	new Sex(name:'Female', code:'F').save()

		new Pledge(name:'pup').save()
		new Pledge(name:'udp').save()

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
