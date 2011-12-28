package org.zkoss.zklargelivelist.model

import grails.test.*

import bz.voter.management.Voter
import bz.voter.management.Division


class DivisionVotersPagingListModelTests extends GroovyTestCase {


	Division division

    protected void setUp() {
        super.setUp()
		  division = Division.findByName('Albert')
    }

    protected void tearDown() {
        super.tearDown()
    }

    void test_getTotalSize_Returns_Count_Of_Voters_In_A_Division() {

	 	DivisionVotersPagingListModel divisionVotersPagingListModel = 
			new DivisionVotersPagingListModel(division,0,5)

			assertEquals 13, divisionVotersPagingListModel.getTotalSize()

    }


	 void test_Model_Returned(){
	 	DivisionVotersPagingListModel model = new DivisionVotersPagingListModel(division,0,5)

		assertEquals 5, model.getSize()
	 }
}
