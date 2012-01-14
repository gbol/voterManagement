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
import bz.voter.management.zk.PollStationVoterRenderer

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

class VoterElectionComposer extends GrailsComposer {

	def springSecurityService
	def voterElectionService


	def electionIdLabel

	def searchTextbox

	def filterBtn
	def searchVoterButton


	def divisionInstance

	def votersListRows
    Grid pollStationVotersGrid
    Paging voterPaging

    ElectionVotersPagingListModel electionVoterModel = null 

	def election 

    def voterListFacade

	private final int _pageSize = 10
	private int _startPageNumber = 0
	private int _totalSize = 0
	private boolean _needsTotalSizeUpdate = true

    def afterCompose = { window ->
	 	if(springSecurityService.isLoggedIn()){
			election = Election.get(Executions.getCurrent().getArg().id)
            divisionInstance = voterListFacade.getSystemDivision()
            pollStationVotersGrid.setRowRenderer(new PollStationVoterRenderer())
		}else{
			execution.sendRedirect('/login')
		}
    }


	 def onClick_filterBtn(){
        if(SpringSecurityUtils.ifAnyGranted('ROLE_ADMIN,ROLE_POLL_STATION')){
		    if(divisionInstance){
			    //def votersList = VoterElection.getAllVotersByElectionAndDivision(election,divisionInstance)
			    //showVoters(election,votersList)
                def numberOfVoters = voterElectionService.countByElectionAndDivision(election,divisionInstance)
                if(numberOfVoters>0){
                    refreshModel(_startPageNumber)
                }else{
                   votersListRows.getChildren().clear() 
                   Messagebox.show('No voters exist!', 'Poll Station Message', 
                        Messagebox.OK, Messagebox.INFORMATION)
                }
		    }
        }else{
            ComposerHelper.permissionDeniedBox()
        }
	 }


	def onClick_searchVoterButton(){
		if(divisionInstance){
			def searchText = searchTextbox.getValue()?.trim()
			def votersList = voterElectionService.search(searchText, election,divisionInstance)
			showVoters(election, votersList)
		}else{
			Messagebox.show('You must to select a division!', 'Message', 
				Messagebox.OK, Messagebox.EXCLAMATION)
		}
	}


	 def showVoters(Election election, def voterList){
	 	votersListRows.getChildren().clear()

		for(_voterElection in voterList){
			def voterElectionInstance = _voterElection
			def _voter = Voter.load(_voterElection.voter.id)
			def _election = Election.load(_voterElection.election.id)
			def voted = _voterElection.voted ? true : false
			votersListRows.append{
				def backgroundColor = voted ? "red" : "white"
				row(style: "background-color: ${backgroundColor}"){
					label(value: _voterElection.voter.registrationNumber)
					label(value: _voterElection.voter.registrationDate.format("dd-MMM-yyyy"))
					label(value: _voterElection.voter.person.lastName)
					label(value: _voterElection.voter.person.firstName)
					label(value: _voterElection.voter.person.address.houseNumber)
					label(value: _voterElection.voter.person.address.street)
					label(value: _voterElection.voter.person.sex.code)
					label(value: _voterElection.voter.person.age)
					label(value: _voterElection.voter.person.birthDate.format("dd-MMM-yyyy"))
					label(value: _voterElection.voter.pollStation.pollNumber)
					checkbox(checked: voted, onCheck: {event->
						if(voterElectionInstance.voted){
							voterElectionInstance.voted = false
							voterElectionInstance.voteTime = null
						}else{
							voterElectionInstance.voted = true
							voterElectionInstance.voteTime = new Date()
						}
					})
					button(label: 'Save', onClick:{evt->
						if(SpringSecurityUtils.ifAnyGranted('ROLE_ADMIN, ROLE_POLL_STATION')){
							voterElectionInstance.save(flush:true)
							Messagebox.show("Saved Successfuly!", "Voter", 
								Messagebox.OK, Messagebox.INFORMATION)
							if(voterElectionInstance.voted){
								evt.getTarget().getParent().setStyle("background-color:red")
							}else{
								evt.getTarget().getParent().setStyle("background-color: white")
							}
						}else{
							ComposerHelper.permissionDeniedBox()
						}


					})
					button(label: 'Details', onClick:{
						final Window win = (Window) Executions.createComponents("voterGeneralInformation.zul", 
							null, [id: voterElectionInstance.voter.id, electionId: voterElectionInstance.election.id])
						win.doModal()
						win.setPosition("top,center")
					})

					style(content: "background-color: red;")
					
				}
			}
		}
		
	 }

    /**
    This controls the paging event. Calls refreshModel() telling it to get the next batch of voters starting at
    _startPageNumber.
    **/
	public void onPaging_voterPaging(ForwardEvent event){
		final PagingEvent pagingEvent = (PagingEvent) event.getOrigin()
		_startPageNumber = pagingEvent.getActivePage()
		if(searchTextbox.getValue().isAllWhitespace()){
			refreshModel(_startPageNumber)
		}else{
			refreshModel(searchTextbox.getValue().trim(),_startPageNumber)
		}
	}


    private void refreshModel(int activePage){
  		voterPaging.setPageSize(_pageSize)
		electionVoterModel = new ElectionVotersPagingListModel(election,divisionInstance,activePage, _pageSize)
		voterPaging.setTotalSize(electionVoterModel.getTotalSize())

		if(_needsTotalSizeUpdate || activePage == 0){
			_totalSize = electionVoterModel.getTotalSize()
			_needsTotalSizeUpdate = false
		}

		pollStationVotersGrid.setModel(electionVoterModel)
    }


    private void refreshModel(String search, int activePage){
  		voterPaging.setPageSize(_pageSize)
		electionVoterModel = new ElectionVotersPagingListModel(search,election,divisionInstance,activePage, _pageSize)
		voterPaging.setTotalSize(electionVoterModel.getTotalSize())

		if(_needsTotalSizeUpdate || activePage == 0){
			_totalSize = electionVoterModel.getTotalSize()
			voterPaging.setDetailed(true)
			_needsTotalSizeUpdate = false
		}

		if(_totalSize < _pageSize){
			voterPaging.setDetailed(false)
		}

		pollStationVotersGrid.setModel(electionVoterModel)
    }

}
