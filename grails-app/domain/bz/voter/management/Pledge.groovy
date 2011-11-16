package bz.voter.management

class Pledge {

	String name

	String toString(){
		name
	}

    static constraints = {
	 	name(nullable:false,unique:true,blank:false)
    }

	 def beforeValidate(){
	 	name = name?.trim()?.toUpperCase()
	}

}
