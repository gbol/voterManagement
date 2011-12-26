package bz.voter.management.facade

import org.zkoss.zkgrails.*

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


	 def flushSession(){
	 	sessionFactory.currentSession.flush()
	 	sessionFactory.currentSession.clear()
	 }
}
