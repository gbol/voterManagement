package bz.voter.management

class Affiliation implements Serializable{
	String name

	String toString(){
		name
	}

    static constraints = {
	 	name(unique:true,blank:false)
    }

	 def beforeValidate(){
	 	name = name?.trim()?.toUpperCase()
	}

}
