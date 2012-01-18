package bz.voter.management.facade

import org.zkoss.zkgrails.*

import bz.voter.management.VoterElection
import bz.voter.management.Voter
import bz.voter.management.Address
import bz.voter.management.AddressType

class VoterFacade {

    Voter voter

	 def voterService

	 def sessionFactory

    List<Voter> getVoters() {
        Voter.list()
    }

	 def save(params){
	 	if(params.voter){
			voter = voterService.save(params)
		}else{
			voter = voterService.add(params,null,true)
		}
		flushSession()
		return voter
	 }


    /**
    Displays basic summary of a voter.  Displays only basic information ideal for using on a 
    small pop-up window.
    @param voterElection is an instance that holds the voter and the election. A voter's information varies 
    from election to election.
    @return a map with the basic information
    **/
    def getBasicSummary(VoterElection voterElection ){
        voterElection = voterElection.merge()
        def voterInstance = voterElection.voter
        def electionInstance = voterElection.election
        def details = [
            firstName: voterInstance.firstName,
            middleName: voterInstance.middleName,
            lastName: voterInstance.lastName,
            birthDate: voterInstance.birthDate.format('dd-MMM-yyyy'),
            age: voterInstance.age,
            sex: voterInstance.sex,
            registrationAddress: "${Address.findByPersonAndAddressType(voterInstance.person, AddressType.findByName('Registration'))}",
            workAddress: "${Address.findByPersonAndAddressType(voterInstance.person, AddressType.findByName('Work'))}",
            alternateAddress: "${Address.findByPersonAndAddressType(voterInstance.person, AddressType.findByName('Alternate'))}",
            pollStation: voterInstance.pollStation,
            pollNumber: voterInstance.pollStation.pollNumber,
            registrationDate: voterInstance.registrationDate.format('dd-MMM-yyyy'),
            registrationNumber: voterInstance.registrationNumber,
            voteTime: voterElection.voteTime,
            affiliation: "${voterInstance.affiliation}",
            pledge:     "${voterElection.pledge}",
            pickupTime: "${voterElection.pickupTime}"
        ]

        return details
    }


    /**
    Gets basic demographic information about a voter. Primary use is for displaying this information in a zk view.
    @return a map with the following fields:
    <ol>
        <li>firstName</li>
        <li>middleName</li>
        <li>lastName</li>
        <li>birthDate</li>
        <li>age</li>
        <li>sex</li>
        <li>alive</li>
    </ol>
    **/
    def getBasicInformation(){
        this.voter = Voter.load(voter.id)
        def basicInfo = [
            firstName: voter.firstName,
            middleName: voter.middleName,
            lastName: voter.lastName,
            birthDate: voter.birthDate,
            age: voter.age,
            sex: voter.sex,
            alive: voter.isAlive()
        ]

        return basicInfo
    }



    /**
    Saves a voter's basic information.
    @param a map with the basci information to save:
    <ul>
        <li>firstName</li>
        <li>middleName</li>
        <li>lastName</li>
        <li>birthDate</li>
        <li>age</li>
        <li>sex</li>
        <li>alive</li>
    </ul>
    **/
    def saveBasicInformation(params){
        voter = Voter.load(voter.id)
        def person = voter.person
        
        person.firstName = params.firstName ?: person.firstName
        person.middleName = params.middleName ?: person.middleName
        person.lastName = params.lastName ?: person.lastName
        person.birthDate = params.birthDate ?: person.birthDate
        person.sex = params.sex ?: person.sex
        person.alive = params.alive

        person.validate()

        if(person.hasErrors()){
            log.error person.retrieveErrors()
        }else{
            person.save()
        }

        flushSession()

        return person
    }


    
    /**
    Returns the address of a particular voter.
    @return A map with the address information 
    <ol>
        <li>houseNumber</li>
        <li>street</li>
        <li>district</li>
        <li>municipality</li>
    </ol>
    **/
    def getAddress(){
        //def addressInstance = Address.load(voterInstance.person.address.id)
        this.voter = Voter.load(voter.id)
        def addressInstance = this.voter.person.address
        def addressInfo = [
            houseNumber: addressInstance.houseNumber,
            street: addressInstance.street,
            district: addressInstance.municipality.district,
            municipality: addressInstance.municipality
        ]

        return addressInfo
    }


