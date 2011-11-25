package bz.voter.management

import org.zkoss.zkgrails.*
import org.zkoss.zul.*
import org.zkoss.zk.ui.*

import bz.voter.management.zk.ComposerHelper

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

class ElectionOfficeMainComposer extends GrailsComposer {

	def electionOfficeCenter

    def afterCompose = { window ->
	 	if(SpringSecurityUtils.ifAnyGranted('ROLE_ADMIN,ROLE_OFFICE_STATION')){
			electionOfficeCenter.getChildren().clear()
			Executions.createComponents("electionOfficeVoters.zul",electionOfficeCenter,[id: Executions.getCurrent().getArg().id])
		}else{
			ComposerHelper.permissionDeniedBox()
		}
    }
}
