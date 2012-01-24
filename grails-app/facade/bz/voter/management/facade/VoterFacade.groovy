package bz.voter.management.facade

import org.zkoss.zkgrails.*

import bz.voter.management.VoterElection
import bz.voter.management.Voter
import bz.voter.management.Person
import bz.voter.management.Address
import bz.voter.management.AddressType
import bz.voter.management.Dependent
import bz.voter.management.utils.AddressEnum
import bz.voter.management.Affiliation
import bz.voter.management.Activity
import bz.voter.management.Pledge

class VoterFacade {

    Voter voter

	 def voterService
     def personService

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
        <li>emailAddress</li>
        <li>alive</li>
        <li>affiliation</li>
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
            emailAddress: voter.emailAddress,
            affiliation: voter.affiliation,
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
        <li>emailAddress</li>
        <li>affiliation</li>
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
        person.emailAddress = params.emailAddress

        person.validate()

        if(person.hasErrors()){
            log.error person.retrieveErrors()
        }else{
            person.save()

            if(params.affiliation && (params.affiliation instanceof Affiliation)){
                voter.affiliation = params.affiliation
                voter.save()
            }
        }

        flushSession()

        return person
    }


    
    /**
    Returns the address of a particular voter.
    @return A map with the address information 
    <ul>
        <li>id</li>
        <li>houseNumber</li>
        <li>street</li>
        <li>district</li>
        <li>municipality</li>
        <li>phoneNumber1</li>
        <li>phoneNumber2</li>
        <li>phoneNumber3</li>
    </ul>
    **/
    def getAddress(AddressEnum addressType){
        this.voter = Voter.load(voter.id)
        def person = voter.person
        def addressInstance

        switch(addressType){
            case AddressEnum.REGISTRATION:
                addressInstance = Address.findByPersonAndAddressType(person,AddressType.findByName('Registration'))
                break
            case AddressEnum.WORK:
                addressInstance = Address.findByPersonAndAddressType(person,AddressType.findByName('Work'))
                break
            case AddressEnum.ALTERNATE:
                addressInstance = Address.findByPersonAndAddressType(person,AddressType.findByName('Alternate'))
                break
        }

        def addressInfo = [
            id:             addressInstance?.id,
            houseNumber:    addressInstance?.houseNumber,
            street:         addressInstance?.street,
            district:       addressInstance?.municipality?.district,
            municipality:   addressInstance?.municipality,
            phoneNumber1:   addressInstance?.phoneNumber1,
            phoneNumber2:   addressInstance?.phoneNumber2,
            phoneNumber3:   addressInstance?.phoneNumber3
        ]

        return addressInfo
    }




    /**
    Saves a voter's address.
    @param A map with the address values we wish to save.
    @return A map with the address information
    <ul>
        <li>id</li>
        <li>houseNumber</li>
        <li>street</li>
        <li>municipality</li>
        <li>phoneNumber1</li>
        <li>phoneNumber2</li>
        <li>phoneNumber3</li>
    </ul>
    **/
    def saveAddress(params){
        def addressInstance = Address.load(params.id) ?: new Address()

        addressInstance.houseNumber = params.houseNumber ?: addressInstance?.houseNumber
        addressInstance.street = params.street ?: addressInstance?.street
        addressInstance.municipality = params.municipality ?: addressInstance?.municipality
        addressInstance.phoneNumber1 = params.phoneNumber1 ?: addressInstance?.phoneNumber1
        addressInstance.phoneNumber2 = params.phoneNumber2 ?: addressInstance?.phoneNumber2
        addressInstance.phoneNumber3 = params.phoneNumber3 ?: addressInstance?.phoneNumber3

        if(!addressInstance.id){
            this.voter = Voter.load(voter.id)
            def addressType
            switch(params.addressType){
                case AddressEnum.ALTERNATE:
                    addressType = AddressType.findByName('Alternate')
                    break
                case AddressEnum.WORK:
                    addressType = AddressType.findByName('Work')
                    break
            }

            addressInstance.addressType = addressType
            addressInstance.person = voter.person
        }

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


    /**
    Gets a voter's dependents.
    @param Map with voter's dependents' information.
    <ul>
        <li>id</li>
        <li>firstName</li>
        <li>middleName</li>
        <li>lastName</li>
        <li>birthDate</li>
        <li>age</li>
        <li>emailAddress</li>
        <li>sex</li>
        <li>relation<li>
    </ul>
    **/
    def getDependents(voter){
        def dependents = []
        voter = Voter.load(voter.id)
        
        for(voterDependent in Dependent.getByVoter(voter)){
            def dependent = voterDependent.person
            def _dependent = [
                id:             dependent.id,
                firstName:      dependent.firstName,
                middleName:     dependent.middleName,
                lastName:       dependent.lastName,
                birthDate:      dependent.birthDate,
                age:            dependent.age,
                emailAddress:   dependent.emailAddress,
                sex:            dependent.sex,
                relation:       voterDependent.relation
            ]

            dependents.push(_dependent)
        }

        return dependents
    }



    /**
    Saves a dependent instance.
    @param Map with the dependent's information:
    <ul>
        <li>id</li>
        <li>firstName</li>
        <li>middleName</li>
        <li>lastName</li>
        <li>birthDate</li>
        <li>emailAddress</li>	
        <li>sex</li>
        <li>relation</li>
    </ul>
    @return Dependent 
    **/
    def saveDependent(params){
        this.voter = Voter.load(voter.id)
        def person = Person.load(params.id)
        params.person = Person.get(params.id) 

        def personInstance = personService.save(params)
        def dependentInstance

        if(!personInstance.hasErrors()){
            dependentInstance = Dependent.get(voter.id, personInstance.id)
            if(!dependentInstance?.voter){
                dependentInstance = Dependent.create(voter,personInstance,params.relation,true)
            }else{
                dependentInstance.relation = params.relation
            }
        }
        
        return dependentInstance 
    }




    /**
    Deletes a dependent.
    @param id person's id.
    @return true if successful deletion.
    **/
    def deleteDependent(id){
        voter = Voter.load(voter.id)
        def person = Person.load(id)
        Dependent.remove(voter,person,true)
    }


    
    /**
    Lists all activities for a specific staff
    @return List of activities
    **/
    def getActivities(){
        voter = Voter.load(voter.id)

        def activities = []

        for(activity in Activity.findAllByVoter(voter)){
            def data = [
                activityId:     activity.id,
                activityType:   activity.activityType,
                notes:          activity.notes,
                date:           activity.activityDate,
                voter:          activity.voter
            ]

            activities.push(data)
        }

        return activities

    }


    
    /**
    Saves an activity that a voter participates in.
    @param Map with required values to save an activity:
    <ul>
        <li>voterId</li>
        <li>activityId</li>
        <li>activityType</li>
        <li>notes</li>
        <li>activityDate</li>
    </ul>
    **/
    def saveActivity(params){

        voter = Voter.load(params.voterId.toLong())
        def activity = Activity.get(params.activityId?.toLong())  ?: new Activity()

        activity.voter = voter
        activity.activityType = params.activityType ?: activity?.activityType
        activity.activityDate = params.activityDate ?: activity?.activityDate
        activity.notes = params.notes ?: activity?.notes

        activity.validate()
        if(activity.hasErrors()){
            log.error activity.retrieveErrors()
        }else{
            activity.save()
        }

        flushSession()

        return activity

    }



    def deleteActivity(activityId){
        def activity = Activity.get(activityId?.toLong())
        if(activity){
            activity.delete()
        }

        flushSession()
        
    }


    /**
    Gets a voter's list of pledges.
    @return Map with details of pledges.
    <ul>
        <li>election</li>
        <li>pledge</li>
    </ul>
    **/
    def getPledges(){
        voter = Voter.load(voter.id)

        def results = [] 

        for(ve in VoterElection.findAllByVoter(voter)){
            def data = [
                election: ve.election,
                pledge:   ve.pledge
            ]

            results.push(data)
        }

        return results
    }


    /**
    Save's a voter's pledge for an election.
    @param Pledge 
    @param Election
    @return VoterElection instance
    **/
    def savePledge(election, pledge){

        def voterElection = VoterElection.get(this.voter?.id, election?.id)

        if(voterElection instanceof VoterElection){
            voterElection.pledge = pledge
            voterElection.validate()

            if(voterElection.hasErrors()){
                log.error voterElection.retrieveErrors()
            }else{
                voterElection.save()
            }
        }

        flushSession()

        return voterElection
    }


    /**
    Gets a voter's pledge for a specific election.
    @param Election
    @return Pledge
    **/
    def getPledge(election){
        def voterElection = VoterElection.get(voter?.id, election?.id)

        return voterElection?.pledge
    }


	 def flushSession(){
	 	sessionFactory.currentSession.flush()
	 	sessionFactory.currentSession.clear()
	 }
}
