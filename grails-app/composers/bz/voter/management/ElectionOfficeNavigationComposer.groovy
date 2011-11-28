package bz.voter.management

import org.zkoss.zkgrails.*
import org.zkoss.zk.ui.*

import bz.voter.management.zk.ComposerHelper

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils


class ElectionOfficeNavigationComposer extends GrailsComposer {

	def votersButton
	def dashboardButton

	def electionOfficeCenter

	def election


    def afterCompose = { window ->
		def electionId = Executions.getCurrent().getArg().id
		election = Election.get(electionId)
    }


	 def onClick_dashboardButton(){
	 	if(SpringSecurityUtils.ifAnyGranted('ROLE_ADMIN,ROLE_OFFICE_STATION')){
			electionOfficeCenter.getChildren().clear()
			Executions.createComponents("electionOfficeDashboard.zul",electionOfficeCenter,
			[electionId: election.id])
		}else{
			ComposerHelper.permissionDeniedBox()
		}
	 }

	 def onClick_votersButton(){
	 	if(SpringSecurityUtils.ifAnyGranted('ROLE_ADMIN,ROLE_OFFICE_STATION')){
			electionOfficeCenter.getChildren().clear()
			Executions.createComponents("electionOfficeVoters.zul",electionOfficeCenter,
				[id: election.id])
		}else{
			ComposerHelper.permissionDeniedBox()
		}
	 }

}
