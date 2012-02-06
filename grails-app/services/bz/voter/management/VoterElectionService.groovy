package bz.voter.management

import java.util.Calendar


import bz.voter.management.utils.PickupTimeEnum
import bz.voter.management.spring.SpringUtil

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.SqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class VoterElectionService {

   static transactional = true

   def jdbcTemplate
   def dataSource
   //def namedParameterJdbcTemplate

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

	static String  FILTER_BY_PLEDGE_QUERY  =  "select ve from VoterElection as ve " +
							                    "inner join ve.voter as v " +
						                        "inner join v.person as p " +
					 	                        "inner join v.pollStation as poll " +
							                    "where ve.election =:election " +
							                    "and poll.division =:division " +
                                                "and ve.pledge =:pledge " 

	static String  COUNT_BY_PLEDGE =  "select count(ve.voter) as votes_count from VoterElection as ve " +
										"inner join ve.voter as v " +
							     		"inner join v.person as p " +
					 			   		"inner join v.pollStation as poll " +
										"where ve.election =:election " +
										"and poll.division =:division " +
                                        "and ve.pledge =:pledge"


    static String HOURLY_COUNT_BY_POLLSTATION_QUERY = "SELECT count(ve.voter_id) as votes_count, " +
                                        "affiliation.name as affiliation, " +
                                        "EXTRACT(HOUR FROM ve.vote_time) as 'vote_time' " + 
                                        "FROM voter_election as ve " +
                                        "inner join voter as v on ve.voter_id=v.id " +
                                        "inner join affiliation as affiliation on v.affiliation_id = affiliation.id " +
                                        "inner join poll_station as poll on v.poll_station_id = poll.id " +
                                        "WHERE ve.election_id = :election_id " +
                                        "and poll.id = :poll_station_id " +
                                        "and poll.division_id = :division_id " +
                                        "GROUP BY affiliation.name, 'vote_time'"
                                            


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
		def votersList 
        if(max > 0){
            votersList= VoterElection.executeQuery(QUERY, 
			[election: election, 
			division: division,
			offset: offset,
			max: max])
        }else{
            votersList= VoterElection.executeQuery(QUERY, 
			[election: election, 
			division: division])
        }
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


    /**
    Filters list of voters by a specific pledge
    @param Election election for which we want to filter voters.
    @param Division division whose voters we wish to filter.
    @param Pledge
    @param int offset
    @param int max
    @return List of VoterElection
    **/
    public List<VoterElection> filterByPledge(Election election, Division division, Pledge pledge, int offset, int max){
        def _voters 
        
        def query = FILTER_BY_PLEDGE_QUERY + " order by p.lastName"
        if(max > 0){
            _voters = VoterElection.executeQuery(query,[
                        election: election,
                        division: division,
                        pledge: pledge,
                        offset: offset,
                        max: max
                   ])
        }else{
            _voters = VoterElection.executeQuery(query,[
                        election: election,
                        division: division,
                        pledge: pledge
                   ])
        }

        return _voters
    }



    /**
    Filters voters by a specific pledge and whether they voted or not.
    @param Election
    @param Division
    @param Pledge
    @param boolean voted
    @param int offset
    @param int max
    @return List<VoterElection>
    **/
    public List<VoterElection> filterByPledgeAndVoted(Election election, Division division, Pledge pledge, boolean voted, int offset, int max){
        def _voters

        def query = FILTER_BY_PLEDGE_QUERY + " and voted =:voted order by p.lastName"

        if(max>0){
            _voters = VoterElection.executeQuery(query,
                    [
                        election: election,
                        division: division,
                        pledge: pledge,
                        voted: voted,
                        offset: offset,
                        max: max
                    ])
        }else{
            _voters = VoterElection.executeQuery(query,
                      [
                        election: election,
                        division: division,
                        pledge: pledge,
                        voted: voted
                      ])
        }

        return _voters
    }


    
   /**
   Counts the total number of voters with a specific pledge.
   @param Election
   @param Division
   @param Pledge
   @return int total count of voters with a specific pledge.
   **/
   public int countByPledge(Election election, Division division, Pledge pledge){
        def _count = VoterElection.executeQuery(COUNT_BY_PLEDGE,[
                        election: election,
                        division: division,
                        pledge: pledge
                        ])
        return _count[0]
   }



   /**
   Counts the total number of voters with a specific pledge that either voted or not.
   @param Election
   @param Division
   @param Pledge
   @return int
   **/
   public int countByPledgeAndVoted(Election election, Division division, Pledge pledge, boolean voted){
        def query = COUNT_BY_PLEDGE + " and ve.voted =:voted"
        def _count = VoterElection.executeQuery(query,
                [election: election,
                 division: division,
                 pledge: pledge,
                 voted: voted
                ])


        return _count[0]
   }



   /**
   Fiter the voters at an election by pickup time.
   @param Election
   @param Division
   @param PickupTimeEnum
   @param int offset
   @param int max
   @return List<VoterElection>
   **/
   public List<VoterElection> filterByPickupTime(Election election, Division division, PickupTimeEnum pickupTimeEnum, int offset, int max){
        def hourMarks = pickupTimeEnum.value().split('-')
        def results = []


        def query = QUERY + " AND ve.pickupTime  like (:hour)"

        if(max > 0){
            results = VoterElection.executeQuery(query, [
                division: division,
                election: election,
                hour: (hourMarks[0] + ':%'),
                offset: offset,
                max: max
            ])
        }else{
            results = VoterElection.executeQuery(query, [
                division: division,
                election: election,
                hour: (hourMarks[0] + ':%')
            ])
        }

        return results

   }


    /**
    Filters the voters at an election by Pickup Time and if they voted or not.
    @param Election
    @param Division
    @param PickupTimeEnum
    @param boolean voted
    @param int offset
    @param int max
    @return List<VoterElection>
    **/
    public List<VoterElection> filterByPickupTimeAndVoted(Election election, Division division, PickupTimeEnum pickupTimeEnum, boolean voted, int offset, int max){
        def hourMarks = pickupTimeEnum.value().split('-')
        def results = []

        def query = QUERY + " AND ve.pickupTime like (:hour) AND ve.voted =:voted "

        if(max>0){
            results = VoterElection.executeQuery(query, [
                division: division,
                election: election,
                hour: (hourMarks[0] + ':%'),
                voted: voted,
                offset: offset,
                max: max
                ])
        }else{
            results = VoterElection.executeQuery(query, [
                division: division,
                election: election,
                hour: (hourMarks[0] + ':%'),
                voted: voted])
        }


        return results
    }


    /**
    Counts the voters at an election that were scheduled to be picked up at a certain time 
    and if they voted or not.
    @param Election
    @param Division
    @param PickupTimeEnum
    @param boolean voted
    @return int : count of voters
    **/
    public int countByPickupTimeAndVoted(Election election, Division division, PickupTimeEnum pickupTimeEnum, boolean voted ){
        def hourMark = pickupTimeEnum.value().split('-')

        def query = COUNT_BY_SEARCH_QUERY + " AND ve.pickupTime like (:hour) AND ve.voted =:voted"

        def results = VoterElection.executeQuery(query, [
                        division: division,
                        election: election,
                        hour: (hourMark[0] + ':%'),
                        voted: voted
                        ])

        return results[0]

    }
   


   public int countByPickupTime(Election election, Division division, PickupTimeEnum pickupTimeEnum){
    def hourMark = pickupTimeEnum.value().split('-')

    def query = COUNT_BY_SEARCH_QUERY +  " and ve.pickupTime like (:hour)"

    def results = VoterElection.executeQuery(query, [
                  division: division,
                  election: election,
                  hour: (hourMark[0] + ':%')
               ])

    return results[0]
   }


    
    def countByHourAndPollStation(Election election,Division division, PollStation pollStation){

        SqlParameterSource namedParameters = new MapSqlParameterSource("election_id", election.id)
        namedParameters.addValue("division_id", division.id)
        namedParameters.addValue("poll_station_id", pollStation.id)
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource)

        def results = namedParameterJdbcTemplate.queryForList(HOURLY_COUNT_BY_POLLSTATION_QUERY, namedParameters)

        /*println "\nresults: ${results} \n"

        results.each{
            switch(it.vote_time){
                
                case "14":
                    println "${PickupTimeEnum.TWO.value()} : ${it.affiliation} : ${it.votes_count}"
                    break

                case "15":
                    println "${PickupTimeEnum.THREE.value()} : ${it.affiliation} : ${it.votes_count}"
                    break

                case "16":
                    println "${PickupTimeEnum.FOUR.value()} : ${it.affiliation} : ${it.votes_count}"
                    break

                case "17":
                    println "${PickupTimeEnum.FIVE.value()} : ${it.affiliation} : ${it.votes_count}"
                    break

                case "18":
                    println "${PickupTimeEnum.EIGHTEEN.value()} : ${it.affiliation} : ${it.votes_count}"
                    break


            }
        }
        */

        return results

   }



}

