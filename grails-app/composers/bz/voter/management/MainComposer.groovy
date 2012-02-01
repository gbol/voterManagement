package bz.voter.management

import org.zkoss.zkgrails.*


class MainComposer extends GrailsComposer {

	def springSecurityService

    def headerLabel

    def grailsApplication

    def afterCompose = { window ->
	 	if(!springSecurityService.isLoggedIn()){
			execution.sendRedirect('/login')
		}else{
            headerLabel.setValue("Voter Management System ${grailsApplication.metadata['app.version']}")
        }
    }
}
