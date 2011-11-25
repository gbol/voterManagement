import org.grails.plugins.excelimport.*
import grails.util.Environment

import java.util.*

import bz.voter.management.*
import bz.voter.management.importer.*

import grails.util.Environment

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
      def pollStationRole = SecRole.findByAuthority('ROLE_POLL_STATION') ?: new SecRole(authority: 'ROLE_POLL_STATION').save(failOnError: true)
      def officeStationRole = SecRole.findByAuthority('ROLE_OFFICE_STATION') ?: new SecRole(authority: 'ROLE_OFFICE_STATION').save(failOnError: true)
		def adminUser = SecUser.findByUsername('admin') ?: new SecUser(
                username: 'admin',
                password: 'p4ssw0rd',
                enabled: true).save(failOnError: true)
 
        if (!adminUser.authorities.contains(adminRole)) {
            SecUserSecRole.create adminUser, adminRole
        }


		sessionFactory.currentSession.flush()


		Environment.executeForCurrentEnvironment{
			development{
				def division = new Division(name:'Albert').save()
				def pollStation = new PollStation(pollNumber:23, division: division).save()

				def address = new Address(houseNumber: '45', street: 'Street Name', municipality: Municipality.findByName('Belmopan')).save()
				def person = new Person()
				person.firstName = 'John'
				person.lastName = 'Doe'
				person.birthDate = new Date().parse('dd/MM/yyyy','28/10/1984')
				person.sex = Sex.findByCode('M')
				person.ethnicity = Ethnicity.findByName('Creole')
				person.address = address
				person.cellPhone = '634-0921'
				person.save()

				def voter = new Voter()
				voter.person = person
				voter.registrationDate = new Date().parse('dd/MM/yyyy','12/11/2010')
				voter.registrationNumber = '42323'
				voter.pollStation = pollStation
				voter.identificationType = IdentificationType.findByName('Passport')
				voter.affiliation = Affiliation.findByName('UNKNOWN')
				voter.save()


			}
		}

		/*

   String fileName = "C:\\development\\Spring\\voterManagement\\sample.xls"
        
        AddressExcelImporter importer = new AddressExcelImporter(fileName);
       
        def addressesMapList = importer.getAddresses();
	 println addressesMapList
       
        
        addressesMapList.each { Map addressParams ->
            def newAddress = new Address(addressParams)
            //println newBook
            if (!newAddress.save()) {
            println "Address not saved, errors = ${newAddress.errors}"
           } 
    }
        
        
        
        String fileName2 = "C:\\development\\Spring\\voterManagement\\sample.xls"
        
        PersonExcelImporter importer2 = new PersonExcelImporter(fileName2);
       
        def personsMapList = importer2.getPersons();
	 println personsMapList
       
        
        personsMapList.each { Map personParams ->
            def newPerson = new Person(personParams)
            //println newBook
            if (!newPerson.save()) {
            println "Person not saved, errors = ${newPerson.errors}"
           } 
    }
    /////////////////////////////////////////////////////////////////////////////
        
       String fileName3 = "C:\\development\\Spring\\voterManagement\\Sample.xls"
                
        VoterExcelImporter importer3 = new VoterExcelImporter(fileName3);
       
        def votersMapList = importer3.getVoters();
	 println votersMapList
       
        
        votersMapList.each { Map voterParams ->
            def newVoter = new Voter(voterParams)
            if (!newVoter.save()) {
            println "Voter not saved, errors = ${newVoter.errors}"
       } 
   } 
	*/
		sessionFactory.currentSession.flush()

    }

    def destroy = {

    }

}
