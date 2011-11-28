package bz.voter.management

import org.zkoss.zkgrails.*
import org.zkoss.zul.*
import org.zkoss.zk.ui.*

import bz.voter.management.zk.ComposerHelper

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

class ElectionOfficeMainComposer extends GrailsComposer {

	def electionOfficeCenter
	def navigationBox

    def afterCompose = { window ->
	 	if(SpringSecurityUtils.ifAnyGranted('ROLE_ADMIN,ROLE_OFFICE_STATION')){
			electionOfficeCenter.getChildren().clear()
			navigationBox.getChildren().clear()
			def electionId = Executions.getCurrent().getArg().id
			Executions.createComponents("electionOfficeVoters.zul",
				electionOfficeCenter,[id: electionId])
			Executions.createComponents("electionOfficeNavigation.zul",
				navigationBox,[id: electionId])
		}else{
			ComposerHelper.permissionDeniedBox()
		}
    }
}
