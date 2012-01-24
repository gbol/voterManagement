package bz.voter.management

class Election implements Serializable{
	
	Integer year
	Date electionDate
	ElectionType electionType
    boolean complete //After an election is complete, no records pertaining to that election can be modified.

    static constraints = {
	 	electionDate(nullable: true)
	 	year(validator: {val,obj->
			if(!obj.id && Election.findByYearAndElectionType(val,obj.electionType)){
				return 'custom.error'
			}
		})
    }


    def beforeInsert(){
        complete = false
    }


}
