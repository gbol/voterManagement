package bz.voter.management

import java.util.Calendar

class VoterService {

    static transactional = true

	 def messageSource

	 def personService

	/*
	Save a new instance of voters.
	@param params : a map containing the fields for adding a new voter:
	id
	firstName
	middleName
	lastName
	birthDate
	registrationDate
	registrationNumber
	sex
	houseNumber
	street
	municipality
	homePhone
	workPhone
	cellPhone
	comments
	identificationType
	pollStation
	affiliation

	If id does not exist, then an insert is done, otherwise the existing instance is updated.

	@returns an instance of voter
	
	*/

	def save(def params){

		def errorMessages
		def voterInstance
		def personInstance
		
		if(params.voter?.id){
			voterInstance = params.voter
			params.person = voterInstance.person
		}else{
			voterInstance = new Voter()
		}

		personInstance = personService.save(params)

		if(personInstance.hasErrors()){
			for(error in personInstance.errors.allErrors){
				errorMessages = errorMessages + "\n" + 
					messageSource.getMessage(error,null)
			}

			voterInstance.errors.rejectValue("person", "person.error", errorMessages)
			
		}else{
			voterInstance.person = personInstance
			voterInstance.registrationDate = params.registrationDate ?: voterInstance.registrationDate
			voterInstance.registrationNumber = params.registrationNumber ?: voterInstance.registrationNumber
			voterInstance.identificationType = params.identificationType ?: voterInstance.identificationType
			voterInstance.pollStation = params.pollStation ?: voterInstance.pollStation
			voterInstance.affiliation = params.affiliation ?: voterInstance.affiliation

			voterInstance.validate()

			if(voterInstance.hasErrors()){
			}else{
				voterInstance.save()
			}
		}

		return voterInstance
		
	}


	def add(params){
		def voterInstance = save(params)

		if(!voterInstance.hasErrors()){
			def year = voterInstance.registrationDate.toCalendar().get(Calendar.YEAR)

			def elections = Election.findAllByYearGreaterThanEquals(year)

			for(election in elections){
				VoterElection.create(voterInstance,election,true)
			}
			
		}

		return voterInstance
	}


	/**
	Searches for voters that match a certain name. 
	The search pattern can be an empty string, in which case a list of all Voters is returned.
	A single string, in which case a search for a voter whose first name of last name match the
	string.
	A string separated by a comma. The first string being the first name and the last string a 
	last name.
	@return A list of results
	**/

	 def search(String searchString){
	 	def searchParams
		def results

	 	if(!searchString.isAllWhitespace()){
	 		searchParams = searchString.split(',').collect{it}

			def criteria = Voter.createCriteria()
			results = criteria.list {
				person{
					if(searchParams.size() == 1){
					or {
						ilike("firstName", "%${searchParams[0].trim()}%")
						ilike("lastName", "%${searchParams[0].trim()}%")
					}
				}else{
					and{
						ilike("firstName", "%${searchParams[0].trim()}%")
						ilike("lastName", "%${searchParams[1].trim()}%")
					}
				}
				}
			}
		}else{
			results = Voter.list()
		}

		return results

	 }


	 def searchByDivision(String searchString, Division division){
	 	def searchParams
		def results


		def query = "select v " +
			"from Voter as v " +
			"inner join v.person as p "+
			"inner join v.pollStation as poll " +
			"where poll.division =:division " +
		   "and ((lower(p.firstName) like lower(:firstName)) " 

	 	if(!searchString.isAllWhitespace()){
	 		searchParams = searchString.split(',').collect{it}

			if(searchParams.size() == 1){
				query = query + 
					" or (lower(p.lastName) like lower(:lastName)))"

				results = Voter.executeQuery(query, [
					firstName: '%' + searchParams[0].trim() + '%',
					lastName: '%' + searchParams[0].trim() + '%',
					division: division])
			}else{
				query = query + 
					" and (lower(p.lastName) like lower(:lastName)))"

				results = Voter.executeQuery(query, [
					firstName: '%' + searchParams[0].trim() + '%',
					lastName: '%' + searchParams[1].trim() + '%',
					division: division])
			}

		}else{
			query = "select v " +
				"from Voter as v " +
				"inner join v.pollStation as p " +
				"where p.division =:division"
			results = Voter.executeQuery(query, [
				division: division
			])
		}

		return results

	 }


	 def listByDivision(Division division){
	 	def query = "select v " +
			"from Voter as v " +
			"inner join v.pollStation as poll " +
			"where  poll.division =:division"

		return Voter.executeQuery(query, [division: division])
	 	
	 }

}
