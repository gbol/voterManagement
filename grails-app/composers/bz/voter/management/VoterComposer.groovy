package bz.voter.management

import org.zkoss.zkgrails.*
import org.zkoss.zk.ui.*

class VoterComposer extends GrailsComposer {

	def addVoterButton
	def center
	
	def votersListRows

    def afterCompose = { window ->
	 	showVoters()
    }


	 def onClick_addVoterButton(){
	 	center.getChildren().clear()
		Executions.createComponents("voterNewForm.zul", center,null) //.doModal()
	 }


	 def showVoters(){
	 	votersListRows.getChildren().clear()
		for(voter in Person.list([sort:'firstName'])){
			def voterInstance = voter
			votersListRows.append{
				row{
					label(value: voter.firstName)
					label(value: voter.lastName)
					label(value: voter.age)
					label(value: voter.registrationNumber)
					label(value: voter.sex)
					label(value: voter.homePhone)
					label(value: voter.cellPhone)
					label(value: voter.pledge)
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
