package bz.voter.management

import org.codehaus.groovy.grails.commons.ConfigurationHolder

import bz.voter.management.utils.VoterListTypeEnum
import bz.voter.management.utils.PickupTimeEnum

class VoterElectionController {

    def exportService
    def voterElectionService

    def list = {
        def voters
        List fields
        Map labels
        def parameters = [:]

        def electionInstance = Election.get(params.election.toLong())
        def divisionInstance = Division.get(params.division.toLong())

	 	if(params?.format && params.format != 'html'){
			response.contentType = ConfigurationHolder.config.grails.mime.types[params.format]
			response.setHeader("Content-disposition", "attachment; filename=voters.${params.format}")

            fields = ["registrationNumber", "lastName", "firstName", "registrationAddress", "pollNumber", 
                "affiliation","pledge", "voted","pickupTime"]

            labels = [
                "registrationNumber": "Registration #",
                "lastName": "Last Name",
                "firstName": "First Name",
                "registrationAddress": "Address",
                "pollNumber": "Poll #",
                "affiliation": "Affiliation",
                "pledge": "Pledge",
                "voted": "Voted",
                "pickupTime": "Pickup Time"
            ]

            def registrationNumberFormatter = {domain, value->
                domain.voter.registrationNumber
            }

            def registrationAddressFormatter = {domain,value->
                "${domain.voter.registrationAddress}"
            }


            def lastNameFormatter = {domain, value->
                "${domain.voter.lastName}"
            }

            def firstNameFormatter = {domain, value->
                "${domain.voter.firstName}"
            }

            
            def pollNumberFormatter = {domain, value->
                "${domain.voter.pollStation}"
            }


            def affiliationFormatter = {domain,value->
                "${domain.voter.affiliation}"
            }


            def votedFormatter = {domain, value->
                def voted
                switch(value){
                    case true:
                        voted = "Yes"
                        break

                    case false:
                        voted = "No"
                        break

                }

                "${voted}"
            }

            
            Map formatters = [
                registrationNumber: registrationNumberFormatter,
                registrationAddress: registrationAddressFormatter,
                firstName:      firstNameFormatter,
                lastName:       lastNameFormatter,
                pollNumber:     pollNumberFormatter,
                affiliation:    affiliationFormatter,
                voted:          votedFormatter
                ]

            switch(params.listType){
                case "PICKUP_TIME":
                    voters = voterElectionService.filterByPickupTime(electionInstance, divisionInstance,(PickupTimeEnum)params.pickupTime,0,0)
                    parameters.title = "Voters Pickup Times"
                    break

                case "ALL":
                    voters = voterElectionService.listByElectionAndDivision(electionInstance,divisionInstance,0,0)
                    parameters.title = "Voters List for ${electionInstance.year} Election"
                    break

                case "PLEDGE":
                    voters = voterElectionService.filterByPledge(electionInstance,divisionInstance,Pledge.get(params.pledge),0,0)
                    parameters.title = "Voters Pledges for ${electionInstance.year} Election"
                    break
            }

            exportService.export(params.format, response.outputStream,voters,
                fields, labels,formatters, parameters)
        }
    }

    def pickupTime = {

        redirect(action: "list", 
            params: [
                "extension":"pdf", 
                "format":"pdf", 
                "division": params.division, 
                "election": params.election,
                "listType": params.listType,
                "pickupTime": params.pickupTime])
    }

    def allVoters = {
        redirect(action: "list", 
            params: [
                "extension":"pdf", 
                "format":"pdf", 
                "division": params.division, 
                "election": params.election,
                "listType": params.listType
            ])
    }


    def pledges = {
        redirect(action: "list", 
            params: [
                "extension":"pdf", 
                "format":"pdf", 
                "division": params.division, 
                "election": params.election,
                "pledge" : params.pledge,
                "listType": params.listType
            ])
    }
}
