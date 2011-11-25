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

	def votersListRows

	def election 

    def afterCompose = { window ->
	 	if(springSecurityService.isLoggedIn()){
			election = Election.get(Executions.getCurrent().getArg().id)
			def voterElectionList = VoterElection.findAllByElection(election)
			showVoters(election,voterElectionList)
		}else{
			execution.sendRedirect('/login')
		}
    }


	def onChange_searchTextbox(){
		def searchText = searchTextbox.getValue()?.trim()
		def votersList = voterElectionService.search(searchText, election)
		def results

		if(!searchText.isAllWhitespace()){
			results = votersList.collect{
				it[0]
			}
		}else{
			results = VoterElection.findAllByElection(election)
		}
		showVoters(election, results)
	}


	 def showVoters(Election election, def voterList){
	 	votersListRows.getChildren().clear()

		for(_voterElection in voterList){
			def voterElectionInstance = _voterElection
			def voted = _voterElection.voted ? true : false
			votersListRows.append{
				def backgroundColor = voted ? "red" : "white"
				row(style: "background-color: ${backgroundColor}"){
					label(value: _voterElection.voter.person.firstName)
					label(value: _voterElection.voter.person.lastName)
					label(value: _voterElection.voter.person.age)
					label(value: _voterElection.voter.registrationNumber)
					label(value: _voterElection.voter.person.sex)
					label(value: _voterElection.voter.pledge)
					label(value: _voterElection.voter.affiliation)
					checkbox(checked: voted, onCheck: {event->
						if(voterElectionInstance.voted){
							voterElectionInstance.voted = false
							voterElectionInstance.voteTime = new Date()
						}else{
							voterElectionInstance.voted = true
							voterElectionInstance.voteTime = null
						}
					})
					button(label: 'Save', onClick:{evt->
						if(SpringSecurityUtils.ifAnyGranted('ROLE_ADMIN, ROLE_POLL_STATION')){
							voterElectionInstance.save(flush:true)
							Messagebox.show("Saved Successfuly!", "Voter", 
								Messagebox.OK, Messagebox.INFORMATION)
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
