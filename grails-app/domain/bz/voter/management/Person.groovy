package bz.voter.management

import java.util.Calendar

class Person {

	String firstName
	String middleName
	String lastName
	Date birthDate
	Date registrationDate
	String registrationNumber
	String homePhone
	String cellPhone
	String workPhone
	String comments
	Sex sex
	Address address
	IdentificationType identificationType
	PollStation pollStation
	Pledge pledge


	static transients = ['age','numberOfYearsRegistered']

    static constraints = {
	 	registrationNumber(nullable:false, unique:true)
    }


	 def beforeValidate(){
	 	firstName = firstName?.trim()?.capitalize()
		middleName = middleName?.trim()?.capitalize()
		lastName = lastName?.trim()?.capitalize()
		registrationNumber = registrationNumber?.trim()
		homePhone = homePhone?.trim()
		cellPhone = cellPhone?.trim()
		workPhone = workPhone?.trim()
	 }


	 def getAge(){
	   Calendar today = Calendar.getInstance()
    	Calendar dob = Calendar.getInstance()
    
   	dob.setTime(this.birthDate)
        
    	int age = today.get(Calendar.YEAR)- dob.get(Calendar.YEAR)
    
      dob.add(Calendar.YEAR,age)
    
    	if(today.before(dob)) {
        age = age-1
    	}
    	return age
	 }

	 def getNumberOfYearsRegistered(){

	 	Calendar today = Calendar.getInstance()
		Calendar dateOfRegistration = Calendar.getInstance()
		dateOfRegistration.setTime(this.registrationDate)

		int years = today.get(Calendar.YEAR) - dateOfRegistration(Calendar.YEAR)

		return years

	 }
}
