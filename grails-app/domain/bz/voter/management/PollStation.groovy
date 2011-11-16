package bz.voter.management

class PollStation {

	Integer pollNumber
	Division division

	static transients = ['name']

	String toString(){
		"${division.name} : ${pollNumber}"
	}

    static constraints = {

    }


	 def getName(){
	 	pollNumber.toString()
	 }
}
