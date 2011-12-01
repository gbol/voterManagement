package bz.voter.management

import grails.test.*

class VoterElectionServiceIntegrationTests extends GroovyTestCase {


	 def voterElectionService
	 def albertDivision

    protected void setUp() {
        super.setUp()

		  albertDivision = Division.findByName('Albert')
		  println "Albert division: ${albertDivision}"

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

		  def person1 = new Person()
		  person1.firstName = 'Joseph'
		  person1.lastName = 'User'
		  person1.birthDate = new Date().parse('dd-MM-yyyy','15-04-1980')
		  person1.sex = Sex.findByCode('M')
		  person1.address = address
		  person1.save()

		  def voter = new Voter()
		  voter.person = person
		  voter.registrationDate = new Date()
		  voter.registrationNumber = "21"
		  voter.identificationType = IdentificationType.findByName('Passport')
		  voter.pollStation = pollStation
		  voter.pledge = Pledge.findByName("Undecided")
		  voter.save(flush:true)

		  def voter2 = new Voter()
		  voter2.person = person1
		  voter2.registrationDate = new Date()
		  voter2.registrationNumber = "21"
		  voter2.identificationType = IdentificationType.findByName('Passport')
		  voter2.pollStation = pollStation
		  voter2.pledge = Pledge.findByName("Undecided")
		  voter2.save(flush:true)


    }

    protected void tearDown() {
        super.tearDown()
    }

    void test_Add_All_Voters_When_Election_Is_Created() {

		def election = new Election()
		election.year = 2008
		election.electionType = ElectionType.findByName('General')
		election.save()
		voterElectionService.addAllVoters(election)

		assertEquals 13, VoterElection.findAllByElection(election).size()

    }


	 void test_Search_For_Voters_In_An_Election(){

	 	def election = new Election()
		election.year = 2008
		election.electionType = ElectionType.findByName('General')
		election.save()
		voterElectionService.addAllVoters(election)

		def results = voterElectionService.search('f',election,albertDivision)

		assertEquals 6, results.size()
	 }


	 void test_Search_For_Voters_In_An_Election_Using_Both_Names(){
	 	def election = new Election()
		election.year = 2008
		election.electionType = ElectionType.findByName('General')
		election.save()
		voterElectionService.addAllVoters(election)

		def results = voterElectionService.search('Fe, re ',election,albertDivision)

		assertEquals 1, results.size()
	 }


	 void test_Search_For_Voters_With_Empty_String_Should_Return_All_Voters(){
	 	def election = new Election()
		election.year = 2008
		election.electionType = ElectionType.findByName('General')
		election.save()
		voterElectionService.addAllVoters(election)

		def results = voterElectionService.search('  ',election,albertDivision)

		assertEquals VoterElection.findAllByElection(election).size(), results.size()
	 	
	 }
}
