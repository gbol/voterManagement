import grails.util.Environment

import org.codehaus.groovy.grails.commons.ConfigurationHolder

import org.grails.plugins.excelimport.*

import java.util.*

import bz.voter.management.*
import bz.voter.management.importer.*

import grails.util.Environment


class BootStrap {

	def springSecurityService
	def voterService
	def importFileService

	def sessionFactory
	def grailsApplication
	def messageSource
	def unknownMunicipality

   static String fileName = ConfigurationHolder.config.files.dir + "Sample.xls"
   static String albertFile = "/usr/local/files/albert.xlsx"
   static String caribbeanShoresFile = "/usr/local/files/caribbean_shores.xlsx"

    def init = { servletContext ->

	 	grailsApplication.domainClasses.each{domainClass ->
			if(domainClass.clazz.name.contains("bz.voter.management")){
				domainClass.metaClass.retrieveErrors = {
					def list = delegate?.errors?.allErrors.collect{messageSource.getMessage(it,null)}
					return list?.join("\n")
				}
			}
		}

		if(Sex.count() == 0){
	 		new Sex(name:'Male', code:'M').save()
	 		new Sex(name:'Female', code:'F').save()
			new Sex(name:'Unknown', code:'U').save()
		}

		if(Pledge.count() == 0){
			new Pledge(name:'Yes',code:'Y').save()
			new Pledge(name:'No',code:'N').save()
			new Pledge(name:'Undecided',code:'U').save()
		}


		if(Affiliation.count() == 0){
			new Affiliation(name:'PUP').save()
			new Affiliation(name:'UDP').save()
			new Affiliation(name:'Unknown').save()
		}

		if(Ethnicity.count() == 0){
			new Ethnicity(name:'Creole').save()
			new Ethnicity(name:'Indian').save()
			new Ethnicity(name:'Garifuna').save()
			new Ethnicity(name:'Chinese').save()
		}


		if(IdentificationType.count() == 0){
	//	new IdentificationType(name:'Passport').save()
		new IdentificationType(name:'Social Security Card').save()
		new IdentificationType(name:'Voter Id').save()

		new IdentificationType(name:'PASSPORT').save()
      new IdentificationType(name:'BIRTH CERTIFICATE').save()
      new IdentificationType(name:'NATURALIZATION DOC.').save()
      new IdentificationType(name:'BAPTISMAL CERTIFICATE').save()
      new IdentificationType(name:'UNKNOWN').save()
      new IdentificationType(name:'DEED POLL').save()
      new IdentificationType(name:'ADOPTION CERTIFICATE').save()
      new IdentificationType(name:'AFFIDAVIT').save()
      new IdentificationType(name:'SCHEDULE I').save(flush:true)
		}

		if(ElectionType.count() == 0){
			new ElectionType(name:'General',code:'GN').save()
			new ElectionType(name:'Municipal',code:'MN').save()
			new ElectionType(name:'Convention',code:'CN').save()
		}

		if(District.count() == 0){
			new District(name:'Corozal',code: 'CZ').save()
			new District(name:'Orange Walk', code:'OW').save()
			new District(name:'Belize', code:'BZ').save()
			new District(name:'Cayo', code:'CY').save()
			new District(name:'Stann Creek',code:'SC').save()
			new District(name:'Toledo',code:'TO').save(flush:true)
		}

		if(Municipality.count() == 0){
			new Municipality(name:'Belmopan',district:District.findByCode('CY')).save()
			unknownMunicipality = new Municipality(name:'Unknown', district: District.findByCode('BZ')).save(flush:true)
		}

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

		//sessionFactory.currentSession.flush()

		Environment.executeForCurrentEnvironment{
			development{
				//populateTestData()

			}//End of development
			
			test{
				populateTestData()
			}

			production{
				/*if(Voter.count() == 0 ){
					def albertDiv = Division.findByName('Albert')
					def caribbeanShoresDiv = Division.findByName('Caribbean Shores')
					if(albertDiv){
						if(voterService.listByDivision(albertDiv).size() == 0){
							populateDataFromXLSForProduction('Albert',albertFile)
						}
					}else{
						albertDiv = new Division(name:'Albert').save(flush:true)
						populateDataFromXLSForProduction('Albert',albertFile)
					}
					if(caribbeanShoresDiv){
						if(voterService.listByDivision(caribbeanShoresDiv).size() == 0){
							populateDataFromXLSForProduction('Caribbean Shores',caribbeanShoresFile)
						}
					}else{
						caribbeanShoresDiv = new Division(name:'Caribbean Shores').save(flush:true)
						populateDataFromXLSForProduction('Caribbean Shores',caribbeanShoresFile)
					}
				}*/
			}
		}

		sessionFactory.currentSession.flush()
   } 


    def destroy = {

    }


	 def populateTestData(){
		def division = new Division(name:'Albert').save()
		def election = new Election(year: 2011, electionType: ElectionType.findByName('General')).save()
		importFileService.importVoters(division,election,'Sample.xls')

	 }


	 def populateDataFromXLSForProduction(String divisionName, String fileName){
      VoterExcelImporter importer3 = new VoterExcelImporter(fileName);
      def votersMapList = importer3.getVoters()
		def division = Division.findByName(divisionName) ?: new Division(name:divisionName).save()
        
      votersMapList.each { Map voterParams ->
		  		def municipality = Municipality.findByName(voterParams.municipality) ?:  unknownMunicipality

		  		def addressParams = [
					houseNumber: voterParams.houseNumber,
					street: voterParams.street,
					municipality:  municipality
				]

				voterParams.sex = Sex.findByCode(voterParams.sex)
				voterParams.identificationType = IdentificationType.findByName(voterParams.identificationType) ?: new IdentificationType(name:'Unknown').save(flush:true)
				voterParams.affiliation = Affiliation.findByName(voterParams.affiliation) ?: Affiliation.findByName('UNKNOWN')
				voterParams.pollStation = PollStation.findByPollNumber(voterParams.pollStation) ?: new PollStation(pollNumber: "${voterParams.pollStation}", division: division).save()
				voterParams.pledge = Pledge.findByName(voterParams.pledge) ?: Pledge.findByName('Undecided')

				voterParams.address = addressParams
				voterService.add(voterParams)
       } 
	 }

	 def populateDataFromXLSForDev(){
      //VoterExcelImporter importer3 = new VoterExcelImporter(fileName);
      new VoterExcelImporter(fileName);
	 }

}
