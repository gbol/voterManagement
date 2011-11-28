package bz.voter.management

import grails.test.*

class VoterElectionTests extends GroovyTestCase {

	def election
	def voterElectionService

    protected void setUp() {
        super.setUp()

		  election = new Election(year: 2012, electionType: ElectionType.findByCode('GN')).save()

		  def division = new Division(name: 'Belmopan').save()

		  def pollStation = new PollStation(pollNumber: 2011, division: division).save()

		  def address = new Address()
		  address.houseNumber = '0'
		  address.street = 'n/a'
		  address.municipality = Municipality.findByName('Belmopan')
		  address.save()

		  def person1 = new Person()
		  person1.firstName = 'Michael'
		  person1.lastName = 'Jordi'
		  person1.birthDate = new Date().parse('dd-mm-yyyy','01-09-1983')
		  person1.sex = Sex.findByCode('M')
		  person1.address = address
		  person1.save()

		  def voter1 = new Voter()
		  voter1.person = person1
		  voter1.registrationNumber = '1244523'
		  voter1.registrationDate = new Date().parse('dd-mm-yyyy','12-10-1999')
		  voter1.identificationType = IdentificationType.findByName('Passport')
		  voter1.affiliation = Affiliation.findByName('PUP')
		  voter1.pollStation = pollStation
		  voter1.validate()
		  voter1.save()

		  voterElectionService.addAllVoters(election)
    }

    protected void tearDown() {
        super.tearDown()
    }

    void test_findVotesSummaryByPollStationsAndElection_should_return_list_of_vote_count_by_poll_station() {
		def voterElection = VoterElection.get(Voter.findByPerson(Person.findByFirstName('John')).id, election.id)
		voterElection.voted = true
		voterElection.voteTime = new Date()
		voterElection.save()

		def voterElection2 = VoterElection.get(Voter.findByPerson(Person.findByLastName('Faber')).id, election.id)
		voterElection2.voted = true
		voterElection2.voteTime = new Date()
		voterElection2.save()

		def voterElection3 = VoterElection.get(Voter.findByPerson(Person.findByLastName('Ross')).id, election.id)
		voterElection3.voted = true
		voterElection3.voteTime = new Date()
		voterElection3.save()

		def voterElection4 = VoterElection.get(Voter.findByPerson(Person.findByLastName('Perez')).id, election.id)
		voterElection4.voted = true
		voterElection4.voteTime = new Date()
		voterElection4.save()

		def voterElection5 = VoterElection.get(Voter.findByPerson(Person.findByLastName('Santos')).id, election.id)
		voterElection5.voted = true
		voterElection5.voteTime = new Date()
		voterElection5.save()

		def voterElection6 = VoterElection.get(Voter.findByPerson(Person.findByLastName('Aguilar')).id, election.id)
		voterElection6.voted = true
		voterElection6.voteTime = new Date()
		voterElection6.save()

		def voterElection7 = VoterElection.get(Voter.findByPerson(Person.findByLastName('Jordi')).id,election.id)
		voterElection7.voted = true
		voterElection7.voteTime = new Date()
		voterElection7.save()

		def results = VoterElection.countVotesByPollStationAndAffiliation(election)

		assert [1,23,'UNKNOWN','Albert'] ==  results[0]
		assert [3,10,'PUP','Albert'] ==  results[1]
		assert [2,10,'UDP','Albert'] ==  results[2]
		assert [1,2011,'PUP','Belmopan'] ==  results[3]

    }
}
