package bz.voter.management

import org.zkoss.zkgrails.*
import org.zkoss.zk.ui.*
import org.zkoss.zul.*

import bz.voter.management.zk.ComposerHelper

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

class VoterElectionComposer extends GrailsComposer {

	def springSecurityService

	def votersListRows

    def afterCompose = { window ->
	 	if(springSecurityService.isLoggedIn()){
			def election = Election.get(Executions.getCurrent().getArg().id)
			showVoters(election)
		}else{
			execution.sendRedirect('/login')
		}
    }


	 def showVoters(Election election){
	 	votersListRows.getChildren().clear()
		for(_voterElection in VoterElection.findAllByElection(election)){
			def voterElectionInstance = _voterElection
			def voted = _voterElection.voted ? true : false
			votersListRows.append{
				row{
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
						}else{
							voterElectionInstance.voted = true
						}
					})
					button(label: 'Save', onClick:{
						if(SpringSecurityUtils.ifAllGranted('ROLE_POLL_STATION')){
							voterElectionInstance.save(flush:true)
							Messagebox.show("Saved Successfuly!", "Voter", 
								Messagebox.OK, Messagebox.INFORMATION)
						}else{
							ComposerHelper.permissionDeniedBox()
						}

					})
					button(label: 'Details', onClick:{
					})
				}
			}
		}
		
	 }
}
