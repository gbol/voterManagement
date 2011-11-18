package bz.voter.management

import org.apache.commons.lang.builder.HashCodeBuilder

class VoterElection implements Serializable{

	Voter voter
	Election election
	boolean voted
	Date pickupTime


    static constraints = {
	 	pickupTime(nullable:true)
    }


	 boolean equals(other){
	 	if(!(other instanceof VoterElection)){
			return false
		}

		other.voter?.id == voter?.id &&
			other.election?.id == election?.id
	 }


	 int hashCode(){
	 	def builder = new HashCodeBuilder()
		if (voter) builder.append(voter.id)
		if(election) election.append(election.id)
		builder.toHashCode()
	 }


	 static VoterElection get(long voterId, electionId){
	 	find "from VoterElection where voter.id=:voterId and election.id=:electionId",
			[voterId: voterId, electionId: electionId]
	 }


	 static VoterElection create(Voter voter, Election election, boolean flush=false){
	 	new VoterElection(voter: voter, election: election, voted: false).save(flush:flush,insert:true)
	 }


	 static VoterElection update(Voter voter, Election election, boolean voted, Date pickupTime, flush=false){
	 	def instance = VoterElection.get(voter.id,election.id)
		if(instance){
			instance.voted = voted
			instance.pickupTime = pickupTime
			instance.save(flush:flush)
		}

		return instance
	 }


	 static mapping = {
	 	id composite: ['voter','election']
		version false
	 }
}
