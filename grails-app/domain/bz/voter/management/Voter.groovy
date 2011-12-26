package bz.voter.management

import java.util.Locale
import java.text.DateFormat

class Voter implements Serializable{
	

	static transients = ["firstName", "lastName", "birthDate",
		"age", "sex"]

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


	 String getFirstName(){
	 	person.firstName
	 }

	 String getLastName(){
	 	person.lastName
	 }

	 def getBirthDate(){
	 	DateFormat.getDateInstance(DateFormat.MEDIUM,Locale.UK).format(person.birthDate)
	 }

	 int getAge(){
	 	person.age
	 }

	 Sex getSex(){
	 	person.sex
	 }

	 static totalVotersByPollStation(PollStation pollStation){
	 	Voter.countByPollStation(pollStation)
	 }

}
