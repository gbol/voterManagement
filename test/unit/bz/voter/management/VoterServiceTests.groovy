package bz.voter.management

import grails.test.*

import java.util.Calendar

class VoterServiceTests extends GrailsUnitTestCase {
    protected void setUp() {
        super.setUp()
		  def pledge = new Pledge(name:'Yes')
		  def affiliation = new Affiliation(name:'PUP')
		  def district = new District(name:'Cayo', code:'CY')
		  def municipality = new Municipality(name:'Belmopan', district:district)
		  def division = new Division(name: 'Belmopan')
		  def pollStation = new PollStation(pollNumber:1,division:division)
		  def sex = new Sex(name:'Male', code:'M')
		  def identificationType = new IdentificationType(name:'Passport')
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

		  new ElectionType(name: 'General',code:'G').save()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testSaveVoter() {

		new Election(year: 2010, electionType: ElectionType.findByName('General')).save()
		new Election(year: 2011, electionType: ElectionType.findByName('General')).save()
		new Election(year: 2012, electionType: ElectionType.findByName('General')).save()
		new Election(year: 2008, electionType: ElectionType.findByName('General')).save()
		new Election(year: 2007, electionType: ElectionType.findByName('General')).save()


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

			return person
		}]

		/*voterService.Election = [findAll: {
			Election.findAllByYearGreaterThanEquals(2011)	
		}
		]*/

		def voter = voterService.add(params)

		assertNotNull voter.id
		assertEquals 'John', voter.person.firstName
		assertEquals 'N/A', voter.person.address.street

		assertEquals 3, VoterElection.findAllByVoter(voter).size()

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
			municipality: Municipality.findByName('Belmopan') ).save()
	
		def person = new Person(
			firstName: 'John',
			lastName: 'Doe',
			birthDate: new Date(77,04,15),
			sex: Sex.findByCode('M'),
			address: addressInstance
		).save()

		def voter = new Voter(
			person: person,
			registrationDate: new Date().parse('dd/MM/yyyy','01/11/2011'),
			registrationNumber: '123',
			identificationType: IdentificationType.findByName('Passport'),
			pledge: Pledge.findByName('Yes'),
			affiliation: Affiliation.findByName('PUP'),
			pollStation: PollStation.findByPollNumber(1)).save()

		def params = [
			registrationNumber: '456',
			firstName: 'John',
			voter: voter
		]


		voterService.messageSource = [getMessage:{errors, locale-> return "error message"}]
		voterService.personService = [save : {
			def personInstance = Person.findByFirstName('John')
			personInstance.firstName = 'John'

			personInstance.validate()

			personInstance.save()

			return personInstance
		}]

		def voterInstance = voterService.save(params)

		assertEquals 'John',voterInstance.person.firstName
		assertEquals '456', voterInstance.registrationNumber
		assertEquals 2011, voterInstance.registrationDate.toCalendar().get(Calendar.YEAR)
			
	 }
}
