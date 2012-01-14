package bz.voter.management

import org.zkoss.zkgrails.*
import org.zkoss.zul.*
import org.zkoss.zk.ui.*

import bz.voter.management.zk.ComposerHelper

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

class ElectionCrudPanelComposer extends GrailsComposer {

	def addElectionButton
	def cancelElectionButton
	def saveElectionButton

	def electionFormPanel

	def electionIdLabel

	def yearTextbox

	def electionDatebox

	def electionTypeListbox

	def electionsListRows

	def errorMessages
	def messageSource

	def election

	def center

	def springSecurityService
	def voterElectionService

    def afterCompose = { window ->
	 	if(springSecurityService.isLoggedIn()){
			showElectionsList()
		}else{
			execution.sendRedirect('/login')
		}
    }


	 def onClick_addElectionButton(){
	 	if(SpringSecurityUtils.ifAnyGranted('ROLE_ADMIN,ROLE_OFFICE_STATION')){
			showElectionFormGrid(null)
		}else{
			ComposerHelper.permissionDeniedBox()
		}
	 }


	 def onClick_cancelElectionButton(){
	 	addElectionButton.setVisible(true)
		hideElectionFormGrid()
	 }


	 def onClick_saveElectionButton(){
	 if(SpringSecurityUtils.ifAllGranted('ROLE_ADMIN')){

	 	def electionInstance
		if(electionIdLabel.getValue()){
			electionInstance = Election.get(electionIdLabel.getValue())
		}else{
	 		electionInstance = new Election()
		}

		electionInstance.year = yearTextbox.getValue()?.trim()?.toInteger()
		electionInstance.electionDate = electionDatebox.getValue()
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
			voterElectionService.addAllVoters(electionInstance)
			Messagebox.show("Election Saved!", "Election Message", Messagebox.OK, Messagebox.INFORMATION)
			hideElectionFormGrid()
			showElectionsList()
		}

		}else{
			ComposerHelper.permissionDeniedBox()
		}

	 }


	 def showElectionsList(){
		electionsListRows.getChildren().clear()
		if(!addElectionButton.isVisible()){
			addElectionButton.setVisible(true)
		}

		electionsListRows.append{
			for(_election in Election.list([sort:'year'])){
				def electionInstance = _election
				row{
					label(value: _election.year)
					label(value: _election.electionType)
					label(value: _election.electionDate?.format('dd-MMM-yyyy'))
					button(label: 'Edit', onClick:{
						showElectionFormGrid(electionInstance)
					})
					button(label: 'Poll Station', onClick:{
						if(SpringSecurityUtils.ifAnyGranted("ROLE_ADMIN, ROLE_POLL_STATION")){
							center.getChildren().clear()
							Executions.createComponents("voterElection.zul", center, 
								[id: electionInstance.id])
						}else{
							ComposerHelper.permissionDeniedBox()
						}
					})
					button(label: 'Office Management', onClick:{
						if(SpringSecurityUtils.ifAnyGranted("ROLE_ADMIN, ROLE_OFFICE_STATION")){

							center.getChildren().clear()
							Executions.createComponents("electionOfficeMain.zul",center,
								[id: electionInstance.id])
						}else{
							ComposerHelper.permissionDeniedBox()
						}

					})
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
			electionDatebox.setValue(electionInstance?.electionDate)
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
