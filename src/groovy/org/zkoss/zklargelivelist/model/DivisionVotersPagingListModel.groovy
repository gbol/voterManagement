package org.zkoss.zklargelivelist.model;

import java.util.List;
import bz.voter.management.Voter
import bz.voter.management.VoterService
import bz.voter.management.Division

//import org.zkoss.zklargelivelist.database.DatabaseInformation;
//import org.zkoss.zklargelivelist.database.User;

public class DivisionVotersPagingListModel extends AbstractDivisionVotersPagingListModel<Voter> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4370353438941246687L;

	VoterService voterService //= new VoterService()
	Division division
	String searchString

	public DivisionVotersPagingListModel(){
	}

	
	public DivisionVotersPagingListModel(Division division, int startPageNumber, int pageSize) {
		super(division, startPageNumber, pageSize)
		this.division = division
	}

	
	public DivisionVotersPagingListModel(String search,Division division, int startPageNumber, int pageSize) {
		super(search,division, startPageNumber, pageSize)
		this.division = division
		this.searchString = search
	}


	@Override
	protected List<Voter> getPageData(Division division, int itemStartNumber, int pageSize) {
		voterService = new VoterService()
		return voterService.listByDivision(division, itemStartNumber, pageSize)
	}


	@Override
	protected List<Voter> getPageData(String search, Division division, int itemStartNumber, int pageSize) {
		voterService = new VoterService()
		return voterService.searchByDivision(search,division, itemStartNumber, pageSize)
	}


	@Override
	public int getTotalSize() {
		def totalSize = searchString ? voterService.countByDivisionAndSearch(division,searchString) : voterService.countByDivision(division)
		return totalSize
	}


}
