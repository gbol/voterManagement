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
import bz.voter.management.zk.OfficeStationVoterRenderer

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils


class ElectionOfficeVotersComposer extends GrailsComposer {

	def springSecurityService
	def voterElectionService

	def votersListRows
    Grid electionOfficeVotersGrid
    Paging voterPaging

	def searchTextbox

	def election

	def showAllVotersButton
	def searchVoterButton

	def divisionInstance

	def divisionListbox

	ListModelList divisionModel

    def voterListFacade

    ElectionVotersPagingListModel electionVoterModel = null 

	private final int _pageSize             = 10
	private int _startPageNumber            = 0
	private int _totalSize                  = 0
	private boolean _needsTotalSizeUpdate   = true

    def afterCompose = { window ->
	 	if(SpringSecurityUtils.ifAnyGranted('ROLE_ADMIN,ROLE_OFFICE_STATION')){
            divisionInstance = voterListFacade.getSystemDivision()
			election = Election.get(Executions.getCurrent().getArg().id)
            electionOfficeVotersGrid.setRowRenderer(new OfficeStationVoterRenderer())
		}else{
			ComposerHelper.permissionDeniedBox()
		}
    }


	 def onClick_showAllVotersButton(){
        if(SpringSecurityUtils.ifAnyGranted('ROLE_ADMIN,ROLE_OFFICE_STATION')){
		    if(divisionInstance){
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
            refreshModel(searchText,0)
		}else{
			Messagebox.show("You must select a division!",
				"Message", Messagebox.OK, Messagebox.EXCLAMATION)
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


    /**
    Refreshes the grid's model that displays  the voters.
    **/
    private void refreshModel(int activePage){
  		voterPaging.setPageSize(_pageSize)
		electionVoterModel = new ElectionVotersPagingListModel(election,divisionInstance,activePage, _pageSize)
		voterPaging.setTotalSize(electionVoterModel.getTotalSize())

		if(_needsTotalSizeUpdate || activePage == 0){
			_totalSize = electionVoterModel.getTotalSize()
			_needsTotalSizeUpdate = false
		}

		electionOfficeVotersGrid.setModel(electionVoterModel)
    }

    /**
    Refreshes the grid's model based on a search string.
    **/
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

		electionOfficeVotersGrid.setModel(electionVoterModel)
    }

}
