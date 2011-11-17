package bz.voter.management

import grails.test.*

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
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testSaveVoter() {
		def params = [
			firstName : 'John',
			lastName : 'Doe',
			birthDate: new Date(77,04,15),
			registrationDate: new Date(2001, 01, 01),
			registrationNumber: 1,
			sex: Sex.findByCode('M'),
			houseNumber: 2,
			street: "None",
			municipality: Municipality.findByName('Belmopan'),
			identificationType: IdentificationType.findByName('Passport'),
			pollStation: PollStation.findByPollNumber(1),
			pledge: Pledge.findByName('Yes'),
			affiliation: Affiliation.findByName('PUP')
		]

		def voterService = new VoterService()
		voterService.messageSource = [getMessage:{errors, locale-> return "error message"}]
		//voterService.addressInstance = [validate: {}, hasErrors:{false}]
		//voterService.addressInstance.metaClass.validate = {}
		//voterService.addressInstance.metaClass.hasErrors = {false}
		/*MockForConstraintsTests(Address,voterService.addressInstance)
		MockForConstraintsTests(Person,voterService.personInstance)
		MockForConstraintsTests(Voter,voterService.voterInstance)*/
		def voter = voterService.saveVoter(params)
		println "voter.id: ${voter.id}"

    }
}
