package org.zkoss.zklargelivelist.model

import grails.test.*
import groovy.mock.interceptor.MockFor

import bz.voter.management.Voter
import bz.voter.management.Pledge
import bz.voter.management.Affiliation
import bz.voter.management.District
import bz.voter.management.Municipality
import bz.voter.management.Division
import bz.voter.management.Person
import bz.voter.management.Address
import bz.voter.management.Sex
import bz.voter.management.VoterElection
import bz.voter.management.Election
import bz.voter.management.IdentificationType
import bz.voter.management.PollStation
import bz.voter.management.ElectionType
import bz.voter.management.VoterService


class DivisionVotersPagingListModelTests extends GrailsUnitTestCase {

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


    void testSomething() {
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
			affiliation: Affiliation.findByName('PUP')
			//address: addressParams
		]


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


		new Voter(
			person: person,
			pollStation: params.pollStation,
			registrationNumber: params.registrationNumber,
			affiliation: params.affiliation,
			registrationDate: params.registrationDate,
			identificationType: params.identificationType
		).save()

	 	println "Voters List: ${Voter.list()}"

		VoterService.metaClass.static.listByDivision = {
			Voter.list()
		}

		def mockForVoterService  = new MockFor(VoterService)

		mockForVoterService.demand.listByDivision(division) {
			return Voter.list()
		}

		DivisionVotersPagingListModel divisionVotersPagingListModel 
		//divisionVotersPagingListModel.voterService = mockForVoterService.proxyInstance()
		divisionVotersPagingListModel = new DivisionVotersPagingListModel(division,0,1)

		//def voterService = mockForVoterService.proxyInstance()



		//println "voterService.listByDivision(division): ${voterService.listByDivision(division)}"
		println "total Size:  ${divisionVotersPagingListModel.getTotalSize(division)}"


    }
}
