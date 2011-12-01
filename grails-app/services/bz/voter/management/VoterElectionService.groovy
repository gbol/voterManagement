package bz.voter.management

import java.util.Calendar

class VoterElectionService {

	def sessionFactory

    static transactional = true

    def addAllVoters(Election election) {
	 	if(VoterElection.findAllByElection(election).size() < 1){
			//Add all voters to election
			int cnt = 0
			def flush = false
			def sizeOfList = Voter.count()
			for(voter in Voter.list()){
				if(cnt == 100 || (sizeOfList < 100 && cnt == sizeOfList - 1 )){
					flush = true
					cnt =  0
				}
				if(voter.registrationDate.toCalendar().get(Calendar.YEAR) <= election.year){
					VoterElection.create(voter,election,flush)	
					flush = false
					cnt++
				}
			}
		}

		sessionFactory.getCurrentSession().flush()

    }



	List<VoterElection> findAllByElection(Election election){
		//println "\nSearching for voters in election: ${election?.year} | ${election?.electionType?.name}\n\n"
		(List<VoterElection>) VoterElection.findAllByElection(election)
	}

	 /**
	 Search for voters in an election by first name and/or last name.
	 @param searchString : first name and/or last name separated by a comma
	 @param election: the election where we want to search for a voter
	 @return a List of VoterElection
	 **/

	 def search(String searchString, Election election, Division division){
	 	def searchParams
		def results
		def query = "select ve from VoterElection as ve " +
						"inner join ve.voter as v " +
						"inner join v.person as p " 

	 	if(!searchString.isAllWhitespace()){
	 		searchParams = searchString.split(',').collect{it}

			if(searchParams.size() == 1){
				if(division){
					println "\n Searching for voters with name ${searchString} in ${division}\n"

					query = query + " inner join v.pollStation as poll " +
						"where ((lower(p.firstName) like lower(:firstName)) " +
						"or (lower(p.lastName) like lower(:lastName))) " +
						"and ve.election =:election and poll.division =:division"

					results = VoterElection.executeQuery("${query}", [
							firstName: '%' + searchParams[0].trim() + '%', 
							lastName: '%' + searchParams[0].trim() + '%', 
							division: division,
							election: election])
					println "results: ${results}\n"

				}else{

					query = query + " where (lower(p.firstName) like lower(:firstName) " +
						"or lower(p.lastName) like lower(:lastName)) " +
						"and ve.election =:election"

					results = VoterElection.executeQuery("${query}", [
							firstName: '%' + searchParams[0].trim() + '%', 
							lastName: '%' + searchParams[0].trim() + '%', 
							election: election])
				}

			}else{

				if(division){
					query = query + " inner join v.pollStation as poll " +
						"where (lower(p.firstName) like lower(:firstName) "+
						"and lower(p.lastName) like lower(:lastName)) " +
						"and ve.election =:election and poll.division =:division"

					results = VoterElection.executeQuery("${query}", [
							firstName: '%' + searchParams[0].trim() + '%', 
							lastName: '%' + searchParams[1].trim() + '%', 
							division: division,
							election: election])
				}else{
					query = query + " where (lower(p.firstName) like lower(:firstName) "+
						"and lower(p.lastName) like lower(:lastName)) " +
						"and ve.election =:election"

					results = VoterElection.executeQuery("${query}", [
							firstName: '%' + searchParams[0].trim() + '%', 
							lastName: '%' + searchParams[1].trim() + '%', 
							election: election])
				}

			}

		}else{
			if(division){
				query = query + " inner join v.pollStation as poll " +
				"where ve.election =:election and poll.division =:division"
				results = VoterElection.executeQuery("${query}", [
					election: election,
					division: division])

			}else{
				results = VoterElection.findAllByElection(election)
			}
		}

		return results

	 }


}
