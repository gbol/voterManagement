package bz.voter.management

import org.zkoss.zkgrails.*
import org.zkoss.zk.ui.*

class MenuComposer extends GrailsComposer {

	def electionButton
	def userButton
	def voterButton
	def signOutButton
	def systemButton

	def center
	
    def afterCompose = { window ->
        // initialize components here
    }

	 def onClick_electionButton(){
	 	center.getChildren().clear()
		Executions.createComponents("election.zul", center, null)
	 	
	 }

	 def onClick_voterButton(){
	 	center.getChildren().clear()
		Executions.createComponents("voter.zul",center,null)
		
	 }

	
	def onClick_userButton(){
		center.getChildren().clear()
		Executions.createComponents("user.zul",center,null)
	}


	 def onClick_signOutButton(){
	 	execution.sendRedirect('/logout')
	 }

	 def onClick_systemButton(){
	 	center.getChildren().clear()
		Executions.createComponents("uploadVotersFile.zul",center,null)
	 }
}
