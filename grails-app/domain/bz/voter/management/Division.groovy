package bz.voter.management

class Division {

	String name

	String toString() { 
		name
	}

    static constraints = {
	 	name(nullable:false, unique:true)
    }

	 def beforeValidate(){
	 	name = name?.trim()
	}
}
