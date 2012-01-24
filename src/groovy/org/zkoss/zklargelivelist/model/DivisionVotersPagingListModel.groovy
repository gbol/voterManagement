package org.zkoss.zklargelivelist.model;

import java.util.List;
import bz.voter.management.Voter
import bz.voter.management.VoterService
import bz.voter.management.Division


/**
A ListModel used for Paging the general list of voters.
**/

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


	/**
	Use this constructor when you just need all the voters in a division.
	@arg division the division that you want to voters from.
	@arg startPageNumber the offset
	@arg pageSize the size of the page
	**/
	
	public DivisionVotersPagingListModel(Division division, int startPageNumber, int pageSize) {
		super(division, startPageNumber, pageSize)
		this.division = division
	}

	
	/**
	Use this constructor when you want a list of voters that match a search pattern within a division.
	@arg search the search string that will be used to search for voters. The search is done on voters first and last names only.
	@arg division the division you want voters from.
	@arg startPageNumber the offset
	@arg pageSize the size of the page
	**/

	public DivisionVotersPagingListModel(String search,Division division, int startPageNumber, int pageSize) {
		super(search,division, startPageNumber, pageSize)
		this.division = division
		this.searchString = search
	}


	/**
	Gets a list of voters in the specified division. This is used for paging.
	@param division the division you want voters from
	@param itemStartNumber the offset
	@param pageSize the size of the results returned
	@return List<Map> a list of voters
    <ul>
        <li>voter</li>
        <li>registrationDate</li>
        <li>registrationNumber</li>
        <li>lastName</li>
        <li>firstName</li>
        <li>houseNumber</li>
        <li>street</li>
        <li>phoneNumber1</li>
        <li>phoneNumber2</li>
        <li>phoneNumber3</li>
        <li>sex</li>
        <li>age</li>
        <li>birthDate</li>
        <li>pollStation</li>
        <li>pollNumber</li>
        <li>affiliation</li>
    </ul>
	**/
	@Override
	protected List<Map> getPageData(Division division, int itemStartNumber, int pageSize) {
		voterService = new VoterService()
        def votersList = []
		for(_voter in  voterService.listByDivision(division, itemStartNumber, pageSize)){
            def voter = _voter.read(_voter.id)
            def address = voter.registrationAddress
            def resultMap = [
                voter:              voter,
                registrationDate:   voter.registrationDate,
                registrationNumber: voter.registrationNumber,
                lastName:           voter.lastName,
                firstName:          voter.firstName,
                houseNumber:        address.houseNumber,
                phoneNumber1:       address.phoneNumber1,
                phoneNumber2:       address.phoneNumber2,
                phoneNumber3:       address.phoneNumber3,
                street:             address.street,
                sex:                voter.sex,
                age:                voter.age,
                birthDate:          voter.birthDate,
                pollStation:        voter.pollStation,
                pollNumber:         voter.pollStation.pollNumber,
                affiliation:        voter.affiliation
            ]

            votersList.push(resultMap)
        }

        return votersList
	}


	/**
	Gets a list of voters in a specified division whose first and/or last name match the search string.
	@param search the search string that is used to search for the voters.
	@param division the division you want voters from
	@param itemStartNumber the offset
	@param pageSize the size of the results returned
	@return List<Map> a list of voters
    <ul>
        <li>voter</li>
        <li>registrationDate</li>
        <li>registrationNumber</li>
        <li>lastName</li>
        <li>firstName</li>
        <li>houseNumber</li>
        <li>street</li>
        <li>phoneNumber1</li>
        <li>phoneNumber2</li>
        <li>phoneNumber3</li>
        <li>sex</li>
        <li>age</li>
        <li>birthDate</li>
        <li>pollStation</li>
        <li>pollNumber</li>
        <li>affiliation</li>
    </ul>
	**/
	@Override
	protected List<Voter> getPageData(String search, Division division, int itemStartNumber, int pageSize) {
		voterService = new VoterService()
        def votersList = []
		for(_voter in  voterService.searchByDivision(search,division, itemStartNumber, pageSize)){
            def voter = _voter.read(_voter.id)
            def address = voter.registrationAddress
            def resultMap = [
                voter:              voter,
                registrationDate:   voter.registrationDate,
                registrationNumber: voter.registrationNumber,
                lastName:           voter.lastName,
                firstName:          voter.firstName,
                houseNumber:        address?.houseNumber,
                phoneNumber1:       address?.phoneNumber1,
                phoneNumber2:       address?.phoneNumber2,
                phoneNumber3:       address?.phoneNumber3,
                street:             address?.street,
                sex:                voter.sex,
                age:                voter.age,
                birthDate:          voter.birthDate,
                pollStation:        voter.pollStation,
                pollNumber:         voter.pollStation.pollNumber,
                affiliation:        voter.affiliation
            ]

            votersList.push(resultMap)
        }

        return votersList
	}


	/**
	Returns total size of voters returned by a query.
	**/
	@Override
	public int getTotalSize() {
		def totalSize = searchString ? voterService.countByDivisionAndSearch(division,searchString) : voterService.countByDivision(division)
		return totalSize
	}


}
