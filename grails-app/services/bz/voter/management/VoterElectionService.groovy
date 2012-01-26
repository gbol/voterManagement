package bz.voter.management

import java.util.Calendar

class VoterElectionService {

   static transactional = true

	static String  QUERY =  "select ve from VoterElection as ve " +
							"inner join ve.voter as v " +
						    "inner join v.person as p " +
					 	    "inner join v.pollStation as poll " +
							"where ve.election =:election " +
							"and poll.division =:division " 

	static String  COUNT_BY_SEARCH_QUERY =  "select count(ve.voter) from VoterElection as ve " +
											"inner join ve.voter as v " +
							      			"inner join v.person as p " +
					 			   			"inner join v.pollStation as poll " +
											"where ve.election =:election " +
											"and poll.division =:division "


   def sessionFactory

   def addAllVoters(Election election) {
	 	if(VoterElection.findAllByElection(election).size() < 1){
			//Add all voters to election
			def pledge = Pledge.findByCode('U')
			int cnt = 0
			def flush = false
			def sizeOfList = Voter.count()
			for(voter in Voter.list()){
				if(cnt == 100 || (sizeOfList < 100 && cnt == sizeOfList - 1 )){
					flush = true
					cnt =  0
				}
				if(voter.registrationDate.toCalendar().get(Calendar.YEAR) <= election.year){
					VoterElection.create(voter,election,null,flush)	
					flush = false
					cnt++
				}
			}
		}

		sessionFactory.getCurrentSession().flush()

    }


	List<VoterElection> findAllByElection(Election election){
		(List<VoterElection>) VoterElection.findAllByElection(election)
	}

	 /**
	 Search for voters in an election by first name and/or last name.
	 @arg searchString : first name and/or last name separated by a comma
	 @arg election: the election where we want to search for a voter
	 @arg offset the offset for the query
	 @arg max the maximum size of the results returned
	 @return a List of VoterElection
	 **/

	 def search(String searchString, Election election, Division division, int offset, int max){
		executeQuery(searchString, QUERY,election,division,offset,max)

	 }


	/**
	Lists all voters in a certain division registered to vote in a specific election.
	@arg election is the election 
	@arg division is the division
	@arg offset offset
	@arg max maximum size of the results
	@returns List<VoterElection> of voters registered to vote in a division for a specific election.
	**/
	public List<VoterElection> listByElectionAndDivision(Election election, Division division, int offset, int max){
		def votersList = VoterElection.executeQuery(QUERY, 
			[election: election, 
			division: division,
			offset: offset,
			max: max])

        return votersList
	}

	def countByElectionAndDivision(Election election, Division division){
		def numberOfVoters = VoterElection.executeQuery(COUNT_BY_SEARCH_QUERY,[
			election: election,
			division: division])

       return numberOfVoters[0]
	}

	 
	 /**
	 Get count of voters in an election by first name and/or last name.
	 @arg searchString : first name and/or last name separated by a comma
	 @arg election: the election where we want to search for a voter
	 @return total number of records/voters returned by a search.
	 **/
	public int countVoters(String searchString,Election election, Division division){
		def result 
		if(searchString.isAllWhitespace()){
			result = countByElectionAndDivision(election,division)
		}else{
			def resultCount = executeQuery(searchString, COUNT_BY_SEARCH_QUERY, election,division,0,0)
            result = resultCount[0]
		}
		return result
	}



	def executeQuery(String searchString, String searchQuery, Election election, Division division, int offset, int max){
	 	def searchParams
		def results
		def query = searchQuery
		def firstName
		def lastName

	 	if(!searchString.isAllWhitespace()){
	 		searchParams = searchString.split(',').collect{it}

			if(searchParams.size() == 1){

				query +=  "and ((lower(p.firstName) like lower(:firstName)) " +
						  "or (lower(p.lastName) like lower(:lastName))) "  

				firstName =  '%' + searchParams[0].trim() + '%'
				lastName = '%' + searchParams[0].trim() + '%' 


			}else{

				query +=  "and (lower(p.firstName) like lower(:firstName) "+
						  "and lower(p.lastName) like lower(:lastName)) " 

				firstName =  '%' + searchParams[0].trim() + '%' 
				lastName =  '%' + searchParams[1].trim() + '%' 

			}

			if(max == 0){
				results = VoterElection.executeQuery("${query}", [
						firstName: firstName, 
						lastName: lastName, 
						division: division,
						election: election])
			}else{
				results = VoterElection.executeQuery("${query}", [
						firstName: firstName, 
						lastName: lastName, 
						division: division,
						election: election,
						offset: offset,
						max: max])
		}

		}else{
			results = listByElectionAndDivision(election,division,offset, max)

		}

		return results
		
	}

}
