package bz.voter.management

import org.zkoss.zkgrails.*
import org.zkoss.zk.ui.*

import bz.voter.management.zk.ComposerHelper

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils


class ElectionOfficeNavigationComposer extends GrailsComposer {

	def votersButton
	def dashboardButton

	def electionOfficeCenter

    def afterCompose = { window ->
    }


	 def onClick_dashboardButton(){
	 	if(SpringSecurityUtils.ifAnyGranted('ROLE_ADMIN,ROLE_OFFICE_STATION')){
			electionOfficeCenter.getChildren().clear()
			Executions.createComponents("electionOfficeDashboard.zul",electionOfficeCenter,
			null)
		}else{
			ComposerHelper.permissionDeniedBox()
		}
	 }

}
