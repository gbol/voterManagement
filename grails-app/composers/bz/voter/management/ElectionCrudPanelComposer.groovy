package bz.voter.management

import org.zkoss.zkgrails.*
import org.zkoss.zul.*

class ElectionCrudPanelComposer extends GrailsComposer {

	def addElectionButton
	def cancelElectionButton
	def saveElectionButton

	def electionFormPanel

	def electionIdLabel

	def yearTextbox

	def electionTypeListbox

	def electionsListRows


	def errorMessages
	def messageSource

	def election
    def afterCompose = { window ->
        // initialize components here
			showElectionsList()
    }


	 def onClick_addElectionButton(){
		showElectionFormGrid(null)
	 }


	 def onClick_cancelElectionButton(){
	 	addElectionButton.setVisible(true)
		hideElectionFormGrid()
	 }


	 def onClick_saveElectionButton(){
	 	def electionInstance
		if(electionIdLabel.getValue()){
			electionInstance = Election.get(electionIdLabel.getValue())
		}else{
	 		electionInstance = new Election()
		}

		electionInstance.year = yearTextbox.getValue()?.trim()?.toInteger()
		electionInstance.electionType = ElectionType.get(electionTypeListbox.getSelectedItem()?.getLastChild()?.getLabel()) ?: null
		electionInstance.validate()

		if(electionInstance.hasErrors()){
			errorMessages.append{
				for(error in electionInstance.errors.allErrors){
					log.error  error
					label(value: messageSource.getMessage(error,null),class:'errors')
				}
			}
		}else{
			electionInstance.save(flush:true)
			Messagebox.show("Election Saved!", "Election Message", Messagebox.OK, Messagebox.INFORMATION)
			hideElectionFormGrid()
			showElectionsList()
		}

	 }


	 def showElectionsList(){
		electionsListRows.getChildren().clear()
		//hideElectionFormGrid()
		if(!addElectionButton.isVisible()){
			addElectionButton.setVisible(true)
		}

		electionsListRows.append{
			for(_election in Election.list([sort:'year'])){
				def electionInstance = _election
				row{
					label(value: _election.year)
					label(value: _election.electionType)
					button(label: 'Edit', onClick:{
						showElectionFormGrid(electionInstance)
					})
					button(label: 'Manage')
				}
			}
		}
		
	 }


	def showElectionFormGrid(Election electionInstance){
		electionIdLabel.setValue("")
		errorMessages.getChildren().clear()
	 	addElectionButton.setVisible(false)
		electionFormPanel.setVisible(true)
		yearTextbox.setConstraint('no empty')


		electionTypeListbox.getChildren().clear()
		def electionTypes = ElectionType.list([sort:'name'])
		electionTypeListbox.append{
			int cnt = 0
			def selected = false
			for(electionType in electionTypes){
				listitem(value : electionType, selected:selected){
					listcell(label: electionType.name)
					listcell(label: electionType.id)
				}
				cnt++
			}
		}

		if(electionInstance){
			electionFormPanel.setTitle("Edit Election")
			yearTextbox.setValue("${electionInstance?.year}")
			electionIdLabel.setValue("${electionInstance.id}")
			for(item in electionTypeListbox.getItems()){
				if(item?.getValue()?.id ==  electionInstance.electionType.id){
					electionTypeListbox.setSelectedItem(item)
				}
			}
		}else{
			electionFormPanel.setTitle("Add New Election")
			electionTypeListbox.setSelectedIndex(-1)
		}

	}


	 def hideElectionFormGrid(){
	 	errorMessages.getChildren().clear()
		electionFormPanel.setTitle("")
		yearTextbox.setConstraint('')
		yearTextbox.setValue("")
		electionIdLabel.setVisible(false)
		electionTypeListbox.setSelectedIndex(-1)
		electionFormPanel.setVisible(false)
		addElectionButton.setVisible(true)
	 }
}
