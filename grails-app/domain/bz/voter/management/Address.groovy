package bz.voter.management

class Address {

	String houseNumber
	String street
	Municipality municipality

    static constraints = {
    }


	 def beforeValidate(){
	 	street = street?.trim()?.capitalize()
		houseNumber = houseNumber?.trim()
	 }
}
