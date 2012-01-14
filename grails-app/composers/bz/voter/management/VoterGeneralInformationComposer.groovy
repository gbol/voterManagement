package bz.voter.management

import org.zkoss.zkgrails.*
import org.zkoss.zk.ui.*

class VoterGeneralInformationComposer extends GrailsComposer {

	def springSecurityService

    def voterFacade
    def details

    def afterCompose = { window ->
	 	if(springSecurityService.isLoggedIn()){
			def voterElection = Executions.getCurrent().getArg().voterElection
            details = voterFacade.getBasicSummary(voterElection)
		}else{
			execution.sendRedirect('/login')
		}
	 	
    }
}
