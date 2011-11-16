package bz.voter.management

class IdentificationType {

	String name

	String toString(){
		name
	}

    static constraints = {
	 	name(nullable:false,  unique:true)
    }

	 def beforeValidate(){
	 	name = name?.trim()?.capitalize()
	}
}
