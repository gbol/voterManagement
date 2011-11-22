package bz.voter.management

class Address implements Serializable{

	String houseNumber
	String street
	Municipality municipality

	String toString(){
		"${houseNumber} ${street} , ${municipality.name}"
	}

    static constraints = {
	 	street(blank: false)
    }


	 def beforeValidate(){
	 	street = street?.trim()?.capitalize()
		houseNumber = houseNumber?.trim()
	 }
}
