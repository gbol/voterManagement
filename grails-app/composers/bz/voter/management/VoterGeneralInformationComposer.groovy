package bz.voter.management

import org.zkoss.zkgrails.*
import org.zkoss.zk.ui.*

class VoterGeneralInformationComposer extends GrailsComposer {

	def voter
	def birthDate
	def registrationDate
	def voteTime

	def springSecurityService

    def afterCompose = { window ->
	 	if(springSecurityService.isLoggedIn()){
			voter = Voter.load(Executions.getCurrent().getArg().id)
			def voterElection = VoterElection.get(voter.id, Executions.getCurrent().getArg().electionId)
			birthDate = voter.person.birthDate.format('dd-MMM-yyyy')
			registrationDate = voter.registrationDate.format('dd-MMM-yyyy')
			voteTime = voterElection.voteTime
		}else{
			execution.sendRedirect('/login')
		}
	 	
    }
}
