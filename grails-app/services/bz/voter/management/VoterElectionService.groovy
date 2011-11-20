package bz.voter.management

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
				VoterElection.create(voter,election,flush)	
				flush = false
				cnt++
			}
		}

		sessionFactory.getCurrentSession().flush()

    }


	 /**
	 Search for voters in an election by first name and/or last name.
	 @param searchString : first name and/or last name separated by a comma
	 @param election: the election where we want to search for a voter
	 @return a List of VoterElection
	 **/

	 def search(String searchString, Election election){
	 	def searchParams
		def results
		def query = "from VoterElection as ve " +
						"inner join ve.voter as v " +
						"inner join v.person as p " +
						"where " 

	 	if(!searchString.isAllWhitespace()){
	 		searchParams = searchString.split(',').collect{it}

			if(searchParams.size() == 1){

				query = query + "(lower(p.firstName) like lower(:firstName) " +
						"or lower(p.lastName) like lower(:lastName)) " +
						"and ve.election = :election"

				results = VoterElection.findAll("${query}", [
							firstName: '%' + searchParams[0].trim() + '%', 
							lastName: '%' + searchParams[0].trim() + '%', 
							election: election])
			}else{

				query = query + "(lower(p.firstName) like lower(:firstName) "+
						"and lower(p.lastName) like lower(:lastName)) " +
						"and ve.election = :election"

				results = VoterElection.findAll("${query}", [
							firstName: '%' + searchParams[0].trim() + '%', 
							lastName: '%' + searchParams[1].trim() + '%', 
							election: election])
			}

		}else{
			results = VoterElection.findAllByElection(election)
		}

		return results

	 }
}
