package bz.voter.management

class VoterElectionService {

    static transactional = true

    def addAllVoters(Election election) {
	 	if(VoterElection.findAllByElection(election).size() < 1){
			//Add all voters to election
			int cnt = 0
			def flush = false
			for(voter in Voter.list()){
				if(cnt == 100){
					flush = true
				}
				VoterElection.create(voter,election,flush)	
				flush = false
				cnt++
			}
		}

    }
}
