package bz.voter.management.facade

import org.zkoss.zkgrails.*

import bz.voter.management.VoterElection
import bz.voter.management.Voter

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
    @returns a map with the basic information
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
            address: voterInstance.address,
            pollStation: voterInstance.pollStation,
            affiliation: voterInstance.affiliation,
            pledge: voterElection.pledge,
            registrationDate: voterInstance.registrationDate.format('dd-MMM-yyyy'),
            registrationNumber: voterInstance.registrationNumber,
            voteTime: voterElection.voteTime
        ]

        return details
    }


	 def flushSession(){
	 	sessionFactory.currentSession.flush()
	 	sessionFactory.currentSession.clear()
	 }
}
