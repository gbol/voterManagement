package bz.voter.management

class Voter implements Serializable{
	
	Person person
	Date registrationDate
	String registrationNumber
	IdentificationType identificationType
	PollStation pollStation
	Pledge pledge
	Affiliation affiliation

    static constraints = {
	 	registrationNumber(blank:false)
		affiliation(nullable: true)
		pledge(nullable: true)
    }

	 def beforeValidate(){
	 	registrationNumber = registrationNumber?.trim()
	 }


	 def getNumberOfYearsRegistered(){

	 	Calendar today = Calendar.getInstance()
		Calendar dateOfRegistration = Calendar.getInstance()
		dateOfRegistration.setTime(this.registrationDate)

		int years = today.get(Calendar.YEAR) - dateOfRegistration(Calendar.YEAR)

		return years

	 }
}
