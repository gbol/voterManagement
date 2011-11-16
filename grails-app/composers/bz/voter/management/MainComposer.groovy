package bz.voter.management

import org.zkoss.zkgrails.*


class MainComposer extends GrailsComposer {

	def springSecurityService

    def afterCompose = { window ->
	 	if(!springSecurityService.isLoggedIn()){
			execution.sendRedirect('/login')
		}
    }
}
