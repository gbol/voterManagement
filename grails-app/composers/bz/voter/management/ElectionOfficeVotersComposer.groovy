package bz.voter.management

import org.zkoss.zkgrails.*
import org.zkoss.zk.ui.*
import org.zkoss.zk.ui.event.ForwardEvent
import org.zkoss.zul.Messagebox
import org.zkoss.zul.Grid
import org.zkoss.zul.Paging
import org.zkoss.zul.ListModelList
import org.zkoss.zul.Window
import org.zkoss.zul.Paging
import org.zkoss.zul.RowRenderer
import org.zkoss.zul.Label
import org.zkoss.zul.event.PagingEvent

import org.zkoss.zklargelivelist.model.ElectionVotersPagingListModel

import bz.voter.management.zk.ComposerHelper
import bz.voter.management.zk.VoterRenderer

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils


class ElectionOfficeVotersComposer extends GrailsComposer {

	def springSecurityService
	def voterElectionService
	def searchTextbox

	def votersListRows
	def election

	def filterBtn
	def searchVoterButton

	def divisionInstance

	def divisionListbox

	ListModelList divisionModel

    def voterListFacade

    def afterCompose = { window ->
	 	if(SpringSecurityUtils.ifAnyGranted('ROLE_ADMIN,ROLE_OFFICE_STATION')){
            divisionInstance = voterListFacade.getSystemDivision()
			election = Election.get(Executions.getCurrent().getArg().id)
			divisionModel = new ListModelList(Division.list([sort:'name']))
			divisionListbox.setModel(divisionModel)
		}else{
			ComposerHelper.permissionDeniedBox()
		}
    }



	def onSelect_divisionListbox(){
	 	divisionInstance = divisionListbox.getSelectedItem()?.getValue()
	}

	 def onClick_filterBtn(){
	 	//divisionInstance = divisionListbox.getSelectedItem()?.getValue()
		if(divisionInstance){
			def votersList = VoterElection.getAllVotersByElectionAndDivision(election,divisionInstance)
			showVoters(votersList)
		}
	 }


	def onClick_searchVoterButton(){
		if(divisionInstance){
			def searchText = searchTextbox.getValue()?.trim()
			def votersList = voterElectionService.search(searchText, election,divisionInstance)
			showVoters(votersList)
		}else{
			Messagebox.show("You must select a division!",
				"Message", Messagebox.OK, Messagebox.EXCLAMATION)
		}
	}


	 def showVoters(def votersElection){
		votersListRows.getChildren().clear()
		for(_voterElection in votersElection){
			def voterElectionInstance = _voterElection
			def _voter = _voterElection.voter
			def _election = _voterElection.election
			def pickupTimeButtonLabel = voterElectionInstance.pickupTime ? 'Edit' : 'Add'
			votersListRows.append{
				row(style: "font-size: 0.5em; margin:0",height: "32px"){
					label(value: voterElectionInstance.voter.registrationNumber)
					label(value: voterElectionInstance.voter.person.lastName)
					label(value: voterElectionInstance.voter.person.firstName)
					label(value: voterElectionInstance.voter.person.age)
					label(value: voterElectionInstance.voter.person.sex.code)
					label(value: voterElectionInstance.voter.pollStation.pollNumber)
					label(value: voterElectionInstance.voter.affiliation)
					label(value: voterElectionInstance.pledge)
					label(value: voterElectionInstance.voted ? 'Yes' : 'No')
					textbox(value: voterElectionInstance.pickupTime, onChange:{e->
						voterElectionInstance.pickupTime = e.getTarget().getValue()
					})
					button(label: pickupTimeButtonLabel, onClick:{evt->
						if(SpringSecurityUtils.ifAnyGranted('ROLE_ADMIN, ROLE_OFFICE_STATION')){
						def HOUR = /10|11|12|[0-9]/
						def MINUTE = /[0-5][0-9]/
						def TIME = /($HOUR):($MINUTE)/
						def valid = (voterElectionInstance.pickupTime =~ TIME).matches()
						if(valid){
							voterElectionInstance.save(flush:true)
							evt.getTarget().setLabel("Edit")
							Messagebox.show("Saved Pickup Time Successfully!", "Pickup Time Message",
								Messagebox.OK, Messagebox.INFORMATION)
						}else{
							Messagebox.show("Wrong Time Format", "Time Format Message", Messagebox.OK,
								Messagebox.ERROR)
						}
						}else{
							ComposerHelper.permissionDeniedBox()
						}
						
					})
					button(label: 'Details', onClick:{
						final Window win = (Window) Executions.createComponents("voterGeneralInformation.zul", 
							null, [id: _voter.id, electionId: _election.id])
						win.doModal()
						win.setPosition("top,center")
					})
				}
			}//End of votersListRows.append
		}

	 }
}
