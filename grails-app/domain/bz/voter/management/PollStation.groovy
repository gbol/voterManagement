package bz.voter.management

class PollStation implements Serializable{

	String pollNumber
	Division division

	static transients = ['name' ]

	String toString(){
		"${division.name} : ${pollNumber}"
	}

    static constraints = {
	 	pollNumber(blank: false)

    }


	 def getName(){
	 	pollNumber
	 }

}
