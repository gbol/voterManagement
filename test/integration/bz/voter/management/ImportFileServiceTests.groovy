package bz.voter.management

import grails.test.*

class ImportFileServiceTests extends GroovyTestCase {

    protected void setUp() {
        super.setUp()
    }


    protected void tearDown() {
        super.tearDown()
    }


    void testImportExcelFileWithVotersList() {
	 	def division = new Division(name:'Albert').save()
		def election = new Election(year:2008, electionType: ElectionType.findByCode('GN')).save()

		def initialNumberOfVoters = Voter.count()
		def importFileService = new ImportFileService()
		def numberOfVotersImported = importFileService.importVoters(division,election,'Sample.xls')

		assertEquals 13, numberOfVotersImported
    }

}
