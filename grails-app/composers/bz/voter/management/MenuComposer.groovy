package bz.voter.management

import org.zkoss.zkgrails.*
import org.zkoss.zk.ui.*

class MenuComposer extends GrailsComposer {

	def electionButton
	def userButton
	def voterButton
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
}
