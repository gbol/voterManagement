package bz.voter.management

import org.codehaus.groovy.grails.commons.ConfigurationHolder

import bz.voter.management.importer.*


/**
Service that has utilities to import files with voters into the database.
**/

class ImportFileService {

	def voterService

    static transactional = true


	/**
	Imports a list of voters in an excel file into the database. 
	@param division : the division that the voters belongs to.
	@param fileName: the name of the file that contains the voters list. This file should be located in 
	ConfigurationHolder.config.files.dir
	@return void
	**/
    def importVoters(Division division, String fileName) {
      VoterExcelImporter excelImporter = new VoterExcelImporter(ConfigurationHolder.config.files.dir + fileName);
      def votersMapList = excelImporter.getVoters()
        
      votersMapList.each { Map voterParams ->
		  		def municipality = Municipality.findByName(voterParams.municipality) ?:  Municipality.findByName('Unknown')

		  		def addressParams = [
					houseNumber: voterParams.houseNumber,
					street: voterParams.street,
					municipality:  municipality
				]

				voterParams.sex = Sex.findByCode(voterParams.sex)
				voterParams.identificationType = IdentificationType.findByName(voterParams.identificationType) ?: 
																new IdentificationType(name:'Unknown').save(flush:true)
				voterParams.affiliation = Affiliation.findByName(voterParams.affiliation) ?: Affiliation.findByName('UNKNOWN')
				voterParams.pollStation = PollStation.findByPollNumber(voterParams.pollStation) ?: 
														new PollStation(pollNumber: "${voterParams.pollStation}", division: division).save()
				voterParams.pledge = Pledge.findByName(voterParams.pledge) ?: Pledge.findByName('Undecided')

				voterParams.address = addressParams
				if(!voterService) voterService = new VoterService()
				voterService.add(voterParams)
       } 

    }

}
