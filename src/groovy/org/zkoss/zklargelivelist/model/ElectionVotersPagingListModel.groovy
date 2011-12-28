package org.zkoss.zklargelivelist.model;

import java.util.List;
import bz.voter.management.Voter
import bz.voter.management.VoterElectionService
import bz.voter.management.Division
import bz.voter.management.Election


public class ElectionVotersPagingListModel extends AbstractElectionVotersPagingListModel<Voter> {

	def voterElectionService

	public ElectionVotersPaginListModel(){
	}


	public ElectionVotersPagingListModel(Election election, Division division, int startPageNumber, int pageSize){
		super(election, division, startPageNumber, pageSize)
	}


	public ElectionVotersPagingListModel(String searchString, Election election, Division division, int startPageNumber, int pageSize){
		super(searchString, election, division, startPageNumber, pageSize)
	}


	/**
	Gets a list of voters in a specified division whose first and/or last name match the search string.
	@arg search the search string that is used to search for the voters.
	@arg division the division you want voters from
	@arg itemStartNumber the offset
	@arg pageSize the size of the results returned
	@returns List<Voter> a list of voters
	**/
	@Override
	protected List<Voter> getPageData(String search, Election election,Division division, int itemStartNumber, int pageSize) {
		voterElectionService = new VoterElectionService()
		return voterElectionService.search(search, getElection(), getDivision(), itemStartNumber, pageSize)
	}


	@Override
	protected List<Voter> getPageData(Election election,Division division, int itemStartNumber, int pageSize) {
		voterElectionService = new VoterElectionService()
		return voterElectionService.listByElectionAndDivision(getElection(),getDivision(), itemStartNumber, pageSize )
	}


	/**
	Returns total size of voters returned by a query.
	**/
	@Override
	public int getTotalSize() {

		def search = getSearchString()
		search = search ? search : ""
		def totalSize = voterElectionService.countVoters(search, getElection(), getDivision())
		return totalSize

	}

	

}

