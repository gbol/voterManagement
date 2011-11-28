package bz.voter.management

import org.apache.commons.lang.builder.HashCodeBuilder

class VoterElection implements Serializable{

	Voter voter
	Election election
	boolean voted
	String pickupTime
	Date voteTime


    static constraints = {
	 	pickupTime(nullable:true)
		voteTime(nullable: true)
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
		if(election) builder.append(election.id)
		if(pickupTime) builder.append(pickupTime)
		if(voteTime) builder.append(voteTime)
		builder.toHashCode()
	 }


	 static VoterElection get(long voterId, electionId){
	 	find "from VoterElection where voter.id=:voterId and election.id=:electionId",
			[voterId: voterId, electionId: electionId]
	 }


	 static VoterElection create(Voter voter, Election election, boolean flush=false){
	 	new VoterElection(voter: voter, election: election, voted: false).save(flush:flush,insert:true)
	 }


	 static VoterElection update(Voter voter, Election election, boolean voted, 
	 	Date pickupTime,Date voteTime, flush=false){
	 	def instance = VoterElection.get(voter.id,election.id)
		if(instance){
			instance.voted = voted
			instance.pickupTime = pickupTime
			instance.pickupTime = voteTime
			instance.save(flush:flush)
		}

		return instance
	 }


	 static mapping = {
	 	id composite: ['voter','election']
		version false
	 }

	 static namedQueries={
	 	findVotesSummaryByPollStationsAndElection{election->
			createAlias("voter", "voter")
			createAlias 'election', 'e'
			//createAlias 'v.pollStation', 'p'
			//createAlias 'v.affiliation', 'party'

			projections{
				//property 'p.pollNumber', 'pollStation'
				//property 'party.name', 'affiliation'
				//property("voter.registrationNumber")
				count 'voter' , 'votes'
				//groupProperty('voter.affiliation.name')
				//groupProperty('voter')
				groupProperty('voted')
				groupProperty('voter')
				//groupProperty('v.registrationNumber')

			}

			and{
				eq 'election', election
				eq 'voted', true
			}

			/*order 'pollStation'
			order 'votes'
			order 'affiliation'
			*/
			//order 'voter'
		}
	 }


	 static countVotesByPollStationAndAffiliation(Election election){
	 	if(election instanceof Election){
	 		def query = "select count(v.id) as voteCounts, " +
				"p.pollNumber as poll_station, " +
				"aff.name as party, div.name as divisions "+
				"from VoterElection ve"+
				" inner join ve.voter as v" +
				" inner join v.affiliation as aff" +
				" inner join v.pollStation as p" +
				" inner join p.division as div" +
				" where ve.election = :election and ve.voted = true " +
				" group by p.pollNumber, aff.name, div.name "

			def results = VoterElection.executeQuery(query,[election: election])
		}
	 }
}
