package bz.voter.management

import org.zkoss.zkgrails.*
import org.zkoss.zk.ui.*
import org.zkoss.zk.ui.event.ForwardEvent
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.event.EventQueue
import org.zkoss.zk.ui.event.EventQueues
import org.zkoss.zk.ui.event.EventListener
import org.zkoss.zul.Messagebox
import org.zkoss.zul.Grid
import org.zkoss.zul.Paging
import org.zkoss.zul.ListModelList
import org.zkoss.zul.Window
import org.zkoss.zul.Paging
import org.zkoss.zul.RowRenderer
import org.zkoss.zul.Label
import org.zkoss.zul.event.PagingEvent

import org.zkoss.zklargelivelist.model.DivisionVotersPagingListModel

import bz.voter.management.zk.ComposerHelper
import bz.voter.management.zk.VoterRenderer
import bz.voter.management.utils.FilterType

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

class VoterComposer extends GrailsComposer {

	def addVoterButton
	def showAllVotersBtn
	def searchVoterButton
	def printButton
    def filterVotersBtn

	def voterSearchTextbox

	def center
	
	def votersListRows

	def division

	Grid votersGrid
	Paging voterPaging

    def votersDiv

	DivisionVotersPagingListModel voterModel = null

	def springSecurityService
	def voterService

    def voterListFacade

    private Division division

	private final int _pageSize = 10
	private int _startPageNumber = 0
	private int _totalSize = 0
	private boolean _needsTotalSizeUpdate = true

    private EventQueue queue

    def afterCompose = { window ->
	 	if(springSecurityService.isLoggedIn()){
            division = voterListFacade.getSystemDivision()
			votersGrid.setRowRenderer(new VoterRenderer())
            showVoters()
            queue = EventQueues.lookup('filterVoters', EventQueues.DESKTOP,true)
            queue.subscribe(new EventListener(){
                public void onEvent(Event evt){
                    def data = evt.getData()
                    filter(data.filterType,data.filterValue)
                }
                
            })
		}else{
			execution.sendRedirect('/login')
		}
    }


    def filter(FilterType filterType, Object value){
        switch(filterType){
            case filterType.AFFILIATION:
                def affiliation = (Affiliation)value
                refreshModel(filterType, affiliation,0)
                break
        }
    }


	 def onClick_searchVoterButton(){
	 	if(division){
	 		def searchText = voterSearchTextbox.getValue()?.trim()
			refreshModel(searchText,0)
		}else{
			Messagebox.show("You must select a division!",
				"Message", Messagebox.OK, Messagebox.EXCLAMATION)
		}

	 }


    def onClick_showAllVotersBtn(){
	 	if(SpringSecurityUtils.ifAnyGranted('ROLE_ADMIN, ROLE_OFFICE_STATION')){
            voterSearchTextbox.setValue("")
            showVoters()
        }else{
			ComposerHelper.permissionDeniedBox()
        }
    }


    def onClick_filterVotersBtn(){
	 	if(SpringSecurityUtils.ifAnyGranted('ROLE_ADMIN, ROLE_OFFICE_STATION')){
            Executions.createComponents("/bz/voter/management/filter/voter.zul",
                votersDiv.getParent().getParent().getParent().getFirstChild().getFirstChild()
                ,null).doModal()
        }else{
            ComposerHelper.permissionDeniedBox()
        }
    }


	 def showVoters(){
        def divisionInstance = division
		if(divisionInstance){
		    def divisionVoters = voterService.countByDivision(divisionInstance)
			voterSearchTextbox.setValue("")
			if(divisionVoters>0){
				refreshModel(_startPageNumber)
			}else{
				votersListRows.getChildren().clear()
				Messagebox.show("No voters exist in ${divisionInstance.name}!",
					"Division Message", Messagebox.OK,
					Messagebox.INFORMATION)
			}
		}else{
			Messagebox.show("Error: division does not exist!", "Error",
				Messagebox.OK, Messagebox.ERROR)
		}
	 }


	 def onClick_addVoterButton(){
		final Window win = (Window)Executions.createComponents("voterNewForm.zul",votersDiv ,null) //.doModal()
		win.doModal()
	 }


	 def onClick_printButton(){
	 	if(SpringSecurityUtils.ifAnyGranted('ROLE_ADMIN, ROLE_PRINT_VOTERS')){
	 	    if(division){
	 		    Executions.sendRedirect("/person/pdf?division=${division.id}")
		    }else{
			    Messagebox.show("You must select a division!", "Message", Messagebox.OK,
				    Messagebox.EXCLAMATION)
		    }
        }else{
            ComposerHelper.permissionDeniedBox()
        }
	 }


	public void onPaging_voterPaging(ForwardEvent event){
		final PagingEvent pagingEvent = (PagingEvent) event.getOrigin()
		_startPageNumber = pagingEvent.getActivePage()
		if(voterSearchTextbox.getValue().isAllWhitespace()){
			refreshModel(_startPageNumber)
		}else{
			refreshModel(voterSearchTextbox.getValue().trim(),_startPageNumber)
		}
	}


    private void refreshModel(int activePage){
  		voterPaging.setPageSize(_pageSize)
		voterModel = new DivisionVotersPagingListModel(division,activePage, _pageSize)
		voterPaging.setTotalSize(voterModel.getTotalSize())

		if(_needsTotalSizeUpdate || activePage == 0){
			_totalSize = voterModel.getTotalSize()
			_needsTotalSizeUpdate = false
			voterPaging.setDetailed(true)
		}

		if(_totalSize < _pageSize){
			voterPaging.setDetailed(false)
		}


		votersGrid.setModel(voterModel)
    }


    private void refreshModel(String search, int activePage){
  		voterPaging.setPageSize(_pageSize)
		voterModel = new DivisionVotersPagingListModel(search,division,activePage, _pageSize)
		voterPaging.setTotalSize(voterModel.getTotalSize())

		if(_needsTotalSizeUpdate || activePage == 0){
			_totalSize = voterModel.getTotalSize()
			voterPaging.setDetailed(true)
			_needsTotalSizeUpdate = false
		}

		if(_totalSize < _pageSize){
			voterPaging.setDetailed(false)
		}

		votersGrid.setModel(voterModel)
  }


  private void refreshModel(FilterType filterType, Object filterValue, int activePage){
    voterPaging.setPageSize(_pageSize)
    voterModel = new DivisionVotersPagingListModel(filterType,(Object)filterValue, division,activePage, _pageSize)
    voterPaging.setTotalSize(voterModel.getTotalSize())

    if(_needsTotalSizeUpdate || activePage == 0){
        _totalSize = voterModel.getTotalSize()
        voterPaging.setDetailed(true)
        _needsTotalSizeUpdate = false
    }

    if(_totalSize < _pageSize){
        voterPaging.setDetailed(false)
    }

    votersGrid.setModel(voterModel)
  }

}


