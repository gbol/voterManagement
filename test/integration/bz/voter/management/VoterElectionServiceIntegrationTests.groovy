package bz.voter.management

import grails.test.*

class VoterElectionServiceIntegrationTests extends GroovyTestCase {


	 def voterElectionService

    protected void setUp() {
        super.setUp()

		  def address = new Address(houseNumber:'2',street:'n/a',
		  		municipality: Municipality.findByName('Belmopan')).save()

		  def division = new Division(name:"Belmopan").save()
		  def pollStation = new PollStation()
		  pollStation.pollNumber = 2
		  pollStation.division = division
		  pollStation.save()
		  
		  def person = new Person()
		  person.firstName = 'John'
		  person.lastName = 'Doe'
		  person.birthDate = new Date().parse('dd-MM-yyyy','15-04-1980')
		  person.sex = Sex.findByCode('M')
		  person.address = address
		  person.save()

		  def voter = new Voter()
		  voter.person = person
		  voter.registrationDate = new Date()
		  voter.registrationNumber = "21"
		  voter.identificationType = IdentificationType.findByName('Passport')
		  voter.pollStation = pollStation
		  voter.pledge = Pledge.findByName("Undecided")
		  voter.save()

		  def voter2 = new Voter()
		  voter2.person = person
		  voter2.registrationDate = new Date()
		  voter2.registrationNumber = "21"
		  voter2.identificationType = IdentificationType.findByName('Passport')
		  voter2.pollStation = pollStation
		  voter2.pledge = Pledge.findByName("Undecided")
		  voter2.save()


    }

    protected void tearDown() {
        super.tearDown()
    }

    void testSomething() {

		def election = new Election()
		election.year = 2008
		election.electionType = ElectionType.findByName('General')
		election.save()
		voterElectionService.addAllVoters(election)

		assertEquals 2, VoterElection.findAllByElection(election).size()

    }
}
