package bz.voter.management

import grails.test.*

class ImportFileServiceTests extends GroovyTestCase {

    protected void setUp() {
        super.setUp()
    }


    protected void tearDown() {
        super.tearDown()
    }


    void testSomething() {
	 	def division = new Division(name:'Albert').save()

		def initialNumberOfVoters = Voter.count()
		def importFileService = new ImportFileService()
		importFileService.importVoters(division,'Sample.xls')

		assertEquals((Voter.count() - initialNumberOfVoters), 13)
    }

}
