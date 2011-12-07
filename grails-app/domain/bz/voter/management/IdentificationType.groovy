package bz.voter.management

class IdentificationType implements Serializable{

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
