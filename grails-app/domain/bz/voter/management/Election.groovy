package bz.voter.management

class Election {
	
	Integer year
	Date electionDate
	ElectionType electionType

    static constraints = {
	 	electionDate(nullable: true)
	 	year(validator: {val,obj->
			if(!obj.id && Election.findByYearAndElectionType(val,obj.electionType)){
				return 'custom.error'
			}
		})
    }


}
