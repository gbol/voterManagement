package bz.voter.management

import grails.test.*

import java.util.Calendar

class VoterServiceTests extends GrailsUnitTestCase {
  def pledge
  def affiliation
  def district
  def municipality
  def division
  def pollStation
  def sex
  def identificationType

    protected void setUp() {
        super.setUp()
		  mockDomain(Pledge, [pledge])
		  mockDomain(Affiliation, [affiliation])
		  mockDomain(District, [district])
		  mockDomain(Municipality, [municipality])
		  mockDomain(Division, [division])
		  mockDomain(PollStation, [pollStation])
		  mockDomain(Sex, [sex])
		  mockDomain(IdentificationType, [identificationType])
		  mockDomain(Person)
		  mockDomain(Address)
		  mockForConstraintsTests(Voter)
		  mockForConstraintsTests(Person)
		  mockDomain(Voter)
		  mockDomain(VoterElection)
		  mockDomain(Election)
		  mockDomain(ElectionType)
		  mockLogging(VoterService.class,true)
		  pledge = new Pledge(name:'Yes').save()
		  affiliation = new Affiliation(name:'PUP').save()
		  district = new District(name:'Cayo', code:'CY').save()
		  municipality = new Municipality(name:'Belmopan', district:district).save()
		  division = new Division(name: 'Belmopan').save()
		  pollStation = new PollStation(pollNumber:'1',division:division).save()
		  sex = new Sex(name:'Male', code:'M').save()
		  identificationType = new IdentificationType(name:'Passport').save()

		  new ElectionType(name: 'General',code:'G').save()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testSaveVoter() {

		new Election(year: 2010, electionType: ElectionType.findByName('General')).save()
		new Election(year: 2011, electionType: ElectionType.findByName('General')).save()
		def election = new Election(year: 2012, electionType: ElectionType.findByName('General')).save()
		new Election(year: 2008, electionType: ElectionType.findByName('General')).save()
		new Election(year: 2007, electionType: ElectionType.findByName('General')).save()

		def pledge = new Pledge(name:'Yes', code:'Y').save()


		def addressParams = [
			houseNumber: 2,
			street: "None",
			municipality: Municipality.findByName('Belmopan')
		]

		def params = [
			firstName : 'John',
			lastName : 'Doe',
			birthDate: new Date(77,04,15),
			registrationDate: new Date().parse('yyyy-dd-mm','2010-01-01'),
			registrationNumber: 1,
			sex: Sex.findByCode('M'),
			identificationType: identificationType,
			pollStation: pollStation,
			pledge: Pledge.findByName('Yes'),
			affiliation: Affiliation.findByName('PUP'),
			address: addressParams,
			pledge: pledge
		]



		def voterService = new VoterService()
		voterService.messageSource = [getMessage:{errors, locale-> return "error message"}]

		voterService.personService = [save : {
			def addressInstance = new Address(houseNumber: '0',
								street: 'N/A',
								municipality: Municipality.findByName('Belmopan')).save()
			def person = new Person(
				address: addressInstance,
				firstName: params.firstName,
				lastName: params.lastName,
				birthDate: params.birthDate,
				sex: params.sex
			).save()

			return person
		}]


		def voter = voterService.add(params,election, true)

		assertNotNull voter.id
		assertEquals 'John', voter.person.firstName
		assertEquals 'N/A', voter.person.address.street

		assertEquals 1, VoterElection.findAllByVoter(voter).size()

    }


	 def test_Save_New_Voter_With_Missing_FirstName_Should_Fail(){

		def addressParams = [
			houseNumber: 2,
			street: "None",
			municipality: Municipality.findByName('Belmopan')
		]

		def params = [
			lastName : 'Doe',
			birthDate: new Date(77,04,15),
			registrationDate: new Date(2001, 01, 01),
			registrationNumber: 1,
			sex: Sex.findByCode('M'),
			identificationType: IdentificationType.findByName('Passport'),
			pollStation: PollStation.findByPollNumber(1),
			pledge: Pledge.findByName('Yes'),
			affiliation: Affiliation.findByName('PUP'),
			address: addressParams
		]



		def voterService = new VoterService()
		voterService.messageSource = [getMessage:{errors, locale-> return "error message"}]

		voterService.personService = [save : {
			def addressInstance = new Address(houseNumber: '0',
								street: 'N/A',
								municipality: Municipality.findByName('Belmopan')).save()
			def person = new Person(
				address: addressInstance,
				firstName: params.firstName,
				lastName: params.lastName,
				birthDate: params.birthDate,
				sex: params.sex
			)

			person.validate()

			return person
		}]

		def voter = voterService.save(params)

		assertEquals '1 errors', voter.errors.toString().substring(61,69) 

	 }


	 def test_Update_Existing_Voter(){

	 	def voterService = new VoterService()

		def addressInstance = new Address(
			houseNumber: '1',
			street: 'N/A',
			municipality: Municipality.findByName('Belmopan') 
		).save()

		def personInstance = new Person(
			firstName: 'John',
			lastName: 'Doe',
			birthDate: new Date(77,04,15),
			sex: Sex.findByCode('M'),
			address: addressInstance
			
		).save(flush:true)
	 	
		def addressParams = [ 
			houseNumber: '1',
			street: 'N/A',
			municipality: Municipality.findByName('Belmopan') 
		]
	

		def params = [
			registrationNumber: '456',
			registrationDate: new Date().parse('dd/MM/yyyy','01/11/2011'),
			firstName: 'John',
			lastName: 'Doe',
			birthDate: new Date(77,04,15),
			sex: Sex.findByCode('M'),
			identificationType: IdentificationType.findByName('Passport'),
			pledge: Pledge.findByName('Yes'),
			affiliation: Affiliation.findByName('PUP'),
			pollStation: PollStation.findByPollNumber(1),
			address: addressParams
		]


		println "\nPersons: ${Person.list()}"

		voterService.messageSource = [getMessage:{errors, locale-> return "error message"}]
		voterService.personService = [save : {
			/*def personInstance = Person.findByFirstNameIlike('John')
			personInstance.firstName = 'John'

			personInstance.validate()

			personInstance.save()*/

			return personInstance
		}]

		def voterInstance = voterService.save(params)
		println "person: ${voterInstance.person}"
		println "registrationDate: ${voterInstance.registrationDate}\n"

		assertEquals 'John',voterInstance.person.firstName
		assertEquals '456', voterInstance.registrationNumber
		assertEquals 2011, voterInstance.registrationDate.toCalendar().get(Calendar.YEAR)
			
	 }
}
