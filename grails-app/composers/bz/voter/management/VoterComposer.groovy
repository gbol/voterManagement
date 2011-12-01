package bz.voter.management

import org.zkoss.zkgrails.*
import org.zkoss.zk.ui.*

import org.zkoss.zul.*

import bz.voter.management.zk.ComposerHelper

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

class VoterComposer extends GrailsComposer {

	def addVoterButton
	def filterBtn

	def center
	
	def votersListRows

	def voterDivisionListbox

	def division

	ListModelList divisionModel

	def springSecurityService
	def voterService

    def afterCompose = { window ->
	 	if(springSecurityService.isLoggedIn()){
			divisionModel = new ListModelList(Division.list([sort:'name']))
			voterDivisionListbox.setModel(divisionModel)
	 		//showVoters()
		}else{
			execution.sendRedirect('/login')
		}
    }


	 def onClick_filterBtn(){
	 	if(SpringSecurityUtils.ifAnyGranted('ROLE_ADMIN, ROLE_OFFICE_STATION')){
	 		def divisionInstance = voterDivisionListbox.getSelectedItem().getValue()
			if(divisionInstance){
				def divisionVoters = voterService.listByDivision(divisionInstance)
				if(divisionVoters.size()>0){
					showVoters(divisionVoters)
				}else{
					votersListRows.getChildren().clear()
					Messagebox.show("No voters exist in ${divisionInstance.name}!",
						"Division Message", Messagebox.OK,
						Messagebox.INFORMATION)
				}
			}else{
				Messagebox.show("Error: division does not exist!", "Error",
					Messagebox.OK, Messagebox.ERROR)
			}
		}else{
			ComposerHelper.permissionDeniedBox()
		}
	 }


	 def onClick_addVoterButton(){
	 	center.getChildren().clear()
		Executions.createComponents("voterNewForm.zul", center,null) //.doModal()
	 }


	 def showVoters(divisionVoters){
	 	votersListRows.getChildren().clear()
		//for(voter in Voter.list()){
		for(voter in divisionVoters){
			def voterInstance = voter
			votersListRows.append{
				row{
					label(value: voter.person.firstName)
					label(value: voter.person.lastName)
					label(value: voter.person.age)
					label(value: voter.registrationNumber)
					label(value: voter.person.sex)
					label(value: voter.person.homePhone)
					label(value: voter.person.cellPhone)
					label(value: voter.affiliation)
					button(label: 'Edit', onClick:{
						center.getChildren().clear()
						Executions.createComponents("voterNewForm.zul", center,
							[id:voterInstance.id]) 
					})
					button(label: 'Manage', onClick:{
					})
				}
			}
		}
		
	 }
}
