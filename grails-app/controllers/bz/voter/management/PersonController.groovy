package bz.voter.management

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class PersonController {

	def exportService
	def voterService

    def index = { }

	 def list = {
	 	if(params?.format && params.format != 'html'){
			response.contentType = ConfigurationHolder.config.grails.mime.types[params.format]
			response.setHeader("Content-disposition", "attachment; fielname=person.${params.format}")

			List fields = ["firstName", "lastName","birthDate", "age", "sex"]
			Map labels = ["firstName": "First Name", "lastName": "Last Name", 
				"birthDate" : "DOB", "age" : "Age", "sex" : "Sex"]

			Map parameters = [title: "People" , "column.widths": [0.3,  0.3, 0.2, 0.1, 0.1]]

			def divisionInstance = Division.get(params.division)

			def voters = voterService.listByDivision(divisionInstance)

			exportService.export(params.format,response.outputStream,voters, 
				fields, labels, null,parameters)
		}
	 }

	 def test={
	 	render("Hello form test")
	 }


	 def pdf = {
	 	redirect(action: "list", params: ["extension" : "pdf", "format":"pdf","division" : params.division])
	 }
}
