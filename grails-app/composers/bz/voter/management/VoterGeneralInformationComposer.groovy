package bz.voter.management

import org.zkoss.zkgrails.*
import org.zkoss.zk.ui.*

class VoterGeneralInformationComposer extends GrailsComposer {

	def voter
	def birthDate
	def registrationDate

	def springSecurityService

    def afterCompose = { window ->
	 	if(springSecurityService.isLoggedIn()){
			voter = Voter.get(Executions.getCurrent().getArg().id)
			birthDate = voter.person.birthDate.format('dd-MMM-yyyy')
			registrationDate = voter.registrationDate.format('dd-MMM-yyyy')
		}else{
			execution.sendRedirect('/login')
		}
	 	
    }
}
