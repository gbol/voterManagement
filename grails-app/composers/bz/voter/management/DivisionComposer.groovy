package bz.voter.management

import org.zkoss.zkgrails.*
import org.zkoss.zul.*

class DivisionComposer extends GrailsComposer {

	def addDivisionButton
	def divisionCancelButton
	def divisionSaveButton

	def divisionFormPanel

	def divisionIdLabel

	def divisionsListRows

	def divisionNameTextbox

	def messageSource
	def errorMessages

    def afterCompose = { window ->
	 	showDivisionsList()

    }


	def onClick_addDivisionButton(){
		showDivisionForm(null)
	}

	def onClick_divisionCancelButton(){
		hideDivisionForm()
	}


	def onClick_divisionSaveButton(){
		def divisionInstance
		if(divisionIdLabel.getValue()){
			divisionInstance = Division.get(divisionIdLabel.getValue())
		}else{
			divisionInstance = new Division()
		}

		divisionInstance.name = divisionNameTextbox.getValue()?.trim()?.capitalize()
		divisionInstance.validate()

		if(divisionInstance.hasErrors()){
			errorMessages.append{
				for(error in divisionInstance.errors.allErrors){
					log.error error
					label(value: messageSource.getMessage(error,null),class:'errors')
				}
			}
		}else{
			divisionInstance.save(flush:true)
			Messagebox.show("Division Saved!", "Division Message", Messagebox.OK,
				Messagebox.INFORMATION)
			hideDivisionForm()
			showDivisionsList()
		}
	}


	 def showDivisionForm(Division divisionInstance){
	 	divisionIdLabel.setValue("")
		errorMessages.getChildren().clear()
		addDivisionButton.setVisible(false)
		divisionFormPanel.setVisible(true)
		divisionNameTextbox.setConstraint('no empty')

		if(divisionInstance){
			divisionFormPanel.setTitle("Edit Division")
			divisionNameTextbox.setValue("${divisionInstance.name}")
			divisionIdLabel.setValue("${divisionInstance.id}")
		}else{
			divisionFormPanel.setTitle("Add New Division")
		}

	 }


	 def hideDivisionForm(){
	 	errorMessages.getChildren().clear()
		divisionFormPanel.setTitle("")
		divisionNameTextbox.setConstraint("")
		divisionNameTextbox.setValue("")
		addDivisionButton.setVisible(true)
		divisionFormPanel.setVisible(false)
	 }


	 def showDivisionsList(){
	 	divisionsListRows.getChildren().clear()
		if(!addDivisionButton.isVisible()){
			addDivisionButton.setVisible(true)
		}

		divisionsListRows.append{
			for(_division in Division.list([sort:'name'])){
				def divisionInstance = _division
				row{
					label(value: _division.name)
					button(label: 'Edit', onClick: {
						showDivisionForm(divisionInstance)
					})
				}
			}
		}
	 }

}
