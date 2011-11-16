package bz.voter.management

import org.zkoss.zkgrails.*
import org.zkoss.zk.ui.*

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
	 	electionCenter.getChildren().clear()
		Executions.createComponents("electionCrudPanel.zul", electionCenter, null)
	 }

	 def onClick_divisionsButton(){
	 	electionCenter.getChildren().clear()
		Executions.createComponents("division.zul", electionCenter, null)
	 }

	 def onClick_pollStationsButton(){
	 	electionCenter.getChildren().clear()
		Executions.createComponents("pollingStation.zul", electionCenter, null)
	 }

	 def onClick_pledgesButton(){
	 	electionCenter.getChildren().clear()
		Executions.createComponents("pledgeCrudPanel.zul", electionCenter, null)
	 }


	 def onClick_municipalitiesButton(){
	 	electionCenter.getChildren().clear()
		Executions.createComponents("municipalityCrudPanel.zul", electionCenter, null)
	 }

}
