package bz.voter.management

import grails.test.*

class ElectionTests extends GrailsUnitTestCase {

	def electionType

    protected void setUp() {
        super.setUp()
			
	 		electionType = new ElectionType(name: 'General')
	 		mockDomain(ElectionType, [electionType])
	 		mockDomain(Election)
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testSuccessfulSave() {
	 	def electionInstance = new Election(year: 2008,electionType: electionType)
	 	mockForConstraintsTests(Election, [electionInstance] ) 
		electionInstance.validate()
		electionInstance.save(flush:true)
		assertNotNull electionInstance.id

    }


	 void test_Can_Not_Save_Election_Twice(){
	 	def electionInstance = new Election(year: 2008,electionType: electionType)
		mockDomain(Election, [electionInstance])
		def electionInstance2 = new Election(year: 2008,electionType: electionType)
		mockForConstraintsTests(Election, [electionInstance2])
		electionInstance2.validate()
		assertTrue electionInstance2.hasErrors()

	 }
}
