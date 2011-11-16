package bz.voter.management

import org.zkoss.zkgrails.*

import org.zkoss.zkgrails.*
import org.zkoss.zul.*

import bz.voter.management.zk.ComposerHelper

class MunicipalityCrudPanelComposer extends GrailsComposer {

	def addMunicipalityButton
	def municipalitySaveButton
	def municipalityCancelButton

	def nameTextbox
	def districtListbox
	def municipalityIdLabel

	def municipalitiesListRows

	def municipalityFormPanel

	def errorMessages
	def messageSource


	private static EDIT_TITLE = "Edit Municipality"
	private static NEW_TITLE = "New Municipality"

    def afterCompose = { window ->
	 	showMunicipalitiesList()
    }


	def onClick_addMunicipalityButton(){
		showMunicipalityForm(null)
	}


	def onClick_municipalityCancelButton(){
		hideMunicipalityForm()
	}


	def onClick_municipalitySaveButton(){
		def municipalityInstance
		municipalityInstance = (municipalityIdLabel.getValue()) ? (Municipality.get(municipalityIdLabel.getValue())) : (new Municipality())

		municipalityInstance.name = nameTextbox.getValue()?.trim()?.capitalize()
		municipalityInstance.district = districtListbox.getSelectedItem()?.getValue()

		municipalityInstance.validate()

		if(municipalityInstance.hasErrors()){
			errorMessages.append{
				for(error in municipalityInstance.errors.allErrors){
					log.error error
					label(value:messageSource.getMessage(error, null), class:'errors')
				}
			}
		}else{
			municipalityInstance.save(flush:true)
			Messagebox.show("Municipality Saved!", 'Municipality Message!', 
				Messagebox.OK, Messagebox.INFORMATION)
			hideMunicipalityForm()
			showMunicipalitiesList()

		}

	}


	 def showMunicipalitiesList(){
	 	municipalitiesListRows.getChildren().clear()
		if(!addMunicipalityButton.isVisible()){
			addMunicipalityButton.setVisible(true)
		}

		municipalitiesListRows.append{
			for(_municipality in Municipality.list([sort:'name'])){
				def municipalityInstance = _municipality
				row{
					label(value: _municipality.name)
					label(value: _municipality.district.name)
					button(label: 'Edit', onClick: {
						showMunicipalityForm(municipalityInstance)
					})
				}
			}
		} //End of municpalityListRows.append

	 }

	
	def showMunicipalityForm(Municipality municipalityInstance){
		municipalityIdLabel.setValue('')
		errorMessages.getChildren().clear()
		addMunicipalityButton.setVisible(false)
		municipalityFormPanel.setVisible(true)
		nameTextbox.setConstraint('no empty')

		if(municipalityInstance){
			municipalityFormPanel.setTitle(EDIT_TITLE)
			nameTextbox.setValue("${municipalityInstance.name}")
		}else{
			municipalityInstance = new Municipality()
			municipalityFormPanel.setTitle(NEW_TITLE)
		}

		ComposerHelper.initializeListbox(districtListbox, municipalityInstance, 'district')
		
	}


	def hideMunicipalityForm(){
		errorMessages.getChildren().clear()
		municipalityFormPanel.setTitle("")
		nameTextbox.setConstraint('')
		nameTextbox.setValue('')
		districtListbox.setSelectedIndex(-1)
		addMunicipalityButton.setVisible(true)
		municipalityFormPanel.setVisible(false)
	}
	
}
