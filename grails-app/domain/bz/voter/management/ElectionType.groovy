package bz.voter.management

class ElectionType {
	
	String name
	String code

	String toString(){
		name
	}

    static constraints = {
	 	name(nullable:false,unique:true)
	 	code(nullable:false,unique:true)
    }

	 def beforeValidate(){
	 	name = name?.trim()?.capitalize()
		code = code?.trim()?.toUpperCase()
	}
}
