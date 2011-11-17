package bz.voter.management

import java.util.Calendar

class Person {

	String firstName
	String middleName
	String lastName
	Date birthDate
	String homePhone
	String cellPhone
	String workPhone
	String comments
	Sex sex
	Address address

	static transients = ['age','numberOfYearsRegistered']

    static constraints = {
	 	firstName(blank: false)
		lastName(blank: false)
		middleName(nullable:true)
		homePhone(nullable:true)
		cellPhone(nullable:true)
		workPhone(nullable:true)
		comments(nullable:true)
    }


	 def beforeValidate(){
	 	firstName = firstName?.trim()?.capitalize()
		middleName = middleName?.trim()?.capitalize()
		lastName = lastName?.trim()?.capitalize()
		homePhone = homePhone?.trim()
		cellPhone = cellPhone?.trim()
		workPhone = workPhone?.trim()
		comments = comments?.trim()?.capitalize()
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

}
