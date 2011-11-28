package bz.voter.management

import org.zkoss.zkgrails.*
import org.zkoss.zk.ui.*

class VoterComposer extends GrailsComposer {

	def addVoterButton
	def center
	
	def votersListRows

	def springSecurityService

    def afterCompose = { window ->
	 	if(springSecurityService.isLoggedIn()){
	 		showVoters()
		}else{
			execution.sendRedirect('/login')
		}
    }


	 def onClick_addVoterButton(){
	 	center.getChildren().clear()
		Executions.createComponents("voterNewForm.zul", center,null) //.doModal()
	 }


	 def showVoters(){
	 	votersListRows.getChildren().clear()
		for(voter in Voter.list()){
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
