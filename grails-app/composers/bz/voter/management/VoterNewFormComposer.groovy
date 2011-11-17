package bz.voter.management

import org.zkoss.zkgrails.*
import org.zkoss.zk.ui.*
import org.zkoss.zul.*

import bz.voter.management.zk.ComposerHelper

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

import java.lang.reflect.Field

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
	def affiliationListbox

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
	def Voter voter
	def Address address
	def municipality


	def messageSource
	def errorMessages

	def voterService
	def springSecurityService
	
   def afterCompose = { window ->
		if(springSecurityService.isLoggedIn()){

		  //if(voterIdLabel.getValue()){
		  if(Executions.getCurrent().getArg().id){
		  		voterNewFormPanel.setTitle("Edit Voter")
				voterIdLabel.setValue(Executions.getCurrent().getArg().id.toString())
				//person = Person.get(voterIdLabel.getValue())
				voter = Voter.get(Executions.getCurrent().getArg().id)
				person = voter.person
				address = person.address
				municipality = address.municipality

				firstNameTextbox.setValue(person.firstName)
				middleNameTextbox.setValue(person.middleName)
				lastNameTextbox.setValue(person.lastName)
				registrationNumberTextbox.setValue("${voter.registrationNumber}")
				homePhoneTextbox.setValue(person.homePhone)
				cellPhoneTextbox.setValue(person.cellPhone)
				workPhoneTextbox.setValue(person.workPhone)
				houseNumberTextbox.setValue(person.address.houseNumber)
				streetTextbox.setValue(person.address.street)
				commentsTextbox.setValue(person.comments)
				birthDateBox.setValue(person.birthDate)
				registrationDateBox.setValue(voter.registrationDate)

		  }else{
		  		voter = new Voter()
		  		person = new Person()
				address = new Address()
				municipality = new Municipality()
		  		voterNewFormPanel.setTitle("Create New Voter")
			}
			

		  		ComposerHelper.initializeListbox(sexListbox, person, 'sex')
		  		ComposerHelper.initializeListbox(identificationTypeListbox, voter, 'identificationType')
		  		ComposerHelper.initializeListbox(pollStationListbox,voter,'pollStation')
		  		ComposerHelper.initializeListbox(districtListbox,municipality,'district')
		  		ComposerHelper.initializeListbox(municipalityListbox,address,'municipality')
		  		ComposerHelper.initializeListbox(pledgeListbox,voter,'pledge')
		  		ComposerHelper.initializeListbox(affiliationListbox,voter,'affiliation')
	 	}else{
			execution.sendRedirect('/login')
		}

	 }


	 def onClick_cancelButton(){
	 	center.getChildren().clear()
		Executions.createComponents("voter.zul", center,null)
	 }


	 def onClick_saveButton(){
	 	if(SpringSecurityUtils.ifAllGranted('ROLE_ADMIN')){

	 	errorMessages.getChildren().clear()
	 	def personInstance
		def addressInstance
		def voterInstance

		voterInstance = (voterIdLabel.getValue()) ?  Voter.get(voterIdLabel.getValue()) : (new Voter())
		personInstance = (voterIdLabel.getValue()) ? voterInstance.person : (new Person())
		addressInstance = (voterIdLabel.getValue()) ? personInstance.address : (new Address())

		def params = [
			id : voterIdLabel.getValue(),
			firstName : firstNameTextbox.getValue()?.trim()?.capitalize(),
			middleName : middleNameTextbox.getValue()?.trim()?.capitalize(),
			lastName : lastNameTextbox.getValue()?.trim()?.capitalize(),
			homePhone : homePhoneTextbox.getValue()?.trim(),
			workPhone : workPhoneTextbox.getValue()?.trim(),
			cellPhone : cellPhoneTextbox.getValue()?.trim(),
			comments : commentsTextbox.getValue()?.trim(),
			registrationNumber: registrationNumberTextbox.getValue()?.trim(),
			birthDate : birthDateBox.getValue(),
			registrationDate : registrationDateBox.getValue(),
			identificationType : identificationTypeListbox.getSelectedItem()?.getValue(),
			sex : sexListbox.getSelectedItem()?.getValue(),
			pollStation : pollStationListbox.getSelectedItem()?.getValue(),
			pledge : pledgeListbox.getSelectedItem()?.getValue(),
			affiliation: affiliationListbox.getSelectedItem()?.getValue(),
			houseNumber : houseNumberTextbox.getValue()?.trim(),
			street : streetTextbox.getValue()?.trim(),
			municipality: municipalityListbox.getSelectedItem()?.getValue()

		]

		voterInstance = voterService.saveVoter(params)

		if(voterInstance.retrieveErrors()){
			errorMessages.append{
				label(value: voterInstance.retrieveErrors(),class:'errors')
			}
		}else{
			Messagebox.show("Voter Saved!", "Voter Message!", Messagebox.OK,
				Messagebox.INFORMATION)
			closeWindow()
		}

		}else{
			ComposerHelper.permissionDeniedBox()
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
