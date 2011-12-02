package bz.voter.management

import org.zkoss.zkgrails.*
import org.zkoss.zk.ui.*
import org.zkoss.zul.*

import bz.voter.management.zk.ComposerHelper

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

class VoterElectionComposer extends GrailsComposer {

	def springSecurityService
	def voterElectionService


	def electionIdLabel

	def searchTextbox

	def filterBtn

	def divisionListbox

	def divisionInstance

	def votersListRows

	def election 

	ListModelList divisionModel

    def afterCompose = { window ->
	 	if(springSecurityService.isLoggedIn()){
			election = Election.get(Executions.getCurrent().getArg().id)
			divisionModel = new ListModelList(Division.list([sort:'name']))
			divisionListbox.setModel(divisionModel)
		}else{
			execution.sendRedirect('/login')
		}
    }


	 def onClick_filterBtn(){
	 	divisionInstance = divisionListbox.getSelectedItem()?.getValue()
		if(divisionInstance){
			def votersList = VoterElection.getAllVotersByElectionAndDivision(election,divisionInstance)
			showVoters(election,votersList)
		}
	 }


	def onChange_searchTextbox(){
		def searchText = searchTextbox.getValue()?.trim()
		def votersList = voterElectionService.search(searchText, election,divisionInstance)

		showVoters(election, votersList)
	}


	 def showVoters(Election election, def voterList){
	 	votersListRows.getChildren().clear()

		for(_voterElection in voterList){
			def voterElectionInstance = _voterElection
			def _voter = Voter.load(_voterElection.voter.id)
			def _election = Election.load(_voterElection.election.id)
			def voted = _voterElection.voted ? true : false
			votersListRows.append{
				def backgroundColor = voted ? "red" : "white"
				row(style: "background-color: ${backgroundColor}"){
					label(value: _voterElection.voter.registrationNumber)
					label(value: _voterElection.voter.registrationDate.format("dd-MMM-yyyy"))
					label(value: _voterElection.voter.person.lastName)
					label(value: _voterElection.voter.person.firstName)
					label(value: _voterElection.voter.person.address.houseNumber)
					label(value: _voterElection.voter.person.address.street)
					label(value: _voterElection.voter.person.sex.code)
					label(value: _voterElection.voter.person.age)
					label(value: _voterElection.voter.person.birthDate.format("dd-MMM-yyyy"))
					label(value: _voterElection.voter.pollStation.pollNumber)
					checkbox(checked: voted, onCheck: {event->
						if(voterElectionInstance.voted){
							voterElectionInstance.voted = false
							voterElectionInstance.voteTime = null
						}else{
							voterElectionInstance.voted = true
							voterElectionInstance.voteTime = new Date()
						}
					})
					button(label: 'Save', onClick:{evt->
						if(SpringSecurityUtils.ifAnyGranted('ROLE_ADMIN, ROLE_POLL_STATION')){
							voterElectionInstance.save(flush:true)
							Messagebox.show("Saved Successfuly!", "Voter", 
								Messagebox.OK, Messagebox.INFORMATION)
							if(voterElectionInstance.voted){
								evt.getTarget().getParent().setStyle("background-color:red")
							}else{
								evt.getTarget().getParent().setStyle("background-color: white")
							}
						}else{
							ComposerHelper.permissionDeniedBox()
						}


					})
					button(label: 'Details', onClick:{
						final Window win = (Window) Executions.createComponents("voterGeneralInformation.zul", 
							null, [id: voterElectionInstance.voter.id, electionId: voterElectionInstance.election.id])
						win.doModal()
						win.setPosition("top,center")
					})

					style(content: "background-color: red;")
					
				}
			}
		}
		
	 }
}
