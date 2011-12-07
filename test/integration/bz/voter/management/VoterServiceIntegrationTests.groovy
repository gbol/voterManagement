package bz.voter.management

import grails.test.*

class VoterServiceIntegrationTests extends GroovyTestCase {


	def voterService

    protected void setUp() {
        super.setUp()
		  def address = new Address(houseNumber:'2',street:'n/a',
		  		municipality: Municipality.findByName('Belmopan')).save()

		  def division = new Division(name:"Belmopan").save()
		  def pollStation = new PollStation()
		  pollStation.pollNumber = 2
		  pollStation.division = division
		  pollStation.save()
		  
		  /*def person = new Person()
		  person.firstName = 'John'
		  person.lastName = 'Doe'
		  person.birthDate = new Date().parse('dd-MM-yyyy','15-04-1980')
		  person.sex = Sex.findByCode('M')
		  person.address = address
		  person.save()

		  def person1 = new Person()
		  person1.firstName = 'Charlie'
		  person1.lastName = 'Brown'
		  person1.birthDate = new Date().parse('dd-MM-yyyy','15-04-1980')
		  person1.sex = Sex.findByCode('M')
		  person1.address = address
		  person1.save()

		  def person2 = new Person()
		  person2.firstName = 'Sherlock'
		  person2.lastName = 'Homes'
		  person2.birthDate = new Date().parse('dd-MM-yyyy','15-04-1980')
		  person2.sex = Sex.findByCode('M')
		  person2.address = address
		  person2.save()

		  def person3 = new Person()
		  person3.firstName = 'Jane'
		  person3.lastName = 'Doe'
		  person3.birthDate = new Date().parse('dd-MM-yyyy','15-04-1980')
		  person3.sex = Sex.findByCode('F')
		  person3.address = address
		  person3.save()

		  def voter = new Voter()
		  voter.person = person
		  voter.registrationDate = new Date()
		  voter.registrationNumber = "21"
		  voter.identificationType = IdentificationType.findByName('Passport')
		  voter.pollStation = pollStation
		  voter.pledge = Pledge.findByName("Undecided")
		  voter.save()

		  def voter2 = new Voter()
		  voter2.person = person1
		  voter2.registrationDate = new Date()
		  voter2.registrationNumber = "22"
		  voter2.identificationType = IdentificationType.findByName('Passport')
		  voter2.pollStation = pollStation
		  voter2.pledge = Pledge.findByName("Undecided")
		  voter2.save()

		  def voter3 = new Voter()
		  voter3.person = person2
		  voter3.registrationDate = new Date()
		  voter3.registrationNumber = "23"
		  voter3.identificationType = IdentificationType.findByName('Passport')
		  voter3.pollStation = pollStation
		  voter3.pledge = Pledge.findByName("Undecided")
		  voter3.save()

		  def voter4 = new Voter()
		  voter4.person = person3
		  voter4.registrationDate = new Date()
		  voter4.registrationNumber = "24"
		  voter4.identificationType = IdentificationType.findByName('Passport')
		  voter4.pollStation = pollStation
		  voter4.pledge = Pledge.findByName("Undecided")
		  voter4.save()
		  */

    }

    protected void tearDown() {
        super.tearDown()
    }


    void test_Search_Only_One_Name() {
	 	def searchResults = voterService.search('Jules')
		assertEquals 1, searchResults.size()

    }

	 def test_Search_First_And_Last_Names(){
	 	def searchResults = voterService.search('Cesar, Ross')
		assertEquals 1, searchResults.size()
	 }


	 def test_Search_Partial_Names(){
		def searchResults = voterService.search('C , ro ')
		assertEquals 1, searchResults.size()
	 }


	 def test_Search_Empty_String_Should_Return_All_Voters(){
	 	def searchResults = voterService.search("")
		assertEquals Voter.count(), searchResults.size()
	 }


	 def test_List_By_Division(){
	 	def division = Division.findByName('Albert')
	 	def listResults = voterService.listByDivision(division)

		def totalVoters = 0

		PollStation.findAllByDivision(division).each{poll->
			totalVoters += Voter.countByPollStation(poll)
		}

		assertEquals listResults.size(), totalVoters
	 }

}
