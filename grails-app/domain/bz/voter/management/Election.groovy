package bz.voter.management

class Election {
	
	Integer year
	ElectionType electionType

    static constraints = {
	 	year(validator: {val,obj->
			if(Election.findByYearAndElectionType(val,obj.electionType)){
				return 'custom.error'
			}
		})
    }


}