    /**
    Saves a voter's address.
    @param A map with the address values we wish to save.
    @return A map with the address information
    <ul>
        <li>houseNmumber</li>
        <li>street</li>
        <li>municipality</li>
    </ul>
    **/
    def saveAddress(params){
        this.voter = Voter.load(voter.id)
        def addressInstance = this.voter.person.address

        addressInstance.houseNumber = params.houseNumber ?: addressInstance.houseNumber
        addressInstance.street = params.street ?: addressInstance.street
        addressInstance.municipality = params.municipality ?: addressInstance.municipality

        addressInstance.validate()

        if(addressInstance.hasErrors()){
            log.error addressInstance.retrieveErrors()
        }else{
            addressInstance.save()
        }

        flushSession()

        return addressInstance
    }


    /**
    Get a voter's registration information.
    @return A map with the voter's registration information:
    <ul>
        <li>registrationNumber</li>
        <li>registrationDate</li>
        <li>pollStation</li>
    </ul>
    **/
    def getRegistrationInformation(){
        this.voter = Voter.load(voter.id)
        def information = [
            registrationNumber: voter.registrationNumber,
            registrationDate: voter.registrationDate,
            pollStation: voter.pollStation
        ]

        return information

    }



    /**
    Save a voter's registration information.
    @param Map with the registration information to be saved:
    <ul>
        <li>registrationNumber</li>
        <li>registrationDate</li>
        <li>pollStation</li>
    </ul>
    @return Voter instance.
    **/
    def saveRegistrationInformation(params){
        this.voter = Voter.load(voter.id)

        voter.registrationNumber = params.registrationNumber ?: voter.registrationNumber
        voter.registrationDate = params.registrationDate ?: voter.registrationDate
        voter.pollStation = params.pollStation ?: voter.pollStation


        voter.validate()

        if(voter.hasErrors()){
            log.error voter.retrieveErrors()
        }else{
            voter.save()
        }


        return voter
    }



    /**
    Gets a voter's contact information.
    @return Map with contact information:
     <ul>
        <li>homePhone</li>
        <li>cellPhone</li>
        <li>workPhone</li>
        <li>emailAddress</li>
     </ul>
    **/
    def getContactInformation(){
        this.voter = Voter.load(voter.id)
        def personInstance = voter.person

        def contactInformation =[
            homePhone:      personInstance.homePhone,
            cellPhone:      personInstance.cellPhone,
            workPhone:      personInstance.workPhone,
            emailAddress:   personInstance.emailAddress
        ]

        return contactInformation

    }


    /**
    Save a voter's contact information
    @param Map with the voter's contact information that should be saved:
    <ul>
        <li>homePhone</li>
        <li>cellPhone</li>
        <li>workPhone</li>
        <li>emailAddress</li>
    </ul>
    @return An instance of person.
    **/
    def saveContactInformation(params){
        this.voter = Voter.load(voter.id)
        def personInstance = voter.person

        personInstance.homePhone        = params.homePhone ?: personInstance.homePhone
        personInstance.cellPhone        = params.cellPhone ?: personInstance.cellPhone
        personInstance.workPhone        = params.workPhone ?: personInstance.workPhone
        personInstance.emailAddress     = params.emailAddress ?: personInstance.emailAddress

        personInstance.validate()
        if(personInstance.hasErrors()){
            log.error personInstance.retrieveErrors()
        }else{
            personInstance.save()
        }


        return personInstance

    }


	 def flushSession(){
	 	sessionFactory.currentSession.flush()
	 	sessionFactory.currentSession.clear()
	 }
}
