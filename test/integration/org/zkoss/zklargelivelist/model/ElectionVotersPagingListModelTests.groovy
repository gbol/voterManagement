package org.zkoss.zklargelivelist.model

import grails.test.*

import bz.voter.management.Voter
import bz.voter.management.Division
import bz.voter.management.Election
import bz.voter.management.ElectionType

class ElectionVotersPagingListModelTests extends GroovyTestCase {

	Division division
	Election election

   protected void setUp() {
        super.setUp()
		  division = Division.findByName('Albert')
		  election = new Election(year: 2011,
		  				electionType: ElectionType.findByName('General')).save()

    }

    protected void tearDown() {
        super.tearDown()
    }

    void testGetTotalSizeReturnsCountOfVotersInADivisionForAnElection() {
	 	println "\nElection: ${Election.findByYear(2011).id}"
		println "\nDivision: ${division.id}"
	 	ElectionVotersPagingListModel model = 
				new ElectionVotersPagingListModel(Election.findByYear(2011),division,0,5)

		assertEquals 13, model.getTotalSize()

    }

	 void testGetTotalSizeReturnsCountOfVotersAndModelReturnsSizeEqualToMaxSize(){
	 	ElectionVotersPagingListModel model = 
				new ElectionVotersPagingListModel('f',Election.findByYear(2011),division,0,5)

		println "\nmodel: ${model}\n"

		assertEquals 5, model.getSize()
		assertEquals 6, model.getTotalSize()

	 }
}
