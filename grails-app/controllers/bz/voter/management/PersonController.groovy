package bz.voter.management

import org.codehaus.groovy.grails.commons.ConfigurationHolder

import bz.voter.management.utils.VoterListTypeEnum

class PersonController {

	def exportService
	def voterService

    def index = { }

	 def list = {
	 	if(params?.format && params.format != 'html'){
            def voters
			response.contentType = ConfigurationHolder.config.grails.mime.types[params.format]
			response.setHeader("Content-disposition", "attachment; filename=person.${params.format}")

			List fields = ["registrationDate", "registrationNumber","lastName","firstName","age","birthDate", "registrationAddress"]
			Map labels = ["registrationDate": "Registration Date" ,"registrationNumber": "Registration Number", 
                "firstName": "First Name", "lastName": "Last Name", 
				"birthDate" : "DOB", "age" : "Age", "registrationAddress" : "Address"]

			Map parameters = ["column.widths": [0.1,0.1,0.2, 0.2, 0.1, 0.1, 0.2]]

            def dateFormatter = {domain, value->
                value.format("dd-MMM-yyyy")
            }

            Map formatters = [registrationDate: dateFormatter, birthDate: dateFormatter]

			def divisionInstance = Division.get(params.division)

            switch(params.listType){
                case "ALL":
			        voters = voterService.listByDivision(divisionInstance)
                    parameters.title = "Voters"
                    break
                case "NAME":
                    voters = voterService.searchByDivision(params.searchString, divisionInstance,0 , 0)
                    parameters.title = "Voters with Name that Matches: ${params.searchString}"
                    break

                case "AFFILIATION":
                    def affiliationInstance = Affiliation.get(params.affiliation)
                    voters = voterService.filter(affiliationInstance,divisionInstance,0, 0)
                    parameters.title = "Voters with ${affiliationInstance} Affiliation"
                    break
            }


			exportService.export(params.format,response.outputStream,voters, 
				fields, labels, formatters,parameters)
		}
	 }

	 def test={
	 	render("Hello form test")
	 }


	 def pdf = {
        def listType = params.listType

        switch(listType){
            case "ALL":
	 	        redirect(action: "list", params: ["extension" : "pdf", "format":"pdf","division" : params.division, listType: listType])
                break
            
            case "NAME":
	 	        redirect(action: "list", params: ["extension" : "pdf", "format":"pdf","division" : params.division, listType: listType, searchString: params.searchString])
                break

            case "AFFILIATION":
	 	        redirect(action: "list", params: ["extension" : "pdf", "format":"pdf","division" : params.division, listType: listType, affiliation: params.affiliation])
                break
        }
	 }


}
