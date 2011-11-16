package bz.voter.management

import org.zkoss.zkgrails.*
import org.zkoss.zk.ui.*

import bz.voter.management.zk.ComposerHelper

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

class ElectionNavigationComposer extends GrailsComposer {

	def electionsListButton
	def divisionsButton
	def pledgesButton
	def electionCenter
	def pollStationsButton
	def municipalitiesButton

    def afterCompose = { window ->
    }


	 def onClick_electionsListButton(){
	 	if(SpringSecurityUtils.ifAllGranted('ROLE_ADMIN')){
	 		electionCenter.getChildren().clear()
			Executions.createComponents("electionCrudPanel.zul", electionCenter, null)
		}else{
			ComposerHelper.permissionDeniedBox()
		}
	 }

	 def onClick_divisionsButton(){
	 	if(SpringSecurityUtils.ifAllGranted('ROLE_ADMIN')){
	 		electionCenter.getChildren().clear()
			Executions.createComponents("division.zul", electionCenter, null)
		}else{
			ComposerHelper.permissionDeniedBox()
		}
	 }

	 def onClick_pollStationsButton(){
	 	if(SpringSecurityUtils.ifAllGranted('ROLE_ADMIN')){
	 		electionCenter.getChildren().clear()
			Executions.createComponents("pollingStation.zul", electionCenter, null)
		}else{
			ComposerHelper.permissionDeniedBox()
		}
	 }

	 def onClick_pledgesButton(){
	 	if(SpringSecurityUtils.ifAllGranted('ROLE_ADMIN')){
	 		electionCenter.getChildren().clear()
			Executions.createComponents("pledgeCrudPanel.zul", electionCenter, null)
		}else{
			ComposerHelper.permissionDeniedBox()
		}
	 }


	 def onClick_municipalitiesButton(){
	 	if(SpringSecurityUtils.ifAllGranted('ROLE_ADMIN')){
	 		electionCenter.getChildren().clear()
			Executions.createComponents("municipalityCrudPanel.zul", electionCenter, null)
		}else{
			ComposerHelper.permissionDeniedBox()
		}
	 }

}
