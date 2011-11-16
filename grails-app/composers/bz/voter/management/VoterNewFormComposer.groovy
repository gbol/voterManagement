package bz.voter.management

import org.zkoss.zkgrails.*
import org.zkoss.zk.ui.*
import org.zkoss.zul.*

import bz.voter.management.zk.ComposerHelper

import java.lang.reflect.Field

import grails.plugins.springsecurity.Secured

class VoterNewFormComposer extends GrailsComposer {

	def cancelButton
	def saveButton
	def voterIdLabel
	def voterNewFormPanel

	def sexListbox
	def pollStationListbox
	def identificationTypeListbox
	def districtListbox
	def municipalityListbox
	def pledgeListbox

	def firstNameTextbox
	def middleNameTextbox
	def lastNameTextbox
	def registrationNumberTextbox
	def homePhoneTextbox
	def cellPhoneTextbox
	def workPhoneTextbox
	def houseNumberTextbox
	def streetTextbox
	def commentsTextbox

	def birthDateBox
	def registrationDateBox


	def center

	def Person person
	def Address address
	def municipality


	def messageSource
	def errorMessages
	
	@Secured(['ROLE_ADMIN'])
    def afterCompose = { window ->

		  //if(voterIdLabel.getValue()){
		  if(Executions.getCurrent().getArg().id){
		  		voterNewFormPanel.setTitle("Edit Voter")
				voterIdLabel.setValue(Executions.getCurrent().getArg().id.toString())
				//person = Person.get(voterIdLabel.getValue())
				person = Person.get(Executions.getCurrent().getArg().id)
				address = person.address
				municipality = address.municipality

				firstNameTextbox.setValue(person.firstName)
				middleNameTextbox.setValue(person.middleName)
				lastNameTextbox.setValue(person.lastName)
				registrationNumberTextbox.setValue("${person.registrationNumber}")
				homePhoneTextbox.setValue(person.homePhone)
				cellPhoneTextbox.setValue(person.cellPhone)
				workPhoneTextbox.setValue(person.workPhone)
				houseNumberTextbox.setValue(person.address.houseNumber)
				streetTextbox.setValue(person.address.street)
				commentsTextbox.setValue(person.comments)
				birthDateBox.setValue(person.birthDate)
				registrationDateBox.setValue(person.registrationDate)

		  }else{
		  		person = new Person()
				address = new Address()
				municipality = new Municipality()
		  		voterNewFormPanel.setTitle("Create New Voter")
			}
			

		  		ComposerHelper.initializeListbox(sexListbox, person, 'sex')
		  		ComposerHelper.initializeListbox(identificationTypeListbox, person, 'identificationType')
		  		ComposerHelper.initializeListbox(pollStationListbox,person,'pollStation')
		  		ComposerHelper.initializeListbox(districtListbox,municipality,'district')
		  		ComposerHelper.initializeListbox(municipalityListbox,address,'municipality')
		  		ComposerHelper.initializeListbox(pledgeListbox,person,'pledge')
	 }


	 def onClick_cancelButton(){
	 	center.getChildren().clear()
		Executions.createComponents("voter.zul", center,null)
	 }


	 def onClick_saveButton(){
	 	errorMessages.getChildren().clear()
	 	def personInstance
		def addressInstance
		personInstance = (voterIdLabel.getValue()) ?  Person.get(voterIdLabel.getValue()) : (new Person())
		addressInstance = (voterIdLabel.getValue()) ? personInstance.address : (new Address())


		Person.withTransaction{status->

			addressInstance.houseNumber = houseNumberTextbox.getValue()?.trim()
			addressInstance.street = streetTextbox.getValue()?.trim()
			addressInstance.municipality = municipalityListbox.getSelectedItem()?.getValue()

			personInstance.firstName = firstNameTextbox.getValue()?.trim()?.capitalize()
			personInstance.middleName = middleNameTextbox.getValue()?.trim()?.capitalize()
			personInstance.lastName = lastNameTextbox.getValue()?.trim()?.capitalize()
			personInstance.homePhone = homePhoneTextbox.getValue()?.trim()
			personInstance.cellPhone = cellPhoneTextbox.getValue()?.trim()
			personInstance.workPhone = workPhoneTextbox.getValue()?.trim()
			personInstance.comments = commentsTextbox.getValue()?.trim()
			personInstance.registrationNumber = registrationNumberTextbox.getValue()?.trim()
			personInstance.birthDate = birthDateBox.getValue() 
			personInstance.registrationDate = registrationDateBox.getValue()
			personInstance.identificationType = identificationTypeListbox.getSelectedItem()?.getValue()
			personInstance.sex = sexListbox.getSelectedItem()?.getValue()
			personInstance.pollStation = pollStationListbox.getSelectedItem()?.getValue()
			personInstance.pledge = pledgeListbox.getSelectedItem()?.getValue()

		
			addressInstance.validate()
			if(addressInstance.hasErrors()){
				for(error in addressInstance.errors.allErrors){
					log.error error
						errorMessages.append{
							label(value: messageSource.getMessage(error,null),class:'errors')
						}
				}
				status.setRollbackOnly()
			}else{
				addressInstance.save()
				personInstance.address = addressInstance

				personInstance.validate()
				if(personInstance.hasErrors()){
					for(error in personInstance.errors.allErrors){
						log.error error
						errorMessages.append{
							label(value: messageSource.getMessage(error,null),class:'errors')
						}
					}
					status.setRollbackOnly()
				}else{
					personInstance.save(flush:true)
					Messagebox.show("Voter Saved!", "Voter Message!", Messagebox.OK,
						Messagebox.INFORMATION)
					closeWindow()
				}
			}
		
		}

	 }


	 def onClose(){
	 	closeWindow()
	 }


	 def closeWindow(){
	 	center.getChildren().clear()
		Executions.createComponents("voter.zul",center,null)
	 }

}
