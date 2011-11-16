package bz.voter.management

import org.zkoss.zkgrails.*
import org.zkoss.zul.*

import bz.voter.management.zk.ComposerHelper

class PollingStationComposer extends GrailsComposer {

	def addPollStationButton
	def pollStationSaveButton
	def pollStationCancelButton

	def pollNumberTextbox
	def divisionListbox

	def pollStationIdLabel

	def pollStationsListRows

	def pollStationFormPanel

	def errorMessages
	def messageSource


	private static NEW_TITLE = "New Poll Station"
	private static EDIT_TITLE = "Edit Poll Station"


    def afterCompose = { window ->
	 	showPollStationsList()
    }


	def onClick_addPollStationButton(){
		showPollStationForm(null)
	}


	def onClick_pollStationCancelButton(){
		hidePollStationForm()
	}


	def onClick_pollStationSaveButton(){
		def pollStationInstance

		pollStationInstance = (pollStationIdLabel.getValue()) ? (PollStation.get(pollStationIdLabel.getValue())) : (new PollStation())

		pollStationInstance.pollNumber = pollNumberTextbox.getValue()?.trim()?.toInteger()
		pollStationInstance.division = divisionListbox.getSelectedItem()?.getValue()

		pollStationInstance.validate()

		if(pollStationInstance.hasErrors()){
			errorMessages.append{
				for(error in pollStationInstance.errors.allErrors){
					log.error error
					label(value: messageSource.getMessage(error,null),class:'errors')
				}
			}
		}else{
			pollStationInstance.save(flush:true)
			Messagebox.show("Poll Station Saved!", "Poll Station Message", 
				Messagebox.OK, Messagebox.INFORMATION)
			hidePollStationForm()
			showPollStationsList()
		}
	}

	 def showPollStationsList(){
	 	pollStationsListRows.getChildren().clear()
		if(!addPollStationButton.isVisible()){
			addPollStationButton.setVisible(true)
		}

		pollStationsListRows.append{
			for(_pollStation in PollStation.list([sort:'pollNumber'])){
				def pollStationInstance = _pollStation
				row{
					label(value: _pollStation.pollNumber)
					label(value: _pollStation.division.name)
					button(label: 'Edit', onClick: {
						showPollStationForm(pollStationInstance)
						
					})
				}
			}
		}
	 }


	 def showPollStationForm(PollStation pollStationInstance){
	 	pollStationIdLabel.setValue("")
		errorMessages.getChildren().clear()
		addPollStationButton.setVisible(false)
		pollStationFormPanel.setVisible(true)
		pollNumberTextbox.setConstraint('no empty, /[0-9]*/:Only Numeric Values Allowed!')

		if(pollStationInstance){
			pollStationFormPanel.setTitle(EDIT_TITLE)
			pollNumberTextbox.setValue("${pollStationInstance.pollNumber}")
			pollStationIdLabel.setValue("${pollStationInstance.id}")
		}else{
			pollStationInstance = new PollStation()
			pollStationFormPanel.setTitle(NEW_TITLE)
		}

		ComposerHelper.initializeListbox(divisionListbox,pollStationInstance,'division')

	 }

	
	def hidePollStationForm(){
		errorMessages.getChildren().clear()
		pollStationFormPanel.setTitle("")
		pollNumberTextbox.setConstraint("")
		pollNumberTextbox.setValue("")
		divisionListbox.setSelectedIndex(-1)
		addPollStationButton.setVisible(true)
		pollStationFormPanel.setVisible(false)
	}


}
