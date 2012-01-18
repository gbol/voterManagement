package bz.voter.management

import java.util.Calendar

class Person implements Serializable{

	String firstName
	String middleName
	String lastName
	Date birthDate
    String emailAddress
	Sex sex
	Ethnicity ethnicity
	boolean alive


	static transients = ['age','numberOfYearsRegistered', 'registrationAddress']

    static constraints = {
	 	firstName(blank: false)
		lastName(blank: false)
		middleName(nullable:true)
		ethnicity(nullable:true)
        emailAddress(email:true, nullable:true)
    }

    def beforeInsert(){
        this.alive = true
    }


	 def beforeValidate(){
	 	firstName = firstName?.trim()?.capitalize()
		middleName = middleName?.trim()?.capitalize()
		lastName = lastName?.trim()?.capitalize()
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

	String toString(){
		"${lastName} , ${firstName}"
	}


    Address getRegistrationAddress(){
        Address.findByPersonAndAddressType(this, AddressType.findByName('Registration'))
    }

}
