package org.zkoss.zklargelivelist.model;

import java.util.List;
import bz.voter.management.VoterElection
import bz.voter.management.VoterElectionService
import bz.voter.management.Division
import bz.voter.management.Election


public class ElectionVotersPagingListModel extends AbstractElectionVotersPagingListModel<VoterElection> {

	def voterElectionService
    def sessionFactory

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
	@arg itemStartNumber the offset
	@arg pageSize the size of the results returned
	@returns List<Map> a list of voters. We return a map because zk has issues maintaing
    a hibernate session across requests:
    <ul>
        <li>voterElection</li>
        <li>voter</li>
        <li>registrationDate</li>
        <li>lastName</li>
        <li>firstName</li>
        <li>houseNumber</li>
        <li>street</li>
        <li>sex</li>
        <li>age</li>
        <li>birthDate</li>
        <li>pollNumber</li>
        <li>voted</li>
        <li>pollStation</li>
        <li>affiliation</li>
        <li>pickupTime</li>
        <li>pledge</li>
    </ul>
	**/
	@Override
	protected List<Map> getPageData(String search, int itemStartNumber, int pageSize) {
		voterElectionService = new VoterElectionService()
        def votersMap = []
		for(_voterElection in voterElectionService.search(search, getElection(), getDivision(), itemStartNumber, pageSize)){
            def _addressInstance = _voterElection.voter.registrationAddress
            def _voter = _voterElection.voter
            def instance = [
                voterElection:      _voterElection,
                voter:              _voter,
                registrationDate:   _voter.registrationDate,
                registrationNumber: _voter.registrationNumber,
                lastName:           _voter.lastName,
                firstName:          _voter.firstName,
                houseNumber:        _addressInstance?.houseNumber,
                street:             _addressInstance?.street,
                sex:                _voter.sex,
                age:                _voter.age,
                birthDate:          _voter.birthDate,
                pollStation:        _voter.pollStation,
                pollNumber:         _voter.pollStation.pollNumber,
                voted:              _voterElection.voted,
                affiliation:        _voter.affiliation,
                pickupTime:         _voterElection.pickupTime,
                pledge:             _voterElection.pledge

            ]

            votersMap.push(instance)
        }
        return votersMap
	}


	/**
	Gets a list of voters in a specified division eligible to vote in an election.
	@arg itemStartNumber the offset
	@arg pageSize the size of the results returned
	@returns List<Map> a list of voters. We return a map because zk has issues maintaing
    a hibernate session across requests:
    <ul>
        <li>voterElection</li>
        <li>voter</li>
        <li>registrationDate</li>
        <li>lastName</li>
        <li>firstName</li>
        <li>houseNumber</li>
        <li>street</li>
        <li>sex</li>
        <li>age</li>
        <li>birthDate</li>
        <li>pollNumber</li>
        <li>voted</li>
        <li>pollStation</li>
        <li>affiliation</li>
        <li>pickupTime</li>
        <li>pledge</li>
    </ul>
	@Override
	**/
	protected List<Map> getPageData(int itemStartNumber, int pageSize) {
		voterElectionService = new VoterElectionService()
        def votersMap = []
		for(_voterElection in voterElectionService.listByElectionAndDivision(getElection(),getDivision(), itemStartNumber, pageSize )){
            def _addressInstance = _voterElection.voter.registrationAddress
            def _voter = _voterElection.voter
            def instance = [
                voterElection:      _voterElection,
                voter:              _voter,
                registrationDate:   _voter.registrationDate,
                registrationNumber:   _voter.registrationNumber,
                lastName:           _voter.lastName,
                firstName:          _voter.firstName,
                houseNumber:        _addressInstance?.houseNumber,
                street:             _addressInstance?.street,
                sex:                _voter.sex,
                age:                _voter.age,
                birthDate:          _voter.birthDate,
                pollStation:        _voter.pollStation,
                pollNumber:         _voter.pollStation.pollNumber,
                voted:              _voterElection.voted,
                affiliation:        _voterElection.voter.affiliation,
                pickupTime:         _voterElection.pickupTime,
                pledge:             _voterElection.pledge

            ]

            votersMap.push(instance)
        }

        return votersMap
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

